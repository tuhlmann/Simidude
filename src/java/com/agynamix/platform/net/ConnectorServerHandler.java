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
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agynamix.platform.log.ApplicationLog;
import com.agynamix.platform.net.protocol.Auth;
import com.agynamix.platform.net.protocol.Expt;
import com.agynamix.platform.net.protocol.ICommands;
import com.agynamix.platform.net.protocol.NodeCommand;
import com.agynamix.platform.net.protocol.Rcmd;
import com.agynamix.simidude.source.SourceDataContents;

public class ConnectorServerHandler implements IConnectorServerHandler, Runnable {

  final ConnectionCtx connectionCtx;
  final IConnector    connector;
  
  Logger log = ApplicationLog.getLogger(ConnectorServerHandler.class);
  
  ExecutorService parallelTasks = Executors.newCachedThreadPool();
  
  public ConnectorServerHandler(IConnector connector, Socket socket)
  {
    this.connector = connector;
    this.connectionCtx = new ConnectionCtx(connector, socket);
  }

  public void run()
  {
    try {
      NodeCommand command = connectionCtx.getNodeCommandUtils().receiveCommand(ICommands.AUTH);
      if (ICommands.AUTH.equals(command.getCommand()))
      {
        Auth auth = (Auth) command;
        if (isNodeAuthenticated(auth))
        {
          connectionCtx.getNodeCommandUtils().sendAcpt(connector.getMyOwnNode().getNodeId());
          receive();
        } else {
          connectionCtx.getNodeCommandUtils().sendRejt();
          connectionCtx.close();
          return;
        }
      } else {
        log.log(Level.WARNING, "Command not recognized: "+command.getCommand());
        connectionCtx.getNodeCommandUtils().sendRejt();
        connectionCtx.close();
        return;
      }
    } catch (Exception e)
    {
      log.log(Level.WARNING, "Error receiving from client: "+e.getMessage(), e);
    }
  }
  
  private boolean isNodeAuthenticated(Auth authCommand)
  {
    if (connector.getGroupName().equals(authCommand.getGroupname()))
    {
      if (connector.getGroupPassword().equals(authCommand.getPassword()))
      {
        return true;
      }
    }
    return false;
  }

  /**
   * Receive is the main method that will take contents from a client.
   * This method is called after the client has been authorized. It will
   * run in a loop until the client closes the connection.
   * @throws IOException 
   */
  private void receive() throws IOException
  {
    boolean isConnected = true;    
    while (isConnected)
    {
      String methodName = "unknown";
      try
      {
        NodeCommand command = connectionCtx.getNodeCommandUtils().receiveCommand();
        methodName = "handle"+command.getCommand();
        Method handleMethod = this.getClass().getDeclaredMethod(methodName, NodeCommand.class);
        isConnected = (Boolean) handleMethod.invoke(this, command);
//        System.out.println("isConnected = "+isConnected);
        connector.firePacketReceived(command);
      } catch (NetworkProtocolException e)
      {
        log.log(Level.WARNING, e.getMessage());
      } catch (Exception e)
      {
        log.log(Level.WARNING, "Error calling method "+methodName, e);
      }
    }
  }

  protected boolean handleQUIT(NodeCommand command)
  {
//    System.out.println("Received quit command. Close socket and leave handler.");
    try {
      connectionCtx.close();
    } catch (Exception ignore) {}
    return false;
  }
  
  protected boolean handleRCMD(NodeCommand command)
  {
    try {
      Rcmd rcmd = (Rcmd) command;
      Object result = rcmd.getRemoteCommand().invoke(this);      
      connectionCtx.getNodeCommandUtils().sendObjt(result);
      return true;
    } catch (Exception e)
    {
      e.printStackTrace();
      return false;
    }
  }
  
  protected boolean handleEXPT(NodeCommand command)
  {
    Expt expt = (Expt) command;
    Exception e = expt.getException();
    System.out.println("Peer sent Exception: "+e.getMessage());
    e.printStackTrace();
    return true;
  }
  
  public void executeParallel(Runnable task)
  {
    parallelTasks.execute(task);
  }
  
  public void sendSourceDataContents(SourceDataContents contents) throws IOException
  {
    connectionCtx.getNodeCommandUtils().sendObjt(contents);
  }
  
  public void sendException(Exception exception) throws IOException
  {
    connectionCtx.getNodeCommandUtils().sendExpt(exception);
  }
  

}
