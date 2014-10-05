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

public class Auth extends NodeCommand {

  final String groupname;
  final String password;
  
  public Auth(String groupName, String password)
  {
    super(ICommands.AUTH);
    this.groupname = groupName;
    this.password  = password;
  }

  public Auth(byte[] buffer, int offset, int packetSize)
  {
    super(ICommands.AUTH);
    String protocolVersion = decodeField(buffer, offset, 0);
    if (!ICommands.PROTOCOL_VERSION.equals(protocolVersion))
    {
      throw new FatalNetworkException("received protocol version is not compatible. Ours: "
          +ICommands.PROTOCOL_VERSION+", received: "+protocolVersion);
    }
    groupname = decodeField(buffer, offset, 1);
    password  = decodeField(buffer, offset, 2);
  }

  @Override
  public byte[] toByteArray()
  {
    return toByteArray(encodeField(ICommands.PROTOCOL_VERSION), encodeField(groupname), encodeField(password));
  }

  public String getGroupname()
  {
    return groupname;
  }

  public String getPassword()
  {
    return password;
  }
  
  

}
