package org.toxsoft.skf.alarms.gui.m5;

import static org.toxsoft.core.tslib.av.EAtomicType.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;

import org.toxsoft.core.tsgui.m5.IM5Model;
import org.toxsoft.core.tsgui.m5.model.impl.M5LifecycleManager;
import org.toxsoft.core.tslib.av.impl.AvUtils;
import org.toxsoft.core.tslib.av.impl.DataDef;
import org.toxsoft.core.tslib.av.math.EAvCompareOp;
import org.toxsoft.core.tslib.av.metainfo.IDataDef;
import org.toxsoft.core.tslib.av.opset.impl.OptionSet;
import org.toxsoft.core.tslib.bricks.filter.ITsCombiFilterParams;
import org.toxsoft.core.tslib.bricks.time.ITimeInterval;
import org.toxsoft.core.tslib.bricks.time.ITimedList;
import org.toxsoft.core.tslib.bricks.time.impl.TimeInterval;
import org.toxsoft.core.tslib.bricks.time.impl.TimeUtils;
import org.toxsoft.core.tslib.coll.IList;
import org.toxsoft.core.tslib.coll.IListEdit;
import org.toxsoft.core.tslib.coll.impl.ElemArrayList;
import org.toxsoft.core.tslib.utils.TsLibUtils;
import org.toxsoft.core.tslib.utils.errors.TsNullArgumentRtException;
import org.toxsoft.skf.alarms.lib.*;
import org.toxsoft.skf.alarms.lib.filters.SkAlarmFilterByHistory;
import org.toxsoft.skf.alarms.lib.impl.SkAlarmThreadHistoryItem;

/**
 * Lifecycle manager for {@link SkAlarmM5Model}.
 *
 * @author max
 */
public class SkAlarmM5LifecycleManager
    extends M5LifecycleManager<ISkAlarm, ISkAlarmService> {

  private static final String ALARM_GUI_THREAD_ID = "main.gui.thread";

  private static final IDataDef ALARM_QUIT_ID =
      DataDef.create( "org.toxsoft.skf.alarms.gui.m5.alarm.is.quit", BOOLEAN, TSID_NAME, "Alarm is quit", //
          TSID_DEFAULT_VALUE, AV_FALSE );

  public static final ITsCombiFilterParams EMPTY_FILTER =
      SkAlarmFilterByHistory.makeFilterParams( EAvCompareOp.NE, AvUtils.AV_0, false, EAvCompareOp.GE,
          AvUtils.avTimestamp( TimeUtils.MIN_TIMESTAMP ), EAvCompareOp.NE, AvUtils.avStr( TsLibUtils.EMPTY_STRING ) );

  public static final ITsCombiFilterParams FILTER1 = SkAlarmFilterByHistory.makeFilterParams( //
      // test ISkAlarm.history() size ('!=0'):
      EAvCompareOp.NE, AvUtils.AV_0,

      true,
      // test ISkAlarmThreadHistoryItem.timestamp() ('any'):
      EAvCompareOp.GE, AvUtils.avTimestamp( TimeUtils.MIN_TIMESTAMP ),

      // test ISkAlarmThreadHistoryItem.announceThreadId() ('not empty'):
      EAvCompareOp.NE, AvUtils.avStr( ALARM_GUI_THREAD_ID )// ,

  // test ISkAlarmThreadHistoryItem.params():
  // new ParamValue( ALARM_QUIT_ID.id(), EAvCompareOp.NE, AvUtils.AV_TRUE )
  );//

  private static final ITsCombiFilterParams FILTER2 = SkAlarmFilterByHistory.makeFilterParams( //
      // test ISkAlarm.history() size ('!=0'):
      EAvCompareOp.NE, AvUtils.AV_0,

      true,
      // test ISkAlarmThreadHistoryItem.timestamp() ('any'):
      EAvCompareOp.GE, AvUtils.avTimestamp( TimeUtils.MIN_TIMESTAMP ),

      // test ISkAlarmThreadHistoryItem.announceThreadId() ('not empty'):
      EAvCompareOp.EQ, AvUtils.avStr( ISkAlarmThreadHistoryItem.ALARM_THREAD_NULL ) );//

  private ITsCombiFilterParams filter = EMPTY_FILTER;

  private ITimeInterval interval = new TimeInterval( System.currentTimeMillis(), System.currentTimeMillis() );

  /**
   * Constructor.
   *
   * @param aModel {@link IM5Model}&lt;T&gt; - the model
   * @param aMaster {@link ISkAlarmService}&lt;M&gt; - master object
   * @throws TsNullArgumentRtException model is <code>null</code>
   */
  public SkAlarmM5LifecycleManager( IM5Model<ISkAlarm> aModel, ISkAlarmService aMaster ) {
    super( aModel, false, false, true, true, aMaster );
  }

  public synchronized ITimeInterval getInterval() {
    return interval;
  }

  public synchronized void setInterval( ITimeInterval aInterval ) {
    interval = aInterval;
  }

  public ITsCombiFilterParams getFilter() {
    return filter;
  }

  public void setFilter( ITsCombiFilterParams aFilter ) {
    filter = aFilter;
  }

  public void setQuitValue( ISkAlarm aAlarm, boolean aIsQuit ) {
    OptionSet alarmOptions = new OptionSet();
    ALARM_QUIT_ID.setValue( alarmOptions, AvUtils.avBool( aIsQuit ) );

    ISkAlarmThreadHistoryItem quitItem =
        new SkAlarmThreadHistoryItem( System.currentTimeMillis(), ALARM_GUI_THREAD_ID, alarmOptions );
    master().addThreadHistoryItem( aAlarm.alarmId(), quitItem );
  }

  @Override
  public void doRemove( ISkAlarm aAlarm ) {
    OptionSet alarmOptions = new OptionSet();
    ALARM_QUIT_ID.setValue( alarmOptions, AV_TRUE );

    ISkAlarmThreadHistoryItem quitItem =
        new SkAlarmThreadHistoryItem( System.currentTimeMillis(), ALARM_GUI_THREAD_ID, alarmOptions );
    master().addThreadHistoryItem( aAlarm.alarmId(), quitItem );
  }

  @Override
  protected IList<ISkAlarm> doListEntities() {
    IListEdit<ISkAlarm> result = new ElemArrayList<>();

    ITimedList<ISkAlarm> alarms1 = master().queryAlarms( interval, filter );
    for( ISkAlarm alarm : alarms1 ) {
      // dima 24.01.23 добавляем в начало списка
      result.insert( 0, alarm );
    }

    // ITimedList<ISkAlarm> alarms2 = master().queryAlarms( ITimeInterval.WHOLE, FILTER2 );
    // for( ISkAlarm alarm : alarms2 ) {
    // result.add( alarm );
    // }

    // TODO - это вместо фильтра, нужно реализовать фильтр
    // for( ISkAlarm alarm : alarms ) {
    // if( alarm.history().size() == 0 || !REPORT_TITLE_M5_ID.getValue( alarm.history().last().params() ).asBool() ) {
    // result.add( alarm );
    // }
    // }

    System.out.println( "Alarm count = " + result.size() );

    return result;
  }

}
