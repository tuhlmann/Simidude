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

import java.io.Serializable;
import java.util.List;

import com.agynamix.simidude.source.SourceDataStub;

public class CompareSourceDataStubListsResult implements Serializable {

  private static final long serialVersionUID = 1L;

  protected final List<SourceDataStub>    locallyMissingEntries;
  protected final List<SourceDataStub> remotelyMissingEntries;
  
  
  public CompareSourceDataStubListsResult(List<SourceDataStub> locallyMissingEntries, List<SourceDataStub> remotelyMissingEntries)
  {
    this.locallyMissingEntries = locallyMissingEntries;
    this.remotelyMissingEntries = remotelyMissingEntries;
  }
  
  public List<SourceDataStub> getLocallyMissingEntries()
  {
    return this.locallyMissingEntries;
  }
  
  public List<SourceDataStub> getRemotelyMissingEntries()
  {
    return this.remotelyMissingEntries;
  }

}
