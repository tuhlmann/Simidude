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

import com.agynamix.platform.net.ConnectionUtils;
import com.agynamix.platform.net.NetUtils;

public abstract class NodeCommand {

  public final String command;
  
  public NodeCommand(String command)
  {
    this.command = command;
  }
  
  public String getCommand()
  {
    return command;
  }
  
  /**
   * 
   * @return the payload as a byte array suitable for transport over the wire.
   */
  public byte[] toByteArray()
  {
    return null;
  }

  protected byte[] toByteArray(String... fields)
  {
    return ConnectionUtils.toByteArray(fields);
  }

  /**
   * Creates a valid packet to send the command over the wire.
   * It will write the command then it will write the size of the actual data to send and will
   * then delegate to the sub class to write the actual payload.
   * @return a valid packet to send over the wire. 
   */
  public byte[] toPacket()
  {
    int packetSize = 0;
    byte[] payload = toByteArray();
    if (payload != null)
    {
      packetSize = payload.length;
    }
    byte[] buffer = new byte[packetSize + ICommands.PACKET_HEADER_SIZE];
    System.arraycopy(getCommand().getBytes(), 0, buffer, 0, ICommands.COMMAND_SIZE);
    System.arraycopy(NetUtils.intToByteArray(packetSize), 0, buffer, ICommands.COMMAND_SIZE, ICommands.PAYLOAD_COUNTER_SIZE);
    if (packetSize > 0)
    {
      System.arraycopy(toByteArray(), 0, buffer, ICommands.PACKET_HEADER_SIZE, packetSize);
    }
    return buffer;
  }
  
  protected String decodeField(byte[] buffer, int offset, int pos)
  {
    return NetUtils.decodeField(buffer, offset, pos);
  }
  
  protected String encodeField(String value)
  {
    return NetUtils.encodeField(value);
  }

  protected String decodeField(String value)
  {
    return NetUtils.decodeField(value);
  }
  
  
}
