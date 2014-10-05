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

import java.util.ArrayList;
import java.util.List;

import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.platform.infra.PlatformUtils;
import com.agynamix.simidude.impl.SimidudeApplicationContext;
import com.agynamix.simidude.remote.RemoteConnector;

public class TransportClientListCommand extends AbstractRemoteCommand {

  private static final long serialVersionUID = 1L;

  final List<ClientNode> localClientList;

  public TransportClientListCommand()
  {
    this.localClientList = null;
  }
  
  public TransportClientListCommand(List<ClientNode> clientList)
  {
    this.localClientList = clientList;
  }

  /**
   * This command server two purposes:
   * <ul>
   *   <li>First it will transport a local client list (optional) to the peer.</li>
   *   <li>Secondly it will return the peers client list with the freshly sent nodes included.</li>
   * </ul>
   */
  public Object invoke(IConnectorServerHandler connectorServerHandler)
  {
    List<ClientNode> clientList = null;
    RemoteConnector remoteConnector = ((SimidudeApplicationContext)ApplicationBase.getContext()).getRemoteConnector();
    if (remoteConnector != null)
    {
      clientList = remoteConnector.getConnector().getConnectedClientList();
      if (localClientList != null)
      {
        ConnectionUtils.safeAddToClientList(localClientList, clientList);
        ConnectionUtils.setConnectionStatus(clientList);
      }
    } else {
      clientList = new ArrayList<ClientNode>();
    }
    return clientList;
  }

}
