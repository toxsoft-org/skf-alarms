package org.toxsoft.skf.alarms.gui.km5;

import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.skf.alarms.lib.ISkAlarmConstants.*;

import org.toxsoft.core.tsgui.m5.*;
import org.toxsoft.core.tsgui.m5.model.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tslib.math.cond.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.alarms.lib.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.km5.*;
import org.toxsoft.uskat.core.utils.msgen.*;

/**
 * M5-model of {@link ISkAlarm} Sk-objects
 *
 * @author hazard157
 */
public class SkAlarmM5Model
    extends KM5ModelBasic<ISkAlarm> {

  /**
   * Attribute {@link ISkAlarm#severity()}.
   */
  public final IM5AttributeFieldDef<ISkAlarm> SEVERITY = new KM5AttributeFieldDef<>( ATRINF_SEVERITY );

  /**
   * Field {@link ISkAlarm#alertCondition()}.
   */
  public final IM5FieldDef<ISkAlarm, ITsCombiCondInfo> ALERT_CONDITION =
      new M5FieldDef<>( CLBID_ALERT_CONDITION, ITsCombiCondInfo.class ) {

        @Override
        protected void doInit() {
          setName( CLBINF_ALERT_CONDITION.nmName() );
          setDescription( CLBINF_ALERT_CONDITION.description() );
          setDefaultValue( ITsCombiCondInfo.NEVER );

          // FIXME set VALED editor name

          setFlags( M5FF_DETAIL );
        }

        protected ITsCombiCondInfo doGetFieldValue( ISkAlarm aEntity ) {
          return aEntity.alertCondition();
        }

      };

  /**
   * Field {@link ISkAlarm#messageInfo()}.
   */
  public final IM5FieldDef<ISkAlarm, ISkMessageInfo> MESSAGE_INFO =
      new M5FieldDef<>( CLBID_MESSAGE_INFO, ISkMessageInfo.class ) {

        @Override
        protected void doInit() {
          setName( CLBINF_MESSAGE_INFO.nmName() );
          setDescription( CLBINF_MESSAGE_INFO.description() );
          setDefaultValue( ISkMessageInfo.NONE );

          // FIXME set VALED editor name

          setFlags( M5FF_DETAIL );
        }

        protected ISkMessageInfo doGetFieldValue( ISkAlarm aEntity ) {
          return aEntity.messageInfo();
        }

      };

  /**
   * Constructor.
   *
   * @param aConn {@link ISkConnection} - Sk-connection to be used in constructor
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  public SkAlarmM5Model( ISkConnection aConn ) {
    super( CLSID_ALARM, ISkAlarm.class, aConn );
    addFieldDefs( STRID, SEVERITY, NAME, DESCRIPTION
    // , ALERT_CONDITION, MESSAGE_INFO
    );
  }

  @Override
  protected IM5LifecycleManager<ISkAlarm> doCreateLifecycleManager( Object aMaster ) {
    return new SkAlarmM5LifecycleManager( this, ISkConnection.class.cast( aMaster ) );
  }

}
