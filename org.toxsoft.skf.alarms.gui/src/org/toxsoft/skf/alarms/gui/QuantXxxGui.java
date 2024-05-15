package org.toxsoft.skf.alarms.gui;

import org.eclipse.e4.core.contexts.*;
import org.toxsoft.core.tsgui.bricks.quant.*;

/**
 * The GUI library quant.
 *
 * @author hazard157
 */
public class QuantXxxGui
    extends AbstractQuant {

  /**
   * Constructor.
   */
  public QuantXxxGui() {
    super( QuantXxxGui.class.getSimpleName() );
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
