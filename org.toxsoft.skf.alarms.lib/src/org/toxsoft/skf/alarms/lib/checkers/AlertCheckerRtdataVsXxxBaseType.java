package org.toxsoft.skf.alarms.lib.checkers;

import static org.toxsoft.core.tslib.av.EAtomicType.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.core.tslib.utils.gui.ITsLibInnerSharedConstants.*;
import static org.toxsoft.skf.alarms.lib.checkers.ISkResources.*;
import static org.toxsoft.uskat.core.inner.ISkCoreGuiInnerSharedConstants.*;

import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.math.*;
import org.toxsoft.core.tslib.av.metainfo.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.bricks.strid.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.math.cond.*;
import org.toxsoft.core.tslib.math.cond.checker.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.uskat.core.*;

/**
 * Base type for alert checker type: compares specified RTdata current value to the specified data value.
 * <p>
 * {@link ITsSingleCondType} implementation of condition formula: <i><b> RtGwid </i></b> {@link Gwid} <b><i>OP</i></b>
 * {@link EAvCompareOp} <b><i>Xxx</i></b> {@link IAtomicValue}.
 *
 * @author dima
 */
abstract public class AlertCheckerRtdataVsXxxBaseType
    extends AbstractTsSingleCheckerType<ISkCoreApi> {

  /**
   * {@link ITsSingleCondInfo#params()} option: The GWID of the RTdata.<br>
   * Type: {@link Gwid}
   */
  public static final IDataDef OPDEF_RTDATA_GWID = DataDef.create( "RtdataGwid", VALOBJ, //$NON-NLS-1$
      TSID_NAME, STR_RTDVC_RTDATA_GWID, //
      TSID_DESCRIPTION, STR_RTDVC_RTDATA_GWID_D, //
      TSID_KEEPER_ID, Gwid.KEEPER_ID, //
      // dima 03.01.26 patch build error
      // TSLIB_VCC_EDITOR_FACTORY_NAME, SKCGC_VALED_AV_CONCRETE_GWID_EDITOR_NAME, //
      TSLIB_VCC_EDITOR_FACTORY_NAME, SKCGC_VALED_AV_VALOBJ_CONCRETE_GWID_EDITOR_NAME, //
      SKCGC_VALED_CONCRETE_GWID_EDITOR_NAME_OPID_GWID_KIND, avValobj( EGwidKind.GW_RTDATA ), //
      TSID_DEFAULT_VALUE, avValobj( Gwid.createRtdata( IStridable.NONE_ID, IStridable.NONE_ID, IStridable.NONE_ID ) ), //
      TSID_IS_MANDATORY, AV_TRUE //
  );

  /**
   * {@link ITsSingleCondInfo#params()} option: The comparison operation.<br>
   * Type: {@link EAvCompareOp}
   */
  public static final IDataDef OPDEF_COMPARE_OP = DataDef.create( "CompareOp", VALOBJ, //$NON-NLS-1$
      TSID_NAME, STR_RTDVC_COMPARE_OP, //
      TSID_DESCRIPTION, STR_RTDVC_COMPARE_OP_D, //
      TSID_KEEPER_ID, EAvCompareOp.KEEPER_ID, //
      TSLIB_VCC_EDITOR_FACTORY_NAME, TSLIB_VALED_AV_VALOBJ_ENUM_COMBO, //
      TSID_DEFAULT_VALUE, avValobj( EAvCompareOp.GE ), //
      TSID_IS_MANDATORY, AV_TRUE //
  );

  /**
   * Constructor.
   *
   * @param aId String - identifier (IDname or IDpath)
   * @param aParams {@link IOptionSet} - {@link #params()} values
   * @param aParamDefs {@link IStridablesList}&lt;{@link IDataDef}&gt; - description of the condition parameters
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   * @throws TsIllegalArgumentRtException aId is not valid IDpath
   */
  public AlertCheckerRtdataVsXxxBaseType( String aId, IOptionSet aParams, IStridablesList<IDataDef> aParamDefs ) {
    super( aId, aParams, aParamDefs );
  }

  // ------------------------------------------------------------------------------------
  // AbstractTsSingleCheckerType
  //

  // Note: this method is not needed because all options are declared as a mandatory options
  // @Override
  // protected ValidationResult doValidateParams( IOptionSet aCondParams ) {}

  @Override
  protected String doHumanReadableDescription( IOptionSet aCondParams ) {
    Gwid rtdGwid = aCondParams.getValobj( AlertCheckerRtdataVsXxxBaseType.OPDEF_RTDATA_GWID );
    EAvCompareOp op = aCondParams.getValobj( AlertCheckerRtdataVsXxxBaseType.OPDEF_COMPARE_OP );
    IAtomicValue constVal = getXxxValue( aCondParams );
    return rtdGwid.canonicalString() + ' ' + op.nmName() + ' ' + constVal.asString();
  }

  protected abstract IAtomicValue getXxxValue( IOptionSet aCondParams );

  @Override
  protected abstract AbstractTsSingleChecker<ISkCoreApi> doCreateChecker( ISkCoreApi aEnviron, IOptionSet aParams );

}
