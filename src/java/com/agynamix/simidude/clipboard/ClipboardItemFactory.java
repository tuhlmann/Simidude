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

import com.agynamix.simidude.source.ISourceData;
import com.agynamix.simidude.source.impl.FileSourceData;
import com.agynamix.simidude.source.impl.ImageSourceData;
import com.agynamix.simidude.source.impl.TextSourceData;


public class ClipboardItemFactory {

  public static IClipboardItem createItemFromSourceData(SourceDataManager sourceDataManager, ISourceData data)
  {
    IClipboardItem item = null;
    switch (data.getType())
    {
      case TEXT:
        item = new TextClipboardItem((TextSourceData) data);
        break;
      case FILE:
        item = new FileClipboardItem(sourceDataManager, (FileSourceData) data);
        break;
      case IMAGE:
        item = new ImageClipboardItem(sourceDataManager, (ImageSourceData) data);
        break;
    }
    return item;
  }

}
