package org.toxsoft.skf.alarms.gui.panels.impl;

import static org.toxsoft.core.tsgui.bricks.actions.ITsStdActionDefs.*;
import static org.toxsoft.core.tsgui.graphics.icons.ITsStdIconIds.*;
import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tsgui.m5.gui.mpc.IMultiPaneComponentConstants.*;
import static org.toxsoft.core.tslib.av.EAtomicType.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;

import org.eclipse.swt.*;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.actions.*;
import org.toxsoft.core.tsgui.bricks.actions.asp.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.stdevents.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.impl.*;
import org.toxsoft.core.tsgui.m5.gui.panels.*;
import org.toxsoft.core.tsgui.m5.gui.panels.impl.*;
import org.toxsoft.core.tsgui.m5.gui.viewers.*;
import org.toxsoft.core.tsgui.m5.gui.viewers.impl.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.panels.toolbar.*;
import org.toxsoft.core.tsgui.utils.checkcoll.*;
import org.toxsoft.core.tsgui.utils.layout.*;
import org.toxsoft.core.tslib.av.*;
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
      treeViewer.checks().setAllItemsCheckState( true );
    }

    void doUnCheckAll() {
      treeViewer.checks().setAllItemsCheckState( false );
    }

    void doAcknowledge() {
      ITsValidator<String> commentValidator = aValue -> ValidationResult.SUCCESS;
      String comment = AcknowledgeDlg.enterComment( tsContext(), commentValidator );
      if( comment != null ) {
        ISkLoggedUserInfo author = skConn().coreApi().getCurrentUserInfo();

        IList<SkEvent> checkedEvents = treeViewer.checks().listCheckedItems( true );
        for( int i = 0; i < checkedEvents.size(); i++ ) {
          SkEvent event = checkedEvents.get( i );
          // Getting object of alarm from event.
          ISkAlarm alarm = alarmService().findAlarm( event.eventGwid().strid() );
          alarm.sendAcknowledge( author.userSkid(), comment );
        }
      }
    }

    boolean isNotEmpty() {
      return !treeViewer.items().isEmpty();
    }

    boolean canAcknowledge() {
      return !treeViewer.checks().listCheckedItems( true ).isEmpty();
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

    /**
     * {@link EventM5Model},
     */
    String EVENTS_LIST_TABLE_DESCR = "Messages.EVENTS_LIST_TABLE_DESCR";
    String EVENTS_LIST_TABLE_NAME  = "Messages.EVENTS_LIST_TABLE_NAME";
    String EVENT_SRC_COL_DESCR     = "Messages.EVENT_SRC_COL_DESCR";
    String EVENT_SRC_COL_NAME      = "Messages.EVENT_SRC_COL_NAME";
    String EVENT_TIME_COL_DESCR    = "Messages.EVENT_TIME_COL_DESCR";
    String EVENT_TIME_COL_NAME     = "Messages.EVENT_TIME_COL_NAME";
    String EVENT_NAME_COL_DESCR    = "Messages.EVENT_NAME_COL_DESCR";
    String EVENT_NAME_COL_NAME     = "Messages.EVENT_NAME_COL_NAME";
    String DESCRIPTION_STR         = "Messages.DESCRIPTION_STR";
    String EV_DESCRIPTION          = "Messages.EV_DESCRIPTION";

    /**
     * Идентификатор поля {@link #TIME}.
     */
    public static final String FID_TIME = "ts.Time"; //$NON-NLS-1$

    /**
     * The ID of the field {@link SkEvent#paramValues()}.
     */
    public static final String FID_PARAMS = "params"; //$NON-NLS-1$

    /**
     * Время события
     */
    public final M5AttributeFieldDef<SkEvent> TIME = new M5AttributeFieldDef<>( FID_TIME, TIMESTAMP ) {

      @Override
      protected void doInit() {
        // setNameAndDescription( EVENT_TIME_COL_NAME, EVENT_TIME_COL_DESCR );
        setDefaultValue( IAtomicValue.NULL );
        setFlags( M5FF_COLUMN | M5FF_READ_ONLY );
      }

      @Override
      protected IAtomicValue doGetFieldValue( SkEvent aEntity ) {
        return avTimestamp( aEntity.timestamp() );
      }
    };

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

  private TsToolbar                   toolbar    = null;
  private IM5TreeViewer<SkEvent>      treeViewer = null;
  private IM5CollectionPanel<SkEvent> panel      = null;

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

    // CENTER: sash form
    SashForm sfMain = new SashForm( board, SWT.HORIZONTAL );
    sfMain.setLayoutData( BorderLayout.CENTER );
    // tree
    IM5Model<SkEvent> model = m5().getModel( InnerModel.MODEL_ID, SkEvent.class );
    IM5LifecycleManager<SkEvent> lm = new InnerLifecycleManager( model, skConn() );
    treeViewer = new M5TreeViewer<SkEvent>( tsContext(), model, true );
    MultiPaneComponentModown<SkEvent> eventComponent = new MultiPaneComponentModown<>( treeViewer );
    eventComponent.setItemProvider( lm.itemsProvider() );

    initializeAlertEvents();

    OPDEF_IS_TOOLBAR.setValue( tsContext().params(), AV_FALSE );
    OPDEF_IS_DETAILS_PANE.setValue( tsContext().params(), AV_FALSE );
    // OPDEF_DETAILS_PANE_PLACE.setValue( tsContext().params(), avValobj( EBorderLayoutPlacement.SOUTH ) );
    OPDEF_IS_SUPPORTS_TREE.setValue( tsContext().params(), AV_TRUE );
    // OPDEF_IS_ACTIONS_CRUD.setValue( tsContext().params(), AV_FALSE );
    // OPDEF_IS_FILTER_PANE.setValue( tsContext().params(), AV_FALSE );
    OPDEF_IS_SUMMARY_PANE.setValue( tsContext().params(), AV_TRUE );
    OPDEF_IS_SUPPORTS_CHECKS.setValue( tsContext().params(), AV_TRUE );
    // OPDEF_IS_ACTIONS_CRUD.setValue( tsContext().params(), AV_TRUE );
    // panel = model.panelCreator().createCollViewerPanel( tsContext(), lm.itemsProvider() );
    panel = new M5CollectionPanelMpcModownWrapper<>( eventComponent, true );
    // panel = model.panelCreator().createCollViewerPanel( tsContext(), lm.itemsProvider() );
    panel.createControl( sfMain );
    return board;
  }

  // ------------------------------------------------------------------------------------
  // IGenericCollPanel
  //

  @Override
  public IList<SkEvent> items() {
    return treeViewer.items();
  }

  @Override
  public ITsCheckSupport<SkEvent> checkSupport() {
    return treeViewer.checks();
  }

  @Override
  public void refresh() {
    treeViewer.refresh();
  }

  @Override
  public boolean isViewer() {
    return true;
  }

  @Override
  public IGenericChangeEventer genericChangeEventer() {
    return treeViewer.iconSizeChangeEventer(); // ???
  }

  @Override
  public SkEvent selectedItem() {
    return treeViewer.selectedItem();
  }

  @Override
  public void setSelectedItem( SkEvent aItem ) {
    treeViewer.setSelectedItem( aItem );
  }

  @Override
  public void addTsSelectionListener( ITsSelectionChangeListener<SkEvent> aListener ) {
    treeViewer.addTsSelectionListener( aListener );
  }

  @Override
  public void removeTsSelectionListener( ITsSelectionChangeListener<SkEvent> aListener ) {
    treeViewer.removeTsSelectionListener( aListener );
  }

  @Override
  public void addTsDoubleClickListener( ITsDoubleClickListener<SkEvent> aListener ) {
    treeViewer.addTsDoubleClickListener( aListener );
  }

  @Override
  public void removeTsDoubleClickListener( ITsDoubleClickListener<SkEvent> aListener ) {
    treeViewer.removeTsDoubleClickListener( aListener );
  }

  // ------------------------------------------------------------------------------------
  // ISkAlertHandler
  //

  @Override
  public void onAlert( SkEvent aEvent ) {
    treeViewer.items().add( aEvent );
    treeViewer.refresh();
  }

  @Override
  public void onAcknowledge( SkEvent aEvent ) {
    SkEvent alertEvent = findAlertEvent( aEvent );
    if( alertEvent != null ) {
      treeViewer.items().remove( alertEvent );
      treeViewer.refresh();
    }
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
          treeViewer.items().add( lastEvent );
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
