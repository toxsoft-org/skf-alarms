package org.toxsoft.skf.alarms.skide.tasks.upload;

import static org.toxsoft.core.tslib.av.EAtomicType.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.core.tslib.bricks.gentask.IGenericTaskConstants.*;
import static org.toxsoft.skf.alarms.skide.ISkidePluginAlarmsSharedResources.*;
import static org.toxsoft.skide.core.ISkideCoreConstants.*;

import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.metainfo.*;
import org.toxsoft.core.tslib.bricks.ctx.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.*;
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

  static final String OPID_CLEAR_ALARMS_BEFORE_UPLOAD = SKIDE_FULL_ID + ".ClearAlarmsBeforeUpload"; //$NON-NLS-1$

  static final IDataDef OPDEF_CLEAR_ALARMS_BEFORE_UPLOAD = DataDef.create( OPID_CLEAR_ALARMS_BEFORE_UPLOAD, BOOLEAN, //
      TSID_NAME, STR_CLEAR_MNEMOS_BEFORE_UPLOAD, //
      TSID_DESCRIPTION, STR_CLEAR_MNEMOS_BEFORE_UPLOAD_D, //
      TSID_DEFAULT_VALUE, AV_TRUE //
  );

  /**
   * Constructor.
   *
   * @param aOwnerUnit {@link AbstractSkideUnit} - the owner unit
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  public TaskAlarmsUpload( AbstractSkideUnit aOwnerUnit ) {
    super( aOwnerUnit, UploadToServerTaskProcessor.INSTANCE.taskInfo(), // configuration options
        new StridablesList<>( //
            OPDEF_CLEAR_ALARMS_BEFORE_UPLOAD //
        ) );
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
    // if configured, remove all alarms from server
    if( OPDEF_CLEAR_ALARMS_BEFORE_UPLOAD.getValue( getCfgOptionValues() ).asBool() ) {
      IStridablesList<ISkAlarm> alarmsToRemove = destAlarmService.listAlarms();
      for( String alarmId : alarmsToRemove.ids() ) {
        destAlarmService.removeAlarm( alarmId );
      }
    }

    IStridablesList<ISkAlarm> srcAlarmsList = srcAlarmService.listAlarms();
    for( ISkAlarm srcAlarm : srcAlarmsList ) {
      DtoAlarm dto = DtoAlarm.makeAlarm( srcAlarm.id(), coreApi() );
      destAlarmService.defineAlarm( dto );
    }
    ValidationResult vr = ValidationResult.info( FMT_INFO_ALARMS_UPLOADED, srcAlarmsList.size() );
    lop.finished( vr );
    REFDEF_OUT_TASK_RESULT.setRef( aOutput, vr ); // FIXME log shows success, why?
  }

}
