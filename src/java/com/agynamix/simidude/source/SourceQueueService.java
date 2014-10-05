/*
 * Copyright by AGYNAMIX(R). All rights reserved. 
 * This file is made available under the terms of the
 * license this product is released under.
 * 
 * For details please see the license file you should have
 * received, or go to:
 * 
 * http://www.agynamix.de
 */

package com.agynamix.simidude.source;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.agynamix.platform.concurrent.AbstractService;
import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.platform.infra.IQueueManager;
import com.agynamix.simidude.impl.SimidudeApplicationContext;


public class SourceQueueService extends AbstractService {

  public static final String SERVICE_NAME = "SourceDispatcher";

  IQueueManager queueManager;
  
  List<ISourceDataListener> listeners = new CopyOnWriteArrayList<ISourceDataListener>();
  
  public SourceQueueService(String serviceId)
  {
    super(serviceId);
  }
  
  public void addSourceDataListener(ISourceDataListener listener)
  {
    listeners.add(listener);
  }
  
  public void removeSourceDataListener(ISourceDataListener listener)
  {
    listeners.remove(listener);
  }
  
  @Override
  protected void internalInitialize()
  {
    queueManager = ((SimidudeApplicationContext)ApplicationBase.getContext()).getQueueManager();
    ApplicationBase.getContext().registerService(SourceQueueService.SERVICE_NAME, this);
  }

  @Override
  protected void internalRun() throws InterruptedException
  {
    ISourceData data = (ISourceData) queueManager.take(IQueueManager.QUEUE_SOURCE_DATA_MONITOR);
    // notify listeners
    if (data != null)
    {
      for (ISourceDataListener listener : listeners)
      {
        listener.sourceDataChanged(data);
      }
    }
  }

  @Override
  protected void preRunLoop()
  {
  }

}
