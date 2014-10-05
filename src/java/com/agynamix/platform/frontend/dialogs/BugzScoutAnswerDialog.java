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

import com.agynamix.platform.bugzscout.BugzScoutCtl;
import com.agynamix.platform.icons.PlatformIcons;

/**
 * This dialog is shown whenever a hard error was found that will crash the client.
 * The data gathered here is send to our bug tracking system
 * @author tuhlmann
 *
 */
public class BugzScoutAnswerDialog extends TitleAreaDialog {

  private Image image;
  private Color backgroundColor;
  
  private BugzScoutCtl.ScoutAnswer answer;
  
  public BugzScoutAnswerDialog(Shell parentShell, BugzScoutCtl.ScoutAnswer answer)
  {
    super(parentShell);
    setShellStyle(getShellStyle() | SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);

    image = PlatformIcons.get(PlatformIcons.BUGZSCOUT_DIALOG);
    backgroundColor = new Color(parentShell.getDisplay(), 255, 255, 255);    
    this.answer = answer;
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
    setTitle("Bug Database Answer");
    int style = IMessageProvider.INFORMATION;
    if (BugzScoutCtl.ScoutAnswer.ReturnCode.SUCCESS != answer.getReturnCode())
    {
      style = IMessageProvider.ERROR;
    }
    setMessage("This is the answer provided by our bug tracking system.", style); //$NON-NLS-1$
    
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
    l.setText("Status of the submission: " + answer.getReturnCode()); //$NON-NLS-1$
    
    GridData data = new GridData(SWT.FILL, SWT.FILL, false, false);
    l.setLayoutData(data);

    Label msgLabel = new Label(composite, SWT.WRAP);
    msgLabel.setText("\nThe message provided by our bug tracking system: ");
    
    data = new GridData(SWT.FILL, SWT.FILL, true, false);
    msgLabel.setLayoutData(data);
    
    Text msg = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.READ_ONLY);    
    msg.setBackground(backgroundColor);
    msg.setText(answer.getMessage());
    data = new GridData(SWT.FILL, SWT.FILL, true, true);
    data.minimumHeight = 80;
    msg.setLayoutData(data);
    
    return composite;
  }

}
