/*
 * Copyright (c) 2004 agynamiX.com. All rights reserved. 
 * This file is made available under the terms of the
 * license this product is released under.
 * 
 * For details please see the license file you should have
 * received, or go to:
 * 
 * http://www.agynamix.com
 * 
 * Contributors: agynamiX.com (http://www.agynamix.com)
 */
package com.agynamix.platform.infra;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.MenuItem;




/**
 * PluginMenuItem provides all necessary information to create Items in tray or tool bar
 * @version $Revision: 10 $ $Date: 2004-11-17 13:30:10 +0100 (Mi, 17 Nov 2004) $
 * @author tuhlmann
 */
public class PluginMenuEntry {
  
  final Image         image;
  final String        mEntry;
  final int           buttonStyle;
  final boolean       checkButton;
  
  boolean             checked;
  
  IPluginMenuAction    menuAction;

  public PluginMenuEntry(Image image, String mEntry) {
    this(image, mEntry, false);
  }
  
  public PluginMenuEntry(Image image, String mEntry, boolean isCheckButton) {
    this.image         = image;
    this.mEntry        = mEntry;
    this.checkButton   = isCheckButton;
    this.buttonStyle   = isCheckButton ? SWT.CHECK : SWT.PUSH;
  }
  
  /**
   * Get the text of this menu icon
   */
  public String getText() {
    return mEntry;
  }
  
  /**
   * get the Image of this menu entry.
   */
  public Image getImage() {
    return image;
  }

  public int getButtonStyle()
  {
    return this.buttonStyle;
  }
  
  public boolean isCheckButton() {
    return this.checkButton;
  }
  
  public void setPluginMenuAction(IPluginMenuAction a) {
    this.menuAction = a;
  }
  
  public void action(final MenuItem menuItem) {
    menuAction.run(menuItem);
  }

  public void setChecked(boolean checked)
  {
    this.checked = checked;    
  }
  
  public boolean getChecked()
  {
    return this.checked;
  }
  
}

