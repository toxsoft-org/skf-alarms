package org.toxsoft.skf.alarms.lib.l10n;

import org.toxsoft.skf.alarms.lib.*;
import org.toxsoft.skf.alarms.lib.impl.*;

/**
 * Localizable resources.
 *
 * @author hazard157
 */
@SuppressWarnings( "javadoc" )
public interface ISkAlarmSharedResources {

  /**
   * {@link ISkAlarmConstants}
   */
  String STR_ALARM_SEVERTITY       = Messages.getString( "STR_ALARM_SEVERTITY" );       //$NON-NLS-1$
  String STR_ALARM_SEVERTITY_D     = Messages.getString( "STR_ALARM_SEVERTITY_D" );     //$NON-NLS-1$
  String STR_ALERT_CONDITION       = Messages.getString( "STR_ALERT_CONDITION" );       //$NON-NLS-1$
  String STR_ALERT_CONDITION_D     = Messages.getString( "STR_ALERT_CONDITION_D" );     //$NON-NLS-1$
  String STR_ALERT_MESSAGE_INFO    = Messages.getString( "STR_ALERT_MESSAGE_INFO" );    //$NON-NLS-1$
  String STR_ALERT_MESSAGE_INFO_D  = Messages.getString( "STR_ALERT_MESSAGE_INFO_D" );  //$NON-NLS-1$
  String STR_RTD_ALERT             = Messages.getString( "STR_RTD_ALERT" );             //$NON-NLS-1$
  String STR_RTD_ALERT_D           = Messages.getString( "STR_RTD_ALERT_D" );           //$NON-NLS-1$
  String STR_RTD_MUTED             = Messages.getString( "STR_RTD_MUTED" );             //$NON-NLS-1$
  String STR_RTD_MUTED_D           = Messages.getString( "STR_RTD_MUTED_D" );           //$NON-NLS-1$
  String STR_ACK_AUTHOR_GWID       = Messages.getString( "STR_ACK_AUTHOR_GWID" );       //$NON-NLS-1$
  String STR_ACK_AUTHOR_GWID_D     = Messages.getString( "STR_ACK_AUTHOR_GWID_D" );     //$NON-NLS-1$
  String STR_ACK_COMMENT           = Messages.getString( "STR_ACK_COMMENT" );           //$NON-NLS-1$
  String STR_ACK_COMMENT_D         = Messages.getString( "STR_ACK_COMMENT_D" );         //$NON-NLS-1$
  String STR_ACKNOWLEDGE           = Messages.getString( "STR_ACKNOWLEDGE" );           //$NON-NLS-1$
  String STR_ACKNOWLEDGE_D         = Messages.getString( "STR_ACKNOWLEDGE_D" );         //$NON-NLS-1$
  String STR_ALERT_MESSAGE         = Messages.getString( "STR_ALERT_MESSAGE" );         //$NON-NLS-1$
  String STR_ALERT_MESSAGE_D       = Messages.getString( "STR_ALERT_MESSAGE_D" );       //$NON-NLS-1$
  String STR_EV_ALERT              = Messages.getString( "STR_EV_ALERT" );              //$NON-NLS-1$
  String STR_EV_ALERT_D            = Messages.getString( "STR_EV_ALERT_D" );            //$NON-NLS-1$
  String STR_ACK_COMMNET           = Messages.getString( "STR_ACK_COMMNET" );           //$NON-NLS-1$
  String STR_ACK_COMMNET_D         = Messages.getString( "STR_ACK_COMMNET_D" );         //$NON-NLS-1$
  String STR_EV_ACKNOWLEDGE        = Messages.getString( "STR_EV_ACKNOWLEDGE" );        //$NON-NLS-1$
  String STR_EV_ACKNOWLEDGE_D      = Messages.getString( "STR_EV_ACKNOWLEDGE_D" );      //$NON-NLS-1$
  String STR_MUTE_AUTHOR_GWID      = Messages.getString( "STR_MUTE_AUTHOR_GWID" );      //$NON-NLS-1$
  String STR_MUTE_AUTHOR_GWID_D    = Messages.getString( "STR_MUTE_AUTHOR_GWID_D" );    //$NON-NLS-1$
  String STR_MUTE_REASON           = Messages.getString( "STR_MUTE_REASON" );           //$NON-NLS-1$
  String STR_MUTE_REASON_D         = Messages.getString( "STR_MUTE_REASON_D" );         //$NON-NLS-1$
  String STR_EV_ALARM_MUTED        = Messages.getString( "STR_EV_ALARM_MUTED" );        //$NON-NLS-1$
  String STR_EV_ALARM_MUTED_D      = Messages.getString( "STR_EV_ALARM_MUTED_D" );      //$NON-NLS-1$
  String STR_EV_ALARM_UNMUTED      = Messages.getString( "STR_EV_ALARM_UNMUTED" );      //$NON-NLS-1$
  String STR_EV_ALARM_UNMUTED_D    = Messages.getString( "STR_EV_ALARM_UNMUTED_D" );    //$NON-NLS-1$
  String STR_CLASS_ALARM           = Messages.getString( "STR_CLASS_ALARM" );           //$NON-NLS-1$
  String STR_CLASS_ALARM_D         = Messages.getString( "STR_CLASS_ALARM_D" );         //$NON-NLS-1$
  String STR_ABKIND_ALARMS         = Messages.getString( "STR_ABKIND_ALARMS" );         //$NON-NLS-1$
  String STR_ABKIND_ALARMS_D       = Messages.getString( "STR_ABKIND_ALARMS_D" );       //$NON-NLS-1$
  String STR_ABILITY_EDIT_ALARMS   = Messages.getString( "STR_ABILITY_EDIT_ALARMS" );   //$NON-NLS-1$
  String STR_ABILITY_EDIT_ALARMS_D = Messages.getString( "STR_ABILITY_EDIT_ALARMS_D" ); //$NON-NLS-1$

  /**
   * {@link ESkAlarmSeverity}
   */
  String STR_ALSEV_WARNING    = Messages.getString( "STR_ALSEV_WARNING" );    //$NON-NLS-1$
  String STR_ALSEV_WARNING_D  = Messages.getString( "STR_ALSEV_WARNING_D" );  //$NON-NLS-1$
  String STR_ALSEV_CRITICAL   = Messages.getString( "STR_ALSEV_CRITICAL" );   //$NON-NLS-1$
  String STR_ALSEV_CRITICAL_D = Messages.getString( "STR_ALSEV_CRITICAL_D" ); //$NON-NLS-1$

  /**
   * {@link SkAlarmService}
   */
  String FMT_NO_ALARM_ID_TO_REMOVE = Messages.getString( "FMT_NO_ALARM_ID_TO_REMOVE" ); //$NON-NLS-1$

  // ------------------------------------------------------------------------------------
  // Log messages need no translation

  /**
   * {@link SkAlarmProcessor}
   */
  String FMT_LOG_WARN_NOT_ALARM_FOR_MUTE    = "does not knows about alarm with STRID '%s'";          //$NON-NLS-1$
  String FMT_LOG_WARN_INV_CMD_DEST_CLASS    = "invalid command class ID '%s'";                       //$NON-NLS-1$
  String FMT_LOG_WARN_INV_CMD_ID            = "invalid command ID '%s'";                             //$NON-NLS-1$
  String FMT_LOG_WARN_INV_CMD_STRID         = "invalid command STRID '%s'";                          //$NON-NLS-1$
  String FMT_LOG_WARN_ACK_CMD_NO_ALERT      = "ACK command on alarm with no alert '%s'";             //$NON-NLS-1$
  String FMT_ERR_UNEXPECTED_CHECKER         = "unexpected checker error. alarmId = %s. Cause = %s."; //$NON-NLS-1$
  String FMT_LOG_DOJOB_IS_COMPETED          = "doJob is completed. alarms = %d, time = %d (msec).";  //$NON-NLS-1$
  String FMT_LOG_SET_ALARM_ALERT            = "%s: sets alert state.";                               //$NON-NLS-1$
  String FMT_LOG_RESET_ALARM_ALERT          = "%s: reset alert state by command %s.";                //$NON-NLS-1$
  String FMT_LOG_ALERT_ALARM_STATE          = "%s: isAlert = %s, isMute = %s, condCheckState = %s";  //$NON-NLS-1$
  /**
   * {@link SkAlarm}
   */
  String FMT_LOG_WARN_CLOSED_RTDATA_CHANNEL = "Data channel was expected to be open, RTdata ID: %s"; //$NON-NLS-1$

}
