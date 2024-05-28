package org.toxsoft.skf.alarms.lib.filters;

import java.io.*;

import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.bricks.keeper.*;
import org.toxsoft.core.tslib.bricks.keeper.AbstractEntityKeeper.*;
import org.toxsoft.core.tslib.bricks.strid.impl.*;
import org.toxsoft.core.tslib.bricks.strio.*;
import org.toxsoft.core.tslib.math.cond.*;
import org.toxsoft.core.tslib.utils.errors.*;

/**
 * {@link ITsSingleCondInfo} immutable implementation.
 *
 * @author hazard157
 */
public final class RtGwidOpConstCondInfo
    implements ITsSingleCondInfo, Serializable {

  private static final long serialVersionUID = -1585428034740096538L;

  /**
   * The registered keeper ID.
   */
  public static final String KEEPER_ID = "TsSingleCondInfo"; //$NON-NLS-1$

  /**
   * Keeper singleton.
   */
  public static final IEntityKeeper<ITsSingleCondInfo> KEEPER =
      new AbstractEntityKeeper<>( ITsSingleCondInfo.class, EEncloseMode.ENCLOSES_BASE_CLASS, null ) {

        @Override
        protected void doWrite( IStrioWriter aSw, ITsSingleCondInfo aEntity ) {
          // typeId
          aSw.writeAsIs( aEntity.typeId() );
          aSw.writeSeparatorChar();
          // params
          OptionSetKeeper.KEEPER.write( aSw, aEntity.params() );
        }

        @Override
        protected ITsSingleCondInfo doRead( IStrioReader aSr ) {
          // typeId
          String typeId = aSr.readIdPath();
          aSr.ensureSeparatorChar();
          // params
          IOptionSet params = OptionSetKeeper.KEEPER.read( aSr );
          return new RtGwidOpConstCondInfo( typeId, params );
        }

      };

  private final String         typeId;
  private final IOptionSetEdit params = new OptionSet();

  /**
   * Constructor.
   *
   * @param aTypeId String - condition type ID (IDpath) must match corresponding {@link ITsSingleCondType#id()}
   * @param aParams {@link IOptionSet} - condition parameters
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   * @throws TsIllegalArgumentRtException type ID is not an IDpath
   */
  public RtGwidOpConstCondInfo( String aTypeId, IOptionSet aParams ) {
    typeId = StridUtils.checkValidIdPath( aTypeId );
    params.setAll( aParams );
  }

  /**
   * Static constructor.
   *
   * @param aTypeId String - condition type ID (IDpath) must match corresponding {@link ITsSingleCondType#id()}
   * @param aIdsAndValues Object[] - identifier / value pairs
   * @return {@link RtGwidOpConstCondInfo} - the instance
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   * @throws TsIllegalArgumentRtException type ID is not an IDpath
   * @throws TsIllegalArgumentRtException number of elements in array is uneven
   * @throws ClassCastException argument types convention is violated
   */
  public static RtGwidOpConstCondInfo create( String aTypeId, Object... aIdsAndValues ) {
    return new RtGwidOpConstCondInfo( aTypeId, OptionSetUtils.createOpSet( aIdsAndValues ) );
  }

  // ------------------------------------------------------------------------------------
  // ITsSingleCondInfo
  //

  @Override
  public String typeId() {
    return typeId;
  }

  @Override
  public IOptionSet params() {
    return params;
  }

  // ------------------------------------------------------------------------------------
  // Object
  //

  @Override
  public String toString() {
    String s = typeId;
    s += ":{"; //$NON-NLS-1$
    for( String vn : params.keys() ) {
      IAtomicValue av = params.getByKey( vn );
      s += vn;
      s += '=';
      s += av.toString();
      if( s != params.keys().last() ) {
        s += ", "; //$NON-NLS-1$
      }
    }
    s += '}';
    return s;
  }

}
