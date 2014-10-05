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
package com.agynamix.platform.net.protocol;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import com.agynamix.platform.net.ConnectionCtx;
import com.agynamix.platform.net.IConnector;
import com.agynamix.platform.net.IRemoteCommand;
import com.agynamix.platform.net.NetUtils;
import com.agynamix.platform.net.NetworkAuthException;
import com.agynamix.platform.net.NetworkProtocolException;

/**
 * FIXME: NodeCommandUtils should be given an instance per Handler, so that several threads 
 * should not have to wait on each other.
 * @author tuhlmann
 *
 */
public class NodeCommandUtils {
  
  final ConnectionCtx connectionCtx;
  final IConnector    connector;
  
  public NodeCommandUtils(IConnector connector, ConnectionCtx connectionCtx)
  {
    this.connectionCtx = connectionCtx;
    this.connector     = connector;
  }

  public void sendQuit() throws IOException
  {
    Quit quit = new Quit();
    sendPacket(quit);
  }

  public void sendAcpt(UUID nodeId) throws IOException
  {
    Acpt acpt = new Acpt(nodeId);
    sendPacket(acpt);
  }
  
  public void sendRejt() throws IOException
  {
    Rejt rejt = new Rejt();
    sendPacket(rejt);    
  }
  
  public void sendExpt(Exception exception) throws IOException
  {
    Expt expt = new Expt(exception);
    sendPacket(expt);
  }

  public void sendAckn() throws IOException
  {
    Ackn ackn = new Ackn();
    sendPacket(ackn);
  }
  
  public void sendObjt(Object obj) throws IOException
  {
    Objt o = new Objt(obj);
    sendPacket(o);
  }

  public void sendData(File file) throws IOException
  {
    Data data = new Data(file.getAbsolutePath(), file.length());
    sendPacket(data);
  }

  /**
   * Invoke remote command
   * @param command auszuführendes Kommando
   * @return das Ergebnis der ausgeführten Funktion
   * @throws IOException 
   * @throws NetworkProtocolException 
   */
  public Object invoke(IRemoteCommand command) throws IOException, NetworkProtocolException
  {
    Rcmd rcmd = new Rcmd(command);
    sendPacket(rcmd);
    return readObjt();
  }

  public void readAckn() throws NetworkProtocolException, IOException
  {    
    NodeCommand command = receiveCommand();
    if (ICommands.ACKN.equals(command.getCommand()))
    {
//      System.out.println("read ACKN");
    } else {
      System.out.println("did not read ACKN");
      throw new NetworkProtocolException("Peer did not reply with ACKN");
    }
  }
  
  public Object readObjt() throws NetworkProtocolException, IOException
  {    
    NodeCommand command = receiveCommand();
    if (ICommands.OBJT.equals(command.getCommand()))
    {
//      System.out.println("read OBJT");
      Objt objt = (Objt) command;
      return objt.getObject();
    } else {
//      System.out.println("did not read OBJT");
      throw new NetworkProtocolException("Peer did not reply on RCMD with OBJT");
    }
  }
  
  public NodeCommand receiveCommand() throws IOException, NetworkProtocolException
  {
    return receiveCommand((String[]) null);
  }
  
  public NodeCommand receiveCommand(String... recognizedCommands) throws IOException, NetworkProtocolException
  {
    return NodeCommandFactory.toProtocol(receivePacket(recognizedCommands));
  }
  
  /**
   * Read a packet from the wire.
   * @param inputStream
   * @return
   * @throws IOException 
   * @throws NetworkProtocolException 
   */
  public byte[] receivePacket() throws IOException, NetworkProtocolException
  {
    return receivePacket((String[]) null);
  }
  
  
  /**
   * Read a packet from the wire.
   * @param inputStream
   * @return
   * @throws IOException 
   * @throws NetworkProtocolException 
   */
  public byte[] receivePacket(String... recognizedCommands) throws IOException, NetworkProtocolException
  {
    byte[] payload = null;
    byte[] header = readPacket(null, ICommands.PACKET_HEADER_SIZE);
    String command = NodeCommandFactory.getCommand(header);
    if (recognizedCommands != null)
    {
      if (!isRecognizedCommand(command, recognizedCommands))
      {
        throw new NetworkProtocolException("Command not recognized at this point: "+command);
      }
    }
    int packetSize = NetUtils.byteArrayToInt(header, 4);
    if (packetSize > 0)
    {
      payload = readPacket(header, packetSize);
    }
    byte[] buffer = new byte[ICommands.PACKET_HEADER_SIZE+packetSize];
    System.arraycopy(header, 0, buffer, 0, header.length);
    if (packetSize > 0)
    {
      System.arraycopy(payload, 0, buffer, header.length, payload.length);
    }
    
//    System.out.println("received("+packetSize+"): "+new String(buffer));
    return buffer;
  }

  private boolean isRecognizedCommand(String command, String[] recognizedCommands)
  {
    if (recognizedCommands != null)
    {
      for (String s : recognizedCommands)
      {
        if (s.equals(command))
        {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Read a fixed amount of bytes from the input stream
   * @param istream the input stream
   * @param len the amount of bytes to read
   * @return the byte buffer of read bytes.
   * @throws IOException if we cannot read from the stream 
   */
  private byte[] readPacket(byte[] header, int len) throws IOException
  {
    byte[] buffer = new byte[len];
    int offset  = 0;
    int numRead = 0;
    while (offset < buffer.length && (numRead = connectionCtx.getInputStream().read(buffer, offset, buffer.length-offset)) >=0 )
    {
      offset += numRead;
    }
    if (offset < buffer.length)
    {
      String s = "Could not read specified number of bytes from the InputStream. Should read: "+len+", Actually read: "+offset;
      if (header != null)
      {
        s += ", Header was: "+new String(header);
      } else {
        s += ", Try to read header.";
      }
//      throw new IOException(s);
      System.out.println(s);
      System.out.println("Simulate QUIT");
      Quit quit = new Quit(); // Simulate quit command and return this.
      return quit.toPacket();
    }
    return buffer;
  }

  /**
   * Send a protocol packet. The packet is pretty simple. It consists of
   * <ul>
   *   <li>4 Byte command sequence</li>
   *   <li>4 Byte specifying the length of the packet (excluding the first 8 byte)</li>
   *   <li>data of the length defined by the 4 bytes before</li>
   * </ul>
   * a packet command handler will then interpret the received packet
   * @param ostream
   * @param command
   * @throws IOException
   */
  public void sendPacket(NodeCommand command) throws IOException
  {
    byte[] buffer = command.toPacket();
//    System.out.println("Send command: "+new String(buffer));
    connectionCtx.getOutputStream().write(buffer);
    connectionCtx.getOutputStream().flush();
  }

  public void authenticate() throws IOException, NetworkAuthException, NetworkProtocolException
  {
    sendPacket(new Auth(connector.getGroupName(), connector.getGroupPassword()));
    NodeCommand command = NodeCommandFactory.toProtocol(receivePacket());
    if (ICommands.ACPT.equals(command.getCommand()))
    {
//      System.out.println("Node authenticated: "+((Acpt)command).getNodeId());
    } else {
      throw new NetworkAuthException("Could not authenticate with node at "+connectionCtx.getHostAddress());
    }    
  }

}
