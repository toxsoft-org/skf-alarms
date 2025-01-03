package org.toxsoft.skf.alarms.skatlet;

import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.alarms.skatlet.ISkResources.*;

import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.ctx.*;
import org.toxsoft.core.tslib.bricks.ctx.impl.*;
import org.toxsoft.core.tslib.bricks.threadexec.*;
import org.toxsoft.core.tslib.bricks.validator.*;
import org.toxsoft.skf.alarms.lib.impl.*;
import org.toxsoft.uskat.core.*;
import org.toxsoft.uskat.core.connection.*;
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
  private ISkConnection     connection;

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
  protected ValidationResult doSetContext( ITsContextRo aEnviron ) {
    super.doSetContext( aEnviron );
    connection = createConnection( getClass().getSimpleName(), new TsContext() );
    ISkCoreApi coreApi = connection.coreApi();
    threadExecutor = SkThreadExecutorService.getExecutor( coreApi );
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
    SkThreadExecutorService.getExecutor( connection.coreApi() ).syncExec( () -> connection.close() );
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
