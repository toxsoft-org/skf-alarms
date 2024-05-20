package org.toxsoft.skf.alarms.lib;

import org.toxsoft.core.tslib.bricks.validator.*;
import org.toxsoft.core.tslib.utils.errors.*;

/**
 * {@link ISkAlarmService} alarms editing validator.
 *
 * @author hazard157
 */
public interface ISkAlarmServiceValidator {

  /**
   * Checks if alarm can be created.
   *
   * @param aAlarmInfo {@link IDtoAlarm} - the alarm definition
   * @return {@link ValidationResult} - the check result
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  ValidationResult canCreateAlarm( IDtoAlarm aAlarmInfo );

  /**
   * Checks if alarm can be edited.
   *
   * @param aAlarmInfo {@link IDtoAlarm} - the alarm definition
   * @param aExistingAlarm {@link ISkAlarm} - existing alarm, never is <code>null</code>
   * @return {@link ValidationResult} - the check result
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  ValidationResult canEditAlarm( IDtoAlarm aAlarmInfo, ISkAlarm aExistingAlarm );

  /**
   * Checks if the alarm can be removed.
   *
   * @param aAlarmId String - ID of alarm to remove
   * @return {@link ValidationResult} - the check result
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  ValidationResult canRemoveAlarm( String aAlarmId );

}
