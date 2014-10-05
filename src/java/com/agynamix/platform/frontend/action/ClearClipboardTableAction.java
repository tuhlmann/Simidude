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

import com.agynamix.platform.icons.PlatformIcons;
import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.simidude.clipboard.SourceDataManager;
import com.agynamix.simidude.impl.SimidudeApplicationContext;
import com.agynamix.simidude.infra.SimidudeUtils;

public class ClearClipboardTableAction extends Action {
  
  final ApplicationWindow window;
  final boolean isNetworkRemove;
  
  public ClearClipboardTableAction(ApplicationWindow w, boolean isNetworkRemove) {
    super(isNetworkRemove ? "Clear Table Everywhere" : "Clear Table Locally", IAction.AS_PUSH_BUTTON);
    this.window = w;
    this.isNetworkRemove = isNetworkRemove;
    if (isNetworkRemove)
    {
      setToolTipText("Clears all entries from the clipboard table AND all connected clients");
      setImageDescriptor(PlatformIcons.getDescriptor(PlatformIcons.NETWORK_CLEAR_CLIPBOARD));      
    } else {
      setToolTipText("Clears all entries from the clipboard table");
      setImageDescriptor(PlatformIcons.getDescriptor(PlatformIcons.CLEAR_CLIPBOARD));
    }
  }
    
  /**
   * @see org.eclipse.jface.action.Action#run()
   */
  public void run() {
    boolean isNetworkRemove = SimidudeUtils.isModifierKeyPressed() || this.isNetworkRemove;
    SourceDataManager sdm = ((SimidudeApplicationContext)ApplicationBase.getContext()).getSourceDataManager();
    sdm.removeAll();
    if (isNetworkRemove)
    {
      ((SimidudeApplicationContext)ApplicationBase.getContext()).getModelProvider().networkRemoveItem(null);      
    }
  }
  
}
