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
package com.agynamix.platform.net.protocol;


public class Expt extends SerializedDataCommand {

  public Expt(Exception exception)
  {
    super(ICommands.EXPT, exception);
  }
  
  public Expt(byte[] buffer, int offset, int packetSize)
  {
    super(ICommands.EXPT, buffer, offset, packetSize);
  }  
  
  public Exception getException()
  {
    return (Exception) getObject();
  }
  

}
