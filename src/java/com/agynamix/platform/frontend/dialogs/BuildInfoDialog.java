/*
 * Class LicenseDialog
 * created on 01.09.2004
 *
 */
package com.agynamix.platform.frontend.dialogs;

import com.agynamix.simidude.Simidude;



/**
 * @version $Revision$ $Date$
 * @author tuhlmann
 */
public class BuildInfoDialog {

  final IBuildInfoDialogView view;
  
  public BuildInfoDialog(IBuildInfoDialogView view) {
    this.view = view;
    this.view.setPresenter(this);
  }

  /**
   * 
   */
  public void open() {
    view.open();  
  }

  /**
   * hook into initialization of the view.
   *
   */
  public void onInit() {
  }
  
  /**
   * Hook that is called from view at the end of the createDialogArea function.
   *
   */
  public void onCreateDialogArea() {
    setBuildInfo();
  }
  
  /**
   * Passes the license information into the proper fields of the view
   * @param l a License object
   */
  private void setBuildInfo() {
    StringBuffer sb = new StringBuffer();
    Package pkg = Simidude.class.getPackage();
    
    sb.append("Specification Title:    \t\t\t").append(pkg.getSpecificationTitle()).append('\n');
    sb.append("Specification Vendor:   \t\t").append(pkg.getSpecificationVendor()).append('\n');
    sb.append("Specification Version:  \t\t").append(pkg.getSpecificationVersion()).append('\n');
    sb.append("Implementation Title:   \t\t").append(pkg.getImplementationTitle()).append('\n');
    sb.append("Implementation Vendor:  \t\t").append(pkg.getImplementationVendor()).append('\n');
    sb.append("Implementation Version: \t\t").append(pkg.getImplementationVersion()).append('\n');

    view.setInfo(sb.toString());
  }

}


// $Log$