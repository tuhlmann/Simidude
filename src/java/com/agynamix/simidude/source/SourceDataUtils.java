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
package com.agynamix.simidude.source;

import java.net.URL;

import com.agynamix.simidude.source.impl.TextSourceData.TextType;

public class SourceDataUtils {

  public static TextType recognizeTextType(String text)
  {
    if ((text.contains("\n")) || (text.contains("\r")))
    {
      return TextType.Text;
    }
    // Only one line
    try {
      URL uri = new URL(text);
      if (uri != null)
      {
        return TextType.URI;
      }
    } catch (Exception e) {}
    return TextType.Text;
  }

}
