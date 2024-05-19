package org.toxsoft.skf.alarms.lib;

import org.toxsoft.core.tslib.coll.helpers.*;

/**
 * Listens to the alarm service events.
 * <p>
 * This interface informs about alarms configuration and state changes, it <b>does not</b> informs about alarm alerts.
 * Listen common {@link ISkAlarmConstants#EVID_ALERT} events of the {@link ISkAlarm} objects instead.
 *
 * @author hazard157
 */
public interface ISkAlarmServiceListener {

  /**
   * Called when alarm definitions has been changed.
   *
   * @param aSource {@link ISkAlarmService} - the event source
   * @param aOp {@link ECrudOp} - the kind of change
   * @param aAlarmId String - the affected alarm ID or <code>null</code> for {@link ECrudOp#LIST}
   */
  void onAlarmDefinition( ISkAlarmService aSource, ECrudOp aOp, String aAlarmId );

}
