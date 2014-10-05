/*
 * Copyright (c) 2004 agynamiX.com. All rights reserved. 
 * This file is made available under the terms of the
 * license this product is released under.
 * 
 * For details please see the license file you should have
 * received, or go to:
 * 
 * http://www.agynamix.com
 * 
 * Contributors: agynamiX.com (http://www.agynamix.com)
 */
package com.agynamix.platform.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.agynamix.platform.infra.ApplicationInfo;
import com.agynamix.platform.infra.PlatformUtils;

/**
 * static utility functions that help configuring the system
 * 
 * @version $Revision: 27 $ $Date: 2004-11-27 22:42:44 +0100 (Sa, 27 Nov 2004) $
 * @author tuhlmann
 * @since V0404
 */
public class ConfigurationUtil {

  static final String CFG_FILE_EXT = "cfg.xml";

  static String       configFile   = null;

  /**
   * @return a platform dependant directory where user preference data is stored.
   * This will be:
   * ~/.Simidude on Unix
   * ~/Library/Preferences/Simidude on Mac
   * ~/.Simidude on Windows (needs fixing!).
   */
  public static String getConfigDir()
  {
    String commonPath = System.getProperty("user.home") + File.separator;
    String platformPath = "." + ApplicationInfo.getApplicationName() + File.separator;
    if (PlatformUtils.isMacOs())
    {
      platformPath = "Library" + File.separator + "Preferences" + File.separator 
      + ApplicationInfo.getApplicationName() + File.separator;
    }
    return commonPath + platformPath;
  }

  public static void main(String[] args)
  {
    System.out.println(ConfigurationUtil.getConfigDir());
  }

  public static String getConfigFile()
  {
    if (configFile == null)
    {
      String file = ApplicationInfo.getApplicationName();
      configFile = getConfigDir() + file + CFG_FILE_EXT;

      File cfg = new File(configFile);
      if (!cfg.exists())
      {
        // copy template over to the file location
        File tpl = new File("etc/" + file + CFG_FILE_EXT);
        if (tpl.exists())
        {
          copyFile(tpl, cfg);
        }
      }
    }
    return configFile;
  }

  private static void copyFile(File src, File dst)
  {
    dst.getParentFile().mkdirs();
    try {
    InputStream in = new FileInputStream(src);
    OutputStream out = new FileOutputStream(dst);

    // Transfer bytes from in to out
    byte[] buf = new byte[1024];
    int len;
    while ((len = in.read(buf)) > 0)
    {
      out.write(buf, 0, len);
    }
    in.close();
    out.close();
    } catch (IOException e)
    {
      System.out.println("Could not create config file "+dst.getAbsolutePath());
    }
  }

}
