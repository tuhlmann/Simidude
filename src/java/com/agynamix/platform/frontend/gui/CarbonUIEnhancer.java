package com.agynamix.platform.frontend.gui;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;

@SuppressWarnings("unchecked")
public class CarbonUIEnhancer {
  private static final int    kHICommandPreferences = ('p' << 24) + ('r' << 16) + ('e' << 8) + 'f';
  private static final int    kHICommandAbout       = ('a' << 24) + ('b' << 16) + ('o' << 8) + 'u';
  private static final int    kHICommandServices    = ('s' << 24) + ('e' << 16) + ('r' << 8) + 'v';

  private final String fgAboutActionName;
  final Class osCls;
  
  public CarbonUIEnhancer(String aboutTitle)
  {
    osCls = classForName("org.eclipse.swt.internal.carbon.OS");
    fgAboutActionName = aboutTitle;
  }

  /**
   * See Apple Technical Q&A 1079 (http://developer.apple.com/qa/qa2001/qa1079.html)
   */
  public void hookApplicationMenu(Display display, final Listener quitListener, final IAction aboutAction, final IAction preferencesAction)
  {
    // Callback target
    Object target = new Object() {
      @SuppressWarnings("unused")
      int commandProc(int nextHandler, int theEvent, int userData)
      {
        int osActualEventKind = (Integer) invoke(osCls, "GetEventKind", new Class[]{int.class}, theEvent);
        int osEventProcessCommand = (Integer) fieldValue(osCls, "kEventProcessCommand");
        if (osActualEventKind == osEventProcessCommand)
        {
//          HICommand command = new HICommand();          
          Class hiCmdCls = classForName("org.eclipse.swt.internal.carbon.HICommand");
          Object command = newInstance("org.eclipse.swt.internal.carbon.HICommand");
          
          Class[] pt = new Class[]{int.class, int.class, int.class, int[].class, int.class, int[].class, hiCmdCls};          
          invoke(osCls, "GetEventParameter", pt, theEvent, fieldValue(osCls, "kEventParamDirectObject"), 
              fieldValue(osCls, "typeHICommand"), null, fieldValue(command, "sizeof"), null, command);
          switch ((Integer)fieldValue(command, "commandID"))
          {
            case kHICommandPreferences:
              return runAction(preferencesAction); 
            case kHICommandAbout:
              return runAction(aboutAction); 
            default:
              break;
          }
        }
        return (Integer) fieldValue(osCls, "eventNotHandledErr");
      }
    };
    
    Class[] pt = new Class[] {Object.class, String.class, int.class};
    final Object commandCallback = newInstance("org.eclipse.swt.internal.Callback", pt, target, "commandProc", 3);    
    int commandProc = (Integer)invoke(commandCallback, "getAddress");
    if (commandProc == 0)
    {
      invoke(commandCallback, "dispose");
      return; // give up
    }

    // Install event handler for commands
    int[] mask = new int[] { (Integer)fieldValue(osCls, "kEventClassCommand"), (Integer)fieldValue(osCls, "kEventProcessCommand") };
    pt = new Class[] {int.class, int.class, int.class, int[].class, int.class, int[].class};
    
    int appEventTarget = (Integer)invoke(osCls, "GetApplicationEventTarget");
    invoke(osCls, "InstallEventHandler", pt, appEventTarget, commandProc, mask.length / 2, mask, 0, null);

    // create About Eclipse menu command
    int[] outMenu = new int[1];
    short[] outIndex = new short[1];
//    GetIndMenuItemWithCommandID(int mHandle, int commandId, int index, int[] outMenu, short[] outIndex);
    int indMenuItem = (Integer)invoke(osCls, "GetIndMenuItemWithCommandID", new Class[]{int.class, int.class, int.class, int[].class, short[].class}, 
        0, kHICommandPreferences, 1, outMenu, outIndex);
    int osNoErr = (Integer)fieldValue(osCls, "noErr");
//    if (OS.GetIndMenuItemWithCommandID(0, kHICommandPreferences, 1, outMenu, outIndex) == OS.noErr && outMenu[0] != 0)
    if (indMenuItem == osNoErr && outMenu[0] != 0)
    {
      int menu = outMenu[0];

      int l = fgAboutActionName.length();
      char buffer[] = new char[l];
      fgAboutActionName.getChars(0, l, buffer, 0);
      int str = (Integer)invoke(osCls, "CFStringCreateWithCharacters", new Class[]{int.class, char[].class, int.class}, 
          fieldValue(osCls, "kCFAllocatorDefault"), buffer, l);
      
//      OS.InsertMenuItemTextWithCFString(menu, str, (short) 0, 0, kHICommandAbout);
      invoke(osCls, "InsertMenuItemTextWithCFString", new Class[]{int.class, int.class, short.class, int.class, int.class}, 
          menu, str, (short) 0, 0, kHICommandAbout);
//      OS.CFRelease(str);
      invoke(osCls, "CFRelease", new Class[]{int.class}, str);
      // add separator between About & Preferences
//      OS.InsertMenuItemTextWithCFString(menu, 0, (short) 1, OS.kMenuItemAttrSeparator, 0);
      invoke(osCls, "InsertMenuItemTextWithCFString", new Class[]{int.class, int.class, short.class, int.class, int.class}, 
          menu, 0, (short) 1, fieldValue(osCls, "kMenuItemAttrSeparator"), 0);
      

      // enable pref menu
//      OS.EnableMenuCommand(menu, kHICommandPreferences);
      invoke(osCls, "EnableMenuCommand", new Class[]{int.class, int.class}, menu, kHICommandPreferences);
      // disable services menu
//      OS.DisableMenuCommand(menu, kHICommandServices);
      invoke(osCls, "DisableMenuCommand", new Class[]{int.class, int.class}, menu, kHICommandServices);
      
    }
    
    // hook up quit listener
    if (!display.isDisposed())
    {
      display.addListener(SWT.Close, quitListener);      
    }

    // schedule disposal of callback object
    display.disposeExec(new Runnable() {
      public void run()
      {
        invoke(commandCallback, "dispose");
      }
    });
  }

  /**
   * Locate and run an action in the current menubar by name.
   */
  private int runAction(IAction action)
  {
    try {
      action.run();
      return (Integer)fieldValue(osCls, "noErr");
    } catch (Exception e) {
      System.out.println("Error while executing Apple menu action: "+e.getMessage());
    }
    return (Integer) fieldValue(osCls, "eventNotHandledErr");
  }
  
  
  private Class classForName(String classname)
  {
    try
    {
      Class cls = Class.forName(classname);
      return cls;
    } catch (ClassNotFoundException e)
    {
      throw new IllegalStateException(e);
    }
  }
  
  private Object newInstance(String classname)
  {
    try
    {
      Class cls = classForName(classname);
      return cls.newInstance();
    } catch (Exception e)
    {
      throw new IllegalStateException(e);
    }
  }
  
  private Object newInstance(String classname, Class[] paramTypes, Object... arguments)  
  {
    try
    {
      Class cls = classForName(classname);
      Constructor ctor = cls.getConstructor(paramTypes);
      return ctor.newInstance(arguments);
    } catch (Exception e)
    {
      throw new IllegalStateException(e);
    }
  }
  
  private Object invoke(Class cls, String methodName)
  {
    return invoke(cls, methodName, null, (Object[])null);
  }
    
  private Object invoke(Class cls, String methodName, Class[] paramTypes, Object... arguments)
  {
    try {      
      Method m = cls.getDeclaredMethod(methodName, paramTypes);
      return m.invoke(null, arguments);
    } catch (Exception e)
    {
      throw new IllegalStateException(e);
    }
  }
    
  private Object invoke(Object obj, String methodName)
  {
    return invoke(obj, methodName, (Class[]) null, (Object[]) null);
  }
  
  private Object invoke(Object obj, String methodName, Class[] paramTypes, Object... arguments)
  {  
    try {
      Method m = obj.getClass().getDeclaredMethod(methodName, paramTypes);
      return m.invoke(obj, arguments);
    } catch (Exception e)
    {
      throw new IllegalStateException(e);
    }
  }
  
  private Object fieldValue(Class cls, String fieldName)
  {
    try
    {
      Field field = cls.getDeclaredField(fieldName);
      return field.get(null);
    } catch (Exception e)
    {
      throw new IllegalStateException(e);
    }
  }
  
  private Object fieldValue(Object obj, String fieldName)
  {
    try
    {
      Field field = obj.getClass().getDeclaredField(fieldName);
      return field.get(obj);
    } catch (Exception e)
    {
      throw new IllegalStateException(e);
    }
  }
  
}
