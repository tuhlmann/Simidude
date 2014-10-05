/*
 * Class ExitAction
 * created on 01.06.2004
 *
 */
package com.agynamix.platform.frontend.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.ApplicationWindow;

import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.simidude.impl.SimidudeApplicationContext;
import com.agynamix.simidude.infra.ModelProvider;



/**
 * @version $Revision: 10 $ $Date: 2004-11-17 13:30:10 +0100 (Mi, 17 Nov 2004) $
 * @author tuhlmann
 */
public class PreferencesAction extends Action {
  
  final ApplicationWindow window;
  
  public PreferencesAction(ApplicationWindow w) {
    this.window = w;
    setText("&Preferences");
    setToolTipText("Use this to set Simidude's preferences according to your needs.");
  }
  
  
  /**
   * @see org.eclipse.jface.action.Action#run()
   */
  public void run() {
    ModelProvider mp = ((SimidudeApplicationContext)ApplicationBase.getContext()).getModelProvider();
    mp.openPreferencesDialog(window.getShell());
  }

}

