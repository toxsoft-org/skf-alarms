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

  /**
   * {@link AlertCheckerRtdataVsAttrType}
   */
  String STR_RTD_VC_ATTR       = Messages.getString( "STR_RTD_VC_ATTR" );       //$NON-NLS-1$
  String STR_RTD_VC_ATTR_D     = Messages.getString( "STR_RTD_VC_ATTR_D" );     //$NON-NLS-1$
  String STR_RTDVC_ATTR_GWID   = Messages.getString( "STR_RTDVC_ATTR_GWID" );   //$NON-NLS-1$
  String STR_RTDVC_ATTR_GWID_D = Messages.getString( "STR_RTDVC_ATTR_GWID_D" ); //$NON-NLS-1$

  /**
   * {@link AlertCheckerRtDataGtZero}
   */
  String STR_RTDATA_GT_ZERO   = "RtData value == true";                                      //$NON-NLS-1$
  String STR_RTDATA_GT_ZERO_D = "RtData value > 0 (numeric type) or value == true (boolean)";//$NON-NLS-1$

  // ------------------------------------------------------------------------------------
  // Log messages, no need to localize

  String FMT_WARN_CANT_OPEN_READ_RTD_CHANNEL = "Can not open read current RTdata channel for GWID '%s'"; //$NON-NLS-1$
  String FMT_WARN_CANT_FIND_ATTR             = "Can find attribute for GWID '%s'";                       //$NON-NLS-1$
  String FMT_ERR_INVALID_RRI_ATTR_TYPE       = "Invalid type for that checker '%s'";                     //$NON-NLS-1$

}
