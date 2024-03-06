package org.toxsoft.skf.alarms.gui.m5;

/**
 * Локализуемые ресурсы.
 *
 * @author dima
 */
@SuppressWarnings( "nls" )
interface ISkResources {

  /**
   * {@link SkAlarmPanel}
   */
  String STR_N_QUIT_ALARMS          = "Квитировать тревоги";
  String STR_D_QUIT_ALARMS          = "Квитирование выбранных тревог";
  String STR_N_ASK_USER_QUIT_ALARMS = "Квитировать выбранные тревоги?";
  String STR_N_REMOVE_ALARMS        = "Удалить тревоги";
  String STR_D_REMOVE_ALARMS        = "Безусловное удаление выбранных тревог из панели";

  /**
   * {@link SkAlarmM5Model}
   */
  String STR_N_PARAM_AUTHOR = "Автор";
  String STR_D_PARAM_AUTHOR = "Автор тревоги";

  String STR_N_PARAM_MESSAGE = "Сообщение";
  String STR_D_PARAM_MESSAGE = "Сообщение тревоги";

  String STR_N_PARAM_TIME = "Время";
  String STR_D_PARAM_TIME = "Время тревоги";

  String STR_N_ALARM = "Тревоги";
  String STR_D_ALARM = "Список тревог";

  String STR_N_PARAM_PRIORITY  = "Приоритет";
  String STR_D_PARAM_PRIORITY  = "Приоритет тревоги";
  String STR_HIGH_PR_ALARM     = "Авария";
  String STR_CRITICAL_PR_ALARM = "Авария";
  String STR_NORMAL_PR_ALARM   = "Предупреждение";
}
