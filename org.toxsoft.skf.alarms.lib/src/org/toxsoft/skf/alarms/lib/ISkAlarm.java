package org.toxsoft.skf.alarms.lib;

import org.toxsoft.core.tslib.av.opset.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.core.tslib.math.cond.*;
import org.toxsoft.core.tslib.math.cond.checker.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.uskat.core.api.cmdserv.*;
import org.toxsoft.uskat.core.api.objserv.*;
import org.toxsoft.uskat.core.utils.msgen.*;

/**
 * Generated alarm instance.
 * <p>
 * Generates the following events:
 * <ul>
 * <li>{@link ISkAlarmConstants#EVID_ALERT} - alert! the main and most significant event fired when "alarm
 * happens";</li>
 * <li>{@link ISkAlarmConstants#EVID_ACKNOWLEDGE} - fired when command {@link ISkAlarmConstants#CMDID_ACKNOWLEDGE} was
 * acquired and processed so alert was reset;</li>
 * <li>{@link ISkAlarmConstants#EVID_MUTED} - someone has muted alert generation, the author and reason are
 * specified;</li>
 * <li>{@link ISkAlarmConstants#EVID_UNMUTED} - the alarm returned to duty and can generate alerts again.</li>
 * </ul>
 *
 * @author hazard157
 */
public interface ISkAlarm
    extends ISkObject {

  /**
   * Returns the alarm object SKID - an unique identifier of the alarm.
   *
   * @return {@link Skid} - the alarm object SKID
   */
  @Override
  Skid skid();

  /**
   * Returns the severity of the alarm.
   *
   * @return {@link ESkAlarmSeverity} - the alarm severity
   */
  ESkAlarmSeverity severity();

  /**
   * Returns the parameters to create alarm generation condition checker.
   * <p>
   * The checker {@link ITsChecker} is created based on returned definition by the
   * {@link ISkAlarmService#getAlarmCheckersTpoicManager()}. When checker returns <code>true</code> the alert is fired
   * (if {@link #isAlert()} was <code>false</code>). However, alert if checker returns <code>false</code> alert is
   * <b>not</b> reset. The only way to reset the alert is to send {@link ISkAlarmConstants#CMDID_ACKNOWLEDGE} by the
   * {@link #sendAcknowledge(Skid, String)} method.
   *
   * @return {@link ITsCombiCondInfo} - alarm generation condition parameters
   */
  ITsCombiCondInfo alertCondition();

  /**
   * Definition of the message to be send as an event parameter {@link ISkAlarmConstants#EVPRMDEF_ALARM_MESSAGE}.
   *
   * @return {@link ISkMessageInfo} - the alarm message definition
   */
  ISkMessageInfo messageInfo();

  /**
   * Determines if alarm needs external acknowledgement.
   *
   * @return boolean - the flag that external acknowledgement is required <br>
   *         <b>true</b> - to reset alarm dedicated command executor is required;<br>
   *         <b>false</b> - alert will be reset by alarm service itself.
   */
  boolean isExternalAckowledgement();

  /**
   * Determines if alert of this alarm is active.
   *
   * @return boolean - the alerts state <br>
   *         <b>true</b> - alert is on, alarm condition was met and not yet acknowledged;<br>
   *         <b>false</b> - alert if off, inactive.
   */
  boolean isAlert();

  /**
   * Sets alert state {@link #isAlert()} to <code>true</code> and generates event {@link ISkAlarmConstants#EVID_ALERT}.
   * <p>
   * Has no effect if alert state is already set ({@link #isAlert()} = <code>true</code>) or when alert is muted
   * ({@link #isMuted()} = <code>true</code>).
   */
  // FIXME this method must be visible only for the alert firing code!
  void setAlert();

  /**
   * Sends command to set {@link #isAlert()} to <code>false</code>.
   * <p>
   * This is a dangerous operation so author and reason is required.
   * <p>
   * Prepares and sends {@link ISkAlarmConstants#CMDID_ACKNOWLEDGE} command by method
   * {@link ISkCommandService#sendCommand(Gwid, Skid, IOptionSet)} so returns immediately. In fact, the ALERT state will
   * be reset after the executor processes the command. When alert becomes off the event
   * {@link ISkAlarmConstants#EVID_ACKNOWLEDGE} is generated.
   *
   * @param aAuthor {@link Skid} - the acknowledge author object SKID
   * @param aReason String - human readable reason for acknowledge, may be an empty string
   * @return {@link ISkCommand} - the command sent by command service
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   * @throws TsItemNotFoundRtException object with <code>aAuthor</code> SKID does not exists
   */
  ISkCommand sendAcknowledge( Skid aAuthor, String aReason );

  /**
   * Determines if the alert state setting to <code>true</code> is enabled.
   *
   * @return boolean - <code>true</code> when alert can not be set
   */
  boolean isMuted();

  /**
   * Disables alert - {@link #isAlert()} setting to <code>true</code>.
   * <p>
   * This is a dangerous operation so author and reason is required.
   * <p>
   * Existing alert state remains unchanged.
   * <p>
   * Generates the event {@link ISkAlarmConstants#EVID_MUTED}.
   *
   * @param aAuthor {@link Skid} - the muting author object SKID
   * @param aReason String - human readable reason for muting, may be an empty string
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   * @throws TsItemNotFoundRtException object with <code>aAuthor</code> SKID does not exists
   */
  void muteAlert( Skid aAuthor, String aReason );

  /**
   * Enables alert - {@link #isAlert()} setting to <code>true</code>.
   * <p>
   * Generates the event {@link ISkAlarmConstants#EVID_UNMUTED}.
   */
  void unmuteAlert();

}
