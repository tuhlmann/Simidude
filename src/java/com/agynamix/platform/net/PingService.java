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
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Logger;

import com.agynamix.platform.concurrent.ThreadManagerAware;
import com.agynamix.platform.log.ApplicationLog;

public class PingService implements ThreadManagerAware {

  final IConnector connector;
  
  boolean shouldStop = false;
  
  Logger log = ApplicationLog.getLogger(PingService.class);
  
  public PingService(IConnector connector)
  {
    this.connector = connector;
  }

  /**
   * Thread only send one ping. The thread is managed by a scheduled Executor service who will trigger
   * the thread itself. 
   */
  public void run()
  {
    if (!shouldStop)
    {
      advertise();
    }
  }
  
  private void advertise()
  {
    try
    {
      List<InetAddress> myAddresses = NetUtils.getHostAddresses();
      for (InetAddress myAddr : myAddresses)
      {
        byte[] buffer = NetUtils.nodeToBcBuffer(connector.getMyOwnNode());
        String ip = myAddr.getHostAddress();
        InetAddress bcAddr = getBroadcastAddress(myAddr);
//        InetAddress bcAddr = InetAddress.getByName("255.255.255.255");
//        System.arraycopy(ip.getBytes(), 0, buffer, 0, ip.length());
        DatagramSocket s = new DatagramSocket(null);
        DatagramPacket dp = new DatagramPacket(buffer, buffer.length, bcAddr, connector.getHelloPort()); 
        log.fine("Send Broadcast to "+bcAddr);
        s.send(dp);
      }      
    } catch (SocketException e)
    {
      e.printStackTrace();
    } catch (UnknownHostException e)
    {
      e.printStackTrace();
    } catch (IOException e)
    {
      e.printStackTrace();
    }

  }

  private InetAddress getBroadcastAddress(InetAddress myAddr)
  {
    InetAddress re = myAddr;
    if (myAddr instanceof Inet4Address)
    {
      try
      {
        byte[] ip = myAddr.getAddress();
//        ip[0] = (byte)255;
//        ip[1] = (byte)255;
//        ip[2] = (byte)255;
        ip[3] = (byte)255;
          re = InetAddress.getByAddress(ip);
//        System.out.println("BC address is "+re.getHostAddress());
      } catch (UnknownHostException e)
      {
        e.printStackTrace();
      }
    }
    return re;
  }

  public String getId()
  {
    return "PingService";
  }

  public void shutdown()
  {
    this.shouldStop = true;
  }
  

}
