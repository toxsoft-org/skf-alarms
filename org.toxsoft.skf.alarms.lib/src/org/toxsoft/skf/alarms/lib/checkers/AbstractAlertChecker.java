package org.toxsoft.skf.alarms.lib.checkers;

import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.av.opset.impl.*;
import org.toxsoft.core.tslib.av.utils.*;
import org.toxsoft.core.tslib.math.cond.checker.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.uskat.core.*;

/**
 * Base implementation of {@link ITsChecker} for alert conditions.
 *
 * @author hazard157
 */
public abstract class AbstractAlertChecker
    extends AbstractTsSingleChecker<ISkCoreApi>
    implements IParameterized {

  private final IOptionSet params;

  /**
   * Constructor.
   *
   * @param aEnviron {@link ISkCoreApi} - Core API as a checker environment
   * @param aParams {@link IOptionSet} - configuration params
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  public AbstractAlertChecker( ISkCoreApi aEnviron, IOptionSet aParams ) {
    super( aEnviron );
    params = new OptionSet( aParams );
  }

  // ------------------------------------------------------------------------------------
  // IParameterized
  //

  @Override
  final public IOptionSet params() {
    return params;
  }

  // ------------------------------------------------------------------------------------
  // API for subclasses
  //

  /**
   * Returns Core API.
   * <p>
   * Method simply returns {@link #env()}.
   *
   * @return {@link ISkCoreApi} - Core API
   */
  public ISkCoreApi coreApi() {
    return env();
  }

  // ------------------------------------------------------------------------------------
  // To override/implement
  //

  @Override
  public abstract boolean checkCondition();

  @Override
  public abstract void close();

}
