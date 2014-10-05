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
import com.agynamix.simidude.clipboard.IClipboardItem;
import com.agynamix.simidude.clipboard.SourceDataManager;
import com.agynamix.simidude.impl.SimidudeApplicationContext;

public class SaveAsAction extends Action {
  
  final ApplicationWindow window;
  
  public SaveAsAction(ApplicationWindow w) {
    super("Save As...", IAction.AS_PUSH_BUTTON);
    this.window = w;
    setToolTipText("Save the selected entry to the filesystem");
    setAccelerator(SWT.MOD1 + 'S');     
    setImageDescriptor(PlatformIcons.getDescriptor(PlatformIcons.SAVE_CONTENTS_AS_TABLE_ENTRY));
  }
    
  /**
   * @see org.eclipse.jface.action.Action#run()
   */
  public void run() {
    SourceDataManager tm = ((SimidudeApplicationContext)ApplicationBase.getContext()).getSourceDataManager();
    IClipboardItem item = tm.getSelectedItem();
    if (item != null)
    {
      tm.saveClipboardItemAs(item, false);
    }
  }

}
