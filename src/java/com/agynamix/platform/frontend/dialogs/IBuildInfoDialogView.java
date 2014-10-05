/*
 * Class ILicenseDialogView
 * created on 01.09.2004
 *
 */
package com.agynamix.platform.frontend.dialogs;



/**
 * @version $Revision$ $Date$
 * @author tuhlmann
 */
public interface IBuildInfoDialogView {

  /**
   * Opens the dialog
   */
  int open();

  /**
   * Pass the presenter class to the view
   * @param dialog the smart object that implements the dialog's logic
   */
  void setPresenter(BuildInfoDialog dialog);
  
  /**
   * Sends a formatted build info string to the dialog to display.
   * @param buildinfo the build info to display.
   */
  void setInfo(String buildinfo);

}


// $Log$