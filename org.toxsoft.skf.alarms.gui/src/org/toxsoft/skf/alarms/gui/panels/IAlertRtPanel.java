package org.toxsoft.skf.alarms.gui.panels;

import org.toxsoft.core.tsgui.panels.generic.*;
import org.toxsoft.core.tslib.bricks.time.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.alarms.lib.*;
import org.toxsoft.uskat.core.api.evserv.*;

/**
 * The panel displays currently active alerts with ability to acknowledge them.
 * <p>
 * Panel contains:
 * <p>
 * <ul>
 * <li>toolbar on top with button to acknowledge currently selected alert;</li>
 * <li>main area with a list of active alert events {@link ISkAlarmConstants#EVINF_ALERT}. List is implemented as a
 * table with columns "Time", "Alarm name" and "Alert event message";</li>
 * <li>status line on bottom displaying number of alerts in list.</li>
 * </ul>
 * <p>
 * Behaviour description:
 * <ul>
 * <li>panel listens to the alert events. When {@link ISkAlarmConstants#EVINF_ALERT} occurs places event in the list.
 * When event {@link ISkAlarmConstants#EVINF_ACKNOWLEDGE} occurs - removes alert from the list;</li>
 * <li>when user presses "Acknowledge" button (or by any other means wants to acknowledge alert) the dialog is
 * displayed. Dialog allows to enter optional comment string, pressing OK will cause panel to call
 * {@link ISkAlarm#sendAcknowledge(Skid, String)} method with user SKID.;</li>
 * <li>special actions are performed when panel is created and displayed first time. Panel searches alarms for active
 * {@link ISkAlarm#isAlert()} flag. If alert is active, panel finds last alert event using
 * {@link ISkAlarm#getHistory(IQueryInterval)} and event to the displayed list.</li>
 * </ul>
 * <p>
 * Additionally panel allows to check multiple alerts with {@link #checkSupport()} to acknowledge multiple alerts with
 * single user action.
 * <p>
 * The panel is a viewer so {@link #isViewer()} = <code>true</code>.
 *
 * @author hazard157
 */
public interface IAlertRtPanel
    extends IGenericCollPanel<SkEvent> {

  /**
   * Returns the SKIDs of alarms to be monitored.
   * <p>
   * <p>
   * Note: an empty list means that <b>all</b> alarms {@link ISkAlarmService#listAlarms()} will be monitored. Monitoring
   * all alarms is not the same as monitoring alarms even if all {@link ISkAlarmService#listAlarms()} are listed. An
   * empty list means that even alarms, created after panel start, will be monitored while fixed list does not adds
   * newly created alarms to the monitoring.
   *
   * @return {@link ISkidList} - monitored alarm SKIDs or an empty list when all alarms are monitored
   */
  ISkidList listMonitoredAlarms();

  /**
   * Sets the SKIDs of alarms to be monitored.
   * <p>
   * Note: an empty list means that <b>all</b> alarms {@link ISkAlarmService#listAlarms()} will be monitored.
   * <p>
   * Invalid SKIDs in argument are silently ignored.
   *
   * @param aAlarmSkids {@link ISkidList} - the SKIDs of alarms to monitor
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  void setMeonitoredAlarms( ISkidList aAlarmSkids );

  /**
   * Returns list of alarms currently having active alerts in this panel displayed.
   *
   * @return {@link IList}&lt;{@link ISkAlarm}&gt; - list of alarms with alerts in panel
   */
  IList<ISkAlarm> listAlertAlarms();

}
