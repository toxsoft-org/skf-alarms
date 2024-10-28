package org.toxsoft.skf.alarms.gui.km5;

import static org.toxsoft.core.tsgui.bricks.gw.IGwM5Constants.*;
import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tsgui.m5.gui.mpc.IMultiPaneComponentConstants.*;
import static org.toxsoft.core.tsgui.valed.api.IValedControlConstants.*;
import static org.toxsoft.core.tslib.av.EAtomicType.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.alarms.gui.ISkResources.*;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.impl.*;
import org.toxsoft.core.tsgui.m5.gui.panels.*;
import org.toxsoft.core.tsgui.m5.gui.panels.impl.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.m5.std.models.av.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.misc.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.skf.reports.gui.panels.valed.*;
import org.toxsoft.uskat.core.api.evserv.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.km5.*;

/**
 * @author Slavage
 */
public class SkAlertM5Model
    extends KM5ConnectedModelBase<SkEvent> {

  public static final String MODEL_ID = "SkAlertM5Model"; //$NON-NLS-1$

  /**
   * The ID of the field {@link SkEvent#paramValues()}.
   */
  public static final String FID_PARAMS = "params"; //$NON-NLS-1$

  /**
   * Attribute {@link SkEvent#eventGwid() } Green world ID
   */
  public M5AttributeFieldDef<SkEvent> EV_GWID = new M5AttributeFieldDef<>( FID_GWID, VALOBJ, //
      TSID_NAME, STR_N_EV_GWID, //
      TSID_DESCRIPTION, STR_D_EV_GWID, //
      TSID_KEEPER_ID, Gwid.KEEPER_ID, //
      OPID_EDITOR_FACTORY_NAME, ValedAvValobjGwidEditor.FACTORY_NAME //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_READ_ONLY | M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( SkEvent aEntity ) {
      return AvUtils.avValobj( aEntity.eventGwid() );
    }

  };

  /**
   * Field {@link SkEvent#paramValues()}.
   */
  public final IM5MultiModownFieldDef<SkEvent, IdValue> EV_PARAMS =
      new M5MultiModownFieldDef<>( FID_PARAMS, IdValueM5Model.MODEL_ID ) {

        @Override
        protected void doInit() {
          setNameAndDescription( STR_N_EVENT_PARAMS, STR_D_EVENT_PARAMS );
          setFlags( M5FF_DETAIL | M5FF_COLUMN );
        }

        protected IList<IdValue> doGetFieldValue( SkEvent aEntity ) {
          return IdValue.makeIdValuesCollFromOptionSet( aEntity.paramValues() ).values();
        }

      };

  public SkAlertM5Model( ISkConnection aConn ) {
    super( MODEL_ID, SkEvent.class, aConn );
    addFieldDefs( EV_GWID, EV_PARAMS );
    setPanelCreator( new M5DefaultPanelCreator<>() {

      @Override
      protected IM5CollectionPanel<SkEvent> doCreateCollEditPanel( ITsGuiContext aContext,
          IM5ItemsProvider<SkEvent> aItemsProvider, IM5LifecycleManager<SkEvent> aLifecycleManager ) {
        OPDEF_IS_ACTIONS_CRUD.setValue( aContext.params(), AV_FALSE );
        OPDEF_IS_FILTER_PANE.setValue( aContext.params(), AV_TRUE );
        MultiPaneComponentModown<SkEvent> mpc = new SkAlertMpc( aContext, model(), aItemsProvider, aLifecycleManager );
        return new M5CollectionPanelMpcModownWrapper<>( mpc, false );
      }
    } );
  }

  @Override
  protected IM5LifecycleManager<SkEvent> doCreateLifecycleManager( Object aMaster ) {
    return new SkAlertM5LifecycleManager( this, (ISkConnection)aMaster );
  }

  @Override
  protected IM5LifecycleManager<SkEvent> doCreateDefaultLifecycleManager() {
    return new SkAlertM5LifecycleManager( this, skConn() );
  }
}
