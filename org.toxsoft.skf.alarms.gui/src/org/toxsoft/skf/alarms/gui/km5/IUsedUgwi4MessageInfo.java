package org.toxsoft.skf.alarms.gui.km5;

import org.toxsoft.core.tslib.gw.ugwi.*;
import org.toxsoft.uskat.core.utils.msgen.*;

/**
 * Interface to specify one used Ugwi for {@link ISkMessageInfo} <br>
 *
 * @author dima
 */

public interface IUsedUgwi4MessageInfo {

  /**
   * @return id path
   */
  String idPath();

  /**
   * FIXME need change to {@link Ugwi}
   *
   * @return { link Gwid} usedUgwi
   */
  Ugwi usedUgwi();

}
