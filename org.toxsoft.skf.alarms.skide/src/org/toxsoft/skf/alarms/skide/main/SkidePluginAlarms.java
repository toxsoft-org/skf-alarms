package org.toxsoft.skf.alarms.skide.main;

import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.alarms.gui.ISkfAlarmsGuiConstants.*;
import static org.toxsoft.skf.alarms.skide.ISkidePluginAlarmsSharedResources.*;
import static org.toxsoft.skide.core.ISkideCoreConstants.*;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.skide.core.api.*;

/**
 * SkIDE plugin: alarms.
 *
 * @author hazard157
 */
public class SkidePluginAlarms
    extends AbstractSkidePlugin {

  /**
   * The plugin ID.
   */
  public static final String SKIDE_PLUGIN_ID = SKIDE_FULL_ID + ".plugin.alarms"; //$NON-NLS-1$

  /**
   * The singleton instance.
   */
  public static final AbstractSkidePlugin INSTANCE = new SkidePluginAlarms();

  SkidePluginAlarms() {
    super( SKIDE_PLUGIN_ID, OptionSetUtils.createOpSet( //
        TSID_NAME, STR_SKIDE_ALARMS_UNIT, //
        TSID_DESCRIPTION, STR_SKIDE_ALARMS_UNIT_D, //
        TSID_ICON_ID, ICONID_APP_ALARM_EDITOR //
    ) );
  }

  @Override
  protected void doCreateUnits( ITsGuiContext aContext, IStridablesListEdit<ISkideUnit> aUnitsList ) {
    aUnitsList.add( new SkideUnitAlarms( aContext, this ) );
  }

}
