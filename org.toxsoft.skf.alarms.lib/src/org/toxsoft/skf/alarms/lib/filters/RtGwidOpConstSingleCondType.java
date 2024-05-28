package org.toxsoft.skf.alarms.lib.filters;

import static org.toxsoft.core.tslib.ITsHardConstants.*;
import static org.toxsoft.core.tslib.av.EAtomicType.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.alarms.lib.filters.ITsResources.*;

import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.math.*;
import org.toxsoft.core.tslib.av.metainfo.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.core.tslib.bricks.validator.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.math.cond.*;
import org.toxsoft.core.tslib.math.cond.impl.*;

/**
 * {@link ITsSingleCondType} implementation of condition for RtGwid {@link Gwid} OP {@link EAvCompareOp} Const
 * {@link IAtomicValue} filter.
 *
 * @author dima
 */
public class RtGwidOpConstSingleCondType
    extends TsSingleCondType {

  /**
   * id of type
   */
  public static String TYPE_ID = TS_FULL_ID + ".skf.alarms.lib.filters.RtGwidOpConstSingleCondType.cond.type.id";

  /**
   * The RtGwid {@link Gwid} in condition.
   */
  public static final IDataDef OPDEF_RTGWID =
      DataDef.create( TS_FULL_ID + ".skf.alarms.lib.filters.RtGwidOpConstSingleCondType.RtGwid", VALOBJ, //$NON-NLS-1$
          TSID_NAME, "RtGwid", //
          TSID_DESCRIPTION, "RtGwid in condition", //
          TSID_KEEPER_ID, Gwid.KEEPER_ID ); //

  /**
   * The OPeration {@link EAvCompareOp} in condition.
   */
  public static final IDataDef OPDEF_OPERATION =
      DataDef.create( TS_FULL_ID + ".skf.alarms.lib.filters.RtGwidOpConstSingleCondType.operation", VALOBJ, //$NON-NLS-1$
          TSID_NAME, "Operation", //
          TSID_DESCRIPTION, "Comparing operation", //
          TSID_KEEPER_ID, EAvCompareOp.KEEPER_ID, //
          TSID_DEFAULT_VALUE, EAvCompareOp.GE ); //

  /**
   * The Constant {@link IAtomicValue} in condition.
   */
  public static final IDataDef OPDEF_CONST =
      DataDef.create( TS_FULL_ID + ".skf.alarms.lib.filters.RtGwidOpConstSingleCondType.const", INTEGER, //$NON-NLS-1$
          TSID_NAME, "Const", //
          TSID_DESCRIPTION, "Constant to compare with", //
          TSID_KEEPER_ID, EAvCompareOp.KEEPER_ID, //
          TSID_DEFAULT_VALUE, avInt( 0 ) ); //

  /**
   * Constructor.
   */
  public RtGwidOpConstSingleCondType() {
    super( TYPE_ID );
    setDefs( OPDEF_RTGWID, OPDEF_OPERATION, OPDEF_CONST );
  }

  /**
   * Subclass may perform additional validation of the condition parameters.
   * <p>
   * In base class returns {@link ValidationResult#SUCCESS}. There is no need to call superclass method when overriding.
   *
   * @param aCondParams {@link IOptionSet} - values already checked with
   *          {@link OptionSetUtils#validateOptionSet(IOptionSet, IStridablesList)}
   * @return {@link ValidationResult} - validation result
   */
  @Override
  protected ValidationResult doValidateParams( IOptionSet aCondParams ) {
    // check params validation
    if( !aCondParams.hasValue( OPDEF_RTGWID ) ) {
      return ValidationResult.error( FMT_ERR_COND_HAS_NO_GWID );
    }
    if( !aCondParams.hasValue( OPDEF_OPERATION ) ) {
      return ValidationResult.error( FMT_ERR_COND_HAS_NO_OPERATION );
    }
    if( !aCondParams.hasValue( OPDEF_CONST ) ) {
      return ValidationResult.error( FMT_ERR_COND_HAS_NO_CONST );
    }
    return ValidationResult.SUCCESS;
  }

  /**
   * Subclass may override creation of the condition description.
   * <p>
   * Returned value depends on {@link #id()}:
   * <ul>
   * <li>for {@link ITsSingleCondInfo#TYPE_ID_ALWAYS TYPE_ID_ALWAYS} returns {@link #HUMAN_READABLE_DESCR_ALWAYS};</li>
   * <li>for {@link ITsSingleCondInfo#TYPE_ID_NEVER TYPE_ID_NEVER} returns {@link #HUMAN_READABLE_DESCR_NEVER};</li>
   * <li>for all other type IDs returns {@link IOptionSet#toString() ITsSingleCondInfo.params().toString()}.</li>
   * </ul>
   * There is no need to call superclass method when overriding.
   *
   * @param aCondParams {@link IOptionSet} - values already checked with
   *          {@link OptionSetUtils#validateOptionSet(IOptionSet, IStridablesList)}
   * @return String - human-readable description, never is <code>null</code>
   */
  @Override
  protected String doHumanReadableDescription( IOptionSet aCondParams ) {
    return switch( id() ) {
      case ITsSingleCondInfo.TYPE_ID_ALWAYS -> HUMAN_READABLE_DESCR_ALWAYS;
      case ITsSingleCondInfo.TYPE_ID_NEVER -> HUMAN_READABLE_DESCR_NEVER;
      default -> "RtGwid comparing operation Const";
    };
  }

}
