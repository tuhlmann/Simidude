package com.agynamix.simidude.clipboard;

import java.util.UUID;

import com.agynamix.platform.frontend.preferences.IPreferenceConstants;
import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.platform.infra.PlatformUtils;
import com.agynamix.simidude.impl.SimidudeApplicationContext;
import com.agynamix.simidude.infra.IItemAddedListener;
import com.agynamix.simidude.infra.ModelProvider;

public class AutomaticDownloadHandler implements IItemAddedListener {

  final SourceDataManager sourceDataManager;
  final UUID senderId;

  public static final String SERVICE_NAME = "AutomaticDownloadHandler";
  
  public AutomaticDownloadHandler(SourceDataManager sourceDataManager, UUID mySenderId)
  {
    this.sourceDataManager = sourceDataManager;
    this.senderId = mySenderId;
    sourceDataManager.addItemAddedListener(this);
  }

  /**
   * A new item was added to the list of clipboard items.
   * Check if we should acvtivate this item and/or automatically download
   * contents if that's somewhere remote. 
   */
  public void itemAdded(int insertPos, IClipboardItem item)
  {
    boolean isActivateItem = shouldActivateItem(insertPos, item);
    if (shouldDownloadContents(item))
    {
      downloadContents(item, isActivateItem);
    } else {
      if (isActivateItem)
      {
        activateItem(item);
      }
    }
  }
  
  private boolean shouldDownloadContents(IClipboardItem item)
  {
    if ((ApplicationBase.getContext().getConfiguration().getBoolean(IPreferenceConstants.AUTO_DOWNLOAD_CONTENTS))
        && (!senderId.equals(item.getSourceData().getSenderId())))
    {
      if ((sourceDataManager.isRetrieveContentsNeeded(item)) && (!sourceDataManager.isDownloadInProgress(item.getSourceData())))
      {
        return true;
      }
    }
    return false;
  }

  private boolean shouldActivateItem(int insertPos, IClipboardItem item)
  {
    if (ApplicationBase.getContext().getConfiguration().getBoolean(IPreferenceConstants.AUTO_ACTIVATE_NEW_ENTRY))
    {
      if (insertPos == 0)
      {
        return true; 
      }
    }
    return false;
  }
    
  /**
   * TODO When the item is no longer available it should be removed from the item list. A message should be sent to the tray bar icon.
   * This can only happen if the item is a file or directory.
   * @param item
   * @param isActivateItem
   */
  private void downloadContents(final IClipboardItem item, final boolean isActivateItem)
  {
    ModelProvider mp = ((SimidudeApplicationContext)ApplicationBase.getContext()).getModelProvider();
    mp.retrieveContentsForProxyObject(item.getSourceData(), new Runnable() {
      
      public void run()
      {
        if (isActivateItem)
        {
          activateItem(item);
        }
      }
    });
  }

  private void activateItem(final IClipboardItem item)
  {
    PlatformUtils.safeAsyncRunnable(new Runnable() {          
      public void run()
      {
        sourceDataManager.activateItem(item);
      }
    });
  }
  
}
