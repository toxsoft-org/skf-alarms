package org.toxsoft.skf.alarms.skide.main;

import static org.toxsoft.core.tsgui.m5.gui.mpc.IMultiPaneComponentConstants.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;

import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.ctx.impl.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.panels.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.skf.alarms.lib.*;
import org.toxsoft.skide.core.api.*;
import org.toxsoft.skide.core.api.impl.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;

/**
 * Alarms configuration plugin panel is simply an editable M5 collection panel.
 *
 * @author hazard157
 */
class SkideUnitMnemoAlarms
    extends AbstractSkideUnitPanel {

  public SkideUnitMnemoAlarms( ITsGuiContext aContext, ISkideUnit aUnit ) {
    super( aContext, aUnit );
  }

  @Override
  protected Control doCreateControl( Composite aParent ) {
    ISkConnection skConn = tsContext().get( ISkConnectionSupplier.class ).defConn();
    IM5Domain m5d = skConn.scope().get( IM5Domain.class );
    IM5Model<ISkAlarm> model = m5d.getModel( ISkAlarmConstants.CLSID_ALARM, ISkAlarm.class );
    IM5LifecycleManager<ISkAlarm> lm = model.getLifecycleManager( skConn );
    ITsGuiContext ctx = new TsGuiContext( tsContext() );
    OPDEF_IS_FILTER_PANE.setValue( ctx.params(), AV_TRUE );
    IM5CollectionPanel<ISkAlarm> panel = model.panelCreator().createCollEditPanel( ctx, lm.itemsProvider(), lm );
    return panel.createControl( aParent );
  }

}
