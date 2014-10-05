package com.agynamix.platform.net;

import java.net.InetAddress;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.agynamix.platform.frontend.preferences.IPreferenceConstants;
import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.platform.log.ConnectionLog;
import com.agynamix.platform.log.ConnectionLogNodeStatistic;
import com.agynamix.simidude.impl.SimidudeApplicationContext;
import com.agynamix.simidude.remote.RemoteConnector;
import com.agynamix.simidude.source.ISourceData.SourceType;

public class NetworkAnalyzer {
  
  List<InetAddress> myAddresses;
  InetAddress       primaryAddress;
  InetAddress       customPrefsAddress;
  
  int               helloPort;
  int               serverPort;
  int               httpPort;
  
  boolean           isHttpEnabled;
  
  Throwable         exceptionOcured = null;
  
  RemoteConnector   remoteConnector;
  IConnector        connector;
  
  ConnectionLog     connectionLog;

  Map<ClientNode, Boolean>  probedClients = null;
  
  Date timeNow;

  public void run()
  {
    try {
      
      timeNow = new Date();
      
      remoteConnector = ((SimidudeApplicationContext)ApplicationBase.getContext()).getRemoteConnector();
      connector       = remoteConnector.getConnector();
      
      myAddresses        = NetUtils.getHostAddresses();
      primaryAddress     = NetUtils.getPrimaryHostAddress();
      customPrefsAddress = NetUtils.getCustomAddressFromPreferences();
      
      helloPort     = connector.getHelloPort();
      serverPort    = connector.getServerPort();
      isHttpEnabled = ApplicationBase.getContext().getConfiguration().getBoolean(IPreferenceConstants.START_HTTP_SERVER);
      httpPort      = ApplicationBase.getContext().getConfiguration().getInteger(IPreferenceConstants.HTTP_SERVER_PORT);
      
      connectionLog = (ConnectionLog) ApplicationBase.getContext().getService(ConnectionLog.SERVICE_NAME);
      
      probedClients = probeClientNodes(connector.getConnectedClientList());
      
    } catch (Exception e) {
      exceptionOcured = e;
    }
  }

  public String asString()
  {
    StringBuilder sb = new StringBuilder();
    
    sb.append("Information about this machine:\n");
    sb.append("===============================\n\n");    
    sb.append(infoAboutMyIpAddress());  
    sb.append(infoAboutCommunicationPorts());
    sb.append("\n\n");
    
    sb.append(infoAboutConnectedClients());
    
    
    sb.append(connectionLogAsString(connectionLog));

    sb.append("\n");
    
    return sb.toString();
  }

  private String infoAboutMyIpAddress()
  {
    StringBuilder sb = new StringBuilder();

    if (customPrefsAddress != null)
    {
      sb.append("IP address you entered in Network Prefs: "+customPrefsAddress.getHostAddress()+"\n");
      if (primaryAddress == null)
      {
        sb.append("INTERNAL PROBLEM: Primary IP address is NULL\n");
      } else {
        if (!primaryAddress.equals(customPrefsAddress))
        {
          sb.append("PROBLEM: The primary address does not match the address you have entered: "+primaryAddress.getHostAddress()+"\n");
        }
      }
      if (myAddresses == null)
      {
        sb.append("INTERNAL PROBLEM: My IP addresses list is NULL\n");
      } else {
        if (myAddresses.size() > 1)
        {
          sb.append("PROBLEM: My IP addresses contains more addresses then the one you entered:\n");
          sb.append(listAddresses(myAddresses)).append("\n");
        } else if (myAddresses.size() == 1) {
          if (!myAddresses.get(0).equals(customPrefsAddress))
          {
            sb.append("PROBLEM: The address from my addresses does not match the address you have entered: "+primaryAddress.getHostAddress()+"\n");            
          }
        } else {
          sb.append("PROBLEM: My IP addresses list is empty!\n");
        }
      }      
    } else {
      if (primaryAddress == null)
      {
        sb.append("INTERNAL PROBLEM: Primary IP address is NULL\n");
      } else {
        sb.append("Primary IP address: "+primaryAddress.getHostAddress()+"\n");
      }      
      if (myAddresses == null)
      {
        sb.append("INTERNAL PROBLEM: My IP addresses list is NULL\n");
      } else {
        sb.append("My IP addresses list:\n");
        sb.append(listAddresses(myAddresses)).append("\n");
      }      
    }   
    return sb.toString();
  }
  
  private String infoAboutCommunicationPorts()
  {
    StringBuilder sb = new StringBuilder();
    
    sb.append("Communication Ports:\n");
    sb.append("====================\n\n");
    sb.append("Broadcast Port:           "+helloPort+"\n");
    sb.append("Communication Port:       "+serverPort+"\n");
    if (isHttpEnabled)
    {
      sb.append("HTTP access enabled at: ");
      sb.append("http://" + NetUtils.getLocalHostAddress() + ":" + httpPort);
    } else {
      sb.append("HTTP access disabled\n");
    }
    
    
    return sb.toString();
  }  

  private String infoAboutConnectedClients()
  {
    StringBuilder sb = new StringBuilder();
    
    if (connector.getConnectedClientList().size() > 1) // our own node is stored in here as well
    {
      sb.append("Connected Clients:\n");
      sb.append("==================\n\n");
    }
    
    for (ClientNode node : connector.getConnectedClientList())
    {
      if (!connector.isMyOwnNode(node))
      {
        sb.append("IP: "+node.getAddress().getHostAddress()+ " ("+node.getAddress().getHostName()+"), ID: "+node.getNodeId()+"\n");
        sb.append("Connected Since: "+node.getCreationDate()  +"\n");
        if (probedClients.get(node) != null)
        {
          if (probedClients.get(node) == true)
          {
            sb.append("Connection to the client successfully established.\n");                  
          } else {
            sb.append("Could not connect to the client.\n");                            
          }
        } else {
          sb.append("No Connection info for that client available.\n");
        }
        if (node.getInactiveSince() != null)
        {
          sb.append("Inactive Since:  "+calculateTimeDifference(timeNow, node.getInactiveSince())+" (Since: "+node.getInactiveSince() +")\n");
        }
        if (node.getShutdownDate() != null)
        {
          sb.append("Shutdown Date:   "+node.getShutdownDate()  +"\n");
        }
        if ((node.getInactiveSince() == null) && (node.getShutdownDate() == null))
        {
          sb.append("Client seems to be active.\n");
        }
        sb.append("\n");
      }
    }
    
    return sb.toString();
  }  
  
  private String calculateTimeDifference(Date timeNow, Date eventTime)
  {
    long diffMillis = timeNow.getTime() - eventTime.getTime();
    long diffSec = diffMillis / 1000;
    long diffMin = diffSec / 60;
    long diffHour = diffMin / 60;
    
    int sec  = (int)diffSec % 60;
    int min  = (int)diffMin % 60;
    int hour = (int)diffHour % 60;
    
    return hour+":"+min+":"+sec+"s";
  }

  private String listAddresses(List<InetAddress> addresses)
  {
    boolean first = true;
    StringBuilder sb = new StringBuilder();
    if (addresses != null)
    {
      for (InetAddress a : addresses)
      {
        if (!first)
        {
          sb.append(", ");
        } else {
          first = false;
        }
        sb.append(a.getHostAddress());
      }
    }
    return sb.toString();
  }
  
  private Object connectionLogAsString(ConnectionLog connectionLog)
  {
    StringBuilder sb = new StringBuilder();
    
    Map<InetAddress, ConnectionLogNodeStatistic> nodeStatisticsEntries = connectionLog.getNodeStatisticsEntries();
    
    if (nodeStatisticsEntries.size() > 0)
    {    
      sb.append("Connection History:\n");
      sb.append("===================\n\n");
      
      for (InetAddress address : nodeStatisticsEntries.keySet())
      {
        ConnectionLogNodeStatistic nodeStatistic = nodeStatisticsEntries.get(address);
        
        sb.append(connectionLogEntryAsString(address, nodeStatistic));
        
      }
    }
    return sb.toString();
  }

  private String connectionLogEntryAsString(InetAddress address, ConnectionLogNodeStatistic nodeStatistic)
  {
    StringBuilder sb = new StringBuilder();
    
    sb.append("IP: "+address.getHostAddress()+"\n");
    sb.append("-------------------\n");
    
    int textClips  = nodeStatistic.getEntryCountBySourceType(SourceType.TEXT);
    int imageClips = nodeStatistic.getEntryCountBySourceType(SourceType.IMAGE);
    int fileClips  = nodeStatistic.getEntryCountBySourceType(SourceType.FILE);
    
    sb.append("Received (during last 12 hours):\n");
    sb.append("Text Clips:                     "+textClips+"\n");
    sb.append("Image Clips:                    "+imageClips+"\n");
    sb.append("File/Directory Clips:           "+fileClips+"\n");
    
    sb.append("\n");
    
    return sb.toString();
  }

  private Map<ClientNode, Boolean> probeClientNodes(List<ClientNode> connectedClientList)
  {
    Map<ClientNode, Boolean> probedClients = new HashMap<ClientNode, Boolean>();
    for (ClientNode node : connectedClientList)
    {
      if (!connector.isMyOwnNode(node))
      {
        // Connect to Client and disconnect
        try
        {
          ConnectionCtx ctx = ConnectionUtils.connectTo(connector, node);
          ctx.close();
          probedClients.put(node, true);
        } catch (Exception couldNotConnect)
        {
          probedClients.put(node, false);          
        }
      }
    }
    return probedClients;
  }

  public void close()
  {
    probedClients = null;
  }



}
