/*
 * Class LogFactory
 * Created on 07.01.2005
 * 
 * $Id: GuardianLog.java 265 2005-11-07 18:41:27Z tuhlmann $
 * $LastChangedBy: tuhlmann $
 * $LastChangedDate: 2005-11-07 19:41:27 +0100 (Mo, 07 Nov 2005) $
 * $LastChangedRevision: 265 $
 * 
 * This file is copyrighted by AGYNAMIX Design.
 * Please refer to the license of our products for details.
 */
package com.agynamix.platform.log;

import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.agynamix.platform.infra.PlatformUtils;

/**
 * @author tuhlmann
 * @since
 */
public class ApplicationLog {

  static String logDirectory;

  static
  {
    initializeLogger();
  }

  public static Logger getLogger(Class<?> cls)
  {
    return getLogger(cls.getName());
  }

  public static Logger getLogger(String str)
  {
    return Logger.getLogger(str);
  }

  static void initializeLogger()
  {
    logDirectory = PlatformUtils.getApplicationDataDir();
    System.setProperty("log.dir", logDirectory);
    try
    {
      InputStream ins = ApplicationLog.class.getResourceAsStream("/logging.properties");
      if (ins != null)
      {
        LogManager.getLogManager().readConfiguration(ins);
      } else {
        System.out.println("Could not find logging.properties");
      }
    } catch (Exception e)
    {
      System.out.println("Could not read logging.properties file");
      e.printStackTrace();
    }
  }

}
