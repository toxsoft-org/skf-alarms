package org.toxsoft.skf.alarms.gui.km5;

import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tslib.coll.primtypes.*;
import org.toxsoft.core.tslib.coll.primtypes.impl.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.alarms.lib.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.km5.*;

/**
 * Contributes M5-model for {@link ISkAlarmService} manager classes.
 *
 * @author hazard157
 */
public class KM5AlarmsContributor
    extends KM5AbstractContributor {

  /**
   * Creator singleton.
   */
  public static final IKM5ContributorCreator CREATOR = KM5AlarmsContributor::new;

  private static final IStringList CONRTIBUTED_MODEL_IDS = new StringArrayList( //
      UsedUgwi4MessageInfoM5Model.MODEL_ID, //
      SkMessageInfoM5Model.MODEL_ID, //
      ISkAlarmConstants.CLSID_ALARM //
  );

  /**
   * Constructor.
   *
   * @param aConn {@link ISkConnection} - the connection
   * @param aDomain {@link IM5Domain} - connection domain
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  public KM5AlarmsContributor( ISkConnection aConn, IM5Domain aDomain ) {
    super( aConn, aDomain );
  }

  @Override
  protected IStringList papiCreateModels() {
    m5().addModel( new UsedUgwi4MessageInfoM5Model() );
    m5().addModel( new SkMessageInfoM5Model() );
    m5().addModel( new SkAlarmM5Model( skConn() ) );
    return CONRTIBUTED_MODEL_IDS;
  }

}
