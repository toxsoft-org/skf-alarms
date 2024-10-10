package org.toxsoft.skf.alarms.gui.km5;

import org.toxsoft.core.tsgui.bricks.actions.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.graphics.icons.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.impl.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.panels.toolbar.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.uskat.core.api.evserv.*;

/**
 * {@link MultiPaneComponentModown} implementation for {@link ISkEvent}.
 *
 * @author Slavage
 */
public class SkEventMpc
    extends MultiPaneComponentModown<SkEvent> {

  public SkEventMpc( ITsGuiContext aContext, IM5Model<SkEvent> aModel, IM5ItemsProvider<SkEvent> aItemsProvider,
      IM5LifecycleManager<SkEvent> aLifecycleManager ) {
    super( aContext, aModel, aItemsProvider, aLifecycleManager );
  }

  @Override
  protected ITsToolbar doCreateToolbar( ITsGuiContext aContext, String aName, EIconSize aIconSize,
      IListEdit<ITsActionDef> aActs ) {

    return super.doCreateToolbar( aContext, aName, aIconSize, aActs );
  }

  @Override
  protected void doProcessAction( String aActionId ) {
  }

  @Override
  protected void doUpdateActionsState( boolean aIsAlive, boolean aIsSel, SkEvent aSel ) {
  }
}
