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

/**
 * Covers exceptions occuring in and around plugins.
 * 
 * @version $Revision: 32 $ $Date: 2004-11-28 18:23:55 +0100 (So, 28 Nov 2004) $
 * @author tuhlmann
 */
public class PluginException extends Exception {

  private static final long serialVersionUID = 1L;

  public PluginException(String msg, Throwable cause)
  {
    super(msg, cause);
  }

  public PluginException(String msg)
  {
    super(msg);
  }

}

// $Log$