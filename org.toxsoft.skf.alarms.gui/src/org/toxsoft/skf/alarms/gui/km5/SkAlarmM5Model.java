package org.toxsoft.skf.alarms.gui.km5;

import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tsgui.m5.gui.mpc.IMultiPaneComponentConstants.*;
import static org.toxsoft.core.tsgui.valed.api.IValedControlConstants.*;
import static org.toxsoft.core.tslib.av.EAtomicType.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.alarms.gui.ISkResources.*;
import static org.toxsoft.skf.alarms.lib.ISkAlarmConstants.*;

import org.eclipse.jface.resource.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.ui.plugin.*;
import org.toxsoft.core.tsgui.bricks.cond.valed.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.graphics.icons.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.impl.*;
import org.toxsoft.core.tsgui.m5.gui.panels.*;
import org.toxsoft.core.tsgui.m5.gui.panels.impl.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.valed.controls.av.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.math.cond.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.alarms.gui.*;
import org.toxsoft.skf.alarms.lib.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.km5.*;
import org.toxsoft.uskat.core.utils.msgen.*;

/**
 * M5-model of {@link ISkAlarm} Sk-objects
 *
 * @author hazard157
 */
public class SkAlarmM5Model
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

  /**
   * Attribute {@link ISkAlarm#description()}.
   */
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

  /**
   * Attribute {@link ISkAlarm#severity()}.
   */
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

  /**
   * Attribute {@link ISkAlarm#isAlert()}.
   */
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

  /**
   * Attribute {@link ISkAlarm#isMuted()}.
   */
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

  /**
   * Field {@link ISkAlarm#messageInfo()}.
   */
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

  /**
   * Field {@link ISkAlarm#alertCondition()}.
   */
  public final IM5FieldDef<ISkAlarm, ITsCombiCondInfo> ALERT_CONDITION =
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

  /**
   * Constructor.
   *
   * @param aConn {@link ISkConnection} - Sk-connection to be used in constructor
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  public SkAlarmM5Model( ISkConnection aConn ) {
    super( CLSID_ALARM, ISkAlarm.class, aConn );
    addFieldDefs( STRID, ALARM_NAME, ALARM_SEVERITY, ALARM_ISALERT, ALARM_ISMUTED,
        ALERT_CONDITION/*
                        * , ALARM_MESSAGE_INFO, ALARM_ALERT_CONDITION
                        */ );
    setPanelCreator( new M5DefaultPanelCreator<>() {

      @Override
      protected IM5CollectionPanel<ISkAlarm> doCreateCollEditPanel( ITsGuiContext aContext,
          IM5ItemsProvider<ISkAlarm> aItemsProvider, IM5LifecycleManager<ISkAlarm> aLifecycleManager ) {
        OPDEF_IS_ACTIONS_CRUD.setValue( aContext.params(), AV_TRUE );
        OPDEF_IS_FILTER_PANE.setValue( aContext.params(), AV_TRUE );
        MultiPaneComponentModown<ISkAlarm> mpc = new SkAlarmMpc( aContext, model(), aItemsProvider, aLifecycleManager );
        return new M5CollectionPanelMpcModownWrapper<>( mpc, false );
      }
    } );

  }

  @Override
  protected IM5LifecycleManager<ISkAlarm> doCreateLifecycleManager( Object aMaster ) {
    return new SkAlarmM5LifecycleManager( this, ISkConnection.class.cast( aMaster ) );
  }

}
