package org.toxsoft.skf.alarms.gui.panels.impl;

import static org.toxsoft.core.tsgui.bricks.actions.ITsStdActionDefs.*;
import static org.toxsoft.core.tsgui.graphics.icons.ITsStdIconIds.*;
import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tsgui.valed.api.IValedControlConstants.*;
import static org.toxsoft.core.tslib.av.EAtomicType.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.alarms.gui.ISkResources.*;
import static org.toxsoft.skf.alarms.gui.ISkfAlarmsGuiConstants.*;

import org.eclipse.jface.resource.*;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.plugin.*;
import org.toxsoft.core.tsgui.bricks.actions.*;
import org.toxsoft.core.tsgui.bricks.actions.asp.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.ctx.impl.*;
import org.toxsoft.core.tsgui.bricks.stdevents.*;
import org.toxsoft.core.tsgui.graphics.icons.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.impl.*;
import org.toxsoft.core.tsgui.m5.gui.panels.*;
import org.toxsoft.core.tsgui.m5.gui.viewers.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.panels.toolbar.*;
import org.toxsoft.core.tsgui.utils.checkcoll.*;
import org.toxsoft.core.tsgui.utils.layout.*;
import org.toxsoft.core.tsgui.valed.controls.av.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.bricks.events.change.*;
import org.toxsoft.core.tslib.bricks.time.*;
import org.toxsoft.core.tslib.bricks.time.impl.*;
import org.toxsoft.core.tslib.bricks.validator.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.alarms.gui.*;
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
      STR_N_ALARM_ACKNOWLEDGE, STR_D_ALARM_ACKNOWLEDGE, ICONID_ALERT_ACKNOWLEDGE //
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
      // defineSeparator();
      // defineAction( ACDEF_DEBUG, this::doDebug, this::isDebug );
      // defineAction( ACDEF_DEBUG2, this::doDebug2, this::isDebug );
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

    public static final String AID_EVENT_TIMESTAMP      = "EventTimestamp";     //$NON-NLS-1$
    public static final String AID_ALARM_NAME           = "EventAlarmName";     //$NON-NLS-1$
    public static final String AID_ALARM_EVENT_MESSAGE  = "AlarmEventMessage";  //$NON-NLS-1$
    public static final String AID_EVENT_ALARM_SEVERITY = "EventAlarmSeverity"; //$NON-NLS-1$

    private static final ImageDescriptor imgDescrWarning =
        AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, "icons/is16x16/warningSeverityAlarm.png" ); //$NON-NLS-1$
    private static final Image           warningImage    = imgDescrWarning.createImage();

    private static final ImageDescriptor imgDescrCritical =
        AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, "icons/is16x16/criticalSeverityAlarm.png" ); //$NON-NLS-1$
    private static final Image           criticalImage    = imgDescrCritical.createImage();

    public final IM5AttributeFieldDef<SkEvent> EVENT_TIMESTAMP =
        new M5AttributeFieldDef<>( AID_EVENT_TIMESTAMP, TIMESTAMP, //
            TSID_NAME, STR_N_EVENT_TIME, //
            TSID_DESCRIPTION, STR_D_EVENT_TIME, //
            TSID_DEFAULT_VALUE, AV_TIME_0, //
            TSID_FORMAT_STRING, "%tF %tT" //$NON-NLS-1$
        ) {

          @Override
          protected void doInit() {
            setFlags( M5FF_COLUMN );
          }

          @Override
          protected String doGetFieldValueName( SkEvent aEntity ) {
            return TimeUtils.timestampToString( aEntity.timestamp() );
          }
        };

    public final IM5AttributeFieldDef<SkEvent> EVENT_ALARM_NAME = new M5AttributeFieldDef<>( AID_ALARM_NAME, STRING, //
        TSID_NAME, STR_N_ALARM_NAME, //
        TSID_DESCRIPTION, STR_D_ALARM_NAME, //
        TSID_DEFAULT_VALUE, avStr( NONE_ID ) //
    ) {

      @Override
      protected void doInit() {
        setFlags( M5FF_COLUMN );
      }

      @Override
      protected String doGetFieldValueName( SkEvent aEntity ) {
        ISkAlarm alarm = alarmService().findAlarm( aEntity.eventGwid().strid() );
        return alarm.description();
      }
    };

    public final IM5AttributeFieldDef<SkEvent> ALERT_EVENT_MESSAGE =
        new M5AttributeFieldDef<>( AID_ALARM_EVENT_MESSAGE, STRING, //
            TSID_NAME, STR_N_ALERT_EVENT_MESSAGE, //
            TSID_DESCRIPTION, STR_D_ALERT_EVENT_MESSAGE, //
            TSID_DEFAULT_VALUE, avStr( NONE_ID ) //
        ) {

          @Override
          protected void doInit() {
            setFlags( M5FF_COLUMN );
          }

          @Override
          protected String doGetFieldValueName( SkEvent aEntity ) {
            ISkAlarm alarm = alarmService().findAlarm( aEntity.eventGwid().strid() );
            return alarm.messageInfo().makeMessage( coreApi() );
          }
        };

    public M5AttributeFieldDef<SkEvent> EVENT_ALARM_SEVERITY =
        new M5AttributeFieldDef<>( AID_EVENT_ALARM_SEVERITY, EAtomicType.STRING, //
            TSID_NAME, STR_N_ALERT_SEVERITY, //
            TSID_DESCRIPTION, STR_D_ALERT_SEVERITY, //
            OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME //
        ) {

          @Override
          protected void doInit() {
            setFlags( M5FF_COLUMN );
          }

          protected IAtomicValue doGetFieldValue( SkEvent aEntity ) {
            IAtomicValue retVal = avStr( TsLibUtils.EMPTY_STRING );
            ISkAlarm alarm = alarmService().findAlarm( aEntity.eventGwid().strid() );
            switch( alarm.severity() ) {
              case WARNING: {
                retVal = avStr( STR_WARNING_SEVERITY_ALARM );
                break;
              }
              case CRITICAL: {
                retVal = avStr( STR_CRITICAL_SEVERITY_ALARM );
                break;
              }
              default:
                break;
            }
            return retVal;
          }

          @Override
          protected Image doGetFieldValueIcon( SkEvent aEntity, EIconSize aIconSize ) {
            Image retVal = warningImage;
            ISkAlarm alarm = alarmService().findAlarm( aEntity.eventGwid().strid() );
            switch( alarm.severity() ) {
              case WARNING: {
                retVal = warningImage;
                break;
              }
              case CRITICAL: {
                retVal = criticalImage;
                break;
              }
              default:
                break;
            }
            return retVal;
          }

        };

    public InnerModel( ISkConnection aConn ) {
      super( MODEL_ID, aConn );
      setNameAndDescription( ESkClassPropKind.EVENT.nmName(), ESkClassPropKind.EVENT.description() );
      addFieldDefs( EVENT_TIMESTAMP, EVENT_ALARM_SEVERITY, EVENT_ALARM_NAME, ALERT_EVENT_MESSAGE );
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

  private SoundAlarmManager soundAlarmManager;

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

    soundAlarmManager = (SoundAlarmManager)aContext.find( SoundAlarmManager.CONTEXT_ID );
  }

  @Override
  protected void doDispose() {
    soundAlarmManager.setType( SoundAlarmType.NONE );

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
    componentModown.tree().refresh();
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
    // componentModown.addTsSelectionListener( aListener );
  }

  @Override
  public void removeTsSelectionListener( ITsSelectionChangeListener<SkEvent> aListener ) {
    // componentModown.removeTsSelectionListener( aListener );
  }

  @Override
  public void addTsDoubleClickListener( ITsDoubleClickListener<SkEvent> aListener ) {
    // componentModown.addTsDoubleClickListener( aListener );
  }

  @Override
  public void removeTsDoubleClickListener( ITsDoubleClickListener<SkEvent> aListener ) {
    // componentModown.removeTsDoubleClickListener( aListener );
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
    ((IListEdit<SkEvent>)items()).insert( 0, aEvent );
    componentModown.tree().refresh();
    updateSoundAlarm();
  }

  @Override
  public void onAcknowledge( SkEvent aEvent ) {
    SkEvent alertEvent = findAlertEvent( aEvent );
    if( alertEvent != null ) {
      ((IListEdit<SkEvent>)items()).remove( alertEvent );
      componentModown.tree().refresh();
      updateSoundAlarm();
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

    // Using temporary model.
    InnerModel model = new InnerModel( skConn() );
    m5().initTemporaryModel( model );

    IM5LifecycleManager<SkEvent> lm = new InnerLifecycleManager( model, skConn() );

    IMultiPaneComponentConstants.OPDEF_IS_TOOLBAR.setValue( ctx.params(), AV_FALSE );
    IMultiPaneComponentConstants.OPDEF_DETAILS_PANE_PLACE.setValue( ctx.params(),
        avValobj( EBorderLayoutPlacement.SOUTH ) );
    IMultiPaneComponentConstants.OPDEF_IS_COLUMN_HEADER.setValue( ctx.params(), AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_IS_SUMMARY_PANE.setValue( ctx.params(), AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_IS_SUPPORTS_CHECKS.setValue( ctx.params(), AvUtils.AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_IS_ACTIONS_CRUD.setValue( ctx.params(), AvUtils.AV_FALSE );
    IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( ctx.params(), AvUtils.AV_FALSE );

    componentModown = new MultiPaneComponentModown<>( ctx, model, lm.itemsProvider(), lm );
    // eventsPanel = new M5CollectionPanelMpcModownWrapper<>( componentModown, false );

    componentModown.createControl( board );
    // eventsPanel.createControl( board );

    // Initializing width of columns.
    initializeColumnsWidth();

    initializeAlertEvents();

    return board;
  }

  /**
   * Initializing width of columns.
   */
  private void initializeColumnsWidth() {
    IStringMap<IM5Column<SkEvent>> columns = componentModown.tree().columnManager().columns();
    columns.findByKey( InnerModel.AID_EVENT_TIMESTAMP ).adjustWidth( "00.00.0000 00:00:00" );
    columns.findByKey( InnerModel.AID_EVENT_ALARM_SEVERITY ).setWidth( 150 );
    columns.findByKey( InnerModel.AID_ALARM_NAME ).setWidth( 400 );
    columns.findByKey( InnerModel.AID_ALARM_EVENT_MESSAGE ).setWidth( 500 );
    componentModown.tree().refresh();
  }

  // ------------------------------------------------------------------------------------
  // Implementation
  //

  private ISkAlarmService alarmService() {
    return coreApi().getService( ISkAlarmService.SERVICE_ID );
  }

  // private void updateActionsState() {
  // for( String actId : asp.listHandledActionIds() ) {
  // toolbar.setActionEnabled( actId, asp.isActionEnabled( actId ) );
  // toolbar.setActionChecked( actId, asp.isActionChecked( actId ) );
  // }
  // }

  /**
   * Updating the status of the sound alarm, i.s. turning on, turning off.
   */
  private void updateSoundAlarm() {
    SoundAlarmType type = SoundAlarmType.NONE;
    for( SkEvent event : items() ) {
      ISkAlarm alarm = alarmService().findAlarm( event.eventGwid().strid() );
      if( alarm.severity() == ESkAlarmSeverity.WARNING ) {
        if( type != SoundAlarmType.WARNING ) {
          type = SoundAlarmType.WARNING;
        }
      }
      else {
        if( alarm.severity() == ESkAlarmSeverity.CRITICAL ) {
          type = SoundAlarmType.CRITICAL;
          break;
        }
      }
    }
    soundAlarmManager.setType( type );
  }

  /**
   * Initializing alert event.
   */
  private void initializeAlertEvents() {
    IList<ISkAlarm> allAlarms = alarmService().listAlarms();
    // The events, sortabled by timestamp.
    IMapEdit<Long, SkEvent> alertEvents = new ElemMap<>();
    for( ISkAlarm alarm : allAlarms ) {
      if( alarm.isAlert() ) {
        long now = System.currentTimeMillis();
        IQueryInterval interval = new QueryInterval( EQueryIntervalType.OSCE, now, now );
        ITimedList<SkEvent> events = alarm.getHistory( interval );
        // TODO Почему не работает interval
        SkEvent lastEvent = events.last(); // events.findOnly();
        if( lastEvent != null ) {
          alertEvents.put( lastEvent.timestamp(), lastEvent );
        }
      }
    }
    for( SkEvent event : alertEvents ) {
      ((IListEdit<SkEvent>)items()).insert( 0, event );
    }
    refresh();
    updateSoundAlarm();
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
