/*
 * Class ThreadManager
 * Created on 31.01.2005
 * 
 * This file is copyrighted by AGYNAMIX.
 * Please refer to the license of our products for details.
 */
package com.agynamix.platform.concurrent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import com.agynamix.platform.log.ApplicationLog;

/**
 * Class is a pin point for the created threads.
 * They are gonna be registered here.
 * 
 * @author  tuhlmann
 * @created 31.01.2005
 */
public class ThreadManager {

  public static final int MaxThreads = 50;

  public static final String SERVICE_NAME = "ThreadManager";
  
  List<ThreadManagerAware> processList = new CopyOnWriteArrayList<ThreadManagerAware>();
  
  ExecutorService service;
  
  Logger log = ApplicationLog.getLogger(ThreadManager.class);
  
  public ThreadManager() {
    initialize();
  }
  
  public void initialize() {
    service = Executors.newFixedThreadPool(MaxThreads);
  }
  
  /**
   * @param process
   */
  public void registerNew(ThreadManagerAware process) {
//    System.out.println("Register process: "+process.getClass().getName());
    if (!processList.contains(process))
    {
//      System.out.println("Start new thread for "+process.getClass().getName());
      processList.add(process);
    }
  }
  
  /**
   * Call this method to really start the registered processed.
   */
  public void startRegisteredServices() {
//    System.out.println("Start registered services");
    for (ThreadManagerAware proc : processList) 
    {
      service.execute(proc);
    }
  }
  
  public void shutdownNow() {
    log.info("Shutdown ThreadManager");
    for (ThreadManagerAware proc : processList) 
    {
      log.config("Shutdown Object: "+proc.getId());
      proc.shutdown();
    }
    service.shutdownNow();
  }

  /**
   * @see org.springframework.beans.factory.DisposableBean#destroy()
   */
  public void destroy() throws Exception {
    shutdownNow();
  }
  
}
