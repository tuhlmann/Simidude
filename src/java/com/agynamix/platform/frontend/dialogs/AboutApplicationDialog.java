/*
 * Class LicenseDialogView
 * created on 31.08.2004
 *
 */
package com.agynamix.platform.frontend.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.agynamix.platform.icons.PlatformIcons;
import com.agynamix.platform.infra.ApplicationInfo;

/**
 * This dialog is used to show some build information to the user
 * 
 * @author tuhlmann
 */
public class AboutApplicationDialog extends TitleAreaDialog {

  public AboutApplicationDialog(Shell shell)
  {
    super(shell);
  }

  /**
   * @see org.eclipse.jface.dialogs.TitleAreaDialog#createContents(org.eclipse.swt.widgets.Composite)
   */
  protected Control createContents(Composite parent)
  {
    Control content = super.createContents(parent);
    String applicationName = ApplicationInfo.getApplicationName();
    String applicationVersion = ApplicationInfo.getApplicationVersion();
    getShell().setText("About Simidude, Version " + applicationVersion);
    setTitle("About AGYNAMIX Simidude");
    setTitleImage(PlatformIcons.get(PlatformIcons.SIMIDUDE_LOGO));
    setMessage("Simidude - Painless cross platform Drag & Drop.");
    return content;
  }

  /**
   * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
   */
  protected Control createDialogArea(final Composite arg0)
  {
    final Composite parentComp = (Composite) super.createDialogArea(arg0);
    final Composite composite = new Composite(parentComp, SWT.NONE);

    String applicationName = ApplicationInfo.getApplicationName();
    String company = ApplicationInfo.getCompanyName();
    String companyEmail = ApplicationInfo.getCompanyEmail();
    String companyWww = ApplicationInfo.getCompanyWww();
    final String companyOrderUrl = ApplicationInfo.getCompanyOrderUrl();
    String applicationYear = ApplicationInfo.getApplicationYears();
    String applicationVersion = ApplicationInfo.getApplicationVersion();

    composite.setSize(parentComp.getSize());

    GridLayout layout = new GridLayout();
    layout.numColumns = 1;
    composite.setLayout(layout);
    composite.setLayoutData(new GridData(GridData.FILL_BOTH));

    final Label l1 = new Label(composite, SWT.WRAP);
    l1.setText(" "+applicationName + " is developed by " + company + ".\n For requests or problems please email us at");
    
    Composite linkC = new Composite(composite, SWT.NONE);
    GridLayout linkLay = new GridLayout();
    linkLay.numColumns = 3;
    linkC.setLayout(linkLay);
    
    final Label email = new Label(linkC, SWT.LEAD);
    email.setText(companyEmail);
    final Color hoverForeground = new Color(linkC.getDisplay(), 255, 0, 0);
    final Cursor hoverCursor = new Cursor(linkC.getDisplay(), SWT.CURSOR_HAND);
    email.setForeground(hoverForeground);
    email.setCursor(hoverCursor);
    email.addMouseListener(new MouseAdapter() {
      public void mouseDown(MouseEvent e)
      {
        Program.launch("mailto:" + email.getText() + "?subject=Simidude");
      }      
    });
    linkC.addDisposeListener(new DisposeListener() {
      public void widgetDisposed(DisposeEvent e)
      {
        hoverCursor.dispose();
        hoverForeground.dispose();
      }
    });

    new Label(linkC, SWT.NONE).setText("or visit us at");
    final Label www = new Label(linkC, SWT.LEAD);
    www.setText(companyWww);
    www.setForeground(hoverForeground);
    www.setCursor(hoverCursor);
    www.addMouseListener(new MouseAdapter() {
      public void mouseDown(MouseEvent e)
      {
        Program.launch(www.getText());
      }

    });
  
    seperator(composite);
    
    Composite buildInfo = new Composite(composite, SWT.BORDER);
    layout = new GridLayout();
    layout.numColumns = 2;
    buildInfo.setLayout(layout);
    GridData d = new GridData();
    d.horizontalAlignment = SWT.FILL;
    buildInfo.setLayoutData(d);
    
//    String biStr = "Version: "+ApplicationInfo.getApplicationVersion()+", Build-No: " + ApplicationInfo.getBuildNumber()+
//                   ", Rev-Id: "+ApplicationInfo.getRepoRevision()+", Build-Time: "+ApplicationInfo.getBuildTime();
    String biStr = "Version: "+ApplicationInfo.getApplicationVersion()+", Build-No: " + ApplicationInfo.getBuildNumber()+
        ", Build-Time: "+ApplicationInfo.getBuildTime();
    Label biLbl = new Label(buildInfo, SWT.NONE);
    biLbl.setText(biStr);
    d = new GridData();
    d.grabExcessHorizontalSpace = true;
    biLbl.setLayoutData(d);
    Button btnBuild = new Button(buildInfo, SWT.PUSH);
    d = new GridData();
    d.horizontalAlignment = SWT.RIGHT;
    btnBuild.setLayoutData(d);
    btnBuild.setText("Copy");
    btnBuild.addMouseListener(new MouseAdapter(){
      @Override
      public void mouseDown(MouseEvent e)
      {
        copyBuildInfoToClipboard();
      }
    });
    
    composite.pack();

    return composite;
  }
  
  protected void seperator(Composite parent)
  {
    final Label sep = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
    GridData spec = new GridData();
    spec.widthHint = 420;
    spec.grabExcessHorizontalSpace = true;
    spec.horizontalAlignment = GridData.HORIZONTAL_ALIGN_CENTER;
    sep.setLayoutData(spec);    
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
   */
  protected void createButtonsForButtonBar(Composite parent)
  {
    createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
  }
  
  private void copyBuildInfoToClipboard()
  {
    String appInfo = ApplicationInfo.getApplicationInfo();
    Clipboard clipboard = new Clipboard(getShell().getDisplay());
    clipboard.setContents(new Object[]{appInfo}, new Transfer[]{TextTransfer.getInstance()});
    clipboard.dispose();
  }

}

