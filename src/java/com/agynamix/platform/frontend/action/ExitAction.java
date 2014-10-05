/*
 * Class ExitAction
 * created on 01.06.2004
 *
 */
package com.agynamix.platform.frontend.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;

import com.agynamix.platform.icons.PlatformIcons;


/**
 * @version $Revision: 10 $ $Date: 2004-11-17 13:30:10 +0100 (Mi, 17 Nov 2004) $
 * @author tuhlmann
 * @since V0404
 */
public class ExitAction extends Action {
  
  final ApplicationWindow window;
  
  public ExitAction(ApplicationWindow w) {
    this.window = w;
    setText("E&xit");
    setToolTipText("Exit the application");
    setAccelerator(SWT.MOD1 + 'Q'); 
    setImageDescriptor(PlatformIcons.getDescriptor(PlatformIcons.EXIT));
    setDescription("Exit the application");
  }
    
  /**
   * @see org.eclipse.jface.action.Action#run()
   */
  public void run() {
    window.close();
  }

}

