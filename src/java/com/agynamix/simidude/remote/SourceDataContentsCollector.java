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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.agynamix.platform.frontend.gui.ApplicationGUI;
import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.platform.infra.PlatformUtils;
import com.agynamix.platform.net.ConnectionCtx;
import com.agynamix.platform.net.NetworkProtocolException;
import com.agynamix.platform.net.protocol.Expt;
import com.agynamix.platform.net.protocol.ICommands;
import com.agynamix.platform.net.protocol.NodeCommand;
import com.agynamix.platform.net.protocol.Objt;
import com.agynamix.simidude.clipboard.SourceDataManager;
import com.agynamix.simidude.impl.SimidudeApplicationContext;
import com.agynamix.simidude.infra.CacheManagerFactory;
import com.agynamix.simidude.infra.IContentsCacheInfo;
import com.agynamix.simidude.infra.IContentsCacheManager;
import com.agynamix.simidude.source.SourceDataContents;
import com.agynamix.simidude.source.impl.FileSourceData;

public class SourceDataContentsCollector implements Runnable {

  final ConnectionCtx  connectionCtx;
  final FileSourceData fileSourceData;
  final Runnable       attachedCommand;
  
  public SourceDataContentsCollector(ConnectionCtx connectionCtx, FileSourceData fileSourceData, Runnable attachedCommand)
  {
    this.connectionCtx   = connectionCtx;
    this.fileSourceData  = fileSourceData;
    this.attachedCommand = attachedCommand;
  }

  public void run()
  {
    ApplicationGUI gui = ApplicationBase.getContext().getApplicationGUI();
    IProgressMonitor pm = gui.getStatusLineManager().getProgressMonitor();
    requestContentsForProxyObject(pm, connectionCtx, fileSourceData);
    if (attachedCommand != null)
    {
      try {
        attachedCommand.run();
      } catch (Exception e)
      {
        e.printStackTrace();
      }
    }
  }
  
  protected void requestContentsForProxyObject(final IProgressMonitor pm, final ConnectionCtx connectionCtx, FileSourceData sourceData)
  {
    PlatformUtils.safeAsyncRunnable(new Runnable() {
      public void run()
      {
        pm.beginTask("Download from "+connectionCtx.getHostAddress(), IProgressMonitor.UNKNOWN);
      }
    });
    IContentsCacheManager cacheManager = CacheManagerFactory.newContentsCacheManager();
    SourceDataManager sdm = ((SimidudeApplicationContext)ApplicationBase.getContext()).getSourceDataManager();
    sdm.setDownloadInProgress(sourceData, true);
    List<SourceDataContents> failedDownloads = new ArrayList<SourceDataContents>();
    try {
      SourceDataContents contents = null;
      boolean isFinished = false;
      cacheManager.begin();
      boolean isFollowUp = false;
      while ((!isFinished) && ((contents = readSourceDataContentsPackage(connectionCtx)) != null))
      {
        isFollowUp = true;
        if (contents.isLastPackage())
        {
          isFinished = true;
        } else if (contents.isAborted()) {
          failedDownloads.add(contents);
          cacheManager.removeAbortedItem(contents);
        } else {
          cacheManager.write(contents);
        }
      }
      IContentsCacheInfo cacheInfo = cacheManager.finish();
      if (cacheInfo != null) {
        sourceData.setContentsCacheInfo(cacheInfo);
      }
      
      // Change appearance of this IClipboardItem
      sdm.contentsReceived(failedDownloads); 
    } catch (RemoteException e)
    {
      Throwable t = e.getCause();
      System.out.println("Exception occured on the client: "+t.getMessage());
      if (t instanceof ContentsLoaderException)
      {
        ContentsLoaderException clE = (ContentsLoaderException) t;
        PlatformUtils.showErrorMessageWithException("Error while downloading contents", 
            "The connected client at "+connectionCtx.getHostAddress()+" experienced a problem\nreading the ressource "+
            clE.getFilename()+"\nThe client might not be able to read the ressource.", t);
      } else {
        PlatformUtils.showErrorMessageWithException("Error while downloading contents", 
            "The connected client at "+connectionCtx.getHostAddress()+" experienced a problem:\n"+t.getMessage(), t);        
      }
      cacheManager.abort();
    } catch (IOException e)
    {
      e.printStackTrace();
      cacheManager.abort();
    } catch (NetworkProtocolException e)
    {
      e.printStackTrace();
      cacheManager.abort();
    } finally {
      connectionCtx.disconnect();
      sdm.setDownloadInProgress(sourceData, false);
      PlatformUtils.safeAsyncRunnable(new Runnable() {
        public void run()
        {
          pm.done();
        }
      });
    }
    
  }

  private SourceDataContents readSourceDataContentsPackage(ConnectionCtx connectionCtx) throws IOException, NetworkProtocolException, RemoteException
  {
    NodeCommand nodeCmd = connectionCtx.getNodeCommandUtils().receiveCommand();
    if (ICommands.OBJT.equals(nodeCmd.getCommand()))
    {
      Objt objt = (Objt) nodeCmd;
      return (SourceDataContents) objt.getObject();
    } else if (ICommands.EXPT.equals(nodeCmd.getCommand()))
    {
      Expt expt = (Expt) nodeCmd;
      throw new RemoteException(expt.getException());
    } else {
      throw new NetworkProtocolException("Expected OBJT, but got "+nodeCmd.getCommand());
    }
  }
  

}
