package org.toxsoft.skf.alarms.gui;

import org.eclipse.e4.core.contexts.*;
import org.toxsoft.core.tsgui.bricks.quant.*;
import org.toxsoft.core.tslib.utils.valobj.*;
import org.toxsoft.skf.alarms.gui.km5.*;
import org.toxsoft.skf.alarms.lib.*;
import org.toxsoft.skf.alarms.lib.impl.*;
import org.toxsoft.uskat.core.gui.km5.*;
import org.toxsoft.uskat.core.impl.*;

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
    TsValobjUtils.registerKeeper( ESkAlarmSeverity.KEEPER_ID, ESkAlarmSeverity.KEEPER );
    SkCoreUtils.registerSkServiceCreator( SkAlarmService.CREATOR );
    KM5Utils.registerContributorCreator( KM5AlarmsContributor.CREATOR );
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
