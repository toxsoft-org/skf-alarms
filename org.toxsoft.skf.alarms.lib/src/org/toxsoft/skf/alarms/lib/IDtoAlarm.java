package org.toxsoft.skf.alarms.lib;

import org.toxsoft.core.tslib.bricks.strid.*;
import org.toxsoft.core.tslib.math.cond.*;
import org.toxsoft.uskat.core.utils.msgen.*;

/**
 * Alarm description used for alarm object creation.
 *
 * @author hazard157
 */
public interface IDtoAlarm
    extends IStridable {

  /**
   * Returns the severity of the alarm.
   *
   * @return {@link ESkAlarmSeverity} - the alarm severity
   */
  ESkAlarmSeverity severity();

  /**
   * Returns the parameters to create alarm generation condition checker.
   *
   * @return {@link ITsCombiCondInfo} - alarm generation condition parameters
   */
  ITsCombiCondInfo firingCondition();

  /**
   * Definition of the alarm event message.
   *
   * @return {@link ISkMessageInfo} - the alarm message definition
   */
  ISkMessageInfo messageInfo();

}
