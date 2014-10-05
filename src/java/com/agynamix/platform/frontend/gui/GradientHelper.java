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
package com.agynamix.platform.frontend.gui;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

public class GradientHelper {

  private static Image oldImage = null;

  public static void applyGradientBG(Composite composite)
  {
    Rectangle rect = composite.getClientArea();
    Image newImage = new Image(composite.getDisplay(), 1, Math.max(1, rect.height));
    GC gc = new GC(newImage);
//    gc.setForeground(composite.getDisplay().getSystemColor(SWT.COLOR_WHITE));
    
    // Windows
    gc.setForeground(new Color(composite.getDisplay(), 239, 242, 249));

//    gc.setBackground(new Color(composite.getDisplay(), 228, 234, 243));
    
    // Windows
    gc.setBackground(new Color(composite.getDisplay(), 240, 240, 240));

    gc.fillGradientRectangle(0, 0, 1, rect.height, true);
    gc.dispose();
    composite.setBackgroundImage(newImage);

    if (oldImage != null)
    {
      oldImage.dispose();
    }
    oldImage = newImage;
  }
}
