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
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.agynamix.platform.infra.ExecutorUtils;
import com.agynamix.platform.log.ApplicationLog;
import com.agynamix.platform.net.protocol.NodeCommand;

/**
 * This is a testbed for a user friendly peer-to-peer connector.
 * How does the connector work?
 * The Connector will regularly advertise itself (broadcast) to the network. 
 * @author tuhlmann
 *
 */
public class Connector implements IConnector {

  protected final String groupName;
  protected final String groupPassword;
  protected final int    helloPort;
  protected final int    serverPort;
  private   final UUID   nodeId;
  
  private final BlockingQueue<ClientNode> requestorQueue = new LinkedBlockingQueue<ClientNode>();
  
  private PingService              pingService;
  private PingServiceListener      pingListener;
  private ClientListSynchronizer   clientListSynchronizer;
  private ConnectorServer          connectorServer;
  private ContactRequestorService  contactRequestorService;
  
  private       ClientNode         myOwnNode;
  
  private List<ClientNode>         connectedClients = new CopyOnWriteArrayList<ClientNode>();
  
  private List<IPacketReceivedListener> packetReceivedListeners = new ArrayList<IPacketReceivedListener>();
  
  static Logger log = ApplicationLog.getLogger(Connector.class);
  
  public Connector(UUID applicationId, String groupName, String groupPassword, int helloPort, int serverPort)
  {
    this.nodeId        = applicationId;
    this.groupName     = groupName;
    this.groupPassword = groupPassword;
    this.helloPort     = helloPort;
    this.serverPort    = serverPort;
  }
  
  public void initialize()
  {
    myOwnNode = new ClientNode(nodeId, getGroupName(), NetUtils.getPrimaryHostAddress(), getServerPort());
    ConnectionUtils.safeAddToClientList(myOwnNode, getConnectedClientList());
    pingService     = new PingService(this);
    pingListener    = new PingServiceListener(this);
    contactRequestorService = new ContactRequestorService(this);
    connectorServer = new ConnectorServer(this);
    clientListSynchronizer = new ClientListSynchronizer(this);
  }

  public void run()
  {
    ExecutorUtils.addFixedService(connectorServer);
    ExecutorUtils.addFixedService(pingListener);
    ExecutorUtils.addFixedService(contactRequestorService);
    ExecutorUtils.addScheduledService(pingService, 2, 60, TimeUnit.SECONDS);
    ExecutorUtils.addScheduledService(clientListSynchronizer, 20, 120, TimeUnit.SECONDS);
  }
  
  public void shutdown()
  {
    pingListener.shutdown();
    connectorServer.shutdown();
    contactRequestorService.shutdown();
    ExecutorUtils.shutdownScheduledService();
    ExecutorUtils.shutdownFixedService();
    // Signal all connected Clients that we are leaving
    signalGoodBye();
    log.info("Executors shut down.");
  }
  
  public static class ConnectorShutdownHook extends Thread {
    @Override
    public void run()
    {
      log.info("ShutdownHook called");
    }
  }
  
  public ClientNode getMyOwnNode()
  {
    return myOwnNode;
  }
  
  public boolean isMyOwnNode(ClientNode node)
  {
    return node.getNodeId().equals(getMyOwnNode().getNodeId());
  }
  
  public int getHelloPort()
  {
    return this.helloPort;
  }

  public BlockingQueue<ClientNode> getRequestorQueue()
  {
    return this.requestorQueue;
  }
  
  public List<ClientNode> getConnectedClientList()
  {
    return connectedClients;
  }

  public ClientNode getClientNode(UUID nodeId)
  {
    for (ClientNode node : getConnectedClientList())
    {
      if (node.getNodeId().equals(nodeId))
      {
        return node;
      }
    }
    return null;
  }
  
  public int getServerPort()
  {
    return this.serverPort;
  }
  
  public String getGroupName()
  {
    return this.groupName;
  }
  
  public String getGroupPassword()
  {
    return this.groupPassword;
  }
  
  public UUID getNodeId()
  {
    return this.nodeId;
  }
  
  public void addPacketReceivedListener(IPacketReceivedListener listener)
  {
    packetReceivedListeners.add(listener);
  }
  
  public void removePacketReceivedListener(IPacketReceivedListener listener)
  {
    packetReceivedListeners.remove(listener);
  }
  
  public void firePacketReceived(NodeCommand command)
  {
    for (IPacketReceivedListener listener : packetReceivedListeners)
    {
      listener.packetReceived(command);
    }
  }

  /**
   * Signal all connected clients that we are leaving.
   */
  protected void signalGoodBye()
  {
    for (ClientNode node : getConnectedClientList())
    {
      if ((!node.equals(getMyOwnNode())) && (!node.isShutdown()))
      {
        signalGoodBye(node);
      }
    }
  }

  protected void signalGoodBye(ClientNode node)
  {
    try
    {
      ConnectionCtx connectionCtx = ConnectionUtils.connectTo(this, node);
      connectionCtx.invoke(new SignalGoodByeCommand(getMyOwnNode()));
      connectionCtx.disconnect();
    } catch (Exception e)
    {
      log.warning("Problem connecting to "+node+": "+e.getMessage());
    }
  }
  
  public void shutdownNode(ClientNode nodeToShutdown)
  {
    for (ClientNode node : getConnectedClientList())
    {
      if (node.equals(nodeToShutdown))
      {
        log.info("Shutdown node "+node);
        node.shutdown();
      }
    }
  }


}
