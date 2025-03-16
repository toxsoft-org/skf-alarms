package org.toxsoft.skf.alarms.skatlet;

import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.alarms.skatlet.ISkResources.*;

import org.toxsoft.core.log4j.*;
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
  private SkAlarmProcessor  alarmProcessor;
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
    alarmProcessor = new SkAlarmProcessor( coreApi, LoggerWrapper.getLogger( SkAlarmProcessor.class ) );
    return ValidationResult.SUCCESS;
  }

  @Override
  public void start() {
    super.start();
    threadExecutor.syncExec( () -> alarmProcessor.start() );
    logger().info( MSG_ALARM_PROCESSOR_IS_RUNNING, id() );
  }

  @Override
  public void doJob() {
    super.doJob();
    threadExecutor.syncExec( () -> alarmProcessor.doJob() );
  }

  @Override
  public boolean queryStop() {
    super.queryStop();
    threadExecutor.syncExec( () -> alarmProcessor.queryStop() );
    SkThreadExecutorService.getExecutor( connection.coreApi() ).syncExec( () -> connection.close() );
    return alarmProcessor.isStopped();
  }

  @Override
  public boolean isStopped() {
    return alarmProcessor.isStopped();
  }

  @Override
  public void destroy() {
    super.destroy();
    threadExecutor.syncExec( () -> alarmProcessor.destroy() );
  }
}
