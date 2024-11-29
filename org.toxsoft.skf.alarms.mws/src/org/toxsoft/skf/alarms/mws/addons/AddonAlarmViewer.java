package org.toxsoft.skf.alarms.mws.addons;

import org.eclipse.e4.core.contexts.*;
import org.toxsoft.core.tsgui.bricks.quant.*;
import org.toxsoft.core.tsgui.mws.bases.*;
import org.toxsoft.skf.alarms.gui.*;
import org.toxsoft.skf.alarms.mws.Activator;
import org.toxsoft.skf.alarms.mws.e4.*;

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
  }

  @Override
  protected void doRegisterQuants( IQuantRegistrator aQuantRegistrator ) {
    aQuantRegistrator.registerQuant( new QuantSkfAlarmsGui() );
  }
}
