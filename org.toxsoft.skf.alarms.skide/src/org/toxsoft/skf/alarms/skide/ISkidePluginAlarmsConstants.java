package org.toxsoft.skf.alarms.skide;

import org.eclipse.e4.core.contexts.*;
import org.toxsoft.core.tsgui.graphics.icons.*;

/**
 * Plugin constants.
 *
 * @author hazard157
 */
@SuppressWarnings( "javadoc" )
public interface ISkidePluginAlarmsConstants {

  // ------------------------------------------------------------------------------------
  // Icons

  String PREFIX_OF_ICON_FIELD_NAME       = "ICONID_";                   //$NON-NLS-1$
  String ICONID_SKIDE_PLUGIN_ALARMS      = "skide-plugin-alarmss";      //$NON-NLS-1$
  String ICONID_SKIDE_UNIT_ALARMS_EDITOR = "skide-unit-alarmss-editor"; //$NON-NLS-1$

  /**
   * Constants registration.
   *
   * @param aWinContext {@link IEclipseContext} - windows level context
   */
  static void init( IEclipseContext aWinContext ) {
    ITsIconManager iconManager = aWinContext.get( ITsIconManager.class );
    iconManager.registerStdIconByIds( Activator.PLUGIN_ID, ISkidePluginAlarmsConstants.class,
        PREFIX_OF_ICON_FIELD_NAME );
    //
  }

}
