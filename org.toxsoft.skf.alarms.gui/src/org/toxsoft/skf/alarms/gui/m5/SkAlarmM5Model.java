package org.toxsoft.skf.alarms.gui.m5;

import static org.toxsoft.core.tsgui.m5.IM5Constants.*;
import static org.toxsoft.core.tsgui.valed.api.IValedControlConstants.*;
import static org.toxsoft.core.tslib.av.impl.AvUtils.*;
import static org.toxsoft.core.tslib.av.metainfo.IAvMetaConstants.*;
import static org.toxsoft.skf.alarms.gui.m5.ISkResources.*;

import java.text.*;
import java.util.*;
import java.util.regex.*;
import java.util.regex.Pattern;

import org.eclipse.jface.resource.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.ui.plugin.*;
import org.toxsoft.core.tsgui.graphics.icons.*;
import org.toxsoft.core.tsgui.m5.model.impl.*;
import org.toxsoft.core.tsgui.valed.controls.av.*;
import org.toxsoft.core.tslib.av.*;
import org.toxsoft.core.tslib.utils.*;
import org.toxsoft.skf.alarms.gui.*;
import org.toxsoft.skf.alarms.lib.*;
import org.toxsoft.uskat.core.api.objserv.*;
import org.toxsoft.uskat.core.connection.*;
import org.toxsoft.uskat.core.gui.conn.*;

/**
 * M5-model of {@link ISkAlarm}.
 *
 * @author max
 * @author dima
 */
public class SkAlarmM5Model
    extends M5Model<ISkAlarm> {

  private static Pattern unitNumPattern = Pattern.compile( "[a-z_A-Z]+([\\d]+)" ); //$NON-NLS-1$

  /**
   * картинка для аларма с приоритетом CRITICAL
   */
  private static final ImageDescriptor imgDescrCritical =
      AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, "icons/is16x16/criticalPriorityAlarm.png" ); //$NON-NLS-1$
  private static final Image           criticalImage    = imgDescrCritical.createImage();

  /**
   * картинка для аларма с приоритетом HIGH
   */
  private static final ImageDescriptor imgDescrHigh =
      AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, "icons/is16x16/highPriorityAlarm.png" ); //$NON-NLS-1$
  private static final Image           highImage    = imgDescrHigh.createImage();

  /**
   * картинка для аларма с приоритетом NORMAL
   */
  private static final ImageDescriptor imgDescrNormal =
      AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, "icons/is16x16/normalPriorityAlarm.png" ); //$NON-NLS-1$
  private static final Image           normalImage    = imgDescrNormal.createImage();

  /**
   * картинка для аларма с приоритетом LOW
   */
  private static final ImageDescriptor imgDescrLow =
      AbstractUIPlugin.imageDescriptorFromPlugin( Activator.PLUGIN_ID, "icons/is16x16/infoPriorityAlarm.png" ); //$NON-NLS-1$
  private static final Image           lowImage    = imgDescrLow.createImage();

  /**
   * формат для отображения метки времени
   */
  private static final String     timestampFormatString = "dd.MM.yy HH:mm:ss.SSS ";                     //$NON-NLS-1$
  private static final DateFormat timestampFormat       = new SimpleDateFormat( timestampFormatString );

  /**
   * Model id
   */
  public static final String MODEL_ID = "sk.Alarm"; //$NON-NLS-1$

  /**
   * Print Model id
   */
  public static final String PRINT_MODEL_ID = MODEL_ID + ".print"; //$NON-NLS-1$

  /**
   * Author of alarm
   */
  public static final String FID_AUTHOR = "author"; //$NON-NLS-1$

  /**
   * Message of alarm
   */
  public static final String FID_MESSAGE = "message"; //$NON-NLS-1$

  /**
   * Time of alarm
   */
  public static final String FID_TIME = "time"; //$NON-NLS-1$

  /**
   * Priority of alarm
   */
  public static final String FID_PRIORITY = "priority"; //$NON-NLS-1$

  /**
   * Attribute {@link ISkAlarm#authorId() } author of alarm
   */
  public M5AttributeFieldDef<ISkAlarm> AUTHOR = new M5AttributeFieldDef<>( FID_AUTHOR, EAtomicType.STRING, //
      TSID_NAME, STR_N_PARAM_AUTHOR, //
      TSID_DESCRIPTION, STR_D_PARAM_AUTHOR, //
      OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( ISkAlarm aEntity ) {
      // dima 17.01.23
      // return avStr( aEntity.authorId().strid() );
      ISkConnectionSupplier connSup = tsContext().get( ISkConnectionSupplier.class );
      ISkConnection conn = connSup.defConn();
      ISkObject author = conn.coreApi().objService().find( aEntity.authorId() );
      return avStr( author.nmName() );
    }

  };

  /**
   * Attribute {@link ISkAlarm#message() } message of alarm
   */
  public M5AttributeFieldDef<ISkAlarm> MESSAGE = new M5AttributeFieldDef<>( FID_MESSAGE, EAtomicType.STRING, //
      TSID_NAME, STR_N_PARAM_MESSAGE, //
      TSID_DESCRIPTION, STR_D_PARAM_MESSAGE, //
      OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( ISkAlarm aEntity ) {
      ISkConnectionSupplier connSup = tsContext().get( ISkConnectionSupplier.class );
      ISkConnection conn = connSup.defConn();
      ISkObject author = conn.coreApi().objService().find( aEntity.authorId() );
      String unitNum = unitNumber( author.strid() );

      return avStr( aEntity.message() + unitNum );
    }

  };

  /**
   * Attribute {@link ISkAlarm#timestamp() } time of alarm
   */
  public M5AttributeFieldDef<ISkAlarm> TIME = new M5AttributeFieldDef<>( FID_TIME, EAtomicType.STRING, //
      TSID_NAME, STR_N_PARAM_TIME, //
      TSID_DESCRIPTION, STR_D_PARAM_TIME, //
      OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( ISkAlarm aEntity ) {
      return avStr( timestampFormat.format( new Date( aEntity.timestamp() ) ) );
    }

  };

  /**
   * Attribute {@link ISkAlarm#message() } message of alarm
   */
  public M5AttributeFieldDef<ISkAlarm> PRIORITY = new M5AttributeFieldDef<>( FID_PRIORITY, EAtomicType.STRING, //
      TSID_NAME, STR_N_PARAM_PRIORITY, //
      TSID_DESCRIPTION, STR_D_PARAM_PRIORITY, //
      OPID_EDITOR_FACTORY_NAME, ValedAvStringText.FACTORY_NAME //
  ) {

    @Override
    protected void doInit() {
      setFlags( M5FF_COLUMN );
    }

    protected IAtomicValue doGetFieldValue( ISkAlarm aEntity ) {
      IAtomicValue retVal = avStr( TsLibUtils.EMPTY_STRING );
      EAlarmPriority alarmPriority = aEntity.priority();
      switch( alarmPriority ) {
        case HIGH: {
          retVal = avStr( STR_HIGH_PR_ALARM );
          break;
        }
        case CRITICAL:
          retVal = avStr( STR_CRITICAL_PR_ALARM );
          break;
        case INFO:
          break;
        case LOW:
          break;
        case NORMAL:
          retVal = avStr( STR_NORMAL_PR_ALARM );
          break;
        default:
          break;
      }
      return retVal;
    }

    @Override
    protected Image doGetFieldValueIcon( ISkAlarm aEntity, EIconSize aIconSize ) {
      Image retVal = highImage;
      EAlarmPriority alarmPriority = aEntity.priority();
      switch( alarmPriority ) {
        case HIGH: {
          retVal = highImage;
          break;
        }
        case CRITICAL:
          retVal = criticalImage;
          break;
        case INFO:
          retVal = lowImage;
          break;
        case LOW:
          retVal = lowImage;
          break;
        case NORMAL:
          retVal = normalImage;
          break;
        default:
          break;
      }
      return retVal;
    }

  };

  /**
   * Constructor
   *
   * @param aForPrint - attribute signs the model for prints (if true).
   */
  public SkAlarmM5Model( boolean aForPrint ) {
    super( aForPrint ? PRINT_MODEL_ID : MODEL_ID, ISkAlarm.class );
    setNameAndDescription( STR_N_ALARM, STR_D_ALARM );
    // add fields
    addFieldDefs( PRIORITY, TIME, AUTHOR, MESSAGE );
  }

  /**
   * Номер агрегата в составе которого работает данная сущность
   *
   * @param aObjId id сущности
   * @return строка номера агрегата
   */
  @SuppressWarnings( "nls" )
  private static String unitNumber( String aObjId ) {
    String retVal = TsLibUtils.EMPTY_STRING;
    // ищем первое число в составе strid, в ci это номер компрессора
    Matcher n = unitNumPattern.matcher( aObjId );
    if( n.find() ) {
      retVal = " агрегат №" + n.group( 1 );
    }
    return retVal;
  }

}
