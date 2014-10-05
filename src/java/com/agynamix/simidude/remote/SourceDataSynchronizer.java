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
import java.util.logging.Level;
import java.util.logging.Logger;

import com.agynamix.platform.concurrent.ThreadManagerAware;
import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.platform.infra.IQueueManager;
import com.agynamix.platform.log.ApplicationLog;
import com.agynamix.platform.net.ClientNode;
import com.agynamix.platform.net.ConnectionCtx;
import com.agynamix.platform.net.ConnectionUtils;
import com.agynamix.platform.net.IConnector;
import com.agynamix.platform.net.NetworkAuthException;
import com.agynamix.platform.net.NetworkProtocolException;
import com.agynamix.simidude.clipboard.IClipboardItem;
import com.agynamix.simidude.clipboard.SourceDataManager;
import com.agynamix.simidude.impl.SimidudeApplicationContext;
import com.agynamix.simidude.source.ISourceData;
import com.agynamix.simidude.source.SourceDataStub;
import com.agynamix.simidude.source.ISourceData.TransportType;

/**
 * SourceDataSynchronizer wird verwendet, um periodisch die ISourceData-Einträge der verbundenen
 * Simidude-Clients miteinander abzugleichen. Wenn also ein Client eine Zeitlang mit
 * anderen nicht verbunden war, so werden die fehlenden Einträge trotzdem periodisch angestoßen.
 * @author tuhlmann
 *
 */
public class SourceDataSynchronizer implements ThreadManagerAware {

  Logger log = ApplicationLog.getLogger(SourceDataSynchronizer.class);
  
  final RemoteConnector remoteConnector;
  
  public final static int CALL_COUNT_UNTIL_RUN = 6; // runs each minute
  
  List<ClientNode> lastRunClientList = new ArrayList<ClientNode>();
  int callCount = 0;
  
  boolean shouldStop = false;
  
  public SourceDataSynchronizer(RemoteConnector remoteConnector)
  {
    this.remoteConnector = remoteConnector;
  }

  /**
   * Der Executor wird alle 10s gestartet, soll aber nicht immer etwas unternehmen.
   * Die Vorgehensweise ist wie folgt:
   * <ul>
   *   <li>Wenn seit dem letzten Aufruf neue Nodes zu unserer Liste hinzugefügt wurden, dann wird synchronisiert</li>
   *   <li>Wenn keine neuen Nodes hinzugekommen sind, dann wird nur bei jedem X-ten Aufruf etwas unternommen.</li>
   * </ul>
   */
  public void run()
  {
    if (!shouldStop)
    {
      callCount++;
      IConnector connector = remoteConnector.getConnector();
      List<ClientNode> connectedClients = connector.getConnectedClientList();
      ClientNode myOwnNode = remoteConnector.getConnector().getMyOwnNode();
      if (callCount >= CALL_COUNT_UNTIL_RUN)
      {
        callCount = 0;
        synchronizeSourceData(connector, myOwnNode, connectedClients);
      } else {
        if (isNewClientsConnected(lastRunClientList, connectedClients))
        {
//          System.out.println("New clients connected");
          synchronizeSourceData(connector, myOwnNode, connectedClients);
        }
      }
      lastRunClientList.clear();
      lastRunClientList.addAll(connectedClients);
    }    
  }

  /**
   * Go to our peers and get the SourceDataStub items.
   * We only get those that are visible in the Clipboard table, not those that have been cleared out.
   */
  private void synchronizeSourceData(IConnector connector, ClientNode myOwnNode, List<ClientNode> connectedClients)
  {
    for (ClientNode node : connectedClients)
    {
      if ((!node.equals(myOwnNode)) && (!node.isShutdown()))
      {
//        System.out.println("SourceDataSynchronizer: Contact "+node);
        try
        {
          synchronizeSourceData(connector, node);
        } catch (IOException e)
        {
          log.log(Level.FINE, e.getMessage(), e);
        } catch (NetworkAuthException e)
        {
          log.log(Level.WARNING, e.getMessage(), e);
        } catch (NetworkProtocolException e)
        {
          log.log(Level.WARNING, e.getMessage(), e);
        }
      }
    }
    
  }

  /**
   * Synchronize SourceData with a specific peer. Algorithm:
   * <ul>
   *   <li>connect to the node</li>
   *   <li>send a list of our visible SourceDataStubs</li>
   *   <li>the peer sends back two lists:
   *     <ul>
   *       <li>The first List<ISourceData> contains the items we do not yet have</li>
   *       <li>The second List<SourceDataStub> contains the items the peer requests from us.</li>
   *     </ul>
   * </ul>
   * @param node the peer to contact.
   * @throws NetworkAuthException 
   * @throws IOException 
   * @throws NetworkProtocolException 
   */
  @SuppressWarnings("unchecked")
  private void synchronizeSourceData(IConnector connector, ClientNode node) throws IOException, NetworkAuthException, NetworkProtocolException
  {
    ConnectionCtx connectionCtx = ConnectionUtils.connectTo(connector, node);
    SourceDataManager sdm  = ((SimidudeApplicationContext)ApplicationBase.getContext()).getSourceDataManager();
    List<SourceDataStub> mySourceDataEntries        = sdm.getSourceDataStubEntries();
    List<SourceDataStub> myRemovedSourceDataEntries = sdm.getRemovedSourceDataStubEntries();
    CompareSourceDataStubListsResult result = (CompareSourceDataStubListsResult) connectionCtx.invoke(new CompareSourceDataStubListsCommand(mySourceDataEntries, myRemovedSourceDataEntries));
    List<SourceDataStub> stubsForEntriesWeWant = getNeededSourceDataEntryList(sdm, result.getLocallyMissingEntries());
    List<ISourceData> entriesRemoteWants = assembleRemotelyNeededEntries(sdm, result.getRemotelyMissingEntries());
    if ((stubsForEntriesWeWant.size() > 0) || (entriesRemoteWants.size() > 0))
    {
      List<ISourceData> entriesWeWant = (List<ISourceData>) connectionCtx.invoke(new TransmitSourceDataStubListsCommand(stubsForEntriesWeWant, entriesRemoteWants));
      IQueueManager qm = ((SimidudeApplicationContext)ApplicationBase.getContext()).getQueueManager();
      qm.putAllReverse(IQueueManager.QUEUE_SOURCE_DATA_MONITOR, entriesWeWant);
    }
    connectionCtx.disconnect();
  }

  private List<SourceDataStub> getNeededSourceDataEntryList(SourceDataManager sdm, List<SourceDataStub> locallyMissingEntries)
  {
    List<SourceDataStub> stubsForEntriesWeWant = new ArrayList<SourceDataStub>();
    for (SourceDataStub stub : locallyMissingEntries)
    {
      if (sdm.getClipboardItem(stub) == null)
      {
        stubsForEntriesWeWant.add(stub);
      }
    }
    return stubsForEntriesWeWant;
  }

  private List<ISourceData> assembleRemotelyNeededEntries(SourceDataManager sdm, List<SourceDataStub> remotelyMissingEntries)
  {
    List<ISourceData> entriesRemoteWants = new ArrayList<ISourceData>();
    for (SourceDataStub stub : remotelyMissingEntries)
    {
      IClipboardItem item = sdm.getClipboardItem(stub);
      if (item != null)
      {
        ISourceData sourceData = item.getSourceData().copy();
        sourceData.setTransportType(TransportType.remote);
        entriesRemoteWants.add(sourceData);
      } else {
        System.out.println("WARN: No ClipboardItem found for remotely wanted item with stub "+stub);
      }
    }
    return entriesRemoteWants;
  }
  
  /**
   * Checks if new clients have connected since our last run
   * @param lastRunClientList list of clients connected the last time we ran.
   * @param connectedClients connected clients
   * @return true if new clients have connected, false otherwise.
   */
  private boolean isNewClientsConnected(List<ClientNode> lastRunClientList, List<ClientNode> connectedClients)
  {
    for (ClientNode node : connectedClients)
    {
      if (!lastRunClientList.contains(node))
      {
//        System.out.println("SourceDataSynchronizer: New Node detected: "+node);
        return true;
      }
    }
    return false;
  }

  public String getId()
  {
    return "SourceDataSynchronizer";
  }

  public void shutdown()
  {
    shouldStop = true;
  }


}
