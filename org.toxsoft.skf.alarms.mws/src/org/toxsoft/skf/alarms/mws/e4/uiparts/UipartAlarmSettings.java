package org.toxsoft.skf.alarms.mws.e4.uiparts;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.bricks.ctx.impl.*;
import org.toxsoft.core.tsgui.widgets.*;
import org.toxsoft.skf.alarms.gui.panels.impl.*;
import org.toxsoft.uskat.core.gui.e4.uiparts.*;

/**
 * Просмотр настроек алармов
 * <p>
 *
 * @author dima
 */
public class UipartAlarmSettings
    extends SkMwsAbstractPart {

  private AlarmRtPanel panel;

  @Override
  protected void doCreateContent( TsComposite aParent ) {
    ITsGuiContext ctx = new TsGuiContext( tsContext() );
    // TODO Slava insert panel
    panel = new AlarmRtPanel( ctx );
    panel.createControl( aParent );
    // panel.setLayoutData( BorderLayout.CENTER );
  }

}
