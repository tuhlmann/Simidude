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
package com.agynamix.simidude.source;

import java.io.Serializable;
import java.util.UUID;

public class SourceDataContents implements Serializable {

  private static final long serialVersionUID = 1L;
  
  /**
   * The UUID of the associated ISourceData instance.
   */
  final UUID                 sourceDataId;
  final String               name;
  final byte[]               fileContents;
  final boolean              isDirectory;
  
  final boolean              lastPackage;
        boolean              endOfFile = false;
        boolean              aborted = false;
        Throwable            exception = null;
  
  public SourceDataContents(UUID sourceDataId, String fileName, byte[] fileContents)
  {
    this.sourceDataId      = sourceDataId;
    this.name              = fileName;
    this.fileContents      = fileContents;
    this.isDirectory       = false;
    this.lastPackage       = false;
  }

  public SourceDataContents(UUID sourceDataId, String directoryName)
  {
    this.sourceDataId      = sourceDataId;
    this.name              = directoryName;
    this.isDirectory       = true;
    this.fileContents      = null;
    this.lastPackage       = false;
  }
  
  public SourceDataContents(UUID sourceDataId, boolean isLastPackage)
  {
    this.lastPackage       = isLastPackage;
    this.sourceDataId      = sourceDataId;
    this.name              = null;
    this.isDirectory       = false;
    this.fileContents      = null;
  }

  public String getName()
  {
    return name;
  }

  public byte[] getFileContents()
  {
    return fileContents;
  }

  public boolean isDirectory()
  {
    return this.isDirectory;
  }
  
  public boolean isFile()
  {
    return fileContents != null;
  }

  public boolean isLastPackage()
  {
    return lastPackage;
  }

  public boolean isEndOfFile()
  {
    return endOfFile;
  }

  public void setEndOfFile(boolean endOfFile)
  {
    this.endOfFile = endOfFile;
  }  
 
  public boolean isAborted()
  {
    return aborted;
  }

  public void setAborted(Throwable exception)
  {
    this.aborted = true;
    this.exception = exception;
  }  
  
  public Throwable getException() 
  {
    return this.exception;
  }
  
  
}
