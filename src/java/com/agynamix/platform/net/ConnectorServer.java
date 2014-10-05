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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import com.agynamix.platform.concurrent.ThreadManagerAware;
import com.agynamix.platform.log.ApplicationLog;

public class ConnectorServer implements ThreadManagerAware {

  final ExecutorService serverHandlerService = Executors.newFixedThreadPool(10);
  
  final IConnector connector;
  ServerSocket serverSocket;
    
  boolean shouldStop = false;
  
  private static Logger log = ApplicationLog.getLogger(ConnectorServer.class);
  
  public ConnectorServer(IConnector connector)
  {
    this.connector = connector;
    try
    {
      // 23-08-2011: changed backlog from 1 to 100
      // don't bind to all addresses, only to the primary one.
      log.config("Try to bind to "+NetUtils.getPrimaryHostAddress()+" at port "+connector.getServerPort());
      serverSocket = new ServerSocket(connector.getServerPort(), 100, NetUtils.getPrimaryHostAddress()); 
      serverSocket.setSoTimeout(1000);
    } catch (IOException e)
    {
      e.printStackTrace();
      throw new FatalNetworkException(e);
    }
  }

  public void run()
  {
    while (!shouldStop)
    {
      try {
        Socket socket = serverSocket.accept();
//        System.out.println("Accept Client connecting from "+socket.getInetAddress());
//        serverHandlerService.execute(new ConnectorServerHandler(connector, socket));
        new Thread(new ConnectorServerHandler(connector, socket)).start();
      } catch (SocketTimeoutException e)
      {
        // Timeout occured      
      } catch (IOException e)
      {
        //e.printStackTrace();
      }
      try
      {
        Thread.sleep(2000);
      } catch (InterruptedException e) { }
    }
  }

  public String getId()
  {
    return "ConnectorService";
  }
  
  public void shutdown()
  {
    serverHandlerService.shutdownNow();
    shouldStop = true;
  }
  
}
