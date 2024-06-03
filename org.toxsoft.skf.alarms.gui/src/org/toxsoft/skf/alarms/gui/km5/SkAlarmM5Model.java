package org.toxsoft.skf.alarms.gui.km5;

import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tsgui.m5.gui.mpc.IMultiPaneComponentConstants.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.alarms.lib.ISkAlarmConstants.*;

import org.toxsoft.core.tsgui.bricks.cond.valed.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.impl.*;
import org.toxsoft.core.tsgui.m5.gui.panels.*;
import org.toxsoft.core.tsgui.m5.gui.panels.impl.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tslib.math.cond.*;
import org.toxsoft.core.tslib.utils.errors.*;
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

  /**
   * Attribute {@link ISkAlarm#severity()}.
   */
  public final IM5AttributeFieldDef<ISkAlarm> SEVERITY = new KM5AttributeFieldDef<>( ATRINF_SEVERITY );

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
   * Field {@link ISkAlarm#messageInfo()}.
   */
  // public final IM5FieldDef<ISkAlarm, ISkMessageInfo> MESSAGE_INFO =
  // new M5FieldDef<>( CLBID_MESSAGE_INFO, ISkMessageInfo.class ) {
  public final IM5SingleModownFieldDef<ISkAlarm, ISkMessageInfo> MESSAGE_INFO =
      new M5SingleModownFieldDef<>( CLBID_MESSAGE_INFO, ISkAlarmConstants.CLSID_MESSAGE_INFO ) {

        @Override
        protected void doInit() {
          setName( CLBINF_MESSAGE_INFO.nmName() );
          setDescription( CLBINF_MESSAGE_INFO.description() );
          setDefaultValue( ISkMessageInfo.NONE );
          params().setBool( TSID_IS_NULL_ALLOWED, false );
          // params().setStr( M5_VALED_OPDEF_WIDGET_TYPE_ID, M5VWTID_INPLACE );
          // FIXME set VALED editor name
          setFlags( M5FF_DETAIL );
        }

        protected ISkMessageInfo doGetFieldValue( ISkAlarm aEntity ) {
          return aEntity.messageInfo();
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
    addFieldDefs( STRID, SEVERITY, NAME, DESCRIPTION, MESSAGE_INFO, ALERT_CONDITION );
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
