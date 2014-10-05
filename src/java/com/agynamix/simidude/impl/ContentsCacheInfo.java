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
package com.agynamix.simidude.impl;

import java.io.File;
import java.util.Date;

import com.agynamix.simidude.infra.IContentsCacheInfo;

public class ContentsCacheInfo implements IContentsCacheInfo {

  private static final long serialVersionUID = 1L;

  private final Date creationDate;
  private final File itsFile;
  
  public ContentsCacheInfo(Date creationDate, File file)
  {
    if ((file == null) || (creationDate == null))
    {
      throw new NullPointerException();
    }
    this.creationDate = creationDate;
    this.itsFile = file;
  }

  public String getFilenameInCache()
  {
    return itsFile.getAbsolutePath();
  }

  public Date getCreationDate()
  {
    return creationDate;
  }

  public boolean isDirectory()
  {
    return itsFile.isDirectory();
  }
  

}
