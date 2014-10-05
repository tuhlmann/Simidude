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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.agynamix.platform.infra.PlatformUtils;
import com.agynamix.simidude.impl.CacheCleaner;
import com.agynamix.simidude.impl.ContentsCacheManagerImpl;

public class CacheManagerFactory {
  
  static IContentsCacheManager commonCacheManager = null;
  
  static List<File> inTransitDirectories = new CopyOnWriteArrayList<File>();

  /**
   *    
   * For now there is only one cache manager implementation.
   * @param sourceData 
   * @return
   */
  public static IContentsCacheManager newContentsCacheManager()
  {
    return new ContentsCacheManagerImpl();
  }

  public static IContentsCacheManager commonContentsCacheManager()
  {
    if (commonCacheManager == null)
    {
      commonCacheManager = new ContentsCacheManagerImpl();      
    }
    return commonCacheManager;
  }
  
  
  public static File createTempFile(String suffix) throws IOException
  {
    return commonContentsCacheManager().createTempFile(suffix);
  }
  
  /**
   * Removes the contents references by this IContentsCacheInfo from the Simidude cache.
   * @param cacheInfo the cacheInfo element that points to the local cache.
   */
  public static void removeContentsFromCache(IContentsCacheInfo cacheInfo)
  {
    commonContentsCacheManager().removeContentsFromCache(cacheInfo);
  }
  
  /**
   * Is called at the start of the application.
   * It will start a thread that runs in the background and deletes all cache directories that
   * are left overs from an old run
   */
  public static Runnable newCacheCleaner()
  {
    return new CacheCleaner(PlatformUtils.getApplicationCacheDir());
  }

  /**
   * Adds a directory to the in-transit-directory list.
   * In-Transit directories are those that just have been created and are being filled
   * with data, but the directory has not yet been assigned to an ISourceData entry.
   * @param directory
   */
  public static void addInTransitCacheDirectory(File directory)
  {
    inTransitDirectories.add(directory);    
  }

  /**
   * When CacheContents has been downloaded the directory is removed from the In-Transit list
   * of directories.
   * @param directory
   */
  public static void removeInTransitCacheDirectory(File directory)
  {
    inTransitDirectories.remove(directory);    
  }

  /**
   * Checks if a given directory is currently in transit.
   * @param directory the directory to check
   * @return true if the directory is in state of transit, false otherwise.
   */
  public static boolean isInTransitCacheDirectory(File directory)
  {
    return inTransitDirectories.contains(directory);
  }

}
