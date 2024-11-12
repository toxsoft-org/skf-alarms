package org.toxsoft.skf.alarms.gui;

import org.toxsoft.core.tsgui.dialogs.Messages;
import org.toxsoft.skf.alarms.gui.km5.*;
import org.toxsoft.skf.alarms.gui.panels.impl.*;

/**
 * Localizable resources.
 *
 * @author hazard157
 */
public interface ISkResources {

  String STR_DO_IT   = Messages.getString( "STR_DO_IT" );   //$NON-NLS-1$
  String STR_DO_IT_D = Messages.getString( "STR_DO_IT_D" ); //$NON-NLS-1$

  /**
   * {@link SkMessageInfoM5Model}
   */
  String STR_N_FDEF_FMT_STR = "format string";
  String STR_D_FDEF_FMT_STR = "format string for use in OptionSetUtils#format(String, IOptionSet)";
  String STR_N_USED_UGWIES  = "used Ugwi";
  String STR_D_USED_UGWIES  = "used Ugwi for alarm message";

  /**
   * {@link UsedUgwi4MessageInfoM5Model}
   */
  String STR_N_IDPATH    = "idPath";
  String STR_D_IDPATH    = "key in Map to link Ugwi ";
  String STR_N_USED_UGWI = "used Ugwi";
  String STR_D_USED_UGWI = "Ugwi assotiated with key in Map";

  String STR_N_EV_GWID      = "Event Gwid";
  String STR_D_EV_GWID      = "Green world id события";
  String STR_N_EVENT_PARAMS = "Params of event";
  String STR_D_EVENT_PARAMS = "Параметры события";

  /**
   * {@link AcknowledgeDlg}
   */
  String DLG_C_ALARM_ACKNOWLEDGE = "Квитирование тревоги";
  String DLG_T_ALARM_ACKNOWLEDGE = "Квитировать тревогу";
  String STR_L_ALARM_COMMENT     = "Комментарий";
  String STR_N_ALARM_ACKNOWLEDGE = "Подтвердить";                             // Acknowledge
  String STR_D_ALARM_ACKNOWLEDGE = "Подтвердить отмеченные оповещения";       // Acknowledge marked alers
  String STR_N_EVENT_TIME        = "Время";                                   // Time
  String STR_D_EVENT_TIME        = "Момент времени, когда произошло событие"; // Time moment when event happaned

  String STR_N_ALARM_NAME = "Имя тревоги"; // Alarm name
  String STR_D_ALARM_NAME = "Имя тревоги"; // The alarm name

  String STR_N_ALERT_EVENT_MESSAGE = "Оповещение о событии"; // Alert event message
  String STR_D_ALERT_EVENT_MESSAGE = "Оповещение о событии"; // The alert event message

  String STR_N_ALERT_SEVERITY = "Важность";         // Severity
  String STR_D_ALERT_SEVERITY = "Важность тревоги"; // Severity of alarm

  String STR_WARNING_SEVERITY_ALARM  = "Внимание";
  String STR_CRITICAL_SEVERITY_ALARM = "Критический";

  String STR_N_EVENT_TYPE_NAME = "Тип события";
  String STR_D_EVENT_TYPE_NAME = "Тип события";

  String STR_ALERT_EVENT_TYPE       = "Тревога";
  String STR_ACKNOWLEDGE_EVENT_TYPE = "Внимание";
  String STR_MUTED_EVENT_TYPE       = "Отключение";
  String STR_UNMUTED_EVENT_TYPE     = "Включение";

}
