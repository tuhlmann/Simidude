/*
 * Class AboutAction
 * created on 01.06.2004
 *
 */
package com.agynamix.simidude.frontend.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.ApplicationWindow;

import com.agynamix.platform.frontend.dialogs.AboutApplicationDialog;




/**
 * @version $Revision: 41 $ $Date: 2005-01-09 18:03:54 +0100 (So, 09 Jan 2005) $
 * @author tuhlmann
 * @since V0404
 */
public class AboutAction extends Action {
  
  final ApplicationWindow window;
  
  public AboutAction(ApplicationWindow w) {
    this.window = w;
    setText("&About Simidude");
    setToolTipText("Show information &about the application");
  }
  
  
  /**
   * @see org.eclipse.jface.action.Action#run()
   */
  public void run() {
    AboutApplicationDialog about = new AboutApplicationDialog(window.getShell());
    about.open();
  }

}

