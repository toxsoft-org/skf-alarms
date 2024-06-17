package org.toxsoft.skf.alarms.gui.panels;

import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.panels.lazy.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.skf.alarms.lib.*;

/**
 * Panel show current active alerts with acknowledge ability.
 *
 * @author hazard157
 */
public interface IAlertRtPanel
    extends ILazyControl<Control> {

  IList<ISkAlarm> listAlertAlarms();

}
