package org.toxsoft.skf.alarms.gui.km5;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.impl.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.skf.alarms.lib.*;
import org.toxsoft.uskat.core.api.evserv.*;

/**
 * {@link MultiPaneComponentModown} implementation for {@link ISkAlarm}.
 *
 * @author slavage
 */
class SkAlertMpc
    extends MultiPaneComponentModown<SkEvent> {

  public SkAlertMpc( ITsGuiContext aContext, IM5Model<SkEvent> aModel, IM5ItemsProvider<SkEvent> aItemsProvider,
      IM5LifecycleManager<SkEvent> aLifecycleManager ) {
    super( aContext, aModel, aItemsProvider, aLifecycleManager );
  }
}
