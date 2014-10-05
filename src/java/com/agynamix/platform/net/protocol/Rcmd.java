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

import com.agynamix.platform.net.IRemoteCommand;

public class Rcmd extends SerializedDataCommand {

  public Rcmd(IRemoteCommand command)
  {
    super(ICommands.RCMD, command);
  }

  public Rcmd(byte[] buffer, int offset, int packetSize)
  {
    super(ICommands.RCMD, buffer, offset, packetSize);
  }

  public IRemoteCommand getRemoteCommand()
  {
    return (IRemoteCommand) this.object;
  }

}
