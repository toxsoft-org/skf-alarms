package org.toxsoft.skf.alarms.s5.generator;

import static org.toxsoft.skf.alarms.s5.generator.IS5Resources.*;

import java.util.function.Predicate;

import org.toxsoft.core.tslib.bricks.ICooperativeMultiTaskable;
import org.toxsoft.core.tslib.bricks.strid.coll.IStridablesList;
import org.toxsoft.core.tslib.gw.skid.Skid;
import org.toxsoft.core.tslib.utils.ICloseable;
import org.toxsoft.core.tslib.utils.errors.TsNullArgumentRtException;
import org.toxsoft.core.tslib.utils.logs.impl.LoggerUtils;
import org.toxsoft.skf.alarms.lib.ISkAlarm;
import org.toxsoft.skf.alarms.lib.ISkAlarmDef;
import org.toxsoft.skf.alarms.lib.flacon.ISkAlarmFlacon;
import org.toxsoft.skf.alarms.s5.generator.IS5AlarmDataProvider.IAlarmDataProviderListener;

/**
 * Профайл для формирования {@link ISkAlarm}
 * <p>
 * TODO: реализовать логику, включение/выключение аларма по таймату заданному через конструктор???
 * <p>
 * TODO обработка алармов связанных с интервалами времени или с частотными отклонениями
 *
 * @author mvk
 */
class S5AlarmProfile
    implements IS5AlarmProfile, IAlarmDataProviderListener, ICooperativeMultiTaskable, ICloseable {

  private final S5AlarmGenerator           generator;
  private final ISkAlarmDef                skAlarmDef;
  private final Skid                       authorId;
  private final Predicate<IS5AlarmProfile> predicate;
  private boolean                          alarmed;

  /**
   * Конструктор
   *
   * @param aGenerator {@link IS5AlarmGenerator} генератор алармов
   * @param aSkAlarmDef {@link ISkAlarmDef} описание аларма
   * @param aAuthorId {@link Skid} идентификтор объекта автора аларма
   * @param aPredicate {@link Predicate}&lt;{@link S5AlarmProfile}&gt; условие формирования аларма
   */
  S5AlarmProfile( S5AlarmGenerator aGenerator, ISkAlarmDef aSkAlarmDef, Skid aAuthorId,
      Predicate<IS5AlarmProfile> aPredicate ) {
    TsNullArgumentRtException.checkNulls( aGenerator, aSkAlarmDef, aPredicate );
    generator = aGenerator;
    skAlarmDef = aSkAlarmDef;
    authorId = aAuthorId;
    predicate = aPredicate;
    for( IS5AlarmDataProvider provider : generator.providers() ) {
      provider.addProviderListener( this );
    }
  }

  // ------------------------------------------------------------------------------------
  // Реализация IS5AlarmProfile
  //
  @Override
  public Skid alarmAuthorId() {
    return authorId;
  }

  @Override
  public ISkAlarmDef skAlarmDef() {
    return skAlarmDef;
  }

  @Override
  public IStridablesList<IS5AlarmDataProvider> providers() {
    return generator.providers();
  }

  @Override
  public void test() {
    try {
      boolean prevAlarmed = alarmed;
      boolean currAlarmed = predicate.test( this );
      if( !alarmed && currAlarmed ) {
        String alarmDefId = skAlarmDef.id();
        byte sublevel = 0; // ??? вынести в конструктор S5AlarmProfile???
        ISkAlarmFlacon skAlarmFlacon = ISkAlarmFlacon.NULL; // ???
        generator.generateAlarm( alarmDefId, authorId, sublevel, skAlarmFlacon );
      }
      alarmed = currAlarmed;
      // Журнал
      if( alarmed && !prevAlarmed ) {
        // Включение аларма
        generator.logger().info( MSG_ALARM_ON, skAlarmDef, authorId );
      }
      if( !alarmed && prevAlarmed ) {
        // Выключение аларма
        generator.logger().info( MSG_ALARM_OFF, skAlarmDef, authorId );
      }
    }
    catch( Throwable e ) {
      LoggerUtils.defaultLogger().error( e );
    }
  }

  @Override
  public boolean alarmed() {
    return alarmed;
  }

  // ------------------------------------------------------------------------------------
  // Реализация IAlarmDataProviderListener
  //
  @Override
  public void onUpdate( IS5AlarmDataProvider aProvider ) {
    test();
  }

  // ------------------------------------------------------------------------------------
  // Реализация ICooperativeMultiTaskable
  //
  @Override
  public void doJob() {
    // TODO обработка алармов связанных с интервалами времени или с частотными отклонениями
  }

  // ------------------------------------------------------------------------------------
  // Реализация ICloseable
  //
  @Override
  public void close() {
    for( IS5AlarmDataProvider provider : generator.providers() ) {
      provider.removeProviderListener( this );
    }
  }
}
