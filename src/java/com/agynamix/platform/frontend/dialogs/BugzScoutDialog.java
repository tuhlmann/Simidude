package com.agynamix.platform.frontend.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.agynamix.platform.bugzscout.BugzScoutCtl;
import com.agynamix.platform.bugzscout.BugzScoutCtl.ScoutAnswer;
import com.agynamix.platform.icons.PlatformIcons;
import com.agynamix.platform.net.NetworkAnalyzer;

/**
 * This dialog is shown whenever a hard error was found that will crash the client.
 * The data gathered here is send to our bug tracking system
 * @author tuhlmann
 *
 */
public class BugzScoutDialog extends TitleAreaDialog {

  private Image   image;
  private Text    bugTitle;
  private Text    bugDescription;
  private Text    userEmail;
  
  private String  extraInformation;
  private String  systemInformation;
  
  private String  bugTitleStr;
  
  private boolean isUserOpened = false;
  
  public BugzScoutDialog(Shell parentShell)
  {
    this(parentShell, "", true);
  }
  
  public BugzScoutDialog(Shell parentShell, String bugTitle, String extraInformation, String systemInformation)
  {
    this(parentShell, systemInformation, false);
    this.bugTitleStr = bugTitle;
    this.extraInformation = extraInformation;
  }
  
  public BugzScoutDialog(Shell parentShell, String systemInformation, boolean isUserOpened)
  {
    super(parentShell);
    setShellStyle(getShellStyle() | SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);

    image = PlatformIcons.get(PlatformIcons.BUGZSCOUT_DIALOG);
    
    this.isUserOpened = isUserOpened;
    this.extraInformation = ""; //$NON-NLS-1$
    this.systemInformation = systemInformation;
    
  }
  
  @Override
  public boolean close()
  {
    return super.close();
  }
  
  @Override
  protected void createButtonsForButtonBar(Composite parent)
  {
    createButton(parent, IDialogConstants.YES_ID, "Send!", true); //$NON-NLS-1$
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CLOSE_LABEL, false);
    createButton(parent, IDialogConstants.DETAILS_ID, "View Report", false);
//    super.createButtonsForButtonBar(parent);
  }
  
  @Override
  protected void buttonPressed(int buttonId)
  {
    if (buttonId == IDialogConstants.YES_ID)
    {
      BugzScoutCtl ctl = new BugzScoutCtl();
      ctl.setArea("BugzScout"); //$NON-NLS-1$
      ctl.setDefaultMessage("Thank you for submitting this report!");
      ctl.setProject("Simidude"); //$NON-NLS-1$
      ctl.setUrl("https://agynamix.fogbugz.com/scoutSubmit.asp"); //$NON-NLS-1$
      ctl.setUserName("Torsten Uhlmann"); //$NON-NLS-1$
      String title = composeTitle(bugTitle.getText(), bugTitleStr, isUserOpened);
      String bugDesc = composeBugDescription(bugDescription.getText(), extraInformation, systemInformation);
      ScoutAnswer answer = ctl.submitBug(title, bugDesc, userEmail.getText(), false);
      BugzScoutAnswerDialog dialog = new BugzScoutAnswerDialog(getShell(), answer);
      dialog.open();
      if (answer.getReturnCode() == BugzScoutCtl.ScoutAnswer.ReturnCode.SUCCESS)
      {
        setReturnCode(buttonId);
        this.close();
      }      
    } else if (buttonId == IDialogConstants.DETAILS_ID)
    {
      String title = composeTitle(bugTitle.getText(), bugTitleStr, isUserOpened);
      String bugDesc = composeBugDescription(bugDescription.getText(), extraInformation, systemInformation);
      BugReportDetailsDialog detailsDialog = new BugReportDetailsDialog(this.getParentShell(), title, bugDesc, userEmail.getText());
      detailsDialog.open();
    } else {
      super.buttonPressed(buttonId);
    }
  }
  
  String composeTitle(String userTitle, String autoTitle, boolean isUserOpened)
  {
    return (isUserOpened) ? userTitle : autoTitle;
  }
  
  String composeBugDescription(String bugDescription, String extraInformation, String systemInfo)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("\n").append(bugDescription); //$NON-NLS-1$ 
    if (extraInformation.length() > 0)
    {
      sb.append("\n\n#####################\nAdded by the software:\n\n").append(extraInformation); //$NON-NLS-1$
    }

    sb.append("\n\n#####################\nNetwork Information:\n\n");      
    NetworkAnalyzer networkAnalyzer = new NetworkAnalyzer();
    networkAnalyzer.run();
    String info = networkAnalyzer.asString();
    networkAnalyzer.close();
    sb.append(info);
    
    if (systemInfo.length() > 0)
    {      
      sb.append("\n\n#####################\nSystem Information:\n\n").append(systemInfo);      
    }
    return sb.toString();    
  }
  
  @Override
  protected Control createContents(Composite parent)
  {
    Control contents = super.createContents(parent);
    setTitle("Bug Report");
    setMessage("Submit a Bug Report", IMessageProvider.INFORMATION);
    
    if (image != null)
    {
      setTitleImage(image);
    }
    
    return contents;
  }
  
  @Override
  protected Control createDialogArea(Composite parent)
  {
    Composite parentComposite = (Composite) super.createDialogArea(parent);
    parentComposite.setLayout(new GridLayout());
        
    Composite composite = new Composite(parentComposite, parentComposite.getStyle());
    GridData compData = new GridData(SWT.FILL, SWT.FILL, true, true);
    composite.setLayoutData(compData);

    composite.setLayout(new GridLayout(2, false));
    
    Label l = new Label(composite, SWT.WRAP);
    l.setText("Thank you very much for helping us to improve our software!");
    
    GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
    data.horizontalSpan = 2;
    l.setLayoutData(data);

    Label tell = new Label(composite, SWT.WRAP);
    tell.setText("\nPlease tell us about this problem.\n\nWhile telling us about the action you where about to execute when the error occured you help us \nto correct this bug and make our product more stable in future versions. \nThis bug report will be sent to our internal bug database.\nYour submitted data will be disclosed to third parties!\nThank you very much!\n\n");
    
    data = new GridData(SWT.FILL, SWT.FILL, true, false);
    data.horizontalSpan = 2;
    tell.setLayoutData(data);

    if (isUserOpened)
    {
      Label titleLabel = new Label(composite, SWT.NONE);
      titleLabel.setText("A Headline for the problem:");
      
      data = new GridData(SWT.FILL, SWT.FILL, false, false);
      data.horizontalSpan = 2;
      titleLabel.setLayoutData(data);
    
      bugTitle = new Text(composite, SWT.BORDER );
      data = new GridData(SWT.FILL, SWT.FILL, true, false);
      data.horizontalSpan = 2;
      data.minimumHeight = 12;
      bugTitle.setLayoutData(data);
      bugTitle.setTextLimit(100);
    }
        
    Label whatHappened = new Label(composite, SWT.NONE);
    whatHappened.setText("What did you do when the problem occured?");
    
    data = new GridData(SWT.FILL, SWT.FILL, false, false);
    data.horizontalSpan = 2;
    whatHappened.setLayoutData(data);
    
    bugDescription = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.WRAP);
    data = new GridData(SWT.FILL, SWT.FILL, false, true);
    data.horizontalSpan = 2;
    data.minimumHeight = 150;
    bugDescription.setLayoutData(data);
    bugDescription.setTextLimit(2000);
    
    Label lUserEmail = new Label(composite, SWT.NONE);
    lUserEmail.setText("Email address (optional)"); //$NON-NLS-1$
    data = new GridData(SWT.LEAD, SWT.FILL, false, false);
    data.horizontalSpan = 1;
    lUserEmail.setLayoutData(data);

    userEmail = new Text(composite, SWT.BORDER );
    data = new GridData(SWT.FILL, SWT.FILL, true, false);
    data.horizontalSpan = 1;
    data.minimumHeight = 12;
    data.minimumWidth = 100;
    userEmail.setLayoutData(data);
    userEmail.setTextLimit(100);
    
    return parentComposite;
  }
  
}
