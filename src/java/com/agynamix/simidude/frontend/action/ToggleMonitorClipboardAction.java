/*
 * Copyright by AGYNAMIX(R). All rights reserved. 
 * This file is made available under the terms of the
 * license this product is released under.
 * 
 * For details please see the license file you should have
 * received, or go to:
 * 
 * http://www.agynamix.com
 * 
 * Contributors: agynamix.com (http://www.agynamix.com)
 */
package com.agynamix.simidude.frontend.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.window.ApplicationWindow;

import com.agynamix.platform.frontend.action.IActionWithValue;
import com.agynamix.platform.frontend.preferences.IPreferenceConstants;
import com.agynamix.platform.icons.PlatformIcons;
import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.simidude.clipboard.SourceDataManager;
import com.agynamix.simidude.impl.SimidudeApplicationContext;
import com.install4j.runtime.beans.actions.desktop.AddStartupItemAction;

public class ToggleMonitorClipboardAction extends Action implements IAction, IActionWithValue {
  
  final ApplicationWindow window;
  
  public ToggleMonitorClipboardAction(ApplicationWindow w) {
    super("Monitor Clipboard", IAction.AS_CHECK_BOX);
    this.window = w;
    setToolTipText("Toggles the monitoring of the clipboard of this computer. If off, clipboard items wont be send to computers.");
    setImageDescriptor(PlatformIcons.getDescriptor(PlatformIcons.TOGGLE_MONITOR_CLIPBOARD));
    boolean checkStatus = ApplicationBase.getContext().getConfiguration().getBoolean(IPreferenceConstants.TOGGLE_CLIPBOARD_MONITOR);
    setChecked(checkStatus);
    SourceDataManager tm = ((SimidudeApplicationContext)ApplicationBase.getContext()).getSourceDataManager();
    tm.setClipboardMonitorEnabled(checkStatus);
  }
    
  /**
   * @see org.eclipse.jface.action.Action#run()
   */
  public void run() {
    runWithValue(isChecked());
  }
  
  public void runWithValue(boolean value)
  {
    SourceDataManager tm = ((SimidudeApplicationContext)ApplicationBase.getContext()).getSourceDataManager();
    tm.setClipboardMonitorEnabled(value);
//    if (value)
//    {
//      System.out.println("Clipboard Monitoring ON");
//    } else {
//      System.out.println("Clipboard Monitoring OFF");            
//    }
    ApplicationBase.getContext().getConfiguration().setBoolean(IPreferenceConstants.TOGGLE_CLIPBOARD_MONITOR, value);
    setChecked(value);
  }
  
  public boolean getSelectionValue()
  {
    return isChecked();
  }

}
