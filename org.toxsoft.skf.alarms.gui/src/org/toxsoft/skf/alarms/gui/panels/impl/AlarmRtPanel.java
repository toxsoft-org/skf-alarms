package org.toxsoft.skf.alarms.gui.panels.impl;

import org.toxsoft.core.tsgui.m5.gui.mpc.impl.*;
import org.toxsoft.core.tsgui.m5.gui.viewers.*;
import org.toxsoft.skf.alarms.gui.panels.*;
import org.toxsoft.skf.alarms.lib.*;

/**
 * @author Slavage
 */
public class AlarmRtPanel
    extends MultiPaneComponent<ISkAlarm>
    implements IAlarmRtPanel {

  /**
   * Constructor.
   *
   * @param aViewer
   */
  public AlarmRtPanel( IM5TreeViewer<ISkAlarm> aViewer ) {
    super( aViewer );
  }

  @Override
  protected void doDispose() {
    super.doDispose();
  }

  // ------------------------------------------------------------------------------------
  // IAlarmRtPanel

  @Override
  public boolean isPaused() {
    return false;
  }

  @Override
  public void pause() {
  }

  @Override
  public void resume() {
  }
}
