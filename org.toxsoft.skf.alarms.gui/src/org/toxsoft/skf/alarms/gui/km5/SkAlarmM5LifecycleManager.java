package org.toxsoft.skf.alarms.gui.km5;

import static org.toxsoft.skf.alarms.lib.ISkAlarmConstants.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;

import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tslib.bricks.validator.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.math.cond.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.alarms.lib.*;
import org.toxsoft.skf.alarms.lib.impl.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.km5.*;

/**
 * LM for {@link SkAlarmM5Model}.
 *
 * @author hazard157
 */
public class SkAlarmM5LifecycleManager
    extends KM5LifecycleManagerBasic<ISkAlarm> {

  /**
   * Constructor.
   *
   * @param aModel {@link IM5Model}&lt;T&gt; - the model
   * @param aMaster {@link ISkConnection} - master object
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  public SkAlarmM5LifecycleManager( IM5Model<ISkAlarm> aModel, ISkConnection aMaster ) {
    super( aModel, true, true, true, true, aMaster );
  }

  // ------------------------------------------------------------------------------------
  // implementation
  //

  private ISkAlarmService alarmService() {
    return coreApi().getService( ISkAlarmService.SERVICE_ID );
  }

  private static IDtoAlarm makeDto( IM5Bunch<ISkAlarm> aValues ) {
    DtoAlarm dto = new DtoAlarm( aValues.getAsAv( AID_STRID ).asString() );
    String name = aValues.getAsAv( AID_NAME ).asString();
    String description = aValues.getAsAv( AID_DESCRIPTION ).asString();
    dto.setNameAndDescription( name, description );
    dto.setSeverity( aValues.getAsAv( ATRID_SEVERITY ).asValobj() );
    dto.setAlertCondition( aValues.getAs( CLBID_ALERT_CONDITION, ITsCombiCondInfo.class ) );
    // FIXME comment to mask error
    // dto.setMessageInfo( aValues.getAs( CLBID_MESSAGE_INFO, ISkMessageInfo.class ) );
    return dto;
  }

  // ------------------------------------------------------------------------------------
  // KM5LifecycleManagerBasic
  //

  @Override
  protected ValidationResult doBeforeCreate( IM5Bunch<ISkAlarm> aValues ) {
    IDtoAlarm dto = makeDto( aValues );
    return alarmService().svs().validator().canCreateAlarm( dto );
  }

  @Override
  protected ISkAlarm doCreate( IM5Bunch<ISkAlarm> aValues ) {
    IDtoAlarm dto = makeDto( aValues );
    return alarmService().defineAlarm( dto );
  }

  @Override
  protected ValidationResult doBeforeEdit( IM5Bunch<ISkAlarm> aValues ) {
    IDtoAlarm dto = makeDto( aValues );
    return alarmService().svs().validator().canEditAlarm( dto, aValues.originalEntity() );
  }

  @Override
  protected ISkAlarm doEdit( IM5Bunch<ISkAlarm> aValues ) {
    IDtoAlarm dto = makeDto( aValues );
    return alarmService().defineAlarm( dto );
  }

  @Override
  protected ValidationResult doBeforeRemove( ISkAlarm aEntity ) {
    return alarmService().svs().validator().canRemoveAlarm( aEntity.strid() );
  }

  @Override
  protected void doRemove( ISkAlarm aEntity ) {
    alarmService().removeAlarm( aEntity.strid() );
  }

  @Override
  protected IList<ISkAlarm> doListEntities() {
    return alarmService().listAlarms();
  }

}
