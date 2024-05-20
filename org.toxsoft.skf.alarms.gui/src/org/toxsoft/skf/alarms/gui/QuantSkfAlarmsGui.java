package org.toxsoft.skf.alarms.gui;

import org.eclipse.e4.core.contexts.*;
import org.toxsoft.core.tsgui.bricks.quant.*;

/**
 * The GUI library quant.
 *
 * @author hazard157
 */
public class QuantSkfAlarmsGui
    extends AbstractQuant {

  // TODO register VED items

  /**
   * Constructor.
   */
  public QuantSkfAlarmsGui() {
    super( QuantSkfAlarmsGui.class.getSimpleName() );
  }

  // ------------------------------------------------------------------------------------
  // AbstractQuant
  //

  @Override
  protected void doInitApp( IEclipseContext aAppContext ) {
    // nop
  }

  @Override
  protected void doInitWin( IEclipseContext aWinContext ) {
    ISkfAlarmsGuiConstants.init( aWinContext );
  }

}
