package org.toxsoft.skf.alarms.gui.panels.impl;

import static org.toxsoft.core.tsgui.bricks.actions.ITsStdActionDefs.*;
import static org.toxsoft.core.tsgui.graphics.icons.ITsStdIconIds.*;
import static org.toxsoft.skf.alarms.lib.ISkAlarmConstants.*;

import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.actions.*;
import org.toxsoft.core.tsgui.bricks.actions.asp.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.m5.gui.viewers.*;
import org.toxsoft.core.tsgui.panels.toolbar.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.alarms.gui.incub.*;
import org.toxsoft.skf.alarms.gui.panels.*;
import org.toxsoft.skf.alarms.lib.*;
import org.toxsoft.uskat.core.api.evserv.*;

/**
 * {@link IAlertRtPanel} implementation.
 *
 * @author hazard157
 */
public class AlertRtPanel
    extends AbstractSkLazyControl
    implements IAlertRtPanel, ISkEventHandler {

  private static final String ACTID_ACKNOWLEDGE = "acknowledge"; //$NON-NLS-1$

  private static final ITsActionDef ACDEF_ACKNOWLEDGE = TsActionDef.ofPush2( ACTID_ACKNOWLEDGE, //
      "Acknowledge", "Acknowledge marked alers", ICONID_ARROW_LEFT_DOUBLE //
  );

  /**
   * Handles user actions.
   *
   * @author hazard157
   */
  class AspLocal
      extends MethodPerActionTsActionSetProvider {

    public AspLocal() {
      defineAction( ACDEF_CHECK_ALL, this::doCheckAll, this::isNotEmpty );
      defineAction( ACDEF_UNCHECK_ALL, this::doUnCheckAll, this::isNotEmpty );
      defineSeparator();
      defineAction( ACDEF_ACKNOWLEDGE, this::doAcknowledge, this::canAcknowledge );
    }

    void doCheckAll() {
      tree.checks().setAllItemsCheckState( true );
    }

    void doUnCheckAll() {
      tree.checks().setAllItemsCheckState( false );
    }

    void doAcknowledge() {
      // TODO AlertRtPanel.AspLocal.doAcknowledge()
    }

    boolean isNotEmpty() {
      return !tree.items().isEmpty();
    }

    boolean canAcknowledge() {
      return !tree.checks().listCheckedItems( true ).isEmpty();
    }

  }

  private final AspLocal asp = new AspLocal();

  private TsToolbar              toolbar = null;
  private IM5TreeViewer<SkEvent> tree    = null;

  /**
   * Constructor.
   * <p>
   * Constructor stores reference to the context, does not creates copy.
   *
   * @param aContext {@link ITsGuiContext} - the context
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  public AlertRtPanel( ITsGuiContext aContext ) {
    super( aContext );
    // listen to the alert/acknowledge events
    IGwidList gwids = new GwidList( //
        Gwid.createEvent( CLSID_ALARM, Gwid.STR_MULTI_ID, EVID_ALERT ), // alert events of all alarms
        Gwid.createEvent( CLSID_ALARM, Gwid.STR_MULTI_ID, EVID_ACKNOWLEDGE ) // acknowledge events of all alarms
    );
    skEventServ().registerHandler( gwids, this );
  }

  @Override
  protected void doDispose() {
    skEventServ().unregisterHandler( this );
    super.doDispose();
  }

  // ------------------------------------------------------------------------------------
  // AbstractLazyPanel
  //

  @Override
  protected Control doCreateControl( Composite aParent ) {
    // aParent.setLayout( new BorderLayout() );
    // // toolbar
    // ITsGuiContext ctx1 = new TsGuiContext( tsContext() );
    // toolbar = TsToolbar.create( aParent, ctx1, asp.listAllActionDefs() );
    // toolbar.createControl( aParent );
    // toolbar.getControl().setLayoutData( BorderLayout.NORTH );
    // // tree
    // ITsGuiContext ctx2 = new TsGuiContext( tsContext() );
    // IM5Model<SkEvent> model = m5().getModel( SkEvMMCLSID_ALARM, ISkAlarm.class );
    // tree = new M5TreeViewer<>( ctx2, model );
    //
    // // TODO Auto-generated method stub

    // TODO реализовать AlertRtPanel.doCreateControl()
    throw new TsUnderDevelopmentRtException( "AlertRtPanel.doCreateControl()" );
  }

  // ------------------------------------------------------------------------------------
  // ISkEventHandler
  //

  @Override
  public void onEvents( ISkEventList aEvents ) {
    // TODO Auto-generated method stub

  }

  @Override
  public IList<ISkAlarm> listAlertAlarms() {
    // TODO Auto-generated method stub
    return null;
  }

}
