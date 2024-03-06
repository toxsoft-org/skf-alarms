package org.toxsoft.skf.alarms.gui;

import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.panels.*;
import org.toxsoft.skf.alarms.gui.m5.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.utils.*;

/**
 * Компонента для размещения панели алармов в проекте МосКокс.
 * <p>
 *
 * @author vs
 */
public class SkAlarmsPanelHolder
    extends TsPanel
    implements ISkConnected {

  private final ISkConnection skConnection;

  /**
   * Конструктор.<br>
   *
   * @param aParent Composite - родительская панель
   * @param aSkConn ISkConnection - соединение с сервером
   * @param aContext ITsGuiContext - соответствующий контекст
   */
  public SkAlarmsPanelHolder( Composite aParent, ISkConnection aSkConn, ITsGuiContext aContext ) {
    super( aParent, aContext );
    skConnection = aSkConn;

    // Макс - здесь должна быть твоя инициализация

    GridLayout gl = new GridLayout( 1, false );
    gl.marginHeight = 0;
    gl.marginWidth = 0;
    setLayout( gl );
    GridData gd = new GridData( SWT.FILL, SWT.FILL, true, true );
    SkAlarmPanel alarmsPanel = new SkAlarmPanel( this, aSkConn, aContext );
    alarmsPanel.setLayoutData( gd );
  }

  // ------------------------------------------------------------------------------------
  // ISkConnected
  //

  @Override
  public ISkConnection skConn() {
    return skConnection;
  }

}
