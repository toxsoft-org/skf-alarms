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
import static org.toxsoft.skf.alarms.lib.ISkAlarmConstants.*;

import org.eclipse.jface.resource.*;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.plugin.*;
import org.toxsoft.core.tsgui.bricks.actions.*;
import org.toxsoft.core.tsgui.bricks.actions.asp.*;
import org.toxsoft.core.tsgui.bricks.cond.valed.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.ctx.impl.*;
import org.toxsoft.core.tsgui.graphics.icons.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.impl.*;
import org.toxsoft.core.tsgui.m5.gui.panels.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.panels.toolbar.*;
import org.toxsoft.core.tsgui.utils.layout.*;
import org.toxsoft.core.tsgui.valed.controls.av.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.bricks.*;
import org.toxsoft.core.tslib.bricks.validator.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.math.cond.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.skf.alarms.gui.*;
import org.toxsoft.skf.alarms.gui.incub.*;
import org.toxsoft.skf.alarms.gui.km5.*;
import org.toxsoft.skf.alarms.gui.panels.*;
import org.toxsoft.skf.alarms.lib.*;
import org.toxsoft.uskat.core.api.evserv.*;
import org.toxsoft.uskat.core.api.users.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.km5.*;
import org.toxsoft.uskat.core.utils.msgen.*;

/**
 * @author Slavage
 */
public class AlarmRtPanel
    extends AbstractSkLazyControl
    implements IAlarmRtPanel, ISkAlertListener, IRealTimeSensitive {

  private static final String ACTID_ACKNOWLEDGE = "acknowledge"; //$NON-NLS-1$
  private static final String ACTID_MUTED       = "muted";       //$NON-NLS-1$
  private static final String ACTID_UNMUTED     = "unmuted";     //$NON-NLS-1$

  private static final ITsActionDef ACDEF_ACKNOWLEDGE = TsActionDef.ofPush2( ACTID_ACKNOWLEDGE, //
      STR_N_ALARM_ACKNOWLEDGE, STR_D_ALARM_ACKNOWLEDGE, ICONID_ALERT_ACKNOWLEDGE //
  );

  private static final ITsActionDef ACDEF_MUTED = TsActionDef.ofPush2( ACTID_MUTED, //
      "Muted", "Set muted", ICONID_ARROW_DOWN //
  );

  private static final ITsActionDef ACDEF_UNMUTED = TsActionDef.ofPush2( ACTID_UNMUTED, //
      "Unmuted", "Set unmuted", ICONID_ARROW_DOWN //
  );

  /**
   * Handles user actions.
   */
  class AspLocal
      extends MethodPerActionTsActionSetProvider {

    public AspLocal() {
      defineAction( ACDEF_CHECK_ALL, this::doCheckAll, this::isNotEmpty );
      defineAction( ACDEF_UNCHECK_ALL, this::doUnCheckAll, this::isNotEmpty );
      defineSeparator();
      defineAction( ACDEF_ACKNOWLEDGE, this::doAcknowledge, this::isCheckedNotEmpty );
      defineSeparator();
      defineAction( ACDEF_MUTED, this::doMuted, this::isCheckedNotEmpty );
      defineAction( ACDEF_UNMUTED, this::doUnmuted, this::isCheckedNotEmpty );
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

    void doAcknowledge() {
      ITsValidator<String> commentValidator = aValue -> ValidationResult.SUCCESS;
      String comment = AcknowledgeDlg.enterComment( tsContext(), commentValidator );
      if( comment != null ) { // Сomment is required.
        ISkLoggedUserInfo author = skConn().coreApi().getCurrentUserInfo();
        IList<ISkAlarm> checkedAlarms = componentModown.tree().checks().listCheckedItems( true );
        for( int i = 0; i < checkedAlarms.size(); i++ ) {
          ISkAlarm alarm = checkedAlarms.get( i );
          alarm.sendAcknowledge( author.userSkid(), comment );
        }
      }
    }

    void doMuted() {
      ITsValidator<String> validator = aValue -> ValidationResult.SUCCESS;
      String reason = AcknowledgeDlg.enterComment( tsContext(), validator );
      ISkLoggedUserInfo author = skConn().coreApi().getCurrentUserInfo();
      IList<ISkAlarm> checkedAlarms = componentModown.tree().checks().listCheckedItems( true );
      for( int i = 0; i < checkedAlarms.size(); i++ ) {
        ISkAlarm alarm = checkedAlarms.get( i );
        alarm.muteAlert( author.userSkid(), reason );
      }
    }

    void doUnmuted() {
      IList<ISkAlarm> checkedAlarms = componentModown.tree().checks().listCheckedItems( true );
      for( int i = 0; i < checkedAlarms.size(); i++ ) {
        ISkAlarm alarm = checkedAlarms.get( i );
        alarm.unmuteAlert();
      }
    }

    boolean isCheckedNotEmpty() {
      return !componentModown.tree().checks().listCheckedItems( true ).isEmpty();
    }
  }

  class InnerM3Model
      extends KM5ModelBasic<ISkAlarm> {

    public static final String AID_ALARM_NAME     = "AlarmName";     //$NON-NLS-1$
    public static final String AID_ALARM_SEVERITY = "AlarmSeverity"; //$NON-NLS-1$
    public static final String AID_ALARM_ISALERT  = "AlarmIsAlert";  //$NON-NLS-1$
    public static final String AID_ALARM_ISMUTED  = "AlarmIsMuted";  //$NON-NLS-1$

    private static final ImageDescriptor imgDescrWarning =
        AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, "icons/is16x16/warningSeverityAlarm.png" ); // $NON-NLS-1$
    private static final Image           warningImage    = imgDescrWarning.createImage();

    private static final ImageDescriptor imgDescrCritical =
        AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, "icons/is16x16/criticalSeverityAlarm.png" ); // $NON-NLS-1$
    private static final Image           criticalImage    = imgDescrCritical.createImage();

    public final IM5AttributeFieldDef<ISkAlarm> ALARM_NAME = new M5AttributeFieldDef<>( AID_ALARM_NAME, STRING, //
        TSID_NAME, STR_N_ALARM_NAME, //
        TSID_DESCRIPTION, STR_D_ALARM_NAME, //
        TSID_DEFAULT_VALUE, avStr( NONE_ID ) //
    ) {

      @Override
      protected void doInit() {
        setFlags( M5FF_COLUMN );
      }

      @Override
      protected String doGetFieldValueName( ISkAlarm aAlarm ) {
        return aAlarm.description();
      }
    };

    public M5AttributeFieldDef<ISkAlarm> ALARM_SEVERITY =
        new M5AttributeFieldDef<>( AID_ALARM_SEVERITY, EAtomicType.STRING, //
            TSID_NAME, STR_N_ALERT_SEVERITY, //
            TSID_DESCRIPTION, STR_D_ALERT_SEVERITY, //
            OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME //
        ) {

          @Override
          protected void doInit() {
            setFlags( M5FF_COLUMN );
          }

          protected IAtomicValue doGetFieldValue( ISkAlarm aAlarm ) {
            IAtomicValue retVal = avStr( TsLibUtils.EMPTY_STRING );
            switch( aAlarm.severity() ) {
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
          protected Image doGetFieldValueIcon( ISkAlarm aAlarm, EIconSize aIconSize ) {
            Image retVal = warningImage;
            switch( aAlarm.severity() ) {
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

    public final IM5AttributeFieldDef<ISkAlarm> ALARM_ISALERT = new M5AttributeFieldDef<>( AID_ALARM_ISALERT, BOOLEAN, //
        TSID_NAME, STR_N_ALARM_ISALERT, //
        TSID_DESCRIPTION, STR_D_ALARM_ISALERT, //
        TSID_DEFAULT_VALUE, avStr( NONE_ID ) //
    ) {

      @Override
      protected void doInit() {
        setFlags( M5FF_COLUMN );
      }

      @Override
      protected String doGetFieldValueName( ISkAlarm aAlarm ) {
        return Boolean.toString( aAlarm.isAlert() );
      }
    };

    public final IM5AttributeFieldDef<ISkAlarm> ALARM_ISMUTED = new M5AttributeFieldDef<>( AID_ALARM_ISMUTED, BOOLEAN, //
        TSID_NAME, STR_N_ALARM_ISMUTED, //
        TSID_DESCRIPTION, STR_D_ALARM_ISMUTED, //
        TSID_DEFAULT_VALUE, avStr( NONE_ID ) //
    ) {

      @Override
      protected void doInit() {
        setFlags( M5FF_COLUMN );
      }

      @Override
      protected String doGetFieldValueName( ISkAlarm aAlarm ) {
        return Boolean.toString( aAlarm.isMuted() );
      }
    };

    public final IM5SingleModownFieldDef<ISkAlarm, ISkMessageInfo> ALARM_MESSAGE_INFO =
        new M5SingleModownFieldDef<>( CLBID_MESSAGE_INFO, SkMessageInfoM5Model.MODEL_ID ) {

          @Override
          protected void doInit() {
            setName( CLBINF_MESSAGE_INFO.nmName() );
            setDescription( CLBINF_MESSAGE_INFO.description() );
            setDefaultValue( ISkMessageInfo.NONE );
            params().setBool( TSID_IS_NULL_ALLOWED, false );
            setFlags( M5FF_DETAIL );
          }

          protected ISkMessageInfo doGetFieldValue( ISkAlarm aEntity ) {
            return aEntity.messageInfo();
          }

        };

    public final IM5FieldDef<ISkAlarm, ITsCombiCondInfo> ALARM_ALERT_CONDITION =
        new M5FieldDef<>( CLBID_ALERT_CONDITION, ITsCombiCondInfo.class ) {

          @Override
          protected void doInit() {
            setName( CLBINF_ALERT_CONDITION.nmName() );
            setDescription( CLBINF_ALERT_CONDITION.description() );
            setDefaultValue( ITsCombiCondInfo.NEVER );
            setValedEditor( ValedCombiCondInfo.FACTORY_NAME );
            ISkAlarmService alarmService = coreApi().getService( ISkAlarmService.SERVICE_ID );
            ITsConditionsTopicManager tm = alarmService.getAlarmCheckersTopicManager();
            valedRefs().put( ValedCombiCondInfo.REFDEF_TOPIC_MANAGER.refKey(), tm );
            setFlags( M5FF_DETAIL );
          }

          protected ITsCombiCondInfo doGetFieldValue( ISkAlarm aEntity ) {
            return aEntity.alertCondition();
          }

        };

    public InnerM3Model( ISkConnection aConn ) {
      super( "LocalM3Model", ISkAlarm.class, aConn );
      addFieldDefs( STRID, ALARM_NAME, ALARM_SEVERITY, ALARM_ISALERT, ALARM_ISMUTED, ALARM_MESSAGE_INFO,
          ALARM_ALERT_CONDITION );
    }

    @Override
    protected IM5LifecycleManager<ISkAlarm> doCreateLifecycleManager( Object aMaster ) {
      return new InnerM5LifecycleManager( this, ISkConnection.class.cast( aMaster ) );
    }
  }

  class InnerM5LifecycleManager
      extends KM5LifecycleManagerBasic<ISkAlarm> {

    public InnerM5LifecycleManager( IM5Model<ISkAlarm> aModel, ISkConnection aMaster ) {
      super( aModel, false, false, false, true, aMaster );
    }

    @Override
    protected IList<ISkAlarm> doListEntities() {
      IList<ISkAlarm> allAlarms = alarmService().listAlarms();
      return allAlarms;
    }
  }

  private AspLocal asp;

  private MultiPaneComponentModown<ISkAlarm> componentModown;
  private IM5CollectionPanel<ISkAlarm>       alarmsPanel;

  private boolean paused = false;

  public AlarmRtPanel( ITsGuiContext aContext ) {
    super( aContext );

    asp = new AspLocal();

    // Listen to the alert/acknowledge events.
    alarmService().addAlertListener( this );
  }

  @Override
  protected void doDispose() {
    guiTimersService().slowTimers().removeListener( this );

    alarmService().removeAlertListener( this );

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

    TsToolbar toolbar = TsToolbar.create( board, ctx, asp.listAllActionDefs() );
    toolbar.getControl().setLayoutData( BorderLayout.NORTH );
    toolbar.addListener( asp );

    // Using temporary model.
    InnerM3Model model = new InnerM3Model( skConn() );
    m5().initTemporaryModel( model );
    // IM5Model<ISkAlarm> model = m5().getModel( ISkAlarmConstants.CLSID_ALARM, ISkAlarm.class );

    IM5LifecycleManager<ISkAlarm> lm = new InnerM5LifecycleManager( model, skConn() );
    // IM5LifecycleManager<ISkAlarm> lm = new SkAlarmM5LifecycleManager( model, skConn() );

    IMultiPaneComponentConstants.OPDEF_IS_TOOLBAR.setValue( ctx.params(), AV_FALSE );
    IMultiPaneComponentConstants.OPDEF_DETAILS_PANE_PLACE.setValue( ctx.params(),
        avValobj( EBorderLayoutPlacement.SOUTH ) );
    IMultiPaneComponentConstants.OPDEF_IS_COLUMN_HEADER.setValue( ctx.params(), AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_IS_SUMMARY_PANE.setValue( ctx.params(), AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_IS_SUPPORTS_CHECKS.setValue( ctx.params(), AvUtils.AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_IS_ACTIONS_CRUD.setValue( ctx.params(), AvUtils.AV_FALSE );
    IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );
    // IMultiPaneComponentConstants.OPDEF_IS_ACTIONS_HIDE_PANES.setValue( ctx.params(), AvUtils.AV_TRUE );

    componentModown = new MultiPaneComponentModown<>( ctx, model, lm.itemsProvider(), lm );
    componentModown.createControl( board );

    guiTimersService().slowTimers().addListener( this );

    return board;
  }

  // ------------------------------------------------------------------------------------
  // IAlarmRtPanel

  @Override
  public boolean isPaused() {
    return paused;
  }

  @Override
  public void pause() {
    paused = true;
  }

  @Override
  public void resume() {
    paused = false;
  }

  // ------------------------------------------------------------------------------------
  // ISkAlertListener

  @Override
  public void onAlert( SkEvent aEvent ) {
    componentModown.tree().refresh();
  }

  @Override
  public void onAcknowledge( SkEvent aEvent ) {
    componentModown.tree().refresh();
  }

  // ------------------------------------------------------------------------------------
  // IRealTimeSensitive

  @Override
  public void whenRealTimePassed( long aRtTime ) {
    if( paused ) {
      return;
    }
    // componentModown.tree().refresh();
  }

  // ------------------------------------------------------------------------------------
  // Implementation

  private ISkAlarmService alarmService() {
    return coreApi().getService( ISkAlarmService.SERVICE_ID );
  }

}
