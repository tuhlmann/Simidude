package com.agynamix.platform.frontend.gui;

import java.io.File;
import java.util.StringTokenizer;

import com.agynamix.platform.infra.FileUtils;
import com.agynamix.platform.infra.Tupel;

public abstract class AbstractHotkeyRegistrar implements IHotkeyRegistrar {

  public void activateGlobalPaste(HotkeyListenerInfo listenerInfo)
  {
    // TODO Auto-generated method stub

  }

  public Tupel<Integer, Integer> parseHotkeyDefinition(String hotKeyStr)
  {
    if ((hotKeyStr == null) || (hotKeyStr.length() == 0))
    {
      return new Tupel<Integer, Integer>(0, 0);
    }
    
    StringTokenizer st = new StringTokenizer(hotKeyStr, "+");
    
    int modifier = 0;
    int keyCode  = 0;
    
    while (st.hasMoreTokens())
    {
      String token = st.nextToken();
      if (!st.hasMoreTokens())
      {
        keyCode = tokenToKeyCode(token);
      } else {
        modifier |= tokenToModifier(token); 
      }
    }
    
    return new Tupel<Integer, Integer>(modifier, keyCode);
  }

  protected abstract int tokenToModifier(String token);
  
  protected int tokenToKeyCode(String token)
  {
    return (int)token.charAt(0);
  }

  /**
   * Loads native library located inside some jar in classpath
   * 
   * @param jarLib library name
   * @return success?
   */
  protected boolean loadJarLibrary(final String jarLib) 
  {
    final String tempLib = System.getProperty("java.io.tmpdir") 
            + File.separator + jarLib;
    boolean copied = FileUtils.copyFile(jarLib, tempLib);
    if (!copied) 
    {
      return false;
    }
    try {
      System.load(tempLib);
    } catch (Throwable e)
    {
      return false;
    }
    return true;
}


  


}
