package org.toxsoft.skf.alarms.gui.panels.impl;

import static org.toxsoft.core.tsgui.bricks.actions.ITsStdActionDefs.*;
import static org.toxsoft.core.tsgui.graphics.icons.ITsStdIconIds.*;
import static org.toxsoft.core.tsgui.m5.gui.mpc.IMultiPaneComponentConstants.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;

import org.eclipse.swt.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.actions.*;
import org.toxsoft.core.tsgui.bricks.actions.asp.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.stdevents.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.panels.*;
import org.toxsoft.core.tsgui.m5.gui.viewers.*;
import org.toxsoft.core.tsgui.m5.gui.viewers.impl.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.panels.toolbar.*;
import org.toxsoft.core.tsgui.utils.checkcoll.*;
import org.toxsoft.core.tsgui.utils.layout.*;
import org.toxsoft.core.tslib.bricks.events.change.*;
import org.toxsoft.core.tslib.bricks.time.*;
import org.toxsoft.core.tslib.bricks.time.impl.*;
import org.toxsoft.core.tslib.bricks.validator.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.alarms.gui.incub.*;
import org.toxsoft.skf.alarms.gui.km5.*;
import org.toxsoft.skf.alarms.gui.panels.*;
import org.toxsoft.skf.alarms.lib.*;
import org.toxsoft.uskat.core.api.evserv.*;
import org.toxsoft.uskat.core.api.users.*;

/**
 * {@link IAlertRtPanel} implementation.
 *
 * @author hazard157
 */
public class AlertRtPanel
    extends AbstractSkLazyControl
    implements IAlertRtPanel, ISkAlertListener {

  // private static final String MODEL_ID = "SkAlert"; //$NON-NLS-1$

  private static final String ACTID_ACKNOWLEDGE = "acknowledge"; //$NON-NLS-1$
  private static final String ACTID_DEBUG       = "debug";       //$NON-NLS-1$
  private static final String ACTID_DEBUG2      = "debug2";      //$NON-NLS-1$

  private static final ITsActionDef ACDEF_ACKNOWLEDGE = TsActionDef.ofPush2( ACTID_ACKNOWLEDGE, //
      "Acknowledge", "Acknowledge marked alers", ICONID_ARROW_LEFT_DOUBLE //
  );

  private static final ITsActionDef ACDEF_DEBUG = TsActionDef.ofPush2( ACTID_DEBUG, //
      "Set acknowledge", "Set acknowledge", ICONID_ARROW_DOWN //
  );

  private static final ITsActionDef ACDEF_DEBUG2 = TsActionDef.ofPush2( ACTID_DEBUG2, //
      "Clear acknowledge", "Clear acknowledge", ICONID_ARROW_UP //
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
      defineSeparator();
      defineAction( ACDEF_DEBUG, this::doDebug, this::isDebug );
      defineAction( ACDEF_DEBUG2, this::doDebug2, this::isDebug );
    }

    void doCheckAll() {
      tree.checks().setAllItemsCheckState( true );
    }

    void doUnCheckAll() {
      tree.checks().setAllItemsCheckState( false );
    }

    void doAcknowledge() {
      ITsValidator<String> commentValidator = aValue -> ValidationResult.SUCCESS;
      String comment = AcknowledgeDlg.enterComment( tsContext(), commentValidator );
      if( comment != null ) {
        ISkLoggedUserInfo author = skConn().coreApi().getCurrentUserInfo();

        IList<SkEvent> checkedEvents = tree.checks().listCheckedItems( true );
        for( int i = 0; i < checkedEvents.size(); i++ ) {
          SkEvent event = checkedEvents.get( i );
          // Getting object of alarm from event.
          ISkAlarm alarm = alarmService().findAlarm( event.eventGwid().strid() );
          alarm.sendAcknowledge( author.userSkid(), comment );
        }
      }
    }

    boolean isNotEmpty() {
      return !tree.items().isEmpty();
    }

    boolean canAcknowledge() {
      return !tree.checks().listCheckedItems( true ).isEmpty();
    }

    void doDebug() {
      IList<ISkAlarm> allAlarms = alarmService().listAlarms();
      for( ISkAlarm alarm : allAlarms ) {
        alarm.setAlert();
      }
    }

    void doDebug2() {
      ISkLoggedUserInfo author = skConn().coreApi().getCurrentUserInfo();
      IList<ISkAlarm> allAlarms = alarmService().listAlarms();
      for( ISkAlarm alarm : allAlarms ) {
        alarm.sendAcknowledge( author.userSkid(), "Debug acknowledge" );
      }
    }

    boolean isDebug() {
      return true;
    }
  }

  private final AspLocal asp = new AspLocal();

  private TsToolbar                   toolbar = null;
  private IM5TreeViewer<SkEvent>      tree    = null;
  private IM5CollectionPanel<SkEvent> panel   = null;

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
    alarmService().addAlertListener( this );
  }

  @Override
  protected void doDispose() {
    alarmService().removeAlertListener( this );
    super.doDispose();
  }

  @Override
  public ISkidList listMonitoredAlarms() {
    return null;
  }

  @Override
  public void setMonitoredAlarms( ISkidList aAlarmSkids ) {
    //
  }

  @Override
  public IList<ISkAlarm> listAlertAlarms() {
    IList<ISkAlarm> allAlarms = alarmService().listAlarms();
    IListEdit<ISkAlarm> alertAlarms = new ElemLinkedBundleList<>( 256, false );
    for( ISkAlarm alarm : allAlarms ) {
      if( alarm.isAlert() ) {
        alertAlarms.add( alarm );
      }
    }
    return alertAlarms;
  }

  // ------------------------------------------------------------------------------------
  // AbstractLazyPanel
  //

  @Override
  protected Control doCreateControl( Composite aParent ) {
    Composite board = new Composite( aParent, SWT.NONE );
    board.setLayout( new BorderLayout() );
    // toolbar
    toolbar = TsToolbar.create( board, tsContext(), asp.listAllActionDefs() );
    toolbar.getControl().setLayoutData( BorderLayout.NORTH );
    toolbar.addListener( asp );

    // ITsGuiContext ctx1 = new TsGuiContext( tsContext() );
    // toolbar = TsToolbar.create( aParent, ctx1, asp.listAllActionDefs() );
    // toolbar.createControl( aParent );
    // toolbar.getControl().setLayoutData( BorderLayout.NORTH );

    // toolbar = TsToolbar.create( board, tsContext(), asp.listAllActionDefs() );
    // toolbar.getControl().setLayoutData( BorderLayout.NORTH );

    // CENTER: sash form
    SashForm sfMain = new SashForm( board, SWT.HORIZONTAL );
    sfMain.setLayoutData( BorderLayout.CENTER );
    // tree
    // ITsGuiContext ctx2 = new TsGuiContext( tsContext() );
    IM5Model<SkEvent> model = m5().getModel( SkAlertM5Model.MODEL_ID, SkEvent.class );
    IM5LifecycleManager<SkEvent> lm = new SkAlertM5LifecycleManager( model, skConn() );
    //
    // IM5Model<SkEvent> model = m5().getModel( SkEventM5Model.MID_SKEVENT_M5MODEL, SkEvent.class );
    // IM5LifecycleManager<SkEvent> lm = new SkEventM5LifecycleManager( model, skConn() );
    tree = new M5TreeViewer<SkEvent>( tsContext(), model, true );

    initializeItems();

    OPDEF_IS_TOOLBAR.setValue( tsContext().params(), AV_FALSE );
    OPDEF_IS_DETAILS_PANE.setValue( tsContext().params(), AV_FALSE );
    // OPDEF_DETAILS_PANE_PLACE.setValue( tsContext().params(), avValobj( EBorderLayoutPlacement.SOUTH ) );
    OPDEF_IS_SUPPORTS_TREE.setValue( tsContext().params(), AV_TRUE );
    OPDEF_IS_ACTIONS_CRUD.setValue( tsContext().params(), AV_FALSE );
    OPDEF_IS_FILTER_PANE.setValue( tsContext().params(), AV_FALSE );
    OPDEF_IS_SUMMARY_PANE.setValue( tsContext().params(), AV_TRUE );
    OPDEF_IS_SUPPORTS_CHECKS.setValue( tsContext().params(), AV_TRUE );
    // OPDEF_IS_ACTIONS_CRUD.setValue( tsContext().params(), AV_TRUE );
    panel = model.panelCreator().createCollEditPanel( tsContext(), lm.itemsProvider(), lm );
    panel.createControl( sfMain );
    return board;
  }

  // ------------------------------------------------------------------------------------
  // IGenericCollPanel
  //

  @Override
  public IList<SkEvent> items() {
    return tree.items();
  }

  @Override
  public ITsCheckSupport<SkEvent> checkSupport() {
    return tree.checks();
  }

  @Override
  public void refresh() {
    tree.refresh();
  }

  @Override
  public boolean isViewer() {
    return true;
  }

  @Override
  public IGenericChangeEventer genericChangeEventer() {
    return tree.iconSizeChangeEventer(); // ???
  }

  @Override
  public SkEvent selectedItem() {
    return tree.selectedItem();
  }

  @Override
  public void setSelectedItem( SkEvent aItem ) {
    tree.setSelectedItem( aItem );
  }

  @Override
  public void addTsSelectionListener( ITsSelectionChangeListener<SkEvent> aListener ) {
    tree.addTsSelectionListener( aListener );
  }

  @Override
  public void removeTsSelectionListener( ITsSelectionChangeListener<SkEvent> aListener ) {
    tree.removeTsSelectionListener( aListener );
  }

  @Override
  public void addTsDoubleClickListener( ITsDoubleClickListener<SkEvent> aListener ) {
    tree.addTsDoubleClickListener( aListener );
  }

  @Override
  public void removeTsDoubleClickListener( ITsDoubleClickListener<SkEvent> aListener ) {
    tree.removeTsDoubleClickListener( aListener );
  }

  // ------------------------------------------------------------------------------------
  // ISkAlertHandler
  //

  @Override
  public void onAlert( SkEvent aEvent ) {
    tree.items().add( aEvent );
    tree.refresh();
  }

  @Override
  public void onAcknowledge( SkEvent aEvent ) {
    tree.items().remove( aEvent );
    tree.refresh();
  }

  // ------------------------------------------------------------------------------------
  // Implementation
  //

  private ISkAlarmService alarmService() {
    return coreApi().getService( ISkAlarmService.SERVICE_ID );
  }

  private void initializeItems() {
    IList<ISkAlarm> allAlarms = alarmService().listAlarms();
    for( ISkAlarm alarm : allAlarms ) {
      if( alarm.isAlert() ) {
        IQueryInterval interval = new QueryInterval( EQueryIntervalType.OSCE, 0, System.currentTimeMillis() );
        ITimedList<SkEvent> events = alarm.getHistory( interval );
        SkEvent lastEvent = events.findOnly();
        if( lastEvent != null ) {
          tree.items().add( lastEvent );
        }
      }
    }
  }

}
