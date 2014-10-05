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

package com.agynamix.simidude.source.impl;

import java.util.UUID;

import com.agynamix.simidude.source.ISourceData;
import com.agynamix.simidude.source.SourceDataUtils;


public class TextSourceData extends AbstractSourceData {
  
  private static final long serialVersionUID = 1L;
  
  public enum TextType { Text, URI };

  private String   data;
  private TextType textType = null;
        
  int hashCode = 0;
  
  public TextSourceData(UUID senderId, String text)
  {
    super(senderId, SourceType.TEXT);
    this.data = text;
    if (this.data == null)
    {
      this.data = "";
    }
    // TextSourceData instances always hold its contents.
    setProxy(false);
  }
    
  public TextSourceData(TextSourceData sourceData)
  {
    super(sourceData);
    this.data = sourceData.data;
    if (this.data == null)
    {
      this.data = "";
    }
    this.textType = sourceData.textType;
  }

  public ISourceData copy()
  {
    TextSourceData sd = new TextSourceData(this);
    return sd;
  }
  
  public ISourceData equalsCopy()
  {
    return this.copy();
  }
  
  @Override
  public String toString()
  {
    return data;
  }

  public Object getData()
  {
    return data;
  }

  public String getText()
  {
    return data;
  }

  public TextType getTextType()
  {
    if (this.textType == null)
    {
      textType = SourceDataUtils.recognizeTextType(getText());
    }
    return this.textType;
  }

  @Override
  public boolean equals(Object o)
  {
    if (o == this)
    {
      return true;
    }
    if (!(o instanceof TextSourceData))
    {
      return false;
    }
    TextSourceData sd = (TextSourceData) o;
    if ((this.data == null) && (sd.data == null))
    {
      return true;
    }
    if ((this.data == null) || (sd.data == null))
    {
      return false;
    }
    return this.data.equals(sd.data);
  }
  
  @Override
  public int hashCode()
  {
    int result = hashCode;
    if (result == 0)
    {
      result = 17;
      result = 31 * result + data.hashCode();
      hashCode = result;
    }
    return result;
  }
  
  public void deleteContents()
  {
    this.data = "";
  }

}
