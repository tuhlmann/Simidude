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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.platform.infra.IQueueManager;
import com.agynamix.platform.net.ClientNode;
import com.agynamix.platform.net.ConnectionCtx;
import com.agynamix.platform.net.ConnectionUtils;
import com.agynamix.platform.net.IConnector;
import com.agynamix.platform.net.NetworkAuthException;
import com.agynamix.platform.net.NetworkProtocolException;
import com.agynamix.simidude.impl.SimidudeApplicationContext;
import com.agynamix.simidude.source.ISourceData;

/**
 * Task is used to retrieve source data from other connected peers.
 * Once a connection to other peers is established this task is started in a seperate thread
 * to retrieve source data items from all other instances, merge them together and
 * populate the source data table. 
 * @author tuhlmann
 *
 */
public class RetrieveExistingSourceDataTask implements Callable<Boolean> {

  final IConnector connector;
  
  public RetrieveExistingSourceDataTask(IConnector connector)
  {
    this.connector = connector;
  }

  public Boolean call() throws Exception
  {
//    System.out.println("RETRIEVE existing SourceData");
    try {
      retrieveExistingSourceData();
      return Boolean.TRUE;
    } catch (Exception e)
    {
      e.printStackTrace();
      return Boolean.FALSE;
    }
  }
  
  /**
   * Doing:
   * Nachdem eine Node eine Clientliste gesendet hat, diese Funktion für jeden Knoten
   * (außer dem eigenen) aufrufen.
   * @throws IOException 
   * @throws NetworkProtocolException 
   * @throws NetworkAuthException 
   */
  public void retrieveExistingSourceData() throws IOException, NetworkProtocolException, NetworkAuthException
  {
    List<ISourceData> completeItemList = new ArrayList<ISourceData>();
    List<ClientNode> clients = connector.getConnectedClientList();
    for (ClientNode node : clients)
    {
      if ((!node.equals(connector.getMyOwnNode())) && (!node.isShutdown()))
      {
//        System.out.println("RETRIEVE: get SourecData from "+node);
        ConnectionCtx connectionCtx = ConnectionUtils.connectTo(connector, node);
        RetrieveExistingSourceDataCommand cmd = new RetrieveExistingSourceDataCommand();
        List<ISourceData> nodeItems = (List<ISourceData>) connectionCtx.invoke(cmd);
        mergeNodeItems(completeItemList, nodeItems);
      }
    }
    IQueueManager qm = ((SimidudeApplicationContext)ApplicationBase.getContext()).getQueueManager();
//    System.out.println("retrieveExistingSourceData");
    qm.putAllReverse(IQueueManager.QUEUE_SOURCE_DATA_MONITOR, completeItemList);
  }

  private void mergeNodeItems(List<ISourceData> completeItemList, List<ISourceData> nodeItems)
  {
    for (ISourceData data : nodeItems)
    {
      if (!listContains(completeItemList, data))
      {
        completeItemList.add(data);
      }
    }
  }

  private boolean listContains(List<ISourceData> completeItemList, ISourceData sourceData)
  {
    for (ISourceData data : completeItemList)
    {
      if (data.getSourceId().equals(sourceData.getSourceId()))
      {
        return true;
      }
    }
    return false;
  }
  


}
