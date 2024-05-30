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
import org.toxsoft.core.tslib.math.cond.checker.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.uskat.core.*;
import org.toxsoft.uskat.core.api.rtdserv.*;

/**
 * Implementation of {@link AbstractTsSingleChecker} created in by this type.
 *
 * @author hazard157
 */
abstract class BaseChecker
    extends AbstractAlertChecker {

  private final ISkReadCurrDataChannel channel;
  private final EAvCompareOp           op;

  public BaseChecker( ISkCoreApi aEnviron, IOptionSet aParams ) {
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

  @Override
  public boolean checkCondition() {
    if( channel != null ) {
      IAtomicValue rtdVal = channel.getValue();
      IAtomicValue val = getXxxValue();
      return AvComparatorStrict.INSTANCE.avCompare( rtdVal, op, val );
    }
    return false;
  }

  protected abstract IAtomicValue getXxxValue();

  @Override
  public void close() {
    if( channel != null ) {
      channel.close();
    }
  }

}
