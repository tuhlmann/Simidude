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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.agynamix.platform.net.protocol.NodeCommandUtils;

public class ConnectionCtx implements IStreamSuplier{

  final Socket socket;
  final BufferedInputStream  istream;
  final BufferedOutputStream ostream;
  
  final NodeCommandUtils     nodeCommandUtils;
  final IConnector           connector;
  
  
  public ConnectionCtx(IConnector connector, Socket socket)
  {
    this.socket = socket;
    try
    {
      ostream = new BufferedOutputStream(socket.getOutputStream());
      istream = new BufferedInputStream(socket.getInputStream());
    } catch (IOException e)
    {
      throw new FatalNetworkException(e);
    }
    
    this.connector        = connector;
    this.nodeCommandUtils = new NodeCommandUtils(connector, this);
  }

  public void disconnect()
  {
    try
    {
      getNodeCommandUtils().sendQuit();
      close();
    } catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  public void close()
  {
    try
    {
      socket.close();
    } catch (IOException e)
    {
      // Already closed.
    }
  }

  public BufferedOutputStream getOutputStream()
  {
    return ostream;
  }
  
  public BufferedInputStream getInputStream()
  {
    return istream;
  }

  public String getHostAddress()
  {
    return socket.getInetAddress().getHostAddress();
  }

  public NodeCommandUtils getNodeCommandUtils()
  {
    return nodeCommandUtils;
  }
  
  public IConnector getConnector()
  {
    return connector;
  }
  
  public Object invoke(IRemoteCommand command) throws IOException, NetworkProtocolException
  {
    return getNodeCommandUtils().invoke(command);
  }

}
