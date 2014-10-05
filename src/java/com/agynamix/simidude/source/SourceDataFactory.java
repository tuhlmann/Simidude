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
package com.agynamix.simidude.source;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.ImageTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.graphics.ImageData;

import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.platform.log.ApplicationLog;
import com.agynamix.simidude.impl.SimidudeApplicationContext;
import com.agynamix.simidude.infra.ModelProvider;
import com.agynamix.simidude.source.impl.FileSourceData;
import com.agynamix.simidude.source.impl.ImageSourceData;
import com.agynamix.simidude.source.impl.TextSourceData;

public class SourceDataFactory {
  
  protected static ModelProvider modelProvider; 
  
  protected static Logger log = ApplicationLog.getLogger(SourceDataFactory.class);
  
  public static ISourceData[] createFromDropTarget(DropTargetEvent event)
  {
    ISourceData[] itemList = null;
    
    try {
      if (ImageTransfer.getInstance().isSupportedType(event.currentDataType))
      {
        log.fine("Image Data dropped");
        ImageData imageData = (ImageData) event.data;
        itemList = new ISourceData[1];
        itemList[0] = new ImageSourceData(getSenderId(), imageData);
      } else if (FileTransfer.getInstance().isSupportedType(event.currentDataType))
      {
        log.fine("File Data dropped");
        String files[] = (String[]) event.data;
        if (files != null)
        {
          itemList = new ISourceData[files.length];
          for (int i = 0; i < files.length; i++)
          {
            log.fine("Dropped File: "+files[i]);
            itemList[i] = new FileSourceData(getSenderId(), files[i]);
          }
        }
      } else if (TextTransfer.getInstance().isSupportedType(event.currentDataType))
      {
        String text = (String) event.data;
        log.fine("Text Data dropped: "+text);
        itemList = new ISourceData[1];
        itemList[0] = new TextSourceData(getSenderId(), text);
      }
    } catch (Exception e)
    {
      log.log(Level.WARNING, "Could not create ISourceData from DropTarget: "+e.getMessage(), e);
    }
    
    return itemList;
  }
  
  public static ISourceData createFromText(String text)
  {
    try {
      return new TextSourceData(getSenderId(), text);
    } catch (Exception e)
    {
      log.log(Level.WARNING, "Could not create TextSourceData entry: "+e.getMessage(), e);
      return null;
    }
  }
  
  public static ISourceData createFromFile(String filename)
  {
    try {
      return new FileSourceData(getSenderId(), filename);
    } catch (Exception e)
    {
      log.log(Level.WARNING, "Could not create FileSourceData entry: "+e.getMessage(), e);
      return null;
    }
  }
  
  public static ISourceData createFromImage(ImageData imageData)
  {
    try {
      return new ImageSourceData(getSenderId(), imageData);
    } catch (Exception e)
    {
      log.log(Level.WARNING, "Could not create ImageSourceData entry: "+e.getMessage(), e);
      return null;
    }
    
  }

  protected static UUID getSenderId()
  {
    return getModelProvider().getSenderId();
  }

  protected static ModelProvider getModelProvider()
  {
    if (modelProvider == null)
    {
      modelProvider = ((SimidudeApplicationContext)ApplicationBase.getContext()).getModelProvider();
    }
    return modelProvider;
  }

}
