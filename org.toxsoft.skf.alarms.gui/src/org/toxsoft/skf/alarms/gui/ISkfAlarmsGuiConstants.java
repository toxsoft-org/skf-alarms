package org.toxsoft.skf.alarms.gui;

import static org.toxsoft.skf.alarms.gui.ISkResources.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;

import org.eclipse.e4.core.contexts.*;
import org.toxsoft.core.tsgui.bricks.actions.*;
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

  String PREFIX_OF_ICON_FIELD_NAME = "ICONID_";  //$NON-NLS-1$
  String ICONID_APP_ICON           = "app-icon"; //$NON-NLS-1$

  // ------------------------------------------------------------------------------------
  // Actions

  String ACTID_DO_IT = SKF_ALARMS_ACT_ID + ".do_it"; //$NON-NLS-1$

  ITsActionDef ACDEF_DO_IT = TsActionDef.ofPush2( ACTID_DO_IT, //
      STR_DO_IT, STR_DO_IT_D, ICONID_APP_ICON );

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
