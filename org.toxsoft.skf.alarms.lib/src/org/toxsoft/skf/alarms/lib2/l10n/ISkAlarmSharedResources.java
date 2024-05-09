package org.toxsoft.skf.alarms.lib2.l10n;

/**
 * Localizable resources.
 *
 * @author hazard157
 */
@SuppressWarnings( "javadoc" )
public interface ISkAlarmSharedResources {

  String STR_ALSEV_WARNING    = Messages.getString( "STR_ALSEV_WARNING" );    //$NON-NLS-1$
  String STR_ALSEV_WARNING_D  = Messages.getString( "STR_ALSEV_WARNING_D" );  //$NON-NLS-1$
  String STR_ALSEV_CRITICAL   = Messages.getString( "STR_ALSEV_CRITICAL" );   //$NON-NLS-1$
  String STR_ALSEV_CRITICAL_D = Messages.getString( "STR_ALSEV_CRITICAL_D" ); //$NON-NLS-1$

  String STR_ALARM_SKID      = Messages.getString( "STR_ALARM_SKID" );      //$NON-NLS-1$
  String STR_ALARM_SKID_D    = Messages.getString( "STR_ALARM_SKID_D" );    //$NON-NLS-1$
  String STR_ALARM_MESSAGE   = Messages.getString( "STR_ALARM_MESSAGE" );   //$NON-NLS-1$
  String STR_ALARM_MESSAGE_D = Messages.getString( "STR_ALARM_MESSAGE_D" ); //$NON-NLS-1$

  // ------------------------------------------------------------------------------------
  // Log messages need no translation

  String FMT_LOG_WARN_NOT_ALARM_FOR_MUTE = "SlAlarmProcessor: does not knows about alarm with STRID '%s'"; //$NON-NLS-1$
  String FMT_LOG_WARN_INV_CMD_DEST_CLASS = "SlAlarmProcessor: invalid command class ID '%s'";              //$NON-NLS-1$
  String FMT_LOG_WARN_INV_CMD_ID         = "SlAlarmProcessor: invalid command ID '%s'";                    //$NON-NLS-1$
  String FMT_LOG_WARN_INV_CMD_STRID      = "SlAlarmProcessor: invalid command STRID '%s'";                 //$NON-NLS-1$
  String FMT_LOG_WARN_ACK_CMD_NO_ALERT   = "SlAlarmProcessor: ACK command on alarm with no alert '%s'";    //$NON-NLS-1$

}
