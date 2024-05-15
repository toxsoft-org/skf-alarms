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
   * Called when alarm generation was muted or unmuted.
   *
   * @param aAlarmId String - the alarm ID
   * @param aMuted boolean - the flag that alarm generation is muted<br>
   *          <b>true</b> - alarms of the specified ID will not be generated (alarm objects will not be created);<br>
   *          <b>false</b> - alarm are generated.
   */
  void onAlarmMuteStateChange( String aAlarmId, boolean aMuted );

  /**
   * Called when alarm definitions has been changed.
   *
   * @param aOp {@link ECrudOp} - the kind of change
   * @param aAlarmId String - the affected alarm ID or <code>null</code> for {@link ECrudOp#LIST}
   */
  void onAlarmDefinition( ECrudOp aOp, String aAlarmId );

}
