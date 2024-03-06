package org.toxsoft.skf.alarms.gui.e4.addons;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.toxsoft.core.tsgui.bricks.quant.IQuantRegistrator;
import org.toxsoft.core.tsgui.m5.IM5Domain;
import org.toxsoft.core.tsgui.mws.bases.MwsAbstractAddon;
import org.toxsoft.skf.alarms.gui.Activator;
import org.toxsoft.skf.alarms.gui.ISkAlarmGuiConstants;
import org.toxsoft.skf.alarms.gui.m5.SkAlarmM5Model;

/**
 * Plugin addon.
 *
 * @author vs
 */
public class AddonSkAlarms
    extends MwsAbstractAddon {

  /**
   * Constructor.
   */
  public AddonSkAlarms() {
    super( Activator.PLUGIN_ID );
  }

  @Override
  protected void initApp( IEclipseContext aAppContext ) {
    // nop
  }

  @Override
  protected void doRegisterQuants( IQuantRegistrator aQuantRegistrator ) {
    //
  }

  @Override
  protected void initWin( IEclipseContext aWinContext ) {
    ISkAlarmGuiConstants.init( aWinContext );

    // регистрируем свои org.toxsoft.skf.alarms.gui.m5 модели TODO перенести в специальное место, см.
    // KM5DataAliasesContributor
    IM5Domain m5 = aWinContext.get( IM5Domain.class );
    m5.addModel( new SkAlarmM5Model( false ) );
  }

}
