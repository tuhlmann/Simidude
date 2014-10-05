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
package com.agynamix.simidude.clipboard;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

import com.agynamix.platform.icons.PlatformIcons;
import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.simidude.source.ISourceData;
import com.agynamix.simidude.source.ISourceData.SourceType;
import com.agynamix.simidude.source.impl.FileSourceData;

public class FileClipboardItem implements IClipboardItem {

  final FileSourceData sourceData;
  final transient SourceDataManager sourceDataManager;
        transient Image             thumbnail               = null;
        transient Image             downloadNeededThumbnail = null;
  
  public FileClipboardItem(SourceDataManager sourceDataManager, FileSourceData sourceData)
  {
    this.sourceDataManager = sourceDataManager;
    this.sourceData = sourceData;
  }
  
  public String getDescription()
  {
    return sourceData.getFilename();
  }
  
  public String getShortDescription()
  {
    return sourceData.getFilename();
  }

  public Image getImage()
  {
    boolean isDownloadContent = sourceDataManager.isRetrieveContentsNeeded(this);
    if (sourceData.isImage())
    {
      ImageRegistry imReg = ApplicationBase.getContext().getImageRegistry();
      if (isDownloadContent)
      {
        if (downloadNeededThumbnail == null)
        {
          if (sourceData != null)
          {
            ImageData imageData = sourceData.getThumbnail();            
            if (imageData != null)
            {
              Image img = imReg.get(sourceData.getSourceId().toString()+"-download-needed");
              if (img == null)
              {
                img = new Image(Display.getDefault(), imageData);              
                downloadNeededThumbnail = new Image(Display.getDefault(), img, SWT.IMAGE_GRAY); 
                img.dispose();
                imReg.put(sourceData.getSourceId().toString()+"-download-needed", downloadNeededThumbnail);
              } else {
                downloadNeededThumbnail = img;
              }
            }
          }
        }
        return downloadNeededThumbnail;
      } else {
        if (thumbnail == null)
        {
          if (sourceData != null)
          {
            ImageData imageData = sourceData.getThumbnail();
            if (imageData != null)
            {
              thumbnail = imReg.get(sourceData.getSourceId().toString());
              if (thumbnail == null)
              {
                thumbnail = new Image(Display.getDefault(), imageData);
                imReg.put(sourceData.getSourceId().toString(), thumbnail);
              }
            }
          }
        }
        return thumbnail;
      }
    }
    if (isDirectory())
    {
      if (isDownloadContent)
      {        
        return PlatformIcons.get(PlatformIcons.COLIMG_DOWNLOAD_NEEDED_FOLDER);
      } else {
        return PlatformIcons.get(PlatformIcons.COLIMG_FOLDER);
      }
    } else {
      if (isDownloadContent)
      {        
        return PlatformIcons.get(PlatformIcons.COLIMG_DOWNLOAD_NEEDED_FILE);
      } else {
        return PlatformIcons.get(PlatformIcons.COLIMG_FILE);
      }
    }
  }

  public String getTooltip()
  {
    return sourceData.getFilename();
  }

  public SourceType getType()
  {
    return sourceData.getType();
  }

  public boolean isDirectory()
  {
    return sourceData.isDirectory();
  }
  
  public ISourceData getSourceData()
  {
    return sourceData;
  }

  public Object getData()
  {
    return sourceData.getFilename();
  }
  
  public IClipboardItem deleteContents()
  {
    sourceData.deleteContents();
    ImageRegistry imReg = ApplicationBase.getContext().getImageRegistry();
    imReg.remove(sourceData.getSourceId().toString());
    imReg.remove(sourceData.getSourceId().toString()+"-download-needed");
    return this;
  }

}

