package org.toxsoft.skf.alarms.gui.km5;

import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tslib.bricks.validator.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.gw.ugwi.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.uskat.core.utils.msgen.*;

/**
 * Lifecycle manager for {@link ISkMessageInfo}.
 *
 * @author dima
 */
public class SkMessageInfoM5LifecycleManager
    extends M5LifecycleManager<ISkMessageInfo, Object> {

  /**
   * Constructor.
   *
   * @param aModel {@link IM5Model}&lt;T&gt; - the model
   * @throws TsNullArgumentRtException model is <code>null</code>
   */
  public SkMessageInfoM5LifecycleManager( IM5Model<ISkMessageInfo> aModel ) {
    super( aModel, true, true, true, true, null );
  }

  // ------------------------------------------------------------------------------------
  // M5LifecycleManager
  //

  @Override
  protected ValidationResult doBeforeCreate( IM5Bunch<ISkMessageInfo> aValues ) {
    return ValidationResult.SUCCESS;
  }

  @Override
  protected ISkMessageInfo doCreate( IM5Bunch<ISkMessageInfo> aValues ) {
    ISkMessageInfo retVal = bunch2UsedUgwi( aValues );
    return retVal;
  }

  @Override
  protected ValidationResult doBeforeEdit( IM5Bunch<ISkMessageInfo> aValues ) {
    return ValidationResult.SUCCESS;
  }

  @Override
  protected ISkMessageInfo doEdit( IM5Bunch<ISkMessageInfo> aValues ) {
    ISkMessageInfo retVal = bunch2UsedUgwi( aValues );
    return retVal;
  }

  @Override
  protected ValidationResult doBeforeRemove( ISkMessageInfo aEntity ) {
    return ValidationResult.SUCCESS;
  }

  @Override
  protected void doRemove( ISkMessageInfo aEntity ) {
    // TODO
  }

  @Override
  protected IList<ISkMessageInfo> doListEntities() {
    return IList.EMPTY;
  }

  // ------------------------------------------------------------------------------------
  // Package API
  //

  private static ISkMessageInfo bunch2UsedUgwi( IM5Bunch<ISkMessageInfo> aValues ) {
    String fmtStr = aValues.getAsAv( SkMessageInfoM5Model.ATRID_FMT_STR ).asString();
    IList<IUsedUgwi4MessageInfo> usedUgwiList = aValues.getAs( SkMessageInfoM5Model.CLBID_USED_UGWIES, IList.class );
    IStringMapEdit<Ugwi> usedUgwiMap = new StringMap<>();
    for( IUsedUgwi4MessageInfo uu : usedUgwiList ) {
      Ugwi ugwi = uu.usedUgwi();
      usedUgwiMap.put( uu.idPath(), ugwi );
    }
    return new SkMessageInfo( fmtStr, usedUgwiMap );
  }

}
