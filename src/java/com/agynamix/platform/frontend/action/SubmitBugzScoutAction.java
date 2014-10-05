/*
 * Class LicenseDialogAction
 * created on 01.06.2004
 *
 */
package com.agynamix.platform.frontend.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.ApplicationWindow;

import com.agynamix.platform.frontend.dialogs.BugzScoutDialog;
import com.agynamix.platform.icons.PlatformIcons;
import com.agynamix.platform.infra.ApplicationInfo;


/**
 * @author tuhlmann
 */
public class SubmitBugzScoutAction extends Action {
  
  final ApplicationWindow window;
  
  public SubmitBugzScoutAction(ApplicationWindow w) {
    this.window = w;
    setText("Submit A Bug");
    setToolTipText("Use this dialog to submit a bug to the Simidude developers.");
    setImageDescriptor(PlatformIcons.getDescriptor(PlatformIcons.SUBMIT_BUG_REPORT));
  }
  
  
  /**
   * @see org.eclipse.jface.action.Action#run()
   */
  public void run() {
    BugzScoutDialog dialog = new BugzScoutDialog(window.getShell(), ApplicationInfo.getApplicationInfo(), true );
    dialog.open();
  }

}

