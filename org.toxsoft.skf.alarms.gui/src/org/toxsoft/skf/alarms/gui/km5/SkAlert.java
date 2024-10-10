package org.toxsoft.skf.alarms.gui.km5;

import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.alarms.lib.*;

/**
 * @author Slavage
 */
public class SkAlert
    implements ISkAlert {

  private ISkAlarm alarm;

  public SkAlert( ISkAlarm aAlarm ) {
    TsNullArgumentRtException.checkNulls( aAlarm );
    alarm = aAlarm;
  }

  @Override
  public ISkAlarm getAlarm() {
    return alarm;
  }
}
