package org.toxsoft.skf.alarms.gui.panels.impl;

import static org.toxsoft.core.tsgui.bricks.actions.ITsStdActionDefs.*;
import static org.toxsoft.core.tsgui.graphics.icons.ITsStdIconIds.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.actions.*;
import org.toxsoft.core.tsgui.bricks.actions.asp.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.ctx.impl.*;
import org.toxsoft.core.tsgui.bricks.stdevents.*;
import org.toxsoft.core.tsgui.bricks.tstree.impl.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.impl.*;
import org.toxsoft.core.tsgui.m5.gui.panels.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.panels.toolbar.*;
import org.toxsoft.core.tsgui.utils.checkcoll.*;
import org.toxsoft.core.tsgui.utils.layout.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.bricks.events.change.*;
import org.toxsoft.core.tslib.bricks.time.*;
import org.toxsoft.core.tslib.bricks.time.impl.*;
import org.toxsoft.core.tslib.bricks.validator.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.alarms.gui.incub.*;
import org.toxsoft.skf.alarms.gui.panels.*;
import org.toxsoft.skf.alarms.lib.*;
import org.toxsoft.uskat.core.api.evserv.*;
import org.toxsoft.uskat.core.api.sysdescr.*;
import org.toxsoft.uskat.core.api.users.*;
import org.toxsoft.uskat.core.connection.*;

/**
 * {@link IAlertRtPanel} implementation.
 *
 * @author hazard157
 */
public class AlertRtPanel
    extends AbstractSkLazyControl
    implements IAlertRtPanel, ISkAlertListener {

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
      componentModown.tree().checks().setAllItemsCheckState( true );
    }

    void doUnCheckAll() {
      componentModown.tree().checks().setAllItemsCheckState( false );
    }

    void doAcknowledge() {
      ITsValidator<String> commentValidator = aValue -> ValidationResult.SUCCESS;
      String comment = AcknowledgeDlg.enterComment( tsContext(), commentValidator );
      if( comment != null ) {
        ISkLoggedUserInfo author = skConn().coreApi().getCurrentUserInfo();

        IList<SkEvent> checkedEvents = componentModown.tree().checks().listCheckedItems( true );
        for( int i = 0; i < checkedEvents.size(); i++ ) {
          SkEvent event = checkedEvents.get( i );
          // Getting object of alarm from event.
          ISkAlarm alarm = alarmService().findAlarm( event.eventGwid().strid() );
          alarm.sendAcknowledge( author.userSkid(), comment );
        }
      }
    }

    boolean isNotEmpty() {
      return !componentModown.tree().items().isEmpty();
    }

    boolean canAcknowledge() {
      return !componentModown.tree().checks().listCheckedItems( true ).isEmpty();
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

  class InnerModel
      extends SkEventM5ModelBase {

    public static final String MODEL_ID = "SkAlertM5Model"; //$NON-NLS-1$

    public InnerModel( ISkConnection aConn ) {
      super( MODEL_ID, aConn );
      setNameAndDescription( ESkClassPropKind.EVENT.nmName(), ESkClassPropKind.EVENT.description() );
      addFieldDefs( EV_TIMESTAMP, EVENT_ID, PARAM_VALUES );
    }

    @Override
    protected IM5LifecycleManager<SkEvent> doCreateDefaultLifecycleManager() {
      ISkConnection master = domain().tsContext().get( ISkConnection.class );
      return new InnerLifecycleManager( this, master );
    }

    @Override
    protected IM5LifecycleManager<SkEvent> doCreateLifecycleManager( Object aMaster ) {
      return new InnerLifecycleManager( this, ISkConnection.class.cast( aMaster ) );
    }
  }

  class InnerLifecycleManager
      extends M5LifecycleManager<SkEvent, ISkConnection> {

    public InnerLifecycleManager( IM5Model<SkEvent> aModel, ISkConnection aMaster ) {
      super( aModel, false, false, false, true, aMaster );
      setItemsProvider( () -> items() );
    }

    @Override
    protected IList<SkEvent> doListEntities() {
      return items();
    }
  }

  private final AspLocal asp = new AspLocal();

  private MultiPaneComponentModown<SkEvent> componentModown;
  private IM5CollectionPanel<SkEvent>       eventsPanel;

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

  // ------------------------------------------------------------------------------------
  // IAlertRtPanel

  @Override
  public IList<SkEvent> items() {
    return componentModown.tree().items();
  }

  @Override
  public ITsCheckSupport<SkEvent> checkSupport() {
    return componentModown.tree().checks();
  }

  @Override
  public void refresh() {
    componentModown.refresh();
  }

  @Override
  public boolean isViewer() {
    return true;
  }

  @Override
  public IGenericChangeEventer genericChangeEventer() {
    return componentModown.genericChangeEventer();
  }

  @Override
  public SkEvent selectedItem() {
    return componentModown.selectedItem();
  }

  @Override
  public void setSelectedItem( SkEvent aItem ) {
    componentModown.setSelectedItem( aItem );
  }

  @Override
  public void addTsSelectionListener( ITsSelectionChangeListener<SkEvent> aListener ) {
    componentModown.addTsSelectionListener( aListener );
  }

  @Override
  public void removeTsSelectionListener( ITsSelectionChangeListener<SkEvent> aListener ) {
    componentModown.removeTsSelectionListener( aListener );
  }

  @Override
  public void addTsDoubleClickListener( ITsDoubleClickListener<SkEvent> aListener ) {
    componentModown.addTsDoubleClickListener( aListener );
  }

  @Override
  public void removeTsDoubleClickListener( ITsDoubleClickListener<SkEvent> aListener ) {
    componentModown.removeTsDoubleClickListener( aListener );
  }

  @Override
  public ISkidList listMonitoredAlarms() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setMonitoredAlarms( ISkidList aAlarmSkids ) {
    // TODO Auto-generated method stub
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
  // ISkAlertListener

  @Override
  public void onAlert( SkEvent aEvent ) {
    ((IListEdit<SkEvent>)componentModown.tree().items()).add( aEvent );
    componentModown.tree().refresh();
  }

  @Override
  public void onAcknowledge( SkEvent aEvent ) {
    SkEvent alertEvent = findAlertEvent( aEvent );
    if( alertEvent != null ) {
      ((IListEdit<SkEvent>)componentModown.tree().items()).remove( aEvent );
      componentModown.tree().refresh();
    }
  }

  // ------------------------------------------------------------------------------------
  // AbstractLazyPanel

  @Override
  protected Control doCreateControl( Composite aParent ) {
    Composite board = new Composite( aParent, SWT.NONE );
    board.setLayout( new BorderLayout() );

    ITsGuiContext ctx = new TsGuiContext( tsContext() );
    ctx.params().addAll( tsContext().params() ); // !!!

    TsToolbar toolbar = TsToolbar.create( board, ctx, asp.listAllActionDefs() );
    toolbar.getControl().setLayoutData( BorderLayout.NORTH );
    toolbar.addListener( asp );

    IM5Model<SkEvent> model = m5().getModel( InnerModel.MODEL_ID, SkEvent.class );
    IM5LifecycleManager<SkEvent> lm = new InnerLifecycleManager( model, skConn() );

    IMultiPaneComponentConstants.OPDEF_IS_TOOLBAR.setValue( ctx.params(), AV_FALSE );
    IMultiPaneComponentConstants.OPDEF_IS_DETAILS_PANE.setValue( ctx.params(), AvUtils.AV_FALSE );
    IMultiPaneComponentConstants.OPDEF_DETAILS_PANE_PLACE.setValue( ctx.params(),
        avValobj( EBorderLayoutPlacement.SOUTH ) );
    IMultiPaneComponentConstants.OPDEF_IS_SUMMARY_PANE.setValue( ctx.params(), AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_IS_COLUMN_HEADER.setValue( ctx.params(), AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_IS_SUPPORTS_CHECKS.setValue( ctx.params(), AvUtils.AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_IS_ACTIONS_CRUD.setValue( ctx.params(), AvUtils.AV_FALSE );
    IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( ctx.params(), AvUtils.AV_FALSE );
    TsTreeViewer.OPDEF_IS_HEADER_SHOWN.setValue( ctx.params(), AvUtils.AV_TRUE );

    componentModown = new MultiPaneComponentModown<>( ctx, model, lm.itemsProvider(), lm );
    // eventsPanel = new M5CollectionPanelMpcModownWrapper<>( componentModown, false );

    componentModown.createControl( board );
    // eventsPanel.createControl( board );

    initializeAlertEvents();

    return board;
  }

  // ------------------------------------------------------------------------------------
  // Implementation
  //

  private ISkAlarmService alarmService() {
    return coreApi().getService( ISkAlarmService.SERVICE_ID );
  }

  /**
   * Initializing alert event.
   */
  private void initializeAlertEvents() {
    IList<ISkAlarm> allAlarms = alarmService().listAlarms();
    for( ISkAlarm alarm : allAlarms ) {
      if( alarm.isAlert() ) {
        IQueryInterval interval = new QueryInterval( EQueryIntervalType.OSCE, 0, System.currentTimeMillis() );
        ITimedList<SkEvent> events = alarm.getHistory( interval );
        SkEvent lastEvent = events.findOnly();
        if( lastEvent != null ) {
          ((IListEdit<SkEvent>)eventsPanel.items()).add( lastEvent );
        }
      }
    }
  }

  /**
   * Returning alert event by acknowledge event.
   *
   * @param aAcknowledgeEvent Event of acknowledge.
   * @return Event of alert.
   */
  private SkEvent findAlertEvent( SkEvent aAcknowledgeEvent ) {
    ISkAlarm alarm = alarmService().findAlarm( aAcknowledgeEvent.eventGwid().strid() );
    for( int i = 0; i < items().size(); i++ ) {
      SkEvent event = items().get( i );
      if( event.eventGwid().strid().equals( alarm.strid() ) ) {
        return event;
      }
    }
    return null;
  }
}
