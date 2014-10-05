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
package com.agynamix.simidude.remote;

import java.util.ArrayList;
import java.util.List;

import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.platform.net.AbstractRemoteCommand;
import com.agynamix.platform.net.IConnectorServerHandler;
import com.agynamix.simidude.clipboard.IClipboardItem;
import com.agynamix.simidude.clipboard.SourceDataManager;
import com.agynamix.simidude.impl.SimidudeApplicationContext;
import com.agynamix.simidude.source.ISourceData;
import com.agynamix.simidude.source.ISourceData.TransportType;

public class RetrieveExistingSourceDataCommand extends AbstractRemoteCommand {

  private static final long serialVersionUID = 1L;

  public Object invoke(IConnectorServerHandler connectorServerHandler)
  {
    return retrieveExistingSourceData();
  }

  public List<ISourceData> retrieveExistingSourceData()
  {
    List<ISourceData> itemList = new ArrayList<ISourceData>();
    
    SourceDataManager tm = ((SimidudeApplicationContext)ApplicationBase.getContext()).getSourceDataManager();
    List<IClipboardItem> clipboardItems = tm.getClipboardItems();
    
    for (IClipboardItem ci : clipboardItems)
    {
      ISourceData d = ci.getSourceData();
      d.setTransportType(TransportType.remote);
      itemList.add(d);
    }
    
    return itemList;
  }

}
