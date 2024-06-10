package org.toxsoft.skf.alarms.gui.km5;

import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.skf.alarms.gui.ISkResources.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;

import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.m5.gui.mpc.*;
import org.toxsoft.core.tsgui.m5.gui.panels.*;
import org.toxsoft.core.tsgui.m5.gui.panels.impl.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.av.metainfo.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.impl.*;
import org.toxsoft.core.tslib.gw.ugwi.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.uskat.core.utils.msgen.*;

/**
 * M5-model of {@link ISkMessageInfo}
 *
 * @author dima
 */
public class SkMessageInfoM5Model
    extends M5Model<ISkMessageInfo> {

  /**
   * The model ID.
   */
  static final String MODEL_ID = USKAT_FULL_ID + ".m5.SkMessageInfo"; //$NON-NLS-1$

  /**
   * ID of attribute {@link ISkMessageInfo#fmtStr()}.
   */
  static final String ATRID_FMT_STR = "fmtStr"; //$NON-NLS-1$

  /**
   * ID of attribute {@link ISkMessageInfo#usedUgwies()}.
   */
  static final String CLBID_USED_UGWIES = "usedUgwies"; //$NON-NLS-1$

  /**
   * Attribute {@link ISkMessageInfo#fmtStr()}.
   */
  public final IM5AttributeFieldDef<ISkMessageInfo> FMT_STR =
      new M5AttributeFieldDef<>( ATRID_FMT_STR, IAvMetaConstants.DDEF_STRING ) {

        @Override
        protected void doInit() {
          setNameAndDescription( STR_N_FDEF_FMT_STR, STR_D_FDEF_FMT_STR );
          setFlags( M5FF_COLUMN );
        }

        protected IAtomicValue doGetFieldValue( ISkMessageInfo aEntity ) {
          return AvUtils.avStr( aEntity.fmtStr() );
        }

      };

  /**
   * Структура для описания поля типа списка параметров которые хранятся ВМЕСТЕ с сущностью. Ключевое отличие от связи с
   * объектам в том что по связи объекты хранятся отдельно от сущности.
   */
  public final IM5MultiModownFieldDef<ISkMessageInfo, IUsedUgwi4MessageInfo> USED_UGWIES =
      new M5MultiModownFieldDef<>( CLBID_USED_UGWIES, UsedUgwi4MessageInfoM5Model.MODEL_ID ) {

        @Override
        protected void doInit() {
          setNameAndDescription( STR_N_USED_UGWIES, STR_D_USED_UGWIES );
          setFlags( M5FF_DETAIL );
        }

        protected IList<IUsedUgwi4MessageInfo> doGetFieldValue( ISkMessageInfo aEntity ) {
          IListEdit<IUsedUgwi4MessageInfo> retVal = new ElemArrayList<>();
          for( String key : aEntity.usedUgwies().keys() ) {
            Ugwi ugwi = aEntity.usedUgwies().getByKey( key );
            retVal.add( new UsedUgwi4MessageInfo( key, ugwi ) );
          }
          return retVal;
        }

      };

  /**
   * Constructor.
   *
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  public SkMessageInfoM5Model() {
    super( MODEL_ID, ISkMessageInfo.class );

    addFieldDefs( FMT_STR, USED_UGWIES );
    setPanelCreator( new M5DefaultPanelCreator<>() {

      protected IM5EntityPanel<ISkMessageInfo> doCreateEntityEditorPanel( ITsGuiContext aContext,
          IM5LifecycleManager<ISkMessageInfo> aLifecycleManager ) {
        IMultiPaneComponentConstants.OPDEF_IS_FILTER_PANE.setValue( aContext.params(), AvUtils.AV_FALSE );

        return new M5DefaultEntityControlledPanel<>( aContext, model(), aLifecycleManager, null );
      }
    } );

  }

  @Override
  protected IM5LifecycleManager<ISkMessageInfo> doCreateLifecycleManager( Object aMaster ) {
    return new SkMessageInfoM5LifecycleManager( this );
  }

  @Override
  protected IM5LifecycleManager<ISkMessageInfo> doCreateDefaultLifecycleManager() {
    return new SkMessageInfoM5LifecycleManager( this );
  }

}
