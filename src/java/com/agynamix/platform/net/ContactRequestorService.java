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
import java.util.List;
import java.util.logging.Logger;

import com.agynamix.platform.concurrent.ThreadManagerAware;
import com.agynamix.platform.log.ApplicationLog;

public class ContactRequestorService implements ThreadManagerAware {

  Logger log = ApplicationLog.getLogger(ContactRequestorService.class);
  
  boolean shouldStop = false;

  final IConnector connector;
  
  public ContactRequestorService(IConnector connector)
  {
    this.connector = connector;
  }

  public void run()
  {
    while (!shouldStop)
    {
      try
      {
        ClientNode requestor = connector.getRequestorQueue().take();
        if (requestor != null)
        {
          log.fine("Connecting with client on this address: "+requestor);
          List<ClientNode> clientList = connector.getConnectedClientList();
          ConnectionUtils.clearInactiveFromClientList(connector, clientList);
          ConnectionUtils.safeAddToClientList(requestor, clientList);
          ConnectionUtils.setConnectionStatus(connector.getConnectedClientList());
          sendConnectedClientList(requestor, clientList);
        }
      } catch (InterruptedException e)
      {
        // Interruption occurs when the connector is shutting down.
      }
    }
  }
  
  public synchronized void sendConnectedClientList(ClientNode requestor, List<ClientNode> connectedClientList)
  {
    ConnectionCtx connectionCtx = null;
    try {
      connectionCtx = ConnectionUtils.connectTo(connector, requestor);
      connectionCtx.invoke(new TransportClientListCommand(connectedClientList));          
      connectionCtx.disconnect();
    } catch (NetworkAuthException e)
    {
      e.printStackTrace();
    } catch (IOException e)
    {
      e.printStackTrace();
    } catch (NetworkProtocolException e)
    {
      e.printStackTrace();
    } finally {
      if (connectionCtx != null)
      {
        connectionCtx.close();
      }
    }
  }

  public String getId()
  {
    return "ContactRequestorService";
  }
  
  public void shutdown()
  {
    this.shouldStop = true;
  }
  
}
