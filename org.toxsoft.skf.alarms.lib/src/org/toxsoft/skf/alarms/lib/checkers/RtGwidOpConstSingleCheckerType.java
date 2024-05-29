package org.toxsoft.skf.alarms.lib.checkers;

import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.core.tslib.math.cond.checker.*;
import org.toxsoft.uskat.core.*;

/**
 * {@link AbstractTsSingleCheckerType} extent for .
 *
 * @author dima
 */
public class RtGwidOpConstSingleCheckerType
    extends AbstractTsSingleCheckerType<ISkCoreApi> {
  // implements ITsSingleCheckerType {

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
  protected <ISkCoreApi> AbstractTsSingleChecker<ISkCoreApi> doCreateChecker( ISkCoreApi aEnviron,
      IOptionSet aParams ) {
    RtGwidOpConstChecker checker = new RtGwidOpConstChecker( aEnviron, aParams );
    return (AbstractTsSingleChecker<ISkCoreApi>)checker;
  }

}
