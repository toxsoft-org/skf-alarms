package org.toxsoft.skf.alarms.gui.km5;

import static org.toxsoft.core.tsgui.bricks.actions.ITsStdActionDefs.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;

import org.toxsoft.core.tsgui.bricks.actions.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.dialogs.datarec.*;
import org.toxsoft.core.tsgui.graphics.icons.*;
import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.gui.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.impl.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.panels.toolbar.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.alarms.lib.*;

/**
 * {@link MultiPaneComponentModown} implementation for {@link ISkAlarm}.
 *
 * @author dima
 */
public class SkAlarmMpc
    extends MultiPaneComponentModown<ISkAlarm> {

  public SkAlarmMpc( ITsGuiContext aContext, IM5Model<ISkAlarm> aModel, IM5ItemsProvider<ISkAlarm> aItemsProvider,
      IM5LifecycleManager<ISkAlarm> aLifecycleManager ) {
    super( aContext, aModel, aItemsProvider, aLifecycleManager );
  }

  @Override
  protected ITsToolbar doCreateToolbar( ITsGuiContext aContext, String aName, EIconSize aIconSize,
      IListEdit<ITsActionDef> aActs ) {
    // add func create copy
    int index = 1 + aActs.indexOf( ACDEF_ADD );
    aActs.insert( index, ACDEF_ADD_COPY );
    return super.doCreateToolbar( aContext, aName, aIconSize, aActs );
  }

  @Override
  protected void doProcessAction( String aActionId ) {
    ISkAlarm selected = selectedItem();
    switch( aActionId ) {
      case ACTID_ADD_COPY: {
        ITsDialogInfo cdi = doCreateDialogInfoToAddItem();
        IM5BunchEdit<ISkAlarm> initVals = new M5BunchEdit<>( model() );
        initVals.fillFrom( selected, false );
        String itemId = initVals.getAsAv( AID_STRID ).asString();
        itemId = itemId + "_copy"; //$NON-NLS-1$
        initVals.set( AID_STRID, avStr( itemId ) );

        ISkAlarm item = M5GuiUtils.askCreate( tsContext(), model(), initVals, cdi, lifecycleManager() );
        if( item != null ) {
          fillViewer( item );
        }
        break;
      }

      default:
        throw new TsNotAllEnumsUsedRtException( aActionId );
    }
  }

  @Override
  protected void doUpdateActionsState( boolean aIsAlive, boolean aIsSel, ISkAlarm aSel ) {
    toolbar().setActionEnabled( ACTID_ADD_COPY, aIsSel );
  }

}
