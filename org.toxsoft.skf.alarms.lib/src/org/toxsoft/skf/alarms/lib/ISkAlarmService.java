package org.toxsoft.skf.alarms.lib;

import org.toxsoft.core.tslib.bricks.events.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.core.tslib.math.cond.checker.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.uskat.core.*;
import org.toxsoft.uskat.core.api.*;

/**
 * The alarm service.
 *
 * @author hazard157
 */
public interface ISkAlarmService
    extends ISkService {

  /**
   * The service ID.
   */
  String SERVICE_ID = ISkHardConstants.SK_SYSEXT_SERVICE_ID_PREFIX + ".Alarms"; //$NON-NLS-1$

  /**
   * Returns all alarms.
   *
   * @return {@link IStridablesList}&lt; {@link ISkAlarm} - list of all alarms
   */
  IStridablesList<ISkAlarm> listAlarms();

  /**
   * Finds the alarm object by ID.
   *
   * @param aAlarmId String the alarm ID
   * @return {@link ISkAlarm} - the found alarm or null
   */
  ISkAlarm findAlarm( String aAlarmId );

  /**
   * Creates new or updates the existing alarm object.
   *
   * @param aAlarmInfo {@link IDtoAlarm} - the alarm definition
   * @return {@link ISkAlarm} - new or edited alarm
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  ISkAlarm defineAlarm( IDtoAlarm aAlarmInfo );

  /**
   * Removes the alarm .
   *
   * @param aAlarmId String - ID of alarm to remove
   */
  void removeAlarmDefinition( String aAlarmId );

  /**
   * Add the alert listener.
   * <p>
   * Does nothing if listener was already added.
   * <p>
   * Note: there is no eventer for alert events as the alert events can not be paused.
   *
   * @param aListener {@link ISkAlertListener} - the alert listener
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  void addAlertListener( ISkAlertListener aListener );

  /**
   * Removes the alert listener.
   * <p>
   * Does nothing if listener was already removed.
   *
   * @param aListener {@link ISkAlertListener} - the alert listener
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  void removeAlertListener( ISkAlertListener aListener );

  /**
   * Returns the means to manage single alarm checkers.
   * <p>
   * The environment of the checkers is {@link ISkCoreApi}, that is
   * {@link ITsCheckerTopicManager#checkEnvironmentClass()} is {@link ISkCoreApi#getClass()}.
   * <p>
   * <b>Important</b>: registered checker types {@link ITsSingleCheckerType} are <b>not stored</b> permanently! The
   * application must register all checkers every time the alarm service is created.
   *
   * @return {@link ITsCheckerTopicManager} - the alarm single checkers topic manager
   */
  ITsCheckerTopicManager<ISkCoreApi> getAlarmCheckersTopicManager();

  /**
   * Returns the service eventer.
   *
   * @return {@link ITsEventer} - the service eventer
   */
  ITsEventer<ISkAlarmServiceListener> serviceEventer();

}
