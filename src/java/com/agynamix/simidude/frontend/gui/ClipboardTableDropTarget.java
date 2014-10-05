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

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.ImageTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;

import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.platform.infra.IQueueManager;
import com.agynamix.simidude.clipboard.ClipboardTable;
import com.agynamix.simidude.clipboard.SourceDataManager;
import com.agynamix.simidude.impl.SimidudeApplicationContext;
import com.agynamix.simidude.source.ISourceData;
import com.agynamix.simidude.source.SourceDataFactory;

public class ClipboardTableDropTarget extends DropTargetAdapter {

  final ClipboardTable      clipboardTable;
  final TableViewer         tableViewer;
  final SourceDataManager transferDataManager;

  public ClipboardTableDropTarget(ClipboardTable clipboardTable, SourceDataManager transferDataManager)
  {
    this.clipboardTable      = clipboardTable;
    this.tableViewer         = clipboardTable.getTableViewer();
    this.transferDataManager = transferDataManager;
    DropTarget target = new DropTarget(tableViewer.getControl(), DND.DROP_MOVE | DND.DROP_COPY);
    target.setTransfer(new Transfer[] { TextTransfer.getInstance(), FileTransfer.getInstance(), ImageTransfer.getInstance() });
    target.addDropListener(this);    
  }
  
  @Override
  public void dragEnter(DropTargetEvent event)
  {
    if (clipboardTable.isDragInProgress())
    {
      event.detail = DND.DROP_NONE;
    }
  }
  
  public void drop(DropTargetEvent event)
  {
    IQueueManager qm = ((SimidudeApplicationContext)ApplicationBase.getContext()).getQueueManager();
    ISourceData[] items = SourceDataFactory.createFromDropTarget(event);
    if (items != null)
    {
      qm.putAll(IQueueManager.QUEUE_SOURCE_DATA_MONITOR, items);
//    transferDataManager.add(item);
    }
  }
  
  
}
