package com.agynamix.platform.log;

import com.agynamix.platform.net.ClientNode;
import com.agynamix.simidude.source.ISourceData;
import com.agynamix.simidude.source.ISourceData.SourceType;

public class NodeStatisticsEntry {

  final SourceType sourceType;
  
  
  public NodeStatisticsEntry(ClientNode node, ISourceData data)
  {
    this.sourceType = data.getType();
  }


  public SourceType getSourceType()
  {
    return sourceType;
  }

}
