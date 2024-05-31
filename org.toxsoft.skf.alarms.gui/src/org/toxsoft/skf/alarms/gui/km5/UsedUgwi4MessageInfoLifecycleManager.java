package org.toxsoft.skf.alarms.gui.km5;

import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tslib.bricks.validator.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.coll.helpers.*;
import org.toxsoft.core.tslib.gw.gwid.*;

/**
 * Lifecycle manager for {@link UsedUgwi4MessageInfoM5Model}.
 *
 * @author dima
 */
class UsedUgwi4MessageInfoLifecycleManager
    extends M5LifecycleManager<IUsedUgwi4MessageInfo, Object> {

  public UsedUgwi4MessageInfoLifecycleManager( IM5Model<IUsedUgwi4MessageInfo> aModel ) {
    super( aModel, true, true, true, false, null );
  }

  /**
   * Subclass may perform validation before instance creation.
   * <p>
   * In base class returns {@link ValidationResult#SUCCESS}, there is no need to call superclass method when overriding.
   *
   * @param aValues {@link IM5Bunch} - field values, never is <code>null</code>
   * @return {@link ValidationResult} - the validation result
   */
  @Override
  protected ValidationResult doBeforeCreate( IM5Bunch<IUsedUgwi4MessageInfo> aValues ) {
    return ValidationResult.SUCCESS;
  }

  /**
   * If creation is supported subclass must create the entity instance.
   * <p>
   * In base class throws an exception, never call superclass method when overriding.
   *
   * @param aValues {@link IM5Bunch} - field values, never is <code>null</code>
   * @return &lt;IUsedUgwi4MessageInfo&gt; - created instance
   */
  @Override
  protected IUsedUgwi4MessageInfo doCreate( IM5Bunch<IUsedUgwi4MessageInfo> aValues ) {
    return bunch2UsedUgwi( aValues );
  }

  private static IUsedUgwi4MessageInfo bunch2UsedUgwi( IM5Bunch<IUsedUgwi4MessageInfo> aValues ) {
    String idPath = aValues.getAsAv( UsedUgwi4MessageInfoM5Model.FID_IDPATH ).asString();
    Gwid usedUgwi = aValues.getAsAv( UsedUgwi4MessageInfoM5Model.FID_USED_UGWI ).asValobj();

    return new UsedUgwi4MessageInfo( idPath, usedUgwi );
  }

  /**
   * Subclass may perform validation before existing editing.
   * <p>
   * In base class returns {@link ValidationResult#SUCCESS}, there is no need to call superclass method when overriding.
   *
   * @param aValues {@link IM5Bunch} - field values, never is <code>null</code>
   * @return {@link ValidationResult} - the validation result
   */
  @Override
  protected ValidationResult doBeforeEdit( IM5Bunch<IUsedUgwi4MessageInfo> aValues ) {
    return ValidationResult.SUCCESS;
  }

  /**
   * If editing is supported subclass must edit the existing entity.
   * <p>
   * In base class throws an exception, never call superclass method when overriding.
   * <p>
   * The old values may be found in the {@link IM5Bunch#originalEntity()} which obviously is not <code>null</code>.
   *
   * @param aValues {@link IM5Bunch} - field values, never is <code>null</code>
   * @return &lt;IUsedUgwi4MessageInfo&gt; - created instance
   */
  @Override
  protected IUsedUgwi4MessageInfo doEdit( IM5Bunch<IUsedUgwi4MessageInfo> aValues ) {
    return bunch2UsedUgwi( aValues );
  }

  /**
   * Subclass may perform validation before remove existing entity.
   * <p>
   * In base class returns {@link ValidationResult#SUCCESS}, there is no need to call superclass method when overriding.
   *
   * @param aEntity &lt;IUsedUgwi4MessageInfo&gt; - the entity to be removed, never is <code>null</code>
   * @return {@link ValidationResult} - the validation result
   */
  @Override
  protected ValidationResult doBeforeRemove( IUsedUgwi4MessageInfo aEntity ) {
    return ValidationResult.SUCCESS;
  }

  /**
   * If removing is supported subclass must remove the existing entity.
   * <p>
   * In base class throws an exception, never call superclass method when overriding.
   *
   * @param aEntity &lt;IUsedUgwi4MessageInfo&gt; - the entity to be removed, never is <code>null</code>
   */
  @Override
  protected void doRemove( IUsedUgwi4MessageInfo aEntity ) {
    // TODO
  }

  /**
   * If enumeration is supported subclass must return list of entities.
   * <p>
   * In base class returns {@link IList#EMPTY}, there is no need to call superclass method when overriding.
   *
   * @return {@link IList}&lt;IUsedUgwi4MessageInfo&gt; - list of entities in the scope of maetr object
   */
  @Override
  protected IList<IUsedUgwi4MessageInfo> doListEntities() {
    return IList.EMPTY;
  }

  /**
   * If enumeration is supported subclass may allow items reordering.
   * <p>
   * In base class returns <code>null</code>, there is no need to call superclass method when overriding.
   * <p>
   * This method is called every time when user asks for {@link IM5ItemsProvider#reorderer()}.
   *
   * @return {@link IListReorderer}&lt;IUsedUgwi4MessageInfo&gt; - optional {@link IM5ItemsProvider#listItems()}
   *         reordering means
   */
  @Override
  protected IListReorderer<IUsedUgwi4MessageInfo> doGetItemsReorderer() {
    return null;
  }

}
