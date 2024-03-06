package org.toxsoft.skf.alarms.s5.addons;

import javax.ejb.Remote;

import org.toxsoft.skf.alarms.lib.IBaAlarms;
import org.toxsoft.uskat.s5.server.backend.addons.IS5BackendAddonSession;

/**
 * Сессия расширения backend {@link IBaAlarms}
 *
 * @author mvk
 */
@Remote
public interface IS5BaAlarmSession
    extends IBaAlarms, IS5BackendAddonSession {
  // nop
}
