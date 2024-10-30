package org.toxsoft.skf.alarms.lib.impl;

import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.skf.alarms.lib.ISkAlarmConstants.*;
import static org.toxsoft.skf.alarms.lib.l10n.ISkAlarmSharedResources.*;

import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.helpers.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.math.cond.checker.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.skf.alarms.lib.*;
import org.toxsoft.uskat.core.*;
import org.toxsoft.uskat.core.api.cmdserv.*;
import org.toxsoft.uskat.core.api.evserv.*;
import org.toxsoft.uskat.core.api.rtdserv.*;
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

    private final ISkReadCurrDataChannel  chReadAlert;
    private final ISkWriteCurrDataChannel chWriteAlert;
    private final ISkReadCurrDataChannel  chReadMute;
    private final ITsChecker              alertChecker;
    private final Gwid                    alertEventGwid;
    private final ISkMessageInfo          messageInfo;

    public AlarmItem( ISkReadCurrDataChannel aChRead, ISkWriteCurrDataChannel aChWrite,
        ISkReadCurrDataChannel aChReadMute, ITsChecker aChecker, ISkAlarm aAlarm ) {
      chReadAlert = aChRead;
      chWriteAlert = aChWrite;
      chReadMute = aChReadMute;
      alertChecker = aChecker;
      alertEventGwid = Gwid.createEvent( aAlarm.classId(), aAlarm.strid(), EVID_ALERT );
      messageInfo = aAlarm.messageInfo();
    }

    @Override
    public void close() {
      alertChecker.close();
      chReadAlert.close();
      chWriteAlert.close();
      chReadMute.close();
    }

  }

  private final ISkCoreApi coreApi;

  private final IStringMapEdit<AlarmItem> alarmItems = new StringMap<>();

  private boolean stopped = true;

  /**
   * Constructor.
   *
   * @param aCoreApi {@link ISkCoreApi} - the USkat to be processed
   */
  public SkAlarmProcessor( ISkCoreApi aCoreApi ) {
    TsNullArgumentRtException.checkNull( aCoreApi );
    coreApi = aCoreApi;
  }

  // ------------------------------------------------------------------------------------
  // implementation
  //

  private void internalStart() {
    GwidList cmdGwidsToRegister = new GwidList();
    ISkAlarmService alarmService = coreApi.getService( ISkAlarmService.SERVICE_ID );
    // create RtData channels for all alarm objects
    GwidList llAlertGwids = new GwidList();
    GwidList llMuteGwids = new GwidList();
    for( ISkAlarm alarm : alarmService.listAlarms() ) {
      Gwid alertGwid = Gwid.createRtdata( CLSID_ALARM, alarm.strid(), RTDID_IS_ALERT );
      llAlertGwids.add( alertGwid );
      Gwid muteGwid = Gwid.createRtdata( CLSID_ALARM, alarm.strid(), RTDID_IS_MUTED );
      llMuteGwids.add( muteGwid );
    }
    IMap<Gwid, ISkReadCurrDataChannel> mmChRead = coreApi.rtdService().createReadCurrDataChannels( llAlertGwids );
    IMap<Gwid, ISkWriteCurrDataChannel> mmChWrite = coreApi.rtdService().createWriteCurrDataChannels( llAlertGwids );
    IMap<Gwid, ISkReadCurrDataChannel> mmChMutes = coreApi.rtdService().createReadCurrDataChannels( llMuteGwids );
    // create items for alarmItems
    for( ISkAlarm alarm : alarmService.listAlarms() ) {
      Gwid alertGwid = Gwid.createRtdata( CLSID_ALARM, alarm.strid(), RTDID_IS_ALERT );
      Gwid muteGwid = Gwid.createRtdata( CLSID_ALARM, alarm.strid(), RTDID_IS_MUTED );
      ITsChecker checker =
          alarmService.getAlarmCheckersTopicManager().createCombiChecker( alarm.alertCondition(), coreApi );
      AlarmItem alit = new AlarmItem( mmChRead.getByKey( alertGwid ), mmChWrite.getByKey( alertGwid ),
          mmChMutes.getByKey( muteGwid ), checker, alarm );
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

  @Override
  public void doJob() {
    if( stopped ) {
      return; // not yet started or already stopped
    }
    long time = System.currentTimeMillis();
    IListEdit<SkEvent> llEvents = null;
    // process all alarms
    for( AlarmItem alit : alarmItems ) {
      boolean isMute = alit.chReadMute.getValue().asBool();
      if( isMute || alit.chReadAlert.getValue().asBool() ) {
        continue; // alert muted or is already set, nothing to do with this alarm
      }
      // if alert, set RtData and fire event
      if( alit.alertChecker.checkCondition() ) {
        alit.chWriteAlert.setValue( AV_TRUE );
        IOptionSetEdit params = new OptionSet();
        String msg = alit.messageInfo.makeMessage( coreApi );
        params.setStr( EVPRMID_ALERT_MESSAGE, msg );
        SkEvent event = new SkEvent( time, alit.alertEventGwid, params );
        if( llEvents == null ) {
          llEvents = new ElemArrayList<>();
        }
        llEvents.add( event );
      }
    }
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
      LoggerUtils.errorLogger().warning( FMT_LOG_WARN_INV_CMD_DEST_CLASS, aCmd.cmdGwid().classId() );
      return;
    }
    if( !aCmd.cmdGwid().propId().equals( CMDID_ACKNOWLEDGE ) ) {
      LoggerUtils.errorLogger().warning( FMT_LOG_WARN_INV_CMD_ID, aCmd.cmdGwid().propId() );
      return;
    }
    // confirm command execution
    String commandInstanceId = aCmd.instanceId();
    Gwid authorGwid = aCmd.argValues().getValobj( CMDARGID_ACK_AUTHOR_GWID );
    String alarmStrid = aCmd.cmdGwid().strid();
    AlarmItem alit = alarmItems.findByKey( alarmStrid );
    if( alit == null ) {
      String comment = String.format( FMT_LOG_WARN_INV_CMD_STRID, alarmStrid );
      LoggerUtils.errorLogger().warning( comment );
      changeCommandState( commandInstanceId, ESkCommandState.FAILED, authorGwid, comment );
      return;
    }
    if( !alit.chReadAlert.getValue().asBool() ) {
      String comment = String.format( FMT_LOG_WARN_ACK_CMD_NO_ALERT, aCmd.cmdGwid().toString() );
      LoggerUtils.errorLogger().warning( comment );
      changeCommandState( commandInstanceId, ESkCommandState.FAILED, authorGwid, comment );
      return;
    }
    // reset alert and fire an event
    alit.chWriteAlert.setValue( AV_FALSE );
    String comment = aCmd.argValues().getStr( CMDARGID_ACK_COMMENT );
    changeCommandState( commandInstanceId, ESkCommandState.SUCCESS, authorGwid, comment );
    // fire an event
    Gwid eventGwid = Gwid.createEvent( CLSID_ALARM, alarmStrid, EVID_ACKNOWLEDGE );
    IOptionSetEdit params = new OptionSet();
    params.setStr( EVPRMID_ACK_COMMNET, comment );
    params.setValobj( EVPRMID_ACK_AUTHOR, authorGwid );
    SkEvent event = new SkEvent( System.currentTimeMillis(), eventGwid, params );
    coreApi.eventService().fireEvent( event );
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
