package org.toxsoft.skf.alarms.lib.checkers;

/**
 * Localizable resources.
 *
 * @author hazard157
 */
interface ISkResources {

  /**
   * {@link AlertCheckerRtdataVsConstType}
   */
  String STR_RTDVC               = Messages.getString( "STR_RTDVC" );               //$NON-NLS-1$
  String STR_RTDVC_D             = Messages.getString( "STR_RTDVC_D" );             //$NON-NLS-1$
  String STR_RTDVC_RTDATA_GWID   = Messages.getString( "STR_RTDVC_RTDATA_GWID" );   //$NON-NLS-1$
  String STR_RTDVC_RTDATA_GWID_D = Messages.getString( "STR_RTDVC_RTDATA_GWID_D" ); //$NON-NLS-1$
  String STR_RTDVC_COMPARE_OP    = Messages.getString( "STR_RTDVC_COMPARE_OP" );    //$NON-NLS-1$
  String STR_RTDVC_COMPARE_OP_D  = Messages.getString( "STR_RTDVC_COMPARE_OP_D" );  //$NON-NLS-1$
  String STR_RTDVC_CONST_VALUE   = Messages.getString( "STR_RTDVC_CONST_VALUE" );   //$NON-NLS-1$
  String STR_RTDVC_CONST_VALUE_D = Messages.getString( "STR_RTDVC_CONST_VALUE_D" ); //$NON-NLS-1$

  // ------------------------------------------------------------------------------------
  // Log messages, no need to localize

  String FMT_WARN_CANT_OPEN_READ_RTD_CHANNEL = "Can not open read current RTdata channel for GWID '%s'"; //$NON-NLS-1$

}
