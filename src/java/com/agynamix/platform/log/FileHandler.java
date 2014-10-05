/*
 * Class FileHandler
 * Created on 26.10.2005
 * 
 * This file is copyrighted by AGYNAMIX.
 * Please refer to the license of our products for details.
 */
package com.agynamix.platform.log;

import java.io.IOException;
import java.util.logging.LogManager;


public class FileHandler extends java.util.logging.FileHandler {
  
  public final static String VARIABLE_START = "${";
  public final static String VARIABLE_END   = "}";

  public FileHandler() throws IOException, SecurityException
  {
    super(configurePattern(), getLimit(), getCount());
  }
  
  private static int getLimit()
  {
    int limit = 0;
    String cname = FileHandler.class.getName();
    LogManager manager = LogManager.getLogManager();    
    String limitStr = manager.getProperty(cname + ".limit");
    if ((limitStr != null) && (limitStr.length() > 0))
    {
      try {
        limit = Integer.parseInt(limitStr);
      } catch (Exception e) {}
    }
    return limit;
  }

  private static int getCount()
  {
    int count = 1;
    String cname = FileHandler.class.getName();
    LogManager manager = LogManager.getLogManager();    
    String countStr = manager.getProperty(cname + ".count");
    if ((countStr != null) && (countStr.length() > 0))
    {
      try {
        count = Integer.parseInt(countStr);
      } catch (Exception e) {}
    }
    return count;
  }
  
  private static String configurePattern()
  {
    String cname = FileHandler.class.getName();
    LogManager manager = LogManager.getLogManager();    
    String pattern = manager.getProperty(cname + ".pattern");
    if ((pattern == null) || (pattern.length() == 0))
    {
      throw new IllegalStateException("Kein Pattern angegeben.");
    }
    
    return resolveVariables(pattern);
  }

  private static String resolveVariables(String pattern)
  {
    StringBuilder sb = new StringBuilder();
    int oldPos = 0;
    int pos1 = pattern.indexOf(VARIABLE_START);
    int pos2 = 0;
    while (pos1 > -1)
    {
      if (pos1 > oldPos)
      {
        sb.append(pattern.substring(oldPos, pos1));
      }
      pos2 = pattern.indexOf(VARIABLE_END, pos1);
      if (pos2 > -1)
      {
        String variable = pattern.substring(pos1+2, pos2);
        String substitute = System.getProperty(variable);
        if (substitute == null)
        {
          substitute = "["+variable+"]";
        }
        sb.append(substitute);
        oldPos = pos2+1;
      } else {
        oldPos = pos1;
      }
      pos1 = pattern.indexOf(VARIABLE_START, pos1+2);
    }
    sb.append(pattern.substring(oldPos));
//    System.out.println("[DEBUG] Pattern: "+sb);
    return sb.toString();
  }

}
