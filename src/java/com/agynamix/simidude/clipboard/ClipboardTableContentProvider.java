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

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;


public class ClipboardTableContentProvider implements IStructuredContentProvider {

  
  
  public void dispose()
  {
  }

  public void inputChanged(Viewer arg0, Object arg1, Object arg2)
  {
  }

  @SuppressWarnings("unchecked")
  public Object[] getElements(Object items)
  {
    List l = (List) items;
    return l.toArray();
  }

}
