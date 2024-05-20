package org.toxsoft.skf.alarms.skide.tasks.codegen;

import static org.toxsoft.core.tslib.av.EAtomicType.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.alarms.skide.ISkidePluginAlarmsSharedResources.*;

import org.toxsoft.core.tsgui.valed.api.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.metainfo.*;
import org.toxsoft.skide.task.codegen.gen.*;
import org.toxsoft.skide.task.codegen.valed.*;

/**
 * Package-private constants.
 *
 * @author hazard157
 */
interface IPackageConstants {

  String DEFAULT_GW_ALARMS_INTERFACE_NAME = "IGreenWorldAlarms"; //$NON-NLS-1$

  IDataDef OPDEF_GW_ALARMS_INTERFACE_NAME = DataDef.create( "GwClaassesInterfaceName", STRING, //$NON-NLS-1$
      TSID_NAME, STR_OPDEF_GW_ALARMS_INTERFACE_NAME, //
      TSID_DESCRIPTION, STR_OPDEF_GW_ALARMS_INTERFACE_NAME_D, //
      TSID_IS_NULL_ALLOWED, AV_FALSE, //
      IValedControlConstants.OPDEF_EDITOR_FACTORY_NAME, ValedAvStringJavaTypeName.FACTORY_NAME, //
      ValedStringJavaTypeName.OPDEF_CODEGEN_JAVA_TYPE, avValobj( ECodegenJavaType.INTERFACE ), //
      TSID_DEFAULT_VALUE, DEFAULT_GW_ALARMS_INTERFACE_NAME //
  );

}
