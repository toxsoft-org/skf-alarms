package org.toxsoft.skf.alarms.gui;

import static org.toxsoft.uskat.core.ISkHardConstants.*;

import org.eclipse.e4.core.contexts.*;
import org.toxsoft.core.tsgui.graphics.icons.*;

/**
 * Plugin common constants.
 *
 * @author hazard157
 */
@SuppressWarnings( "javadoc" )
public interface ISkfAlarmsGuiConstants {

  String SKF_ALARMS_ACT_ID = SK_ID + ".skf.Alarms.action"; //$NON-NLS-1$

  // ------------------------------------------------------------------------------------
  // Icons

  String PREFIX_OF_ICON_FIELD_NAME = "ICONID_";           //$NON-NLS-1$
  String ICONID_ALARM_INFO         = "alarm-info";        //$NON-NLS-1$
  String ICONID_APP_ALARM_EDITOR   = "app-alarms-editor"; //$NON-NLS-1$
  String ICONID_ALARM_INFOS_LIST   = "alarm-infos-list";  //$NON-NLS-1$
  String ICONID_ALERT_INFO         = "alert-info";        //$NON-NLS-1$
  String ICONID_ALERT_INFOS_LIST   = "alert-infos-list";  //$NON-NLS-1$
  String ICONID_ALERT_ACKNOWLEDGE  = "check_green";       //$NON-NLS-1$
  String ICONID_ALERTS_CHECK_GREEN = "check_green";       //$NON-NLS-1$
  String ICONID_ALERTS_CHECK_ALL   = "check_all_on";      //$NON-NLS-1$
  String ICONID_ALERTS_UNCHECK_ALL = "check_all_off";     //$NON-NLS-1$
  String ICONID_ALARM_MUTED_ALL    = "rupor-none";        //$NON-NLS-1$
  String ICONID_ALARM_UNMUTED_ALL  = "rupor";             //$NON-NLS-1$

  /**
   * Constants registration.
   *
   * @param aWinContext {@link IEclipseContext} - windows level context
   */
  static void init( IEclipseContext aWinContext ) {
    ITsIconManager iconManager = aWinContext.get( ITsIconManager.class );
    iconManager.registerStdIconByIds( Activator.PLUGIN_ID, ISkfAlarmsGuiConstants.class, PREFIX_OF_ICON_FIELD_NAME );
  }
}
