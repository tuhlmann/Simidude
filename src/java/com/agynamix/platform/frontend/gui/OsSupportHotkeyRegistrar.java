package com.agynamix.platform.frontend.gui;

import java.util.HashMap;
import java.util.Map;

import com.agynamix.ossupport.HotKeyDesc;
import com.agynamix.ossupport.HotkeyListener;
import com.agynamix.ossupport.OsSupport;

/**
 * @author tuhlmann
 *
 */
public class OsSupportHotkeyRegistrar implements IHotkeyRegistrar, HotkeyListener {
  
  Map<Integer, HotkeyListenerInfo> listenerMap = new HashMap<Integer, HotkeyListenerInfo>();
  
  int     currentHotkeyId = 1;
  boolean hotkeysEnabled  = true;

  public OsSupportHotkeyRegistrar()
  {
    try
    {
      if (OsSupport.checkInstanceAlreadyRunning())
      {
        System.out.println("AGYNAMIX OsSupport started twice");
      }
      // next check to make sure JIntellitype DLL can be found and we are on
      // a Windows operating System
      if (!OsSupport.isSupported(OsSupport.TypeSupported.OS))
      {
        hotkeysEnabled = false;
      } else {
        initOsSupport();
      }
    } catch (Throwable e)
    {
      System.out.println("Error initializing native Mac support: "+e.getMessage());
      hotkeysEnabled = false;
    }

  }

  public void initOsSupport()
  {
    try
    {
      // initialize JIntellitype with the frame so all windows commands can
      // be attached to this window
      OsSupport.getInstance().addHotKeyListener(this);
//      System.out.println("JIntellitype initialized");
    } catch (RuntimeException ex)
    {
      ex.printStackTrace();
      System.out.println("Your operating system is not supported by the OsSupport library.");
    }
  }
  
  public boolean isEnabled()
  {
    return hotkeysEnabled;
  }

  /**
   * Currently only works with a 1:1 relationship between Hotkey and listener
   */
  public void addHotkeyListener(String hotkeyCombination, IHotkeyListener listener)
  {
    if (hotkeysEnabled)
    {
      HotKeyDesc hotkey = parseHotkeyDefinition(hotkeyCombination);
      
  //    System.out.println("modKey="+modKey);
  //    System.out.println("hotKey="+hotKey);
      OsSupport.getInstance().registerHotKey(currentHotkeyId, hotkey);
      listenerMap.put(currentHotkeyId, new HotkeyListenerInfo(this, currentHotkeyId, hotkey, listener));
  //    System.out.println("JIntellitypeHotkeyRegistrar.addHotkeyListener called");
      currentHotkeyId++;
    }
  }
  
  public void onHotKey(int hotkeyId)
  {
    HotkeyListenerInfo listenerInfo = listenerMap.get(hotkeyId);
    if (listenerInfo != null)
    {
      listenerInfo.onHotkey(hotkeyId);
    } else {
      //System.out.println("No listener registered for id "+hotkeyId);
    }
      
  }
  
  public void unregisterHotkeys()
  {
    if (hotkeysEnabled)
    {
      for (Object o : listenerMap.keySet())
      {
        OsSupport.getInstance().unregisterHotKey(((Integer)o).intValue());
  //      System.out.println("Remove hotkeyId "+o);
      }
      listenerMap.clear();
    }
  }
  
  public void activateGlobalPaste(HotkeyListenerInfo listenerInfo)
  {
    if (hotkeysEnabled)
    {
      if (OsSupport.isSupported(OsSupport.TypeSupported.PASTE))
      {   
        OsSupport.getInstance().activateGlobalePaste(listenerInfo.getHotKeyDesc());
      }
    }
  }

  public HotKeyDesc parseHotkeyDefinition(String hotKeyStr)
  {
    if (hotkeysEnabled) {
      return OsSupport.getInstance().parseHotkeyDefinition(hotKeyStr);
    } else {
      return null;
    }
  }


}
