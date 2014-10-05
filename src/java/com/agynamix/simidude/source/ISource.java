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


public interface ISource {

  /**
   * 
   * @return true if new data is available at the source, false otherwise
   */
  boolean isDataAvailable();

  /**
   * 
   * @return retrieve the new data from the source.
   */
  ISourceData[] getData();

  /**
   * Tells the Listener how long it should sleep between calls to "isDataAvailable()".
   * @return sleep time in milliseconds
   */
  int getSleepTime();

}
