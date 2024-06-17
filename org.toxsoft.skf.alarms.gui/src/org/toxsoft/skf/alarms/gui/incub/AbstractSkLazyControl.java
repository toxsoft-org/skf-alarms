package org.toxsoft.skf.alarms.gui.incub;

import static org.toxsoft.uskat.core.gui.ISkCoreGuiConstants.*;

import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.panels.lazy.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.utils.*;

public abstract class AbstractSkLazyControl
    extends AbstractLazyPanel<Control>
    implements ISkConnected {

  private final ISkConnection skConn;

  public AbstractSkLazyControl( ITsGuiContext aContext ) {
    super( aContext );
    skConn = skConnCtx( tsContext() );
  }

  // ------------------------------------------------------------------------------------
  // ISkConnected
  //

  @Override
  public ISkConnection skConn() {
    return skConn;
  }

  // ------------------------------------------------------------------------------------
  // ITsGuiContextable
  //

  @Override
  public IM5Domain m5() {
    return skConn.scope().get( IM5Domain.class );
  }

}
