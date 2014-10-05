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

import com.agynamix.simidude.source.ISourceData;
import com.agynamix.simidude.source.ISourceData.SourceType;


public interface IClipboardItem {

  /**
   * 
   * @return the type of this item. Could be text, image, binary, ...
   */
  SourceType getType();
  
  /**
   * 
   * @return an description for the current item that is shown in the list of available clipboard items
   */
  String getDescription();

  /**
   * 
   * @return a short description shown in the clipboard table. Linux shows all of the text 
   * which messes up the table.
   */
  String getShortDescription();
  
  /**
   * 
   * @return returns the text shown as a tooltip when hovering over the item.
   */
  String getTooltip();
  
  /**
   * Returns an image for the current item.
   * @return
   */
  Image getImage();
  
  /**
   * The IClipboardItem is really just a wrapper around the original source data.
   * It augments the source data with the visual aspects of presenting the source.
   * @return the ISourceData instance that this IClipboardItem wraps.
   */
  ISourceData getSourceData();

  Object getData();

  /**
   * Strip the item off its contents.
   * All items are remembered, even when they have been deleted from the list. To safe memory (images
   * can take a lot of space) we can strip the item from its contents.
   * @return a reference to this item.
   */
  IClipboardItem deleteContents();
  
}
