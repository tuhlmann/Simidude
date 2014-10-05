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

package com.agynamix.platform.infra;

import java.util.List;


public interface IQueueManager {

  String SERVICE_NAME = "QueueManager";

  /**
   * The queue where new ISourceData items are put in and taken from
   */
  String QUEUE_SOURCE_DATA_MONITOR  = "sourceDataMonitorQueue";
  
  /**
   * The queue for the RemoteServer to sync on incoming SourceDataContents objects from clients.
   */
  String QUEUE_SOURCE_DATA_CONTENTS = "sourceDataContentsQueue";

  /**
   * Put new data into the Queue
   * @param sourceData
   */
  void put(String queueName, Object data);
  
  void putAll(String queueName, List<?> items);

  void putAll(String queueName, Object[] items);

  /**
   * Put all received items in the queue but in reverse order. The last item
   * ist put first in the queue.
   * @param queueName
   * @param items
   */
  void putAllReverse(String queueName, List<?> items);

  Object take(String queueName);

  /**
   * Empty the queue with the given name.
   * @param queueName name of the queue that should be empty.
   */
  void emptyQueue(String queueName);



}
