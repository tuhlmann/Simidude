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
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Control;

import com.agynamix.platform.frontend.preferences.IPreferenceConstants;
import com.agynamix.platform.icons.PlatformIcons;
import com.agynamix.platform.infra.ApplicationBase;

public class ToggleShowToolbarAction extends Action implements IAction {
  
  final ApplicationWindow window;
        Control toolbar;
  
  public ToggleShowToolbarAction(ApplicationWindow w) {
    super("Show Toolbar", IAction.AS_CHECK_BOX);
    this.window = w;
    setToolTipText("Click to toggle the visibility of the toolbar.");
    setImageDescriptor(PlatformIcons.getDescriptor(PlatformIcons.TOGGLE_SHOW_TOOLBAR));
    setAccelerator(SWT.MOD1 + 'T');
    boolean checkStatus = ApplicationBase.getContext().getConfiguration().getBoolean(IPreferenceConstants.TOGGLE_SHOW_TOOLBAR);
    setChecked(checkStatus);
//    if (!checkStatus)
//    {
//      showToolbar(false);
//    }
  }

  public void setToolbar(Control toolbar)
  {
    this.toolbar = toolbar;
  }
  
  /**
   * @see org.eclipse.jface.action.Action#run()
   */
  public void run() {
    showToolbar(isChecked());
    ApplicationBase.getContext().getConfiguration().setBoolean(IPreferenceConstants.TOGGLE_SHOW_TOOLBAR, isChecked());
  }
  
  public void showToolbar()
  {
    boolean isShowToolbar = ApplicationBase.getContext().getConfiguration().getBoolean(IPreferenceConstants.TOGGLE_SHOW_TOOLBAR);
    showToolbar(isShowToolbar);
  }

  public void showToolbar(boolean show)
  {
    if (show)
    {
      ((GridData)toolbar.getLayoutData()).heightHint = -1;
    } else {
      ((GridData)toolbar.getLayoutData()).heightHint = 0;      
    }
    toolbar.getParent().pack(true);
    window.getShell().layout(true);
  }

  
}
