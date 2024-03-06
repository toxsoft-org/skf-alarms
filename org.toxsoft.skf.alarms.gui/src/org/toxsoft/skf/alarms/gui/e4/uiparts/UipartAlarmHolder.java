package org.toxsoft.skf.alarms.gui.e4.uiparts;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.toxsoft.core.tsgui.widgets.TsComposite;
import org.toxsoft.core.tslib.utils.logs.impl.LoggerUtils;
import org.toxsoft.skf.alarms.gui.m5.SkAlarmPanel;
import org.toxsoft.uskat.core.connection.ISkConnection;
import org.toxsoft.uskat.core.gui.conn.ISkConnectionSupplier;
import org.toxsoft.uskat.core.gui.e4.uiparts.SkMwsAbstractPart;

/**
 * Зарезервированное место под окно "алармов".
 *
 * @author vs
 */
public class UipartAlarmHolder
    extends SkMwsAbstractPart {

  @Override
  public ISkConnection skConn() {
    ISkConnectionSupplier cs = tsContext().get( ISkConnectionSupplier.class );
    if( cs != null ) {
      return cs.defConn();
    }
    LoggerUtils.errorLogger().error( "ISkConnectionSupplier - null" ); //$NON-NLS-1$
    return null;
  }

  @Override
  protected void doCreateContent( TsComposite aParent ) {
    initContent( aParent );
  }

  protected void initContent( final Composite aParent ) {
    // dima 27.02.23 вставляем боевую панель
    // FillLayout fl = new FillLayout();
    // aParent.setLayout( fl );
    // Button btn = new Button( aParent, SWT.PUSH );
    // btn.setText( "Окно алармов" ); //$NON-NLS-1$

    GridLayout gl = new GridLayout( 1, false );
    gl.marginHeight = 0;
    gl.marginWidth = 0;
    aParent.setLayout( gl );
    GridData gd = new GridData( SWT.FILL, SWT.FILL, true, true );
    SkAlarmPanel alarmsPanel = new SkAlarmPanel( aParent, skConn(), tsContext() );
    alarmsPanel.setLayoutData( gd );
  }
}
