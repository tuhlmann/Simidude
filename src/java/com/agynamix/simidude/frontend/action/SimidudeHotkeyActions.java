package com.agynamix.simidude.frontend.action;

import com.agynamix.platform.frontend.gui.ApplicationGUI;
import com.agynamix.platform.frontend.gui.ApplicationTray;
import com.agynamix.platform.frontend.gui.HotkeyListenerInfo;
import com.agynamix.platform.frontend.gui.HotkeyRegistrarFactory;
import com.agynamix.platform.frontend.gui.IHotkeyListener;
import com.agynamix.platform.frontend.gui.IHotkeyRegistrar;
import com.agynamix.platform.frontend.preferences.IPreferenceConstants;
import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.platform.infra.PlatformUtils;
import com.agynamix.simidude.clipboard.SourceDataManager;
import com.agynamix.simidude.impl.SimidudeApplicationContext;

public class SimidudeHotkeyActions {
  
  public static void unregisterHotkeys()
  {
    IHotkeyRegistrar hotKeyRegistrar = HotkeyRegistrarFactory.getHotkeyRegistrarInstance();
    hotKeyRegistrar.unregisterHotkeys();
  }

  public static void registerHotkeys()
  {
    String bringSimidudeToFront = ApplicationBase.getContext().getConfiguration().getProperty(IPreferenceConstants.HOTKEY_BRING_SIMIDUDE_TO_FRONT);
    String activateLastEntry    = ApplicationBase.getContext().getConfiguration().getProperty(IPreferenceConstants.HOTKEY_ACTIVATE_LAST_ENTRY);

    IHotkeyRegistrar hotKeyRegistrar = HotkeyRegistrarFactory.getHotkeyRegistrarInstance();
    
    if ((bringSimidudeToFront != null) && (bringSimidudeToFront.length() > 0))
    {
      hotKeyRegistrar.addHotkeyListener(bringSimidudeToFront, new IHotkeyListener(){
        public void onHotkey(HotkeyListenerInfo listenerInfo) {
          PlatformUtils.safeAsyncRunnable(new Runnable() {
            public void run()
            {
              ApplicationGUI gui = ((SimidudeApplicationContext)ApplicationBase.getContext()).getApplicationGUI();
              ApplicationTray tray = gui.getTray();
              tray.toggleMainWindowActive(false);
            }
          });
        }
      });
    }
    if ((activateLastEntry != null) && (activateLastEntry.length() > 0))
    {
      hotKeyRegistrar.addHotkeyListener(activateLastEntry, new IHotkeyListener(){
        public void onHotkey(final HotkeyListenerInfo listenerInfo) {
          PlatformUtils.safeAsyncRunnable(new Runnable() {
            public void run()
            {
              SourceDataManager sdm = ((SimidudeApplicationContext)ApplicationBase.getContext()).getSourceDataManager();
              sdm.activateItem(0);
              listenerInfo.getHotkeyRegistrar().activateGlobalPaste(listenerInfo);
            }
          });
        }
      });
    }
    
    
  }

}
