package org.toxsoft.skf.alarms.lib2.impl;

import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.skf.alarms.lib2.ISkAlarmConstants.*;

import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.core.tslib.math.cond.*;
import org.toxsoft.core.tslib.math.cond.impl.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.alarms.lib2.*;
import org.toxsoft.uskat.core.api.cmdserv.*;
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
   * @param aSkid {@link Skid} - идентификатор объекта
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
  public boolean isExternalAckowledgement() {
    return attrs().getValue( ATRID_IS_EXTERNAL_ACK ).asBool();
  }

  @Override
  public boolean isAlert() {

    // TODO SkAlarm.isAlert()

    return false;
  }

  @Override
  public void setAlert() {

    // TODO SkAlarm.setAlert()

  }

  @Override
  public ISkCommand sendAcknowledge( Skid aAuthor, String aReason ) {
    TsNullArgumentRtException.checkNulls( aReason, aReason );
    TsItemNotFoundRtException.checkNull( coreApi().objService().find( aAuthor ) );
    Gwid gwid = Gwid.createCmd( classId(), strid(), CMDID_ACKNOWLEDGE );
    return coreApi().cmdService().sendCommand( gwid, aAuthor, OptionSetUtils.createOpSet( //
        CMDARGID_ACK_AUTHOR_GWID, avValobj( Gwid.createObj( aAuthor ) ), //
        CMDARGID_ACK_REASON, avStr( aReason ) //
    ) );
  }

  @Override
  public boolean isMuted() {

    // TODO SkAlarm.isMuted()

    return false;
  }

  @Override
  public void muteAlert( Skid aAuthor, String aReason ) {

    // TODO SkAlarm.muteAlert()

  }

  @Override
  public void unmuteAlert() {

    // TODO SkAlarm.unmuteAlert()

  }

}
