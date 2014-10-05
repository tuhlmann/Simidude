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
package swt.snippets;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class BroadcastExample {
  
  public final static int port = 12555;

  /**
   * @param args
   */
  public static void main(String[] args)
  {
    if (args.length == 0)
    {
      System.out.println("Usage: BroadcastExample server|client");
      System.exit(1);
    }
    
    BroadcastExample bc = new BroadcastExample();
    
    if (args[0].equals("server"))
    {
      bc.runServer();
    } else {
      bc.runClient();
    }
  }

  private void runServer()
  {
    try
    {
      byte[] buffer = new byte[512];
      DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
      DatagramSocket s = new DatagramSocket(port);
      System.out.println("Wait for package");
      s.receive(dp);
      System.out.println("Returned");
      System.out.println("Received Package from "+dp.getAddress().getHostAddress());
      System.out.println("Length of data: "+dp.getLength());
      String msg = new String(dp.getData());
      System.out.println("Msg: "+msg);
      
      // now send that client back my own address
      DatagramPacket dp2 = new DatagramPacket(buffer, buffer.length, dp.getAddress(), port); 
      s.send(dp2);
      System.out.println("Sent answer");
      
    } catch (SocketException e)
    {
      e.printStackTrace();
    } catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
  }
  
  
  private void runClient()
  {
    try
    {
      byte[] buffer = new byte[512];
      String msg = "192.168.0.71";
      System.arraycopy(msg.getBytes(), 0, buffer, 0, msg.length());
      DatagramSocket s = new DatagramSocket(null);
      DatagramPacket dp = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("192.168.0.255"), port); 
      s.send(dp);
      
      // wait for answer
      byte[] buffer2 = new byte[512];
      DatagramSocket s2 = new DatagramSocket(port);
      DatagramPacket dp2 = new DatagramPacket(buffer2, buffer2.length);
      s2.receive(dp2);
      System.out.println("Received Answer from "+dp2.getAddress().getHostAddress());
      
      
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
  

}
