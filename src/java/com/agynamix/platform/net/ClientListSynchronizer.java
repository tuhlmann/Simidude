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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agynamix.platform.concurrent.ThreadManagerAware;
import com.agynamix.platform.log.ApplicationLog;

public class ClientListSynchronizer implements ThreadManagerAware {

  Logger log = ApplicationLog.getLogger(ClientListSynchronizer.class);

  final IConnector connector;
  
  boolean shouldStop = false;
  
  public ClientListSynchronizer(IConnector connector)
  {
    this.connector = connector;
  }

  /**
   * This Service is run at scheduled intervalls.
   * It will contact all connected clients, get their client lists, sync them with ours and
   * send the delta back to the peer.
   */
  public void run()
  {
    if (!shouldStop)
    {
      List<ClientNode> clients = connector.getConnectedClientList();
      ConnectionUtils.clearInactiveFromClientList(connector, clients);
      if (clients.size() > 1) // we are in there as well
      {
        for (ClientNode client : clients)
        {
          if ((!client.equals(connector.getMyOwnNode())) && (!client.isShutdown())) // its not us
          {
            ConnectionCtx connectionCtx = null;
            try {
              connectionCtx = ConnectionUtils.connectTo(connector, client);
              List<ClientNode> peerClientList = getPeerClientList(connectionCtx);
              List<ClientNode> clientsOnlyWeHaveList = synchronizeClientLists(connector, peerClientList);
              sendDeltaClientList(connectionCtx, clientsOnlyWeHaveList);
              connectionCtx.disconnect();
            } catch (IOException e)
            {
              log.info("Communication with "+client.getAddress().getHostAddress()+" not possible");
            } catch (NetworkAuthException e)
            {
              log.log(Level.WARNING, e.getMessage(), e);
            } catch (NetworkProtocolException e)
            {
              log.log(Level.WARNING, e.getMessage(), e);
            }
          }
        }
      }
    }
  }
  
  private List<ClientNode> getPeerClientList(ConnectionCtx connectionCtx) throws IOException, NetworkProtocolException
  {    
    return (List<ClientNode>) connectionCtx.invoke(new TransportClientListCommand());
  }

  private List<ClientNode> synchronizeClientLists(IConnector connector, List<ClientNode> peerClientList)
  {
    List<ClientNode> clientsOnlyWeHave = new ArrayList<ClientNode>();
    List<ClientNode> ourClientList = connector.getConnectedClientList();
    for (ClientNode node : ourClientList)
    {
      if (!peerClientList.contains(node))
      {
        clientsOnlyWeHave.add(node);
      }
    }
    for (ClientNode node : peerClientList)
    {
      if (!ourClientList.contains(node))
      {
        ourClientList.add(node);
      }
    }
    return clientsOnlyWeHave;
  }
  

  private void sendDeltaClientList(ConnectionCtx connectionCtx, List<ClientNode> deltaList) throws IOException, NetworkProtocolException
  {
    if ((deltaList != null) && (deltaList.size() > 0))
    {
      connectionCtx.invoke(new TransportClientListCommand(deltaList));          
    }
  }

  public String getId()
  {
    return "ClientListSynchronizer";
  }

  public void shutdown()
  {
    this.shouldStop = true;
  }

}
