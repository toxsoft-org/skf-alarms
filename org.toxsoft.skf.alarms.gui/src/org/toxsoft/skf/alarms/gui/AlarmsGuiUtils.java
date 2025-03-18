package org.toxsoft.skf.alarms.gui;

import static org.toxsoft.skf.alarms.gui.ISkResources.*;

import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.bricks.threadexec.*;
import org.toxsoft.core.tslib.bricks.time.*;
import org.toxsoft.core.tslib.bricks.time.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.alarms.lib.*;
import org.toxsoft.uskat.core.*;
import org.toxsoft.uskat.core.api.evserv.*;
import org.toxsoft.uskat.core.api.hqserv.*;
import org.toxsoft.uskat.core.gui.glib.query.*;
import org.toxsoft.uskat.core.impl.*;

/**
 * Alarm helper methods.
 *
 * @author mvk
 */
public class AlarmsGuiUtils {

  /**
   * Максимальное время(мсек) выполнения запроса алармов
   */
  private static final int QUERY_TIMEOUT = 10000;

  /**
   * Returns history of the alarms - alert, acknowledge and muting events.
   * <p>
   * Includes all events of the class events with IDs {@link ISkAlarmConstants#EVID_ALERT ALERT},
   * {@link ISkAlarmConstants#EVID_ACKNOWLEDGE ACKNOWLEDGE}, {@link ISkAlarmConstants#EVINF_ALARM_MUTED MUTED} and
   * {@link ISkAlarmConstants#EVINF_ALARM_UNMUTED UNMUTED}.
   *
   * @param aShell {@link Shell} родительское окно
   * @param aCoreApi {@link ISkCoreApi} API сервера
   * @param aAlarmIds {@link IStringList} alarm IDs
   * @param aInterval {@link IQueryInterval} - the query interval
   * @return {@link IStringMap}&lt;{@link ITimedList}&lt;{@link SkEvent}&gt;&gt; - the alarm events map. <br>
   *         key: alarm ID;<br>
   *         value:the alarm events history.
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  public static IStringMap<ITimedList<SkEvent>> getAlarmHistory( Shell aShell, ISkCoreApi aCoreApi,
      IStringList aAlarmIds, IQueryInterval aInterval ) {
    TsNullArgumentRtException.checkNulls( aCoreApi, aAlarmIds, aInterval );
    GwidList gwids = new GwidList();
    for( String alarmId : aAlarmIds ) {
      gwids.add( Gwid.createEvent( alarmId, Gwid.STR_MULTI_ID ) );
    }
    // Исполнитель запросов в одном потоке
    ITsThreadExecutor threadExecutor = SkThreadExecutorService.getExecutor( aCoreApi );
    // Результат запроса
    IStringMapEdit<ITimedList<SkEvent>> retValue = new StringMap<>();
    // Создание диалога прогресса выполнения запроса
    SkAbstractQueryDialog<ISkQueryRawHistory> dialog =
        new SkAbstractQueryDialog<>( aShell, STR_QUERIENG_ALARM_HISTORY, QUERY_TIMEOUT, threadExecutor ) {

          @Override
          protected ISkQueryRawHistory doCreateQuery( IOptionSetEdit aOptions ) {
            return aCoreApi.hqService().createHistoricQuery( aOptions );
          }

          @Override
          protected void doPrepareQuery( ISkQueryRawHistory aQuery ) {
            // Подготовка запроса
            aQuery.prepare( gwids );
            aQuery.genericChangeEventer().addListener( aSource -> {
              ISkQueryRawHistory q = (ISkQueryRawHistory)aSource;
              switch( q.state() ) {
                case PREPARED:
                  getProgressMonitor().setTaskName( String.format( STR_PREPARE_ALARMS_HISTORY_QUERY ) );
                  break;
                case READY:
                  int cmdQtty = 0;
                  for( Gwid gwid : aQuery.listGwids() ) {
                    ITimedList<SkEvent> cmds = aQuery.get( gwid );
                    retValue.put( gwid.skid().strid(), cmds );
                    cmdQtty += cmds.size();
                  }
                  getProgressMonitor()
                      .setTaskName( String.format( STR_PREPARE_ALARMS_HISTORY_VIEW, Integer.valueOf( cmdQtty ) ) );
                  break;
                case FAILED:
                  break;
                case EXECUTING:
                case CLOSED:
                case UNPREPARED:
                  break;
                default:
                  throw new TsNotAllEnumsUsedRtException();
              }
            } );
          }
        };
    // Запуск выполнения запроса
    dialog.executeQuery( new QueryInterval( EQueryIntervalType.CSCE, aInterval.startTime(), aInterval.endTime() ) );

    if( dialog.query().state() == ESkQueryState.FAILED ) {
      throw new TsIllegalStateRtException( ERR_QUERY_ALARM_HISTORY_FAILED, dialog.query().stateMessage() );
    }
    // Результат
    return retValue;
  }

}
