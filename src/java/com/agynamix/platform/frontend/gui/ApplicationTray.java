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
package com.agynamix.platform.frontend.gui;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

import com.agynamix.platform.frontend.action.IActionWithValue;
import com.agynamix.platform.frontend.preferences.IPreferenceConstants;
import com.agynamix.platform.icons.PlatformIcons;
import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.platform.infra.ApplicationInfo;
import com.agynamix.platform.infra.IPluginMenuAction;
import com.agynamix.platform.infra.PlatformUtils;
import com.agynamix.platform.infra.PluginMenuEntry;
import com.agynamix.simidude.clipboard.IClipboardItem;
import com.agynamix.simidude.clipboard.SourceDataManager;
import com.agynamix.simidude.impl.SimidudeApplicationContext;

/**
 * @version $Revision: 27 $ $Date: 2004-11-27 22:42:44 +0100 (Sa, 27 Nov 2004) $
 * @author tuhlmann
 */
public class ApplicationTray {

  final Window window;
  final Shell  shell;
  final Image  trayImage;
  Image        exitImage;
  Image        openImage;
  
  Tray         tray;
  TrayItem     trayItem;
  ToolTip      trayItemTooltip;
  
  MenuItem     toggleMonitorClipboard;
  
  public ApplicationTray(Window w, Shell shell, Image trayImage)
  {
    this.window = w;
    this.shell = shell;
    this.trayImage = trayImage;
  }

  public void initializeTray()
  {
    tray = shell.getDisplay().getSystemTray();
    if (tray == null)
    {
      // System has no tray
      return;
    }
    
    trayItem = new TrayItem(tray, SWT.NONE);
    
    trayItemTooltip = new ToolTip(shell, SWT.BALLOON | SWT.ICON_INFORMATION);
    trayItemTooltip.setAutoHide(true);

    trayItem.setToolTip(trayItemTooltip);

    exitImage = PlatformIcons.get(PlatformIcons.EXIT);
    openImage = PlatformIcons.get(PlatformIcons.HOME);

    trayItem.setToolTipText(ApplicationInfo.getApplicationName() + " System Tray");
    trayItem.addListener(SWT.Selection, new Listener() {

      public void handleEvent(Event event)
      {
        toggleMainWindowActive(true);
      }
    });
//    trayItem.addListener(SWT.DefaultSelection, new Listener() {
//
//      public void handleEvent(Event event)
//      {
//        toggleMainWindowActive();
//      }
//    });

    final Menu menu = new Menu(shell, SWT.POP_UP);

    PluginMenuEntry[] mEntries = new PluginMenuEntry[3];
    mEntries[0] = new PluginMenuEntry(PlatformIcons.get(PlatformIcons.COPY), "Copy Last Entry to Clipboard");
    mEntries[0].setPluginMenuAction(new IPluginMenuAction() {

      public void run(MenuItem menuItem)
      {
        SourceDataManager sdm = ((SimidudeApplicationContext)ApplicationBase.getContext()).getSourceDataManager();
        sdm.activateItem(0);
      }
    });

    mEntries[1] = new PluginMenuEntry(PlatformIcons.get(PlatformIcons.SAVE_CONTENTS_AS_TABLE_ENTRY), "Save Last Entry As...");
    mEntries[1].setPluginMenuAction(new IPluginMenuAction() {
      
      public void run(MenuItem menuItem)
      {
        SourceDataManager sdm = ((SimidudeApplicationContext)ApplicationBase.getContext()).getSourceDataManager();
        IClipboardItem item = sdm.getClipboardItem(0);
        if (item != null)
        {
          sdm.saveClipboardItemAs(item, false);
        }
      }
    });
    
    mEntries[2] = new PluginMenuEntry(PlatformIcons.get(PlatformIcons.SAVE_CONTENTS_AS_TABLE_ENTRY), "Save Last Entry Compressed...");
    mEntries[2].setPluginMenuAction(new IPluginMenuAction() {
      
      public void run(MenuItem menuItem)
      {
        SourceDataManager sdm = ((SimidudeApplicationContext)ApplicationBase.getContext()).getSourceDataManager();
        IClipboardItem item = sdm.getClipboardItem(0);
        if (item != null)
        {
          sdm.saveClipboardItemAs(item, true);
        }
      }
    });
        
    for (int j = 0; j < mEntries.length; j++)
    {
      final PluginMenuEntry entry = mEntries[j];
      final MenuItem mi = new MenuItem(menu, entry.getButtonStyle());
      if (entry.isCheckButton())
      {
        mi.setSelection(entry.getChecked());
      }
      mi.setText(entry.getText());
      mi.setImage(entry.getImage());
      mi.addListener(SWT.Selection, new Listener() {
        public void handleEvent(Event e)
        {
          entry.action(mi);
        }
      });

      new MenuItem(menu, SWT.SEPARATOR);
    }
    

    toggleMonitorClipboard = new MenuItem(menu, SWT.CHECK);
    toggleMonitorClipboard.setText("Toggle Clipboard Monitoring");
    toggleMonitorClipboard.setImage(PlatformIcons.get(PlatformIcons.TOGGLE_MONITOR_CLIPBOARD));
    toggleMonitorClipboard.addListener(SWT.Selection, new Listener(){
      public void handleEvent(Event event)
      {
        IActionWithValue toggleAction = ((SimidudeApplicationContext)ApplicationBase.getContext()).getApplicationGUI().toggleMonitorClipboardAction;
        boolean newValue = toggleMonitorClipboard.getSelection();
        toggleAction.runWithValue(newValue);        
      }
    });
    toggleMonitorClipboard.setSelection(ApplicationBase.getContext().getConfiguration().getBoolean(IPreferenceConstants.TOGGLE_CLIPBOARD_MONITOR));

    MenuItem miOpen = new MenuItem(menu, SWT.PUSH);
    miOpen.setText("Open Window");
    miOpen.setImage(openImage);
    miOpen.addListener(SWT.Selection, new Listener() {

      public void handleEvent(Event arg0)
      {
        openMainWindow();
      }
    });

    MenuItem miHide = new MenuItem(menu, SWT.PUSH);
    miHide.setText("Hide Window");
//    miHide.setImage(openImage);
    miHide.addListener(SWT.Selection, new Listener() {
      
      public void handleEvent(Event arg0)
      {
        closeMainWindow();
      }
    });
    
    MenuItem miExit = new MenuItem(menu, SWT.PUSH);
    miExit.setText("Exit Simidude");
    miExit.setImage(exitImage);
    miExit.addListener(SWT.Selection, new Listener() {

      public void handleEvent(Event arg0)
      {
        closeApplication();
      }
    });

    // TODO: Should listen to left MB on Mac- so we might listen to a different event
    // when on Mac
    trayItem.addListener(SWT.MenuDetect, new Listener() {

      public void handleEvent(Event event)
      {
        toggleMonitorClipboard.setSelection(ApplicationBase.getContext().getConfiguration().getBoolean(IPreferenceConstants.TOGGLE_CLIPBOARD_MONITOR));        
        menu.setVisible(true);
      }
    });
    trayItem.setImage(trayImage);
    // image.dispose();
    
    // Bind DropTarget object
    // ??? Where can I find a control to bind it to ???
//    TransferDataManager tm = ((SimidudeApplicationContext)ApplicationBase.getContext()).getTransferDataManager();
//    System.out.println("TM="+tm);
//    new SimidudeTrayDropTarget(this, tm);    
    
  }

  public void openMainWindow()
  {
    if (!shell.isVisible())
    {
      shell.setVisible(true);
      shell.forceActive();
      shell.forceFocus();
      shell.setMinimized(false);
    } else {
      if (!shell.isFocusControl())
      {
        shell.forceActive();
        shell.forceFocus();       
        shell.setMinimized(false);
      }
    }
  }

  protected void closeMainWindow()
  {
    if (shell.isVisible())
    {
      shell.setVisible(false);
    }
  }

  public void toggleMainWindowActive(boolean clickedFromTray)
  {
    if ((!shell.isVisible())) //|| ((!shell.isFocusControl())  && (!clickedFromTray)))
    {
      openMainWindow();
    } else {
      closeMainWindow();
    }
  }
  
  public void showTooltip(final IClipboardItem item)
  {
    if (ApplicationBase.getContext().getConfiguration().getBoolean(IPreferenceConstants.SHOW_BALLOON_TOOLTIP))
    {
      PlatformUtils.safeAsyncRunnable(new Runnable() {
        public void run()
        {
          trayItemTooltip.setMessage("New Simidude Entry:\n"+item.getShortDescription());
          trayItemTooltip.setVisible(true);
        }
      });
    }
  }

  protected void closeApplication()
  {
    this.dispose();
    window.close();
  }

  protected void dispose()
  {
    if (exitImage != null)
    {
      exitImage.dispose();
    }
    if (openImage != null)
    {
      openImage.dispose();
    }
  }

  public void closeTray()
  {
    if ((tray != null) && (trayItem != null))
    {
      trayItem.setVisible(false);
      trayItem.dispose();
      tray.dispose();
    }
  }

}
