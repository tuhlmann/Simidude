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
import java.util.logging.Logger;

import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.platform.infra.IQueueManager;
import com.agynamix.platform.log.ApplicationLog;
import com.agynamix.platform.net.AbstractRemoteCommand;
import com.agynamix.platform.net.IConnectorServerHandler;
import com.agynamix.simidude.clipboard.IClipboardItem;
import com.agynamix.simidude.clipboard.SourceDataManager;
import com.agynamix.simidude.impl.SimidudeApplicationContext;
import com.agynamix.simidude.source.ISourceData;
import com.agynamix.simidude.source.SourceDataStub;
import com.agynamix.simidude.source.ISourceData.TransportType;

public class TransmitSourceDataStubListsCommand extends AbstractRemoteCommand {

  private static final long serialVersionUID = 1L;
  
  final List<SourceDataStub> stubsForEntriesWeWant;
  final List<ISourceData> entriesRemoteWants;
  
  public TransmitSourceDataStubListsCommand(List<SourceDataStub> stubsForEntriesWeWant, List<ISourceData> entriesRemoteWants)
  {
    this.stubsForEntriesWeWant = stubsForEntriesWeWant;
    this.entriesRemoteWants    = entriesRemoteWants;
  }

  public Object invoke(IConnectorServerHandler connectorServerHandler)
  {
    SourceDataManager sdm = ((SimidudeApplicationContext)ApplicationBase.getContext()).getSourceDataManager();
    List<ISourceData> entriesWeWant = new ArrayList<ISourceData>();
    for (SourceDataStub stub : stubsForEntriesWeWant)
    {
      IClipboardItem item = sdm.getClipboardItem(stub);
      if (item != null)
      {
        ISourceData sourceData = item.getSourceData().copy();
        sourceData.setTransportType(TransportType.remote);
        entriesWeWant.add(sourceData);
      } else {
        getLogger().warning("WARN: No ClipboardItem found for stub "+stub);
      }
    }
    if (entriesRemoteWants.size() > 0)
    {
      IQueueManager qm = ((SimidudeApplicationContext)ApplicationBase.getContext()).getQueueManager();
      qm.putAllReverse(IQueueManager.QUEUE_SOURCE_DATA_MONITOR, entriesRemoteWants);
    }
    return entriesWeWant;
  }

  private Logger getLogger()
  {
    return ApplicationLog.getLogger(TransmitSourceDataStubListsCommand.class);
  }
}
