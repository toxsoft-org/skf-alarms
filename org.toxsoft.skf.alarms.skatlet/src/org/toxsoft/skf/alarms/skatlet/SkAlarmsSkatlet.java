package org.toxsoft.skf.alarms.skatlet;

import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.alarms.skatlet.ISkResources.*;

import org.toxsoft.core.tslib.av.opset.impl.OptionSetUtils;
import org.toxsoft.core.tslib.bricks.ctx.ITsContextRo;
import org.toxsoft.core.tslib.bricks.strid.coll.IStridablesList;
import org.toxsoft.core.tslib.bricks.threadexec.ITsThreadExecutor;
import org.toxsoft.core.tslib.bricks.validator.ValidationResult;
import org.toxsoft.core.tslib.math.cond.ITsSingleCondType;
import org.toxsoft.core.tslib.math.cond.checker.ITsCheckerTopicManager;
import org.toxsoft.core.tslib.utils.valobj.TsValobjUtils;
import org.toxsoft.skf.alarms.lib.ESkAlarmSeverity;
import org.toxsoft.skf.alarms.lib.ISkAlarmService;
import org.toxsoft.skf.alarms.lib.checkers.*;
import org.toxsoft.skf.alarms.lib.impl.SkAlarmProcessor;
import org.toxsoft.skf.alarms.lib.impl.SkAlarmService;
import org.toxsoft.uskat.core.ISkCoreApi;
import org.toxsoft.uskat.core.impl.SkThreadExecutorService;
import org.toxsoft.uskat.core.impl.SkatletBase;

/**
 * Alarm skatlet Writer for GBH.
 *
 * @author mvk
 */
public class SkAlarmsSkatlet
    extends SkatletBase {

  private ITsThreadExecutor threadExecutor;
  private SkAlarmProcessor  alarmProcecessor;

  /**
   * Constructor.
   */
  public SkAlarmsSkatlet() {
    super( SkAlarmsSkatlet.class.getSimpleName(), OptionSetUtils.createOpSet( //
        TSID_NAME, STR_N_SKATLET, //
        TSID_DESCRIPTION, STR_D_SKATLET //
    ) );
  }

  // ------------------------------------------------------------------------------------
  // SkatletBase
  //
  @Override
  protected ValidationResult doInit( ITsContextRo aEnviron ) {
    super.doInit( aEnviron );
    ISkCoreApi coreApi = connection().coreApi();
    threadExecutor = SkThreadExecutorService.getExecutor( coreApi );
    // if need register alarm keepers
    TsValobjUtils.registerKeeperIfNone( ESkAlarmSeverity.KEEPER_ID, ESkAlarmSeverity.KEEPER );
    // if need register alarm service
    if( !coreApi.services().hasKey( ISkAlarmService.SERVICE_ID ) ) {
      threadExecutor.syncExec( () -> coreApi.addService( SkAlarmService.CREATOR ) );
    }
    ISkAlarmService service = coreApi.getService( ISkAlarmService.SERVICE_ID );
    ITsCheckerTopicManager<ISkCoreApi> tm = service.getAlarmCheckersTopicManager();
    IStridablesList<ITsSingleCondType> types = tm.listSingleCondTypes();
    if( !types.hasKey( AlertCheckerRtdataVsConstType.TYPE_ID ) ) {
      tm.registerType( new AlertCheckerRtdataVsConstType() );
    }
    if( !types.hasKey( AlertCheckerRtdataVsRriType.TYPE_ID ) ) {
      tm.registerType( new AlertCheckerRtdataVsRriType() );
    }
    if( !types.hasKey( AlertCheckerRtdataVsAttrType.TYPE_ID ) ) {
      tm.registerType( new AlertCheckerRtdataVsAttrType() );
    }

    alarmProcecessor = new SkAlarmProcessor( coreApi );
    return ValidationResult.SUCCESS;
  }

  @Override
  public void start() {
    threadExecutor.syncExec( () -> alarmProcecessor.start() );
  }

  @Override
  public void doJob() {
    super.doJob();
    threadExecutor.syncExec( () -> alarmProcecessor.doJob() );
  }

  @Override
  public boolean queryStop() {
    super.queryStop();
    threadExecutor.syncExec( () -> alarmProcecessor.queryStop() );
    return alarmProcecessor.isStopped();
  }

  @Override
  public boolean isStopped() {
    return alarmProcecessor.isStopped();
  }

  @Override
  public void destroy() {
    super.destroy();
    threadExecutor.syncExec( () -> alarmProcecessor.destroy() );
  }
}
