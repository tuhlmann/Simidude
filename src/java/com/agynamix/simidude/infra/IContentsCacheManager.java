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
package com.agynamix.simidude.infra;

import java.io.File;
import java.io.IOException;

import com.agynamix.simidude.source.SourceDataContents;

public interface IContentsCacheManager {

  String SERVICE_NAME     = "ContentsCacheManager";
  String TEMP_FILE_PREFIX = "ClipText_";

  /**
   * Begin a transaction in which multiple write calls can occur to write
   * received data packets into the cache.
   */
  void begin();

  /**
   * Take a package of received contents and write it to the cache.
   * @param contents received contents. This might not be a complete file but just part of it.
   */
  void write(SourceDataContents contents);


  /**
   * Ends the current transaction
   * @return a IContentsCacheInfo object that represents the stored data
   */
  IContentsCacheInfo finish();
  
  /**
   * Remove the currently written information if the transaction has not been completed, i.e. the finish()
   * method has not yet been called. 
   */
  void abort();

  File createTempFile(String suffix) throws IOException;

  /**
   * Remove contents from the cache
   * @param cacheInfo the element that references the previously loaded contents.
   */
  void removeContentsFromCache(IContentsCacheInfo cacheInfo);
  
  /**
   * Remove content from the cache when a file or directory transaction was aborted.
   * @param content the content to remove.
   */
  void removeAbortedItem(SourceDataContents content);

}
