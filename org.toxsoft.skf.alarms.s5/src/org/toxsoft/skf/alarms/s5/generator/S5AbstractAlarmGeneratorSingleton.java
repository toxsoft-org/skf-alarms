package org.toxsoft.skf.alarms.s5.generator;

import static org.toxsoft.skf.alarms.lib.EAlarmPriority.*;
import static org.toxsoft.skf.alarms.s5.generator.IS5Resources.*;

import java.util.function.Predicate;

import javax.ejb.EJB;

import org.toxsoft.core.tslib.av.IAtomicValue;
import org.toxsoft.core.tslib.av.opset.IOptionSet;
import org.toxsoft.core.tslib.bricks.strid.coll.IStridablesList;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.StridablesList;
import org.toxsoft.core.tslib.coll.IMap;
import org.toxsoft.core.tslib.gw.gwid.Gwid;
import org.toxsoft.core.tslib.gw.gwid.GwidList;
import org.toxsoft.core.tslib.gw.skid.Skid;
import org.toxsoft.core.tslib.utils.errors.TsInternalErrorRtException;
import org.toxsoft.core.tslib.utils.errors.TsNullArgumentRtException;
import org.toxsoft.skf.alarms.lib.EAlarmPriority;
import org.toxsoft.skf.alarms.lib.ISkAlarmService;
import org.toxsoft.skf.alarms.s5.supports.S5AlarmDefEntity;
import org.toxsoft.uskat.core.ISkCoreApi;
import org.toxsoft.uskat.core.api.rtdserv.ISkReadCurrDataChannel;
import org.toxsoft.uskat.core.connection.ISkConnection;
import org.toxsoft.uskat.core.impl.SkThreadExecutorService;
import org.toxsoft.uskat.s5.client.local.IS5LocalConnectionSingleton;
import org.toxsoft.uskat.s5.legacy.ISkSystem;
import org.toxsoft.uskat.s5.server.backend.IS5BackendCoreSingleton;
import org.toxsoft.uskat.s5.server.singletons.S5SingletonBase;

import core.tslib.bricks.threadexecutor.ITsThreadExecutor;

/**
 * Абстрактная реализация синглтона генератора алармов службы {@link ISkAlarmService}.
 *
 * @author mvk
 */
// Определения которые должны быть в конечном классе наследника
// @Startup
// @Singleton
// @DependsOn( PROJECT_INITIAL_SYSDESCR_SINGLETON )
// @TransactionManagement( TransactionManagementType.CONTAINER )
// @TransactionAttribute( TransactionAttributeType.SUPPORTS )
// @ConcurrencyManagement( ConcurrencyManagementType.CONTAINER )
// @Lock( LockType.READ )
public abstract class S5AbstractAlarmGeneratorSingleton
    extends S5SingletonBase {

  private static final long serialVersionUID = 157157L;

  @EJB
  private IS5BackendCoreSingleton coreSupport;

  @EJB
  private IS5LocalConnectionSingleton localConnectionSupport;

  /**
   * Локальное соединение с сервером для генерации алармов
   * <p>
   * Требуется отдельное соединение, чтобы в {@link #doInit()} проводить вызовы ITsThread.syncExec(...)
   */
  private ISkConnection connection;

  /**
   * Генератор алармов
   */
  private IS5AlarmGenerator alarmGenerator;

  /**
   * Список gwid-идентификаторов текущих данных которые необходимы алармам используемый при запуске синглетона. null:
   * синглетон уже загружен
   */
  private GwidList startCurrDataGwids;

  /**
   * Конструктор.
   *
   * @param aId String идентификатор синглетона
   * @param aName String имя синглетона
   * @throws TsNullArgumentRtException любой аргумент = null
   */
  public S5AbstractAlarmGeneratorSingleton( String aId, String aName ) {
    super( aId, aName );
  }

  // ------------------------------------------------------------------------------------
  // S5ServiceSingletonBase
  //
  @Override
  protected void doInit() {
    // Новое (!) локальное соединение
    connection = localConnectionSupport.open( id() );
    // API соединения ядра
    // ISkCoreApi coreApi = coreSupport.getConnection().coreApi();
    ISkCoreApi coreApi = connection.coreApi();
    // Служба алармов
    ISkAlarmService alarmService = (ISkAlarmService)coreApi.services().findByKey( ISkAlarmService.SERVICE_ID );
    // Поставщик текущих данных для алармов
    S5AlarmCurrDataProvider provider = new S5AlarmCurrDataProvider( coreApi.rtdService() );
    // Список поставщиков
    IStridablesList<IS5AlarmDataProvider> providers = new StridablesList<>( provider );
    // Создание генераторов алармов. aProvidersAutoClose = true
    alarmGenerator = S5AlarmGeneratorUtils.createGenerator( ISkSystem.SKID_THIS_SYSTEM, alarmService, providers, true );
    // Список идентификаторов текущих данных требуемых для тревог
    startCurrDataGwids = new GwidList();
    try {
      try {
        // Добавление определений алармов
        doAddAlarmDefs( coreApi );
      }
      catch( Throwable e ) {
        logger().error( e );
      }
      // Запись в журнал
      logger().info( MSG_REGISTRY_ALARMS, Integer.valueOf( startCurrDataGwids.size() ) );
      try {
        // 2024-04-07 mvk fix gateway local connection error
        ITsThreadExecutor threadExecutor = SkThreadExecutorService.getExecutor( coreApi );
        // Синхронное выполнение с вызовом doJob исполнителя
        threadExecutor.syncExec( () -> {
          // Создаем необходимые каналы чтения текущих данных
          IMap<Gwid, ISkReadCurrDataChannel> channels =
              coreApi.rtdService().createReadCurrDataChannels( startCurrDataGwids );
          // Все каналы которые необходимы уже используются профилями алармов. Закрываем дублирующие каналы
          for( Gwid gwid : channels.keys() ) {
            channels.getByKey( gwid ).close();
          }
        } );
      }
      catch( Throwable e ) {
        // Вероятно какие-то данные не существуют
        logger().error( e, "Unexpected error %s", e.getLocalizedMessage() );
        throw new TsInternalErrorRtException( e );
      }
      // Проход по всем алармам заставляя проверить конфигурацию и инициализировать свое состояние
      for( IS5AlarmProfile profile : alarmGenerator.getAlarmProfiles() ) {
        profile.test();
      }
    }
    finally {
      startCurrDataGwids = null;
    }
  }

  @Override
  protected void doClose() {
    alarmGenerator.close();
    connection.close();
  }

  // ------------------------------------------------------------------------------------
  // Методы для переопределения
  //
  /**
   * Добавить определения алармов
   *
   * @param aCoreApi {@link ISkCoreApi} API соединения ядра
   */
  protected abstract void doAddAlarmDefs( ISkCoreApi aCoreApi );

// @formatter:off
//    // Служба системного описания
//    ISkSysdescr sysdescr = aCoreApi.sysdescr();
//    // Служба объектов
//    ISkObjectService objectService = aCoreApi.objService();
//    // Добавление алармов для генерации
//    String classId = null;
//    ISkidList objIds = null;
//    // =============================================================================================================
//    // Класс Аналоговый вход
////      classId = "vj.AnalogInput";
////      if( sysdescr.findClassInfo( classId ) == null ) {
////          logger().error( "Не найден класс %s", classId );
////          return;
////      }
////      // aIncludeSubclasses = true
////      objIds = objectService.listSkids( classId, true );
////      for( int index = 0, n = objIds.size(); index < n; index++ ) {
////        Skid objId = objIds.get( index );
////        String dataId = "stateWord";
////        addAlarm( "alarmMax",           "Внимание авария max",                                  objId, dataId, value -> hasBit( value, 14 ) );
////        addAlarm( "alarmMin",           "Внимание авария min",                                  objId, dataId, value -> hasBit( value, 8 )  );
////        addAlarm( "noPowerSupply",      "Внимание нет напряжения питания",                      objId, dataId, value -> hasBit( value, 3 )  );
////        addAlarm( "calibrationError",   "Внимание ошибка калибровки (отклонение свыше 5%)",     objId, dataId, value -> hasBit( value, 1 )  );
////        addAlarm( "warningMax",         "Внимание предупреждение max",                          objId, dataId, value -> hasBit( value, 12 ) );
////        addAlarm( "warningMin",         "Внимание предупреждение min",                          objId, dataId, value -> hasBit( value, 10 ) );
////        //addAlarm( "calibrationWarning", "Внимание предупреждение калибровки (отклонение < 5%)", objId, dataId, value -> hasBit( value, 2)   );
////      }
////      // aIncludeSubclasses = true
////      objIds = objectService.listSkids( "vj.IrreversibleEngine", true );
////      for( int index = 0, n = objIds.size(); index < n; index++ ) {
////        // Нереверсивный двигатель
////        Skid objId = objIds.get( index );
////        String dataId = "errorWord";
////        addAlarm( "trnOnFailure",       "Внимание маслонасос не включился",                     objId,  dataId, value -> hasBit( value, 0 ) );
////        addAlarm( "switchOffFailure",   "Внимание маслонасос не отключился",                    objId,  dataId, value -> hasBit( value, 1 ) );
////        addAlarm( "useless",            "Внимание маслонасос нет готовности",                   objId,  dataId, value -> hasBit( value, 5 ) );
////        addAlarm( "noPowerSupply",      "Внимание маслонасос нет напряжения питания",           objId,  dataId, value -> hasBit( value, 7 ) );
////        addAlarm( "noPowerCtrl",        "Внимание маслонасос нет напряжения управления",        objId,  dataId, value -> hasBit( value, 8 ) );
////      }
////      // aIncludeSubclasses = true
////      objIds = objectService.listSkids( "vj.ReversibleEngine", true );
////      for( int index = 0, n = objIds.size(); index < n; index++ ) {
////        // Реверсивный двигатель
////        Skid objId = objIds.get( index );
////        String dataId = "errorWord";
////        addAlarm( "openOnFailure",      "Внимание не включился на открытие",                    objId,  dataId, value -> hasBit( value, 0 )  );
////        addAlarm( "openOffFailure",     "Внимание не отключился на открытие",                   objId,  dataId, value -> hasBit( value, 1 )  );
////        addAlarm( "closeOnFailure",     "Внимание не включился на закрытие",                    objId,  dataId, value -> hasBit( value, 2 )  );
////        addAlarm( "closeOffFailure",    "Внимание не отключился на закрытие",                   objId,  dataId, value -> hasBit( value, 3 )  );
////        addAlarm( "useless",            "Внимание не готов",                                    objId,  dataId, value -> hasBit( value, 5 )  );
////        addAlarm( "driveFailure",       "Внимание авария привода",                              objId,  dataId, value -> hasBit( value, 6 )  );
////        addAlarm( "noPowerSupply",      "Внимание отсутствует напряжение питания",              objId,  dataId, value -> hasBit( value, 7 )  );
////        addAlarm( "noPowerCtrl",        "Внимание отсутствует напряжение управления",           objId,  dataId, value -> hasBit( value, 8 )  );
////        addAlarm( "openFailure",        "Внимание не открылась",                                objId,  dataId, value -> hasBit( value, 10 ) );
////        addAlarm( "closeFailure",       "Внимание не закрылась",                                objId,  dataId, value -> hasBit( value, 11 ) );
////        addAlarm( "openOverload",       "Внимание перегруз на открытие",                        objId,  dataId, value -> hasBit( value, 12 ) );
////        addAlarm( "closeOverload",      "Внимание перегруз на закрытие",                        objId,  dataId, value -> hasBit( value, 13 ) );
////      }
//
//    // =============================================================================================================
//    classId = "vj.StartBlockings";
//    if( sysdescr.findClassInfo( classId ) == null ) {
//      logger().error( "Не найден класс %s", classId );
//      return;
//    }
//    // aIncludeSubclasses = true
//    objIds = objectService.listSkids( classId, true );
//    for( int index = 0, n = objIds.size(); index < n; index++ ) {
//      Skid objId = objIds.get( index );
//      addAlarm( "pwrBoxPowered",      LOW,    "Нет питания ШС",                         objId,  "pwrBoxPowered",     value -> equals( value, AV_FALSE ) );
//      addAlarm( "emergencyShutdown",  NORMAL, "Ручной аварийный стоп",                  objId,  "emergencyShutdown", value -> equals( value, AV_TRUE  ) );
//      addAlarm( "msBlockCtrl",        NORMAL, "Ручная блокировка включения",            objId,  "msBlockCtrl",       value -> equals( value, AV_TRUE  ) );
//      addAlarm( "onlineUPS1",         LOW,    "Работа от батарей ИБП1",                 objId,  "onlineUPS1",        value -> equals( value, AV_FALSE ) );
//      addAlarm( "lowBatteryUPS1",     LOW,    "Низкий заряд батарей ИБП1",              objId,  "lowBatteryUPS1",    value -> equals( value, AV_FALSE ) );
//      addAlarm( "onlineUPS2",         LOW,    "Работа от батарей ИБП2",                 objId,  "onlineUPS2",        value -> equals( value, AV_FALSE ) );
//      addAlarm( "lowBatteryUPS2",     LOW,    "Низкий заряд батарей ИБП2",              objId,  "lowBatteryUPS2",    value -> equals( value, AV_FALSE ) );
//      addAlarm( "noPowerL61",         LOW,    "Нет напряжения питания L61",             objId,  "noPowerL61",        value -> equals( value, AV_FALSE ) );
//      addAlarm( "noPowerL62",         LOW,    "Нет напряжения питания L62",             objId,  "noPowerL62",        value -> equals( value, AV_FALSE ) );
//      //addAlarm( "noPowerL63",         LOW,    "Нет напряжения питания L63",             objId,  "noPowerL63",        value -> equals( value, AV_FALSE ) );
//      addAlarm( "noPowerThrExiter",   NORMAL, "Нет питания ТВУ",                        objId,  "noPowerThrExiter",  value -> equals( value, AV_FALSE ) );
//      addAlarm( "failureThrExiter",   NORMAL, "Авария ТВУ",                             objId,  "failureThrExiter",  value -> equals( value, AV_TRUE  ) );
//
//    }
//    // aIncludeSubclasses = true
//    objIds = objectService.listSkids( "vj.AntiSurgeProtection", true );
//    for( int index = 0, n = objIds.size(); index < n; index++ ) {
//      Skid objId = objIds.get( index );
//      addAlarm( "preSurge1",          NORMAL, "Предпомпаж1",                            objId,  "preSurge1",         value -> equals( value, AV_TRUE  ) );
//      addAlarm( "preSurge2",          NORMAL, "Предпомпаж2",                            objId,  "preSurge2",         value -> equals( value, AV_TRUE  ) );
//      addAlarm( "surge",              NORMAL, "Помпаж",                                 objId,  "surge",             value -> equals( value, AV_TRUE  ) );
//    }
//    // aIncludeSubclasses = true
//    objIds = objectService.listSkids( "vj.OilContour", true );
//    for( int index = 0, n = objIds.size(); index < n; index++ ) {
//      Skid objId = objIds.get( index );
//      addAlarm( "lowOilLevel",        NORMAL, "Низкий уровень масла в маслобаке",       objId,  "lowOilLevel",       value -> equals( value, AV_FALSE ) );
//      addAlarm( "hightOilLevel",      NORMAL, "Высокий уровень масла",                  objId,  "hightOilLevel",     value -> equals( value, AV_FALSE ) );
//      addAlarm( "oilFilterDirty",     NORMAL, "Масляный фильтр грязный",                objId,  "oilFilterDirty",    value -> equals( value, AV_FALSE ) );
//    }
//    // aIncludeSubclasses = true
//    objIds = objectService.listSkids( "vj.ThyristorExciter", true );
//    for( int index = 0, n = objIds.size(); index < n; index++ ) {
//      Skid objId = objIds.get( index );
//      addAlarm( "warning",            LOW,    "Предупреждение ТВУ",                     objId,  "warning",           value -> equals( value, AV_TRUE ) );
//    }
//    // aIncludeSubclasses = true
//    objIds = objectService.listSkids( "vj.SimpleValve", true );
//    for( int index = 0, n = objIds.size(); index < n; index++ ) {
//      Skid objId = objIds.get( index );
//      addAlarm( "error1",             LOW,    "Авария задвижки нагнетания",             objId,  "error",             value -> equals( value, AV_TRUE ) );
//    }
//    // aIncludeSubclasses = true
//    objIds = objectService.listSkids( "vj.AntiSurgeValve", true );
//    for( int index = 0, n = objIds.size(); index < n; index++ ) {
//      Skid objId = objIds.get( index );
//      addAlarm( "error2",             LOW,    "Авария помпажного клапана",              objId,  "error",             value -> equals( value, AV_TRUE ) );
//      addAlarm( "alarmVFD",           LOW,    "Авария ЧРП ДПК",                         objId,  "alarmVFD",          value -> equals( value, AV_TRUE ) );
//    }
//    // aIncludeSubclasses = true
//    objIds = objectService.listSkids( "vj.ReversibleEngineMidPoint", true );
//    for( int index = 0, n = objIds.size(); index < n; index++ ) {
//      Skid objId = objIds.get( index );
//      addAlarm( "error3",             LOW,    "Авария дроссельной заслонки",            objId,  "error",             value -> equals( value, AV_TRUE ) );
//      addAlarm( "alarmVFD",           LOW,    "Авария ЧРП ДДЗ",                         objId,  "alarmVFD",          value -> equals( value, AV_TRUE ) );
//    }
//    // aIncludeSubclasses = true
//    objIds = objectService.listSkids( "vj.OilPump", true );
//    for( int index = 0, n = objIds.size(); index < n; index++ ) {
//      Skid objId = objIds.get( index );
//      addAlarm( "driveAlarm",         LOW,    "Авария пускового маслонасоса",           objId,  "driveAlarm",        value -> equals( value, AV_TRUE ) );
//    }
//    // aIncludeSubclasses = true
//    objIds = objectService.listSkids( "vj.AnalogInput", true );
//    for( int index = 0, n = objIds.size(); index < n; index++ ) {
//      Skid objId = objIds.get( index );
//      addAlarm( "vj.AnalogInput.alarm",     HIGH,   "Измеренное значение CV вышло за пределы AMax или AMin",  objId,  "alarm", value -> notEquals( value, AV_FALSE ) );
//      addAlarm( "vj.AnalogInput.warn",      LOW,    "Измеренное значение CV вышло за пределы WMax или WMin",  objId,  "warn",  value -> notEquals( value, AV_FALSE ) );
//    }
//  }
//@formatter:om

  // ------------------------------------------------------------------------------------
  // Методы для наследников
  //
  /**
   * Добавление аларма в генератор алармов
   *
   * @param aAlarmId String идентификатор аларма
   * @param aMessage String сообщения для аларма
   * @param aObjId {@link Skid} идентификатор объекта для чтения текущего данного. Он же автор аларма
   * @param aDataId String идентификатор данного формирующего аларм
   * @param aValuePredicate {@link IS5AlarmAtomicValuePredicate} условие на значения для формирования аларма
   * @throws TsNullArgumentRtException любой аргумент = null
   */
  protected final void addAlarm( String aAlarmId, String aMessage, Skid aObjId, String aDataId,
      IS5AlarmAtomicValuePredicate aValuePredicate ) {
    addAlarm( aAlarmId, NORMAL, aMessage, aObjId, aDataId, aValuePredicate, IOptionSet.NULL );
  }

  /**
   * Добавление аларма в генератор алармов
   *
   * @param aAlarmId String идентификатор аларма
   * @param aAlarmPriority {@link EAlarmPriority} приоритет аларма
   * @param aMessage String сообщения для аларма
   * @param aObjId {@link Skid} идентификатор объекта для чтения текущего данного. Он же автор аларма
   * @param aDataId String идентификатор данного формирующего аларм
   * @param aValuePredicate {@link IS5AlarmAtomicValuePredicate} условие на значения для формирования аларма
   * @throws TsNullArgumentRtException любой аргумент = null
   */
  protected final void addAlarm( String aAlarmId, EAlarmPriority aAlarmPriority, String aMessage, Skid aObjId, String aDataId,
      IS5AlarmAtomicValuePredicate aValuePredicate ) {
    addAlarm( aAlarmId, aAlarmPriority, aMessage, aObjId, aDataId, aValuePredicate, IOptionSet.NULL );
  }


  /**
   * Добавление аларма в генератор алармов
   *
   * @param aAlarmId String идентификатор аларма
   * @param aAlarmPriority {@link EAlarmPriority} приоритет аларма
   * @param aMessage String сообщения для аларма
   * @param aObjId {@link Skid} идентификатор объекта для чтения текущего данного. Он же автор аларма
   * @param aDataId String идентификатор данного формирующего аларм
   * @param aValuePredicate {@link IS5AlarmAtomicValuePredicate} условие на значения для формирования аларма
   * @param aParams {@link IOptionSet} параметры тревог
   * @throws TsNullArgumentRtException любой аргумент = null
   */
  protected final void addAlarm( String aAlarmId, EAlarmPriority aAlarmPriority, String aMessage, Skid aObjId, String aDataId,
      IS5AlarmAtomicValuePredicate aValuePredicate, IOptionSet aParams ) {
    TsNullArgumentRtException.checkNulls( aAlarmId, aAlarmPriority, aMessage, aObjId, aDataId, aValuePredicate );
    Skid athorObjId = aObjId;
    S5AlarmDefEntity alarmDef = new S5AlarmDefEntity( aAlarmId, aMessage, aParams );
    alarmDef.setPriority( aAlarmPriority );
    // Условие текущего данного: значение текущего данного должно быть присвоено
    S5AlarmCurrDataPredicate currDataPredicate1 =
        new S5AlarmCurrDataPredicate( aObjId, aDataId, IAtomicValue::isAssigned );
    // Условие текущего данного: условие на значение
    S5AlarmCurrDataPredicate currDataPredicate2 = new S5AlarmCurrDataPredicate( aObjId, aDataId, aValuePredicate );
    // AND условий
    Predicate<IS5AlarmProfile> currDataPredicate = currDataPredicate1.and( currDataPredicate2 );
    // Добавление аларма
    IS5AlarmProfile profile = alarmGenerator.addAlarm( athorObjId, alarmDef, currDataPredicate );
    // Размещение идентификатора
    if( startCurrDataGwids != null ) {
      // Проводится загрузка синглетона
      startCurrDataGwids.add( Gwid.createRtdata( aObjId.classId(), aObjId.strid(), aDataId ) );
    }
    else {
      // Синглетон уже был загружен. Каждый новый аларм инициализируется самостоятельно
      profile.test();
    }
  }

  /**
   * Проверяет установлен ли бит в целом числе
   *
   * @param aValue {@link IAtomicValue} атомарное значение целого типа
   * @param aBitNo int номер бита
   * @return <b>true</b> бит установлен; <b>false</b> бит не установлен
   * @throws TsNullArgumentRtException аргумент = null
   */
  protected final static boolean hasBit( IAtomicValue aValue, int aBitNo ) {
    long mask = (long)Math.pow( 2, aBitNo );
    return ((aValue.asLong() & mask) != 0);
  }

  /**
   * Проверяет эквивалетность указанных параметров
   *
   * @param aValue1 {@link IAtomicValue} атомарное значение 1
   * @param aValue2 {@link IAtomicValue} атомарное значение 2
   * @return <b>true</b> значения эквивалентны; <b>false</b> значения не эквивалентны
   * @throws TsNullArgumentRtException аргумент = null
   */
  protected final static boolean equals( IAtomicValue aValue1, IAtomicValue aValue2 ) {
    TsNullArgumentRtException.checkNulls( aValue1, aValue2 );
    return (aValue1.equals( aValue2 ));
  }

  /**
   * Проверяет НЕэквивалетность указанных параметров
   *
   * @param aValue1 {@link IAtomicValue} атомарное значение 1
   * @param aValue2 {@link IAtomicValue} атомарное значение 2
   * @return <b>true</b> бит установлен; <b>false</b> бит не установлен
   * @throws TsNullArgumentRtException аргумент = null
   */
  protected final static boolean notEquals( IAtomicValue aValue1, IAtomicValue aValue2 ) {
    TsNullArgumentRtException.checkNulls( aValue1, aValue2 );
    return !aValue1.equals( aValue2 );
  }

}
