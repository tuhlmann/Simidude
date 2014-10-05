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
package com.agynamix.platform.net;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.Date;
import java.util.UUID;

/**
 * ClientNode encapsulates the information a node stores about other nodes.
 * This information is used to connect to the described node and send data.
 * @author tuhlmann
 *
 */
public class ClientNode implements Serializable {
  
  private static final long serialVersionUID = 1L;

  private volatile int hashCode;

  final UUID        nodeId;
  final String      groupname;
  final InetAddress address;
  final int         port;
  final Date        creationDate;
  
  Date inactiveSince = null;
  Date shutdownDate = null;
  
  public ClientNode(UUID nodeId, String groupname, InetAddress nodeAddress, int nodePort)
  {
    this.nodeId    = nodeId;
    this.groupname = groupname;
    this.address   = nodeAddress;
    this.port      = nodePort;
    this.creationDate = new Date();
  }
  
  public InetAddress getAddress()
  {
    return this.address;
  }

  public int getPort()
  {
    return this.port;
  }
  
  @Override
  public String toString()
  {
    return address.getHostAddress()+":"+port+"/"+nodeId+"@"+groupname;
  }

  public UUID getNodeId()
  {
    return nodeId;
  }
  
  public String getGroupname()
  {
    return groupname;
  }
  
  @Override
  public boolean equals(Object o)
  {
    if (o == this)
    {
      return true;
    }
    if (!(o instanceof ClientNode))
    {
      return false;
    }
    ClientNode node = (ClientNode) o;
    return this.nodeId.equals(node.nodeId) && this.groupname.equals(node.groupname);
//           && this.address.equals(node.address) && this.port == node.port;
  }
  
  @Override
  public int hashCode()
  {
    int result = hashCode;
    if (result == 0)
    {
      result = 17;
      result = 31 * result + nodeId.hashCode();
      result = 31 * result + groupname.hashCode(); 
//      result = 31 * result + address.hashCode();
//      result = 31 * result + port;
      hashCode = result;
    }
    return result;
  }

  public void setNodeInactive()
  {
    if (inactiveSince == null)
    {
      inactiveSince = new Date();
    }
  }
  
  public void setNodeActive()
  {
    inactiveSince = null;
  }
  
  public boolean isNodeActive()
  {
    return inactiveSince == null;
  }

  public Date getInactiveSince()
  {
    return inactiveSince;
  }
  
  public void shutdown()
  {
    shutdownDate = new Date();
  }
  
  public Date getShutdownDate()
  {
    return this.shutdownDate;
  }
  
  public Date getCreationDate()
  {
    return this.creationDate;
  }
  
  public boolean isShutdown()
  {
    return shutdownDate != null;
  }
  

}
