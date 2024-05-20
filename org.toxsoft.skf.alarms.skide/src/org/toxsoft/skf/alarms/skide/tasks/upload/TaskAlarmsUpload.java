package org.toxsoft.skf.alarms.skide.tasks.upload;

import static org.toxsoft.core.tslib.bricks.gentask.IGenericTaskConstants.*;
import static org.toxsoft.skf.alarms.skide.ISkidePluginAlarmsSharedResources.*;

import org.toxsoft.core.tslib.bricks.ctx.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.core.tslib.bricks.validator.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.alarms.lib.*;
import org.toxsoft.skf.alarms.lib.impl.*;
import org.toxsoft.skf.alarms.skide.main.*;
import org.toxsoft.skide.core.api.*;
import org.toxsoft.skide.core.api.tasks.*;
import org.toxsoft.skide.plugin.exconn.main.*;
import org.toxsoft.uskat.core.connection.*;

/**
 * SkIDE task {@link UploadToServerTaskProcessor} runner for {@link SkideUnitAlarms}.
 *
 * @author hazard157
 */
public class TaskAlarmsUpload
    extends AbstractSkideUnitTaskSync {

  /**
   * Constructor.
   *
   * @param aOwnerUnit {@link AbstractSkideUnit} - the owner unit
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  public TaskAlarmsUpload( AbstractSkideUnit aOwnerUnit ) {
    super( aOwnerUnit, UploadToServerTaskProcessor.INSTANCE.taskInfo(), IStridablesList.EMPTY );
  }

  // ------------------------------------------------------------------------------------
  // implementation
  //

  // ------------------------------------------------------------------------------------
  // AbstractSkideUnitTaskSync
  //

  @SuppressWarnings( "boxing" )
  @Override
  protected void doRunSync( ITsContextRo aInput, ITsContext aOutput ) {
    ILongOpProgressCallback lop = REFDEF_IN_PROGRESS_MONITOR.getRef( aInput );
    ISkAlarmService srcAlarmService = coreApi().getService( ISkAlarmService.SERVICE_ID );
    ISkConnection destConn = UploadToServerTaskProcessor.REFDEF_IN_OPEN_SK_CONN.getRef( aInput );
    ISkAlarmService destAlarmService = destConn.coreApi().getService( ISkAlarmService.SERVICE_ID );
    IStridablesList<ISkAlarm> srcAlarmsList = srcAlarmService.listAlarms();
    for( ISkAlarm srcAlarm : srcAlarmsList ) {
      DtoAlarm dto = DtoAlarm.makeAlarm( srcAlarm.classId(), coreApi() );
      destAlarmService.defineAlarm( dto );
    }
    ValidationResult vr = ValidationResult.info( FMT_INFO_ALARMS_UPLOADED, srcAlarmsList.size() );
    lop.finished( vr );
    REFDEF_OUT_TASK_RESULT.setRef( aOutput, vr ); // FIXME log shows success, why?
  }

}
