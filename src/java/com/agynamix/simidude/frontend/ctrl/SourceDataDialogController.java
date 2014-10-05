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
package com.agynamix.simidude.frontend.ctrl;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.agynamix.simidude.clipboard.ClipboardTable;
import com.agynamix.simidude.clipboard.SourceDataManager;
import com.agynamix.simidude.frontend.gui.ISourceDataDialogView;

public class SourceDataDialogController {

  public static final String     SERVICE_NAME  = "SourceDataDialogController";

  protected final ISourceDataDialogView sourceDataDataView;
  private   final SourceDataManager     sourceDataManager;
  
  public SourceDataDialogController(SourceDataManager sourceDataManager, ISourceDataDialogView transferDataView)
  {
    this.sourceDataManager = sourceDataManager;
    this.sourceDataDataView = transferDataView;
  }
  
  public void addSelectionChangedListener(ISelectionChangedListener l)
  {
    sourceDataDataView.addSelectionChangedListener(l);
  }
  
  public void removeSelectionChangedListener(ISelectionChangedListener l)
  {
    sourceDataDataView.removeSelectionChangedListener(l);
  }

  /**
   * Create the contents to be shown by this dialog
   * @param parent
   * @return
   */
  public Composite createContent(Composite parent)
  {
    return sourceDataDataView.createContent(parent);
  }

  public SourceDataManager getSourceDataManager()
  {
    return this.sourceDataManager;
  }

  public ClipboardTable getClipboardTable()
  {
    return sourceDataDataView.getClipboardTable();
  }

  public Control getCustomToolbar()
  {
    return sourceDataDataView.getCustomToolbar();
  }

  

}
