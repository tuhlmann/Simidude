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
package com.agynamix.simidude.remote;

import java.io.File;

public class ContentsLoaderException extends Exception {

  private static final long serialVersionUID = 1L;
  
  private final File file;
  
  public ContentsLoaderException(File file, Throwable cause)
  {
    super(cause);
    this.file = file;
  }
  
  public File getFile()
  {
    return this.file;
  }

  public String getFilename()
  {
    String fname = "unknown";
    if (file != null)
    {
      fname = file.getAbsolutePath();
    }
    return fname;
  }

}
