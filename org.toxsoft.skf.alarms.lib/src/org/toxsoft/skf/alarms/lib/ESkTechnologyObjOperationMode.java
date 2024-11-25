package org.toxsoft.skf.alarms.lib;

import static org.toxsoft.skf.alarms.lib.l10n.ISkAlarmSharedResources.*;

import org.toxsoft.core.tslib.bricks.keeper.*;
import org.toxsoft.core.tslib.bricks.keeper.std.*;
import org.toxsoft.core.tslib.bricks.strid.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.*;
import org.toxsoft.core.tslib.utils.errors.*;

/**
 * Tecnological object opetarion mode. Used to determine need or needles alarm generation.
 *
 * @author dima
 */
public enum ESkTechnologyObjOperationMode
    implements IStridable {

  /**
   * equipment working. {Valobj,{ts.KeeperId = "ESkTechnologyObjOperationMode",ts.DefaultValue =
   * ESkTechnologyObjOperationMode{working}}}}
   */
  WORKING( "working", STR_WORKING, STR_WORKING_D ), //$NON-NLS-1$

  /**
   * eqipment in repair mode.
   */
  REPAIR( "repair", STR_REPAIR, STR_REPAIR_D ), //$NON-NLS-1$

  /**
   * eqipment in reserve.
   */
  RESERVE( "reserve", STR_RESERVE, STR_RESERVE_D ), //$NON-NLS-1$

  ;

  /**
   * The keeper ID.
   */
  public static final String KEEPER_ID = "ESkTechnologyObjOperationMode"; //$NON-NLS-1$

  /**
   * Keeper singleton.
   */
  public static final IEntityKeeper<ESkTechnologyObjOperationMode> KEEPER =
      new StridableEnumKeeper<>( ESkTechnologyObjOperationMode.class );

  private static IStridablesListEdit<ESkTechnologyObjOperationMode> list = null;

  private final String id;
  private final String name;
  private final String description;

  ESkTechnologyObjOperationMode( String aId, String aName, String aDescription ) {
    id = aId;
    name = aName;
    description = aDescription;
  }

  // --------------------------------------------------------------------------
  // IStridable
  //

  @Override
  public String id() {
    return id;
  }

  @Override
  public String nmName() {
    return name;
  }

  @Override
  public String description() {
    return description;
  }

  // ----------------------------------------------------------------------------------
  // API
  //

  /**
   * Returns all constants in single list.
   *
   * @return {@link IStridablesList}&lt; {@link ESkTechnologyObjOperationMode} &gt; - list of constants in order of
   *         declaraion
   */
  public static IStridablesList<ESkTechnologyObjOperationMode> asList() {
    if( list == null ) {
      list = new StridablesList<>( values() );
    }
    return list;
  }

  /**
   * Returns the constant by the ID.
   *
   * @param aId String - the ID
   * @return {@link ESkTechnologyObjOperationMode} - found constant
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   * @throws TsItemNotFoundRtException no constant found by specified ID
   */
  public static ESkTechnologyObjOperationMode getById( String aId ) {
    return asList().getByKey( aId );
  }

  /**
   * Finds the constant by the name.
   *
   * @param aName String - the name
   * @return {@link ESkTechnologyObjOperationMode} - found constant or <code>null</code>
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  public static ESkTechnologyObjOperationMode findByName( String aName ) {
    TsNullArgumentRtException.checkNull( aName );
    for( ESkTechnologyObjOperationMode item : values() ) {
      if( item.name.equals( aName ) ) {
        return item;
      }
    }
    return null;
  }

  /**
   * Returns the constant by the name.
   *
   * @param aName String - the name
   * @return {@link ESkTechnologyObjOperationMode} - found constant
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   * @throws TsItemNotFoundRtException no constant found by specified name
   */
  public static ESkTechnologyObjOperationMode getByName( String aName ) {
    return TsItemNotFoundRtException.checkNull( findByName( aName ) );
  }

}
