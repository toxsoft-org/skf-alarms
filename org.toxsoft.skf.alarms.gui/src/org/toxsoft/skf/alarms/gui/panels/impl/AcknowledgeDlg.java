package org.toxsoft.skf.alarms.gui.panels.impl;

import static org.toxsoft.core.tsgui.dialogs.ITsGuiDialogResources.*;

import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.dialogs.datarec.*;
import org.toxsoft.core.tslib.bricks.validator.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.core.tslib.utils.errors.*;

/**
 * @author Slavage
 */
public class AcknowledgeDlg {

  static class Panel
      extends AbstractTsDialogPanel<String, Object> {

    private final ITsValidator<String> commentValidator;
    private final Text                 text1;

    Panel( Composite aParent, TsDialog<String, Object> aOwnerDialog, ITsValidator<String> aValidator ) {
      super( aParent, aOwnerDialog );
      commentValidator = aValidator;
      this.setLayout( new GridLayout( 2, false ) );
      // comment enterer field
      Label l1 = new Label( this, SWT.LEFT );
      l1.setLayoutData( new GridData( SWT.LEFT, SWT.CENTER, false, false ) );
      l1.setText( "Комментарий" );// STR_L_ENTER_PASSWORD1 );
      text1 = new Text( this, SWT.BORDER );
      text1.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false ) );
      text1.addModifyListener( notificationModifyListener );
    }

    @Override
    protected ValidationResult validateData() {
      String s = text1.getText();
      ValidationResult vr = commentValidator.validate( s );
      return vr;
    }

    @Override
    protected void doSetDataRecord( String aData ) {
      String s = aData != null ? aData : TsLibUtils.EMPTY_STRING;
      text1.setText( s );
    }

    @Override
    protected String doGetDataRecord() {
      return text1.getText();
    }

  }

  /**
   * Invokes comment editing dialog.
   *
   * @param aDialogInfo {@link ITsDialogConstants} - dialog window parameters
   * @param aValidator {@link ITsValidator}&lt;String&gt; - comment validator
   * @return String - entered, valid password or <code>null</code>
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  public static final String enterComment( ITsDialogInfo aDialogInfo, ITsValidator<String> aValidator ) {
    TsNullArgumentRtException.checkNulls( aDialogInfo, aValidator );
    IDialogPanelCreator<String, Object> creator =
        ( aParent, aOwnerDialog ) -> new Panel( aParent, aOwnerDialog, aValidator );
    TsDialog<String, Object> d = new TsDialog<>( aDialogInfo, null, new Object(), creator );
    return d.execData();
  }

  /**
   * Invokes comment editing dialog with default caption and title
   *
   * @param aContext {@link ITsGuiContext} - the context
   * @param aValidator {@link ITsValidator}&lt;String&gt; - command validator
   * @return String - entered, valid password or <code>null</code>
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  public static final String enterComment( ITsGuiContext aContext, ITsValidator<String> aValidator ) {
    TsNullArgumentRtException.checkNulls( aContext, aValidator );
    TsDialogInfo dialogInfo = new TsDialogInfo( aContext, DLG_C_ENTER_PASSWORD, DLG_T_ENTER_PASSWORD );
    dialogInfo.setMinSizeShellRelative( 10, 10 );
    return enterComment( dialogInfo, aValidator );
  }
}
