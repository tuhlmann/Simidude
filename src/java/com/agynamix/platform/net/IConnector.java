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

import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

import com.agynamix.platform.net.protocol.NodeCommand;

public interface IConnector {

  /**
   * 
   * @return the Hello port for broadcasting that should be used.
   */
  int getHelloPort();

  /**
   * 
   * @return the server port that is used for exchanging messages.
   */
  int getServerPort();

  /**
   * This queue is used to store addresses of clients that have just advertised.
   * @return
   */
  BlockingQueue<ClientNode> getRequestorQueue();

  /**
   * 
   * @return a List of all clients we know of.
   */
  List<ClientNode> getConnectedClientList();

  /**
   * @return the node of this running instance.
   */
  ClientNode getMyOwnNode();

  /**
   * Checks if the given node is my own
   * @return true if the given node is my own node, false otherwise.
   */
  boolean isMyOwnNode(ClientNode node);

  String getGroupPassword();

  String getGroupName();

  UUID getNodeId();

  public void addPacketReceivedListener(IPacketReceivedListener listener);
  
  public void removePacketReceivedListener(IPacketReceivedListener listener);
  
  /**
   * Notify listeners that we received a command
   * @param command the received command
   */
  void firePacketReceived(NodeCommand command);
  
  /**
   * Get the ClientNode with the specified UUID
   * @param nodeId the wanted UUID
   * @return the ClientNode that holds the wanted UUID or null if it can't be found 
   */
  ClientNode getClientNode(UUID nodeId);

  /**
   * Tells the Connector that a specific node had been shutdown.
   * @param node the node that was shut down.
   */
  void shutdownNode(ClientNode node);




}
