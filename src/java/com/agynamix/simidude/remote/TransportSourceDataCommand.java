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

import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.platform.infra.IQueueManager;
import com.agynamix.platform.net.AbstractRemoteCommand;
import com.agynamix.platform.net.IConnectorServerHandler;
import com.agynamix.simidude.impl.SimidudeApplicationContext;
import com.agynamix.simidude.source.ISourceData;

public class TransportSourceDataCommand extends AbstractRemoteCommand {

  private static final long serialVersionUID = 1L;

  final ISourceData sourceData;
  
  public TransportSourceDataCommand(ISourceData sourceData)
  {
    this.sourceData = sourceData;
  }

  public Object invoke(IConnectorServerHandler connectorServerHandler)
  {
    IQueueManager qm = ((SimidudeApplicationContext)ApplicationBase.getContext()).getQueueManager();
    qm.put(IQueueManager.QUEUE_SOURCE_DATA_MONITOR, sourceData);
    return new Boolean(true);
  }

}
