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
package com.agynamix.simidude.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.logging.Logger;

import com.agynamix.platform.infra.FileUtils;
import com.agynamix.platform.infra.PlatformUtils;
import com.agynamix.platform.log.ApplicationLog;
import com.agynamix.simidude.infra.CacheManagerFactory;
import com.agynamix.simidude.infra.IContentsCacheInfo;
import com.agynamix.simidude.infra.IContentsCacheManager;
import com.agynamix.simidude.source.SourceDataContents;

/**
 * A simple caching solution for remote files.
 * 
 * @author tuhlmann
 * 
 */
public class ContentsCacheManagerImpl implements IContentsCacheManager {

  Logger log = ApplicationLog.getLogger(ContentsCacheManagerImpl.class);
  
  final String         cacheDirectoryStr;
  final File           cacheDirectory;

  File                 itsFileRoot                 = null;
  File                 firstContentsElementInCache = null;

  /**
   * Stores the part of the path of the file/directory which needs to be replaced by our cache path.
   */
  String               itsCommonSourcePath         = null;
  boolean              isFirstItem                 = true;

  OutputStream         currentOutputStream         = null;

  boolean              insideTransaction           = false;

  public ContentsCacheManagerImpl()
  {
    log.fine("Inside ContentsCacheManager ctor");

    cacheDirectoryStr = PlatformUtils.getApplicationCacheDir();
    cacheDirectory = new File(cacheDirectoryStr);
    if (!cacheDirectory.exists())
    {
      if (!cacheDirectory.mkdirs())
      {
        throw new IllegalStateException("Can not create cache directory " + cacheDirectoryStr);
      }
    }
  }
  
  public synchronized File createTempFile(String suffix) throws IOException
  {
    File file = new File(cacheDirectory.getAbsolutePath() + "/"+ IContentsCacheManager.TEMP_FILE_PREFIX + System.currentTimeMillis()+suffix);
    file.deleteOnExit();    
    return file;
  }

  public synchronized void begin()
  {
    if (insideTransaction)
    {
      throw new IllegalStateException("Already inside a transaction");
    }
    insideTransaction = true;

    itsFileRoot = new File(cacheDirectory.getAbsolutePath() + "/c" + System.currentTimeMillis());
    itsFileRoot.deleteOnExit();
    if (!itsFileRoot.mkdirs())
    {
      throw new IllegalStateException("Cannot create cache directory " + itsFileRoot.getAbsolutePath());
    }
    CacheManagerFactory.addInTransitCacheDirectory(itsFileRoot);
  }

  public synchronized IContentsCacheInfo finish()
  {
    if (!insideTransaction)
    {
      throw new IllegalStateException("Not inside a transaction.");
    }
    insideTransaction = false;

    if (currentOutputStream != null)
    {
      log.fine("OutputStream was open. Should have been closed with endOfFile package!");
      try
      {
        currentOutputStream.close();
        currentOutputStream = null;
      } catch (IOException IGNORE)
      {
      }
    }

    CacheManagerFactory.removeInTransitCacheDirectory(itsFileRoot);
    
    File f = firstContentsElementInCache;
    itsFileRoot = null;
    firstContentsElementInCache = null;

    return f != null ? new ContentsCacheInfo(new Date(), f) : null;
  }

  public synchronized void abort()
  {
    if (insideTransaction)
    {
      if (itsFileRoot != null)
      {
        if (itsFileRoot.exists())
        {
          log.config("Abort transfer for cache dir "+itsFileRoot.getAbsolutePath());
          FileUtils.deleteRecursive(itsFileRoot);
        }
        CacheManagerFactory.removeInTransitCacheDirectory(itsFileRoot);
        itsFileRoot = null;
      }
      insideTransaction = false;
    }
  }

  public synchronized void write(SourceDataContents contents)
  {
    if (isFirstItem)
    {
      itsCommonSourcePath = FileUtils.getCommonSourcePath(contents.getName());
      firstContentsElementInCache = new File(itsFileRoot.getAbsolutePath() + "/"
          + FileUtils.getRelativePath(itsCommonSourcePath, contents.getName()));
      isFirstItem = false;
    }
    if (contents.isFile())
    {
      writeFileToCache(itsFileRoot, contents);
    } else
    {
      writeDirectoryToCache(itsFileRoot, contents);
    }
  }

  private synchronized void writeFileToCache(File fileRoot, SourceDataContents item)
  {
    File file = new File(fileRoot.getAbsolutePath() + "/" + FileUtils.getRelativePath(itsCommonSourcePath, item.getName()));
    file.deleteOnExit();
    writeContentsToFile(file, item);
  }

  /**
   * SourceDataContents element holds a single directory which needs to be created inside the cache root.
   * 
   * @param fileRoot
   * @param item
   * @return
   */
  private synchronized void writeDirectoryToCache(File fileRoot, SourceDataContents item)
  {
    File file = new File(fileRoot.getAbsolutePath() + "/" + FileUtils.getRelativePath(itsCommonSourcePath, item.getName()));
    file.deleteOnExit();
    if (!file.mkdirs())
    {
      throw new IllegalStateException("Cannot create cache directory " + file.getAbsolutePath());
    }
  }

  private void writeContentsToFile(File element, SourceDataContents contents)
  {
    try
    {
      if (currentOutputStream == null)
      {
        currentOutputStream = new BufferedOutputStream(new FileOutputStream(element));
      }
      byte[] buffer = contents.getFileContents();
      if (buffer.length > 0)
      {
        currentOutputStream.write(contents.getFileContents());
      }

      if (contents.isEndOfFile())
      {
        currentOutputStream.flush();
        currentOutputStream.close();
        currentOutputStream = null;
      }
    } catch (IOException e)
    {
      e.printStackTrace();
      throw new IllegalStateException(e);
    }
  }
  
  public void removeContentsFromCache(IContentsCacheInfo cacheInfo)
  {
    if (cacheInfo != null)
    {
      File f = new File(cacheInfo.getFilenameInCache());
      if (f.exists())
      {
        FileUtils.deleteRecursive(f);
      }
    }
  }
  
  public void removeAbortedItem(SourceDataContents contents) {
    if (itsCommonSourcePath != null) {
      File file = new File(itsFileRoot.getAbsolutePath() + "/" + FileUtils.getRelativePath(itsCommonSourcePath, contents.getName()));
      file.delete();
    } // otherwise nothing had been written yet...
  }

}
