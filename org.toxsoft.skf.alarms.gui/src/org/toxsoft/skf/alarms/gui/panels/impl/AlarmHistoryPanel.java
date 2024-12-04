package org.toxsoft.skf.alarms.gui.panels.impl;

import static org.toxsoft.core.tsgui.bricks.actions.ITsStdActionDefs.*;
import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tslib.av.EAtomicType.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.core.tslib.utils.TsLibUtils.*;
import static org.toxsoft.skf.alarms.gui.ISkResources.*;
import static org.toxsoft.skf.alarms.lib.ISkAlarmConstants.*;

import java.text.*;
import java.util.*;

import org.eclipse.jface.resource.*;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.plugin.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.ctx.impl.*;
import org.toxsoft.core.tsgui.graphics.icons.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.impl.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.m5.std.models.av.*;
import org.toxsoft.core.tsgui.panels.toolbar.*;
import org.toxsoft.core.tsgui.utils.layout.BorderLayout;
import org.toxsoft.core.tsgui.valed.api.*;
import org.toxsoft.core.tsgui.widgets.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.misc.*;
import org.toxsoft.core.tslib.bricks.time.*;
import org.toxsoft.core.tslib.bricks.time.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.helpers.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.skf.alarms.gui.*;
import org.toxsoft.skf.alarms.gui.incub.*;
import org.toxsoft.skf.alarms.lib.*;
import org.toxsoft.uskat.core.api.evserv.*;
import org.toxsoft.uskat.core.api.sysdescr.*;
import org.toxsoft.uskat.core.connection.*;

/**
 * @author Slavage
 */
class AlarmHistoryPanel
    extends AbstractSkLazyControl {

  private DateTime startTime  = null;
  private DateTime startDate  = null;
  private DateTime finishTime = null;
  private DateTime finishDate = null;
  private ISkAlarm alarm      = null;

  private InnerM5Model                 innerModel;
  private IM5LifecycleManager<SkEvent> innerLifecycleManager;

  private MultiPaneComponentModown<SkEvent> componentModown;

  class InnerM5Model
      extends SkEventM5ModelBase {

    public static final String MODEL_ID = "SkEventM5Model.AlarmHistory"; //$NON-NLS-1$

    public static final String AID_EVENT_TIMESTAMP      = "EventTimestamp";          //$NON-NLS-1$
    public static final String AID_ALARM_EVENT_MESSAGE  = "AlarmEventMessage";       //$NON-NLS-1$
    public static final String AID_EVENT_ALARM_SEVERITY = "EventAlarmSeverity";      //$NON-NLS-1$
    public static final String AID_EVENT_TYPE_NAME      = "EventTypeName";           //$NON-NLS-1$
    public static final String AID_ALARM_NAME           = "EventAlarmName";          //$NON-NLS-1$
    public static final String AID_ACKNOWLEDGE_COMMENT  = "EventAcknowledgeComment"; //$NON-NLS-1$
    public static final String AID_ACKNOWLEDGE_AUTHOR   = "EventAcknowledgeAuthor";  //$NON-NLS-1$

    private static final ImageDescriptor imgDescrNone =
        AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, "icons/is16x16/warningSeverityAlarm.png" ); //$NON-NLS-1$
    private static final Image           noneImage    = imgDescrNone.createImage();

    private static final ImageDescriptor imgDescrWarning =
        AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, "icons/is16x16/warningSeverityAlarm.png" ); //$NON-NLS-1$
    private static final Image           warningImage    = imgDescrWarning.createImage();

    private static final ImageDescriptor imgDescrCritical =
        AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, "icons/is16x16/criticalSeverityAlarm.png" ); //$NON-NLS-1$
    private static final Image           criticalImage    = imgDescrCritical.createImage();

    private static final ImageDescriptor imgDescrAlert =
        AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, "icons/is16x16/alertAlarm.png" ); //$NON-NLS-1$
    private static final Image           alertImage    = imgDescrAlert.createImage();

    private static final ImageDescriptor imgDescrAcknowledge =
        AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, "icons/is16x16/acknowledgeAlarm.png" ); //$NON-NLS-1$
    private static final Image           acknowledgeImage    = imgDescrAcknowledge.createImage();

    private static final ImageDescriptor imgDescrMuted =
        AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, "icons/is16x16/mutedAlarm.png" ); //$NON-NLS-1$
    private static final Image           mutedImage    = imgDescrMuted.createImage();

    private static final ImageDescriptor imgDescrUnmuted =
        AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, "icons/is16x16/unmutedAlarm.png" ); //$NON-NLS-1$
    private static final Image           unmutedImage    = imgDescrUnmuted.createImage();

    private static final String timestampFormatString = "yyyy.MM.dd  HH:mm:ss .SSS"; //$NON-NLS-1$

    private static final DateFormat timestampFormat = new SimpleDateFormat( timestampFormatString );

    public final IM5AttributeFieldDef<SkEvent> EVENT_TIMESTAMP =
        new M5AttributeFieldDef<>( AID_EVENT_TIMESTAMP, TIMESTAMP, //
            TSID_NAME, STR_N_EVENT_TIME, //
            TSID_DESCRIPTION, STR_D_EVENT_TIME, //
            TSID_DEFAULT_VALUE, AV_TIME_0, //
            TSID_FORMAT_STRING, "%tF %tT" //$NON-NLS-1$
        ) {

          @Override
          protected void doInit() {
            setFlags( M5FF_COLUMN | M5FF_READ_ONLY );
          }

          @Override
          protected String doGetFieldValueName( SkEvent aEntity ) {
            Date dt = new Date( aEntity.timestamp() );
            return timestampFormat.format( dt );
            // return TimeUtils.timestampToSaneString( aEntity.timestamp() );
          }

          @Override
          protected IAtomicValue doGetFieldValue( SkEvent aEntity ) {
            return AvUtils.avStr( TimeUtils.timestampToSaneString( aEntity.timestamp() ) );
          }
        };

    public final IM5AttributeFieldDef<SkEvent> EVENT_TYPE_NAME = new M5AttributeFieldDef<>( AID_EVENT_TYPE_NAME, STRING, //
        TSID_NAME, STR_N_EVENT_TYPE_NAME, //
        TSID_DESCRIPTION, STR_D_EVENT_TYPE_NAME, //
        TSID_DEFAULT_VALUE, avStr( NONE_ID ) //
    ) {

      @Override
      protected void doInit() {
        setFlags( M5FF_COLUMN | M5FF_READ_ONLY );
      }

      @Override
      protected String doGetFieldValueName( SkEvent aEntity ) {
        String retVal = TsLibUtils.EMPTY_STRING;
        switch( aEntity.eventGwid().propId() ) {
          case ISkAlarmConstants.EVID_ALERT: {
            retVal = STR_ALERT_EVENT_TYPE;
            break;
          }
          case ISkAlarmConstants.EVID_ACKNOWLEDGE: {
            retVal = STR_ACKNOWLEDGE_EVENT_TYPE;
            break;
          }
          case ISkAlarmConstants.EVID_ALARM_MUTED: {
            retVal = STR_MUTED_EVENT_TYPE;
            break;
          }
          case ISkAlarmConstants.EVID_ALARM_UNMUTED: {
            retVal = STR_UNMUTED_EVENT_TYPE;
            break;
          }
        }
        return retVal;
      }

      @Override
      protected IAtomicValue doGetFieldValue( SkEvent aEntity ) {
        String retVal = TsLibUtils.EMPTY_STRING;
        switch( aEntity.eventGwid().propId() ) {
          case ISkAlarmConstants.EVID_ALERT: {
            retVal = STR_ALERT_EVENT_TYPE;
            break;
          }
          case ISkAlarmConstants.EVID_ACKNOWLEDGE: {
            retVal = STR_ACKNOWLEDGE_EVENT_TYPE;
            break;
          }
          case ISkAlarmConstants.EVID_ALARM_MUTED: {
            retVal = STR_MUTED_EVENT_TYPE;
            break;
          }
          case ISkAlarmConstants.EVID_ALARM_UNMUTED: {
            retVal = STR_UNMUTED_EVENT_TYPE;
            break;
          }
        }
        return AvUtils.avStr( retVal );
      }

      @Override
      protected Image doGetFieldValueIcon( SkEvent aEntity, EIconSize aIconSize ) {
        Image retVal = null;
        switch( aEntity.eventGwid().propId() ) {
          case ISkAlarmConstants.EVID_ALERT: {
            retVal = alertImage;
            break;
          }
          case ISkAlarmConstants.EVID_ACKNOWLEDGE: {
            retVal = acknowledgeImage;
            break;
          }
          case ISkAlarmConstants.EVID_ALARM_MUTED: {
            retVal = mutedImage;
            break;
          }
          case ISkAlarmConstants.EVID_ALARM_UNMUTED: {
            retVal = unmutedImage;
            break;
          }
          default:
            break;
        }
        return retVal;
      }

    };

    public final IM5AttributeFieldDef<SkEvent> EVENT_ALARM_NAME = new M5AttributeFieldDef<>( AID_ALARM_NAME, STRING, //
        TSID_NAME, STR_N_ALARM_NAME, //
        TSID_DESCRIPTION, STR_D_ALARM_NAME, //
        TSID_DEFAULT_VALUE, avStr( NONE_ID ) //
    ) {

      @Override
      protected void doInit() {
        setFlags( M5FF_COLUMN | M5FF_READ_ONLY );
      }

      @Override
      protected String doGetFieldValueName( SkEvent aEntity ) {
        ISkAlarm alarm = alarmService().findAlarm( aEntity.eventGwid().strid() );
        return alarm.description();
      }

      @Override
      protected IAtomicValue doGetFieldValue( SkEvent aEntity ) {
        ISkAlarm alarm = alarmService().findAlarm( aEntity.eventGwid().strid() );
        return AvUtils.avStr( alarm.description() );
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
            setFlags( M5FF_COLUMN | M5FF_READ_ONLY );
          }

          @Override
          protected String doGetFieldValueName( SkEvent aEntity ) {
            return extractAlertMsg( aEntity );
          }

          @Override
          protected IAtomicValue doGetFieldValue( SkEvent aEntity ) {
            return AvUtils.avStr( extractAlertMsg( aEntity ) );
          }

          private String extractAlertMsg( SkEvent aEntity ) {
            String retVal = EMPTY_STRING;
            if( aEntity.paramValues().hasKey( EVPRMID_ALERT_MESSAGE ) ) {
              retVal = aEntity.paramValues().getStr( EVPRMID_ALERT_MESSAGE );
            }
            return retVal;
          }

        };

    public final IM5MultiModownFieldDef<SkEvent, IdValue> EVENT_PARAM_VALUES =
        new M5MultiModownFieldDef<>( FID_PARAM_VALUES, IdValueM5Model.MODEL_ID ) {

          @Override
          protected void doInit() {
            setNameAndDescription( STR_N_EVENT_PARAMETERS, STR_D_EVENT_PARAMETERS );
            setFlags( M5FF_DETAIL | M5FF_READ_ONLY );
            // задаем нормальный размер!
            params().setInt( IValedControlConstants.OPDEF_VERTICAL_SPAN, 10 );
          }

          protected IList<IdValue> doGetFieldValue( SkEvent aEntity ) {
            return IdValue.makeIdValuesCollFromOptionSet( aEntity.paramValues() ).values();
          }

        };

    public InnerM5Model( ISkConnection aConn ) {
      super( MODEL_ID, aConn );
      setNameAndDescription( ESkClassPropKind.EVENT.nmName(), ESkClassPropKind.EVENT.description() );
      addFieldDefs( EVENT_TIMESTAMP, EVENT_TYPE_NAME, EVENT_ALARM_NAME, ALERT_EVENT_MESSAGE, EVENT_PARAM_VALUES );
    }

    @Override
    protected IM5LifecycleManager<SkEvent> doCreateDefaultLifecycleManager() {
      ISkConnection master = domain().tsContext().get( ISkConnection.class );
      return new InnerM5LifecycleManager( this, master );
    }

    @Override
    protected IM5LifecycleManager<SkEvent> doCreateLifecycleManager( Object aMaster ) {
      return new InnerM5LifecycleManager( this, ISkConnection.class.cast( aMaster ) );
    }
  }

  class InnerM5LifecycleManager
      extends M5LifecycleManager<SkEvent, ISkConnection> {

    public InnerM5LifecycleManager( IM5Model<SkEvent> aModel, ISkConnection aMaster ) {
      super( aModel, false, false, false, true, aMaster );
    }

    @Override
    protected IList<SkEvent> doListEntities() {
      if( alarm == null ) {
        return IList.EMPTY;
      }
      IQueryInterval interval = new QueryInterval( EQueryIntervalType.CSCE, //
          getTimeInMillis( startTime, startDate ), //
          getTimeInMillis( finishTime, finishDate ) );

      IListEdit<SkEvent> allEvents = new ElemLinkedBundleList<>();

      ITimedList<SkEvent> events = alarm.getHistory( interval );
      allEvents.addAll( events );

      IListReorderer<SkEvent> orderedEvents = new ListReorderer<>( allEvents );
      orderedEvents.sort( SkEvent::compareTo ); // The events, sortabled by timestamp.
      return orderedEvents.list();
    }
  }

  public AlarmHistoryPanel( ITsGuiContext aContext ) {
    super( aContext );
  }

  public void setAlarm( ISkAlarm aAlarm ) {
    alarm = aAlarm;
    componentModown.tree().items().clear();
  }

  @Override
  protected Control doCreateControl( Composite aParent ) {
    TsComposite board = new TsComposite( aParent, SWT.NONE );
    board.setMinimumHeight( 600 );
    board.setLayout( new BorderLayout() );

    ITsGuiContext ctx = new TsGuiContext( tsContext() );
    ctx.params().addAll( tsContext().params() ); // !!!

    TsComposite backplane = new TsComposite( board );
    backplane.setLayout( new GridLayout( 10, false ) );
    backplane.setLayoutData( BorderLayout.NORTH );

    Label l = new Label( backplane, SWT.CENTER );
    l.setText( STR_QUERY_INTERVAL );
    //
    startTime = new DateTime( backplane, SWT.BORDER | SWT.TIME );
    startDate = new DateTime( backplane, SWT.BORDER | SWT.DATE | SWT.CALENDAR | SWT.DROP_DOWN );
    // By default the request is per hour.
    int startHour = startTime.getHours() - 1;
    if( startHour < 0 ) {
      // We are working out the situation when we open at 0 o’clock.
      startTime.setHours( 0 );
      startTime.setMinutes( 0 );
      startTime.setSeconds( 0 );
    }
    else {
      startTime.setHours( startTime.getHours() - 1 );
    }
    // Just a separator.
    l = new Label( backplane, SWT.CENTER );
    l.setText( " - " ); //$NON-NLS-1$
    //
    finishTime = new DateTime( backplane, SWT.BORDER | SWT.TIME );
    finishDate = new DateTime( backplane, SWT.BORDER | SWT.DATE | SWT.CALENDAR | SWT.DROP_DOWN );

    // Heading.
    TsToolbar toolBar = new TsToolbar( ctx );
    toolBar.setIconSize( EIconSize.IS_24X24 );

    toolBar.addSeparator();
    toolBar.addActionDef( ACDEF_REFRESH );
    toolBar.addActionDef( ACDEF_FILTER );
    toolBar.addSeparator();
    toolBar.addActionDef( ACDEF_PRINT );

    Control toolbarCtrl = toolBar.createControl( backplane );

    toolBar.addListener( aActionId -> {
      if( aActionId.equals( ACDEF_REFRESH.id() ) ) {
        componentModown.refresh();
      }
      if( aActionId.equals( ACDEF_FILTER.id() ) ) {
        // IConcerningEventsParams retVal = chooseFilterParams();
        // if( retVal != null ) {
        // selectedParams = retVal;
        // currAction = ECurrentAction.QUERY_SELECTED;
        // genericChangeListenersHolder.fireChangeEvent();
        // }
      }
      if( aActionId.equals( ACDEF_PRINT.id() ) ) {
        // printEvents();
      }
    } );

    // Using temporary model.
    InnerM5Model model = new InnerM5Model( skConn() );
    m5().initTemporaryModel( model );

    IM5LifecycleManager<SkEvent> lm = new InnerM5LifecycleManager( model, skConn() );

    IMultiPaneComponentConstants.OPDEF_IS_TOOLBAR.setValue( ctx.params(), AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_IS_SUMMARY_PANE.setValue( ctx.params(), AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_IS_SUPPORTS_TREE.setValue( ctx.params(), AV_FALSE );
    IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( ctx.params(), AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_IS_DETAILS_PANE.setValue( ctx.params(), AV_TRUE );

    componentModown = new MultiPaneComponentModown<>( ctx, model, lm.itemsProvider(), lm );
    // TreeModeInfo<SkEvent> tmiByAlarm = new TreeModeInfo<>( "ByAlarm", //$NON-NLS-1$
    // "Группировать по тревогам", "Тревоги", "ByAlarm", new TreeMakerByAlarm() );
    // componentModown.treeModeManager().addTreeMode( tmiByAlarm );
    // componentModown.treeModeManager().setCurrentMode( "ByAlarm" ); // Default value is tree view.
    componentModown.createControl( board );
    componentModown.getControl().setLayoutData( BorderLayout.CENTER );

    innerModel = model;
    innerLifecycleManager = lm;

    return board;
  }

  private ISkAlarmService alarmService() {
    return coreApi().getService( ISkAlarmService.SERVICE_ID );
  }

  public static long getTimeInMillis( DateTime aTimeControl, DateTime aDateControl ) {
    Calendar cal = Calendar.getInstance();
    cal.set( Calendar.YEAR, aDateControl.getYear() );
    cal.set( Calendar.MONTH, aDateControl.getMonth() );
    cal.set( Calendar.DAY_OF_MONTH, aDateControl.getDay() );
    cal.set( Calendar.HOUR_OF_DAY, aTimeControl.getHours() );
    cal.set( Calendar.MINUTE, aTimeControl.getMinutes() );
    cal.set( Calendar.SECOND, aTimeControl.getSeconds() );
    return cal.getTimeInMillis();
  }

}
