package org.toxsoft.skf.alarms.skide.e4.addons;

import org.eclipse.e4.core.contexts.*;
import org.toxsoft.core.tsgui.bricks.quant.*;
import org.toxsoft.core.tsgui.mws.bases.*;
import org.toxsoft.skf.alarms.gui.*;
import org.toxsoft.skf.alarms.skide.*;
import org.toxsoft.skf.alarms.skide.Activator;
import org.toxsoft.skf.alarms.skide.main.*;
import org.toxsoft.skide.core.api.*;

/**
 * Plugin addon.
 *
 * @author vs
 */
public class AddonSkidePluginAlarms
    extends MwsAbstractAddon {

  /**
   * Constructor.
   */
  public AddonSkidePluginAlarms() {
    super( Activator.PLUGIN_ID );
  }

  // ------------------------------------------------------------------------------------
  // MwsAbstractAddon
  //

  @Override
  protected void doRegisterQuants( IQuantRegistrator aQuantRegistrator ) {
    aQuantRegistrator.registerQuant( new QuantSkfAlarmsGui() );
  }

  @Override
  protected void initApp( IEclipseContext aAppContext ) {
    ISkideEnvironment skEnv = aAppContext.get( ISkideEnvironment.class );
    skEnv.pluginsRegistrator().registerPlugin( SkidePluginAlarms.INSTANCE );
  }

  @Override
  protected void initWin( IEclipseContext aWinContext ) {
    ISkidePluginAlarmsConstants.init( aWinContext );
  }

}
