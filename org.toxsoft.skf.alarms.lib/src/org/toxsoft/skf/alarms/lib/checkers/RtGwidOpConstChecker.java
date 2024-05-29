package org.toxsoft.skf.alarms.lib.checkers;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.math.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.math.cond.checker.*;
import org.toxsoft.uskat.core.*;
import org.toxsoft.uskat.core.api.rtdserv.*;

/**
 * @author dima
 */
public class RtGwidOpConstChecker
    extends AbstractTsSingleChecker<ISkCoreApi> {

  private final Gwid         rtGwid;
  private final EAvCompareOp operation;
  private final IAtomicValue constVal;

  /**
   * Constructor
   *
   * @param aEnviron - app enviroment {@link ITsGuiContext}
   * @param aParams - confitions params {@link IOptionSet}
   */
  public RtGwidOpConstChecker( Object aEnviron, IOptionSet aParams ) {
    super( (ISkCoreApi)aEnviron );
    rtGwid = aParams.getValobj( RtGwidOpConstSingleCondType.OPDEF_RTGWID );
    operation = aParams.getValobj( RtGwidOpConstSingleCondType.OPDEF_OPERATION );
    constVal = AvUtils.avInt( aParams.getInt( RtGwidOpConstSingleCondType.OPDEF_CONST ) );
  }

  private IAtomicValue readRtGwid() {
    // ISkConnection srcConn = env().get( ISkConnectionSupplier.class ).defConn();
    // ISkCoreApi coreApi = srcConn.coreApi();
    // ISkRtdataService dataServ = coreApi.getService( ISkRtdataService.SERVICE_ID );
    ISkRtdataService dataServ = env().getService( ISkRtdataService.SERVICE_ID );
    if( dataServ.findReadCurrDataChannel( rtGwid ) == null ) {
      // создаем канал текущих данных
      dataServ.createReadCurrDataChannels( new GwidList( rtGwid ) );
    }
    ISkReadCurrDataChannel rtdCahannel = dataServ.findReadCurrDataChannel( rtGwid );

    return rtdCahannel.getValue();
  }

  @Override
  public boolean checkCondition() {
    // get current value of rtGwid
    IAtomicValue rtData = readRtGwid();
    IAvComparator c = AvComparatorStrict.INSTANCE;
    return c.avCompare( rtData, operation, constVal );
  }

}
