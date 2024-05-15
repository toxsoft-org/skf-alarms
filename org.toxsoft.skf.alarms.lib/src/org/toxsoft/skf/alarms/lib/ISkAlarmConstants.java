package org.toxsoft.skf.alarms.lib;

import static org.toxsoft.core.tslib.av.EAtomicType.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.alarms.lib.l10n.ISkAlarmSharedResources.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;

import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.metainfo.*;
import org.toxsoft.core.tslib.math.cond.*;
import org.toxsoft.core.tslib.math.cond.impl.*;
import org.toxsoft.uskat.core.api.sysdescr.dto.*;
import org.toxsoft.uskat.core.impl.dto.*;
import org.toxsoft.uskat.core.utils.msgen.*;

/**
 * Alarm service constants.
 *
 * @author hazard157
 */
public interface ISkAlarmConstants {

  /**
   * ID of class {@link ISkAlarm}
   */
  String CLASS_ID_ALARM = SK_ID + ".Alarm"; //$NON-NLS-1$

  /**
   * ID of attribute {@link #ATRINF_SEVERITY}.
   */
  String ATRID_SEVERITY = "severity"; //$NON-NLS-1$

  /**
   * ID of attribute {@link #ATRINF_IS_EXTERNAL_ACK}.
   */
  String ATRID_IS_EXTERNAL_ACK = "isExternalAck"; //$NON-NLS-1$

  /**
   * ID of CLOB {@link #CLBINF_SEVERITY}
   */
  String CLBID_ALERT_CONDITION = "alertCondition"; //$NON-NLS-1$

  /**
   * ID of CLOB {@link #CLBINF_MESSAGE_INFO}
   */
  String CLBID_MESSAGE_INFO = "messageInfo"; //$NON-NLS-1$

  String RTDID_IS_ALERT = "alert"; //$NON-NLS-1$

  // FIXME usage of muted?
  String RTDID_IS_MUTED = "muted"; //$NON-NLS-1$

  String CMDID_ACKNOWLEDGE        = "cmdAcknowledge"; //$NON-NLS-1$
  String CMDARGID_ACK_AUTHOR_GWID = "ackAuthorGwid";  //$NON-NLS-1$
  String CMDARGID_ACK_REASON      = "ackReason";      //$NON-NLS-1$

  String EVID_ALERT            = "evAlert";      //$NON-NLS-1$
  String EVPRMID_ALERT_MESSAGE = "alertMessage"; //$NON-NLS-1$

  String EVID_ACKNOWLEDGE = "evAcknowledge"; //$NON-NLS-1$
  String EVID_MUTED       = "evMuted";       //$NON-NLS-1$
  String EVID_UNMUTED     = "evUnmuted";     //$NON-NLS-1$

  String EVPRMID_AUTHOR_GWID = "authorGwid"; //$NON-NLS-1$
  String EVPRMID_REASON      = "reason";     //$NON-NLS-1$

  /**
   * {@link ISkAlarm#severity()} attribute definition.
   */
  IDtoAttrInfo ATRINF_SEVERITY = DtoAttrInfo.create2( ATRID_SEVERITY, //
      DataType.create( VALOBJ, //
          TSID_KEEPER_ID, ESkAlarmSeverity.KEEPER_ID, //
          TSID_DEFAULT_VALUE, avValobj( ESkAlarmSeverity.WARNING ) //
      ), //
      TSID_NAME, "", //
      TSID_DESCRIPTION, "" //
  );

  /**
   * {@link ISkAlarm#alertCondition()} CLOB definition.
   */
  IDtoClobInfo CLBINF_ALERT_CONDITION = DtoClobInfo.create2( CLBID_ALERT_CONDITION, //
      TSID_NAME, "", //
      TSID_DESCRIPTION, "", //
      TSID_KEEPER_ID, TsCombiCondInfo.KEEPER_ID, //
      TSID_DEFAULT_VALUE, avValobj( ITsCombiCondInfo.NEVER ) //
  );

  /**
   * {@link ISkAlarm#isExternalAckowledgement()} attribute definition.
   */
  IDtoAttrInfo ATRINF_IS_EXTERNAL_ACK = DtoAttrInfo.create2( ATRID_IS_EXTERNAL_ACK, //
      DataType.create( DDEF_TS_BOOL, //
          TSID_DEFAULT_VALUE, AV_FALSE //
      ), //
      TSID_NAME, "", //
      TSID_DESCRIPTION, "" //
  );

  /**
   * {@link ISkAlarm#severity()} attribute definition.
   */
  IDtoClobInfo CLBINF_MESSAGE_INFO = DtoClobInfo.create2( CLBID_MESSAGE_INFO, //
      TSID_NAME, "", //
      TSID_DESCRIPTION, "", //
      TSID_KEEPER_ID, SkMessageInfo.KEEPER_ID, //
      TSID_DEFAULT_VALUE, avValobj( SkMessageInfo.NONE ) //
  );

  IDataDef EVPRMDEF_ALERT_MESSAGE = DataDef.create( EVPRMID_ALERT_MESSAGE, STRING, //
      TSID_NAME, STR_ALERT_MESSAGE, //
      TSID_DESCRIPTION, STR_ALERT_MESSAGE_D, //
      TSID_IS_MANDATORY, AV_TRUE, //
      TSID_IS_NULL_ALLOWED, AV_TRUE, //
      TSID_DEFAULT_VALUE, AV_STR_EMPTY //
  );

}
