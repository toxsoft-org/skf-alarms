package org.toxsoft.skf.alarms.gui;

import org.toxsoft.skf.alarms.gui.km5.*;

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
}
