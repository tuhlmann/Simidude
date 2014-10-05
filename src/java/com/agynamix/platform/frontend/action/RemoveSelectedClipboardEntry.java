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
package com.agynamix.platform.frontend.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.ApplicationWindow;

import com.agynamix.platform.icons.PlatformIcons;
import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.simidude.clipboard.IClipboardItem;
import com.agynamix.simidude.clipboard.SourceDataManager;
import com.agynamix.simidude.impl.SimidudeApplicationContext;
import com.agynamix.simidude.infra.SimidudeUtils;

public class RemoveSelectedClipboardEntry extends Action implements ISelectionChangedListener {

  final ApplicationWindow window;
  
  final boolean isNetworkRemove;

  public RemoveSelectedClipboardEntry(ApplicationWindow w, boolean isNetworkRemove)
  {
    super(isNetworkRemove ? "Remove Selected Entry Everywhere" : "Remove Selected Entry Locally", IAction.AS_PUSH_BUTTON);    
    this.window = w;
    this.isNetworkRemove = isNetworkRemove;
    if (isNetworkRemove)
    {
      setToolTipText("Removes the selected entry from the clipboard table AND all connected clients");
      setImageDescriptor(PlatformIcons.getDescriptor(PlatformIcons.NETWORK_REMOVE_SELECTED_CLIPBOARD_ENTRY));
    } else {
      setToolTipText("Removes the selected entry from the clipboard table");
      setImageDescriptor(PlatformIcons.getDescriptor(PlatformIcons.REMOVE_SELECTED_CLIPBOARD_ENTRY));      
    }
    setEnabled(false);
  }

  public void selectionChanged(SelectionChangedEvent event)
  {
//    System.out.println("Source: "+event.getSource());
//    System.out.println("Selection Class "+event.getSelection().getClass());
//    System.out.println(event.getSelectionProvider().getClass().getName());
    if (event.getSelection() instanceof IStructuredSelection)
    {
      IStructuredSelection ss = (IStructuredSelection) event.getSelection();
      Object first = ss.getFirstElement();
      if (first != null)
      {
//        System.out.println("First Element Class: "+first.getClass().getName());
        if (first instanceof IClipboardItem)
        {
          setEnabled(true);
        }
      } else {
//        System.out.println("First Element EMPTY");
        setEnabled(false);
      }
    }
  }
  
  /**
   * @see org.eclipse.jface.action.Action#run()
   */
  public void run()
  {
    boolean isNetworkRemove = SimidudeUtils.isModifierKeyPressed() || this.isNetworkRemove;
    SourceDataManager sdm = ((SimidudeApplicationContext) ApplicationBase.getContext()).getSourceDataManager();
    int currentSelectionIndex = sdm.getSelectionIndex();

    IClipboardItem removedItem = sdm.removeSelectedEntry();
    sdm.saveSelectEntry(currentSelectionIndex);
    if ((isNetworkRemove) && (removedItem != null))
    {
      ((SimidudeApplicationContext)ApplicationBase.getContext()).getModelProvider().networkRemoveItem(removedItem.getSourceData().getStub());
    }
  }

}
