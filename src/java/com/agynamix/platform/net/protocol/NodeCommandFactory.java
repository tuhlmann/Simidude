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

import com.agynamix.platform.net.FatalNetworkException;
import com.agynamix.platform.net.NetUtils;

public class NodeCommandFactory {

  /**
   * Takes apart the received string and creates a NodeCommand of it.
   * @param buffer
   * @return
   */
  public static NodeCommand toProtocol(byte[] buffer)
  {
    NodeCommand command = null;
    if (buffer != null)
    {
      String cmd = getCommand(buffer);
      int packetSize = getPacketSize(buffer);
      if (ICommands.AUTH.equals(cmd))
      {
        command = new Auth(buffer, ICommands.PACKET_HEADER_SIZE, packetSize);
      } else if (ICommands.ACPT.equals(cmd))
      {
        command = new Acpt(buffer, ICommands.PACKET_HEADER_SIZE, packetSize);
      } else if (ICommands.REJT.equals(cmd))
      {
        command = new Rejt();
      } else if (ICommands.ACKN.equals(cmd))
      {
        command = new Ackn();
      } else if (ICommands.QUIT.equals(cmd))
      {
        command = new Quit();
      } else if (ICommands.OBJT.equals(cmd))
      {
        command = new Objt(buffer, ICommands.PACKET_HEADER_SIZE, packetSize);
      } else if (ICommands.DATA.equals(cmd))
      {
        command = new Data(buffer, ICommands.PACKET_HEADER_SIZE, packetSize);
      } else if (ICommands.RCMD.equals(cmd))
      {
        command = new Rcmd(buffer, ICommands.PACKET_HEADER_SIZE, packetSize);
      } else if (ICommands.EXPT.equals(cmd))
      {
        command = new Expt(buffer, ICommands.PACKET_HEADER_SIZE, packetSize);
      } else {
        throw new FatalNetworkException("Unknown Command: "+cmd);
      }
    }     
    return command;
  }

  private static int getPacketSize(byte[] buffer)
  {
    return NetUtils.byteArrayToInt(buffer, ICommands.COMMAND_SIZE);
  }

  /**
   * 
   * @return The command is always the first 4 bytes of a line
   */
  public static String getCommand(byte[] buffer)
  {
    return new String(buffer, 0, ICommands.COMMAND_SIZE);
  }

}
