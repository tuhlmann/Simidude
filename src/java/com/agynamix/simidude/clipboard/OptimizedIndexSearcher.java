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

import org.eclipse.swt.widgets.TableItem;

public class OptimizedIndexSearcher {

  private int lastIndex = 0;

  public boolean isEven(TableItem item)
  {
    TableItem[] items = item.getParent().getItems();

    // 1. Search the next ten items
    for (int i = lastIndex; i < items.length && lastIndex + 10 > i; i++)
    {
      if (items[i] == item)
      {
        lastIndex = i;
        return lastIndex % 2 == 0;
      }
    }

    // 2. Search the previous ten items
    for (int i = lastIndex; i < items.length && lastIndex - 10 > i; i--)
    {
      if (items[i] == item)
      {
        lastIndex = i;
        return lastIndex % 2 == 0;
      }
    }

    // 3. Start from the beginning
    for (int i = 0; i < items.length; i++)
    {
      if (items[i] == item)
      {
        lastIndex = i;
        return lastIndex % 2 == 0;
      }
    }

    return false;
  }

}
