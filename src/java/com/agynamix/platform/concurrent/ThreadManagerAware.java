/*
 * Class ThreadManagerAware
 * Created on 06.02.2005
 * 
 * This file is copyrighted by AGYNAMIX.
 * Please refer to the license of our products for details.
 */
package com.agynamix.platform.concurrent;



/**
 * @author  tuhlmann
 * @created 06.02.2005
 */
public interface ThreadManagerAware extends Runnable {
  
  /**
   * This method is called by the ThreadManager prior to shutting down the started threads.
   *
   */
  public void shutdown();
  
  public String getId();

}

