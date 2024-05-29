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
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.core.tslib.math.cond.*;
import org.toxsoft.core.tslib.math.cond.checker.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.uskat.core.*;
import org.toxsoft.uskat.core.api.rtdserv.*;

/**
 * Alert checker type: compares specified RTdata current value to the specified constant.
 * <p>
 * {@link ITsSingleCondType} implementation of condition formula: <i><b> RtGwid </i></b> {@link Gwid} <b><i>OP</i></b>
 * {@link EAvCompareOp} <b><i>Const</i></b> {@link IAtomicValue}.
 *
 * @author dima
 */
public class AlertCheckerRtdataVsConstType
    extends AbstractTsSingleCheckerType<ISkCoreApi> {

  /**
   * The type ID.
   */
  public static final String TYPE_ID = USKAT_FULL_ID + ".alert_checker.RtdataVsConst"; //$NON-NLS-1$

  /**
   * {@link ITsSingleCondInfo#params()} option: The GWID of the RTdata.<br>
   * Type: {@link Gwid}
   */
  public static final IDataDef OPDEF_RTDATA_GWID = DataDef.create( "RtdataGwid", VALOBJ, //$NON-NLS-1$
      TSID_NAME, STR_RTDVC_RTDATA_GWID, //
      TSID_DESCRIPTION, STR_RTDVC_RTDATA_GWID_D, //
      TSID_KEEPER_ID, Gwid.KEEPER_ID, //
      TSID_DEFAULT_VALUE, Gwid.createRtdata( Skid.NONE.classId(), Skid.NONE.strid(), NONE_ID ), //
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
      TSID_DEFAULT_VALUE, EAvCompareOp.EQ, //
      TSID_IS_MANDATORY, AV_TRUE //
  );

  /**
   * {@link ITsSingleCondInfo#params()} option: the constant to compare current RTdata value with it.<br>
   * Type: {@link IAtomicValue}.
   */
  public static final IDataDef OPDEF_CONST_VALUE = DataDef.create( "ConstValue", INTEGER, //$NON-NLS-1$
      TSID_NAME, STR_RTDVC_CONST_VALUE, //
      TSID_DESCRIPTION, STR_RTDVC_CONST_VALUE_D, //
      TSID_KEEPER_ID, EAvCompareOp.KEEPER_ID, //
      TSID_DEFAULT_VALUE, avInt( 0 ), //
      TSID_IS_MANDATORY, AV_TRUE //
  );

  /**
   * Implementation of {@link AbstractTsSingleChecker} created in by this type.
   *
   * @author hazard157
   */
  static class Checker
      extends AbstractAlertChecker {

    private final ISkReadCurrDataChannel channel;
    private final EAvCompareOp           op;
    private final IAtomicValue           constVal;

    public Checker( ISkCoreApi aEnviron, IOptionSet aParams ) {
      super( aEnviron, aParams );
      Gwid rtdGwid = params().getValobj( AlertCheckerRtdataVsConstType.OPDEF_RTDATA_GWID );
      op = params().getValobj( AlertCheckerRtdataVsConstType.OPDEF_COMPARE_OP );
      constVal = AvUtils.avInt( params().getInt( AlertCheckerRtdataVsConstType.OPDEF_CONST_VALUE ) );
      IMap<Gwid, ISkReadCurrDataChannel> chMap =
          coreApi().rtdService().createReadCurrDataChannels( new GwidList( rtdGwid ) );
      channel = chMap.values().first(); // open channel or null
      if( channel == null ) {
        LoggerUtils.errorLogger().warning( FMT_WARN_CANT_OPEN_READ_RTD_CHANNEL, rtdGwid.canonicalString() );
      }
    }

    @Override
    public boolean checkCondition() {
      if( channel != null ) {
        IAtomicValue rtdVal = channel.getValue();
        return AvComparatorStrict.INSTANCE.avCompare( rtdVal, op, constVal );
      }
      return false;
    }

    @Override
    public void close() {
      if( channel != null ) {
        channel.close();
      }
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

  // Note: this method is not needed because all options are declared as a mandatory options
  // @Override
  // protected ValidationResult doValidateParams( IOptionSet aCondParams ) {}

  @Override
  protected String doHumanReadableDescription( IOptionSet aCondParams ) {
    Gwid rtdGwid = aCondParams.getValobj( AlertCheckerRtdataVsConstType.OPDEF_RTDATA_GWID );
    EAvCompareOp op = aCondParams.getValobj( AlertCheckerRtdataVsConstType.OPDEF_COMPARE_OP );
    IAtomicValue constVal = AvUtils.avInt( aCondParams.getInt( AlertCheckerRtdataVsConstType.OPDEF_CONST_VALUE ) );
    return rtdGwid.canonicalString() + ' ' + op.nmName() + ' ' + constVal.asString();
  }

  @Override
  protected AbstractTsSingleChecker<ISkCoreApi> doCreateChecker( ISkCoreApi aEnviron, IOptionSet aParams ) {
    return new Checker( aEnviron, aParams );
  }

}
