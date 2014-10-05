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

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.agynamix.platform.frontend.gui.ApplicationGUI;
import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.simidude.clipboard.ClipboardTable;
import com.agynamix.simidude.clipboard.SourceDataManager;
import com.agynamix.simidude.frontend.ctrl.SourceDataDialogController;
import com.agynamix.simidude.impl.SimidudeApplicationContext;

public class SourceDataDialogViewImpl implements ISourceDataDialogView {
  
  private ClipboardTable             clipboardTable;
  
  private SourceDataDialogController itsController;
  private Composite                  toolbar;
  
  public SourceDataDialogViewImpl()
  {
  }
  
  public void setController(SourceDataDialogController itsController)
  {
    this.itsController = itsController;
  }
  
  public void addSelectionChangedListener(ISelectionChangedListener l)
  {
    clipboardTable.addSelectionChangeListener(l);
  }
  
  public void removeSelectionChangedListener(ISelectionChangedListener l)
  {
    clipboardTable.removeSelectionChangeListener(l);
  }
  
  /**
   * @param comp
   * @return
   */
  public Composite createContent(final Composite parent)
  {
    final Composite c1 = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout();
    layout.numColumns = 1;
    c1.setSize(parent.getSize());
    c1.setLayout(layout);
    
    ApplicationGUI gui = ApplicationBase.getContext().getApplicationGUI();
    toolbar = gui.createCustomToolbar(c1);
    
    SourceDataManager sourceDataManager = ((SimidudeApplicationContext)ApplicationBase.getContext()).getSourceDataManager();
    
    clipboardTable = new ClipboardTable(sourceDataManager, c1, SWT.BORDER | SWT.FLAT | SWT.V_SCROLL);
    clipboardTable.getTableViewer().setInput(sourceDataManager.getClipboardItems());

    GridData spec = new GridData();
    spec.horizontalAlignment = GridData.FILL;
    spec.grabExcessHorizontalSpace = true;
    spec.verticalAlignment = GridData.FILL;
    spec.grabExcessVerticalSpace = true;
    // spec.horizontalSpan = 5;
    clipboardTable.getTable().setLayoutData(spec);

    c1.layout();

    return c1;
  }
  
  public Control getCustomToolbar()
  {
    return toolbar;
  }
  
  public ClipboardTable getClipboardTable()
  {
    return clipboardTable;
  }

}
