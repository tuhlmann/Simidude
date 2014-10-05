/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *  * Contributors:
 *     IBM Corporation - initial API and implementation
 *     
 *     AGYNAMIX Torsten Uhlmann, http://www.agynamix.de
 *******************************************************************************/

package com.agynamix.platform.frontend.gui;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.action.CoolBarManager;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.agynamix.platform.frontend.action.CheckUpdatesAction;
import com.agynamix.platform.frontend.action.ClearClipboardTableAction;
import com.agynamix.platform.frontend.action.CopyAction;
import com.agynamix.platform.frontend.action.CutAction;
import com.agynamix.platform.frontend.action.ExitAction;
import com.agynamix.platform.frontend.action.HideAction;
import com.agynamix.platform.frontend.action.InputTextAction;
import com.agynamix.platform.frontend.action.NetworkAnalysisAction;
import com.agynamix.platform.frontend.action.PreferencesAction;
import com.agynamix.platform.frontend.action.RemoveSelectedClipboardEntry;
import com.agynamix.platform.frontend.action.SaveAsAction;
import com.agynamix.platform.frontend.action.SaveAsCompressedAction;
import com.agynamix.platform.frontend.action.SubmitBugzScoutAction;
import com.agynamix.platform.frontend.action.ToggleShowToolbarAction;
import com.agynamix.platform.frontend.preferences.IPreferenceConstants;
import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.platform.infra.ApplicationInfo;
import com.agynamix.platform.infra.IConfiguration;
import com.agynamix.platform.infra.PlatformUtils;
import com.agynamix.platform.log.ApplicationLog;
import com.agynamix.platform.net.NetUtils;
import com.agynamix.simidude.clipboard.IClipboardItem;
import com.agynamix.simidude.clipboard.SourceDataManager;
import com.agynamix.simidude.frontend.action.AboutAction;
import com.agynamix.simidude.frontend.action.SelectMonitoredClipboardItemsAction;
import com.agynamix.simidude.frontend.action.ShowOfflineHelpAction;
import com.agynamix.simidude.frontend.action.ToggleMonitorClipboardAction;
import com.agynamix.simidude.impl.SimidudeApplicationContext;
import com.agynamix.simidude.infra.SimidudeUtils;

/**
 * @author tuhlmann
 * 
 */
public abstract class ApplicationGUI extends ApplicationWindow {

  public static final String SERVICE_NAME = "ApplicationGUI";

  Image                      shellImage;
  ApplicationTray            myTray;

  protected IConfiguration   config;
  
  Logger log = ApplicationLog.getLogger(ApplicationGUI.class);

  protected IAction          exitAction;
  protected IAction          hideAction;
  protected ToggleMonitorClipboardAction toggleMonitorClipboardAction;
  protected IAction          selectMonitoredClipboardItemsAction;
  protected ToggleShowToolbarAction      toggleShowToolbarAction;
//  protected ToggleShowStatuslineAction toggleShowStatuslineAction;
  protected IAction                      clearClipboardTableAction;
  protected RemoveSelectedClipboardEntry removeSelectedClipboardEntryAction;
  protected IAction                      networkClearClipboardTableAction;
  protected RemoveSelectedClipboardEntry networkRemoveSelectedClipboardEntryAction;
//  protected IAction          onlineHelpAction;
  protected IAction          offlineHelpAction;
  protected IAction          aboutAction;
  protected IAction          preferencesAction;
  protected IAction          copyAction;
  protected IAction          cutAction;
  protected IAction          saveAsAction;
  protected IAction          saveAsCompressedAction;
  protected IAction          inputTextAction;
  protected IAction          submitABugAction;
  protected IAction          networkAnalysisAction;

  public ApplicationGUI()
  {
    super(null);
    ApplicationBase.getContext().registerService(ApplicationGUI.SERVICE_NAME, this);
    config = ApplicationBase.getContext().getConfiguration();
  }

  public void initializeApplicationGUI()
  {
    makeActions();
    addMenuBar();
    // addToolBar(SWT.TOP | SWT.FLAT | SWT.HORIZONTAL | SWT.SHADOW_OUT );
    // addCoolBar(SWT.TOP);
    addStatusLine();
    create();
    getMenuBarManager().updateAll(true);
    registerForMacOSXEvents();

//    toggleShowStatuslineAction.setStatusline(getStatusLineManager().getControl());
//    toggleShowStatuslineAction.showStatusline();
    
    // XXX Comment to disable license check
    if (config.getBoolean(IPreferenceConstants.IS_FIRST_RUN))
    {
//      System.out.println("First Run");
//      preferencesAction.run();
      config.setBoolean(IPreferenceConstants.IS_FIRST_RUN, false);
    }
    
    getShell().getDisplay().addFilter(SWT.KeyDown, new Listener(){
      public void handleEvent(Event event)
      {
        SimidudeUtils.keyPressed(event.keyCode);
      }
    });
    
    getShell().getDisplay().addFilter(SWT.KeyUp, new Listener(){
      public void handleEvent(Event event)
      {
        SimidudeUtils.keyReleased(event.keyCode);
      }
    });
    
  }

  protected void makeActions()
  {
    exitAction = new ExitAction(this);
    hideAction = new HideAction(this);
    toggleMonitorClipboardAction = new ToggleMonitorClipboardAction(this);
    selectMonitoredClipboardItemsAction = new SelectMonitoredClipboardItemsAction(this);
    clearClipboardTableAction = new ClearClipboardTableAction(this, false);
    networkClearClipboardTableAction = new ClearClipboardTableAction(this, true);
    removeSelectedClipboardEntryAction = new RemoveSelectedClipboardEntry(this, false);
    networkRemoveSelectedClipboardEntryAction = new RemoveSelectedClipboardEntry(this, true);
//    onlineHelpAction = new ShowOnlineHelpAction(this);
    offlineHelpAction = new ShowOfflineHelpAction(this);
    preferencesAction = new PreferencesAction(this);
    aboutAction = new AboutAction(this);
    copyAction = new CopyAction(this);
    cutAction = new CutAction(this);
    saveAsAction = new SaveAsAction(this);
    saveAsCompressedAction = new SaveAsCompressedAction(this);
    inputTextAction = new InputTextAction(this);
    toggleShowToolbarAction = new ToggleShowToolbarAction(this);
//    toggleShowStatuslineAction = new ToggleShowStatuslineAction(this);
    submitABugAction = new SubmitBugzScoutAction(this);
    networkAnalysisAction = new NetworkAnalysisAction(this);
  }

  protected StatusLineManager createStatusLineManager()
  {
    return new ApplicationStatusLineManager();
  }

  public ApplicationStatusLineManager getStatusLineManager()
  {
    return (ApplicationStatusLineManager) super.getStatusLineManager();
  }

  @Override
  protected CoolBarManager createCoolBarManager(int style)
  {
    CoolBarManager cbm = new CoolBarManager(style);
    cbm.add(createToolBarManager(SWT.FLAT));
    // cbm.add(createSearchToolbar(SWT.FLAT));
    return cbm;
  }

  @Override
  protected ToolBarManager createToolBarManager(int style)
  {
    ToolBarManager tm = new ToolBarManager(style);
    tm.add(exitAction);
    tm.add(new Separator());
    tm.add(toggleMonitorClipboardAction);
    tm.add(selectMonitoredClipboardItemsAction);
    tm.add(new Separator());
    tm.add(clearClipboardTableAction);
    tm.add(new Separator());
    tm.add(removeSelectedClipboardEntryAction);
    tm.add(new Separator());
    tm.add(offlineHelpAction);
    tm.add(new Separator());
    // tm.add(new SpacerToolbarItem());
    // SourceDataManager sdm = ((SimidudeApplicationContext)ApplicationBase.getContext()).getSourceDataManager();
    // ClipboardTableSearchBox searchBox = new ClipboardTableSearchBox(sdm);
    // tm.add(new Separator());
    // tm.add(searchBox);
    return tm;
  }

  // protected ToolBarManager createSearchToolbar(int style)
  // {
  // ToolBarManager tm = new ToolBarManager(style);
  // SourceDataManager sdm = ((SimidudeApplicationContext)ApplicationBase.getContext()).getSourceDataManager();
  // ClipboardTableSearchBox searchBox = new ClipboardTableSearchBox(sdm);
  // tm.add(new Separator());
  // tm.add(searchBox);
  //    
  // return tm;
  // }

  /**
   * @see org.eclipse.jface.window.ApplicationWindow#createMenuManager()
   */
  protected MenuManager createMenuManager()
  {
    MenuManager menubar    = new MenuManager();
    MenuManager filemenu   = new MenuManager("&File");
    MenuManager editmenu   = new MenuManager("&Edit");
    MenuManager windowmenu = new MenuManager("&Window");
    MenuManager helpmenu   = new MenuManager("&Help");
    menubar.add(filemenu);
    menubar.add(editmenu);
    menubar.add(windowmenu);
    menubar.add(helpmenu);

    filemenu.add(inputTextAction);
    filemenu.add(new Separator());
    filemenu.add(saveAsAction);
    filemenu.add(saveAsCompressedAction);
    filemenu.add(new Separator());
    filemenu.add(hideAction);
    if (!PlatformUtils.isMacOs())
    {
      filemenu.add(new Separator());
      filemenu.add(exitAction);
    }    
    
    filemenu.addMenuListener(new IMenuListener(){
      public void menuAboutToShow(IMenuManager manager)
      {
        SourceDataManager sdm = ((SimidudeApplicationContext)ApplicationBase.getContext()).getSourceDataManager();
        IClipboardItem item = sdm.getSelectedItem();
        saveAsAction.setEnabled(item != null);
        saveAsCompressedAction.setEnabled(item != null);
      }
    });
    
    editmenu.add(copyAction);
    editmenu.add(cutAction);
    editmenu.add(new Separator());
    editmenu.add(toggleMonitorClipboardAction);
    editmenu.add(new Separator());
    editmenu.add(removeSelectedClipboardEntryAction);
    editmenu.add(networkRemoveSelectedClipboardEntryAction);
    editmenu.add(clearClipboardTableAction);
    editmenu.add(networkClearClipboardTableAction);
    if (!PlatformUtils.isMacOs())
    {
      editmenu.add(new Separator());
      editmenu.add(preferencesAction);
      helpmenu.add(aboutAction);
    }
    
    windowmenu.add(toggleShowToolbarAction);
//    windowmenu.add(toggleShowStatuslineAction);
    
    helpmenu.add(offlineHelpAction);
    helpmenu.add(new Separator());
//    helpmenu.add(onlineHelpAction);
//    helpmenu.add(new Separator());
    helpmenu.add(new CheckUpdatesAction(this));
    helpmenu.add(new Separator());
    helpmenu.add(networkAnalysisAction);
//    helpmenu.add(new Separator());
//    helpmenu.add(submitABugAction);

    editmenu.addMenuListener(new IMenuListener(){
      public void menuAboutToShow(IMenuManager manager)
      {
        SourceDataManager sdm = ((SimidudeApplicationContext)ApplicationBase.getContext()).getSourceDataManager();
        IClipboardItem item = sdm.getSelectedItem();
        copyAction.setEnabled(item != null);
        cutAction.setEnabled(item != null);
//        diese 2 Actions sind als Listener registriert und enablen sich selbst.
//        removeSelectedClipboardEntryAction.setEnabled(item != null);
//        networkRemoveSelectedClipboardEntryAction.setEnabled(item != null);
      }
    });
        
    return menubar;
  }

  /**
   * Returns the image shown as the shell image
   */
  protected abstract Image getShellImage();

  /**
   * @return the initial size of the shell
   */
  protected abstract Point getInitialShellSize();

  protected abstract String getInitialStatusLine();

  protected abstract Control fillMainWindow(Composite parent);
  
  protected abstract Control getCustomToolbar();

  protected Control createContents(Composite parent)
  {
    shellImage = getShellImage();

    getShell().setText(getShellText());
    
    if (!PlatformUtils.isMacOs())
    {
      getShell().setImage(shellImage);
    }
    
    getShell().setVisible(false);

    Rectangle bounds = config.getRectangle(IPreferenceConstants.GUI_POSITION);
    if (bounds != null)
    {
      getShell().setBounds(bounds);
    } else {
      getShell().setSize(getInitialShellSize());
    }

    Control c1 = fillMainWindow(parent);
    
    // Now the toolbar was created and can be handed to the ToolbarAction
    toggleShowToolbarAction.setToolbar(getCustomToolbar());
    toggleShowToolbarAction.showToolbar();
    
    // Now we can register Actions with the SelectionProvider (the ClipboardTable)
    ((SimidudeApplicationContext)ApplicationBase.getContext()).getSourceDataManager().addSelectionChangedListener(removeSelectedClipboardEntryAction);
    ((SimidudeApplicationContext)ApplicationBase.getContext()).getSourceDataManager().addSelectionChangedListener(networkRemoveSelectedClipboardEntryAction);

    myTray = createTray(this, getShell(), shellImage);

    setStatus(getInitialStatusLine());

    getShell().addShellListener(new ShellAdapter() {

      public void shellClosed(ShellEvent arg0)
      {
        super.shellIconified(arg0);
        // System.out.println("Close");
        getShell().setVisible(false);
      }
    });

    return c1;
  }

  public String getShellText()
  {
    String shellText = "";
    String urlStr = NetUtils.getLocalHostAddress();
    if (ApplicationBase.getContext().getConfiguration().getBoolean(IPreferenceConstants.START_HTTP_SERVER))
    {
      urlStr = " - http://" + urlStr + ":" + ApplicationBase.getContext().getConfiguration().getProperty(IPreferenceConstants.HTTP_SERVER_PORT);
    } else {
      urlStr = " - " + urlStr;
    }
    shellText += urlStr;
    return ApplicationInfo.getApplicationName() + shellText;
  }

  protected abstract ApplicationTray createApplicationTray(Window window, Shell shell, Image image);

  /**
   * Create the SimiDude Tray
   * 
   * @param shell
   *          the shell we use
   * @param img
   *          the image to show
   * @return Returns an SimiDudeTray Object
   */
  ApplicationTray createTray(Window w, Shell shell, Image img)
  {
    ApplicationTray t = createApplicationTray(w, shell, img);
    if (t != null)
    {
      t.initializeTray();
    }
    return t;
  }

  protected boolean canHandleShellCloseEvent()
  {
    return false;
  }

  /**
   * @see org.eclipse.jface.window.ApplicationWindow#close()
   */
  public boolean close()
  {
    saveShellPosition(getShell().getBounds());
    return super.close();
  }

  private void saveShellPosition(Rectangle bounds)
  {
    IConfiguration config = ApplicationBase.getContext().getConfiguration();
    config.setRectangle(IPreferenceConstants.GUI_POSITION, bounds);
  }

  public void startup()
  {
    this.open();
  }

  /**
   * Runs the GUI. A call to this method blocks until the user exits.
   */
  public void run()
  {
    runEventLoop(getShell());

    shutdownApplicationGui();

    Display.getCurrent().dispose();
  }

  protected void shutdownApplicationGui()
  {
    myTray.closeTray();
    ApplicationBase.getInstance().exit();
  }

  /**
   * @see org.eclipse.jface.window.Window#open()
   */
  public int open()
  {
    super.open();
    if (getShell() == null)
    {
      // create the window
      create();
    }

    // limit the shell size to the display size
    constrainShellSize();

    if (config.getBoolean("app.start_minimized"))
    {
      getShell().setVisible(false);
      getShell().setMinimized(true);
    }

    // open the window
    getShell().open();

    if (config.getBoolean("app.start_minimized"))
    {
      getShell().setVisible(false);
      getShell().setMinimized(false);
    }
    return 0;
  }

  private void runEventLoop(Shell loopShell)
  {

    Display display;
    if (loopShell != null)
    {
      display = loopShell.getDisplay();

      while (loopShell != null && !loopShell.isDisposed())
      {
        try
        {
          if (!display.readAndDispatch())
            display.sleep();
        } catch (Throwable e)
        {
          e.printStackTrace();
          log.log(Level.SEVERE, e.getMessage(), e);
//          throw new RuntimeException(e.getMessage());
        }
      }
      display.update();
    }
  }

  public Composite createCustomToolbar(Composite parent)
  {
    CustomToolbar ctb = new CustomToolbar(parent);

//    ctb.add(exitAction);
//    ctb.add(new Separator());
    ctb.add(inputTextAction);
    ctb.add(new Separator());
    ctb.add(toggleMonitorClipboardAction);
    ctb.add(selectMonitoredClipboardItemsAction);
    ctb.add(new Separator());
    ctb.add(clearClipboardTableAction);
    ctb.add(removeSelectedClipboardEntryAction);
    ctb.add(new Separator());
    ctb.add(offlineHelpAction);

    return ctb.getToolbar();
  }

  // Generic registration with the Mac OS X application menu
  // Checks the platform, then attempts to register with the Apple EAWT
  // See OSXAdapter.java to see how this is done without directly referencing any Apple APIs
  public void registerForMacOSXEvents()
  {
    if (PlatformUtils.isMacOs())
    {
      try
      {
        CarbonUIEnhancer carbonUI = new CarbonUIEnhancer("About "+ApplicationInfo.getApplicationName());
        carbonUI.hookApplicationMenu(getShell().getDisplay(), new Listener() {
          public void handleEvent(Event event)
          {
            shutdownApplicationGui();
            // could veto
//          event.doit = false;
          }
        }, aboutAction, preferencesAction);
      } catch (Exception e)
      {
        System.err.println("Error while registering OSX specific events");
        e.printStackTrace();
      }
    }
  }

  public ApplicationTray getTray()
  {
    return myTray;
  }

}
