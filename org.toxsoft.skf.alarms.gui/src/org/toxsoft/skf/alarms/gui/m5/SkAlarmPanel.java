package org.toxsoft.skf.alarms.gui.m5;

import static org.toxsoft.core.tsgui.bricks.actions.ITsStdActionDefs.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.skf.alarms.gui.m5.ISkResources.*;
import static org.toxsoft.skf.alarms.lib.ISkAlarmHardConstants.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.toxsoft.core.tsgui.bricks.actions.ITsActionDef;
import org.toxsoft.core.tsgui.bricks.actions.TsActionDef;
import org.toxsoft.core.tsgui.bricks.ctx.ITsGuiContext;
import org.toxsoft.core.tsgui.bricks.ctx.impl.TsGuiContext;
import org.toxsoft.core.tsgui.bricks.tstree.impl.TsTreeViewer;
import org.toxsoft.core.tsgui.graphics.icons.EIconSize;
import org.toxsoft.core.tsgui.graphics.icons.ITsStdIconIds;
import org.toxsoft.core.tsgui.m5.IM5Model;
import org.toxsoft.core.tsgui.m5.gui.mpc.IMultiPaneComponentConstants;
import org.toxsoft.core.tsgui.m5.gui.mpc.impl.MultiPaneComponentModown;
import org.toxsoft.core.tsgui.m5.gui.panels.IM5CollectionPanel;
import org.toxsoft.core.tsgui.m5.gui.panels.impl.M5CollectionPanelMpcModownWrapper;
import org.toxsoft.core.tsgui.m5.gui.viewers.IM5Column;
import org.toxsoft.core.tsgui.panels.TsPanel;
import org.toxsoft.core.tsgui.panels.toolbar.ITsToolbar;
import org.toxsoft.core.tsgui.utils.layout.BorderLayout;
import org.toxsoft.core.tsgui.utils.layout.EBorderLayoutPlacement;
import org.toxsoft.core.tslib.av.impl.AvUtils;
import org.toxsoft.core.tslib.av.opset.IOptionSet;
import org.toxsoft.core.tslib.av.opset.impl.OptionSet;
import org.toxsoft.core.tslib.bricks.events.change.IGenericChangeListener;
import org.toxsoft.core.tslib.bricks.time.ITimeInterval;
import org.toxsoft.core.tslib.coll.IList;
import org.toxsoft.core.tslib.coll.IListEdit;
import org.toxsoft.core.tslib.gw.gwid.Gwid;
import org.toxsoft.core.tslib.gw.skid.Skid;
import org.toxsoft.core.tslib.utils.errors.TsNotAllEnumsUsedRtException;
import org.toxsoft.core.tslib.utils.logs.impl.LoggerUtils;
import org.toxsoft.skf.alarms.gui.ISkAlarmGuiConstants;
import org.toxsoft.skf.alarms.gui.SkAlarmCmdUtils;
import org.toxsoft.skf.alarms.lib.*;
import org.toxsoft.uskat.core.api.cmdserv.*;
import org.toxsoft.uskat.core.api.sysdescr.ISkClassInfo;
import org.toxsoft.uskat.core.api.sysdescr.dto.IDtoCmdInfo;
import org.toxsoft.uskat.core.connection.ISkConnection;

/**
 * Панель отображения и квитирования списка Алармов
 *
 * @author max
 * @author dima // функционал команд квитирования
 */
public class SkAlarmPanel
    extends TsPanel
    implements IGenericChangeListener {

  private Map<String, ISkAlarm> cmd2Alarm = new HashMap<>();

  final static String ACTID_QUIT_ALARM   = SK_ID + ".alarm.vj.QuitAlarm";   //$NON-NLS-1$
  final static String ACTID_REMOVE_ALARM = SK_ID + ".alarm.vj.RemoveAlarm"; //$NON-NLS-1$

  private MultiPaneComponentModown<ISkAlarm> componentModown;

  final static TsActionDef ACDEF_QUIT_ALARM = TsActionDef.ofPush2( ACTID_QUIT_ALARM, STR_N_QUIT_ALARMS,
      STR_D_QUIT_ALARMS, ISkAlarmGuiConstants.ICONID_ALARM_QUIT );

  final static TsActionDef ACDEF_REMOVE_ALARM = TsActionDef.ofPush2( ACTID_REMOVE_ALARM, STR_N_REMOVE_ALARMS,
      STR_D_REMOVE_ALARMS, ITsStdIconIds.ICONID_LIST_REMOVE );

  final static String ALARM_QUIT_CMD_OP_ID = SK_ID + ".alarm.vj.quit.cmd"; //$NON-NLS-1$

  IM5CollectionPanel<ISkAlarm> alarmPanel;

  private final ISkConnection skConn;
  // мелодию аларма
  private SoundPlayer     player;
  private ISkAlarmService alarmService;

  /**
   * Конструктор.
   *
   * @param aParent Composite - родительская компонента
   * @param aSkConn ISkConnection - соединение с сервером
   * @param aCtx ITsGuiContext - соответствующий контекст
   */
  public SkAlarmPanel( Composite aParent, ISkConnection aSkConn, ITsGuiContext aCtx ) {
    super( aParent, aCtx );

    skConn = aSkConn;
    BorderLayout bl = new BorderLayout();

    bl.setHgap( 0 );
    bl.setVgap( 0 );
    aParent.setLayout( bl );
    this.setLayout( bl );

    if( !skConn.coreApi().services().hasKey( ISkAlarmService.SERVICE_ID ) ) {
      return;
    }
    player = new SoundPlayer( "sound/train.wav" ); //$NON-NLS-1$

    alarmService = (ISkAlarmService)skConn.coreApi().services().getByKey( ISkAlarmService.SERVICE_ID );

    IM5Model<ISkAlarm> model = m5().getModel( SkAlarmM5Model.MODEL_ID, ISkAlarm.class );

    SkAlarmM5LifecycleManager lm = new SkAlarmM5LifecycleManager( model, alarmService );
    lm.setInterval( ITimeInterval.WHOLE );
    lm.setFilter( SkAlarmM5LifecycleManager.FILTER1 );
    ITsGuiContext ctx = new TsGuiContext( aCtx );
    ctx.params().addAll( aCtx.params() );
    IMultiPaneComponentConstants.OPDEF_IS_DETAILS_PANE.setValue( ctx.params(), AvUtils.AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_DETAILS_PANE_PLACE.setValue( ctx.params(),
        avValobj( EBorderLayoutPlacement.SOUTH ) );
    IMultiPaneComponentConstants.OPDEF_IS_SUPPORTS_CHECKS.setValue( ctx.params(), AvUtils.AV_TRUE );
    IMultiPaneComponentConstants.OPDEF_IS_ACTIONS_CRUD.setValue( ctx.params(), AvUtils.AV_FALSE );
    // прячем фильтр
    IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( ctx.params(), AvUtils.AV_FALSE );

    // SashForm sf = new SashForm( aParent, SWT.HORIZONTAL );
    TsTreeViewer.OPDEF_IS_HEADER_SHOWN.setValue( ctx.params(), AvUtils.AV_FALSE );
    componentModown = new MultiPaneComponentModown<>( ctx, model, lm.itemsProvider(), lm ) {

      @Override
      protected ITsToolbar doCreateToolbar( ITsGuiContext aContext, String aName, EIconSize aIconSize,
          IListEdit<ITsActionDef> aActs ) {
        aActs.add( ACDEF_SEPARATOR );
        aActs.add( ACDEF_QUIT_ALARM );
        aActs.add( ACDEF_REMOVE_ALARM );

        ITsToolbar toolbar =

            super.doCreateToolbar( aContext, aName, aIconSize, aActs );

        // toolbar.addListener( aActionId -> { } );

        return toolbar;
      }

      @Override
      protected void doProcessAction( String aActionId ) {

        switch( aActionId ) {
          case ACTID_QUIT_ALARM: {
            IList<ISkAlarm> selAlarms = tree().checks().listCheckedItems( true );
            // if( TsDialogUtils.askYesNoCancel( getShell(), STR_N_ASK_USER_QUIT_ALARMS ) != ETsDialogCode.YES ) {
            // return;
            // }

            for( ISkAlarm alarm : selAlarms ) {
              // команда на квитирирование
              if( hasQuitCommand( alarm ) ) {
                Gwid quitCmd = quitCommand( alarm );
                ISkCommand cmd = sendCommand( quitCmd );
                // запомним к какому аларму команда квитирования
                cmd2Alarm.put( cmd.instanceId(), alarm );
              }
              // удаляем после подтверждения успешного исполнения команды
              // lifecycleManager().remove( alarm );
            }
            refresh();
            break;
          }
          case ACTID_REMOVE_ALARM: {
            IList<ISkAlarm> selAlarms = tree().checks().listCheckedItems( true );
            for( ISkAlarm alarm : selAlarms ) {
              lifecycleManager().remove( alarm );
            }
            componentModown.refresh();
            if( componentModown.tree().items().size() == 0 ) {
              player.stop();
            }
            break;
          }

          default:
            throw new TsNotAllEnumsUsedRtException( aActionId );
        }
      }

    };
    alarmPanel = new M5CollectionPanelMpcModownWrapper<>( componentModown, false );

    alarmPanel.createControl( this );
    // dima 30.01.23 начинаем звонить, если есть хоть один аларм
    if( !lm.doListEntities().isEmpty() ) {
      player.start();
    }

    // dima 17.01.23 под Win ширина колонки маленькая, уширим
    IM5Column<ISkAlarm> messageColumn =
        componentModown.tree().columnManager().columns().findByKey( SkAlarmM5Model.FID_MESSAGE );
    messageColumn.setWidth( 600 );

    alarmService.eventer().addListener( new ISkAlarmServiceListener() {

      @Override
      public void onAlarm( ISkAlarm aSkAlarm ) {
        Display display = aCtx.get( Display.class );
        display.asyncExec( () -> {
          componentModown.refresh();
          player.start();
        } );
      }
    } );
  }

  /**
   * Отправка команды ассоциированной с аламом
   *
   * @param aCmdGwid id команды
   * @return true - команда успешно отправлена, иначе false
   */
  private ISkCommand sendCommand( Gwid aCmdGwid ) {

    ISkCommandService cmdService = skConn.coreApi().cmdService();
    Skid currUser = skConn.coreApi().getCurrentUserInfo().userSkid();

    // dima 01.03.2023 пока исходим из предположения, что все команды квитирования имеют один булевый аргумент
    ISkClassInfo classInfo = skConn.coreApi().sysdescr().findClassInfo( aCmdGwid.classId() );
    IDtoCmdInfo cmdInfo = classInfo.cmds().list().getByKey( aCmdGwid.propId() );
    String argId = cmdInfo.argDefs().first().id();

    OptionSet cmdArgs = new OptionSet();
    cmdArgs.setValue( argId, AvUtils.avBool( true ) );

    ISkCommand cmd = cmdService.sendCommand( aCmdGwid, currUser, cmdArgs );
    SkAlarmCmdUtils.logCommandHistory( cmd );

    cmd.stateEventer().addListener( this );
    return cmd;
  }

  private boolean hasQuitCommand( ISkAlarm aAlarm ) {
    IOptionSet alarmParams = alarmParams( aAlarm );
    return alarmParams.hasKey( OP_ACKNOWLEDGMENT_CMD.id() );
  }

  private Gwid quitCommand( ISkAlarm aAlarm ) {
    IOptionSet alarmParams = alarmParams( aAlarm );
    Gwid alarDefCmdGwid = alarmParams.getValobj( OP_ACKNOWLEDGMENT_CMD.id() );
    Gwid retVal = Gwid.createCmd( alarDefCmdGwid.classId(), aAlarm.authorId().strid(), alarDefCmdGwid.propId() );
    return retVal;
  }

  private IOptionSet alarmParams( ISkAlarm aAlarm ) {
    ISkAlarmDef alarmDef = alarmService.findAlarmDef( aAlarm.alarmDefId() );
    return alarmDef.params();
  }

  @Override
  public void onGenericChangeEvent( Object aSource ) {
    ISkCommand cmd = (ISkCommand)aSource;
    SkCommandState cmdState = cmd.state();
    switch( cmdState.state() ) {
      case EXECUTING:
        break;
      case SENDING:
        break;
      case FAILED:
        cmd.stateEventer().removeListener( this );
        break;
      case SUCCESS:
        cmd.stateEventer().removeListener( this );
        // после успешного выполнения команды удаляем аларм из списка
        ISkAlarm alarm = getAlarm( cmd );
        componentModown.lifecycleManager().remove( alarm );
        componentModown.tree().items().remove( alarm );
        cmd2Alarm.remove( cmd.instanceId() );
        componentModown.refresh();
        if( componentModown.tree().items().size() == 0 ) {
          player.stop();
        }
        break;
      case TIMEOUTED:
        cmd.stateEventer().removeListener( this );
        cmd2Alarm.remove( cmd.instanceId() );
        break;
      case UNHANDLED:
        cmd.stateEventer().removeListener( this );
        cmd2Alarm.remove( cmd.instanceId() );
        break;
      default:
        throw new TsNotAllEnumsUsedRtException();
    }
    LoggerUtils.errorLogger().info( "command %s state changed %s", cmd.cmdGwid(), cmdState.state() ); //$NON-NLS-1$
  }

  private ISkAlarm getAlarm( ISkCommand aCmd ) {
    return cmd2Alarm.get( aCmd.instanceId() );
  }

}
