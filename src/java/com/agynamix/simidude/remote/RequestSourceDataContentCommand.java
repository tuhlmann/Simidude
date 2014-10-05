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

import java.io.File;

import com.agynamix.platform.net.AbstractRemoteCommand;
import com.agynamix.platform.net.IConnectorServerHandler;
import com.agynamix.simidude.source.impl.FileSourceData;

public class RequestSourceDataContentCommand extends AbstractRemoteCommand {

  private static final long serialVersionUID = 1L;

  final FileSourceData sourceData;
  
  public RequestSourceDataContentCommand(FileSourceData sourceData)
  {
    this.sourceData = sourceData;
  }

  public Object invoke(IConnectorServerHandler connectorServerHandler)
  {
    File f = sourceData.getFile();
//    if (f.exists() && f.canRead())
//    {
// We catch any error directly when trying to load the file and then report the failed file back.
      // FIXME: Diese Abfrage darf erst Daten liefern, nachdem die invoke-Funktion zurückgekehrt ist!!!
      connectorServerHandler.executeParallel(new SourceDataContentsUploader(connectorServerHandler, sourceData));  
      return Boolean.TRUE;
//    } else {
//      return Boolean.FALSE;
//    }
  }

}
