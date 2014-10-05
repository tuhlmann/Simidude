/*
 * Class ExitAction
 * created on 01.06.2004
 *
 */
package com.agynamix.platform.frontend.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.ApplicationWindow;

import com.agynamix.platform.frontend.gui.ApplicationGUI;
import com.agynamix.platform.icons.PlatformIcons;
import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.simidude.Simidude;
import com.install4j.api.launcher.ApplicationLauncher;


/**
 * @version $Revision: 10 $ $Date: 2004-11-17 13:30:10 +0100 (Mi, 17 Nov 2004) $
 * @author tuhlmann
 * @since V0404
 */
public class CheckUpdatesAction extends Action {
  
  final ApplicationWindow window;
  
  public CheckUpdatesAction(ApplicationWindow w) {
    this.window = w;
    setText("&Check for Updates");
    setToolTipText("Check if new updates are available");
    setImageDescriptor(PlatformIcons.getDescriptor(PlatformIcons.UPDATE));
  }
    
  /**
   * @see org.eclipse.jface.action.Action#run()
   */
  public void run() {
    try
    {
      ApplicationLauncher.launchApplication(Simidude.MANUAL_UPDATER_ID, null, false, new ApplicationLauncher.Callback(){

        public void exited(int exitValue){}

        public void prepareShutdown()
        {
          try {
            ApplicationGUI gui = ApplicationBase.getContext().getApplicationGUI();
            gui.close();
          } catch (Exception ignore){}
        }
        
      });
    } catch (Exception ignore){}
  }

}

