package com.agynamix.simidude.frontend.action;

import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.window.ApplicationWindow;

import com.agynamix.platform.frontend.preferences.IPreferenceConstants;
import com.agynamix.platform.icons.PlatformIcons;
import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.simidude.clipboard.SourceDataManager;
import com.agynamix.simidude.impl.SimidudeApplicationContext;
import com.agynamix.simidude.source.ISourceData;

public class SelectMonitoredClipboardItemsAction extends Action implements IMenuCreator {
  
  private Menu fMenu;

  final ApplicationWindow window;
  
  final Action monitorTextAction;
  final Action monitorImagesAction;
  final Action monitorFilesAction;


  public SelectMonitoredClipboardItemsAction(ApplicationWindow w)
  {
    super(null, IAction.AS_DROP_DOWN_MENU);
    setText("My Actions");
    setMenuCreator(this);
    this.window = w;
    setToolTipText("Click and select the clipboard item types that you want to monitor.");
    setImageDescriptor(PlatformIcons.getDescriptor(PlatformIcons.SELECT_MONITORED_CLIPBOARD_ITEMS));

    monitorTextAction   = new ToggleMonitorClipboardItemTypeAction(ISourceData.SourceType.TEXT, "Text Items", IPreferenceConstants.TOGGLE_CLIPBOARD_MONITOR_TEXT);
    monitorImagesAction = new ToggleMonitorClipboardItemTypeAction(ISourceData.SourceType.IMAGE, "Image Items", IPreferenceConstants.TOGGLE_CLIPBOARD_MONITOR_IMAGES);
    monitorFilesAction  = new ToggleMonitorClipboardItemTypeAction(ISourceData.SourceType.FILE, "File/Directory Items", IPreferenceConstants.TOGGLE_CLIPBOARD_MONITOR_FILES);
    
//    SourceDataManager tm = ((SimidudeApplicationContext) ApplicationBase.getContext()).getSourceDataManager();
  }

  public Menu getMenu(Menu parent)
  {
    return null;
  }
  
  public Menu getMenu(Control parent)
  {
    if (fMenu != null)
    {
      fMenu.dispose();
    }

    fMenu = new Menu(parent);
    addActionToMenu(fMenu, monitorTextAction);
    addActionToMenu(fMenu, monitorImagesAction);
    addActionToMenu(fMenu, monitorFilesAction);
//    new MenuItem(fMenu, SWT.SEPARATOR);

    return fMenu;
  }

  protected void addActionToMenu(Menu parent, Action action)
  {
    ActionContributionItem item = new ActionContributionItem(action);
    item.fill(parent, -1);
  }

  public void run()
  {
    Menu menu = getMenu(window.getShell());
    menu.setVisible(true);
  }

  /**
   * Get's rid of the menu, because the menu hangs on to * the searches, etc.
   */
  void clear()
  {
    dispose();
  }
  
  public void dispose()
  {
    if (fMenu != null)
    {
      fMenu.dispose();
      fMenu = null;
    }
  }

  public static class ToggleMonitorClipboardItemTypeAction extends Action {
    
    final String itemTypeDesc;
    final String preferenceKey;
    final ISourceData.SourceType sourceType;
    
    public ToggleMonitorClipboardItemTypeAction(ISourceData.SourceType sourceType, String itemTypeDesc, String preferenceKey) 
    {
      super("Monitor "+itemTypeDesc, IAction.AS_CHECK_BOX);
      this.sourceType = sourceType;
      this.itemTypeDesc = itemTypeDesc;
      this.preferenceKey = preferenceKey;
      boolean checkStatus = ApplicationBase.getContext().getConfiguration().getBoolean(this.preferenceKey);
      setChecked(checkStatus);
      SourceDataManager tm = ((SimidudeApplicationContext)ApplicationBase.getContext()).getSourceDataManager();
      tm.setClipboardMonitorTypeEnabled(this.sourceType, checkStatus);
    }
      
    public void run() {
      SourceDataManager tm = ((SimidudeApplicationContext)ApplicationBase.getContext()).getSourceDataManager();
      tm.setClipboardMonitorTypeEnabled(this.sourceType, isChecked());
//      if (isChecked())
//      {
//        System.out.println("Clipboard Monitoring ON");
//      } else {
//        System.out.println("Clipboard Monitoring OFF");            
//      }
      ApplicationBase.getContext().getConfiguration().setBoolean(this.preferenceKey, isChecked());
    }

  }
  
}
