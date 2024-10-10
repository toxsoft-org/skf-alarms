package org.toxsoft.skf.alarms.gui.km5;

import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.uskat.core.api.evserv.*;
import org.toxsoft.uskat.core.connection.*;

/**
 * @author Slavage
 */
public class SkAlertM5LifecycleManager
    extends M5LifecycleManager<SkEvent, ISkConnection> {

  /**
   * The ID of the field {@link SkEvent#paramValues()}.
   */
  // public static final String FID_PARAMS = "params"; //$NON-NLS-1$

  public SkAlertM5LifecycleManager( IM5Model<SkEvent> aModel, ISkConnection aMaster ) {
    super( aModel, false, false, false, true, aMaster );
  }

  // private static SkEvent makeEvent( IM5Bunch<SkEvent> aValues ) {
  // IOptionSetEdit params = new OptionSet();
  // IList<IdValue> idvals = aValues.getAs( FID_PARAMS, IList.class );
  // IdValue.fillOptionSetFromIdValuesColl( idvals, params );
  //
  // SkEvent event = new SkEvent( System.currentTimeMillis(), aValues.originalEntity().eventGwid(), params );
  // return event;
  // }

  // @Override
  // protected SkEvent doCreate( IM5Bunch<SkEvent> aValues ) {
  // return makeEvent( aValues );
  // }
  //
  // @Override
  // protected SkEvent doEdit( IM5Bunch<SkEvent> aValues ) {
  // SkEvent retVal = makeEvent( aValues );
  // master().coreApi().eventService().fireEvent( retVal );
  // return retVal;
  // }
  //
  // @Override
  // protected void doRemove( SkEvent aEntity ) {
  // // nop
  // }

  @Override
  protected IList<SkEvent> doListEntities() {
    return IList.EMPTY;
  }

  // @Override
  // protected IList<SkEvent> doListEntities() {
  // return IList.EMPTY;
  // // long now = System.currentTimeMillis();
  // // ITimeInterval interval = new TimeInterval( now - 86400, now );
  // // IList<SkEvent> ll = master().coreApi().eventService().queryObjEvents( interval, Gwid aGwid );
  // // //listObjs( IPostalAddressConstants.CLSID, false );
  // // return ll;
  // }
}
