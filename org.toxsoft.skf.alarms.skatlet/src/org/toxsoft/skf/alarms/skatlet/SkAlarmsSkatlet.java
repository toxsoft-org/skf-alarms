package org.toxsoft.skf.alarms.skatlet;

import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.alarms.skatlet.ISkResources.*;

import org.toxsoft.core.tslib.av.math.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.ctx.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.core.tslib.bricks.threadexec.*;
import org.toxsoft.core.tslib.bricks.validator.*;
import org.toxsoft.core.tslib.math.cond.*;
import org.toxsoft.core.tslib.math.cond.checker.*;
import org.toxsoft.core.tslib.utils.valobj.*;
import org.toxsoft.skf.alarms.lib.*;
import org.toxsoft.skf.alarms.lib.checkers.*;
import org.toxsoft.skf.alarms.lib.impl.*;
import org.toxsoft.skf.rri.lib.*;
import org.toxsoft.skf.rri.lib.impl.*;
import org.toxsoft.uskat.core.*;
import org.toxsoft.uskat.core.impl.*;

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

    // 2024-10-20 mvk на существующем API нельзя использовать SkfAlarmUtils.initialize(): соединение уже существует(!)
    // if need register alarm keepers
    TsValobjUtils.registerKeeperIfNone( ESkAlarmSeverity.KEEPER_ID, ESkAlarmSeverity.KEEPER );
    TsValobjUtils.registerKeeperIfNone( EAvCompareOp.KEEPER_ID, EAvCompareOp.KEEPER );
    // if need register rri service
    if( !coreApi.services().hasKey( ISkRegRefInfoService.SERVICE_ID ) ) {
      SkfRriUtils.initialize();
      threadExecutor.syncExec( () -> coreApi.addService( SkRegRefInfoService.CREATOR ) );
    }
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
    if( !types.hasKey( AlertCheckerRriTypeGtZero.TYPE_ID ) ) {
      tm.registerType( new AlertCheckerRriTypeGtZero() );
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
