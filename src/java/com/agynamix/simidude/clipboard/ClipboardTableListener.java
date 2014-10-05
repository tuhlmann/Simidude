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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class ClipboardTableListener implements Listener {
  
  final Table itsTable;
  
  public ClipboardTableListener(Table table)
  {
    this.itsTable = table;
  }
  
  /*
   * NOTE: MeasureItem, PaintItem and EraseItem are called repeatedly. Therefore, it is critical for performance that
   * these methods be as efficient as possible.
   */
  public void handleEvent(Event event)
  {
    switch (event.type)
    {
    case SWT.MeasureItem: {
      TableItem item = (TableItem) event.item;
      String text = getText(item, event.index);
      Point size = event.gc.textExtent(text);
      event.width = size.x;
//      event.height = Math.max(event.height, size.y);
      event.height = 40;
      break;
    }
    case SWT.PaintItem: {
      TableItem item = (TableItem) event.item;
      String text = getText(item, event.index);
      Point size = event.gc.textExtent(text);
      int offset2 = event.index == 0 ? Math.max(0, (event.height - size.y) / 2) : 0;
      event.gc.drawText(text, event.x, event.y + offset2, true);
      break;
    }
    case SWT.EraseItem: {
      event.detail &= ~SWT.FOREGROUND;
      break;
    }
    }
  }

  String getText(TableItem item, int column)
  {
    String text = item.getText(column);
    if ((text != null) && (text.length() > 100))
    {
      text = text.substring(0, 100)+"...";
    }
    return text;
  }
}


