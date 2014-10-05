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
import org.eclipse.swt.widgets.*;

/*
 * Menu example snippet: enable menu items dynamically (when menu shown)
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 */
public class EnableMenuItemsDynamicallyExample {

  public static void main(String[] args)
  {
    Display display = new Display();
    Shell shell = new Shell(display);
    final Tree tree = new Tree(shell, SWT.BORDER | SWT.MULTI);
    final Menu menu = new Menu(shell, SWT.POP_UP);
    tree.setMenu(menu);
    for (int i = 0; i < 12; i++)
    {
      TreeItem treeItem = new TreeItem(tree, SWT.NONE);
      treeItem.setText("Item " + i);
      MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
      menuItem.setText(treeItem.getText());
    }
    menu.addListener(SWT.Show, new Listener() {
      public void handleEvent(Event event)
      {
        MenuItem[] menuItems = menu.getItems();
        TreeItem[] treeItems = tree.getSelection();
        for (int i = 0; i < menuItems.length; i++)
        {
          String text = menuItems[i].getText();
          int index = 0;
          while (index < treeItems.length)
          {
            if (treeItems[index].getText().equals(text))
              break;
            index++;
          }
          menuItems[i].setEnabled(index != treeItems.length);
        }
      }
    });
    tree.setSize(200, 200);
    shell.setSize(300, 300);
    shell.open();
    while (!shell.isDisposed())
    {
      if (!display.readAndDispatch())
        display.sleep();
    }
    display.dispose();
  }

}
