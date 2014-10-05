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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.agynamix.simidude.clipboard.ClipboardTable;

public interface ISourceDataDialogView {

  /**
   * Create the dialogs contents.
   * @param parent the parent Composite
   * @return the control created by this dialog
   */
  Composite createContent(Composite parent);
  
  ClipboardTable getClipboardTable();

  Control getCustomToolbar();
  
  void addSelectionChangedListener(ISelectionChangedListener l);
  
  void removeSelectionChangedListener(ISelectionChangedListener l);

}
