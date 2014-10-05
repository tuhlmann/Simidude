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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agynamix.platform.concurrent.ThreadManagerAware;
import com.agynamix.platform.log.ApplicationLog;
import com.agynamix.platform.net.protocol.ICommands;

public class PingServiceListener implements ThreadManagerAware {

  boolean shouldStop = false;

  final IConnector connector;
  
  DatagramSocket listenSocket;
  List<InetAddress> myAddresses = new ArrayList<InetAddress>();
  
  Logger log = ApplicationLog.getLogger(PingServiceListener.class);
  
  public PingServiceListener(IConnector connector)
  {
    this.connector = connector;
    try
    {
      myAddresses = NetUtils.getHostAddresses();
      listenSocket = new DatagramSocket(connector.getHelloPort());
      listenSocket.setSoTimeout(1000);
    } catch (SocketException e)
    {
      log.severe("Error registering port "+connector.getHelloPort()+": "+e.getMessage());
      new FatalNetworkException(e);
    }
  }

  public void run()
  {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    while (!shouldStop)
    {
      listenForPing();
    }
  }

  private void listenForPing()
  {
    try
    {
      byte[] buffer = new byte[ICommands.BC_BUFFER_SIZE];
      DatagramPacket dp = new DatagramPacket(buffer, buffer.length);   
      listenSocket.receive(dp);
      ClientNode requestor = NetUtils.bcBufferToNode(buffer);
      if (areWeInterested(dp, requestor))
      {
        log.fine("We are interested in ping from "+dp.getAddress().getHostAddress());
//        System.out.println("We received interesting BC from "+dp.getAddress().getHostAddress());
        if (requestor != null)
        {
          // Replace the IP from the buffer with the one we received the packet from.
          //ClientNode requestor2 = new ClientNode(requestor.getNodeId(), requestor.getGroupname(), dp.getAddress(), requestor.getPort());
//          log.fine("Connection request from "+dp.getAddress().getHostAddress());
          log.fine("Connection request from "+requestor.getAddress());
          // FIXME: Test using the address from the buffer
          connector.getRequestorQueue().put(requestor); // Try with the address from the buffer
        } else {
          log.info("The received bc buffer could not be decoded: "+new String(buffer));
        }
      }
    } catch (SocketTimeoutException e)
    {
      // Nothing came, no problem though, just the timeout expired
    } catch (SocketException e)
    {
      //e.printStackTrace();
    } catch (IOException e)
    {
      //e.printStackTrace();
    } catch (InterruptedException e)
    {
    }    
  }
  
  private boolean areWeInterested(DatagramPacket dp, ClientNode requestor)
  {
//    System.out.println("areWeInterested: Are we interested in "+requestor);
    if (dp.getAddress().isLoopbackAddress())
    {
      log.log(Level.WARNING, "Loopback address submitted: "+dp.getAddress().getHostAddress());
      return false;
    }
    if (myAddresses.contains(dp.getAddress()))
    {
      return false;
    }
    if (myAddresses.contains(requestor.getAddress()))
    {
      return false;
    }
    if (!connector.getMyOwnNode().getGroupname().equals(requestor.getGroupname()))
    {
      // Wrong Simidude Group
      return false;
    }
    return true;
    
//    if (connector.getConnectedClientList().contains(requestor))
//    {
//      System.out.println("areWeInterested: We already know this Client: "+requestor);
//      return false;
//    }
//    return true;
  }

  public String getId()
  {
    return "PingServiceListener";
  }
  
  public void shutdown()
  {
    this.shouldStop = true;
  }
  
}
