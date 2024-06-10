package org.toxsoft.skf.alarms.gui.km5;

import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tsgui.valed.api.IValedControlConstants.*;
import static org.toxsoft.core.tslib.av.EAtomicType.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.alarms.gui.ISkResources.*;

import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.valed.controls.av.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.av.impl.*;
import org.toxsoft.core.tslib.gw.ugwi.*;
import org.toxsoft.uskat.core.api.ugwis.kinds.*;
import org.toxsoft.uskat.core.gui.ugwi.valed.*;

/**
 * M5-model of {@link IUsedUgwi4MessageInfo}.
 *
 * @author dima
 */
public class UsedUgwi4MessageInfoM5Model
    extends M5Model<IUsedUgwi4MessageInfo> {

  /**
   * model id
   */
  public static final String MODEL_ID      = "UsedUgwi4MessageInfoM5Model"; //$NON-NLS-1$
  /**
   * id field of idPath
   */
  public static final String FID_IDPATH    = "idPath";                      //$NON-NLS-1$
  /**
   * id field of usedUgwi
   */
  public static final String FID_USED_UGWI = "usedUgwi";                    //$NON-NLS-1$

  /**
   * Attribute {@link IUsedUgwi4MessageInfo#idPath() }
   */
  public M5AttributeFieldDef<IUsedUgwi4MessageInfo> IDPATH = new M5AttributeFieldDef<>( FID_IDPATH, EAtomicType.STRING, //
      TSID_NAME, STR_N_IDPATH, //
      TSID_DESCRIPTION, STR_D_IDPATH, //
      OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( IUsedUgwi4MessageInfo aEntity ) {
      return AvUtils.avStr( aEntity.idPath() );
    }

  };

  /**
   * Attribute {@link IUsedUgwi4MessageInfo#usedUgwi() } Green world ID
   */
  public M5AttributeFieldDef<IUsedUgwi4MessageInfo> USED_UGWI = new M5AttributeFieldDef<>( FID_USED_UGWI, VALOBJ, //
      TSID_NAME, STR_N_USED_UGWI, //
      TSID_DESCRIPTION, STR_D_USED_UGWI, // FIXME change to list types
      ValedUgwiSelectorFactory.OPDEF_SINGLE_UGWI_KIND_ID, avStr( UgwiKindSkRtdata.KIND_ID ), //
      TSID_KEEPER_ID, Ugwi.KEEPER_ID, //
      OPID_EDITOR_FACTORY_NAME, ValedAvValobjUgwiSelectorTextAndButton.FACTORY_NAME //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( IUsedUgwi4MessageInfo aEntity ) {
      return AvUtils.avValobj( aEntity.usedUgwi() );
    }

  };

  /**
   * Constructor
   */
  public UsedUgwi4MessageInfoM5Model() {
    super( MODEL_ID, IUsedUgwi4MessageInfo.class );

    addFieldDefs( IDPATH, USED_UGWI );

  }

  @Override
  protected IM5LifecycleManager<IUsedUgwi4MessageInfo> doCreateLifecycleManager( Object aMaster ) {
    return new UsedUgwi4MessageInfoLifecycleManager( this );
  }

  @Override
  protected IM5LifecycleManager<IUsedUgwi4MessageInfo> doCreateDefaultLifecycleManager() {
    return new UsedUgwi4MessageInfoLifecycleManager( this );
  }

}
