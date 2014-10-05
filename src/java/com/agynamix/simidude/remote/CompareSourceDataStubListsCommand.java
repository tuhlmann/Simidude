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
import com.agynamix.platform.net.IConnectorServerHandler;
import com.agynamix.simidude.clipboard.SourceDataManager;
import com.agynamix.simidude.impl.SimidudeApplicationContext;
import com.agynamix.simidude.source.SourceDataStub;

public class CompareSourceDataStubListsCommand extends AbstractRemoteCommand {

  private static final long serialVersionUID = 1L;

  protected final List<SourceDataStub> localSourceDataEntries;
  protected final List<SourceDataStub> removedLocalSourceDataEntries;
  
  public CompareSourceDataStubListsCommand(List<SourceDataStub> localSourceDataEntries, List<SourceDataStub> removedSourceDataEntries)
  {
    this.localSourceDataEntries = localSourceDataEntries;
    this.removedLocalSourceDataEntries = removedSourceDataEntries;
  }

  public Object invoke(IConnectorServerHandler connectorServerHandler)
  {
    SourceDataManager sdm = ((SimidudeApplicationContext)ApplicationBase.getContext()).getSourceDataManager();
    List<SourceDataStub> remoteSourceDataEntries = sdm.getSourceDataStubEntries();
    List<SourceDataStub> removedRemoteSourceDataEntries = sdm.getRemovedSourceDataStubEntries();
    
    CompareSourceDataStubListsResult result = compareStubLists(sdm, localSourceDataEntries, removedLocalSourceDataEntries, 
        remoteSourceDataEntries, removedRemoteSourceDataEntries);
    return result;
  }
  
  private CompareSourceDataStubListsResult compareStubLists(SourceDataManager sdm,
      List<SourceDataStub> localSourceDataEntries, List<SourceDataStub> removedLocalSourceDataEntries, 
      List<SourceDataStub> remoteSourceDataEntries, List<SourceDataStub> removedRemoteSourceDataEntries)
  {
    // First find out the entries we lack
    List<SourceDataStub> locallyMissingEntries = new ArrayList<SourceDataStub>();
    for (SourceDataStub stub : remoteSourceDataEntries)
    {
      if ((!localSourceDataEntries.contains(stub)) && (!removedLocalSourceDataEntries.contains(stub)))
      {
        locallyMissingEntries.add(stub);
      }
    }
    
    // Now find the entries this peer does not have    
    List<SourceDataStub> remotelyMissingEntries = new ArrayList<SourceDataStub>();
    for (SourceDataStub stub : localSourceDataEntries)
    {
      if ((!remoteSourceDataEntries.contains(stub)) && (!removedRemoteSourceDataEntries.contains(stub)))
      {
        remotelyMissingEntries.add(stub);
      }
    }
    
    return new CompareSourceDataStubListsResult(locallyMissingEntries, remotelyMissingEntries);
  }

  public List<SourceDataStub> getLocalSourceDataStubs()
  {
    return this.localSourceDataEntries;
  }

}
