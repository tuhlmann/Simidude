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

import java.io.Serializable;
import java.util.UUID;

/**
 * This class is created by a ISourceData item to represent the id of the data.
 * It is potentially much smaller then a complete ISourceData item.
 * It is used to transmit the id of a ISourceData item to peers and ask them if they already have this item.
 * peers can then request the contents of the items they do not yet have.
 * @author tuhlmann
 *
 */
public class SourceDataStub implements Serializable {
  
  private static final long serialVersionUID = 1L;

  private volatile int hashCode;
  
  final UUID sourceId;
  
  public SourceDataStub(ISourceData sourceData)
  {
    this.sourceId = sourceData.getSourceId();
  }
  
  public SourceDataStub(UUID sourceId)
  {
    this.sourceId = sourceId;
  }
  
  @Override
  public boolean equals(Object o)
  {
    if (o == this)
    {
      return true;
    }
    if (!(o instanceof SourceDataStub))
    {
      return false;
    }
    SourceDataStub stub = (SourceDataStub) o;
    return this.sourceId.equals(stub.sourceId);
  }
  
  @Override
  public int hashCode()
  {
    int result = hashCode;
    if (result == 0)
    {
      result = 17;
      result = 31 * result + sourceId.hashCode();
      hashCode = result;
    }
    return result;
  }
  
  @Override
  public String toString()
  {
    return sourceId.toString();
  }
  

}
