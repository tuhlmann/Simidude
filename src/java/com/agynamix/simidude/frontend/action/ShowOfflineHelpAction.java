/*
 * Class ExitAction
 * created on 01.06.2004
 *
 */
package com.agynamix.simidude.frontend.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;

import com.agynamix.platform.icons.PlatformIcons;
import com.agynamix.platform.infra.PlatformUtils;
import com.agynamix.simidude.infra.SimidudeUtils;



/**
 * @version $Revision$ $Date: 2004-11-17 13:30:10 +0100 (Mi, 17 Nov 2004) $
 * @author tuhlmann
 * @since V0404
 */
public class ShowOfflineHelpAction extends Action {
  
  final ApplicationWindow window;
  
  public ShowOfflineHelpAction(ApplicationWindow w) {
    this.window = w;
    setText("View &Help");
    setAccelerator(SWT.F1);    
    setToolTipText("Display Simidude Help Information");    
    setImageDescriptor(PlatformIcons.getDescriptor(PlatformIcons.HELP));  
  }
  
  
  /**
   * @see org.eclipse.jface.action.Action#run()
   */
  public void run() {
    SimidudeUtils.launchStandardApplication(PlatformUtils.getApplicationBasedir()+"/Simidude User Manual.pdf");
  }

}

