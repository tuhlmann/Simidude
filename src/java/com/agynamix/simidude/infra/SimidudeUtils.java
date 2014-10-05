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
package com.agynamix.simidude.infra;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.program.Program;

import com.agynamix.platform.frontend.preferences.IPreferenceConstants;
import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.platform.infra.PlatformUtils;
import com.agynamix.simidude.source.impl.FileSourceData;
import com.agynamix.simidude.source.impl.TextSourceData;

public class SimidudeUtils {
  
  static int currentModifierKey = 0;
  
  static List<Program> mimePrograms = null;
  
  public static void launchDefaultImageEditor(File f)
  {
    findAndlaunchApplication(IPreferenceConstants.DEFAULT_IMAGE_EDITOR, f.getAbsolutePath());
  }
  
  public static void launchDefaultTextEditor(File f)
  {
    findAndlaunchApplication(IPreferenceConstants.DEFAULT_TEXT_EDITOR, f.getAbsolutePath());
  }
  
  public static void launchDefaultFileBowser(FileSourceData fileSourceData)
  {    
    findAndlaunchApplication(IPreferenceConstants.DEFAULT_FILE_BROWSER, fileSourceData.getLocalFilename());
  }

  
  public static void findAndlaunchApplication(String appTypePrefName, String arg)
  {
    String defaultApplication = ApplicationBase.getContext().getConfiguration().getProperty(appTypePrefName);
    if (isEmpty(defaultApplication))
    {
//      System.out.println("Execute Standard Application");
      launchStandardApplication(arg);   
    } else {
      File app = new File(defaultApplication);
      if (!app.exists())
      {
        Program program = findProgram(defaultApplication);
        if (program != null)
        {
//          System.out.println("Launch Found Program "+program.getName());
          program.execute(arg);
        } else {
//          System.out.println("No program found, launch standard app");
          launchStandardApplication(arg);
        }
      } else {
//        System.out.println("launch app: "+app);
        launchApplication(app, arg);
      }
    }
  }
  
  public static void launchApplication(File app, String args)
  {
    try {
      String[] cmdArgs = null;
      if (PlatformUtils.isMacOs())
      {
        cmdArgs = new String[] {"open", "-a", app.getAbsolutePath(), args};
      } else {
        cmdArgs = new String[] {app.getAbsolutePath(), args};
      }
      Runtime.getRuntime().exec(cmdArgs);
    } catch (Exception err) {
      err.printStackTrace();
    }   
  }

  public static List<Program> getMimePrograms()
  {
    if (mimePrograms == null)
    {
      mimePrograms = new ArrayList<Program>();
      for (Program p : Program.getPrograms())
      {
        mimePrograms.add(p);
      }
    }
    return mimePrograms;
  }
  
  public static Program findProgram(String appName)
  {
    for (Program p : getMimePrograms())
    {
      if (p.getName().equalsIgnoreCase(appName))
      {
        return p;
      }
    }
    return null;
  }
  
  public static void launchURI(TextSourceData textSourceData)
  {
    Program.launch(textSourceData.getText());
  }

  public static void launchStandardApplication(String arg)
  {
    Program.launch(arg);
  }

  public static synchronized void keyPressed(int keyCode)
  {
    if (isSupportedModifierKey(keyCode))
    {
      currentModifierKey |= keyCode;
//      System.out.println("currentModifierKey (pressed)= "+currentModifierKey);
    }
  }

  public static synchronized void keyReleased(int keyCode)
  {
    if (isSupportedModifierKey(keyCode))
    {
      currentModifierKey = currentModifierKey ^ keyCode;
//      System.out.println("currentModifierKey (released)= "+currentModifierKey);
    }
  }
  
  public static synchronized boolean isModifierKeyPressed()
  {
    int modifierKey = ApplicationBase.getContext().getConfiguration().getInteger(IPreferenceConstants.MODIFIER_KEY);
//    System.out.println("Modifier: "+modifierKey);
    boolean isPressed = (currentModifierKey & modifierKey) != 0;
//    System.out.println("Pressed: "+isPressed);
    return isPressed;
  }
  
  public static boolean isSupportedModifierKey(int keyCode)
  {
    return ((keyCode == SWT.SHIFT) || (keyCode == SWT.COMMAND) || (keyCode == SWT.CONTROL) || (keyCode == SWT.ALT));
  }
  
  public static String getKeyCodeName(int keyCode)
  {
    String name = "unknown";
    if (keyCode == SWT.SHIFT)
    {
      name = "Shift";
    } else if (keyCode == SWT.COMMAND)
    {
      if (PlatformUtils.isMacOs())
      {
        name = "Apple";
      } else {
        name = "Win";
      }
    } else if (keyCode == SWT.CONTROL)
    {
      name = "Ctrl";
    } else if (keyCode == SWT.ALT)
    {
      name = "Alt";
    }
    return name;
  }
  
  public static boolean isEmpty(String string)
  {
    return ((string == null) || (string.length() == 0));
  }
  
}
