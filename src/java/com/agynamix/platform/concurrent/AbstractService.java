/*
 * Class AbstractDispatcher
 * Created on 15.02.2005
 * 
 * This file is copyrighted by AGYNAMIX.
 * Please refer to the license of our products for details.
 */
package com.agynamix.platform.concurrent;

import java.util.logging.Logger;

import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.platform.log.ApplicationLog;



/**
 * Base class for all Dispatchers. This class provides a Runnable implementation
 * to run in a thread of it's own.
 * 
 * @author tuhlmann
 */
public abstract class AbstractService implements ThreadManagerAware {

  protected final String    serviceId;
  protected boolean         stopped;
  
  private   boolean         isInitialized = false;

  Logger log = ApplicationLog.getLogger(AbstractService.class);
  
  public AbstractService(String serviceId)
  {
    this.serviceId = serviceId;
  }
  
  public String getId() {
    return this.serviceId;
  }
  
  public synchronized void initialize() {
    if (!isInitialized) {
      internalInitialize();
      ApplicationBase.getContext().getThreadManager().registerNew(this);
      isInitialized = true;
    }
  }
  
  /**
   * internalInitialize() is called by this Objects initialize() method
   *
   */
  protected abstract void internalInitialize();

  public void shutdown() {
    this.stopped = true;
  }
  
  /**
   * internalRun is called by this objects run() method.
   * It is embedded in a loop so the subclass does not have to care
   * about implementing a sane Thread loop.
   * 
   * @throws InterruptedException
   */
  protected abstract void internalRun() throws InterruptedException;
  
  /**
   * Everything that should be executed during run, but only once.
   *
   */
  protected abstract void preRunLoop();
  
  /**
   * @see java.lang.Runnable#run()
   */
  public void run() {
    preRunLoop();
    stopped = false;
    while (!stopped) {
      try {
        internalRun();
      } catch (InterruptedException e) {
      }
    }
    log.config("Exiting run() loop: "+this.getClass().getName());
  }  
  
  public void setStopped(boolean stopped)
  {
    this.stopped = stopped;
  }
  
}
