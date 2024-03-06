package org.toxsoft.skf.alarms.gui;

import org.toxsoft.core.tslib.utils.errors.*;
import org.toxsoft.core.tslib.utils.logs.impl.*;
import org.toxsoft.uskat.core.api.cmdserv.*;

/**
 * Вспомогательные методы для работы с командами.
 * <p>
 *
 * @author vs
 */
public class SkAlarmCmdUtils {

  /**
   * Возвращает признак того, что команда завершена неуспешно.
   *
   * @param aState ESkCommandState - состояние команды
   * @return <b>true</b> - команда завершена неуспешно<br>
   *         <b>false</b> - команда не завершена или завершена успешно
   */
  public static boolean isFailed( ESkCommandState aState ) {
    switch( aState ) {
      case FAILED:
      case TIMEOUTED:
      case UNHANDLED:
        return true;
      case EXECUTING:
      case SENDING:
      case SUCCESS:
        return false;
      default:
        throw new TsNotAllEnumsUsedRtException();
    }
  }

  /**
   * Записывает в log историю состояния команды.
   *
   * @param aCommand ISkCommand - команда
   */
  public static void logCommandHistory( ISkCommand aCommand ) {
    if( aCommand == null ) {
      return;
    }
    for( SkCommandState state : aCommand.statesHistory() ) {
      if( isFailed( state.state() ) ) {
        LoggerUtils.errorLogger().error( state.state().description() + ": " + aCommand.toString() ); //$NON-NLS-1$
      }
      else {
        LoggerUtils.defaultLogger().info( state.state().description() + ": " + aCommand.toString() ); //$NON-NLS-1$
      }
    }
  }

  /**
   * Возвращает строковое представление ошибки посылки или выполнения команды, или null если ошибок нет.
   *
   * @param aCommand ISkCommand - команда
   * @return String - строковое представление ошибки посылки или выполнения команды, или null если ошибок нет
   */
  public static String errorString( ISkCommand aCommand ) {
    if( aCommand == null ) {
      return "Command sending failed: cmd - null"; //$NON-NLS-1$
    }
    if( aCommand.isComplete() && aCommand.state().state() != ESkCommandState.SUCCESS ) {
      return aCommand.state().state().description() + ": " + aCommand.toString(); //$NON-NLS-1$
    }
    return null;
  }

  // /**
  // * Возвращает признак того, что команда завершена неуспешно.
  // *
  // * @param aState ESkCommandState - состояние команды
  // * @return <b>true</b> - команда завершена неуспешно<br>
  // * <b>false</b> - команда не завершена или завершена успешно
  // */
  // public static boolean isFailed( ESkCommandState aState ) {
  // switch( aState ) {
  // case FAILED:
  // case TIMEOUTED:
  // case UNHANDLED:
  // return true;
  // case EXECUTING:
  // case SENDING:
  // case SUCCESS:
  // return false;
  // default:
  // throw new TsNotAllEnumsUsedRtException();
  // }
  // }

  private SkAlarmCmdUtils() {
    // запрет на создание экземпляров
  }

}
