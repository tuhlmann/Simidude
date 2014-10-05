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

package com.agynamix.simidude.source.impl;

import java.io.File;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.graphics.ImageData;

import com.agynamix.platform.log.ApplicationLog;
import com.agynamix.simidude.infra.CacheManagerFactory;
import com.agynamix.simidude.source.ISourceData;


public class FileSourceData extends AbstractSourceData {
  
  private static final long serialVersionUID = 1L;

  private final static String[] imgExt = new String[] {".jpg", ".png", ".gif", ".bmp", ".tiff"};
  private final static long MAX_IMAGE_FOR_THUMBNAIL = 2000000;
 
  File    file;  
  boolean isDirectory;  
  String  filename;
  boolean isImage;
  
  ImageDataWrapper thumbnail = null;
  
  int hashCode = 0;
  
  /**
   * The contents of a file or directory
   */
  Object contents;
  
  public FileSourceData(UUID senderId, String filename)
  {
    super(senderId, SourceType.FILE);
    this.filename = filename;
    this.file = new File(filename);
    this.isDirectory = file.isDirectory();
    if (this.isDirectory)
    {
      this.isImage = false;
    } else {
      this.isImage = checkFileIsImage(file);
    }
    // FileSourceData instances always are proxies.
    setProxy(true);
  }
  
  public FileSourceData(FileSourceData sourceData)
  {
    this(sourceData, true);
  }

  public FileSourceData(FileSourceData sourceData, boolean fullCopy)
  {
    super(sourceData);
    this.filename = sourceData.filename;
    if (fullCopy)
    {
      this.isImage = sourceData.isImage;
      this.file = new File(filename);
      this.isDirectory = file.isDirectory();  
      this.thumbnail = sourceData.thumbnail;
    }
  }

  public ISourceData copy()
  {
    FileSourceData sd = new FileSourceData(this);
    return sd;
  }
  
  public ISourceData equalsCopy()
  {
    FileSourceData sd = new FileSourceData(this, false);
    return sd;
  }
  
  @Override
  public String toString()
  {
    return filename;
  }

  public Object getData()
  {
    return filename;
  }

  public String getText()
  {
    return filename;
  }

  public boolean isDirectory()
  {
    return isDirectory;
  }

  public String getFilename()
  {
    return filename;
  }
  
  public File getFile()
  {
    return this.file;
  }


  public String getLocalFilename()
  {
    if (isCached())
    {
      return getContentsCacheInfo().getFilenameInCache();
    }
    return filename;
  }

  @Override
  public boolean equals(Object o)
  {
    if (o == this)
    {
      return true;
    }
    if (!(o instanceof FileSourceData))
    {
      return false;
    }
    FileSourceData sd = (FileSourceData) o;
    return this.filename.equals(sd.filename);
  }
  
  @Override
  public int hashCode()
  {
    int result = hashCode;
    if (result == 0)
    {
      result = 17;
      result = 31 * result + filename.hashCode();
      hashCode = result;
    }
    return result;
  }
  
  private boolean checkFileIsImage(File file)
  {
    if (file.canRead())
    {
      int pos = file.getAbsolutePath().lastIndexOf(".");
      if (pos > -1)
      {
        String extension = file.getAbsolutePath().substring(pos).toLowerCase();
        for (String ext : imgExt)
        {
          if (ext.equals(extension))
          {
            return true;
          }
        }
      }
    }
    return false;
  }

  public boolean isImage()
  {
    return isImage;
  }

  /**
   * If this FileSourceData holds an image under a predefined size, we read the file and create a thumbnail.
   * @return a thumbnail of the image or null if the file is no image or the image is too large.
   */
  public ImageData getThumbnail()
  {
    if (thumbnail == null)
    {
      try {
        if ((isImage()) && (file.exists()) && (file.canRead()) && (file.length() < MAX_IMAGE_FOR_THUMBNAIL))
        {    
          ImageDataWrapper wrapper = new ImageDataWrapper(new ImageData(filename));
          thumbnail = new ImageDataWrapper(wrapper.getThumbnailImageData());
        }
      } catch (Exception e)
      {
        getLogger().log(Level.WARNING, e.getMessage(), e);
      }
    }
    if (thumbnail != null)
    {
      return thumbnail.getImageData();
    } else {
      return null;
    }
  }
  
  public void deleteContents()
  {
    if (isCached())
    {
      CacheManagerFactory.removeContentsFromCache(getContentsCacheInfo());
      setContentsCacheInfo(null);
    }
    this.filename = "";
    this.file = null;
    this.thumbnail = null;
  }
  
}
