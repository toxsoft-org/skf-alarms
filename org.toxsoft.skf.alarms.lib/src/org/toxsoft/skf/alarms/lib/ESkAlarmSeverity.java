package org.toxsoft.skf.alarms.lib;

import static org.toxsoft.skf.alarms.lib.l10n.ISkAlarmSharedResources.*;

import org.toxsoft.core.tslib.bricks.keeper.*;
import org.toxsoft.core.tslib.bricks.keeper.std.*;
import org.toxsoft.core.tslib.bricks.strid.*;
import org.toxsoft.core.tslib.bricks.strid.coll.*;
import org.toxsoft.core.tslib.bricks.strid.coll.impl.*;
import org.toxsoft.core.tslib.utils.errors.*;

/**
 * The alarm severity level.
 *
 * @author hazard157
 */
public enum ESkAlarmSeverity
    implements IStridable {

  /**
   * Warning about violation of normal technological regulations.
   */
  WARNING( "warning", STR_ALSEV_WARNING, STR_ALSEV_WARNING_D ), //$NON-NLS-1$

  /**
   * Critical emergency situation need immediate reaction.
   */
  CRITICAL( "critical", STR_ALSEV_CRITICAL, STR_ALSEV_CRITICAL_D ), //$NON-NLS-1$

  ;

  /**
   * The keeper ID.
   */
  public static final String KEEPER_ID = "ESkAlarmSeverity"; //$NON-NLS-1$

  /**
   * Keeper singleton.
   */
  public static final IEntityKeeper<ESkAlarmSeverity> KEEPER = new StridableEnumKeeper<>( ESkAlarmSeverity.class );

  private static IStridablesListEdit<ESkAlarmSeverity> list = null;

  private final String id;
  private final String name;
  private final String description;

  ESkAlarmSeverity( String aId, String aName, String aDescription ) {
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
   * @return {@link IStridablesList}&lt; {@link ESkAlarmSeverity} &gt; - list of constants in order of declaraion
   */
  public static IStridablesList<ESkAlarmSeverity> asList() {
    if( list == null ) {
      list = new StridablesList<>( values() );
    }
    return list;
  }

  /**
   * Returns the constant by the ID.
   *
   * @param aId String - the ID
   * @return {@link ESkAlarmSeverity} - found constant
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   * @throws TsItemNotFoundRtException no constant found by specified ID
   */
  public static ESkAlarmSeverity getById( String aId ) {
    return asList().getByKey( aId );
  }

  /**
   * Finds the constant by the name.
   *
   * @param aName String - the name
   * @return {@link ESkAlarmSeverity} - found constant or <code>null</code>
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  public static ESkAlarmSeverity findByName( String aName ) {
    TsNullArgumentRtException.checkNull( aName );
    for( ESkAlarmSeverity item : values() ) {
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
   * @return {@link ESkAlarmSeverity} - found constant
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   * @throws TsItemNotFoundRtException no constant found by specified name
   */
  public static ESkAlarmSeverity getByName( String aName ) {
    return TsItemNotFoundRtException.checkNull( findByName( aName ) );
  }

}
