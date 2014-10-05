package com.agynamix.platform.frontend.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jxgrabkey.HotkeyConflictException;
import jxgrabkey.HotkeyListener;
import jxgrabkey.JXGrabKey;
import jxgrabkey.X11KeysymDefinitions;

import com.agynamix.ossupport.HotKeyDesc;
import com.agynamix.platform.frontend.gui.JIntellitypeHotkeyRegistrar.User32;
import com.agynamix.platform.frontend.gui.x11.X11;
import com.agynamix.platform.frontend.gui.x11.X11.Display;
import com.agynamix.platform.frontend.gui.x11.X11.KeySym;
import com.agynamix.platform.infra.Tupel;
import com.agynamix.platform.log.ApplicationLog;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;

/**
 * @author tuhlmann
 *
 */
public class JXGrabKeyHotkeyRegistrar extends AbstractHotkeyRegistrar implements HotkeyListener {
  
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
  
  Logger log = ApplicationLog.getLogger(JXGrabKeyHotkeyRegistrar.class);
  
  int currentHotkeyId = 100;
  
  boolean hotkeysEnabled = true;

  public JXGrabKeyHotkeyRegistrar()
  {
    initJXGrabKey();
  }

  public void initJXGrabKey()
  {
    if (!loadJarLibrary("libJXGrabKey.so")) 
    {
      System.out.println("Can not load native Hotkey library. Disabling hotkeys...");
      hotkeysEnabled = false;
    }

    if (hotkeysEnabled)
    {
      try
      {
        // initialize JIntellitype with the frame so all windows commands can
        // be attached to this window
        JXGrabKey.getInstance().addHotkeyListener(this);
  //      System.out.println("JIntellitype initialized");
      } catch (RuntimeException ex)
      {
        System.out.println("Either you are not on Linux, or there is a problem with the JXGrabKey library!");
      }
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

    if (!hotkeysEnabled) return;
    
//    System.out.println("modKey="+modKey);
//    System.out.println("hotKey="+hotKey);
    try
    {
      Tupel<Integer, Integer> hotkeys = parseHotkeyDefinition(hotkeyCombination);
      
      JXGrabKey.getInstance().registerAwtHotkey(currentHotkeyId, hotkeys.getValue1(), hotkeys.getValue2());
      listenerMap.put(currentHotkeyId, new HotkeyListenerInfo(this, currentHotkeyId, new HotKeyDesc(hotkeys.getValue1(), 0, hotkeys.getValue2()), listener));
      currentHotkeyId++;
//      System.out.println("JXGrabKeyHotkeyRegistrar.addHotkeyListener called");
    } catch (HotkeyConflictException e)
    {
      log.log(Level.WARNING, "Hotkey could not be registered.", e);
    }
  }
  
  public void onHotkey(int hotkeyId)
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
    if (!hotkeysEnabled) return;
    
    for (Object o : listenerMap.keySet())
    {
      JXGrabKey.getInstance().unregisterHotKey(((Integer)o).intValue());
//      System.out.println("Remove hotkeyId "+o);
    }
    listenerMap.clear();
  }
  
  public void activateGlobalPaste(HotkeyListenerInfo listenerInfo)
  {
    // get X11 display
    X11.Display display = X11.INSTANCE.XOpenDisplay(null);
    
    if (display == null) {
        log.warning("Can't open X11 display");
    } else {
    	
    	boolean allKeysUp = false;
    	while (!allKeysUp)
    	{
    		allKeysUp = true;
    		byte[] keysReturn = new byte[32];
    		X11.INSTANCE.XQueryKeymap(display, keysReturn);
    		//System.out.print("KeyMap State:");
    		for (int i = 0; i < 32; i++) {
    			//System.out.print(keysReturn[i]+" ");
    			if (keysReturn[i] != 0) {
    				allKeysUp = false;
    			}
    		}
    		//System.out.println("");
    		try {
				Thread.sleep(50);
			} catch (InterruptedException IGNORE) {}
    	}
    	
	  //System.out.println("Opened X11 Display. Can send Keypresses now..."); 

	  byte ctrlKC = X11.INSTANCE.XKeysymToKeycode(display, new X11.KeySym(X11KeysymDefinitions.CONTROL_L));
	  byte vKC = X11.INSTANCE.XKeysymToKeycode(display, new X11.KeySym(X11KeysymDefinitions.V));
	   
      X11.XTest.INSTANCE.XTestFakeKeyEvent(display, ctrlKC, true, new NativeLong(0)); // CTRL DOWN
      X11.XTest.INSTANCE.XTestFakeKeyEvent(display, vKC, true, new NativeLong(0)); // V DOWN
      X11.XTest.INSTANCE.XTestFakeKeyEvent(display, vKC, false, new NativeLong(0)); // V UP
      X11.XTest.INSTANCE.XTestFakeKeyEvent(display, ctrlKC, false, new NativeLong(0)); // CTRL UP
      
      X11.INSTANCE.XCloseDisplay(display);
    }
        
  }
  
  protected int tokenToModifier(String token)
  {
    if (token.equalsIgnoreCase("Ctrl"))
    {
      return java.awt.event.InputEvent.CTRL_MASK;
    } else if (token.equalsIgnoreCase("Shift"))
    {
      return java.awt.event.InputEvent.SHIFT_MASK;
    } else if (token.equalsIgnoreCase("Alt"))
    {
      return java.awt.event.InputEvent.ALT_MASK;
    }
    return 0;
  }
  
  protected int tokenToKeyCode(String token)
  {
    int swtKey = (int)token.charAt(0);
    return JXGrabKeyKeyCodeTranslator.translateSWTKey(swtKey);
  }

}
