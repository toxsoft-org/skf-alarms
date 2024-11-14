package org.toxsoft.skf.alarms.gui.panels.impl;

import static org.toxsoft.core.tsgui.bricks.actions.ITsStdActionDefs.*;
import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tsgui.valed.api.IValedControlConstants.*;
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
import org.toxsoft.core.jasperreports.gui.main.*;
import org.toxsoft.core.tsgui.bricks.actions.asp.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.ctx.impl.*;
import org.toxsoft.core.tsgui.bricks.tsnodes.*;
import org.toxsoft.core.tsgui.bricks.tstree.tmm.*;
import org.toxsoft.core.tsgui.dialogs.*;
import org.toxsoft.core.tsgui.graphics.icons.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.impl.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.m5.std.models.av.*;
import org.toxsoft.core.tsgui.panels.toolbar.*;
import org.toxsoft.core.tsgui.utils.layout.BorderLayout;
import org.toxsoft.core.tsgui.valed.controls.av.*;
import org.toxsoft.core.tsgui.widgets.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.misc.*;
import org.toxsoft.core.tslib.bricks.events.change.*;
import org.toxsoft.core.tslib.bricks.time.*;
import org.toxsoft.core.tslib.bricks.time.impl.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.helpers.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.skf.alarms.gui.*;
import org.toxsoft.skf.alarms.gui.incub.*;
import org.toxsoft.skf.alarms.gui.panels.*;
import org.toxsoft.skf.alarms.lib.*;
import org.toxsoft.uskat.core.api.evserv.*;
import org.toxsoft.uskat.core.api.sysdescr.*;
import org.toxsoft.uskat.core.api.users.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.type.*;

/**
 * @author Slavage
 */
public class AlarmJournalPanel
    extends AbstractSkLazyControl
    implements IAlarmJournalPanel {

  /**
   * Panel displays the history of alarm events: alerts/acknowledges and .mutes/unmutes.
   * <p>
   * Panel contains:
   * <ul>
   * <li>toolbar on the top;</li>
   * <li>table-tree with selected events;</li>
   * <li>bottom pane with summary information.</li>
   * </ul>
   */

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
    }

    void doCheckAll() {
      componentModown.tree().checks().setAllItemsCheckState( true );
    }

    void doUnCheckAll() {
      componentModown.tree().checks().setAllItemsCheckState( false );
    }

    boolean isNotEmpty() {
      return !componentModown.tree().items().isEmpty();
    }
  }

  class InnerModel
      extends SkEventM5ModelBase {

    public static final String MODEL_ID = "SkAlertM5Model"; //$NON-NLS-1$

    public static final String AID_EVENT_TIMESTAMP      = "EventTimestamp";          //$NON-NLS-1$
    public static final String AID_ALARM_NAME           = "EventAlarmName";          //$NON-NLS-1$
    public static final String AID_ALARM_EVENT_MESSAGE  = "AlarmEventMessage";       //$NON-NLS-1$
    public static final String AID_EVENT_ALARM_SEVERITY = "EventAlarmSeverity";      //$NON-NLS-1$
    public static final String AID_EVENT_TYPE_NAME      = "EventTypeName";           //$NON-NLS-1$
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
            return TimeUtils.timestampToSaneString( aEntity.timestamp() );
          }

          @Override
          protected IAtomicValue doGetFieldValue( SkEvent aEntity ) {
            return AvUtils.avStr( TimeUtils.timestampToSaneString( aEntity.timestamp() ) );
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

      @Override
      protected IAtomicValue doGetFieldValue( SkEvent aEntity ) {
        ISkAlarm alarm = alarmService().findAlarm( aEntity.eventGwid().strid() );
        return AvUtils.avStr( alarm.description() );
      }
    };

    public final IM5AttributeFieldDef<SkEvent> EVENT_TYPE_NAME = new M5AttributeFieldDef<>( AID_EVENT_TYPE_NAME, STRING, //
        TSID_NAME, STR_N_EVENT_TYPE_NAME, //
        TSID_DESCRIPTION, STR_D_EVENT_TYPE_NAME, //
        TSID_DEFAULT_VALUE, avStr( NONE_ID ) //
    ) {

      @Override
      protected void doInit() {
        setFlags( M5FF_COLUMN );
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
    };

    public final IM5AttributeFieldDef<SkEvent> ALERT_EVENT_MESSAGE =
        new M5AttributeFieldDef<>( AID_ALARM_EVENT_MESSAGE, STRING, //
            TSID_NAME, STR_N_ALERT_EVENT_MESSAGE, //
            TSID_DESCRIPTION, STR_D_ALERT_EVENT_MESSAGE, //
            TSID_DEFAULT_VALUE, avStr( NONE_ID ) //
        ) {

          @Override
          protected void doInit() {
            setFlags( M5FF_COLUMN | M5FF_DETAIL );
          }

          @Override
          protected String doGetFieldValueName( SkEvent aEntity ) {
            return extractAlertMsg( aEntity );
          }

          private String extractAlertMsg( SkEvent aEntity ) {
            String retVal = EMPTY_STRING;
            // dima 12/11/24 отображаем сгенерированный в момент возникновения алерта текст
            if( aEntity.paramValues().hasKey( EVPRMID_ALERT_MESSAGE ) ) {
              retVal = aEntity.paramValues().getStr( EVPRMID_ALERT_MESSAGE );
            }
            return retVal;
          }

          @Override
          protected IAtomicValue doGetFieldValue( SkEvent aEntity ) {
            return AvUtils.avStr( extractAlertMsg( aEntity ) );
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
            if( aEntity.eventGwid().propId().equals( ISkAlarmConstants.EVID_ALERT ) ) {
              // Only for alert event.
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
            }
            return retVal;
          }

          @Override
          protected Image doGetFieldValueIcon( SkEvent aEntity, EIconSize aIconSize ) {
            Image retVal = null;
            if( aEntity.eventGwid().propId().equals( ISkAlarmConstants.EVID_ALERT ) ) {
              // Only for alert event.
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
            }
            return retVal;
          }

        };

    public final IM5MultiModownFieldDef<SkEvent, IdValue> EVENT_PARAM_VALUES =
        new M5MultiModownFieldDef<>( FID_PARAM_VALUES, IdValueM5Model.MODEL_ID ) {

          @Override
          protected void doInit() {
            setNameAndDescription( STR_N_EVENT_PARAMETERS, STR_D_EVENT_PARAMETERS );
            setFlags( M5FF_DETAIL );
          }

          protected IList<IdValue> doGetFieldValue( SkEvent aEntity ) {
            return IdValue.makeIdValuesCollFromOptionSet( aEntity.paramValues() ).values();
          }

        };

    public InnerModel( ISkConnection aConn ) {
      super( MODEL_ID, aConn );
      setNameAndDescription( ESkClassPropKind.EVENT.nmName(), ESkClassPropKind.EVENT.description() );
      addFieldDefs( EVENT_TIMESTAMP, EVENT_ALARM_SEVERITY, EVENT_TYPE_NAME, EVENT_ALARM_NAME, ALERT_EVENT_MESSAGE,
          EVENT_PARAM_VALUES );
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
    }

    @Override
    protected IList<SkEvent> doListEntities() {
      IQueryInterval interval = new QueryInterval( EQueryIntervalType.CSCE, //
          getTimeInMillis( startTime, startDate ), //
          getTimeInMillis( finishTime, finishDate ) );

      IList<ISkAlarm> allAlarms = alarmService().listAlarms();
      IListEdit<SkEvent> allEvents = new ElemLinkedBundleList<>();
      for( ISkAlarm alarm : allAlarms ) {
        ITimedList<SkEvent> events = alarm.getHistory( interval );
        allEvents.addAll( events );
      }
      IListReorderer<SkEvent> orderedEvents = new ListReorderer<>( allEvents );
      orderedEvents.sort( SkEvent::compareTo ); // The events, sortabled by timestamp.
      return orderedEvents.list();
    }
  }

  static final ITsNodeKind<String>  NK_ALARM = new TsNodeKind<>( "LeafSkAlarm", String.class, true );   //$NON-NLS-1$
  static final ITsNodeKind<SkEvent> NK_EVENT = new TsNodeKind<>( "LeafSkEvent", SkEvent.class, false ); //$NON-NLS-1$

  class CacheNode {

    ITsNode                     node;
    IMapEdit<String, CacheNode> childs = new ElemMap<String, CacheNode>();

    CacheNode( ITsNode aNode ) {
      node = aNode;
    }

    CacheNode findChild( String aNodeName ) {
      return childs.findByKey( aNodeName );
    }

    void addChild( String aNodeName, CacheNode aNode ) {
      childs.put( aNodeName, aNode );
    }
  }

  class TreeMakerByAlarm
      implements ITsTreeMaker<SkEvent> {

    @SuppressWarnings( { "unchecked", "rawtypes" } )
    @Override
    public IList<ITsNode> makeRoots( ITsNode aRootNode, IList<SkEvent> aEvents ) {
      IStringMapEdit<DefaultTsNode<String>> roots = new StringMap<>();

      CacheNode rootCacheNode = new CacheNode( aRootNode );

      for( SkEvent event : aEvents ) {
        ISkAlarm alarm = skConn().coreApi().objService().find( event.eventGwid().skid() );

        // Grouping by alarm.
        CacheNode alarmCacheNode = rootCacheNode.findChild( alarm.description() );
        if( alarmCacheNode == null ) {
          DefaultTsNode<String> alarmNode = new DefaultTsNode<>( NK_ALARM, aRootNode, alarm.description() );
          ((DefaultTsNode<String>)aRootNode).addNode( alarmNode );
          roots.put( alarm.description(), alarmNode );

          alarmCacheNode = new CacheNode( alarmNode );
          rootCacheNode.addChild( alarm.description(), alarmCacheNode );
        }

        DefaultTsNode<SkEvent> eventNode = new DefaultTsNode<SkEvent>( NK_EVENT, alarmCacheNode.node, event );
        ((DefaultTsNode<String>)alarmCacheNode.node).addNode( eventNode );
      }
      return (IList)roots.values();
    }

    @Override
    public boolean isItemNode( ITsNode aNode ) {
      return aNode.kind() == NK_EVENT;
    }
  }

  /**
   * Default items provider.
   *
   * @author hazard157
   */
  static class InternalItemsProvider
      implements IM5ItemsProvider<SkEvent> {

    private IList<SkEvent> items = new ElemArrayList<>();

    @Override
    public IGenericChangeEventer genericChangeEventer() {
      return NoneGenericChangeEventer.INSTANCE;
    }

    @Override
    public IList<SkEvent> listItems() {
      return items;
    }

    void setItems( IList<SkEvent> aItems ) {
      items = aItems;
    }
  }

  /**
   * формат для отображения метки времени
   */
  private static final String timestampFormatString = "dd.MM.yy HH:mm:ss"; //$NON-NLS-1$

  private static final DateFormat timestampFormat = new SimpleDateFormat( timestampFormatString );

  private final AspLocal asp = new AspLocal();

  private DateTime startTime  = null;
  private DateTime startDate  = null;
  private DateTime finishTime = null;
  private DateTime finishDate = null;

  private InnerModel                   innerModel;
  private IM5LifecycleManager<SkEvent> innerLifecycleManager;

  private MultiPaneComponentModown<SkEvent> componentModown;

  // private InternalItemsProvider eventProvider;

  /**
   * Constructor.
   *
   * @param aContext
   */
  public AlarmJournalPanel( ITsGuiContext aContext ) {
    super( aContext );
  }

  @Override
  protected void doDispose() {
    super.doDispose();
  }

  // ------------------------------------------------------------------------------------
  // AbstractLazyPanel

  @Override
  protected Control doCreateControl( Composite aParent ) {
    Composite board = new Composite( aParent, SWT.NONE );
    board.setLayout( new BorderLayout() );

    ITsGuiContext ctx = new TsGuiContext( tsContext() );
    ctx.params().addAll( tsContext().params() ); // !!!

    TsComposite backplane = new TsComposite( board );
    backplane.setLayout( new GridLayout( 10, false ) );
    backplane.setLayoutData( BorderLayout.NORTH );

    Label l = new Label( backplane, SWT.CENTER );
    l.setText( "Query interval:" );
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

    // Заголовок
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
        printEvents();
      }
    } );

    // Using temporary model.
    InnerModel model = new InnerModel( skConn() );
    m5().initTemporaryModel( model );

    IM5LifecycleManager<SkEvent> lm = new InnerLifecycleManager( model, skConn() );

    IMultiPaneComponentConstants.OPDEF_IS_TOOLBAR.setValue( ctx.params(), AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_IS_SUMMARY_PANE.setValue( ctx.params(), AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_IS_SUPPORTS_TREE.setValue( ctx.params(), AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( ctx.params(), AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_IS_DETAILS_PANE.setValue( ctx.params(), AV_TRUE );

    componentModown = new MultiPaneComponentModown<>( ctx, model, lm.itemsProvider(), lm );
    TreeModeInfo<SkEvent> tmiByAlarm = new TreeModeInfo<>( "ByAlarm", //$NON-NLS-1$
        "Группировать по тревогам", "Тревоги", "ByAlarm", new TreeMakerByAlarm() );
    componentModown.treeModeManager().addTreeMode( tmiByAlarm );
    componentModown.treeModeManager().setCurrentMode( "ByAlarm" ); // Default value is tree view.
    componentModown.createControl( board );
    componentModown.getControl().setLayoutData( BorderLayout.CENTER );

    innerModel = model;
    innerLifecycleManager = lm;

    return board;
  }

  // ------------------------------------------------------------------------------------
  // Implementation
  //

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

  private void printEvents() {
    try {
      ISkConnectionSupplier connectionSup = eclipseContext().get( ISkConnectionSupplier.class );
      ISkConnection connection = connectionSup.defConn();
      InnerModel printEventsModel = new InnerModel( connection );

      m5().initTemporaryModel( printEventsModel );

      ITsGuiContext printContext = new TsGuiContext( tsContext() );

      long startTime2 = getTimeInMillis( startTime, startDate );
      long endTime2 = getTimeInMillis( finishTime, finishDate );

      String PRINT_EVENT_LIST_TITLE_FORMAT = "Events of alarms from %s to %s";
      String AUTHOR_STR = "Author: ";
      String DATE_STR = "Creation date: ";

      String title = String.format( PRINT_EVENT_LIST_TITLE_FORMAT, timestampFormat.format( new Date( startTime2 ) ),
          timestampFormat.format( new Date( endTime2 ) ) );

      IJasperReportConstants.REPORT_TITLE_M5_ID.setValue( printContext.params(), AvUtils.avStr( title ) );

      // выясняем текущего пользователя

      Skid currUser = connection.coreApi().getCurrentUserInfo().userSkid();
      ISkUser user = connection.coreApi().userService().getUser( currUser.strid() );
      String userName = user.nmName().trim().length() > 0 ? user.nmName() : user.login();

      IJasperReportConstants.LEFT_BOTTOM_STR_M5_ID.setValue( printContext.params(),
          AvUtils.avStr( AUTHOR_STR + userName ) );
      IJasperReportConstants.RIGHT_BOTTOM_STR_M5_ID.setValue( printContext.params(),
          AvUtils.avStr( DATE_STR + timestampFormat.format( new Date() ) ) );

      printContext.params().setStr( IJasperReportConstants.REPORT_DATA_HORIZONTAL_TEXT_ALIGN_ID,
          HorizontalTextAlignEnum.LEFT.getName() );

      final JasperPrint jasperPrint =
          ReportGenerator.generateJasperPrint( printContext, printEventsModel, innerLifecycleManager.itemsProvider() );
      JasperReportDialog.showPrint( printContext, jasperPrint );
    }
    catch( Exception ex ) {
      LoggerUtils.errorLogger().error( ex );
      TsDialogUtils.error( getShell(), ex );
    }

  }

}
