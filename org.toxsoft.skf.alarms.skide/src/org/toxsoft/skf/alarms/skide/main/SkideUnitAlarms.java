package org.toxsoft.skf.alarms.skide.main;

import static org.toxsoft.core.tsgui.bricks.actions.ITsStdActionDefs.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.alarms.gui.ISkfAlarmsGuiConstants.*;
import static org.toxsoft.skf.alarms.skide.ISkidePluginAlarmsSharedResources.*;
import static org.toxsoft.skide.core.ISkideCoreConstants.*;
import static org.toxsoft.skide.core.api.ucateg.ISkideUnitCategoryConstants.*;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.skf.alarms.skide.tasks.codegen.*;
import org.toxsoft.skf.alarms.skide.tasks.upload.*;
import org.toxsoft.skide.core.api.*;
import org.toxsoft.skide.core.api.impl.*;
import org.toxsoft.skide.core.api.tasks.*;

/**
 * SkiDE unit: unit template 1.
 *
 * @author hazard157
 */
public class SkideUnitAlarms
    extends AbstractSkideUnit {

  /**
   * The plugin ID.
   */
  public static final String UNIT_ID = SKIDE_FULL_ID + ".unit.mnemos"; //$NON-NLS-1$

  SkideUnitAlarms( ITsGuiContext aContext, AbstractSkidePlugin aCreator ) {
    super( UNIT_ID, OptionSetUtils.createOpSet( //
        TSID_NAME, STR_SKIDE_ALARMS_UNIT, //
        TSID_DESCRIPTION, STR_SKIDE_ALARMS_UNIT_D, //
        OPDEF_SKIDE_UNIT_CATEGORY, UCATEGID_SYSDESCR, //
        TSID_ICON_ID, ICONID_ALARM_INFOS_LIST //
    ), aContext, aCreator );
    unitActions().add( ACDEF_ABOUT );
  }

  @Override
  protected AbstractSkideUnitPanel doCreateUnitPanel( ITsGuiContext aContext ) {
    return new SkideUnitMnemoAlarms( aContext, this );
  }

  @Override
  protected void doFillTasks( IStringMapEdit<AbstractSkideUnitTask> aTaskRunnersMap ) {
    AbstractSkideUnitTask task = new TaskAlarmsCodegen( this );
    aTaskRunnersMap.put( task.taskInfo().id(), task );
    task = new TaskAlarmsUpload( this );
    aTaskRunnersMap.put( task.taskInfo().id(), task );
  }

}
