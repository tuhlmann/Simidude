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
package com.agynamix.simidude.frontend.gui;

import java.util.logging.Logger;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.ImageTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.TableItem;

import com.agynamix.platform.log.ApplicationLog;
import com.agynamix.simidude.clipboard.ClipboardTable;
import com.agynamix.simidude.clipboard.IClipboardItem;
import com.agynamix.simidude.clipboard.SourceDataManager;
import com.agynamix.simidude.source.ISourceData.SourceType;
import com.agynamix.simidude.source.impl.FileSourceData;
import com.agynamix.simidude.source.impl.ImageSourceData;

public class ClipboardTableDragSource implements DragSourceListener {

  final ClipboardTable      clipboardTable;
  final TableViewer         tableViewer;
  final SourceDataManager   sourceDataManager;
  
  final DragSource          dragSource;
  
  Transfer[]                currentTransfer = null;
  
  Logger log = ApplicationLog.getLogger(ClipboardTableDragSource.class);
  
  public ClipboardTableDragSource(ClipboardTable clipboardTable, SourceDataManager sourceDataManager)
  {
    this.clipboardTable = clipboardTable;
    this.tableViewer = clipboardTable.getTableViewer();
    this.sourceDataManager = sourceDataManager;
    dragSource = new DragSource(tableViewer.getControl(), DND.DROP_COPY);
    dragSource.setTransfer(new Transfer[] { TextTransfer.getInstance(), FileTransfer.getInstance() });
    dragSource.addDragListener(this);
  }

  public void dragStart(DragSourceEvent event)
  {
    IClipboardItem item = getSelectedItem();
    if (item != null)
    {
      event.doit = true;
      clipboardTable.setDragInProgress(true);
      switch (item.getType())
      {
        case FILE:
          if (sourceDataManager.isRetrieveContentsNeeded(item))
          {
            currentTransfer = new Transfer[] { TextTransfer.getInstance() };
          } else {
            currentTransfer = new Transfer[] { TextTransfer.getInstance(), FileTransfer.getInstance() };            
          }
          break;
        case TEXT:
          currentTransfer = new Transfer[] { TextTransfer.getInstance() };
          break;
        case IMAGE:
          currentTransfer = new Transfer[] { ImageTransfer.getInstance() };
          break;
      }
      dragSource.setTransfer(currentTransfer);  
    } else {
      event.doit = false;
    }  
  }

  public void dragSetData(DragSourceEvent event)
  {
    IClipboardItem item = getSelectedItem();
    if (item == null)
    {
      return;
    }
    event.image = item.getImage();

//    final ModelProvider mp = ((SimidudeApplicationContext)ApplicationBase.getContext()).getModelProvider();
//    final ISourceData sourceData = item.getSourceData();
    
    if (TextTransfer.getInstance().isSupportedType(event.dataType))
    {
      switch (item.getType())
      {
        case TEXT:
          event.data = item.getDescription();
          break;
        case FILE:
          event.data = item.getDescription();
          break;
      }
    } else if (FileTransfer.getInstance().isSupportedType(event.dataType)) 
    {
      switch (item.getType())
      {
        case FILE:
          FileSourceData fsd = (FileSourceData) item.getSourceData();
          event.data = new String[] {fsd.getLocalFilename()};
//          System.out.println("Local Filename: "+fsd.getLocalFilename());
          break;
        case TEXT:
          // we should not get here.
          event.data = new String[] {item.getDescription()};
          break;
      }
    } else if (ImageTransfer.getInstance().isSupportedType(event.dataType))
    {
      if (item.getType() == SourceType.IMAGE)
      {
        ImageSourceData isd = (ImageSourceData) item.getSourceData();
        event.data = isd.getImageData();
      } else {
        log.warning("Cannot support ImageTransfer for Types other than images!");
      }
    }
  }

  protected IClipboardItem getSelectedItem()
  {
    ISelection s = tableViewer.getSelection();
    if (s instanceof IStructuredSelection)
    {
      IStructuredSelection ss = (IStructuredSelection) s;
      Object item = ss.getFirstElement();
      if (item instanceof IClipboardItem)
      {
        return (IClipboardItem) item;
      }
    }
    return null;
  }

  public void dragFinished(DragSourceEvent event)
  {
    if (event.detail == DND.DROP_MOVE)
    {
      TableItem[] tableItems = tableViewer.getTable().getSelection();
      if ((tableItems != null) && (tableItems.length > 0))
      {
        sourceDataManager.removeItem((IClipboardItem) tableItems[0].getData());
      }
    }
    // We have File.deleteOnExit() enabled, so lets see how this goes.
//    IClipboardItem item = getSelectedItem();
//    ((TextSourceData)item.getSourceData()).removeTempFile();
    clipboardTable.setDragInProgress(false);
  }
  
}
