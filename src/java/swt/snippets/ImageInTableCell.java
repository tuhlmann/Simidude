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
package swt.snippets;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class ImageInTableCell {

  public static void main(String[] args)
  {
    Display display = new Display();
    final Image image = display.getSystemImage(SWT.ICON_INFORMATION);
    Shell shell = new Shell(display);
    shell.setText("Images on the right side of the TableItem");
    shell.setLayout(new FillLayout());
    Table table = new Table(shell, SWT.MULTI | SWT.FULL_SELECTION);
    table.setHeaderVisible(false);
    table.setLinesVisible(true);
    int columnCount = 3;
    for (int i = 0; i < columnCount; i++)
    {
      TableColumn column = new TableColumn(table, SWT.NONE);
      column.setText("Column " + i);
    }
    int itemCount = 8;
    for (int i = 0; i < itemCount; i++)
    {
      TableItem item = new TableItem(table, SWT.NONE);
      item.setText(new String[] { "item " + i + " a", "item " + i + " b", "item " + i + " c" });
    }
    /*
     * NOTE: MeasureItem, PaintItem and EraseItem are called repeatedly. Therefore, it is critical for performance that
     * these methods be as efficient as possible.
     */
    Listener paintListener = new Listener() {
      public void handleEvent(Event event)
      {
        switch (event.type)
        {
        case SWT.MeasureItem: {
          Rectangle rect = image.getBounds();
          event.width += rect.width;
          System.out.println("event.height="+event.height);
          System.out.println("rect.height ="+rect.height);
          event.height = Math.max(event.height, rect.height + 20);
          break;
        }
        case SWT.PaintItem: {
          int x = event.x + event.width;
          Rectangle rect = image.getBounds();
          int offset = Math.max(0, (event.height - rect.height) / 2);
          event.gc.drawImage(image, x, event.y + offset);
          break;
        }
        }
      }
    };
    table.addListener(SWT.MeasureItem, paintListener);
    table.addListener(SWT.PaintItem, paintListener);

    for (int i = 0; i < columnCount; i++)
    {
      table.getColumn(i).pack();
    }
    shell.setSize(500, 200);
    shell.open();
    while (!shell.isDisposed())
    {
      if (!display.readAndDispatch())
        display.sleep();
    }
    if (image != null)
      image.dispose();
    display.dispose();
  }
}
