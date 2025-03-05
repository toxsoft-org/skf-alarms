package org.toxsoft.skf.alarms.mws.addons;

import static org.toxsoft.skf.alarms.lib.ISkAlarmConstants.*;
import static org.toxsoft.skf.alarms.mws.e4.IAlarmsConstants.*;

import org.eclipse.e4.core.contexts.*;
import org.toxsoft.core.tsgui.bricks.ctx.impl.*;
import org.toxsoft.core.tsgui.bricks.quant.*;
import org.toxsoft.core.tsgui.mws.bases.*;
import org.toxsoft.skf.alarms.gui.*;
import org.toxsoft.skf.alarms.mws.Activator;
import org.toxsoft.skf.alarms.mws.e4.*;
import org.toxsoft.uskat.core.gui.utils.*;
import org.toxsoft.uskat.core.impl.*;

/**
 * Plugin addon.
 *
 * @author dima
 */
public class AddonAlarmViewer
    extends MwsAbstractAddon {

  /**
   * Constructor.
   */
  public AddonAlarmViewer() {
    super( Activator.PLUGIN_ID );
  }

  // ------------------------------------------------------------------------------------
  // MwsAbstractAddon
  //

  @Override
  protected void initApp( IEclipseContext aAppContext ) {
    // nop
  }

  @Override
  protected void initWin( IEclipseContext aWinContext ) {
    IAlarmsConstants.init( aWinContext );
    // implement access rights ABILITYID_ALARM_VALUES_EDITOR
    GuiE4ElementsToAbilitiesBinder binder = new GuiE4ElementsToAbilitiesBinder( new TsGuiContext( aWinContext ) );
    binder.bindPerspective( ABILITYID_EDIT_ALARMS, E4_VISUAL_ELEM_ID_PERSP_ALARMS_VALUES );
    binder.bindMenuElement( ABILITYID_EDIT_ALARMS, E4_VISUAL_ELEM_ID_MENU_ITEEM_ALARMS_VALUES );
    binder.bindToolItem( ABILITYID_EDIT_ALARMS, E4_VISUAL_ELEM_ID_TOOL_ITEEM_ALARMS_VALUES );
    SkCoreUtils.registerCoreApiHandler( binder );
  }

  @Override
  protected void doRegisterQuants( IQuantRegistrator aQuantRegistrator ) {
    aQuantRegistrator.registerQuant( new QuantSkfAlarmsGui() );
  }
}
