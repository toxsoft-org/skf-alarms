package org.toxsoft.skf.alarms.lib2;

import org.toxsoft.uskat.core.api.evserv.*;

/**
 * Listens to the alarms.
 *
 * @author hazard157
 */
public interface ISkAlertListener {

  /**
   * Called when alert state of the alarm is set.
   *
   * @param aEvent {@link SkEvent} - the alert event
   */
  void onAlert( SkEvent aEvent );

  /**
   * Called when alert state of the alarm is reset.
   *
   * @param aEvent {@link SkEvent} - the acknowledge event
   */
  void onAcknowledge( SkEvent aEvent );

}
