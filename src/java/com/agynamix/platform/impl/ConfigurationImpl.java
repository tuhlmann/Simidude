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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.eclipse.swt.graphics.Rectangle;

import com.agynamix.platform.infra.IConfiguration;
import com.agynamix.platform.log.ApplicationLog;

public class ConfigurationImpl implements IConfiguration {

  ConfigurationImpl    configuration;
        Preferences    userPrefs;
  final Properties     defaultValues = new Properties();
  
  Logger log = ApplicationLog.getLogger(ConfigurationImpl.class);

  public ConfigurationImpl getInstance()
  {
    if (configuration == null)
    {
      configuration = new ConfigurationImpl();
    }
    return configuration;
  }

  public ConfigurationImpl()
  {
    String path = classname2path(IConfiguration.class.getName());
    log.fine("Check Path "+path);
    try
    {
      InputStream ins = this.getClass().getClassLoader().getResourceAsStream(IConfiguration.CONFIG_FILE);
      if (ins != null)
      {
        defaultValues.load(ins);
        ins.close();
        userPrefs = Preferences.userNodeForPackage(IConfiguration.class);
        for (Object key : defaultValues.keySet()) 
        {
          if (userPrefs.get((String)key, null) == null)
          {          
            String value = defaultValues.getProperty((String)key);
            log.fine("Insert default value into Preferences for key "+key+": "+value);
            userPrefs.put((String)key, value);
          }
        }
      }
    } catch (IOException e)
    {
      e.printStackTrace();
    }
    userPrefs = Preferences.userNodeForPackage(IConfiguration.class);
  }

  private static String classname2path(String classname)
  {
    int pos = classname.lastIndexOf(".");
    String part = classname.substring(0, pos);
    part = part.replace('.', '/');
    return '/' + part;
  }

  public void save()
  {
    // Nothing to do here.
  }
  
  public Properties exportProperties()
  {
    Properties props = new Properties();
    try
    {
      String[] keys = userPrefs.keys();
      for (String key : keys)
      {
        String value = userPrefs.get(key, null);
        props.setProperty(key, value);
      }    
    } catch (BackingStoreException e)
    {
      e.printStackTrace();
    }
    return props;
  }
  
  public Properties exportDefaultProperties()
  {
    Properties p = new Properties();
    for (Object key : defaultValues.keySet())
    {      
      p.setProperty((String) key, defaultValues.getProperty((String) key));
    }
    return p;
  }

  public String getProperty(String key)
  {
    return userPrefs.get(key, getStringDefault(key));
  }
  
  public List<String> getProperyList(String key)
  {
    String value = userPrefs.get(key, getStringDefault(key));
    if ((value != null) && (value.length() > 0))
    {
      StringTokenizer st = new StringTokenizer(value, ",");
      List<String> v = new ArrayList<String>();
      while (st.hasMoreTokens())
      {
        v.add(st.nextToken());
      }
      return v;
    }
    return new ArrayList<String>();
  }

  public int getInteger(String key)
  {
    return userPrefs.getInt(key, getIntDefault(key));
  }

  public void setInteger(String key, int value)
  {
    userPrefs.putInt(key, value);
  }
  
  public byte[] getByteArray(String key)
  {
    return userPrefs.getByteArray(key, getByteArrayDefault(key));
  }
  
  public long getLong(String key)
  {
    return userPrefs.getLong(key, getLongDefault(key));
  }
  
  public Rectangle getRectangle(String key)
  {
    String value = userPrefs.get(key, getStringDefault(key));
    if (value != null)
    {
      StringTokenizer st = new StringTokenizer(value, "-");
      int[] v = new int[4];
      int i = 0;
      try {
        while (st.hasMoreTokens())
        {
          String s = st.nextToken();
          v[i] = Integer.parseInt(s);
          i++;
        }
      } catch (Exception e)
      {
        log.log(Level.WARNING, "Rectangle property could not be read: "+e.getMessage(), e);
        return null;
      }
      return new Rectangle(v[0], v[1], v[2], v[3]);
    }
    return null;
  }
  
  public void setByteArray(String key, byte[] value)
  {
    userPrefs.putByteArray(key, value);
  }
  
  public void setLong(String key, long value)
  {
    userPrefs.putLong(key, value);
  }

  public boolean getBoolean(String key)
  {
    return userPrefs.getBoolean(key, getBooleanDefault(key));
  }

  public void setBoolean(String key, boolean value)
  {
    userPrefs.putBoolean(key, value);
  }

  public void setProperty(String key, String value)
  {
    userPrefs.put(key, value);
  }
  
  public void setRectangle(String key, Rectangle rectangle)
  {
    StringBuilder sb = new StringBuilder();
    sb.append(rectangle.x).append("-").append(rectangle.y).append("-").append(rectangle.width).append("-").append(rectangle.height);
    userPrefs.put(key, sb.toString());
  }
  
  public void setProperty(String key, Object value)
  {
    if (value instanceof Boolean)
    {
      setBoolean(key, ((Boolean) value));
    } else if (value instanceof Integer)
    {
      setInteger(key, ((Integer) value));
    } else if (value instanceof Long)
    {
      setLong(key, ((Long) value));
    } else if (value instanceof Rectangle)
    {
      setRectangle(key, (Rectangle)value);
    } else {
      if (value != null)
      {
        setProperty(key, value.toString());
      }
    }
  }

  public String getStringDefault(String key)
  {
    return defaultValues.getProperty(key);
  }

  public byte[] getByteArrayDefault(String key)
  {
    String s = getStringDefault(key);
    return s != null ? s.getBytes() : null;
  }

  public int getIntDefault(String key)
  {
    return (int) getLongDefault(key);
  }
  
  public long getLongDefault(String key)
  {
    String s = getStringDefault(key);
    try
    {
      return s != null ? Long.parseLong(s): 0;
    } catch (Exception e)
    {
      e.printStackTrace();
      return 0;
    }
  }

  public boolean getBooleanDefault(String key)
  {
    String s = getStringDefault(key);
    if (s != null)
    {
      return Boolean.parseBoolean(s);
    }
    return false;
  }

  
}
