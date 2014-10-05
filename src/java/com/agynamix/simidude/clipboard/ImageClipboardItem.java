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

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

import com.agynamix.platform.icons.PlatformIcons;
import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.simidude.source.ISourceData;
import com.agynamix.simidude.source.ISourceData.SourceType;
import com.agynamix.simidude.source.impl.ImageSourceData;

public class ImageClipboardItem implements IClipboardItem {

  final ImageSourceData             sourceData;
  final transient SourceDataManager sourceDataManager;
  transient Image                   cachedThumbnail = null;
            String                  tooltip = null;

  public ImageClipboardItem(SourceDataManager sourceDataManager, ImageSourceData sourceData)
  {
    this.sourceData        = sourceData;
    this.sourceDataManager = sourceDataManager;
  }

  public String getDescription()
  {
    return sourceData.getText();
  }

  public Image getImage()
  {
    if (cachedThumbnail == null)
    {
      cachedThumbnail = createThumbnail(sourceData);
    }
    return cachedThumbnail;
  }

  public String getShortDescription()
  {
    return sourceData.getText();
  }

  public ISourceData getSourceData()
  {
    return sourceData;
  }

  public String getTooltip()
  {
    if (tooltip == null)
    {
      tooltip = createTooltip(sourceData);
    }
    return tooltip;
  }

  public SourceType getType()
  {
    return sourceData.getType();
  }

  public Object getData()
  {
    return sourceData.getImageData();
  }

  private Image createThumbnail(ImageSourceData sourceData)
  {
    Image img = null;
    if (sourceData != null)
    {
      ImageRegistry imReg = ApplicationBase.getContext().getImageRegistry();
      img = imReg.get(sourceData.getSourceId().toString());
      if (img == null)
      {
        img = new Image(Display.getDefault(), sourceData.getThumbnail());
        imReg.put(sourceData.getSourceId().toString(), img);
      }
    }
    if (img == null)
    {
      img = PlatformIcons.get(PlatformIcons.COLIMG_IMAGE_UNDEF);
    }
    return img;
  }

  private String createTooltip(ImageSourceData sourceData)
  {
    ImageData id = sourceData.getImageData();
    StringBuilder sb = new StringBuilder();
    sb.append("Clipboard Image:\n\n");
    sb.append("Width:  ").append(id.width).append("\n");
    sb.append("Height: ").append(id.height).append("\n");
    sb.append("Size:   ").append(id.data.length).append(" Bytes\n");
    
    
    return sb.toString();
  }

  public IClipboardItem deleteContents()
  {
    sourceData.deleteContents();
    if (cachedThumbnail != null)
    {
      cachedThumbnail = null;
      ImageRegistry imReg = ApplicationBase.getContext().getImageRegistry();
      imReg.remove(sourceData.getSourceId().toString());
    }
    tooltip = "";
    return this;
  }
  
}
