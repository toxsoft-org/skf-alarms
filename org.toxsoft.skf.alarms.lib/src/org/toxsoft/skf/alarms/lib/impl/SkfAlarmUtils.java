package org.toxsoft.skf.alarms.lib.impl;

import org.toxsoft.core.tslib.math.cond.checker.*;
import org.toxsoft.core.tslib.utils.valobj.*;
import org.toxsoft.skf.alarms.lib.*;
import org.toxsoft.skf.alarms.lib.checkers.*;
import org.toxsoft.uskat.core.*;
import org.toxsoft.uskat.core.api.*;
import org.toxsoft.uskat.core.api.ugwis.*;
import org.toxsoft.uskat.core.impl.*;

/**
 * Initialization and utility methods.
 *
 * @author hazard157
 */
public class SkfAlarmUtils {

  /**
   * Core handler to register all registered Sk-connection bound {@link ISkUgwiKind} when connection opens.
   */
  private static final ISkCoreExternalHandler coreRegistrationHandler = aCoreApi -> {
    ISkAlarmService alarmService = aCoreApi.getService( ISkAlarmService.SERVICE_ID );
    ITsCheckerTopicManager<ISkCoreApi> tm = alarmService.getAlarmCheckersTopicManager();
    tm.registerType( new AlertCheckerRtdataVsConstType() );
    tm.registerType( new AlertCheckerRtdataVsAttrType() );
  };

  /**
   * The plugin initialization must be called before any action to access classes in this plugin.
   */
  public static void initialize() {
    TsValobjUtils.registerKeeperIfNone( ESkAlarmSeverity.KEEPER_ID, ESkAlarmSeverity.KEEPER );
    SkCoreUtils.registerSkServiceCreator( SkAlarmService.CREATOR );
    SkCoreUtils.registerCoreApiHandler( coreRegistrationHandler );
  }

  /**
   * No subclasses.
   */
  private SkfAlarmUtils() {
    // nop
  }

}
