package com.agynamix.platform.log;

import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.platform.net.ClientNode;
import com.agynamix.platform.net.IConnector;
import com.agynamix.simidude.impl.SimidudeApplicationContext;
import com.agynamix.simidude.remote.RemoteConnector;
import com.agynamix.simidude.source.ISourceData;
import com.agynamix.simidude.source.ISourceDataListener;

public class ConnectionLog implements ISourceDataListener {

  public static final String SERVICE_NAME = "ConnectionLog";
  
  public static final int    STATISTICS_MAP_MAX_DURATION = 12; // 12 hours max.
  
  RemoteConnector   remoteConnector;
  IConnector        connector;
  
  Map<InetAddress, ConnectionLogNodeStatistic> nodeStatistics = new HashMap<InetAddress, ConnectionLogNodeStatistic>();

  public void sourceDataChanged(ISourceData data)
  {
    ClientNode node = getConnector().getClientNode(data.getSenderId());
    if ((node != null) && (isRemoteNode(node)))
    {
//      System.out.println("ConnectionLog: Data from "+node.getAddress().getHostAddress());
      addToStatisticsMap(nodeStatistics, node, data);
      removeExpiredEntries(nodeStatistics);
    }
  }
  
  private void addToStatisticsMap(Map<InetAddress, ConnectionLogNodeStatistic> nodeStatistics, ClientNode node, ISourceData data)
  {
    ConnectionLogNodeStatistic ns = nodeStatistics.get(node.getAddress());
    if (ns == null)
    {
      ns = new ConnectionLogNodeStatistic(STATISTICS_MAP_MAX_DURATION, node);
      nodeStatistics.put(node.getAddress(), ns);
    }
    
    ns.addEntry(node, data);
    
  }

  private void removeExpiredEntries(Map<InetAddress, ConnectionLogNodeStatistic> nodeStatistics)
  {
    for (ConnectionLogNodeStatistic ns : nodeStatistics.values())
    {
      ns.removeExpiredEntries();
    }
    
  }
  
  private boolean isRemoteNode(ClientNode node)
  {
    return !getConnector().getMyOwnNode().getNodeId().equals(node.getNodeId());
  }

  private RemoteConnector getRemoteConnector()
  {
    if (remoteConnector == null)
    {
      remoteConnector = ((SimidudeApplicationContext)ApplicationBase.getContext()).getRemoteConnector();
    }
    return remoteConnector;

  }
  
  private IConnector getConnector()
  {
    if (connector == null)
    {
      connector = getRemoteConnector().getConnector();      
    }
    return connector;
  }

  public Map<InetAddress, ConnectionLogNodeStatistic> getNodeStatisticsEntries()
  {
    return Collections.unmodifiableMap(nodeStatistics);
  }
  

}
