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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.agynamix.platform.frontend.gui.ApplicationGUI;
import com.agynamix.platform.frontend.gui.ApplicationTray;
import com.agynamix.platform.icons.PlatformIcons;
import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.platform.infra.ApplicationInfo;
import com.agynamix.platform.infra.PlatformUtils;
import com.agynamix.simidude.clipboard.SourceDataManager;
import com.agynamix.simidude.frontend.ctrl.SourceDataDialogController;
import com.agynamix.simidude.impl.SimidudeApplicationContext;

public class SimidudeGUI extends ApplicationGUI {
  
  SourceDataDialogViewImpl   sourceDataView;
  SourceDataDialogController sourceDataController;

  @Override
  protected Point getInitialShellSize()
  {
    return new Point(600, 400);
  }

  @Override
  protected Image getShellImage()
  {
    if (PlatformUtils.isMacOs()) {
      return PlatformIcons.get(PlatformIcons.SIMIDUDE_MAC_ICN);
    }
    
    return PlatformIcons.get(PlatformIcons.SIMIDUDE_WND_ICN);
  }
  
  @Override
  protected String getInitialStatusLine()
  {
    String status = ApplicationInfo.getApplicationName()+", Version "+ApplicationInfo.getApplicationVersion();
    return status;
  }
  
  @Override
  protected Control fillMainWindow(Composite parent)
  {
    SourceDataManager sourceDataManager = ((SimidudeApplicationContext)ApplicationBase.getContext()).getSourceDataManager();
    sourceDataView = new SourceDataDialogViewImpl(); 
    sourceDataController = new SourceDataDialogController(sourceDataManager, sourceDataView);
    sourceDataView.setController(sourceDataController);
    sourceDataManager.setSourceDataDialogController(sourceDataController);
    final Composite c1 = sourceDataController.createContent(parent);
    
    return parent;
  }
  
  @Override
  protected Control getCustomToolbar()
  {
    return sourceDataController.getCustomToolbar();
  }

  @Override
  protected ApplicationTray createApplicationTray(Window window, Shell shell, Image image)
  {
    return new SimidudeApplicationTray(window, shell, image);  
  }
  
}
