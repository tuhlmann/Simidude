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
package com.agynamix.platform.net;

import java.io.IOException;

import com.agynamix.simidude.source.SourceDataContents;


public interface IConnectorServerHandler {

  /**
   * Execute a Runnable in a separate thread.
   * @param task the runnable to execute
   */
  void executeParallel(Runnable task);

  /**
   * Send DATA packages to the connected peer.
   * @param contents the package to send
   */
  public void sendSourceDataContents(SourceDataContents contents) throws IOException;

  /**
   * Send an exception that occured on this side over to the peer.
   * @throws IOException 
   */
  void sendException(Exception exception) throws IOException;

}
