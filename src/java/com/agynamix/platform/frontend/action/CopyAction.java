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

import com.agynamix.platform.icons.PlatformIcons;
import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.simidude.clipboard.SourceDataManager;
import com.agynamix.simidude.impl.SimidudeApplicationContext;

public class CopyAction extends Action {
  
  final ApplicationWindow window;
  
  public CopyAction(ApplicationWindow w) {
    super("Copy Selected Entry", IAction.AS_PUSH_BUTTON);
    this.window = w;
    setToolTipText("Copy the selected entry to the clipboard");
    setAccelerator(SWT.MOD1 + 'C');     
    setImageDescriptor(PlatformIcons.getDescriptor(PlatformIcons.COPY));
  }
    
  /**
   * @see org.eclipse.jface.action.Action#run()
   */
  public void run() {
    SourceDataManager tm = ((SimidudeApplicationContext)ApplicationBase.getContext()).getSourceDataManager();
    tm.activateItem();
  }
  

}
