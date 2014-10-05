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



public class Data extends NodeCommand {

  final String filename;
  final long   length;
  
  public Data(String filename, long length)
  {
    super(ICommands.DATA);
    this.filename = filename;
    this.length = length;
  }

  public Data(byte[] buffer, int offset, int packetSize)
  {
    super(ICommands.DATA);
    filename = decodeField(buffer, offset, 0);
    length   = Long.parseLong(decodeField(buffer, offset, 1));
  }

  public String getFilename()
  {
    return filename;
  }
  
  public long getFileSize()
  {
    return length;
  }
  
  @Override
  public byte[] toByteArray()
  {
    return toByteArray(encodeField(filename), encodeField(""+length));
  }

}
