package org.toxsoft.skf.alarms.lib.impl;

import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.skf.alarms.lib.ISkAlarmConstants.*;
import static org.toxsoft.skf.alarms.lib.l10n.ISkAlarmSharedResources.*;

import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.time.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.core.tslib.math.cond.*;
import org.toxsoft.core.tslib.math.cond.impl.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.skf.alarms.lib.*;
import org.toxsoft.uskat.core.api.cmdserv.*;
import org.toxsoft.uskat.core.api.evserv.*;
import org.toxsoft.uskat.core.api.objserv.*;
import org.toxsoft.uskat.core.impl.*;
import org.toxsoft.uskat.core.utils.msgen.*;

/**
 * @author hazard157
 */
class SkAlarm
    extends SkObject
    implements ISkAlarm {

  /**
   * The creator singleton.
   */
  static final ISkObjectCreator<SkAlarm> CREATOR = SkAlarm::new;

  /**
   * Constructor.
   *
   * @param aSkid {@link Skid} - the object SKID
   */
  SkAlarm( Skid aSkid ) {
    super( aSkid );
  }

  // ------------------------------------------------------------------------------------
  // ISkAlarm
  //

  @Override
  public ESkAlarmSeverity severity() {
    return attrs().getValue( ATRID_SEVERITY ).asValobj();
  }

  @Override
  public ITsCombiCondInfo alertCondition() {
    String clob = getClob( CLBID_ALERT_CONDITION );
    return TsCombiCondInfo.KEEPER.str2ent( clob );
  }

  @Override
  public ISkMessageInfo messageInfo() {
    String clob = getClob( CLBID_MESSAGE_INFO );
    return SkMessageInfo.KEEPER.str2ent( clob );
  }

  @Override
  public boolean isAlert() {
    IAtomicValue val = readRtdataIfOpen( RTDID_IS_ALERT );
    if( val == null ) {
      LoggerUtils.errorLogger().warning( FMT_LOG_WARN_CLOSED_RTDATA_CHANNEL, RTDID_IS_ALERT );
      return false;
    }
    return val.isAssigned() ? val.asBool() : false;
  }

  @Override
  public void setAlert() {
    if( isAlert() ) {
      return;
    }
    if( writeRtdataIfOpen( RTDID_IS_ALERT, AV_TRUE ) ) {
      SkEvent alertEvent = SkEvent.create( skid(), EVID_ALERT, //
          EVPRMID_ALERT_MESSAGE, messageInfo().makeMessage( coreApi() ) //
      );
      coreApi().eventService().fireEvent( alertEvent );
    }
    else {
      LoggerUtils.errorLogger().warning( FMT_LOG_WARN_CLOSED_RTDATA_CHANNEL, RTDID_IS_ALERT );
    }
  }

  @Override
  public ISkCommand sendAcknowledge( Skid aAuthor, String aComment ) {
    TsNullArgumentRtException.checkNull( aComment );
    TsItemNotFoundRtException.checkNull( coreApi().objService().find( aAuthor ) );
    Gwid gwid = Gwid.createCmd( classId(), strid(), CMDID_ACKNOWLEDGE );
    return coreApi().cmdService().sendCommand( gwid, aAuthor, OptionSetUtils.createOpSet( //
        CMDARGID_ACK_AUTHOR_GWID, avValobj( Gwid.createObj( aAuthor ) ), //
        CMDARGID_ACK_COMMENT, avStr( aComment ) //
    ) );
  }

  @Override
  public boolean isMuted() {
    IAtomicValue val = readRtdataIfOpen( RTDID_IS_MUTED );
    if( val == null ) {
      LoggerUtils.errorLogger().warning( FMT_LOG_WARN_CLOSED_RTDATA_CHANNEL, RTDID_IS_MUTED );
      return false;
    }
    return val.isAssigned() ? val.asBool() : false;
  }

  @Override
  public void muteAlert( Skid aAuthor, String aReason ) {
    TsNullArgumentRtException.checkNull( aReason );
    TsItemNotFoundRtException.checkNull( coreApi().objService().find( aAuthor ) );
    if( isMuted() ) {
      return;
    }
    writeRtdataIfOpen( RTDID_IS_MUTED, AV_TRUE );
    Gwid eventGwid = Gwid.createEvent( classId(), strid(), EVID_ALARM_MUTED );
    IOptionSetEdit params = new OptionSet();
    params.setValobj( EVPRMID_MUTE_AUTHOR, aAuthor );
    params.setStr( EVPRMID_MUTE_REASON, aReason );
    SkEvent event = new SkEvent( System.currentTimeMillis(), eventGwid, params );
    coreApi().eventService().fireEvent( event );
  }

  @Override
  public void unmuteAlert() {
    if( !isMuted() ) {
      return;
    }
    writeRtdataIfOpen( RTDID_IS_MUTED, AV_FALSE );
    Gwid eventGwid = Gwid.createEvent( classId(), strid(), EVID_ALARM_UNMUTED );
    SkEvent event = new SkEvent( System.currentTimeMillis(), eventGwid, IOptionSet.NULL );
    coreApi().eventService().fireEvent( event );
  }

  @Override
  public ITimedList<SkEvent> getHistory( IQueryInterval aInterval ) {
    TsNullArgumentRtException.checkNull( aInterval );
    Gwid gwid = Gwid.createEvent( classId(), strid(), Gwid.STR_MULTI_ID );
    return coreApi().eventService().queryObjEvents( aInterval, gwid );
  }

}
