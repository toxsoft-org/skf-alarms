package org.toxsoft.skf.alarms.gui.km5;

import static org.toxsoft.core.tslib.bricks.strio.IStrioHardConstants.*;

import org.toxsoft.core.tslib.bricks.keeper.*;
import org.toxsoft.core.tslib.bricks.keeper.AbstractEntityKeeper.*;
import org.toxsoft.core.tslib.bricks.strio.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.gw.ugwi.*;

/**
 * {@link IUsedUgwi4MessageInfo} immutable implementation.
 *
 * @author dima
 */
public final class UsedUgwi4MessageInfo
    implements IUsedUgwi4MessageInfo {

  /**
   * Value-object keeper identifier.
   */
  public static final String KEEPER_ID = "UsedUgwi4MessageInfo"; //$NON-NLS-1$

  /**
   * Keeper singleton.
   */
  public final static IEntityKeeper<IUsedUgwi4MessageInfo> KEEPER =
      new AbstractEntityKeeper<>( IUsedUgwi4MessageInfo.class, EEncloseMode.NOT_IN_PARENTHESES, null ) {

        @Override
        protected void doWrite( IStrioWriter aSw, IUsedUgwi4MessageInfo aEntity ) {
          // idPath
          aSw.writeQuotedString( aEntity.idPath() );
          aSw.writeChar( CHAR_ITEM_SEPARATOR );
          // пишем Ugwi
          Ugwi.KEEPER.write( aSw, aEntity.usedUgwi() );
        }

        @Override
        protected IUsedUgwi4MessageInfo doRead( IStrioReader aSr ) {
          String idPath = aSr.readQuotedString();
          aSr.ensureChar( CHAR_ITEM_SEPARATOR );
          Ugwi ugwi = Ugwi.KEEPER.read( aSr );
          return new UsedUgwi4MessageInfo( idPath, ugwi );
        }
      };

  protected final String idPath;
  protected final Ugwi   usedUgwi;

  /**
   * Constructor.
   *
   * @param aIdPath - id path
   * @param aUsedUgwi - {@link Gwid} green world id of that parameter
   */
  public UsedUgwi4MessageInfo( String aIdPath, Ugwi aUsedUgwi ) {
    idPath = aIdPath;
    usedUgwi = aUsedUgwi;
  }

  // ------------------------------------------------------------------------------------
  // API
  //

  @Override
  public String idPath() {
    return idPath;
  }

  @Override
  public Ugwi usedUgwi() {
    return usedUgwi;
  }

}
