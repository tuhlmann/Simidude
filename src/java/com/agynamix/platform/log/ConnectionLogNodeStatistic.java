package com.agynamix.platform.log;

import java.net.InetAddress;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import com.agynamix.platform.net.ClientNode;
import com.agynamix.simidude.source.ISourceData;
import com.agynamix.simidude.source.ISourceData.SourceType;

public class ConnectionLogNodeStatistic {

  final long statisticsMapMaxDurationMillis;
  
  SortedMap<Long, NodeStatisticsEntry> statisticsEntryMap = new TreeMap<Long, NodeStatisticsEntry>();
  
  final UUID        senderId;
  final InetAddress senderAddress;
  
  final long        firstContactTime;
  
  
  public ConnectionLogNodeStatistic(int statisticsMapMaxDuration, ClientNode node)
  {
    this.statisticsMapMaxDurationMillis = (long)statisticsMapMaxDuration * 3600000l;
    this.senderId = node.getNodeId();
    this.senderAddress = node.getAddress();
    this.firstContactTime = getCurrentTimeMillis();
  }

  public void addEntry(ClientNode node, ISourceData data)
  {
    NodeStatisticsEntry entry = new NodeStatisticsEntry(node, data);
    Long t = getCurrentTimeMillisLong();
    statisticsEntryMap.put(t, entry);
  }

  /**
   * Remove entries that are older than statisticsMapMaxDuration.
   */
  public void removeExpiredEntries()
  {
    long lastTimestamp = getCurrentTimeMillis() - statisticsMapMaxDurationMillis;
    boolean allClear = false;
    while ((!allClear) && (statisticsEntryMap.size() > 0))
    {
      Long lastKey = statisticsEntryMap.lastKey();
      if (lastKey.longValue() < lastTimestamp)
      {
//        System.out.println("Remove Entry with date "+new Date(lastKey.longValue()));
        statisticsEntryMap.remove(lastKey);
      } else {
        allClear = true;
      }
    }
    
  }

  private long getCurrentTimeMillis()
  {
    return System.currentTimeMillis();
  }

  private long getCurrentTimeMillisLong()
  {
    return new Long(getCurrentTimeMillis());
  }

  public int getEntryCountBySourceType(SourceType sourceType)
  {
    removeExpiredEntries();
    
    int count = 0;
    
    for (NodeStatisticsEntry entry : statisticsEntryMap.values())
    {
      if (entry.getSourceType() == sourceType)
      {
        count++;
      }
    }
    
    return count;
  }
  

}
