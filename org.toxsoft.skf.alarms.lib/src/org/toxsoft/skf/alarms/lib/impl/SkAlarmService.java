package org.toxsoft.skf.alarms.lib.impl;

import static org.toxsoft.skf.alarms.lib.ISkAlarmConstants.*;
import static org.toxsoft.skf.alarms.lib.l10n.ISkAlarmSharedResources.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;

import org.toxsoft.core.tslib.bricks.ctx.*;
import org.toxsoft.core.tslib.bricks.events.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.*;
import org.toxsoft.core.tslib.bricks.validator.*;
import org.toxsoft.core.tslib.bricks.validator.impl.*;
import org.toxsoft.core.tslib.bricks.validator.std.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.helpers.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.core.tslib.math.cond.checker.*;
import org.toxsoft.core.tslib.math.cond.impl.*;
import org.toxsoft.core.tslib.utils.diff.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.alarms.lib.*;
import org.toxsoft.uskat.core.*;
import org.toxsoft.uskat.core.api.evserv.*;
import org.toxsoft.uskat.core.api.objserv.*;
import org.toxsoft.uskat.core.api.rtdserv.*;
import org.toxsoft.uskat.core.devapi.*;
import org.toxsoft.uskat.core.impl.*;
import org.toxsoft.uskat.core.impl.dto.*;
import org.toxsoft.uskat.core.utils.msgen.*;

/**
 * {@link ISkAlarmService} implementation.
 *
 * @author hazard157
 */
public class SkAlarmService
    extends AbstractSkService
    implements ISkAlarmService {

  /**
   * Service creator singleton.
   */
  public static final ISkServiceCreator<AbstractSkService> CREATOR = SkAlarmService::new;

  /**
   * {@link ISkAlarmService#svs()} implementation.
   *
   * @author hazard157
   */
  private class Svs
      extends AbstractTsValidationSupport<ISkAlarmServiceValidator>
      implements ISkAlarmServiceValidator {

    @Override
    public ISkAlarmServiceValidator validator() {
      return this;
    }

    @Override
    public ValidationResult canCreateAlarm( IDtoAlarm aAlarmInfo ) {
      TsNullArgumentRtException.checkNull( aAlarmInfo );
      ValidationResult vr = ValidationResult.SUCCESS;
      for( ISkAlarmServiceValidator v : validatorsList() ) {
        vr = ValidationResult.firstNonOk( vr, v.canCreateAlarm( aAlarmInfo ) );
        if( vr.isError() ) {
          break;
        }
      }
      return vr;
    }

    @Override
    public ValidationResult canEditAlarm( IDtoAlarm aAlarmInfo, ISkAlarm aExistingAlarm ) {
      TsNullArgumentRtException.checkNulls( aAlarmInfo, aExistingAlarm );
      ValidationResult vr = ValidationResult.SUCCESS;
      for( ISkAlarmServiceValidator v : validatorsList() ) {
        vr = ValidationResult.firstNonOk( vr, v.canEditAlarm( aAlarmInfo, aExistingAlarm ) );
        if( vr.isError() ) {
          break;
        }
      }
      return vr;
    }

    @Override
    public ValidationResult canRemoveAlarm( String aAlarmId ) {
      TsNullArgumentRtException.checkNull( aAlarmId );
      ValidationResult vr = ValidationResult.SUCCESS;
      for( ISkAlarmServiceValidator v : validatorsList() ) {
        vr = ValidationResult.firstNonOk( vr, v.canRemoveAlarm( aAlarmId ) );
        if( vr.isError() ) {
          break;
        }
      }
      return vr;
    }

  }

  /**
   * {@link ISkAlarmService#serviceEventer()} implementation.
   *
   * @author hazard157
   */
  private class Eventer
      extends AbstractTsEventer<ISkAlarmServiceListener> {

    private ECrudOp op      = null;
    private String  alarmId = null;

    @Override
    protected boolean doIsPendingEvents() {
      return op != null;
    }

    @Override
    protected void doFirePendingEvents() {
      reallyFire( op, alarmId );
    }

    @Override
    protected void doClearPendingEvents() {
      op = null;
    }

    void reallyFire( ECrudOp aOp, String aAlarmId ) {
      for( ISkAlarmServiceListener l : listeners() ) {
        l.onAlarmDefinition( SkAlarmService.this, aOp, aAlarmId );
      }
    }

    void fireAlarmDefinition( ECrudOp aOp, String aAlarmId ) {
      if( isFiringPaused() ) {
        if( op == null ) { // first event to remember
          op = aOp;
          alarmId = aAlarmId;
        }
        else { // second and further events generate LIST event
          op = ECrudOp.LIST;
          alarmId = null;
        }
      }
      else {
        reallyFire( aOp, aAlarmId );
      }
    }

  }

  private final IListEdit<ISkAlertListener>       alertListeners = new ElemLinkedBundleList<>();
  private final TsCheckerTopicManager<ISkCoreApi> topicManager   = new TsCheckerTopicManager<>( ISkCoreApi.class );

  private final Svs     svs     = new Svs();
  private final Eventer eventer = new Eventer();

  private final ClassClaimingCoreValidator claimingValidator = new ClassClaimingCoreValidator();

  private final IStringMapEdit<ISkReadCurrDataChannel>  chReadAlerts  = new StringMap<>();
  private final IStringMapEdit<ISkWriteCurrDataChannel> chWriteAlerts = new StringMap<>();
  private final IStringMapEdit<ISkReadCurrDataChannel>  chReadMutes   = new StringMap<>();
  private final IStringMapEdit<ISkWriteCurrDataChannel> chWriteMutes  = new StringMap<>();

  /**
   * Translates events from event service to the {@link ISkAlertListener}.
   */
  private final ISkEventHandler eventsHandler = aEvents -> {
    if( alertListeners.isEmpty() ) {
      return;
    }
    IList<ISkAlertListener> ll = new ElemArrayList<>( alertListeners );
    for( SkEvent e : aEvents ) {
      switch( e.eventGwid().propId() ) {
        case EVID_ALERT: {
          for( ISkAlertListener l : ll ) {
            l.onAlert( e );
          }
          break;
        }
        case EVID_ACKNOWLEDGE: {
          for( ISkAlertListener l : ll ) {
            l.onAcknowledge( e );
          }
          break;
        }
        default: // other events are not handled
          break;
      }
    }
  };

  /**
   * Translates objects change events to the {@link ISkAlarmServiceListener}.
   */
  private final ISkObjectServiceListener objectServiceListener = ( aCoreApi, aOp, aSkid ) -> {
    if( aSkid == null ) {
      reopenAllRtdataChannels();
      eventer.fireAlarmDefinition( ECrudOp.LIST, null );
      return;
    }
    switch( aOp ) {
      case CREATE:
      case EDIT:
      case REMOVE: {
        if( aSkid.classId().equals( CLSID_ALARM ) ) {
          reopenAllRtdataChannels();
          eventer.fireAlarmDefinition( aOp, aSkid.strid() );
        }
        break;
      }
      case LIST: {
        break; // already processed when aSkid = null
      }
      default:
        throw new TsNotAllEnumsUsedRtException();
    }
  };

  /**
   * Builtin validator.
   */
  private final ISkAlarmServiceValidator builtinValidator = new ISkAlarmServiceValidator() {

    @Override
    public ValidationResult canCreateAlarm( IDtoAlarm aAlarmInfo ) {
      ValidationResult vr = NameStringValidator.VALIDATOR.validate( aAlarmInfo.nmName() );
      return vr;
    }

    @Override
    public ValidationResult canEditAlarm( IDtoAlarm aAlarmInfo, ISkAlarm aExistingAlarm ) {
      ValidationResult vr = NameStringValidator.VALIDATOR.validate( aAlarmInfo.nmName() );
      return vr;
    }

    @Override
    public ValidationResult canRemoveAlarm( String aAlarmId ) {
      if( findAlarm( aAlarmId ) == null ) {
        return ValidationResult.warn( FMT_NO_ALARM_ID_TO_REMOVE, aAlarmId );
      }
      return ValidationResult.SUCCESS;
    }

  };

  /**
   * Constructor.
   *
   * @param aCoreApi {@link IDevCoreApi} - owner core API implementation
   */
  public SkAlarmService( IDevCoreApi aCoreApi ) {
    super( SERVICE_ID, aCoreApi );
    svs.addValidator( builtinValidator );
  }

  // ------------------------------------------------------------------------------------
  // AbstractSkService
  //

  @Override
  protected void doInit( ITsContextRo aArgs ) {
    // create class for ISkAlarm
    sysdescr().defineClass( CLSINF_ALARM );
    objServ().registerObjectCreator( CLSID_ALARM, SkAlarm.CREATOR );
    // claim on self classes
    sysdescr().svs().addValidator( claimingValidator );
    objServ().svs().addValidator( claimingValidator );
    linkService().svs().addValidator( claimingValidator );
    clobService().svs().addValidator( claimingValidator );
    // listen to ALERT and ACKNOWLEDGE events to fire ISkAlertListener.onXxx() events
    Gwid gwidAllAlerts = Gwid.createEvent( CLSID_ALARM, Gwid.STR_MULTI_ID, EVID_ALERT );
    Gwid gwidAllAcks = Gwid.createEvent( CLSID_ALARM, Gwid.STR_MULTI_ID, EVID_ACKNOWLEDGE );
    eventService().registerHandler( new GwidList( gwidAllAcks, gwidAllAlerts ), eventsHandler );
    // listen to object service to fire ISkAlarmServiceListener.onXxx() events
    objServ().eventer().addListener( objectServiceListener );
    // initialize RTdata: ALERT and MUTE for all alarms
    reopenAllRtdataChannels();
  }

  @Override
  protected void doClose() {
    closeChannels( chReadAlerts );
    closeChannels( chWriteAlerts );
    closeChannels( chReadMutes );
    closeChannels( chWriteMutes );
  }

  @Override
  protected boolean doIsClassClaimedByService( String aClassId ) {
    return switch( aClassId ) {
      case ISkAlarmConstants.CLSID_ALARM -> true;
      default -> false;
    };
  }

  // ------------------------------------------------------------------------------------
  // implementation
  //

  private static IDtoFullObject makeDtoFullObject( IDtoAlarm aAlarmInfo ) {
    TsNullArgumentRtException.checkNull( aAlarmInfo );
    Skid skid = new Skid( CLSID_ALARM, aAlarmInfo.id() );
    DtoFullObject dto = new DtoFullObject( skid );
    dto.attrs().setStr( AID_NAME, aAlarmInfo.nmName() );
    dto.attrs().setStr( AID_DESCRIPTION, aAlarmInfo.description() );
    dto.attrs().setValobj( ATRID_SEVERITY, aAlarmInfo.severity() );
    dto.clobs().put( CLBID_ALERT_CONDITION, TsCombiCondInfo.KEEPER.ent2str( aAlarmInfo.alertCondition() ) );
    dto.clobs().put( CLBID_MESSAGE_INFO, SkMessageInfo.KEEPER.ent2str( aAlarmInfo.messageInfo() ) );
    return dto;
  }

  private void pauseCoreValidation() {
    sysdescr().svs().pauseValidator( claimingValidator );
    objServ().svs().pauseValidator( claimingValidator );
    linkService().svs().pauseValidator( claimingValidator );
    clobService().svs().pauseValidator( claimingValidator );
  }

  private void resumeCoreValidation() {
    sysdescr().svs().resumeValidator( claimingValidator );
    objServ().svs().resumeValidator( claimingValidator );
    linkService().svs().resumeValidator( claimingValidator );
    clobService().svs().resumeValidator( claimingValidator );
  }

  /**
   * Brings channels for ALERT and MUTED RTdata up do date with the current list of alarms.
   * <p>
   * Removes channels for removed alarms, adds for new alarms.
   */
  private void reopenAllRtdataChannels() {
    // determine difference between old channels and new (according to current list of alarms)
    IStringList oldAlarmStrids = new SortedStringLinkedBundleList( chReadAlerts.keys() );
    IStringList newAlarmStrids = new SortedStringLinkedBundleList( listAlarms().ids() );
    IMapEdit<EDiffNature, IListEdit<String>> diff = DiffUtils.compareLists( oldAlarmStrids, newAlarmStrids );
    IList<String> stridsToRemove = diff.findByKey( EDiffNature.LEFT );
    IList<String> stridsToAdd = diff.findByKey( EDiffNature.RIGHT );
    // remove channels for removed alarms
    for( String alarmId : stridsToRemove ) {
      chReadAlerts.removeByKey( alarmId ).close();
      chWriteAlerts.removeByKey( alarmId ).close();
      chReadMutes.removeByKey( alarmId ).close();
      chWriteMutes.removeByKey( alarmId ).close();
    }
    // add channels, step 1: create GWIDs lists
    GwidList glAlerts = new GwidList();
    GwidList glMutes = new GwidList();
    for( String alarmId : stridsToAdd ) {
      glAlerts.add( Gwid.createRtdata( CLSID_ALARM, alarmId, RTDID_IS_ALERT ) );
      glMutes.add( Gwid.createRtdata( CLSID_ALARM, alarmId, RTDID_IS_MUTED ) );
    }
    IMap<Gwid, ISkReadCurrDataChannel> mapR;
    IMap<Gwid, ISkWriteCurrDataChannel> mapW;
    // add channels, step 2: open and add ALERT channels
    mapR = rtdService().createReadCurrDataChannels( glAlerts );
    mapW = rtdService().createWriteCurrDataChannels( glAlerts );
    for( Gwid g : mapR.keys() ) {
      chReadAlerts.put( g.strid(), mapR.getByKey( g ) );
      chWriteAlerts.put( g.strid(), mapW.getByKey( g ) );
    }
    // add channels, step 3: open and add MUTED channels
    mapR = rtdService().createReadCurrDataChannels( glMutes );
    mapW = rtdService().createWriteCurrDataChannels( glMutes );
    for( Gwid g : mapR.keys() ) {
      chReadMutes.put( g.strid(), mapR.getByKey( g ) );
      chWriteMutes.put( g.strid(), mapW.getByKey( g ) );
    }
  }

  private static void closeChannels( IStringMapEdit<? extends ISkRtdataChannel> aMap ) {
    while( !aMap.isEmpty() ) {
      aMap.removeByKey( aMap.keys().first() ).close();
    }
  }

  private void addRtdataChannelsForAlarm( String aAlarmId ) {
    IMap<Gwid, ISkReadCurrDataChannel> mapR;
    IMap<Gwid, ISkWriteCurrDataChannel> mapW;
    // add ALERT channels
    Gwid gwidAlert = Gwid.createRtdata( CLSID_ALARM, aAlarmId, RTDID_IS_ALERT );
    mapR = rtdService().createReadCurrDataChannels( new GwidList( gwidAlert ) );
    chReadAlerts.put( aAlarmId, mapR.values().first() );
    mapW = rtdService().createWriteCurrDataChannels( new GwidList( gwidAlert ) );
    chWriteAlerts.put( aAlarmId, mapW.values().first() );
    // add MUTEDchannels
    Gwid gwidMuted = Gwid.createRtdata( CLSID_ALARM, aAlarmId, RTDID_IS_ALERT );
    mapR = rtdService().createReadCurrDataChannels( new GwidList( gwidMuted ) );
    chReadMutes.put( aAlarmId, mapR.values().first() );
    mapW = rtdService().createWriteCurrDataChannels( new GwidList( gwidMuted ) );
    chWriteMutes.put( aAlarmId, mapW.values().first() );
  }

  private void removeRtdataChannelsOfAlarm( String aAlarmId ) {
    chReadAlerts.removeByKey( aAlarmId ).close();
    chWriteAlerts.removeByKey( aAlarmId ).close();
    chReadMutes.removeByKey( aAlarmId ).close();
    chWriteMutes.removeByKey( aAlarmId ).close();
  }

  // ------------------------------------------------------------------------------------
  // ISkAlarmService
  //

  @Override
  public IStridablesList<ISkAlarm> listAlarms() {
    IList<ISkAlarm> ll = objServ().listObjs( CLSID_ALARM, true );
    return new StridablesList<>( ll );
  }

  @Override
  public ISkAlarm findAlarm( String aAlarmId ) {
    return objServ().find( new Skid( CLSID_ALARM, aAlarmId ) );
  }

  @Override
  public ISkAlarm defineAlarm( IDtoAlarm aAlarmInfo ) {
    IDtoFullObject dto = makeDtoFullObject( aAlarmInfo );
    boolean wasAlarm = findAlarm( aAlarmInfo.id() ) != null;
    pauseCoreValidation();
    try {
      ISkAlarm skAlarm = DtoFullObject.defineFullObject( coreApi(), dto );
      if( !wasAlarm ) {
        addRtdataChannelsForAlarm( skAlarm.strid() );
      }
      return skAlarm;
    }
    finally {
      resumeCoreValidation();
    }
  }

  @Override
  public void removeAlarm( String aAlarmId ) {
    ISkAlarm skAlarm = findAlarm( aAlarmId );
    if( skAlarm == null ) {
      return;
    }
    removeRtdataChannelsOfAlarm( aAlarmId );
    pauseCoreValidation();
    try {
      objServ().removeObject( skAlarm.skid() );
    }
    finally {
      resumeCoreValidation();
    }
  }

  @Override
  public void addAlertListener( ISkAlertListener aListener ) {
    if( !alertListeners.hasElem( aListener ) ) {
      alertListeners.add( aListener );
    }
  }

  @Override
  public void removeAlertListener( ISkAlertListener aListener ) {
    alertListeners.remove( aListener );
  }

  @Override
  public ITsCheckerTopicManager<ISkCoreApi> getAlarmCheckersTopicManager() {
    return topicManager;
  }

  @Override
  public ITsValidationSupport<ISkAlarmServiceValidator> svs() {
    return svs;
  }

  @Override
  public ITsEventer<ISkAlarmServiceListener> serviceEventer() {
    return eventer;
  }

}
