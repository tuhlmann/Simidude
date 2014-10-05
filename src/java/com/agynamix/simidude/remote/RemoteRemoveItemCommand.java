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

import java.util.ArrayList;
import java.util.List;

import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.platform.net.AbstractRemoteCommand;
import com.agynamix.platform.net.ClientNode;
import com.agynamix.platform.net.IConnectorServerHandler;
import com.agynamix.simidude.clipboard.IClipboardItem;
import com.agynamix.simidude.clipboard.SourceDataManager;
import com.agynamix.simidude.impl.SimidudeApplicationContext;
import com.agynamix.simidude.infra.ModelProvider;
import com.agynamix.simidude.source.SourceDataStub;

public class RemoteRemoveItemCommand extends AbstractRemoteCommand {

  private static final long serialVersionUID = 1L;

  private final List<ClientNode> connectedClients;
  private final SourceDataStub   sourceDataStub;
  
  public RemoteRemoveItemCommand(List<ClientNode> connectedClients, SourceDataStub sourceDataStub)
  {
    this.connectedClients = connectedClients;
    this.sourceDataStub   = sourceDataStub;
  }

  public Object invoke(IConnectorServerHandler connectorServerHandler)
  {
    SourceDataManager sdm = ((SimidudeApplicationContext)ApplicationBase.getContext()).getSourceDataManager();
    ModelProvider mp = ((SimidudeApplicationContext)ApplicationBase.getContext()).getModelProvider();
    if (sourceDataStub == null)
    {
      sdm.removeAll();
    } else {
      sdm.removeItem(sdm.getClipboardItem(sourceDataStub));
    }
    
    RemoteConnector remoteConnector = ((SimidudeApplicationContext)ApplicationBase.getContext()).getRemoteConnector();
    List<ClientNode> thisPeerConnectedClients = remoteConnector.getConnector().getConnectedClientList();
    List<ClientNode> notYetReachedList = new ArrayList<ClientNode>();
    for (ClientNode client : thisPeerConnectedClients)
    {
      if (!connectedClients.contains(client))
      {
        notYetReachedList.add(client);
      }
    }
    if (notYetReachedList.size() > 0)
    {
      mp.networkRemoveItem(notYetReachedList, sourceDataStub);
    }

    return Boolean.TRUE;
  }

}
