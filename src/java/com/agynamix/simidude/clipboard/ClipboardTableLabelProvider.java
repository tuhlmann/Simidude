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

package com.agynamix.simidude.clipboard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;


public class ClipboardTableLabelProvider implements ITableLabelProvider {
  
  List<ILabelProviderListener> listeners = new ArrayList<ILabelProviderListener>();  

  public ClipboardTableLabelProvider()
  {
  }

  public Image getColumnImage(Object arg0, int arg1)
  {
    return null;
  }

  public String getColumnText(Object element, int columnIndex)
  {
    String text = "";
    IClipboardItem item = (IClipboardItem) element;
    switch (columnIndex)
    {
      case ClipboardTable.NAME_COL:
        text = item.getDescription();
        break;
    }
    return text;
  }
  
  public void addListener(ILabelProviderListener listener)
  {
    listeners.add(listener);
  }

  public void removeListener(ILabelProviderListener arg0)
  {
    listeners.remove(listeners);
  }
  
  public boolean isLabelProperty(Object arg0, String arg1)
  {
    return false;
  }

  public void dispose()
  {
  }
  
}
