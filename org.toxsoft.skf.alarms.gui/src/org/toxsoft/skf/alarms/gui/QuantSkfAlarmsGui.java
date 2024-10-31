package org.toxsoft.skf.alarms.gui;

import org.eclipse.e4.core.contexts.*;
import org.eclipse.e4.ui.model.application.ui.basic.*;
import org.toxsoft.core.tsgui.bricks.quant.*;
import org.toxsoft.core.tslib.utils.valobj.*;
import org.toxsoft.skf.alarms.gui.km5.*;
import org.toxsoft.skf.alarms.gui.panels.impl.*;
import org.toxsoft.skf.alarms.lib.impl.*;
import org.toxsoft.uskat.core.gui.km5.*;

/**
 * The GUI library quant.
 *
 * @author hazard157
 */
public class QuantSkfAlarmsGui
    extends AbstractQuant {

  private SoundAlarmManager soundAlarmManager;

  /**
   * Constructor.
   */
  public QuantSkfAlarmsGui() {
    super( QuantSkfAlarmsGui.class.getSimpleName() );
    SkfAlarmUtils.initialize();
    TsValobjUtils.registerKeeper( UsedUgwi4MessageInfo.KEEPER_ID, UsedUgwi4MessageInfo.KEEPER );
    // GUI initialization
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
    soundAlarmManager = new SoundAlarmManager();

    aWinContext.set( SoundAlarmManager.CONTEXT_ID, soundAlarmManager );
  }

  @Override
  protected void doCloseWin( MWindow aWindow ) {
    soundAlarmManager.setType( SoundAlarmType.NONE );
  }

}
