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

import java.io.IOException;

import com.agynamix.platform.net.IConnectorServerHandler;
import com.agynamix.simidude.clipboard.SourceDataContentsLoader;
import com.agynamix.simidude.source.SourceDataContents;
import com.agynamix.simidude.source.impl.FileSourceData;

public class SourceDataContentsUploader implements Runnable {

   final FileSourceData          sourceData;
   final IConnectorServerHandler connectorServerHandler;
   
   public SourceDataContentsUploader(IConnectorServerHandler connectorServerHandler, FileSourceData sourceData)
   {
     this.connectorServerHandler = connectorServerHandler;
     this.sourceData = sourceData;
   }

  public void run()
  {
    try
    {
      try
      {
        Thread.sleep(200);
      } catch (InterruptedException ignore){} // Wait until the instantiating method has returned.

      SourceDataContentsLoader loader = new SourceDataContentsLoader(sourceData);
      SourceDataContents contents = null;
      while ((contents = loader.loadContents()) != null)
      {
        connectorServerHandler.sendSourceDataContents(contents);
      }      
//    } catch (ContentsLoaderException e)
//    {
//      System.out.println("Error while trying to send file: "+e.getFile());
////      e.printStackTrace();
//      abortTransaction(e);
    } catch (IOException e)
    {
      System.out.println("Connection error: "+e.getMessage());
      abortTransaction(e);
    }
  }
  
  private void abortTransaction(Exception exception)
  {
    try {
      connectorServerHandler.sendException(exception);
    } catch (IOException e2)
    {
      System.out.println("Error sending abort signal: "+e2.getMessage());
    }    
  }
  
}
