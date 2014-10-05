package com.agynamix.platform.frontend.gui;

import java.util.HashMap;
import java.util.Map;

import com.agynamix.ossupport.HotKeyDesc;
import com.agynamix.platform.infra.ApplicationInfo;
import com.agynamix.platform.infra.Tupel;
import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.JIntellitype;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

/**
 * @author tuhlmann
 *
 */
public class JIntellitypeHotkeyRegistrar extends AbstractHotkeyRegistrar implements HotkeyListener {
	
	public interface User32 extends Library {
		User32 lib = (User32) Native.loadLibrary("user32" , User32.class);
		
		void keybd_event(short bVk, short bScan, long dwFlags, com.sun.jna.Pointer dwExtraInfo);
	}
	
  
  /**
   * ALT key for registering Hotkeys.
   */
  public static final int MOD_ALT = 1;
  
  /**
   * CONTROL key for registering Hotkeys.
   */
  public static final int MOD_CONTROL = 2;
  
  /**
   * SHIFT key for registering Hotkeys.
   */
  public static final int MOD_SHIFT = 4;
  
  /**
   * WINDOWS key for registering Hotkeys.
   */
  public static final int MOD_WIN = 8;

  final static short VK_SHIFT        = 0x10;
  final static short VK_CONTROL      = 0x11;
  final static short VK_ALT          = 0x12;
  final static short KEYEVENTF_KEYUP = 0x2;

  Map<Integer, HotkeyListenerInfo> listenerMap = new HashMap<Integer, HotkeyListenerInfo>();
  
  int     currentHotkeyId = 100;
  boolean hotkeysEnabled  = true;

  public JIntellitypeHotkeyRegistrar()
  {
    try
    {
      if (JIntellitype.checkInstanceAlreadyRunning(ApplicationInfo.getApplicationName()))
      {
        System.out.println("JIntellitype started twice");
      }
      // next check to make sure JIntellitype DLL can be found and we are on
      // a Windows operating System
      if (!JIntellitype.isJIntellitypeSupported())
      {
        hotkeysEnabled = false;
      } else {
        initJIntellitype();
      }
    } catch (Exception e)
    {
      System.out.println("Error initializing JIntellitype: "+e.getMessage());
      hotkeysEnabled = false;
    }

  }

  public void initJIntellitype()
  {
    try
    {
      // initialize JIntellitype with the frame so all windows commands can
      // be attached to this window
      JIntellitype.getInstance().addHotKeyListener(this);
//      System.out.println("JIntellitype initialized");
    } catch (RuntimeException ex)
    {
      System.out.println("Either you are not on Windows, or there is a problem with the JIntellitype library!");
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
      Tupel<Integer, Integer> hotkeys = parseHotkeyDefinition(hotkeyCombination);

  //    System.out.println("modKey="+modKey);
  //    System.out.println("hotKey="+hotKey);
      JIntellitype.getInstance().registerHotKey(currentHotkeyId, hotkeys.getValue1(), hotkeys.getValue2());
      listenerMap.put(currentHotkeyId, new HotkeyListenerInfo(this, currentHotkeyId, new HotKeyDesc(hotkeys.getValue1(), 0, hotkeys.getValue2()), listener));
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
      System.out.println("No listener registered for id "+hotkeyId);
    }
      
  }
  
  public void unregisterHotkeys()
  {
    if (hotkeysEnabled)
    {
      for (Object o : listenerMap.keySet())
      {
        JIntellitype.getInstance().unregisterHotKey(((Integer)o).intValue());
  //      System.out.println("Remove hotkeyId "+o);
      }
      listenerMap.clear();
    }
  }
  
  public void activateGlobalPaste(HotkeyListenerInfo listenerInfo)
  {
//      IUnknown scriptHost = new IUnknownImpl(CLSID.createFromProgID("WScript.Shell"), ClsCtx.INPROC_SERVER);
//      Automation scriptHostAutomation = new Automation(scriptHost, true);
//      scriptHostAutomation.invoke("SendKeys", "^v");  // "Ctrl+V"
    
    resetKeyPress(listenerInfo.getHotKeyDesc().getModifier(), listenerInfo.getHotKeyDesc().getKeycode());
    
//    Function keybd_event = User32.getInstance().getFunction("keybd_event");
//    keybd_event.invoke(null, new UInt8(VK_CONTROL), new UInt8((short)0x9d), new UInt32(0), new Pointer.Void());
//    keybd_event.invoke(null, new UInt8((short)'V'), new UInt8((short)0xaf), new UInt32(0), new Pointer.Void());
//    keybd_event.invoke(null, new UInt8((short)'V'), new UInt8((short)0xaf), new UInt32(KEYEVENTF_KEYUP), new Pointer.Void());
//    keybd_event.invoke(null, new UInt8(VK_CONTROL), new UInt8((short)0x9d), new UInt32(KEYEVENTF_KEYUP), new Pointer.Void());    

    User32.lib.keybd_event(VK_CONTROL, (short)0x9d, (short)0, Pointer.NULL);
    User32.lib.keybd_event((short)'V', (short)0xaf, (short)0, Pointer.NULL);
    User32.lib.keybd_event((short)'V', (short)0xaf, KEYEVENTF_KEYUP, Pointer.NULL);
    User32.lib.keybd_event(VK_CONTROL, (short)0x9d, KEYEVENTF_KEYUP, Pointer.NULL);

  }

  private void resetKeyPress(int modKey, int hotKey)
  {
    //Function keybd_event = User32.getInstance().getFunction("keybd_event");    
    resetKey((short)hotKey);
    
    if ((modKey & MOD_SHIFT) != 0)
    {
      resetKey(VK_SHIFT);      
    }
    
    if ((modKey & MOD_ALT) != 0)
    {
      resetKey(VK_ALT);      
    }
    
    if ((modKey & MOD_CONTROL) != 0)
    {
      resetKey(VK_CONTROL);      
    }
    
  }

  private void resetKey(short key)
  {
    short scanCode = mapVirtualKey(key);
    User32.lib.keybd_event(key, scanCode, KEYEVENTF_KEYUP, Pointer.NULL);
  }

  private short mapVirtualKey(short key)
  {
//    Function mapVirtualKey = User32.getInstance().getFunction("MapVirtualKey");
//    UInt8 result = new UInt8();
//    mapVirtualKey.invoke(result, new UInt8(key), new UInt8((short)0));
//    return (short) result.getValue();
    return (short)0;
  }
  
  protected int tokenToModifier(String token)
  {
    if (token.equalsIgnoreCase("Ctrl"))
    {
      return MOD_CONTROL;
    } else if (token.equalsIgnoreCase("Shift"))
    {
      return MOD_SHIFT;
    } else if (token.equalsIgnoreCase("Alt"))
    {
      return MOD_ALT;
    }
    return 0;
  }


}
