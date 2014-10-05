package com.agynamix.simidude.impl;

import java.io.File;
import java.util.logging.Logger;

import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.platform.infra.FileUtils;
import com.agynamix.platform.log.ApplicationLog;
import com.agynamix.simidude.clipboard.IClipboardItem;
import com.agynamix.simidude.clipboard.SourceDataManager;
import com.agynamix.simidude.infra.CacheManagerFactory;
import com.agynamix.simidude.infra.IContentsCacheManager;
import com.agynamix.simidude.source.ISourceData;

public class CacheCleaner implements Runnable {

  private final String cacheRoot;
  
  Logger log = ApplicationLog.getLogger(CacheCleaner.class);

  public CacheCleaner(String cacheRoot)
  {
    this.cacheRoot = cacheRoot;
  }
  
  /**
   * For each directory in cache:
   * <ul>
   *   <li>check if it is referenced from any Simidude entry. If not it can be deleted.
   * </ul>
   */
  public void run()
  {
    SourceDataManager sdm = ((SimidudeApplicationContext)ApplicationBase.getContext()).getSourceDataManager();
    File root = new File(cacheRoot);
    if (root.exists())
    {      
      File[] children = root.listFiles();
      for (File child : children)
      {
        if (child.isDirectory())
        {
          log.fine("Check dir "+child.getAbsolutePath());
          if ((isCacheEntryDirectory(child)) && (!CacheManagerFactory.isInTransitCacheDirectory(child)))
          {
            if (!isReferenced(sdm, child.getAbsolutePath()))
            {
              removeDirectory(child);
            }
          }
        } else {
          // check if we have a temp file here
          if (isCacheEntryFile(child))
          {
            if (!isReferenced(sdm, child.getAbsolutePath()))
            {
              removeFile(child);
            }
          }
        }
      }
    } else {
      log.info(cacheRoot+" does not exist");
    }
  }

  private boolean isCacheEntryDirectory(File cacheDirectory)
  {
    String name = cacheDirectory.getName();
    return name.matches("^c\\d+$");
  }
  
  private boolean isCacheEntryFile(File cacheFile)
  {
    String name = cacheFile.getName();
    if (name.matches(IContentsCacheManager.TEMP_FILE_PREFIX+"\\d+\\..+"))
    {
      return true;
    } else {
      return false;
    }
  }

  private boolean isReferenced(SourceDataManager sdm, String cacheEntry)
  {
    for (IClipboardItem item : sdm.getClipboardItems())
    {
      ISourceData sd = item.getSourceData();
      if (sd.isCached())
      {
        if (pathMatches(sd.getContentsCacheInfo().getFilenameInCache(), cacheEntry))
        {
          return true;
        }
      }
    }
    return false;
  }

  private boolean pathMatches(String localFilename, String cacheDirectory)
  {
    if (localFilename.startsWith(cacheDirectory))
    {
      log.fine("MATCH: "+localFilename + " starts with "+cacheDirectory);
      return true;
    }
    return false;
  }

  private void removeDirectory(File cacheDirectory)
  {
    log.fine("Remove Directory "+cacheDirectory);
    FileUtils.deleteRecursive(cacheDirectory);
  }

  private void removeFile(File cacheFile)
  {
    log.fine("Remove File "+cacheFile);
    cacheFile.delete();
  }
  
}
