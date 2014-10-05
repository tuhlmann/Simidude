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
package com.agynamix.simidude.clipboard;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TransferData;

import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.simidude.source.ISourceData;
import com.agynamix.simidude.source.ISourceData.SourceType;

public class PollingClipboardMonitor extends ClipboardMonitorBase {

  public final static int POLLING_SLEEP_TIME      = 2000;

  Clipboard               clipboard;
  ISourceData[]           currentSourceData        = null;
  ISourceData[]           currentCompareSourceData = null;
  boolean                 newDataAvailable         = false;
  boolean                 clipboardMonitorEnabled  = true;
  
  Map<ISourceData.SourceType, Boolean> clpItemtypesEnabledMap = new Hashtable<ISourceData.SourceType, Boolean>();

  public PollingClipboardMonitor()
  {
    clpItemtypesEnabledMap.put(SourceType.TEXT,  true);
    clpItemtypesEnabledMap.put(SourceType.IMAGE, true);
    clpItemtypesEnabledMap.put(SourceType.FILE,  true);
  }

  public boolean isClipboardMonitorEnabled()
  {
    return clipboardMonitorEnabled;
  }

  public void setClipboardMonitorEnabled(boolean enable)
  {
    this.clipboardMonitorEnabled = enable;
  }
  
  public void setClipboardMonitorTypeEnabled(SourceType sourceType, boolean enable)
  {
    clpItemtypesEnabledMap.put(sourceType, enable);
//    System.out.println("SET "+sourceType+" to "+clpItemtypesEnabledMap.get(sourceType)+" with Map size "+clpItemtypesEnabledMap.size());
  }
  
  public boolean isClipboardEmpty()
  {
    Clipboard clipboard = getClipboard();
    TransferData[] td = clipboard.getAvailableTypes();
    if ((td == null) || (td.length == 0))
    {
      return true;
    }
    return false;
  }
  
  public void emptyClipboard()
  {
    getClipboard().clearContents();
  }

  public ISourceData[] getData()
  {
    if (newDataAvailable)
    {
      newDataAvailable = false;
      return currentSourceData;
    }
    return null;
  }

  /**
   * Check the current data. Algorithmus: - itemActivated wird gesetzt, wenn im ClipboardTable ein Eintrag aktiviert,
   * also ins Clipboard geschrieben wird. - Hier muss so lange gewartet werden, bis dieser aktivierte Eintrag wieder im
   * Clipboard erscheint, erst dann darf activateItem wieder zurueckgesetzt werden.
   */
  public boolean isDataAvailable()
  {
    if (!clipboardMonitorEnabled)
    {
      return false;
    }
    newDataAvailable = false;
    ISourceData[] sourceData = saveProcessClipboard(getClipboard(), clpItemtypesEnabledMap);
    if ((sourceData != null) && (sourceData.length > 0) && (sourceData[0] != null)) // at least one entry
    {
      if (currentSourceData == null)
      {
        currentSourceData = sourceData;
        currentCompareSourceData = createEqualsCopy(sourceData);
        if (!itemActivated) // should never happen anyway
        {
          newDataAvailable = true;
        }
      } else
      {
        if (currentSourceData.length != sourceData.length)
        {
          currentSourceData = sourceData;
          currentCompareSourceData = createEqualsCopy(sourceData);
          if (!itemActivated)
          {
            newDataAvailable = true;
          }
        } else
        {
          if ((sourceData.length > 0) && (currentCompareSourceData.length > 0))
          {
            if (!sourceData[0].getType().equals(currentCompareSourceData[0].getType()))
            {
              currentSourceData = sourceData;
              currentCompareSourceData = createEqualsCopy(sourceData);
              if (!itemActivated)
              {
                newDataAvailable = true;
              }
            } else
            {
              if (!sourceData[0].equals(currentCompareSourceData[0]))
              {
                currentSourceData = sourceData;
                currentCompareSourceData = createEqualsCopy(sourceData);
                if (!itemActivated)
                {
//                  System.out.println("New SourceData available");
                  newDataAvailable = true;
                }
              }
            }
          }
        }
      }
      // Check if we have read our own activated item again
      if ((itemActivated) && (activatedSourceData != null))
      {
        String activatedTxt = activatedSourceData.getText();
        String sourceDataTxt = sourceData[0].getText();
        if ((activatedTxt != null) && (sourceDataTxt != null))
        {
          if (activatedTxt.equals(sourceDataTxt))
          {
//            System.out.println("Reset itemActivated to false");
            itemActivated = false;
            activatedSourceData = null;
          }
        }
      }
    }
    return newDataAvailable;
  }

  @Override
  public int getSleepTime()
  {
    return POLLING_SLEEP_TIME;
  }

  private Clipboard getClipboard()
  {
    if (clipboard == null)
    {
      clipboard = ApplicationBase.getContext().getClipboard();
    }
    return clipboard;
  }

  protected ISourceData[] createEqualsCopy(ISourceData[] sourceData)
  {
    if (sourceData != null)
    {
      ISourceData[] copied = new ISourceData[sourceData.length];
      for (int i = 0; i < sourceData.length; i++)
      {
        copied[i] = sourceData[i].equalsCopy();
      }
      return copied;
    } else {
      return null;
    }
  }

  
}
