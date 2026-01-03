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
import org.toxsoft.core.tslib.av.math.*;
import org.toxsoft.core.tslib.av.metainfo.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.strid.*;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.math.cond.*;
import org.toxsoft.core.tslib.math.cond.checker.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.uskat.core.*;

/**
 * Alert checker type: compares specified RTdata current value to the specified constant.
 * <p>
 * {@link ITsSingleCondType} implementation of condition formula: <i><b> RtGwid </i></b> {@link Gwid} <b><i>OP</i></b>
 * {@link EAvCompareOp} <b><i>attribute value</i></b> {@link Gwid}.
 *
 * @author dima
 */
public class AlertCheckerRtdataVsAttrType
    extends AlertCheckerRtdataVsXxxBaseType {

  /**
   * The type ID.
   */
  public static final String TYPE_ID = USKAT_FULL_ID + ".alert_checker.RtdataVsAttr"; //$NON-NLS-1$

  /**
   * {@link ITsSingleCondInfo#params()} option: The GWID of the attribute.<br>
   * Type: {@link Gwid}
   */
  public static final IDataDef OPDEF_ATTR_GWID = DataDef.create( "AttrGwid", VALOBJ, //$NON-NLS-1$
      TSID_NAME, STR_RTDVC_ATTR_GWID, //
      TSID_DESCRIPTION, STR_RTDVC_ATTR_GWID_D, //
      TSID_KEEPER_ID, Gwid.KEEPER_ID, //
      // dima 03.01.26 patch build error
      // TSLIB_VCC_EDITOR_FACTORY_NAME, SKCGC_VALED_AV_CONCRETE_GWID_EDITOR_NAME, //
      TSLIB_VCC_EDITOR_FACTORY_NAME, SKCGC_VALED_AV_VALOBJ_CONCRETE_GWID_EDITOR_NAME, //
      SKCGC_VALED_CONCRETE_GWID_EDITOR_NAME_OPID_GWID_KIND, avValobj( EGwidKind.GW_ATTR ), //
      TSID_DEFAULT_VALUE, avValobj( Gwid.createAttr( IStridable.NONE_ID, IStridable.NONE_ID, IStridable.NONE_ID ) ), //
      TSID_IS_MANDATORY, AV_TRUE //
  );

  /**
   * Implementation of {@link AbstractTsSingleChecker} created in by this type.
   *
   * @author dima
   */
  static class Checker
      extends AbstractChecker {

    private final Gwid attrGwid;
    private boolean    init = false;

    public Checker( ISkCoreApi aEnviron, IOptionSet aParams ) {
      super( aEnviron, aParams );
      attrGwid = params().getValobj( AlertCheckerRtdataVsAttrType.OPDEF_ATTR_GWID );
      if( coreApi().objService().find( attrGwid.skid() ) != null
          && coreApi().objService().get( attrGwid.skid() ).attrs().findValue( attrGwid.propId() ) != null ) {
        init = true;
      }
      else {
        LoggerUtils.errorLogger().warning( FMT_WARN_CANT_FIND_ATTR, attrGwid.canonicalString() );
      }
    }

    @Override
    protected IAtomicValue doGetXxxValue() {
      IAtomicValue retVal = IAtomicValue.NULL;
      if( init ) {
        retVal = coreApi().objService().get( attrGwid.skid() ).attrs().getValue( attrGwid.propId() );
      }
      return retVal;
    }

  }

  /**
   * Constructor.
   */
  public AlertCheckerRtdataVsAttrType() {
    super( TYPE_ID, //
        OptionSetUtils.createOpSet( //
            TSID_NAME, STR_RTD_VC_ATTR, //
            TSID_DESCRIPTION, STR_RTD_VC_ATTR_D //
        ), //
        new StridablesList<>( //
            OPDEF_RTDATA_GWID, //
            OPDEF_COMPARE_OP, //
            OPDEF_ATTR_GWID //
        ) );
  }

  // ------------------------------------------------------------------------------------
  // AlertCheckerRtdataVsXxxBaseType
  //

  @Override
  protected AbstractTsSingleChecker<ISkCoreApi> doCreateChecker( ISkCoreApi aEnviron, IOptionSet aParams ) {
    return new Checker( aEnviron, aParams );
  }

  @Override
  protected IAtomicValue getXxxValue( IOptionSet aCondParams ) {
    Gwid attrGwid = aCondParams.getValobj( AlertCheckerRtdataVsAttrType.OPDEF_ATTR_GWID );
    return AvUtils.avStr( attrGwid.canonicalString() );
  }

}
