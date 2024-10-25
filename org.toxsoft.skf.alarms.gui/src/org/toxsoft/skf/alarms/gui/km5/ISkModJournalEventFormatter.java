package org.toxsoft.skf.alarms.gui.km5;

import org.eclipse.swt.graphics.*;
import org.toxsoft.core.tsgui.bricks.ctx.*;
import org.toxsoft.core.tsgui.graphics.icons.*;
import org.toxsoft.uskat.core.api.evserv.*;

/**
 * Интерфейс форматировщика текста для отображения событий в таблицах.
 *
 * @author goga, dima
 */
public interface ISkModJournalEventFormatter {

  /**
   * Формирует краткий однострочный текст для вывода в строке таблицы.
   *
   * @param aEvent {@link SkEvent} - отображаемое событие
   * @param aContext
   * @return String - сформированный текст, не бывает <code>null</code>
   */
  String formatShortText( SkEvent aEvent, ITsGuiContext aContext );

  /**
   * Формирует длинный (скорее всего, многострочный) текст для вывода в в поле деетального просмотра таблицы.
   *
   * @param aEvent {@link SkEvent} - отображаемое событие
   * @param aContext
   * @return String - сформированный текст, не бывает <code>null</code>
   */
  String formatLongText( SkEvent aEvent, ITsGuiContext aContext );

  /**
   * Возвращает значок заданного размера.
   *
   * @param aEvent {@link SkEvent} - отображаемое событие
   * @param aIconSize {@link EIconSize} - запрошенный размер значка
   * @param aContext
   * @return {@link Image} - значок для отображения или <code>null</code>
   */
  @SuppressWarnings( "unused" )
  default Image getIcon( SkEvent aEvent, EIconSize aIconSize, ITsGuiContext aContext ) {
    return null;
  }
}
