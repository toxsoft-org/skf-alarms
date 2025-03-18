package org.toxsoft.skf.alarms.gui;

import org.toxsoft.skf.alarms.gui.km5.*;
import org.toxsoft.skf.alarms.gui.panels.impl.*;

/**
 * Localizable resources.
 *
 * @author hazard157
 */
@SuppressWarnings( { "nls", "javadoc" } )
public interface ISkResources {

  String STR_DO_IT   = Messages.getString( "STR_DO_IT" );   //$NON-NLS-1$
  String STR_DO_IT_D = Messages.getString( "STR_DO_IT_D" ); //$NON-NLS-1$

  /**
   * {@link SkMessageInfoM5Model}
   */
  String STR_N_FDEF_FMT_STR = Messages.getString("STR_N_FDEF_FMT_STR"); //$NON-NLS-1$
  String STR_D_FDEF_FMT_STR = Messages.getString("STR_D_FDEF_FMT_STR"); //$NON-NLS-1$
  String STR_N_USED_UGWIES  = Messages.getString("STR_N_USED_UGWIES"); //$NON-NLS-1$
  String STR_D_USED_UGWIES  = Messages.getString("STR_D_USED_UGWIES"); //$NON-NLS-1$

  /**
   * {@link UsedUgwi4MessageInfoM5Model}
   */
  String STR_N_IDPATH    = Messages.getString("STR_N_IDPATH"); //$NON-NLS-1$
  String STR_D_IDPATH    = Messages.getString("STR_D_IDPATH"); //$NON-NLS-1$
  String STR_N_USED_UGWI = Messages.getString("STR_N_USED_UGWI"); //$NON-NLS-1$
  String STR_D_USED_UGWI = Messages.getString("STR_D_USED_UGWI"); //$NON-NLS-1$

  String STR_N_EV_GWID      = Messages.getString("STR_N_EV_GWID"); //$NON-NLS-1$
  String STR_D_EV_GWID      = Messages.getString("STR_D_EV_GWID"); //$NON-NLS-1$
  String STR_N_EVENT_PARAMS = Messages.getString("STR_N_EVENT_PARAMS"); //$NON-NLS-1$
  String STR_D_EVENT_PARAMS = Messages.getString("STR_D_EVENT_PARAMS"); //$NON-NLS-1$

  /**
   * {@link ConfirmDlg}
   */
  String DLG_C_ALARM_ACKNOWLEDGE = Messages.getString( "DLG_C_ALARM_ACKNOWLEDGE" ); //$NON-NLS-1$
  String DLG_T_ALARM_ACKNOWLEDGE = Messages.getString( "DLG_T_ALARM_ACKNOWLEDGE" ); //$NON-NLS-1$
  String STR_L_ALARM_COMMENT     = Messages.getString( "STR_L_ALARM_COMMENT" );     //$NON-NLS-1$
  String DLG_C_ALARM_REASON      = Messages.getString( "DLG_C_ALARM_REASON" );      //$NON-NLS-1$
  String DLG_T_ALARM_REASON      = Messages.getString( "DLG_C_ALARM_REASON" );      //$NON-NLS-1$

  String STR_L_ALARM_REASON                 = Messages.getString( "STR_L_ALARM_REASON" );                  //$NON-NLS-1$
  String STR_N_ALARM_ACKNOWLEDGE            = Messages.getString( "STR_N_ALARM_ACKNOWLEDGE" );             //$NON-NLS-1$
  String STR_ALARM_ACKNOWLEDGE_DFLT_COMMENT = Messages.getString("STR_ALARM_ACKNOWLEDGE_DFLT_COMMENT"); //$NON-NLS-1$
  String STR_D_ALARM_ACKNOWLEDGE            = Messages.getString( "STR_D_ALARM_ACKNOWLEDGE" );             //$NON-NLS-1$
  String STR_N_ALARM_CHECK_ALL              = Messages.getString( "STR_N_ALARM_CHECK_ALL" );               //$NON-NLS-1$
  String STR_D_ALARM_CHECK_ALL              = Messages.getString( "STR_D_ALARM_CHECK_ALL" );               //$NON-NLS-1$
  String STR_N_ALARM_UNCHECK_ALL            = Messages.getString( "STR_N_ALARM_UNCHECK_ALL" );             //$NON-NLS-1$
  String STR_D_ALARM_UNCHECK_ALL            = Messages.getString( "STR_D_ALARM_UNCHECK_ALL" );             //$NON-NLS-1$
  String STR_N_MUTE_ALL                     = Messages.getString("STR_N_MUTE_ALL"); //$NON-NLS-1$
  String STR_D_MUTE_ALL                     = Messages.getString("STR_D_MUTE_ALL"); //$NON-NLS-1$
  String STR_N_EVENT_TIME                   = Messages.getString( "STR_N_EVENT_TIME" );                    //$NON-NLS-1$
  String STR_D_EVENT_TIME                   = Messages.getString( "STR_D_EVENT_TIME" );                    //$NON-NLS-1$

  String STR_N_ALARM_NAME = Messages.getString( "STR_N_ALARM_NAME" ); //$NON-NLS-1$
  String STR_D_ALARM_NAME = Messages.getString( "STR_D_ALARM_NAME" ); //$NON-NLS-1$

  String STR_N_ALARM_DESCRIPTION = Messages.getString( "STR_N_ALARM_DESCRIPTION" ); //$NON-NLS-1$
  String STR_D_ALARM_DESCRIPTION = Messages.getString( "STR_D_ALARM_DESCRIPTION" ); //$NON-NLS-1$

  String STR_N_ALERT_EVENT_MESSAGE = Messages.getString( "STR_N_ALERT_EVENT_MESSAGE" ); //$NON-NLS-1$
  String STR_D_ALERT_EVENT_MESSAGE = Messages.getString( "STR_D_ALERT_EVENT_MESSAGE" ); //$NON-NLS-1$

  String STR_N_ALERT_SEVERITY = Messages.getString( "STR_N_ALERT_SEVERITY" ); //$NON-NLS-1$
  String STR_D_ALERT_SEVERITY = Messages.getString( "STR_D_ALERT_SEVERITY" ); //$NON-NLS-1$

  String STR_WARNING_SEVERITY_ALARM  = Messages.getString( "STR_WARNING_SEVERITY_ALARM" );  //$NON-NLS-1$
  String STR_CRITICAL_SEVERITY_ALARM = Messages.getString( "STR_CRITICAL_SEVERITY_ALARM" ); //$NON-NLS-1$

  String STR_N_EVENT_TYPE_NAME = Messages.getString( "STR_N_EVENT_TYPE_NAME" ); //$NON-NLS-1$
  String STR_D_EVENT_TYPE_NAME = Messages.getString( "STR_D_EVENT_TYPE_NAME" ); //$NON-NLS-1$

  String STR_QUERY_INTERVAL         = Messages.getString( "STR_QUERY_INTERVAL" );         //$NON-NLS-1$
  String STR_ALERT_EVENT_TYPE       = Messages.getString( "STR_ALERT_EVENT_TYPE" );       //$NON-NLS-1$
  String STR_ACKNOWLEDGE_EVENT_TYPE = Messages.getString( "STR_ACKNOWLEDGE_EVENT_TYPE" ); //$NON-NLS-1$
  String STR_MUTED_EVENT_TYPE       = Messages.getString( "STR_MUTED_EVENT_TYPE" );       //$NON-NLS-1$
  String STR_UNMUTED_EVENT_TYPE     = Messages.getString( "STR_UNMUTED_EVENT_TYPE" );     //$NON-NLS-1$

  String STR_N_EVENT_PARAMETERS = Messages.getString( "STR_N_EVENT_PARAMETERS" ); //$NON-NLS-1$
  String STR_D_EVENT_PARAMETERS = Messages.getString( "STR_D_EVENT_PARAMETERS" ); //$NON-NLS-1$

  String STR_N_ACKNOWLEDGE_COMMENT = Messages.getString( "STR_N_ACKNOWLEDGE_COMMENT" ); //$NON-NLS-1$
  String STR_D_ACKNOWLEDGE_COMMENT = Messages.getString( "STR_D_ACKNOWLEDGE_COMMENT" ); //$NON-NLS-1$

  String STR_N_ACKNOWLEDGE_AUTHOR = Messages.getString( "STR_N_ACKNOWLEDGE_AUTHOR" ); //$NON-NLS-1$
  String STR_D_ACKNOWLEDGE_AUTHOR = Messages.getString( "STR_D_ACKNOWLEDGE_AUTHOR" ); //$NON-NLS-1$

  String STR_N_ALARM_SEVERITY = Messages.getString( "STR_N_ALARM_SEVERITY" ); //$NON-NLS-1$
  String STR_D_ALARM_SEVERITY = Messages.getString( "STR_D_ALARM_SEVERITY" ); //$NON-NLS-1$

  String STR_N_ALARM_ISALERT = Messages.getString( "STR_N_ALARM_ISALERT" ); //$NON-NLS-1$
  String STR_D_ALARM_ISALERT = Messages.getString( "STR_D_ALARM_ISALERT" ); //$NON-NLS-1$

  String STR_N_ALARM_ISMUTED = Messages.getString( "STR_N_ALARM_ISMUTED" ); //$NON-NLS-1$
  String STR_D_ALARM_ISMUTED = Messages.getString( "STR_D_ALARM_ISMUTED" ); //$NON-NLS-1$

  String STR_N_ALARM_MUTEREASON = Messages.getString( "STR_N_ALARM_MUTEREASON" ); //$NON-NLS-1$
  String STR_D_ALARM_MUTEREASON = Messages.getString( "STR_D_ALARM_MUTEREASON" ); //$NON-NLS-1$

  String STR_N_ALARM_MUTED_ALL = Messages.getString( "STR_N_ALARM_MUTED_ALL" ); //$NON-NLS-1$
  String STR_D_ALARM_MUTED_ALL = Messages.getString( "STR_D_ALARM_MUTED_ALL" ); //$NON-NLS-1$

  String STR_N_ALARM_UNMUTED_ALL = Messages.getString( "STR_N_ALARM_UNMUTED_ALL" ); //$NON-NLS-1$
  String STR_D_ALARM_UNMUTED_ALL = Messages.getString( "STR_D_ALARM_UNMUTED_ALL" ); //$NON-NLS-1$

  String STR_COMMON_INFORMATION = Messages.getString( "STR_COMMON_INFORMATION" ); //$NON-NLS-1$
  String STR_EVENTS_HISTORY     = Messages.getString( "STR_EVENTS_HISTORY" );     //$NON-NLS-1$

  String STR_ACCESS_ALARM_VALUES_EDITOR   = Messages.getString( "STR_ACCESS_ALARM_VALUES_EDITOR" );   //$NON-NLS-1$
  String STR_ACCESS_ALARM_VALUES_EDITOR_D = Messages.getString( "STR_ACCESS_ALARM_VALUES_EDITOR_D" ); //$NON-NLS-1$

  String STR_QUERIENG_ALARM_HISTORY       = Messages.getString("STR_QUERIENG_ALARM_HISTORY"); //$NON-NLS-1$
  String STR_PREPARE_ALARMS_HISTORY_QUERY = Messages.getString("STR_PREPARE_ALARMS_HISTORY_QUERY"); //$NON-NLS-1$
  String STR_PREPARE_ALARMS_HISTORY_VIEW  = Messages.getString("STR_PREPARE_ALARMS_HISTORY_VIEW"); //$NON-NLS-1$
  String ERR_QUERY_ALARM_HISTORY_FAILED   = Messages.getString("ERR_QUERY_ALARM_HISTORY_FAILED"); //$NON-NLS-1$

}
