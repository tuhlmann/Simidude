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

import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

import com.agynamix.platform.log.ApplicationLog;
import com.agynamix.simidude.infra.IContentsCacheInfo;
import com.agynamix.simidude.source.ISourceData;
import com.agynamix.simidude.source.SourceDataStub;

public abstract class AbstractSourceData implements ISourceData {

  private static final long serialVersionUID = 1L;

  protected final UUID senderId;
  protected final UUID uuid;
  protected final SourceType sourceType;
  protected       TransportType transportType;
  
  protected final Date creationDate;
  
  protected SourceDataStub sourceDataStub = null;

  protected boolean isProxy  = false;
  
  protected transient IContentsCacheInfo contentsCacheInfo;
  
  public  AbstractSourceData(UUID senderId, SourceType sourceType)
  {
    this.senderId      = senderId;
    this.sourceType    = sourceType;
    this.transportType = TransportType.local;
    this.uuid          = UUID.randomUUID();
    this.creationDate  = new Date();
  }
  
  public AbstractSourceData(AbstractSourceData sourceData)
  {
    this.isProxy       = sourceData.isProxy;
    this.senderId      = sourceData.senderId;
    this.sourceType    = sourceData.sourceType;
    this.transportType = sourceData.transportType;
    this.uuid          = sourceData.uuid;
    this.creationDate  = sourceData.creationDate;
  }
  
  public UUID getSenderId()
  {
    return this.senderId;
  }
  
  public UUID getSourceId()
  {
    return this.uuid;
  }

  public SourceType getType()
  {
    return this.sourceType;
  }
  
  public void setTransportType(TransportType transportType)
  {
    this.transportType = transportType;
  }
  
  public TransportType getTransportType()
  {
    return this.transportType;
  }

  public boolean isProxy()
  {
    return isProxy;
  }

  public void setProxy(boolean isProxy)
  {
    this.isProxy = isProxy;
  }

  public boolean isCached()
  {
    return contentsCacheInfo != null;
  }
  
  public void setContentsCacheInfo(IContentsCacheInfo cacheInfo)
  {
    this.contentsCacheInfo = cacheInfo;
  }
  
  public IContentsCacheInfo getContentsCacheInfo()
  {
    return this.contentsCacheInfo;
  }
  
  public Date getCreationDate()
  {
    return this.creationDate;
  }
  
  public SourceDataStub getStub()
  {
    if (sourceDataStub == null)
    {
      sourceDataStub = new SourceDataStub(this);
    }
    return sourceDataStub;
  }

  protected Logger getLogger()
  {
    return ApplicationLog.getLogger(AbstractSourceData.class);
  }

  
}
