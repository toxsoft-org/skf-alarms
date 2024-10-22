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
import org.toxsoft.core.tslib.bricks.strid.coll.impl.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.gw.ugwi.*;
import org.toxsoft.core.tslib.math.cond.*;
import org.toxsoft.core.tslib.math.cond.checker.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.skf.rri.lib.*;
import org.toxsoft.skf.rri.lib.ugwi.*;
import org.toxsoft.uskat.core.*;

/**
 * Alert checker type: compares specified RRI attribute value is true.
 * <p>
 * {@link ITsSingleCondType} implementation of condition formula: <b><i>RRI value</i></b> {@link Ugwi} == true.
 *
 * @author dima
 */
public class AlertCheckerRriTypeGtZero
    extends AbstractTsSingleCheckerType<ISkCoreApi> {

  /**
   * The type ID.
   */
  public static final String TYPE_ID = USKAT_FULL_ID + ".alert_checker.Rri"; //$NON-NLS-1$

  /**
   * {@link ITsSingleCondInfo#params()} option: The Ugwi of the RRI attribute.<br>
   * Type: {@link Ugwi}
   */
  public static final IDataDef OPDEF_RRI_UGWI = DataDef.create( "RriUgwi", VALOBJ, //$NON-NLS-1$
      TSID_NAME, STR_RTDVC_RRI_GWID, //
      TSID_DESCRIPTION, STR_RTDVC_RRI_GWID_D, //
      TSID_KEEPER_ID, Ugwi.KEEPER_ID, //
      TSLIB_VCC_EDITOR_FACTORY_NAME, SKCGC_VALED_AV_VALOBJ_UGWI_SELECTOR_TEXT_AND_BUTTON, //
      SKCGC_VALED_UGWI_SELECTOR_OPID_SINGLE_UGWI_KIND_ID, avStr( UgwiKindRriAttr.KIND_ID ), //
      TSID_DEFAULT_VALUE, avValobj( UgwiKindRriAttr.makeUgwi( NONE_ID, Gwid.NONE_CONCR_ATTR ) ) );

  /**
   * Implementation of {@link AbstractTsSingleChecker} created in by this type.
   *
   * @author dima
   */
  static class Checker
      extends AbstractAlertChecker {

    private final Gwid          rriGwid;
    private final ISkRriSection rriSection;
    private boolean             init = false;

    public Checker( ISkCoreApi aEnviron, IOptionSet aParams ) {
      super( aEnviron, aParams );
      Ugwi ugwi = params().getValobj( AlertCheckerRriTypeGtZero.OPDEF_RRI_UGWI );
      String sectionId = UgwiKindRriAttr.getSectionId( ugwi );
      String classId = UgwiKindRriAttr.getClassId( ugwi );
      String objId = UgwiKindRriAttr.getObjStrid( ugwi );
      String attrId = UgwiKindRriAttr.getAttrId( ugwi );

      // переделываем на правильную
      rriGwid = Gwid.createAttr( classId, objId, attrId );
      ISkRegRefInfoService rriService =
          (ISkRegRefInfoService)coreApi().services().getByKey( ISkRegRefInfoService.SERVICE_ID );
      rriSection = rriService.listSections().getByKey( sectionId );
      try {
        IAtomicValue val = rriSection.getAttrParamValue( rriGwid.skid(), rriGwid.propId() );
        switch( val.atomicType() ) {
          case NONE, STRING, TIMESTAMP, VALOBJ -> throw new TsIllegalArgumentRtException( FMT_ERR_INVALID_RRI_ATTR_TYPE,
              val.atomicType().name() );
          case BOOLEAN -> {
            // nop
          }
          case FLOATING -> {
            // nop
          }
          case INTEGER -> {
            // nop
          }
          default -> {
            // nop
          }
        }

        init = true;
      }
      catch( @SuppressWarnings( "unused" ) Exception ex ) {
        LoggerUtils.errorLogger().warning( FMT_WARN_CANT_FIND_RRI, rriGwid.canonicalString() );
      }
    }

    protected IAtomicValue doGetXxxValue() {
      IAtomicValue retVal = IAtomicValue.NULL;
      if( init ) {
        retVal = rriSection.getAttrParamValue( rriGwid.skid(), rriGwid.propId() );
      }
      return retVal;
    }

    // ------------------------------------------------------------------------------------
    // AbstractAlertChecker
    //

    @Override
    public boolean checkCondition() {
      boolean retVal = false;
      IAtomicValue val = doGetXxxValue();
      retVal = switch( val.atomicType() ) {
        case BOOLEAN -> val.asBool();
        case FLOATING -> val.asFloat() > 0;
        case INTEGER -> val.asInt() > 0;
        case NONE, STRING, TIMESTAMP, VALOBJ -> throw new TsIllegalArgumentRtException( FMT_ERR_INVALID_RRI_ATTR_TYPE,
            val.atomicType().name() );
      };
      return retVal;
    }

    @Override
    public void close() {
      // nop
    }

  }

  /**
   * Constructor.
   */
  public AlertCheckerRriTypeGtZero() {
    super( TYPE_ID, //
        OptionSetUtils.createOpSet( //
            TSID_NAME, STR_RRI_GT_ZERO, //
            TSID_DESCRIPTION, STR_RRI_GT_ZERO_D //
        ), //
        new StridablesList<>( //
            OPDEF_RRI_UGWI //
        ) );
  }

  @Override
  protected AbstractTsSingleChecker<ISkCoreApi> doCreateChecker( ISkCoreApi aEnviron, IOptionSet aParams ) {
    return new Checker( aEnviron, aParams );
  }

}
