/*
 * Class LicenseDialogView
 * created on 31.08.2004
 *
 */
package com.agynamix.platform.frontend.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
 * This dialog is used to show some build information to the user
 * @version $Revision$ $Date$
 * @author tuhlmann
 */
public class BuildInfoDialogView extends TitleAreaDialog implements IBuildInfoDialogView {

  BuildInfoDialog presenter;
  Text info;
  Button copyToClipboard;
  
  public BuildInfoDialogView(Shell arg0) {
    super(arg0);
  }
  
  
  /**
   * @see com.agynamix.simidude.dialogs.ILicenseDialogView#setPresenter(com.agynamix.simidude.dialogs.LicenseDialog)
   */
  public void setPresenter(BuildInfoDialog dialog) {
    presenter = dialog;
  }
  
  /**
   * @see org.eclipse.jface.dialogs.TitleAreaDialog#createContents(org.eclipse.swt.widgets.Composite)
   */
  protected Control createContents(Composite parent) {
    Control content = super.createContents(parent);
    getShell().setText("Build Information Dialog");
    setTitle("Build Information Dialog");
    setMessage("This dialog shows some build information. "+
               "If you have problems\n running this application please send the "+
               "information shown here\n together with your problem report to "+
               "contact@agynamix.com");

    presenter.onInit();
    
    return content;
  }
    
  /**
   * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
   */
  protected Control createDialogArea(Composite arg0) {
    final Composite parentComp = (Composite) super.createDialogArea(arg0);
    final Composite composite = new Composite(parentComp, SWT.NONE);
    composite.setSize(parentComp.getSize());

    GridLayout layout = new GridLayout();
    layout.numColumns = 1;
    composite.setLayout(layout);
    composite.setLayoutData(new GridData(GridData.FILL_BOTH));

    info = new Text(composite, SWT.READ_ONLY | SWT.BORDER | SWT.MULTI | SWT.V_SCROLL );
    GridData catSpec = new GridData();
    catSpec = new GridData();
    catSpec.heightHint = 100;
    catSpec.horizontalAlignment = SWT.FILL;
    catSpec.verticalAlignment = GridData.FILL;
    catSpec.grabExcessVerticalSpace = true;
    catSpec.grabExcessHorizontalSpace = true;
    info.setLayoutData(catSpec);
    
    info.setFocus();

    composite.pack();
//    composite.layout();
    
    presenter.onCreateDialogArea();
    
    return composite;
  }
 
  /* (non-Javadoc)
   * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
   */
  protected void createButtonsForButtonBar(Composite parent) {
    createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
  }
  
  /**
   * @see com.agynamix.simidude.dialogs.ILicenseDialogView#setProduct(java.lang.String)
   */
  public void setInfo(String buildinfo) {
    info.setText(buildinfo);
  }
    
  
}


// $Log$