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

import java.util.Map;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.ImageTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.graphics.ImageData;

import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.platform.infra.PlatformUtils;
import com.agynamix.simidude.infra.ModelProvider;
import com.agynamix.simidude.source.ISourceData;
import com.agynamix.simidude.source.SourceDataFactory;
import com.agynamix.simidude.source.ISourceData.SourceType;

public abstract class ClipboardMonitorBase implements IClipboardMonitor {

  public final static int DEFAULT_SLEEP_TIME = 200;

  ModelProvider           modelProvider      = null;

  boolean                 itemActivated      = false;

  ISourceData[]           sourceDataInTransit = null;
  
  ISourceData             activatedSourceData = null;

  public void itemActivated(ISourceData sourceData)
  {
//    System.out.println("Item activated");
    activatedSourceData = sourceData;
    itemActivated = true;
  }

  protected ModelProvider getModelprovider()
  {
    if (modelProvider == null)
    {
      modelProvider = (ModelProvider) ApplicationBase.getContext().getService(ModelProvider.SERVICE_NAME);
    }
    return modelProvider;
  }

  public int getSleepTime()
  {
    return DEFAULT_SLEEP_TIME;
  }

  protected synchronized ISourceData[] saveProcessClipboard(final Clipboard clipboard, final Map<SourceType, Boolean> clpItemsEnabledMap)
  {
    synchronized (clipboard)
    {
      PlatformUtils.safeSyncRunnable(new Runnable() {
        public void run()
        {
          sourceDataInTransit = saveProcessClipboard2(clipboard, clpItemsEnabledMap);
        }
      });
    }
    return sourceDataInTransit;
  }

  /**
   * Take an item from the clipboard.
   * We first check if it's available as file, if not then we check if we can get it as text.
   * @param clipboard the system clipboard
   * @return the ClipboardItem
   */
  protected ISourceData[] saveProcessClipboard2(Clipboard clipboard, final Map<SourceType, Boolean> clpItemsEnabledMap)
  {
//    int count = 0;
//    boolean successful = false;
    
    ISourceData[] sourceDataList = null;
    Transfer fileTransfer  = FileTransfer.getInstance();
    Transfer textTransfer  = TextTransfer.getInstance();
    Transfer imageTransfer = ImageTransfer.getInstance();
    
    TransferData fileTransferData  = null;
    TransferData textTransferData  = null;
    TransferData imageTransferData = null;
    
    TransferData[] availableTypes = clipboard.getAvailableTypes();
    for (TransferData td : availableTypes)
    {
      if (fileTransfer.isSupportedType(td))
      {
        fileTransferData = td;
//        System.out.println("Supports FileTransfer ");
      } else if (textTransfer.isSupportedType(td))
      {
        textTransferData = td;
//        System.out.println("Supports TextTransfer ");
      } else if (imageTransfer.isSupportedType(td))
      {
        imageTransferData = td;
//        System.out.println("Supports ImageTransfer ");
      }
    }
    if ((fileTransferData != null) && (isItemTypeEnabled(SourceType.FILE, clpItemsEnabledMap)))
    {
      String[] fnames = (String[]) clipboard.getContents(fileTransfer);
      if ((fnames != null) && (fnames.length > 0))
      {
        sourceDataList = new ISourceData[fnames.length];
        for (int i = 0; i < fnames.length; i++)
        {        
          sourceDataList[i] = SourceDataFactory.createFromFile(fnames[i]);
        }
      }
    } else if ((textTransferData != null) && (isItemTypeEnabled(SourceType.TEXT, clpItemsEnabledMap)))
    {
      String text = (String) clipboard.getContents(textTransfer);
      if (text != null)
      {
        sourceDataList = new ISourceData[1];
        sourceDataList[0] = SourceDataFactory.createFromText(text);
      }
    } else if ((imageTransferData != null) && (isItemTypeEnabled(SourceType.IMAGE, clpItemsEnabledMap)))
    {
      ImageData imageData = (ImageData) clipboard.getContents(imageTransfer);
      if (imageData != null)
      {
        sourceDataList = new ISourceData[1];
        sourceDataList[0] = SourceDataFactory.createFromImage(imageData);
      }
    }
    return sourceDataList;
  }

  private boolean isItemTypeEnabled(SourceType sourceType, Map<SourceType, Boolean> clpItemsEnabledMap)
  {
    Boolean re = clpItemsEnabledMap.get(sourceType);
    if (re == null)
    {
      throw new IllegalStateException("Unknown Item type: "+sourceType);
    }
    return re;
  }
    

}
