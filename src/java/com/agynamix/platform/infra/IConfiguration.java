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
package com.agynamix.platform.infra;

import java.util.List;
import java.util.Properties;

import org.eclipse.swt.graphics.Rectangle;



/**
 * Configuration encapsulates services for configuring the application.
 * It should be implementation independent.
 */
public interface IConfiguration {
  
  String CONFIG_FILE = "config.properties";
  
  String  getProperty(String key);
  int     getInteger(String key);
  long    getLong(String key);
  boolean getBoolean(String key);
  byte[]  getByteArray(String key);

  List<String> getProperyList(String permanentNetworkAddresses);
  
  void    setProperty(String key, String value);
  void    setInteger(String key, int value);  
  void    setLong(String key, long value);  
  void    setBoolean(String key, boolean value);
  void    setByteArray(String key, byte[] value);

  void setRectangle(String key, Rectangle rectangle);
  Rectangle getRectangle(String key);

  /**
   * Convenience method that checks the value type and calls the appropriate set function.
   * @param key the key of the property
   * @param value its value object
   */
  void    setProperty(String key, Object value);

  /**
   * Save the configuration.
   *
   */
  void    save();
  
  /**
   * 
   * @return Exports the properties.
   */
  Properties exportProperties();
  Properties exportDefaultProperties();
  
}

