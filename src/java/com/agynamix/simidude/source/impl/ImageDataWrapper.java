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
package com.agynamix.simidude.source.impl;

import java.io.Serializable;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

public class ImageDataWrapper implements Serializable {

  private static final long   serialVersionUID = 1L;

  public final int            width;
  public final int            height;
  public final int            depth;

  public final int            red;
  public final int            green;
  public final int            blue;

  public final int            scanlinePad;

  public byte[]               data;

  private transient ImageData thumbnail        = null;

  public ImageDataWrapper(ImageData imageData)
  {
    if (imageData == null)
    {
      throw new NullPointerException();
    }
    this.width = imageData.width;
    this.height = imageData.height;
    this.depth = imageData.depth;

    this.red = imageData.palette.redMask;
    this.green = imageData.palette.greenMask;
    this.blue = imageData.palette.blueMask;

    this.scanlinePad = imageData.scanlinePad;
    this.data = imageData.data;
  }

  public ImageData getImageData()
  {
    return new ImageData(width, height, depth, new PaletteData(red, green, blue), scanlinePad, data);
  }

  public ImageData getThumbnailImageData()
  {
    if (thumbnail == null)
    {
      ImageData imageData = getImageData();
      if (imageData != null)
      {
        int height = imageData.height;
        int width = imageData.width;
        double heightFactor = (double) 48 / (double) height;
        double widthFactor = (double) 64 / (double) width;

        int scaledHeight;
        int scaledWidth;

        int w = (int) (width * heightFactor);
        if (w <= 64)
        {
          scaledHeight = (int) (height * heightFactor);
          scaledWidth = w;
        } else
        {
          scaledWidth = (int) (width * widthFactor);
          scaledHeight = (int) (height * widthFactor);
        }
        Image thumb1 = new Image(Display.getDefault(), imageData.scaledTo(scaledWidth, scaledHeight));
        Image thumbnailImg = new Image(Display.getDefault(), new Rectangle(0, 0, 64, 48));
        GC gcThumb = new GC(thumbnailImg);

        int x = (64 - scaledWidth) / 2;
        int y = (48 - scaledHeight) / 2;

        gcThumb.drawImage(thumb1, x, y);

        thumbnail = thumbnailImg.getImageData();
        thumb1.dispose();
        thumbnailImg.dispose();
        gcThumb.dispose();
      }
    }
    return thumbnail;
  }

  public void removeImageData()
  {
    this.data = new byte[0];
  }

}
