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

package com.agynamix.simidude.source.impl;

import com.agynamix.platform.concurrent.AbstractService;
import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.platform.infra.IQueueManager;
import com.agynamix.simidude.impl.SimidudeApplicationContext;
import com.agynamix.simidude.source.ISource;
import com.agynamix.simidude.source.ISourceData;



public class SourceListener extends AbstractService {

  final ISource source;
  IQueueManager queueManager;
  
  final int           sleepTime;
  
  public SourceListener(String serviceId, ISource source)
  {
    super(serviceId);
    this.source = source;
    this.sleepTime = source.getSleepTime();
  }

  @Override
  protected void internalInitialize()
  {
    queueManager = ((SimidudeApplicationContext)ApplicationBase.getContext()).getQueueManager();
  }

  @Override
  protected void internalRun() throws InterruptedException
  {
//    System.out.println("Check SourceListener...");
    if (source.isDataAvailable())
    {
      ISourceData[] sourceData = source.getData();
      ISourceData s = sourceData[0];
//      System.out.println("ITEM: "+s.getText()+"("+DateUtils.date2string("HH:mm:ss SSS", s.getCreationDate())+")");
      queueManager.putAll(IQueueManager.QUEUE_SOURCE_DATA_MONITOR, sourceData);
    }
    Thread.sleep(this.sleepTime);
  }

  @Override
  protected void preRunLoop()
  {
  }

}
