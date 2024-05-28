package org.toxsoft.skf.alarms.lib.checkers;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.math.*;
import org.toxsoft.core.tslib.av.metainfo.*;
import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.bricks.filter.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.core.tslib.bricks.validator.*;
import org.toxsoft.core.tslib.bricks.validator.impl.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.math.cond.*;
import org.toxsoft.core.tslib.math.cond.checker.*;
import org.toxsoft.core.tslib.math.cond.filter.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.uskat.core.*;
import org.toxsoft.uskat.core.api.rtdserv.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;

/**
 * {@link ITsSingleFilterType} implementation for .
 *
 * @author dima
 */
public class RtGwidOpConstSingleChecker
    extends AbstractTsSingleCheckerType {

  /**
   * Constructor.
   */
  public RtGwidOpConstSingleChecker( ITsGuiContext aContext ) {
    super( aContext );
    setDefs( RtGwidOpConstSingleCondType.OPDEF_RTGWID, //
        RtGwidOpConstSingleCondType.OPDEF_OPERATION, //
        RtGwidOpConstSingleCondType.OPDEF_CONST );
  }

  // ------------------------------------------------------------------------------------
  // ITsSingleFilterType
  //

  @Override
  public ITsFilter<ITsGuiContext> create( ITsSingleCondInfo aCombi ) {
    TsNullArgumentRtException.checkNull( aCombi );
    TsIllegalArgumentRtException.checkFalse( aCombi.typeId().equals( id() ) );
    TsValidationFailedRtException.checkError( validateParams( aCombi.params() ) );
    return doCreateFilter( aCombi.params() );
  }

  // ------------------------------------------------------------------------------------
  // To override/implement
  //

  /**
   * Implementation must create the filter.
   * <p>
   * Argument is already checked by {@link #validateParams(IOptionSet)}.
   *
   * @param aParams {@link IOptionSet} - the filter options {@link ITsSingleCondInfo#params()}
   * @return {@link ITsFilter} - created filter
   */
  @Override
  protected ITsFilter<ITsGuiContext> doCreateFilter( IOptionSet aParams ) {
    FilterRtGwidOpConst retVal = new FilterRtGwidOpConst( aParams );
    return retVal;
  }

  static class FilterRtGwidOpConst
      implements ITsFilter<ITsGuiContext> {

    private final Gwid         rtGwid;
    private final EAvCompareOp operation;
    private final IAtomicValue constVal;

    /**
     * Construct filter by param {@link IOptionSet}
     *
     * @param aParams - filter params {@link IOptionSet}
     */
    public FilterRtGwidOpConst( IOptionSet aParams ) {
      rtGwid = aParams.getValobj( RtGwidOpConstSingleCondType.OPDEF_RTGWID );
      operation = aParams.getValobj( RtGwidOpConstSingleCondType.OPDEF_OPERATION );
      constVal = AvUtils.avInt( aParams.getInt( RtGwidOpConstSingleCondType.OPDEF_CONST ) );
    }

    @Override
    public boolean accept( ITsGuiContext aTsGuiContext ) {
      // get current value of rtGwid
      IAtomicValue rtData = readRtGwid( aTsGuiContext );
      IAvComparator c = AvComparatorStrict.INSTANCE;
      return c.avCompare( rtData, operation, constVal );
    }

    private IAtomicValue readRtGwid( ITsGuiContext aTsGuiContext ) {
      ISkConnection srcConn = aTsGuiContext.get( ISkConnectionSupplier.class ).defConn();
      ISkCoreApi coreApi = srcConn.coreApi();
      ISkRtdataService dataServ = coreApi.getService( ISkRtdataService.SERVICE_ID );
      if( dataServ.findReadCurrDataChannel( rtGwid ) == null ) {
        // создаем канал текущих данных
        dataServ.createReadCurrDataChannels( new GwidList( rtGwid ) );
      }
      ISkReadCurrDataChannel rtdCahannel = dataServ.findReadCurrDataChannel( rtGwid );

      return rtdCahannel.getValue();
    }
  }

  @Override
  public IStridablesList<IDataDef> paramDefs() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ValidationResult validateParams( IOptionSet aCondParams ) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String humanReadableDescription( IOptionSet aCondParams ) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String id() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String nmName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String description() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IOptionSet params() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <E> AbstractTsSingleChecker<E> create( E aEnviron, ITsSingleCondInfo aCombiCondInfo ) {
    // TODO Auto-generated method stub
    return null;
  }

}
