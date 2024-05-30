package org.toxsoft.skf.alarms.gui;

import org.eclipse.e4.core.contexts.*;
import org.toxsoft.core.tsgui.bricks.quant.*;
import org.toxsoft.core.tslib.math.cond.checker.*;
import org.toxsoft.core.tslib.utils.valobj.*;
import org.toxsoft.skf.alarms.gui.km5.*;
import org.toxsoft.skf.alarms.lib.*;
import org.toxsoft.skf.alarms.lib.checkers.*;
import org.toxsoft.skf.alarms.lib.impl.*;
import org.toxsoft.uskat.core.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;
import org.toxsoft.uskat.core.gui.km5.*;
import org.toxsoft.uskat.core.impl.*;

/**
 * The GUI library quant.
 *
 * @author hazard157
 */
public class QuantSkfAlarmsGui
    extends AbstractQuant {

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
    ISkConnectionSupplier connSup = aWinContext.get( ISkConnectionSupplier.class );
    ISkConnection conn = connSup.defConn();

    ISkAlarmService alarmService = conn.coreApi().getService( ISkAlarmService.SERVICE_ID );
    TsCheckerTopicManager<ISkCoreApi> tm =
        (TsCheckerTopicManager<ISkCoreApi>)alarmService.getAlarmCheckersTopicManager();
    // add my checkers here
    tm.registerType( new AlertCheckerRtdataVsConstType() );
    tm.registerType( new AlertCheckerRtdataVsRriType() );
    tm.registerType( new AlertCheckerRtdataVsAttrType() );
  }

}
