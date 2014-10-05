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
package com.agynamix.platform.frontend.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.agynamix.platform.frontend.preferences.IPreferenceConstants;
import com.agynamix.platform.infra.ApplicationBase;

public class ToggleShowStatuslineAction extends Action implements IAction {
  
  final ApplicationWindow window;
        Control statusline;
        
        Rectangle position;
  
  public ToggleShowStatuslineAction(ApplicationWindow w) {
    super("Show Statusline", IAction.AS_CHECK_BOX);
    this.window = w;
    setToolTipText("Click to toggle the visibility of the status line at the bottom of the window.");
    //setImageDescriptor(PlatformIcons.getDescriptor(PlatformIcons.TOGGLE_MONITOR_CLIPBOARD));
    boolean checkStatus = ApplicationBase.getContext().getConfiguration().getBoolean(IPreferenceConstants.TOGGLE_SHOW_STATUSLINE);
    setChecked(checkStatus);
  }

  public void setStatusline(Control statusline)
  {
    this.statusline = statusline;
    position = statusline.getBounds();
  }
  
  /**
   * @see org.eclipse.jface.action.Action#run()
   */
  public void run() {
    showStatusline(isChecked());
    ApplicationBase.getContext().getConfiguration().setBoolean(IPreferenceConstants.TOGGLE_SHOW_STATUSLINE, isChecked());
  }
  
  public void showStatusline()
  {
    boolean isShowStatusline = ApplicationBase.getContext().getConfiguration().getBoolean(IPreferenceConstants.TOGGLE_SHOW_STATUSLINE);
    showStatusline(isShowStatusline);
  }

  public void showStatusline(boolean show)
  {
    if (show)
    {
      statusline.setBounds(position.x, position.y, position.width, position.height);
//      ((GridData)statusline.getLayoutData()).heightHint = -1;
      //statusline.setVisible(true);
    } else {
      statusline.setBounds(position.x, position.y, position.width, 0);
//      ((GridData)statusline.getLayoutData()).heightHint = 0;     
      statusline.setVisible(false);
    }
    statusline.getParent().layout(true);
    window.getShell().layout(true);
  }

  
}
