package org.toxsoft.skf.alarms.lib;

import static org.toxsoft.core.tslib.av.EAtomicType.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.core.tslib.gw.IGwHardConstants.*;
import static org.toxsoft.skf.alarms.lib.l10n.ISkAlarmSharedResources.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;

import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.core.tslib.math.cond.impl.*;
import org.toxsoft.uskat.core.api.sysdescr.dto.*;
import org.toxsoft.uskat.core.impl.dto.*;
import org.toxsoft.uskat.core.utils.msgen.*;

/**
 * Alarm service constants.
 *
 * @author hazard157
 */
@SuppressWarnings( "javadoc" )
public interface ISkAlarmConstants {

  // ------------------------------------------------------------------------------------
  // Alarm class properties IDs

  String CLSID_ALARM = SK_ID + ".Alarm"; //$NON-NLS-1$

  String ATRID_SEVERITY        = "severity";       //$NON-NLS-1$
  String CLBID_ALERT_CONDITION = "alertCondition"; //$NON-NLS-1$
  String CLBID_MESSAGE_INFO    = "messageInfo";    //$NON-NLS-1$
  String RTDID_IS_ALERT        = "alert";          //$NON-NLS-1$
  String RTDID_IS_MUTED        = "muted";          //$NON-NLS-1$

  String CMDID_ACKNOWLEDGE        = "cmdAcknowledge"; //$NON-NLS-1$
  String CMDARGID_ACK_AUTHOR_GWID = "ackAuthorGwid";  //$NON-NLS-1$
  String CMDARGID_ACK_COMMENT     = "ackComment";     //$NON-NLS-1$

  String EVID_ALERT            = "evAlert";      //$NON-NLS-1$
  String EVPRMID_ALERT_MESSAGE = "alertMessage"; //$NON-NLS-1$

  String EVID_ACKNOWLEDGE    = "evAcknowledge"; //$NON-NLS-1$
  String EVPRMID_ACK_AUTHOR  = "aclAuthor";     //$NON-NLS-1$
  String EVPRMID_ACK_COMMNET = "ackComment";    //$NON-NLS-1$

  String EVID_ALARM_MUTED    = "evMuted";    //$NON-NLS-1$
  String EVPRMID_MUTE_AUTHOR = "muteAuthor"; //$NON-NLS-1$
  String EVPRMID_MUTE_REASON = "muteReason"; //$NON-NLS-1$

  String EVID_ALARM_UNMUTED = "evUnmuted"; //$NON-NLS-1$

  // ------------------------------------------------------------------------------------
  // Alarm class properties INFOs

  DtoAttrInfo ATRINF_SEVERITY = DtoAttrInfo.create2( ATRID_SEVERITY, //
      DataType.create( VALOBJ, //
          TSID_KEEPER_ID, ESkAlarmSeverity.KEEPER_ID, //
          TSID_DEFAULT_VALUE, avValobj( ESkAlarmSeverity.WARNING ) //
      ), //
      TSID_NAME, STR_ALARM_SEVERTITY, //
      TSID_DESCRIPTION, STR_ALARM_SEVERTITY_D //
  );

  DtoClobInfo CLBINF_ALERT_CONDITION = DtoClobInfo.create2( CLBID_ALERT_CONDITION, //
      TSID_NAME, STR_ALERT_CONDITION, //
      TSID_DESCRIPTION, STR_ALERT_CONDITION_D, //
      TSID_KEEPER_ID, TsCombiCondInfo.KEEPER_ID //
  );

  DtoClobInfo CLBINF_MESSAGE_INFO = DtoClobInfo.create2( CLBID_MESSAGE_INFO, //
      TSID_NAME, STR_ALERT_MESSAGE_INFO, //
      TSID_DESCRIPTION, STR_ALERT_MESSAGE_INFO_D, //
      TSID_KEEPER_ID, SkMessageInfo.KEEPER_ID //
  );

  DtoRtdataInfo RTDINF_ALERT = DtoRtdataInfo.create2( RTDID_IS_ALERT, //
      DataType.create( BOOLEAN, //
          TSID_FORMAT_STRING, FMT_BOOL_CHECK, //
          TSID_DEFAULT_VALUE, AV_FALSE //
      ), //
      true, true, false, 1000, //
      TSID_NAME, STR_RTD_ALERT, //
      TSID_DESCRIPTION, STR_RTD_ALERT_D //
  );

  DtoRtdataInfo RTDINF_MUTED = DtoRtdataInfo.create2( RTDID_IS_MUTED, //
      DataType.create( BOOLEAN, //
          TSID_FORMAT_STRING, FMT_BOOL_CHECK, //
          TSID_DEFAULT_VALUE, AV_FALSE //
      ), //
      true, true, false, 1000, //
      TSID_NAME, STR_RTD_MUTED, //
      TSID_DESCRIPTION, STR_RTD_MUTED_D //
  );

  DtoCmdInfo CMDINF_ACKNOWLEDGE = DtoCmdInfo.create1( CMDID_ACKNOWLEDGE, //
      new StridablesList<>( //
          DataDef.create( CMDARGID_ACK_AUTHOR_GWID, VALOBJ, //
              TSID_NAME, STR_ACK_AUTHOR_GWID, //
              TSID_DESCRIPTION, STR_ACK_AUTHOR_GWID_D, //
              TSID_KEEPER_ID, Gwid.KEEPER_ID, //
              TSID_DEFAULT_VALUE, avValobj( Gwid.createObj( Skid.NONE ) ) //
          ), //
          DataDef.create( CMDARGID_ACK_COMMENT, STRING, //
              TSID_NAME, STR_ACK_COMMENT, //
              TSID_DESCRIPTION, STR_ACK_COMMENT_D, //
              TSID_DEFAULT_VALUE, AV_STR_EMPTY //
          ) //
      ), //
      OptionSetUtils.createOpSet( //
          TSID_NAME, STR_ACKNOWLEDGE_D, //
          TSID_DESCRIPTION, STR_ACKNOWLEDGE_D //
      ) );

  DtoEventInfo EVINF_ALERT = DtoEventInfo.create1( EVID_ALERT, true, //
      new StridablesList<>( //
          DataDef.create( EVPRMID_ALERT_MESSAGE, STRING, //
              TSID_NAME, STR_ALERT_MESSAGE, //
              TSID_DESCRIPTION, STR_ALERT_MESSAGE_D, //
              TSID_DEFAULT_VALUE, AV_STR_EMPTY //
          ) //
      ), //
      OptionSetUtils.createOpSet( //
          TSID_NAME, STR_EV_ALERT, //
          TSID_DESCRIPTION, STR_EV_ALERT_D //
      ) );

  DtoEventInfo EVINF_ACKNOWLEDGE = DtoEventInfo.create1( EVID_ACKNOWLEDGE, true, //
      new StridablesList<>( //
          DataDef.create( EVPRMID_ACK_AUTHOR, VALOBJ, //
              TSID_NAME, STR_ACK_AUTHOR_GWID, //
              TSID_DESCRIPTION, STR_ACK_AUTHOR_GWID_D, //
              TSID_KEEPER_ID, Gwid.KEEPER_ID, //
              TSID_DEFAULT_VALUE, avValobj( Gwid.createObj( Skid.NONE ) ) //
          ), //
          DataDef.create( EVPRMID_ACK_COMMNET, STRING, //
              TSID_NAME, STR_ACK_COMMNET, //
              TSID_DESCRIPTION, STR_ACK_COMMNET_D, //
              TSID_DEFAULT_VALUE, AV_STR_EMPTY //
          ) //
      ), //
      OptionSetUtils.createOpSet( //
          TSID_NAME, STR_EV_ACKNOWLEDGE, //
          TSID_DESCRIPTION, STR_EV_ACKNOWLEDGE_D //
      ) );

  DtoEventInfo EVINF_ALARM_MUTED = DtoEventInfo.create1( EVID_ALARM_MUTED, true, //
      new StridablesList<>( //
          DataDef.create( EVPRMID_MUTE_AUTHOR, VALOBJ, //
              TSID_NAME, STR_MUTE_AUTHOR_GWID, //
              TSID_DESCRIPTION, STR_MUTE_AUTHOR_GWID_D, //
              TSID_KEEPER_ID, Gwid.KEEPER_ID, //
              TSID_DEFAULT_VALUE, avValobj( Gwid.createObj( Skid.NONE ) ) //
          ), //
          DataDef.create( EVPRMID_MUTE_REASON, STRING, //
              TSID_NAME, STR_MUTE_REASON, //
              TSID_DESCRIPTION, STR_MUTE_REASON_D, //
              TSID_DEFAULT_VALUE, AV_STR_EMPTY //
          ) //
      ), //
      OptionSetUtils.createOpSet( //
          TSID_NAME, STR_EV_ALARM_MUTED, //
          TSID_DESCRIPTION, STR_EV_ALARM_MUTED_D //
      ) );

  DtoEventInfo EVINF_ALARM_UNMUTED = DtoEventInfo.create1( EVID_ALARM_UNMUTED, true, //
      new StridablesList<>(), //
      OptionSetUtils.createOpSet( //
          TSID_NAME, STR_EV_ALARM_UNMUTED, //
          TSID_DESCRIPTION, STR_EV_ALARM_UNMUTED_D //
      ) );

  IDtoClassInfo CLSINF_ALARM = DtoClassInfo.create( CLSID_ALARM, GW_ROOT_CLASS_ID, //
      OptionSetUtils.createOpSet( //
          TSID_NAME, STR_CLASS_ALARM, //
          TSID_DESCRIPTION, STR_CLASS_ALARM_D, //
          OPDEF_SK_IS_SOURCE_CODE_DEFINED_CLASS, AV_TRUE //
      ), //
      new StridablesList<>( ATRINF_SEVERITY ), //
      new StridablesList<>( CLBINF_ALERT_CONDITION, CLBINF_MESSAGE_INFO ), //
      IStridablesList.EMPTY, //
      IStridablesList.EMPTY, //
      new StridablesList<>( RTDINF_ALERT, RTDINF_MUTED ), //
      new StridablesList<>( CMDINF_ACKNOWLEDGE ), //
      new StridablesList<>( EVINF_ALERT, EVINF_ACKNOWLEDGE, EVINF_ALARM_MUTED, EVINF_ALARM_UNMUTED )//
  );

}
