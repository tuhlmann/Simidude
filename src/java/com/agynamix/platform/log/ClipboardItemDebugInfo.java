package com.agynamix.platform.log;

import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.platform.net.ClientNode;
import com.agynamix.platform.net.IConnector;
import com.agynamix.simidude.clipboard.IClipboardItem;
import com.agynamix.simidude.impl.SimidudeApplicationContext;
import com.agynamix.simidude.source.ISourceData;

public class ClipboardItemDebugInfo {
  
  final IClipboardItem item;
  final ISourceData    sourceData;

  final IConnector     connector;

  public ClipboardItemDebugInfo(IClipboardItem item)
  {
    this.item       = item;
    this.sourceData = item.getSourceData();
    this.connector = ((SimidudeApplicationContext)ApplicationBase.getContext()).getRemoteConnector().getConnector();
  }
  
  @Override
  public String toString()
  {
    ClientNode originatorClientNode = connector.getClientNode(sourceData.getSenderId());
    
    StringBuilder sb = new StringBuilder();
    sb.append("Clipboard Item of Type: "+item.getType()+": "+sourceData.getSourceId()+"\n");
    
    if (originatorClientNode != null)
    {
      sb.append("Originated from Client with IP "+originatorClientNode.getAddress().getHostAddress()+" ("+sourceData.getSenderId()+")\n");
    } else {    
      sb.append("Originated from not connected client with UUID: "+sourceData.getSenderId()+"\n");
    }
    
    sb.append("Item Transport Type: "+sourceData.getTransportType()+"\n");
    sb.append("Item created: "+sourceData.getCreationDate()+"\n");
    sb.append("\nItem Short description\n");
    sb.append("======================\n");
    sb.append(item.getShortDescription()+"\n\n");
    return sb.toString();
  }

}
