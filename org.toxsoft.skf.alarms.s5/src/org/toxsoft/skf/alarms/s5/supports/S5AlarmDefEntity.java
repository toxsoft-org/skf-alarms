package org.toxsoft.skf.alarms.s5.supports;

import static org.toxsoft.core.tslib.utils.TsLibUtils.*;

import java.io.Serializable;

import javax.persistence.*;

import org.toxsoft.core.tslib.av.opset.IOptionSet;
import org.toxsoft.core.tslib.av.opset.impl.OptionSetKeeper;
import org.toxsoft.core.tslib.bricks.strid.impl.StridUtils;
import org.toxsoft.core.tslib.utils.errors.TsNullArgumentRtException;
import org.toxsoft.skf.alarms.lib.EAlarmPriority;
import org.toxsoft.skf.alarms.lib.ISkAlarmDef;

/**
 * Реализация объекта описания аларма для хранения в БД.
 *
 * @author dima
 * @author mvk
 */
@NamedQueries( {
    @NamedQuery( name = S5AlarmEntitiesUtils.QUERY_GET, query = "SELECT alarmDef FROM S5AlarmDefEntity alarmDef" ), } )
@Entity
public class S5AlarmDefEntity
    implements ISkAlarmDef, Serializable {

  private static final long serialVersionUID = 157157L;

  /**
   * Формат вывода toString()
   */
  private static final String TO_STRING_FORMAT = "%s[%s]: %s"; //$NON-NLS-1$

  /**
   * Уникальный идентификатор описания в системе
   */
  @Id
  private String id;

  /**
   * отображаемое имя
   */
  @Column( nullable = true )
  private String nmName;
  /**
   * описание сущности
   */
  @Column( nullable = true )
  private String description;

  /**
   * Значения всех расширенных атрибутов.
   */
  @Lob
  @Column( //
      nullable = false,
      insertable = true,
      updatable = true,
      unique = false )
  private String paramsString;

  /**
   * приоритет обработки аларма
   */
  @Column( nullable = false )
  private String priority;

  /**
   * текст сообщения аларма
   */
  @Column( nullable = true )
  private String message;

  /**
   * Конструктор без параметров
   */
  protected S5AlarmDefEntity() {
    // nop
  }

  /**
   * Lazy
   */
  private transient IOptionSet params;

  /**
   * Конструктор
   *
   * @param aId String идентификатор аларма
   * @param aMessage String текстовое сообщение для аларма
   * @param aParams {@link IOptionSet} параметры аларма
   * @throws TsNullArgumentRtException любой аргумент = null
   */
  public S5AlarmDefEntity( String aId, String aMessage, IOptionSet aParams ) {
    TsNullArgumentRtException.checkNulls( aId, aParams, aMessage, aParams );
    id = StridUtils.checkValidIdPath( aId );
    setPriority( EAlarmPriority.NORMAL );
    setName( EMPTY_STRING );
    setDescription( EMPTY_STRING );
    setParams( aParams );
    setMessage( aMessage );
  }

  /**
   * Конструктор по aAlarmDef
   *
   * @param aSkAlarmDef исходный аларм
   * @throws TsNullArgumentRtException аргумент = null
   */
  public S5AlarmDefEntity( ISkAlarmDef aSkAlarmDef ) {
    id = StridUtils.checkValidIdPath( aSkAlarmDef.id() );
    setPriority( aSkAlarmDef.priority() );
    setName( aSkAlarmDef.nmName() );
    setDescription( aSkAlarmDef.description() );
    setParams( aSkAlarmDef.params() );
    setMessage( aSkAlarmDef.message() );
  }

  // ------------------------------------------------------------------------------------
  // Открытое API
  //
  /**
   * Установить имя аларма
   *
   * @param aName String имя аларма
   * @throws TsNullArgumentRtException аргумент = null
   */
  public void setName( String aName ) {
    TsNullArgumentRtException.checkNull( aName );
    nmName = aName;
  }

  /**
   * Установить описание аларма
   *
   * @param aDescription String описание аларма
   * @throws TsNullArgumentRtException аргумент = null
   */
  public void setDescription( String aDescription ) {
    TsNullArgumentRtException.checkNull( aDescription );
    description = aDescription;
  }

  /**
   * Установить имя аларма
   *
   * @param aPriority {@link EAlarmPriority} важность аларма
   * @throws TsNullArgumentRtException аргумент = null
   */
  public void setPriority( EAlarmPriority aPriority ) {
    TsNullArgumentRtException.checkNull( aPriority );
    priority = aPriority.id();
  }

  /**
   * Установить значение всех параметров аларма
   *
   * @param aParams {@link IOptionSet} карта параметров и их значений
   * @throws TsNullArgumentRtException аргумент = null
   */
  public void setParams( IOptionSet aParams ) {
    TsNullArgumentRtException.checkNull( aParams );
    try {
      paramsString = OptionSetKeeper.KEEPER.ent2str( aParams );
    }
    catch( Throwable e ) {
      throw e;
    }
    params = null;
  }

  /**
   * Установить сообщение аларма
   *
   * @param aMessage String сообщение аларма
   * @throws TsNullArgumentRtException аргумент = null
   */
  public void setMessage( String aMessage ) {
    TsNullArgumentRtException.checkNull( aMessage );
    message = aMessage;
  }

  // ------------------------------------------------------------------------------------
  // Реализация интерфейса ISkAlarmDef
  //
  @Override
  public String id() {
    return id;
  }

  @Override
  public String nmName() {
    return nmName;
  }

  @Override
  public String description() {
    return description;
  }

  @Override
  public IOptionSet params() {
    if( params == null ) {
      params = OptionSetKeeper.KEEPER.str2ent( paramsString );
    }
    return params;
  }

  @Override
  public EAlarmPriority priority() {
    return EAlarmPriority.findById( priority );
  }

  @Override
  public String message() {
    return message;
  }

  // ------------------------------------------------------------------------------------
  // Переопределение Object
  //
  @Override
  public String toString() {
    return String.format( TO_STRING_FORMAT, id, nmName, message );
  }

  @Override
  public int hashCode() {
    int result = INITIAL_HASH_CODE;
    final int prime = PRIME;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
  public boolean equals( Object aObject ) {
    if( this == aObject ) {
      return true;
    }
    if( aObject == null ) {
      return false;
    }
    if( !(aObject instanceof ISkAlarmDef other) ) {
      return false;
    }
    if( !id.equals( other.id() ) ) {
      return false;
    }
    if( !nmName.equals( other.nmName() ) ) {
      return false;
    }
    if( !description.equals( other.description() ) ) {
      return false;
    }
    if( priority() != other.priority() ) {
      return false;
    }
    if( !message.equals( other.message() ) ) {
      return false;
    }
    return true;
  }

}
