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
package com.agynamix.simidude.infra;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import com.agynamix.platform.frontend.preferences.ApplicationPreferenceDialog;
import com.agynamix.platform.frontend.preferences.IPreferenceConstants;
import com.agynamix.platform.frontend.preferences.IPreferenceDialogListener;
import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.platform.infra.ExecutorUtils;
import com.agynamix.platform.infra.PlatformUtils;
import com.agynamix.platform.net.ClientNode;
import com.agynamix.platform.net.ConnectionCtx;
import com.agynamix.platform.net.ConnectionUtils;
import com.agynamix.platform.net.NetworkAuthException;
import com.agynamix.platform.net.NetworkProtocolException;
import com.agynamix.simidude.impl.SimidudeApplicationContext;
import com.agynamix.simidude.remote.RemoteConnector;
import com.agynamix.simidude.remote.RemoteRemoveItemCommand;
import com.agynamix.simidude.remote.RequestSourceDataContentCommand;
import com.agynamix.simidude.remote.SourceDataContentsCollector;
import com.agynamix.simidude.source.ISourceData;
import com.agynamix.simidude.source.SourceDataStub;
import com.agynamix.simidude.source.ISourceData.SourceType;
import com.agynamix.simidude.source.impl.FileSourceData;

/**
 * Takes care of all things model.
 * The remote onnection is hooked up here as well as the monitoring of it or
 * access to model components.
 * @author tuhlmann
 *
 */
public class ModelProvider {

  public static final String SERVICE_NAME = "Modelprovider";
  
  protected final UUID senderId;
  
  boolean              preferenceDialogChanges = false;
  
  public ModelProvider(UUID senderId)
  {
    this.senderId = senderId;
  }
  
  public UUID getSenderId()
  {
    return this.senderId;
  }

  public void retrieveContentsForProxyObject(ISourceData sourceData)
  {
    retrieveContentsForProxyObject(sourceData, null);
  }
  
  /**
   * 
   * @param sourceData the sourceData item whose data should be loaded.
   * @param attachedCommand The command that should be run after the data is loaded.
   */
  public void retrieveContentsForProxyObject(ISourceData sourceData, Runnable attachedCommand)
  {
    // if item is not a local one we must ensure it holds its real data
    ClientNode sourceNode = null;
    try {
      if (!sourceData.getSenderId().equals(getSenderId()))
      {
        if (sourceData.getType() == SourceType.FILE)
        {
          if (!(sourceData instanceof FileSourceData))
          {
            throw new IllegalStateException("We can request contents only for FileSourceData objects.");
          }        
          if (!sourceData.isCached())
          {
            FileSourceData fsd = (FileSourceData) sourceData;
            RemoteConnector remoteConnector = ((SimidudeApplicationContext)ApplicationBase.getContext()).getRemoteConnector();
            sourceNode = remoteConnector.getConnector().getClientNode(fsd.getSenderId());
            if ((sourceNode != null) && (!sourceNode.isShutdown()))
            {
//              System.out.println("Get data from "+sourceNode);
              ConnectionCtx connectionCtx = ConnectionUtils.connectTo(remoteConnector.getConnector(), sourceNode);
              Boolean result = (Boolean) connectionCtx.invoke(new RequestSourceDataContentCommand(fsd));
              if (result.booleanValue())
              {
                // Expect arriving data
                ExecutorUtils.addParallelTask(new SourceDataContentsCollector(connectionCtx, ((FileSourceData)sourceData), attachedCommand));
              } else {
                PlatformUtils.showToggleErrorMessage("Client not reachable", "The client with the address "+sourceNode.getAddress().getHostAddress()+" can not be reached.\nMaybe the client is no longer running?", IPreferenceConstants.DIALOG_HOST_NOT_FOUND_ERR_SHOW);
                ((SimidudeApplicationContext)ApplicationBase.getContext()).getSourceDataManager().removeAndDontRemember(sourceData.getStub());
              }            
            } else {
              PlatformUtils.showToggleErrorMessage("Client not found", "The client that recorded this item can no longer be found.\nMaybe it is no longer running?", IPreferenceConstants.DIALOG_DOWNLOAD_ERR_SHOW);
              // Remove the item from our list
              ((SimidudeApplicationContext)ApplicationBase.getContext()).getSourceDataManager().removeAndDontRemember(sourceData.getStub());
            }          
          }
        }
      }
    } catch (NetworkProtocolException e)
    {
      e.printStackTrace();
    } catch (IOException e) {
      if (sourceNode != null)
      {
        PlatformUtils.showErrorMessageWithException("Client not reachable", "The client with the address "+sourceNode.getAddress().getHostAddress()+" can not be reached.\nMaybe the client is no longer running?", e);
      } else {
        e.printStackTrace();
      }
    } catch (NetworkAuthException e)
    {
      e.printStackTrace();
    }
    
  }

  public void openPreferencesDialog(Shell shell)
  {
    preferenceDialogChanges = false;
    ApplicationPreferenceDialog preferences = new ApplicationPreferenceDialog(shell);
    preferences.addPreferenceDialogListener(new IPreferenceDialogListener(){
      public void propertyChange(PropertyChangeEvent event)
      {
        preferenceDialogChanges = true;
      }
      public void dialogClosed(int result)
      {
        if ((result == Window.OK) && (preferenceDialogChanges))
        {
          // do we need to react on changes          
        }
      }
    });
    preferences.open();
  }

  /**
   * Calls all connected clients to tell them to remove the given item from their
   * ClipboardTable. If the argument is null, all items will be removed.
   * This command will be executed in a separate thread in order to not slow down the application.
   * @param item the item to remove from all connected clients. If the given item is null then
   * all items are removed.
   */
  public void networkRemoveItem(final SourceDataStub sourceDataStub)
  {
    final RemoteConnector remoteConnector = ((SimidudeApplicationContext)ApplicationBase.getContext()).getRemoteConnector();
    List<ClientNode> connectedClients = remoteConnector.getConnector().getConnectedClientList();
    networkRemoveItem(connectedClients, sourceDataStub);
  }

  public void networkRemoveItem(final List<ClientNode> connectedClients, final SourceDataStub sourceDataStub)
  {
    final RemoteConnector remoteConnector = ((SimidudeApplicationContext)ApplicationBase.getContext()).getRemoteConnector();
    ExecutorUtils.addParallelTask(new Runnable(){
      public void run()
      {
        for (ClientNode client : connectedClients)
        {
          try
          {
            if (!client.equals(remoteConnector.getConnector().getMyOwnNode()))
            {
              ConnectionCtx connectionCtx = ConnectionUtils.connectTo(remoteConnector.getConnector(), client);
              connectionCtx.invoke(new RemoteRemoveItemCommand(connectedClients, sourceDataStub));
              connectionCtx.disconnect();
            }
          } catch (Exception e)
          {
            e.printStackTrace();
          }          
        }
      }
    });
  }
  
}
