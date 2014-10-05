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
package com.agynamix.simidude.remote;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agynamix.platform.frontend.preferences.IPreferenceConstants;
import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.platform.infra.ExecutorUtils;
import com.agynamix.platform.infra.Tupel;
import com.agynamix.platform.log.ApplicationLog;
import com.agynamix.platform.net.ClientNode;
import com.agynamix.platform.net.ConnectionCtx;
import com.agynamix.platform.net.ConnectionUtils;
import com.agynamix.platform.net.Connector;
import com.agynamix.platform.net.IConnector;
import com.agynamix.platform.net.IPacketReceivedListener;
import com.agynamix.platform.net.NetworkAuthException;
import com.agynamix.platform.net.NetworkProtocolException;
import com.agynamix.platform.net.protocol.NodeCommand;
import com.agynamix.simidude.infra.ModelProvider;
import com.agynamix.simidude.source.ISourceData;
import com.agynamix.simidude.source.ISourceDataListener;
import com.agynamix.simidude.source.SourceQueueService;
import com.agynamix.simidude.source.ISourceData.TransportType;


public class RemoteConnector implements ISourceDataListener, IPacketReceivedListener {
  
  public static final String SERVICE_NAME = "RemoteConnector";
  
  Connector connector;
  SourceDataSynchronizer sourceDataSynchronizer;
  
  Logger log = ApplicationLog.getLogger(RemoteConnector.class);

  public RemoteConnector()
  {
    
  }
  
  public void shutdown()
  {
    if (connector != null)
    {
      connector.shutdown();
      connector = null;
    }
  }

  public void initializeConnector(ModelProvider modelProvider)
  {
    String groupName     = ApplicationBase.getContext().getConfiguration().getProperty(IPreferenceConstants.NODE_GROUP_NAME);
    String groupPassword = ApplicationBase.getContext().getConfiguration().getProperty(IPreferenceConstants.NODE_GROUP_PWD);
    int    helloPort     = ApplicationBase.getContext().getConfiguration().getInteger(IPreferenceConstants.HELLO_PORT);
    int    serverPort    = ApplicationBase.getContext().getConfiguration().getInteger(IPreferenceConstants.SERVER_PORT);
    
    connector = new Connector(modelProvider.getSenderId(), groupName, groupPassword, helloPort, serverPort);
    connector.initialize();  
  }
  
  public void establishConnection()
  {
    log.info("Start SimiDude's RemoteConnector");
    
    connector.run();
    
    sourceDataSynchronizer = new SourceDataSynchronizer(this);
    ExecutorUtils.addScheduledService(sourceDataSynchronizer, 10, 10, TimeUnit.SECONDS);

    SourceQueueService qs = (SourceQueueService) ApplicationBase.getContext().getService(SourceQueueService.SERVICE_NAME);
    qs.addSourceDataListener(this);

//  WorkbenchUtils.setConnected();
      
    // Get all existing items from server
      
//    if (c.getConnectionState() == ConnectionState.BROKEN)
//    {
//      PlatformUtils.setConnectionBroken();
//    }
        
//    connector.addPacketReceivedListener(this);
      
  }
  
  /**
   * In the preferences there is now a possibility to add permanent IP addresses or network names. These addresses are now added to the connector list
   * as if they had made a connection attempt.
   * @since 1.3.0
   * @param connector
   */
  public void contactPermanentNetworkAddresses()
  {
    ExecutorUtils.addParallelTask(new Runnable() {      
      public void run()
      {
        List<String> networkAddresses = ApplicationBase.getContext().getConfiguration().getProperyList(IPreferenceConstants.PERMANENT_NETWORK_ADDRESSES);
        for (String address : networkAddresses)
        {
          Tupel<String, Integer> ipAndPort = getIPAddressAndPort(getConnector(), address);
          if (ipAndPort != null)
          {
            if (ipAndPort.getValue1() != null)
            {
              ClientNode node = requestClientNode(getConnector(), ipAndPort.getValue1(), ipAndPort.getValue2());
              if (node != null)
              {
                try
                {
//                  System.out.println("ClientNode returned. Put into RequestorQueue");
                  getConnector().getRequestorQueue().put(node);
                } catch (InterruptedException e) {}
//              } else {
//                System.out.println("ClientNode returned NULL");
              }
            }
          } else {
            log.warning("Could not parse permanent address: "+address);
          }
        }
      }
    });
    
  }

  /**
   * Connect a remote client and request its ClientNode data
   * @param ipAddress the ip address to contact
   * @param port the port to contact
   * @return a ClientNode instance or null if a connection was unsuccessful
   */
  private ClientNode requestClientNode(IConnector connector, String ipAddress, Integer port)
  {
    try
    {
      return ConnectionUtils.requestClientId(connector, ipAddress, port);
    } catch (Exception e)
    {
//      e.printStackTrace();
      return null;
    }
  }

  private Tupel<String, Integer> getIPAddressAndPort(IConnector connector, String address)
  {
    String[] arr = address.split(":");
    java.net.InetAddress inetAdd;
    try
    {
      inetAdd = java.net.InetAddress.getByName(arr[0]);
//      System.out.println ("IP Address is for Name "+arr[0]+": " + inetAdd.getHostAddress());
      return new Tupel<String, Integer>(inetAdd.getHostAddress(), arr.length == 2 ? Integer.parseInt(arr[1]) : connector.getServerPort());
    } catch (UnknownHostException e)
    {
//      e.printStackTrace();
      log.log(Level.INFO, "Could not connect to "+address, e);
    }
    return null;
  }

  public IConnector getConnector()
  {
    return this.connector;
  }
  
  public void sourceDataChanged(ISourceData data)
  {
    // send received data to server
    if (data.getTransportType() != TransportType.remote)
    {
      transportItem(data);
    }
  }
  
  public void transportItem(ISourceData data)
  {
//    System.out.println("TransportItem: "+data.getText());
    ISourceData sourceData = data.copy();
    sourceData.setTransportType(TransportType.remote);

    IConnector connector = getConnector();
    send(connector.getConnectedClientList(), sourceData);
  }
  
  /**
   * Responsible to send data out to other clients
   * @param serializer
   */
  private void send(List<ClientNode> nodes, ISourceData sourceData)
  {
    for (ClientNode node : nodes)
    {
      if ((!node.equals(connector.getMyOwnNode())) && (!node.isShutdown()))
      {       
        try {
          send(node, sourceData);
        } catch (IOException e)
        {
          log.warning("Communication with "+node.getAddress().getHostAddress()+" not possible");
        }
      }
    }
  }

  private void send(ClientNode node, ISourceData sourceData) throws IOException
  {
    ConnectionCtx connectionCtx = null;
    try {
      connectionCtx = ConnectionUtils.connectTo(connector, node);     
//      System.out.println("Send ISourceData to "+node);
      connectionCtx.invoke(new TransportSourceDataCommand(sourceData));
      connectionCtx.disconnect();
    } catch (NetworkAuthException e)
    {
      log.log(Level.WARNING, e.getMessage(), e);
    } catch (NetworkProtocolException e)
    {
      log.log(Level.WARNING, e.getMessage(), e);
    } finally {
      if (connectionCtx != null)
      {
        connectionCtx.close();
      }
    }
  }
  
//  public void uploadSourceDataContents(SourceDataContents contents)
//  {
//    transporter.uploadSourceDataContents(contents);
//  }
  
  public void packetReceived(NodeCommand command)
  {
//    if (ICommands.CLIE.equals(command.getCommand()))
//    {
//      System.out.println("packetReceived: CLIE");
//      parallelTasks.submit(new RetrieveExistingSourceDataTask(connector));
//    }
  }
  
//  public SourceDataContents requestContentsForProxyObject(ISourceData sourceData)
//  {
//    RemoteSourceDataTransportMBean transporter = getRemoteSourceDataTransportDelegate();
//    System.out.println("request contents");
//    SourceDataContents sd = transporter.requestContentsForProxyObject(sourceData);
//    System.out.println("Return from request");
//    return sd;
//  }


}
