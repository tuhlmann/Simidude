/*
 * Class ExitAction
 * created on 01.06.2004
 *
 */
package com.agynamix.platform.frontend.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.ApplicationWindow;

import com.agynamix.platform.frontend.dialogs.BuildInfoDialog;
import com.agynamix.platform.frontend.dialogs.BuildInfoDialogView;



/**
 * @version $Revision: 10 $ $Date: 2004-11-17 13:30:10 +0100 (Mi, 17 Nov 2004) $
 * @author tuhlmann
 * @since 1.0
 */
public class BuildInfoAction extends Action {
  
  final ApplicationWindow window;
  
  public BuildInfoAction(ApplicationWindow w) {
    this.window = w;
    setText("Build &Info");
    setToolTipText("Shows the build information dialog");
  }
  
  
  /**
   * @see org.eclipse.jface.action.Action#run()
   */
  public void run() {
    BuildInfoDialog dialog = new BuildInfoDialog(new BuildInfoDialogView(window.getShell()));
    dialog.open();  
  }

}


// $Log$