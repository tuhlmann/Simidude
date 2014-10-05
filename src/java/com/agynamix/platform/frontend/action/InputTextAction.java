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
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;

import com.agynamix.platform.frontend.dialogs.InputTextDialog;
import com.agynamix.platform.icons.PlatformIcons;
import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.platform.infra.IQueueManager;
import com.agynamix.simidude.clipboard.SourceDataManager;
import com.agynamix.simidude.impl.SimidudeApplicationContext;
import com.agynamix.simidude.source.SourceDataFactory;

public class InputTextAction extends Action {
  
  final ApplicationWindow window;
  
  public InputTextAction(ApplicationWindow w) {
    super("Simple Text Editor", IAction.AS_PUSH_BUTTON);
    this.window = w;
    setToolTipText("Open the Simple Text Editor to directly input text");
    setAccelerator(SWT.MOD1 + 'N');     
    setImageDescriptor(PlatformIcons.getDescriptor(PlatformIcons.INPUT_TEXT));
  }
    
  /**
   * @see org.eclipse.jface.action.Action#run()
   */
  public void run() {
    InputTextDialog dialog = new InputTextDialog(window.getShell());
    if (dialog.open() == IDialogConstants.OK_ID)
    {
      String contents = dialog.getText();
      if ((contents != null) && (contents.length() > 0))
      {
        IQueueManager queueManager = ((SimidudeApplicationContext)ApplicationBase.getContext()).getQueueManager();
        queueManager.put(IQueueManager.QUEUE_SOURCE_DATA_MONITOR, SourceDataFactory.createFromText(contents));
      }
    }
  }
  

}
