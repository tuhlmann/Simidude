package com.agynamix.platform.frontend.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.agynamix.platform.icons.PlatformIcons;

/**
 * This dialog shows the user the data that is submitted to our bug tracking system.
 * @author tuhlmann
 *
 */
public class BugReportDetailsDialog extends TitleAreaDialog {

  private Image image;
  private Color backgroundColor;
  
  private String title;
  private String description;
  private String userEmail;
  
  public BugReportDetailsDialog(Shell parentShell, String title, String description, String userEmail)
  {
    super(parentShell);
    setShellStyle(getShellStyle() | SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);

    image = PlatformIcons.get(PlatformIcons.BUGZSCOUT_DIALOG);
    backgroundColor = new Color(parentShell.getDisplay(), 255, 255, 255);    
    this.title = title;
    this.description = description;
    this.userEmail = userEmail;
  }
  
  @Override
  public boolean close()
  {
    if (backgroundColor != null)
    {
      backgroundColor.dispose();
    }
    return super.close();
  }
  
  @Override
  protected void createButtonsForButtonBar(Composite parent)
  {
    createButton(parent, IDialogConstants.OK_ID, IDialogConstants.CLOSE_LABEL, true);
  }
  
  @Override
  protected Control createContents(Composite parent)
  {
    Control contents = super.createContents(parent);
    setTitle("Your Bug Report");
    int style = IMessageProvider.INFORMATION;
    setMessage("This dialog shows you the bug report information that is submitted to our database.", style); //$NON-NLS-1$
    
    if (image != null)
    {
      setTitleImage(image);
    }
    
    return contents;
  }
  
  @Override
  protected Control createDialogArea(Composite parent)
  {
    Composite composite = (Composite) super.createDialogArea(parent);

    composite.setLayout(new GridLayout(1, false));
    
    Label l = new Label(composite, SWT.WRAP);
    l.setText("Bug Report Details:"); //$NON-NLS-1$
    
    GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
    l.setLayoutData(data);

    Text msg = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.READ_ONLY);    
    msg.setBackground(backgroundColor);
    msg.setText(composeBugReport(title, description, userEmail));
    data = new GridData(SWT.FILL, SWT.FILL, true, true);
    data.minimumHeight = 200;
    msg.setLayoutData(data);
    
    return composite;
  }

  private String composeBugReport(String title, String description, String userEmail)
  {
    StringBuilder sb = new StringBuilder();
    
    sb.append("== Title: ==\n"+title+"\n\n");
    if ((userEmail != null) && (userEmail.length() > 0))
    {
      sb.append("== Submitted by: ==\n"+userEmail+"\n\n");
    }
    sb.append("== Description: ==\n");
    sb.append(description);
    
    
    return sb.toString();
  }

}
