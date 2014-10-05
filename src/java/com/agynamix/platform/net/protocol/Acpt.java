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

import java.util.UUID;

public class Acpt extends NodeCommand {

  final UUID nodeId;

  public Acpt(UUID nodeId)
  {
    super(ICommands.ACPT);
    this.nodeId = nodeId;
  }
  
  public Acpt(byte[] buffer, int offset, int packetSize)
  {
    super(ICommands.ACPT);
    nodeId = UUID.fromString(decodeField(buffer, offset, 0));
  }

  public UUID getNodeId()
  {
    return this.nodeId;
  }

  @Override
  public byte[] toByteArray()
  {
    return toByteArray(encodeField(nodeId.toString()));
  }


}
