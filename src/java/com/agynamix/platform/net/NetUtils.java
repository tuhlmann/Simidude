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
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agynamix.platform.frontend.preferences.IPreferenceConstants;
import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.platform.log.ApplicationLog;
import com.agynamix.platform.net.protocol.ICommands;

public class NetUtils {
  
  private static Logger log = ApplicationLog.getLogger(NetUtils.class);

  public static List<InetAddress> getHostAddresses()
  {
    List<InetAddress> ipAddresses = new ArrayList<InetAddress>();
    
    try {
      InetAddress customAddress = getCustomAddressFromPreferences();
      if (customAddress != null) {
        ipAddresses.add(customAddress);
        return ipAddresses;
      }
    } catch (Exception e) {
      log.log(Level.WARNING, e.getMessage(), e);
    }
 
    try
    {
      Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
      while (interfaces.hasMoreElements())
      {
        NetworkInterface ni = interfaces.nextElement();
        Enumeration<InetAddress> addresses = ni.getInetAddresses();
        while (addresses.hasMoreElements())
        {
          InetAddress addr = addresses.nextElement();
          if (!addr.isLoopbackAddress())
          {
            if (addr instanceof Inet4Address) // Fixme: Only IP4 at this time.
            {
              if (!matchesIgnoreAddressesFromPreferences(addr))
              {
                // if (addr.isReachable(10000))
                // {
                // System.out.println("Address "+addr+" is reachable");
                log.fine("My address: "+addr.getHostAddress());
                ipAddresses.add(addr);
              }
              // }
            }
          }
        }
      }
//      addCustomAddressFromPreferences(ipAddresses);
    } catch (SocketException e)
    {
      log.log(Level.WARNING, e.getMessage(), e);
    } catch (IOException e)
    {
      log.log(Level.WARNING, e.getMessage(), e);
    }
    if (ipAddresses.size() == 0)
    {
      try
      {
        ipAddresses.add(InetAddress.getLocalHost());
      } catch (UnknownHostException e)
      {
        e.printStackTrace();
        throw new FatalNetworkException(e);
      }
    }
    return ipAddresses;
  }

  public static InetAddress getPrimaryHostAddress()
  {
    try {
      InetAddress customAddress = getCustomAddressFromPreferences();
      if (customAddress != null) {
        return customAddress;
      }
    } catch (Exception e) {
      log.log(Level.WARNING, e.getMessage(), e);
    }
    try {
      return getHostAddresses().get(0);
    } catch (Exception e) {
      try
      {
        return Inet4Address.getByName("127.0.0.1");
      } catch (UnknownHostException e1)
      {
        return null;
      }
    }
  }
  
  public static String getLocalHostAddress()
  {
    try {
      InetAddress customAddress = getCustomAddressFromPreferences();
      if (customAddress != null) {
        return customAddress.getHostAddress();
      }
//      InetAddress addr = InetAddress.getLocalHost();
//      System.out.println("Found local address: "+addr.getHostAddress());
//      return addr.getHostAddress();
      List<InetAddress> addresses = getHostAddresses();
//      for (InetAddress a : addresses)
//      {
//        System.out.println("Address: "+a.getHostAddress());
//      }
      InetAddress hostname = addresses.get(0);
      return hostname.getHostAddress();
    } catch (Exception e) {
    	e.printStackTrace();
      return "127.0.0.1";
    }
  }

  /**
   * Convert a received buffer from a broadcast to a ClientNode instance
   * 
   * @param buffer
   *          the received byte buffer
   * @return a ClientNode instance
   */
  public static ClientNode bcBufferToNode(byte[] buffer)
  {
    try
    {
      UUID nodeId = UUID.fromString(NetUtils.decodeField(buffer, 0, 0));
      String groupname = NetUtils.decodeField(buffer, 0, 1);
      String ip = NetUtils.decodeField(buffer, 0, 2);
      int port = Integer.parseInt(NetUtils.decodeField(buffer, 0, 3));
      return new ClientNode(nodeId, groupname, InetAddress.getByName(ip), port);
    } catch (Exception e)
    {
      e.printStackTrace();
      return null;
    }
  }

  public static byte[] nodeToBcBuffer(ClientNode node)
  {
    StringBuilder sb = new StringBuilder();
    sb.append(NetUtils.encodeField(node.getNodeId().toString()));
    sb.append(ICommands.FIELD_SEP);
    sb.append(NetUtils.encodeField(node.getGroupname()));
    sb.append(ICommands.FIELD_SEP);
    sb.append(NetUtils.encodeField(node.getAddress().getHostAddress()));
    sb.append(ICommands.FIELD_SEP);
    sb.append(NetUtils.encodeField("" + node.getPort()));
    byte[] b = sb.toString().getBytes();
    byte[] buffer = new byte[ICommands.BC_BUFFER_SIZE];
    System.arraycopy(b, 0, buffer, 0, b.length);
    return buffer;
  }

  public static String encodeField(String str)
  {
    try
    {
      return URLEncoder.encode(str, ICommands.CMD_CHARSET);
    } catch (UnsupportedEncodingException e)
    {
      throw new IllegalStateException(e);
    }
  }
  
  public static String decodeField(String encoded)
  {
    try {
      return URLDecoder.decode(encoded.trim(), ICommands.CMD_CHARSET);
    } catch (UnsupportedEncodingException e)
    {
      throw new IllegalStateException(e);
    }    
  }  

  /**
   * Receives a byte buffer that contains quoted fields.
   * We scan through the buffer starting at offset until we find
   * the elemIdx'th occurence of ICommands.FIELD_SEP. We strip
   * the seperator from the found field and return the found part as a new String
   * @param buffer the byte buffer
   * @param offset the offset where the actual payload starts.
   * @param elemIdx the number of the required field starting with 0.
   * @return
   */
  public static String decodeField(byte[] buffer, int offset, int elemIdx)
  {    
    byte[] sepBuffer = ICommands.FIELD_SEP.getBytes();
    int foundCount  = 0;
    int startOffset = offset;
    int endOffset   = -1;
    for (int i = offset; i < buffer.length; i++)
    {
      if (isFieldSeparator(buffer, i, sepBuffer))
      {
        foundCount++;
        if (foundCount == elemIdx)
        {
          startOffset = i+sepBuffer.length;
        } else if (foundCount == elemIdx+1)
        {
          endOffset = i;
          return decodeField(new String(buffer, startOffset, endOffset - startOffset));
        }
      }
    }
    // End of string reached
    return decodeField(new String(buffer, startOffset, buffer.length - startOffset));
  }

  private static boolean isFieldSeparator(byte[] buffer, int idx, byte[] separator)
  {
    for (int i = 0; i < separator.length; i++)
    {
      if (buffer[idx+i] != separator[i])
      {
        return false;
      }
    }
    return true;
  }

  public static final byte[] intToByteArray(int value)
  {
    return new byte[] { (byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value };
  }

  public static final int byteArrayToInt(byte[] b, int offset)
  {
    return (b[offset] << 24) + ((b[offset+1] & 0xFF) << 16) + ((b[offset+2] & 0xFF) << 8) + (b[offset+3] & 0xFF);
  }
  
  public static InetAddress getCustomAddressFromPreferences() throws UnknownHostException
  {
    String address = ApplicationBase.getContext().getConfiguration().getProperty(IPreferenceConstants.OWN_IP_ADRESS);
    if ((address == null) || (address.length() == 0))
    {
      return null;
    }
    return Inet4Address.getByName(address);
  }
  
  private static boolean matchesIgnoreAddressesFromPreferences(InetAddress address)
  {
    List<String> ignoreAddresses = ApplicationBase.getContext().getConfiguration().getProperyList(IPreferenceConstants.IGNORE_NETWORK_ADDRESSES);
    for (String ignoreAddress : ignoreAddresses)
    {
      try
      {
        InetAddress a = Inet4Address.getByName(ignoreAddress);
        if (matchesAddressPattern(a, address))
        {
          return true;
        }
      } catch (UnknownHostException e)
      {
        e.printStackTrace();
      }
    }
    return false;
  }
  
  private static boolean matchesAddressPattern(InetAddress pattern, InetAddress address)
  {
    byte[] patternArr = pattern.getAddress();
    byte[] addressArr = address.getAddress();
    
    for (int i = 0; i<4; i++)
    {
      short patternS = (short) (patternArr[i] & 255);
      short addressS = (short) (addressArr[i] & 255);
      
      if (patternS == 255)
      {
        continue;
      }
      if (patternS != addressS)
      {
        log.fine("Pattern matches NOT: "+pattern+" vs. "+address);
        return false;
      }
    }
    log.fine("Pattern matches: "+pattern+" vs. "+address);
    return true;    
  }

  private static void addCustomAddressFromPreferences(List<InetAddress> ipAddresses) throws UnknownHostException
  {
    InetAddress customAddress = getCustomAddressFromPreferences();
    if (customAddress != null)
    {
      if (!ipAddresses.contains(customAddress))
      {
        log.fine("Add custom address from prefs: "+customAddress.getHostAddress());
        ipAddresses.add(customAddress);
      }
    }
  }



}
