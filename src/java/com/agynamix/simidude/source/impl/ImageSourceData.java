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

import java.util.UUID;

import org.eclipse.swt.graphics.ImageData;

import com.agynamix.simidude.source.ISourceData;

public class ImageSourceData extends AbstractSourceData {

  public final static String CLIPBOARD_IMAGE_TEXT = "Clipboard Image";
  
  private static final long serialVersionUID = 1L;

  ImageDataWrapper imageDataWrapper;      
  
  String imageText = null;
  
  int hashCode = 0;
  
  public ImageSourceData(ImageSourceData sourceData)
  {
    this(sourceData, true);
  }

  public ImageSourceData(ImageSourceData sourceData, boolean fullCopy)
  {
    super(sourceData);
    this.imageDataWrapper = new ImageDataWrapper(sourceData.imageDataWrapper.getImageData()); 
    if (!fullCopy)
    {
      this.getThumbnail();
      this.imageDataWrapper.removeImageData();
    }
  }
  
  public ImageSourceData(UUID senderId, ImageData imageData)
  {
    super(senderId, SourceType.IMAGE);
    this.imageDataWrapper = new ImageDataWrapper(imageData);
    setProxy(false);
  }

  public ISourceData copy()
  {
    ImageSourceData isd = new ImageSourceData(this);
    return isd;
  }
  
  public ISourceData equalsCopy()
  {
    ImageSourceData isd = new ImageSourceData(this, false);
    return isd;
  }

  public Object getData()
  {
    return getImageData();
  }
  
  public ImageData getImageData()
  {
    return imageDataWrapper.getImageData();
  }

  public ImageData getThumbnail()
  {
    return imageDataWrapper.getThumbnailImageData();
  }

  public String getText()
  {
    if (imageText == null)
    {
      imageText = CLIPBOARD_IMAGE_TEXT + " ("+imageDataWrapper.width+"x"+imageDataWrapper.height+" Pixel)";      
    }
    return imageText;
  }
  
  @Override
  public boolean equals(Object o)
  {
    if (o == this)
    {
      return true;
    }
    if (!(o instanceof ImageSourceData))
    {
      return false;
    }
    ImageSourceData sd = (ImageSourceData) o;
    return bytesEqual(this.getThumbnail().data, sd.getThumbnail().data);
  }
  
  @Override
  public int hashCode()
  {
    int result = hashCode;
    if (result == 0)
    {
      result = 17;
      result = 31 * result + getThumbnail().hashCode();
      hashCode = result;
    }
    return result;
  }

  private boolean bytesEqual(byte[] myData, byte[] itsData)
  {
    if (myData.length != itsData.length)
    {
      return false;
    }
    for (int i = 0; i < myData.length; i++)
    {
      if (myData[i] != itsData[i])
      {
        return false;
      }
    }
    return true;
  }
  
  public void deleteContents()
  {
    imageText = "";
    imageDataWrapper = null;
  }

}

