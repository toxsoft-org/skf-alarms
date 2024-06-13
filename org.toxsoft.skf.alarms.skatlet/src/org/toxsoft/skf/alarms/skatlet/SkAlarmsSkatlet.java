package org.toxsoft.skf.alarms.skatlet;

import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.alarms.skatlet.ISkResources.*;

import org.toxsoft.core.tslib.av.opset.impl.OptionSetUtils;
import org.toxsoft.uskat.core.impl.SkatletBase;

/**
 * Alarm skatlet Writer for GBH.
 *
 * @author mvk
 */
public class SkAlarmsSkatlet
    extends SkatletBase {

  // private SkAlarmProcessor alarmProcecessor;

  /**
   * Constructor.
   */
  public SkAlarmsSkatlet() {
    super( SkAlarmsSkatlet.class.getSimpleName(), OptionSetUtils.createOpSet( //
        TSID_NAME, STR_N_SKATLET, //
        TSID_DESCRIPTION, STR_D_SKATLET //
    ) );
  }

  // ------------------------------------------------------------------------------------
  // SkatletBase
  //
  @Override
  public void start() {
    super.start();

  }

  @Override
  public boolean queryStop() {
    super.queryStop();

    return true;
  }

  @Override
  public boolean isStopped() {
    return true;
  }
}
