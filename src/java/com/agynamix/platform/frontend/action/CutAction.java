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

public class CutAction extends Action {
  
  final ApplicationWindow window;
  
  public CutAction(ApplicationWindow w) {
    super("Cut Selected Entry", IAction.AS_PUSH_BUTTON);
    this.window = w;
    setToolTipText("Copy the selected entry to clipboard and remove it from the list");
    setAccelerator(SWT.MOD1 + 'X');     
    setImageDescriptor(PlatformIcons.getDescriptor(PlatformIcons.CUT));
  }
    
  /**
   * @see org.eclipse.jface.action.Action#run()
   */
  public void run() {
    SourceDataManager sdm = ((SimidudeApplicationContext)ApplicationBase.getContext()).getSourceDataManager();
    int currentSelectionIndex = sdm.getSelectionIndex();
    sdm.activateItem();
    sdm.removeSelectedEntry();
    sdm.saveSelectEntry(currentSelectionIndex);
  }
  

}
