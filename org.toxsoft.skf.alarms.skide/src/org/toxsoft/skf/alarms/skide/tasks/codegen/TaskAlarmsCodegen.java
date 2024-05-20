package org.toxsoft.skf.alarms.skide.tasks.codegen;

import static org.toxsoft.core.tslib.bricks.gentask.IGenericTaskConstants.*;
import static org.toxsoft.skf.alarms.skide.ISkidePluginAlarmsSharedResources.*;
import static org.toxsoft.skf.alarms.skide.tasks.codegen.IPackageConstants.*;
import static org.toxsoft.skide.task.codegen.gen.ICodegenConstants.*;
import static org.toxsoft.skide.task.codegen.gen.impl.CodegenUtils.*;

import org.toxsoft.core.tslib.bricks.ctx.*;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.*;
import org.toxsoft.core.tslib.bricks.validator.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.alarms.lib.*;
import org.toxsoft.skf.alarms.skide.main.*;
import org.toxsoft.skide.core.api.*;
import org.toxsoft.skide.core.api.tasks.*;
import org.toxsoft.skide.task.codegen.gen.*;
import org.toxsoft.skide.task.codegen.main.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;

/**
 * SkIDE task {@link CodegenTaskProcessor} runner for {@link SkideUnitAlarms}.
 *
 * @author hazard157
 */
public class TaskAlarmsCodegen
    extends AbstractSkideUnitTaskSync {

  private static final String PREFIX_ALARM = "ALARMID"; //$NON-NLS-1$

  /**
   * Constructor.
   *
   * @param aOwnerUnit {@link AbstractSkideUnit} - the owner unit
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  public TaskAlarmsCodegen( AbstractSkideUnit aOwnerUnit ) {
    super( aOwnerUnit, CodegenTaskProcessor.INSTANCE.taskInfo(),
        new StridablesList<>( OPDEF_GW_ALARMS_INTERFACE_NAME ) );
  }

  // ------------------------------------------------------------------------------------
  // implementation
  //

  private static void writeConstants( ISkConnection aConn, IJavaConstantsInterfaceWriter aJw ) {
    ISkAlarmService alarmsService = aConn.coreApi().getService( ISkAlarmService.SERVICE_ID );
    for( ISkAlarm alarmCfg : alarmsService.listAlarms() ) {
      String cn = makeJavaConstName( PREFIX_ALARM, alarmCfg.id() );
      aJw.addConstString( cn, alarmCfg.id(), alarmCfg.nmName() );
    }
  }

  // ------------------------------------------------------------------------------------
  // AbstractSkideUnitTaskSync
  //

  @Override
  protected void doRunSync( ITsContextRo aInput, ITsContext aOutput ) {
    ILongOpProgressCallback lop = REFDEF_IN_PROGRESS_MONITOR.getRef( aInput );
    ICodegenEnvironment codegenEnv = REFDEF_CODEGEN_ENV.getRef( aInput );
    String interfaceName = OPDEF_GW_ALARMS_INTERFACE_NAME.getValue( getCfgOptionValues() ).asString();
    IJavaConstantsInterfaceWriter jw = codegenEnv.createJavaInterfaceWriter( interfaceName );
    ISkConnectionSupplier cs = tsContext().get( ISkConnectionSupplier.class );
    writeConstants( cs.defConn(), jw );
    jw.writeFile();
    lop.finished( ValidationResult.info( FMT_INFO_JAVA_INTERFACE_WAS_GENERATED, interfaceName ) );
  }

}
