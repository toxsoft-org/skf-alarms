package org.toxsoft.skf.alarms.gui.panels;

import org.toxsoft.core.tsgui.m5.gui.mpc.*;
import org.toxsoft.core.tsgui.mws.services.timers.*;
import org.toxsoft.core.tsgui.utils.anim.*;
import org.toxsoft.skf.alarms.lib.*;

/**
 * Panel displays all alarms and their states in real time.
 * <p>
 * Panel contains:
 * <ul>
 * <li>top toolbar - with common buttons as for {@link IMultiPaneComponent}, buttons to mute/unmute and acknowledge the
 * alerts. The presence of the button to generate an alert must be configured via context option;</li>
 * <li>under bottom pane is a filter pane.</li>
 * <li>central table-tree control where each row corresponds to the single alarm from the list
 * {@link ISkAlarmService#listAlarms()}. For each alarm at least name, {@link ISkAlarm#severity()},
 * {@link ISkAlarm#isAlert()} and {@link ISkAlarm#isMuted()} state should be displayed as a column.;</li>
 * <li>detail pane with other information about alarm like severity, alert condition, events history, etc.;</li>
 * <li>bottom summary pane with information about total number of alarms, number of muted and alerted alarms;</li>
 * </ul>
 * <p>
 * Panel is implemented as {@link IMultiPaneComponent}.
 * <p>
 * Panel implements {@link IPausableAnimation} allowing to temporary pause eal-time data update.
 * <p>
 * Panel sets {@link ITsGuiTimersService#slowTimers()} listener to update data in real time, on panel dispose the
 * listener is removed.
 *
 * @author hazard157
 */
public interface IAlarmRtPanel
    extends IPausableAnimation {
  // extends IMultiPaneComponent<ISkAlarm>, IPausableAnimation {
}
