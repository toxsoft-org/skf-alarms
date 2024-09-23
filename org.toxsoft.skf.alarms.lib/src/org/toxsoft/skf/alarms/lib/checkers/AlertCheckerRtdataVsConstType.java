package org.toxsoft.skf.alarms.lib.checkers;

import static org.toxsoft.core.tslib.av.EAtomicType.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.alarms.lib.checkers.ISkResources.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;

import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.math.*;
import org.toxsoft.core.tslib.av.metainfo.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.math.cond.*;
import org.toxsoft.core.tslib.math.cond.checker.*;
import org.toxsoft.uskat.core.*;

/**
 * Alert checker type: compares specified RTdata current value to the specified constant.
 * <p>
 * {@link ITsSingleCondType} implementation of condition formula: <i><b> RtGwid </i></b> {@link Gwid} <b><i>OP</i></b>
 * {@link EAvCompareOp} <b><i>Const</i></b> {@link IAtomicValue}.
 *
 * @author dima
 */
public class AlertCheckerRtdataVsConstType
    extends AlertCheckerRtdataVsXxxBaseType {

  /**
   * The type ID.
   */
  public static final String TYPE_ID = USKAT_FULL_ID + ".alert_checker.RtdataVsConst"; //$NON-NLS-1$

  /**
   * {@link ITsSingleCondInfo#params()} option: the constant to compare current RTdata value with it.<br>
   * Type: {@link IAtomicValue}.
   */
  public static final IDataDef OPDEF_CONST_VALUE = DataDef.create( "ConstValue", NONE, //$NON-NLS-1$
      TSID_NAME, STR_RTDVC_CONST_VALUE, //
      TSID_DESCRIPTION, STR_RTDVC_CONST_VALUE_D, //
      TSID_DEFAULT_VALUE, IAtomicValue.NULL, //
      TSID_IS_MANDATORY, AV_TRUE //
  );

  /**
   * Implementation of {@link AbstractTsSingleChecker} created in by this type.
   *
   * @author dima
   */
  static class Checker
      extends AbstractChecker {

    private final IAtomicValue constVal;

    public Checker( ISkCoreApi aEnviron, IOptionSet aParams ) {
      super( aEnviron, aParams );
      constVal = params().getValue( AlertCheckerRtdataVsConstType.OPDEF_CONST_VALUE );
    }

    @Override
    protected IAtomicValue doGetXxxValue() {
      return constVal;
    }

  }

  /**
   * Constructor.
   */
  public AlertCheckerRtdataVsConstType() {
    super( TYPE_ID, //
        OptionSetUtils.createOpSet( //
            TSID_NAME, STR_RTDVC, //
            TSID_DESCRIPTION, STR_RTDVC_D //
        ), //
        new StridablesList<>( //
            OPDEF_RTDATA_GWID, //
            OPDEF_COMPARE_OP, //
            OPDEF_CONST_VALUE //
        ) );
  }

  // ------------------------------------------------------------------------------------
  // AbstractTsSingleCheckerType
  //

  @Override
  protected AbstractTsSingleChecker<ISkCoreApi> doCreateChecker( ISkCoreApi aEnviron, IOptionSet aParams ) {
    return new Checker( aEnviron, aParams );
  }

  @Override
  protected IAtomicValue getXxxValue( IOptionSet aCondParams ) {
    return aCondParams.getValue( AlertCheckerRtdataVsConstType.OPDEF_CONST_VALUE );
  }

}
