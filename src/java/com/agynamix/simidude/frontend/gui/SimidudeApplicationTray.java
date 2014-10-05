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
package com.agynamix.simidude.frontend.gui;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import com.agynamix.platform.frontend.gui.ApplicationTray;
import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.simidude.clipboard.IClipboardItem;
import com.agynamix.simidude.clipboard.SourceDataManager;
import com.agynamix.simidude.impl.SimidudeApplicationContext;
import com.agynamix.simidude.infra.IItemAddedListener;

public class SimidudeApplicationTray extends ApplicationTray {

  public SimidudeApplicationTray(Window w, Shell shell, Image trayImage)
  {
    super(w, shell, trayImage);
  }
  
  @Override
  public void initializeTray()
  {
    super.initializeTray();
    SourceDataManager sdm = ((SimidudeApplicationContext)ApplicationBase.getContext()).getSourceDataManager();
    sdm.addItemAddedListener(new IItemAddedListener() {

      public void itemAdded(int insertPos, IClipboardItem item)
      {
        showTooltip(item);
      }
    });
  }

}
