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

  public static String EVENT_CLASS_ID = "sk.SkEvent"; //$NON-NLS-1$

  /**
   * The ID of the field {@link SkEvent#paramValues()}.
   */
  // public static final String FID_PARAMS = "params"; //$NON-NLS-1$

  public SkAlertM5LifecycleManager( IM5Model<SkEvent> aModel, ISkConnection aMaster ) {
    super( aModel, false, false, false, true, aMaster );
  }

  @Override
  protected IList<SkEvent> doListEntities() {
    return IList.EMPTY;
    // IList<SkEvent> ll = skObjServ().listObjs( EVENT_CLASS_ID, false );
    // return ll;
  }
}
