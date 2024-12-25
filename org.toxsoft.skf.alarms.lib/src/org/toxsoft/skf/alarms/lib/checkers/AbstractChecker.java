/**
 *
 */
package org.toxsoft.skf.alarms.lib.checkers;

import static org.toxsoft.skf.alarms.lib.checkers.ISkResources.*;

import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.math.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.uskat.core.*;
import org.toxsoft.uskat.core.api.rtdserv.*;

/**
 * {@link AbstractAlertChecker} basic implementation for {@link AlertCheckerRtdataVsXxxBaseType} subclasses.
 *
 * @author dima
 * @author hazard157
 */
public abstract class AbstractChecker
    extends AbstractAlertChecker {

  private final ISkReadCurrDataChannel channel;
  private final EAvCompareOp           op;

  /**
   * Constructor.
   *
   * @param aEnviron {@link ISkCoreApi} - Core API as a checker environment
   * @param aParams {@link IOptionSet} - configuration params
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  public AbstractChecker( ISkCoreApi aEnviron, IOptionSet aParams ) {
    super( aEnviron, aParams );
    Gwid rtdGwid = params().getValobj( AlertCheckerRtdataVsXxxBaseType.OPDEF_RTDATA_GWID );
    op = params().getValobj( AlertCheckerRtdataVsXxxBaseType.OPDEF_COMPARE_OP );
    IMap<Gwid, ISkReadCurrDataChannel> chMap =
        coreApi().rtdService().createReadCurrDataChannels( new GwidList( rtdGwid ) );
    channel = chMap.values().first(); // open channel or null
    if( channel == null ) {
      LoggerUtils.errorLogger().warning( FMT_WARN_CANT_OPEN_READ_RTD_CHANNEL, rtdGwid.canonicalString() );
    }
  }

  // ------------------------------------------------------------------------------------
  // AbstractAlertChecker
  //

  @Override
  public boolean checkCondition() {
    if( channel != null ) {
      IAtomicValue rtdVal = channel.getValue();
      IAtomicValue val = doGetXxxValue();
      // 2024-10-22 mvk TODO:
      if( !rtdVal.isAssigned() || !val.isAssigned() ) {
        return false;
      }
      return AvComparatorStrict.INSTANCE.avCompare( rtdVal, op, val );
    }
    return false;
  }

  @Override
  public void close() {
    if( channel != null ) {
      channel.close();
    }
  }

  // ------------------------------------------------------------------------------------
  // To implement/override
  //

  protected abstract IAtomicValue doGetXxxValue();

}
