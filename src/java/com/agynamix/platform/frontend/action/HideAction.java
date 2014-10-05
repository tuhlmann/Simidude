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

public class HideAction extends Action implements IAction {
  
  final ApplicationWindow window;
  
  public HideAction(ApplicationWindow w) {
    this.window = w;
    setText("&Hide Window");
    setToolTipText("Hide this window");
    setAccelerator(SWT.MOD1 + 'H'); 
//    setImageDescriptor(ImageDescriptor.createFromImage(new Image(Display.getDefault(), getClass().getClassLoader().getResourceAsStream("icons/cancle.gif"))));
  }
  
  
  /**
   * @see org.eclipse.jface.action.Action#run()
   */
  public void run() {
    window.getShell().setVisible(false);
  }
  

}
