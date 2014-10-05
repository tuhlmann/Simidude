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

package com.agynamix.platform.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.agynamix.platform.infra.IQueueManager;

@SuppressWarnings("unchecked")
public class QueueManagerImpl implements IQueueManager {
  
  public final static int QUEUE_MAX_CAPACITY = 500;

  Map<String, BlockingQueue> queueMap = new HashMap<String, BlockingQueue>(); 
  
//  BlockingQueue<ISourceData> queue = new LinkedBlockingQueue<ISourceData>();
  
  public QueueManagerImpl()
  {
  }
  
  public void put(String queueName, Object o)
  {
    if (o != null)
    {
      BlockingQueue queue = getQueueByName(queueName);
      try
      {
        queue.put(o);
      } catch (InterruptedException e)
      {
//        e.printStackTrace();
      }
    }
  }

  public void putAll(String queueName, List<?> items)
  {
    if (items != null)
    {
      for (Object item : items)
      {
        put(queueName, item);
      }
    }
  }

  public void putAllReverse(String queueName, List<?> items)
  {
    if (items != null)
    {
      for (int i = items.size()-1; i >= 0; i--)
      {
        put(queueName, items.get(i));        
      }
    }
  }
  
  public void putAll(String queueName, Object[] items)
  {
    if (items != null)
    {
      for (Object item : items)
      {
        put(queueName, item);
      }
    }
  }
  
  public Object take(String queueName)
  {
    BlockingQueue queue = getQueueByName(queueName);
    try
    {
      return queue.take();
    } catch (InterruptedException e)
    {
      return null;
    }
  }
  
  public void emptyQueue(String queueName)
  {
    BlockingQueue queue = getQueueByName(queueName);
    queue.clear();
  }
  
  private BlockingQueue<? extends Object> getQueueByName(String queueName)
  {
    BlockingQueue<? extends Object> queue = queueMap.get(queueName);
    if (queue == null)
    {
      queue = new LinkedBlockingQueue<Object>(QUEUE_MAX_CAPACITY);
      queueMap.put(queueName, queue);
    }
    return queue;
  }

}
