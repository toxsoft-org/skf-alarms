package org.toxsoft.skf.alarms.lib.checkers;

import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.core.tslib.bricks.validator.impl.*;
import org.toxsoft.core.tslib.math.cond.*;
import org.toxsoft.core.tslib.math.cond.checker.*;
import org.toxsoft.core.tslib.math.cond.impl.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.uskat.core.*;

/**
 * {@link AbstractTsSingleCheckerType} extent for .
 *
 * @author dima
 */
public class RtGwidOpConstSingleCheckerType
    extends TsSingleCondType
    implements ITsSingleCheckerType {

  /**
   * Constructor.
   */
  public RtGwidOpConstSingleCheckerType() {
    super( RtGwidOpConstSingleCondType.TYPE_ID, IOptionSet.NULL, IStridablesList.EMPTY );
    setDefs( RtGwidOpConstSingleCondType.OPDEF_RTGWID, //
        RtGwidOpConstSingleCondType.OPDEF_OPERATION, //
        RtGwidOpConstSingleCondType.OPDEF_CONST );
  }

  @Override
  public AbstractTsSingleChecker<ISkCoreApi> create( ISkCoreApi aEnviron, ITsSingleCondInfo aCombiCondInfo ) {
    TsNullArgumentRtException.checkNull( aCombiCondInfo );
    TsIllegalArgumentRtException.checkFalse( aCombiCondInfo.typeId().equals( id() ) );
    TsValidationFailedRtException.checkError( validateParams( aCombiCondInfo.params() ) );
    RtGwidOpConstChecker checker = new RtGwidOpConstChecker( aEnviron, aCombiCondInfo.params() );
    return checker;
  }

  // @Override
  // public <E> AbstractTsSingleChecker<E> create( E aEnviron, ITsSingleCondInfo aCombiCondInfo ) {
  // // TODO Auto-generated method stub
  // return null;
  // }

  // @Override
  // public <ITsGuiContext> AbstractTsSingleChecker<ITsGuiContext> create( ITsGuiContext aEnviron,
  // ITsSingleCondInfo aCombiCondInfo ) {
  //
  // return create1( aEnviron, aCombiCondInfo );
  // }

  // @Override
  // public <ITsGuiContext> AbstractTsSingleChecker<ITsGuiContext> create( ITsGuiContext aEnviron,
  // ITsSingleCondInfo aCombiCondInfo ) {
  // TsNullArgumentRtException.checkNull( aCombiCondInfo );
  // TsIllegalArgumentRtException.checkFalse( aCombiCondInfo.typeId().equals( id() ) );
  // TsValidationFailedRtException.checkError( validateParams( aCombiCondInfo.params() ) );
  // RtGwidOpConstChecker checker = new RtGwidOpConstChecker( aEnviron, aCombiCondInfo.params() );
  // return (AbstractTsSingleChecker<ITsGuiContext>)checker;
  // // return doCreateChecker( aEnviron, aCombiCondInfo.params() );
  // }

  // protected <ITsGuiContext> AbstractTsSingleChecker<ITsGuiContext> doCreateChecker( ITsGuiContext aEnviron,
  // IOptionSet aParams ) {
  // return new AbstractTsSingleChecker<>( aEnviron ) {
  //
  // RtGwidOpConstChecker checker = new RtGwidOpConstChecker( aEnviron, aParams );
  //
  // @Override
  // public boolean checkCondition() {
  // return checker.checkCondition();
  // }
  // };
  // }

}
