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

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class SpacerToolbarItem extends ContributionItem {

  public SpacerToolbarItem()
  {
    super("Spacer");
  }

  public void fill(ToolBar toolbar, int index)
  {
    if (index >= 0)
    {
      new ToolItem(toolbar, SWT.NONE, index);
    } else
    {
      new ToolItem(toolbar, SWT.NONE);
    }
  }

}
