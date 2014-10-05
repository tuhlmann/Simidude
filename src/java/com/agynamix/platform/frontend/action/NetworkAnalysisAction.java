/*
 * Class NetworkAnalysisAction
 * created on 07.01.2010
 *
 */
package com.agynamix.platform.frontend.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.ApplicationWindow;

import com.agynamix.platform.frontend.dialogs.SimpleInformationViewerDialog;
import com.agynamix.platform.net.NetworkAnalyzer;



/**
 * @author tuhlmann
 * @since 1.0
 */
public class NetworkAnalysisAction extends Action {
  
  final ApplicationWindow window;
  
  public NetworkAnalysisAction(ApplicationWindow w) {
    this.window = w;
    setText("&Network Analysis");
    setToolTipText("Shows diagnosis information about how Simidude uses the network.");
  }
  
  
  /**
   * @see org.eclipse.jface.action.Action#run()
   */
  public void run() {
    NetworkAnalyzer networkAnalyzer = new NetworkAnalyzer();
    networkAnalyzer.run();
    String info = networkAnalyzer.asString();
    SimpleInformationViewerDialog dialog = new SimpleInformationViewerDialog(window.getShell(), 
        "Network Analysis", "This dialog shows diagnosis information about your network and how Simidude utilizes it.\nPlease include this in any network related bug reports.", info);
    networkAnalyzer.close();
    dialog.open();  
  }

}

