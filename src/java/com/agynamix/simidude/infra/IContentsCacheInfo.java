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

import java.io.Serializable;

/**
 * Holds information about cached files/directories
 * @author tuhlmann
 *
 */
public interface IContentsCacheInfo extends Serializable {

  /**
   * @return the filename that was given to this item in the file cache.
   */
  String getFilenameInCache();

  boolean isDirectory();

}
