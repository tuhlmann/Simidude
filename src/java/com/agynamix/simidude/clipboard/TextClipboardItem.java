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

import org.eclipse.swt.graphics.Image;

import com.agynamix.platform.icons.PlatformIcons;
import com.agynamix.simidude.source.ISourceData;
import com.agynamix.simidude.source.ISourceData.SourceType;
import com.agynamix.simidude.source.impl.TextSourceData;
import com.agynamix.simidude.source.impl.TextSourceData.TextType;


public class TextClipboardItem implements IClipboardItem {

  final TextSourceData sourceData;
  
  public TextClipboardItem(TextSourceData sourceData)
  {
    this.sourceData = sourceData;
  }
  
  public String getDescription()
  {
    return sourceData.getText();
  }

  public String getShortDescription()
  {
    return getSubstring(sourceData.getText(), 80, "...");
  }
  
  public Image getImage()
  {
    if (sourceData.getTextType() == TextType.URI)
    {
      return PlatformIcons.get(PlatformIcons.COLIMG_URI);
    } else {
      return PlatformIcons.get(PlatformIcons.COLIMG_TEXT);
    }
  }

  public String getTooltip()
  {
    return getSubstring(sourceData.getText(), 1000, "...");
  }

  public SourceType getType()
  {
    return SourceType.TEXT;
  }
  
  public ISourceData getSourceData()
  {
    return sourceData;
  }
  
  private String getSubstring(String text, int len, String postfix)
  {
    return (text.length() <= len) ? text : text.substring(0, len) + postfix;
  }

  public Object getData()
  {
    return sourceData.getText();
  }
  
  public IClipboardItem deleteContents()
  {
    sourceData.deleteContents();
    return this;
  }
  
}
