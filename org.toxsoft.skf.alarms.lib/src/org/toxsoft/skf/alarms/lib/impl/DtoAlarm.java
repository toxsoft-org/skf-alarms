package org.toxsoft.skf.alarms.lib.impl;

import static org.toxsoft.core.tslib.utils.TsLibUtils.*;
import static org.toxsoft.skf.alarms.lib.ISkAlarmConstants.*;

import org.toxsoft.core.tslib.bricks.keeper.*;
import org.toxsoft.core.tslib.bricks.keeper.AbstractEntityKeeper.*;
import org.toxsoft.core.tslib.bricks.strid.impl.*;
import org.toxsoft.core.tslib.bricks.strio.*;
import org.toxsoft.core.tslib.gw.skid.*;
import org.toxsoft.core.tslib.math.cond.*;
import org.toxsoft.core.tslib.math.cond.impl.*;
import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.skf.alarms.lib.*;
import org.toxsoft.uskat.core.*;
import org.toxsoft.uskat.core.api.objserv.*;
import org.toxsoft.uskat.core.api.sysdescr.*;
import org.toxsoft.uskat.core.impl.dto.*;
import org.toxsoft.uskat.core.utils.msgen.*;

/**
 * {@link IDtoAlarm} editable implementation.
 *
 * @author hazard157
 */
public class DtoAlarm
    extends Stridable
    implements IDtoAlarm {

  // public static final String KEEPER_ID = "DtoAlarm"; //$NON-NLS-1$

  /**
   * The keeper singleton.
   */
  public static final IEntityKeeper<IDtoAlarm> KEEPER =
      new AbstractEntityKeeper<>( IDtoAlarm.class, EEncloseMode.ENCLOSES_BASE_CLASS, null ) {

        @Override
        protected void doWrite( IStrioWriter aSw, IDtoAlarm aEntity ) {
          aSw.writeAsIs( aEntity.id() );
          aSw.writeSeparatorChar();
          aSw.writeQuotedString( aEntity.nmName() );
          aSw.writeSeparatorChar();
          aSw.writeQuotedString( aEntity.description() );
          aSw.writeSeparatorChar();
          ESkAlarmSeverity.KEEPER.write( aSw, aEntity.severity() );
          aSw.writeSeparatorChar();
          TsCombiCondInfo.KEEPER.write( aSw, aEntity.firingCondition() );
          aSw.writeSeparatorChar();
          SkMessageInfo.KEEPER.write( aSw, aEntity.messageInfo() );
        }

        @Override
        protected IDtoAlarm doRead( IStrioReader aSr ) {
          String id = aSr.readIdPath();
          aSr.ensureSeparatorChar();
          String name = aSr.readQuotedString();
          aSr.ensureSeparatorChar();
          String description = aSr.readQuotedString();
          aSr.ensureSeparatorChar();
          ESkAlarmSeverity severity = ESkAlarmSeverity.KEEPER.read( aSr );
          aSr.ensureSeparatorChar();
          ITsCombiCondInfo condInfo = TsCombiCondInfo.KEEPER.read( aSr );
          aSr.ensureSeparatorChar();
          ISkMessageInfo msgInfo = SkMessageInfo.KEEPER.read( aSr );
          aSr.ensureSeparatorChar();
          return new DtoAlarm( id, name, description, severity, condInfo, msgInfo );
        }
      };

  private ESkAlarmSeverity severity        = ESkAlarmSeverity.WARNING;
  private ITsCombiCondInfo firingCondition = ITsCombiCondInfo.NEVER;
  private ISkMessageInfo   messageInfo     = new SkMessageInfo( EMPTY_STRING );

  /**
   * Constructor.
   *
   * @param aId String - the alarm ID (an IDpath)
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   * @throws TsIllegalArgumentRtException ID is not an IDpath
   */
  public DtoAlarm( String aId ) {
    super( aId );
  }

  private DtoAlarm( String aId, String aName, String aDescription, ESkAlarmSeverity aSeverity,
      ITsCombiCondInfo aCondInfo, ISkMessageInfo aMessageInfo ) {
    super( aId );
    TsNullArgumentRtException.checkNulls( aSeverity, aCondInfo, aMessageInfo );
    setNameAndDescription( aName, aDescription );
    severity = aSeverity;
    firingCondition = aCondInfo;
    messageInfo = aMessageInfo;
  }

  /**
   * Creates editable {@link DtoAlarm} with all properties filled.
   * <p>
   * If object with specified SKID exists then result will be filled by exiting properties, otherwise default or an
   * empty values will be applied.
   * <p>
   * Created instance does not have system attribute values in the {@link IDtoObject#attrs()} set.
   *
   * @param aAlarmId String - the alarm ID
   * @param aCoreApi {@link ISkSysdescr} - class info provider
   * @return {@link DtoFullObject} - created instance
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   * @throws TsItemNotFoundRtException no such class as {@link Skid#classId()}
   */
  public static DtoAlarm makeAlarm( String aAlarmId, ISkCoreApi aCoreApi ) {
    TsNullArgumentRtException.checkNulls( aAlarmId, aCoreApi );
    Skid skid = new Skid( CLSID_ALARM, aAlarmId );
    DtoFullObject obj = DtoFullObject.createDtoFullObject( skid, aCoreApi );
    DtoAlarm dto = new DtoAlarm( aAlarmId, obj.nmName(), obj.description(), //
        obj.attrs().getValobj( ATRID_SEVERITY ), //
        TsCombiCondInfo.KEEPER.str2ent( obj.clobs().getByKey( CLBID_ALERT_CONDITION ) ), //
        SkMessageInfo.KEEPER.str2ent( obj.clobs().getByKey( CLBID_MESSAGE_INFO ) ) //
    );
    return dto;
  }

  // ------------------------------------------------------------------------------------
  // IDtoAlarm
  //

  @Override
  public ESkAlarmSeverity severity() {
    return severity;
  }

  @Override
  public ITsCombiCondInfo firingCondition() {
    return firingCondition;
  }

  @Override
  public ISkMessageInfo messageInfo() {
    return messageInfo;
  }

  // ------------------------------------------------------------------------------------
  // API
  //

  /**
   * Sets {@link #severity()} value.
   *
   * @param aSeverity {@link ESkAlarmSeverity} - the alarm severity
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  public void setSeverity( ESkAlarmSeverity aSeverity ) {
    TsNullArgumentRtException.checkNull( aSeverity );
    severity = aSeverity;
  }

  /**
   * Sets {@link #firingCondition()} value.
   *
   * @param aCondInfo {@link ITsCombiCondInfo} - the alert firing condition
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  public void setFiringCondition( ITsCombiCondInfo aCondInfo ) {
    TsNullArgumentRtException.checkNull( aCondInfo );
    firingCondition = aCondInfo;
  }

  /**
   * Sets {@link #messageInfo()} value.
   *
   * @param aMessageInfo {@link ISkMessageInfo} = the message
   * @throws TsNullArgumentRtException any argument = <code>null</code>
   */
  public void setMessageInfo( ISkMessageInfo aMessageInfo ) {
    TsNullArgumentRtException.checkNull( aMessageInfo );
    messageInfo = aMessageInfo;
  }

}
