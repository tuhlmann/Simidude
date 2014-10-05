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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.platform.infra.PlatformUtils;
import com.agynamix.platform.log.ApplicationLog;
import com.agynamix.platform.net.protocol.ICommands;
import com.agynamix.simidude.impl.SimidudeApplicationContext;
import com.agynamix.simidude.remote.RemoteConnector;

public class ConnectionUtils {
  
  static Logger log = ApplicationLog.getLogger(ConnectionUtils.class);
  
  private static Object connectSemaphore = new Object();
  private static Object toByteSemaphore = new Object();
  private static Object miscSemaphore = new Object();

  public final static int CONNECT_TIMEOUT = 3000;
  
  /**
   * FIXME: Move into a different utility class
   */
  public static void safeAddToClientList(List<ClientNode> peerClientList, List<ClientNode> connectedClientList)
  {
    synchronized (miscSemaphore)
    {
      for (ClientNode node : peerClientList)
      {
        safeAddToClientList(node, connectedClientList);
      }      
    }
  }
  
  /**
   * FIXME: Move into a different utility class
   * Check the current connector list.
   * If it contains the requestors node id, replace the entry with the new one,
   * else add the new node to the list.
   * We must make sure that we tag our own ClientNode with the isSelf tag, otherwise
   * we would send us our own love letters.
   * @param newNode the ClientNode object of a just received connect request.
   * @param connectedClientList the list of currently connected clients.
   */
  public static void safeAddToClientList(ClientNode newNode, List<ClientNode> connectedClientList)
  {
    synchronized (miscSemaphore)
    {
      boolean shouldAdd = true;
      if (connectedClientList.size() > 0)
      {
        for (int i = connectedClientList.size()-1; i >= 0; i--)
        {
          ClientNode node = connectedClientList.get(i);
          if (node.getNodeId().equals(newNode.getNodeId()))
          {
            if (node.isShutdown())
            {
              if (node.getShutdownDate().before(newNode.getCreationDate()))
              {
                log.log(Level.WARNING, "Shutdown bevor CreationDate for node "+node);
                connectedClientList.remove(i);
              } else {
                shouldAdd = false;
              }
            } else {
              if (!node.isNodeActive())
              {
                connectedClientList.remove(i);
              } else {
                shouldAdd = false;
              }
            }
          }
        }
      }
      if (shouldAdd)
      {
        connectedClientList.add(newNode);      
//        System.out.println("My Client List now:");
//        for (ClientNode node : connectedClientList)
//        {
//          System.out.println("Node: "+node);
//        }
      }      
    }
  }
  
  public static byte[] toByteArray(String... fields)
  {
    StringBuilder sb = new StringBuilder();
    for (String s : fields)
    {
      if (sb.length() > 0)
      {
        sb.append(ICommands.FIELD_SEP);
      }
      sb.append(s);
    }
    return sb.toString().getBytes();      
  }  

  public static void clearInactiveFromClientList(IConnector connector, List<ClientNode> clients)
  {
    synchronized (miscSemaphore)
    {
      long time = System.currentTimeMillis() - 300000; // minus 5 Minuten
      Date threshold = new Date(time);
      for (int i = clients.size()-1; i >= 0; i--)
      {
        ClientNode node = clients.get(i);
        if (!node.equals(connector.getMyOwnNode()))
        {
          Date inactiveSince = node.getInactiveSince();
          Date shutdownDate  = node.getShutdownDate();
          boolean isRemoved = false;
          if (inactiveSince != null)
          {
            if (inactiveSince.before(threshold))
            {
//              System.out.println("Clearing out (inactive): "+node);
              clients.remove(i);
              isRemoved = true;
            }
          }
          if ((!isRemoved) && (shutdownDate != null))
          {
            clients.remove(i);
            isRemoved = true;
          }
        }
      }
      setConnectionStatus(clients);
    }
  }

  /**
   * @param connector interface to our connector
   * @param node client to connect to
   * @return a new and authenticated connection to a peer
   * @throws IOException
   * @throws NetworkAuthException 
   */
  public static ConnectionCtx connectTo(IConnector connector, ClientNode node) throws IOException, NetworkAuthException
  {
    try {
      Socket socket = connectSocket(node.getAddress(), node.getPort());
      ConnectionCtx connectionCtx = new ConnectionCtx(connector, socket);
      connectionCtx.getNodeCommandUtils().authenticate();
      node.setNodeActive();
      return connectionCtx;
    } catch (NetworkProtocolException e)
    {
      log.log(Level.INFO, "Error connecting with "+node, e);
      node.setNodeInactive();
      throw new IOException(e.getMessage());      
    } catch (IOException e)
    {
      log.log(Level.INFO, "Error connecting with "+node, e);
      node.setNodeInactive();
      throw e;
    }      
  }

  /**
   * @param connector interface to our connector
   * @param node client to connect to
   * @return a new and authenticated connection to a peer
   * @throws IOException
   * @throws NetworkAuthException 
   */
  public static ClientNode requestClientId(IConnector connector, String address, int port) throws IOException, NetworkAuthException
  {
    try {
      log.fine("request ClientId from "+address+":"+port);
      Socket socket = connectSocket(address, port);
      ConnectionCtx connectionCtx = new ConnectionCtx(connector, socket);
      connectionCtx.getNodeCommandUtils().authenticate();
      UUID uuid = (UUID) connectionCtx.getNodeCommandUtils().invoke(new RequestClientIdCommand());
      if (uuid != null)
      {
        ClientNode node = new ClientNode(uuid, connector.getGroupName(), socket.getInetAddress(), port);
        return node;
      }
      return null;
    } catch (Exception e)
    {
      log.log(Level.WARNING, "Error connecting with "+address+":"+port, e);
//      e.printStackTrace();
      return null;
    }      
  }
  
  protected static Socket connectSocket(String address, int port) throws IOException
  {
    return connectSocket(new InetSocketAddress(address, port));
  }
  
  protected static Socket connectSocket(InetAddress address, int port) throws IOException
  {
    return connectSocket(new InetSocketAddress(address, port));
  }
  
  protected static Socket connectSocket(InetSocketAddress socketAddress) throws IOException
  {
    Socket socket = new Socket();
    socket.connect(socketAddress, CONNECT_TIMEOUT); 
    return socket;
  }
  
  public static void setConnectionStatus(List<ClientNode> clientList)
  {
    if (clientList.size() > 1)
    {
      if (clientList.size() == 2)
      {
        RemoteConnector c = ((SimidudeApplicationContext)ApplicationBase.getContext()).getRemoteConnector();
        ClientNode myOwnNode = c.getConnector().getMyOwnNode();
        String address = findConnectedClient(myOwnNode, clientList);
        PlatformUtils.setConnected(address);        
      } else {
        PlatformUtils.setConnected(clientList.size()-1);        
      }
    } else {
      PlatformUtils.setNotConnected();
    }
  }
  
  private static String findConnectedClient(ClientNode myOwnNode, List<ClientNode> clientList)
  {
    for (ClientNode node : clientList)
    {
      if (!node.equals(myOwnNode))
      {
        return node.getAddress().getHostAddress();
      }
    }
    return "unknown";
  }


  
}
