package org.toxsoft.skf.alarms.lib.impl;

import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.skf.alarms.lib.ISkAlarmConstants.*;
import static org.toxsoft.skf.alarms.lib.l10n.ISkAlarmSharedResources.*;

import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.*;
import org.toxsoft.core.tslib.coll.helpers.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.math.cond.checker.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.skf.alarms.lib.*;
import org.toxsoft.uskat.core.*;
import org.toxsoft.uskat.core.api.cmdserv.*;
import org.toxsoft.uskat.core.api.evserv.*;
import org.toxsoft.uskat.core.impl.*;
import org.toxsoft.uskat.core.utils.msgen.*;

/**
 * The alarm processor is periodically invoked to check alarm conditions and generate alerts.
 * <p>
 * This is a {@link ICooperativeWorkerComponent} either working in server environment or operated by the serverless
 * Sk-backend.
 * <p>
 * <p>
 * OPTIMIZE do we need to have several instances each processing subset of all alarms ?
 *
 * @author hazard157
 */
public class SkAlarmProcessor
    implements ICooperativeWorkerComponent, ISkAlarmServiceListener, ISkCommandExecutor {

  /**
   * The item for each
   *
   * @author hazard157
   */
  static class AlarmItem
      implements ICloseable {

    private final ISkAlarm       alarm;
    private final ITsChecker     alertChecker;
    private final Gwid           alertEventGwid;
    private final ISkMessageInfo messageInfo;

    /**
     * This value is used to generate an alert only on the edge ("the front") of the alarm triggering.
     */

    // FIXME reset state!

    public boolean previousCondCheckState = false;

    public AlarmItem( ISkAlarm aAlarm, ITsChecker aChecker ) {
      alarm = aAlarm;
      alertChecker = aChecker;
      alertEventGwid = Gwid.createEvent( aAlarm.classId(), aAlarm.strid(), EVID_ALERT );
      messageInfo = aAlarm.messageInfo();
    }

    @Override
    public void close() {
      alertChecker.close();
    }

  }

  private final ISkCoreApi coreApi;

  private final ILogger logger;

  private final IStringMapEdit<AlarmItem> alarmItems = new StringMap<>();

  private boolean stopped = true;

  /**
   * Constructor.
   *
   * @param aCoreApi {@link ISkCoreApi} - the USkat to be processed
   */
  public SkAlarmProcessor( ISkCoreApi aCoreApi ) {
    this( aCoreApi, LoggerUtils.defaultLogger() );
  }

  /**
   * Constructor.
   *
   * @param aCoreApi {@link ISkCoreApi} - the USkat to be processed
   * @param aLogger {@link ILogger} - logger
   */
  public SkAlarmProcessor( ISkCoreApi aCoreApi, ILogger aLogger ) {
    TsNullArgumentRtException.checkNull( aCoreApi );
    coreApi = aCoreApi;
    logger = aLogger;
  }

  // ------------------------------------------------------------------------------------
  // implementation
  //

  private void internalStart() {
    GwidList cmdGwidsToRegister = new GwidList();
    ISkAlarmService alarmService = coreApi.getService( ISkAlarmService.SERVICE_ID );
    // create items for alarmItems
    for( ISkAlarm alarm : alarmService.listAlarms() ) {
      ITsChecker checker =
          alarmService.getAlarmCheckersTopicManager().createCombiChecker( alarm.alertCondition(), coreApi );
      AlarmItem alit = new AlarmItem( alarm, checker );
      alarmItems.put( alarm.strid(), alit );
      // alarm need to register command executor for it
      Gwid acknowledgementCmdGwid = Gwid.createCmd( alarm.classId(), alarm.strid(), CMDID_ACKNOWLEDGE );
      cmdGwidsToRegister.add( acknowledgementCmdGwid );
    }
    alarmService.serviceEventer().addListener( this );
    coreApi.cmdService().registerExecutor( this, cmdGwidsToRegister );
  }

  private void internalFinish() {
    ISkAlarmService as = coreApi.getService( ISkAlarmService.SERVICE_ID );
    as.serviceEventer().removeListener( this );
    coreApi.cmdService().unregisterExecutor( this );
    while( !alarmItems.isEmpty() ) {
      AlarmItem item = alarmItems.removeByKey( alarmItems.keys().first() );
      item.close();
    }
  }

  // ------------------------------------------------------------------------------------
  // ISkAlarmServiceListener
  //

  @Override
  public void onAlarmDefinition( ISkAlarmService aSource, ECrudOp aOp, String aAlarmId ) {
    // OPTIMIZE do we need to optimize - process each aOp individually?
    internalFinish();
    internalStart();
  }

  // ------------------------------------------------------------------------------------
  // ICooperativeWorkerComponent
  //

  @Override
  public void start() {
    internalStart();
    stopped = false;
  }

  @Override
  public boolean queryStop() {
    if( !stopped ) {
      internalFinish();
      stopped = true;
    }
    return stopped;
  }

  @Override
  public boolean isStopped() {
    return stopped;
  }

  @Override
  public void destroy() {
    stopped = true;
    internalFinish();
  }

  // ------------------------------------------------------------------------------------
  // ICooperativeMultiTaskable
  //

  @SuppressWarnings( "boxing" )
  @Override
  public void doJob() {
    if( stopped ) {
      return; // not yet started or already stopped
    }
    long time = System.currentTimeMillis();
    SkEventList llEvents = null;
    // process all alarms
    for( String alarmId : alarmItems.keys() ) {
      AlarmItem alit = alarmItems.getByKey( alarmId );
      ISkAlarm alarm = alit.alarm;
      boolean isMute = alarm.isMuted();
      boolean isAlert = alarm.isAlert();
      try {
        boolean condCheckState = alit.alertChecker.checkCondition();
        if( isAlert ) {
          logger.info( FMT_LOG_ALERT_ALARM_STATE, alarmId, isAlert, isMute, condCheckState );
        }
        /**
         * Alert generation condition:<br>
         * - generation is not muted;<br>
         * - alert is not set right now;<br>
         * - alarm check condition is met;<br>
         * - and alarm condition appears first time (that is, ignore continuous alarm)
         */
        if( !isMute && !isAlert && condCheckState && !alit.previousCondCheckState ) {
          // if alert, set RtData and fire event
          alarm.writeRtdataIfOpen( RTDID_IS_ALERT, AV_TRUE );
          IOptionSetEdit params = new OptionSet();
          String msg = alit.messageInfo.makeMessage( coreApi );
          params.setStr( EVPRMID_ALERT_MESSAGE, msg );
          SkEvent event = new SkEvent( time, alit.alertEventGwid, params );
          if( llEvents == null ) {
            llEvents = new SkEventList();
          }
          llEvents.add( event );
          logger.info( FMT_LOG_SET_ALARM_ALERT, alarm );
        }
        alit.previousCondCheckState = condCheckState;
      }
      catch( Throwable e ) {
        // unexpected checker error
        throw new TsInternalErrorRtException( e, FMT_ERR_UNEXPECTED_CHECKER, alarmId, e.getLocalizedMessage() );
      }
    }
    Integer count = Integer.valueOf( alarmItems.keys().size() );
    Long dt = Long.valueOf( System.currentTimeMillis() - time );
    logger.debug( FMT_LOG_DOJOB_IS_COMPETED, count, dt );

    // fire all alert events at once
    // 2024-06-19 mvk +++ if( != null )
    if( llEvents != null ) {
      coreApi.eventService().fireEvents( llEvents );
    }
  }

  // ------------------------------------------------------------------------------------
  // ISkCommandExecutor
  //

  @Override
  public void executeCommand( IDtoCommand aCmd ) {
    if( !aCmd.cmdGwid().classId().equals( CLSID_ALARM ) ) {
      logger.warning( FMT_LOG_WARN_INV_CMD_DEST_CLASS, aCmd.cmdGwid().classId() );
      return;
    }
    if( !aCmd.cmdGwid().propId().equals( CMDID_ACKNOWLEDGE ) ) {
      logger.warning( FMT_LOG_WARN_INV_CMD_ID, aCmd.cmdGwid().propId() );
      return;
    }
    // confirm command execution
    String commandInstanceId = aCmd.instanceId();
    Gwid authorGwid = aCmd.argValues().getValobj( CMDARGID_ACK_AUTHOR_GWID );
    String alarmStrid = aCmd.cmdGwid().strid();
    AlarmItem alit = alarmItems.findByKey( alarmStrid );
    if( alit == null ) {
      String comment = String.format( FMT_LOG_WARN_INV_CMD_STRID, alarmStrid );
      logger.warning( comment );
      changeCommandState( commandInstanceId, ESkCommandState.FAILED, authorGwid, comment );
      return;
    }
    ISkAlarm alarm = alit.alarm;
    if( !alarm.isAlert() ) {
      String comment = String.format( FMT_LOG_WARN_ACK_CMD_NO_ALERT, aCmd.cmdGwid().toString() );
      logger.warning( comment );
      changeCommandState( commandInstanceId, ESkCommandState.FAILED, authorGwid, comment );
      return;
    }
    // reset alert and fire an event
    alarm.writeRtdataIfOpen( RTDID_IS_ALERT, AV_FALSE );
    String comment = aCmd.argValues().getStr( CMDARGID_ACK_COMMENT );
    changeCommandState( commandInstanceId, ESkCommandState.SUCCESS, authorGwid, comment );
    // fire an event
    Gwid eventGwid = Gwid.createEvent( CLSID_ALARM, alarmStrid, EVID_ACKNOWLEDGE );
    IOptionSetEdit params = new OptionSet();
    params.setStr( EVPRMID_ACK_COMMNET, comment );
    params.setValobj( EVPRMID_ACK_AUTHOR, authorGwid );
    SkEvent event = new SkEvent( System.currentTimeMillis(), eventGwid, params );
    coreApi.eventService().fireEvent( event );
    logger.info( FMT_LOG_RESET_ALARM_ALERT, alarm, aCmd.cmdGwid() );
  }

  // ------------------------------------------------------------------------------------
  // private methods
  //
  private void changeCommandState( String aCommandInstanceId, ESkCommandState aState, Gwid aAuthorGwid,
      String aComment ) {
    long time = System.currentTimeMillis();
    SkCommandState state = new SkCommandState( time, aState, aComment, aAuthorGwid );
    DtoCommandStateChangeInfo stateChangeInfo = new DtoCommandStateChangeInfo( aCommandInstanceId, state );
    coreApi.cmdService().changeCommandState( stateChangeInfo );
  }

}
