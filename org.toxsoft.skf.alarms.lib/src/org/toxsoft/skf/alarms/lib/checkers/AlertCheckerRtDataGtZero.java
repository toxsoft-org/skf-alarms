package org.toxsoft.skf.alarms.lib.checkers;

import static org.toxsoft.core.tslib.av.EAtomicType.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.core.tslib.utils.gui.ITsLibInnerSharedConstants.*;
import static org.toxsoft.skf.alarms.lib.checkers.ISkResources.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;
import static org.toxsoft.uskat.core.inner.ISkCoreGuiInnerSharedConstants.*;

import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.metainfo.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.strid.*;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.gw.ugwi.*;
import org.toxsoft.core.tslib.math.cond.*;
import org.toxsoft.core.tslib.math.cond.checker.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.uskat.core.*;
import org.toxsoft.uskat.core.api.rtdserv.*;

/**
 * Alert checker type: check specified RtData greater then zero (or true 4 boolean).
 * <p>
 * {@link ITsSingleCondType} implementation of condition formula: <b><i>RtData value</i></b> {@link Ugwi} == true.
 *
 * @author dima
 */
public class AlertCheckerRtDataGtZero
    extends AbstractTsSingleCheckerType<ISkCoreApi> {

  /**
   * The type ID.
   */
  public static final String TYPE_ID = USKAT_FULL_ID + ".alert_checker.RtDataGtZero"; //$NON-NLS-1$

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
   * Implementation of {@link AbstractTsSingleChecker} created in by this type.
   *
   * @author dima
   */
  static class Checker
      extends AbstractAlertChecker {

    private final ISkReadCurrDataChannel channel;
    private boolean                      init = false;

    public Checker( ISkCoreApi aEnviron, IOptionSet aParams ) {
      super( aEnviron, aParams );
      Gwid rtdGwid = params().getValobj( AlertCheckerRtdataVsXxxBaseType.OPDEF_RTDATA_GWID );
      IMap<Gwid, ISkReadCurrDataChannel> chMap =
          coreApi().rtdService().createReadCurrDataChannels( new GwidList( rtdGwid ) );
      channel = chMap.values().first(); // open channel or null
      if( channel == null ) {
        LoggerUtils.errorLogger().warning( FMT_WARN_CANT_OPEN_READ_RTD_CHANNEL, rtdGwid.canonicalString() );
      }
      init = true;
    }

    // ------------------------------------------------------------------------------------
    // AbstractAlertChecker
    //

    @Override
    public boolean checkCondition() {
      boolean retVal = false;
      IAtomicValue rtdVal = IAtomicValue.NULL;
      if( init ) {
        rtdVal = channel.getValue();
      }
      retVal = switch( rtdVal.atomicType() ) {
        case BOOLEAN -> rtdVal.asBool();
        case FLOATING -> rtdVal.asFloat() > 0;
        case INTEGER -> rtdVal.asInt() > 0;
        case NONE -> false; // none mean unknown value
        case STRING, TIMESTAMP, VALOBJ -> throw new TsIllegalArgumentRtException( FMT_ERR_INVALID_RRI_ATTR_TYPE,
            rtdVal.atomicType().name() );
      };
      return retVal;
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
  public AlertCheckerRtDataGtZero() {
    super( TYPE_ID, //
        OptionSetUtils.createOpSet( //
            TSID_NAME, STR_RTDATA_GT_ZERO, //
            TSID_DESCRIPTION, STR_RTDATA_GT_ZERO_D //
        ), //
        new StridablesList<>( //
            OPDEF_RTDATA_GWID //
        ) );
  }

  @Override
  protected AbstractTsSingleChecker<ISkCoreApi> doCreateChecker( ISkCoreApi aEnviron, IOptionSet aParams ) {
    return new Checker( aEnviron, aParams );
  }

}
