package org.toxsoft.skf.alarms.s5.generator;

/**
 * Локализуемые ресурсы.
 *
 * @author mvk
 */
@SuppressWarnings( "nls" )
interface IS5Resources {

  // ------------------------------------------------------------------------------------
  // Тексты сообщений
  //
  String MSG_ADD_ALARM_DEF   = "В системе зарегистрированно описание аларма %s";
  String MSG_ALARM_ON        = "Включение аларма: %s. authorId = %s";
  String MSG_ALARM_OFF       = "Выключение аларма: %s. authorId = %s";
  String MSG_ADD_ALARM       = "addAlarm(...): aAuthorId = %s, alarmId = %s, predicate = %s";
  String MSG_REGISTRY_ALARMS = "Количество алармов для регистрации: %d";

  // ------------------------------------------------------------------------------------
  // Тексты ошибок
  //
  String MSG_ERR_ALARM_DEF_NOT_FOUND =
      "В системе не найдено описание аларма. Генератору запрещно автоматически проводить регистрацию описаний алармов %s";
  String ERR_ALARM_DEF_ALREADY_EXIST = "Аларм %s [authorId = %s] уже зарегистрирован для генерации";
  String ERR_SILENT_MODE             =
      "%s. Запрет генерации аларма после запуска сервера. Время до окончания запрета %d (мсек)";
  String ERR_CURRDATA_NOT_FOUND      = "Текущее данное не существует: aObjId = %s, aDataId = %s";

}
