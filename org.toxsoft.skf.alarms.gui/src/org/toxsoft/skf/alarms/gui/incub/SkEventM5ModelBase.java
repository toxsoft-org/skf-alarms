package org.toxsoft.skf.alarms.gui.incub;

import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tslib.av.EAtomicType.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.uskat.core.ISkHardConstants.*;

import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.m5.std.models.av.*;
import org.toxsoft.core.tslib.av.misc.*;
import org.toxsoft.core.tslib.coll.*;
import org.toxsoft.core.tslib.gw.gwid.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.uskat.core.api.evserv.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.km5.*;

/**
 * Base class to implement the M5-model of different events {@link SkEvent}.
 *
 * @author hazard157
 */
public class SkEventM5ModelBase
    extends KM5ConnectedModelBase<SkEvent> {

  public static final String FID_EV_TIMESTAMP = "EvTimestamp"; // FIXME actual model ID ?
  public static final String FID_EV_GWID      = "EvGwid";      // FIXME actual model ID ?
  public static final String FID_EVENT_ID     = "EventId";     // FIXME actual model ID ?
  public static final String FID_PARAM_VALUES = "ParamValues"; // FIXME actual model ID ?

  /**
   * Attribute {@link SkEvent#timestamp()}
   */
  public final IM5AttributeFieldDef<SkEvent> EV_TIMESTAMP = new M5AttributeFieldDef<>( FID_EV_TIMESTAMP, TIMESTAMP, //
      TSID_NAME, "Timestamp", //
      TSID_DESCRIPTION, "Time moment when event happaned", //
      TSID_DEFAULT_VALUE, AV_TIME_0, //
      TSID_FORMAT_STRING, "%tF %tT" //$NON-NLS-1$
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    @Override
    protected String doGetFieldValueName( SkEvent aEntity ) {
      return String.valueOf( aEntity.timestamp() );
    }
  };

  /**
   * Attribute {@link SkEvent#eventGwid()}.
   */
  public final IM5AttributeFieldDef<SkEvent> EV_GWID = new M5AttributeFieldDef<>( FID_EV_GWID, VALOBJ, //
      TSID_NAME, "GWID", //
      TSID_DESCRIPTION, "The event GWID", //
      TSID_KEEPER_ID, Gwid.KEEPER_ID, //
      TSID_DEFAULT_VALUE, avValobj( Gwid.NONE_CONCR_EVENT ) //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }
  };

  /**
   * Attribute event source object class ID (from the GWID {@link SkEvent#eventGwid()}).
   */
  public final IM5AttributeFieldDef<SkEvent> CLASS_ID = new M5AttributeFieldDef<>( AID_CLASS_ID, STRING, //
      TSID_NAME, "Class ID", //
      TSID_DESCRIPTION, "The event source object class ID", //
      TSID_DEFAULT_VALUE, avStr( NONE_ID ) //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }
  };

  /**
   * Attribute event source object STRID (from the GWID {@link SkEvent#eventGwid()}).
   */
  public final IM5AttributeFieldDef<SkEvent> STRID = new M5AttributeFieldDef<>( AID_STRID, STRING, //
      TSID_NAME, "STRID", //
      TSID_DESCRIPTION, "The event source object STRID", //
      TSID_DEFAULT_VALUE, avStr( NONE_ID ) //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }
  };

  /**
   * Attribute event ID (from the GWID {@link SkEvent#eventGwid()}).
   */
  public final IM5AttributeFieldDef<SkEvent> EVENT_ID = new M5AttributeFieldDef<>( FID_EVENT_ID, STRING, //
      TSID_NAME, "Class ID", //
      TSID_DESCRIPTION, "The event source object class ID", //
      TSID_DEFAULT_VALUE, avStr( NONE_ID ) //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }
  };

  /**
   * Field {@link SkEvent#paramValues()} as a {@link IList}&lt;{@link IdValue}&gt;
   */
  public final IM5MultiModownFieldDef<SkEvent, IdValue> PARAM_VALUES =
      new M5MultiModownFieldDef<>( FID_PARAM_VALUES, IdValueM5Model.MODEL_ID ) {

        @Override
        protected void doInit() {
          setNameAndDescription( "Parameters", "The event parameters values" );
          setFlags( M5FF_DETAIL | M5FF_COLUMN );
        }

        protected IList<IdValue> doGetFieldValue( SkEvent aEntity ) {
          return IdValue.makeIdValuesCollFromOptionSet( aEntity.paramValues() ).values();
        }

      };

  /**
   * Constructor.
   *
   * @param aModelId String - the model ID
   * @param aConn {@link ISkConnection} - Sk-connection to be used in constructor
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   * @throws TsIllegalArgumentRtException identifier is not an IDpath
   */
  public SkEventM5ModelBase( String aModelId, ISkConnection aConn ) {
    super( aModelId, SkEvent.class, aConn );
  }

}
