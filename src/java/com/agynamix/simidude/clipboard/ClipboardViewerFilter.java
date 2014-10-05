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

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class ClipboardViewerFilter extends ViewerFilter {
  
  final String searchStr;
  
  public ClipboardViewerFilter(String searchStr)
  {
    this.searchStr = searchStr.toLowerCase();
  }

  @Override
  public boolean select(Viewer viewer, Object parentElement, Object element)
  {
    IClipboardItem item = (IClipboardItem) element;
    String desc = item.getDescription();
    if (desc != null)
    {
      return (desc.toLowerCase().indexOf(searchStr) > -1);
    } else {
      return true;
    }
  }
  
  public String getSearchString()
  {
    return searchStr;
  }
  
  @Override
  public String toString()
  {
    return getSearchString();
  }

}
