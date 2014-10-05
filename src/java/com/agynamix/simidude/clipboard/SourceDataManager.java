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

package com.agynamix.simidude.clipboard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.ImageTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.agynamix.platform.frontend.preferences.IPreferenceConstants;
import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.platform.infra.DateUtils;
import com.agynamix.platform.infra.FileUtils;
import com.agynamix.platform.infra.IConfiguration;
import com.agynamix.platform.infra.PlatformUtils;
import com.agynamix.platform.infra.Tupel;
import com.agynamix.platform.infra.ZipUtils;
import com.agynamix.platform.log.ApplicationLog;
import com.agynamix.platform.net.ClientNode;
import com.agynamix.simidude.frontend.ctrl.SourceDataDialogController;
import com.agynamix.simidude.impl.SimidudeApplicationContext;
import com.agynamix.simidude.infra.IItemActivationListener;
import com.agynamix.simidude.infra.IItemAddedListener;
import com.agynamix.simidude.infra.IModelChangeListener;
import com.agynamix.simidude.infra.ModelProvider;
import com.agynamix.simidude.source.ISourceData;
import com.agynamix.simidude.source.ISourceDataListener;
import com.agynamix.simidude.source.SourceDataContents;
import com.agynamix.simidude.source.SourceDataStub;
import com.agynamix.simidude.source.ISourceData.SourceType;
import com.agynamix.simidude.source.impl.FileSourceData;
import com.agynamix.simidude.source.impl.ImageSourceData;
import com.agynamix.simidude.source.impl.TextSourceData;

/**
 * Our access to clipboard items
 * 
 * @author tuhlmann
 * 
 */
public class SourceDataManager implements ISourceDataListener {

  public static final String SERVICE_NAME   = "SourceDataManager";
  
  Logger log = ApplicationLog.getLogger(SourceDataManager.class);

  final IClipboardMonitor    clipboardMonitor;

  List<IClipboardItem>       clipboardItems = new CopyOnWriteArrayList<IClipboardItem>();
  
  /**
   * Those items that have been removed from the table.
   */
  List<IClipboardItem>       removedClipboardItems = new CopyOnWriteArrayList<IClipboardItem>();
  
  List<ISourceData>          downloadsInProgress = new CopyOnWriteArrayList<ISourceData>();
  
  List<IModelChangeListener>    modelChangeListeners    = new CopyOnWriteArrayList<IModelChangeListener>();
  List<IItemActivationListener> itemActivationListeners = new CopyOnWriteArrayList<IItemActivationListener>();
  List<IItemAddedListener>      itemAddedListeners      = new CopyOnWriteArrayList<IItemAddedListener>();

  Clipboard                  clipboard;
  
  ClipboardViewerFilter      currentViewerFilter = null;

  private SourceDataDialogController sourceDataDialogController;
  
  public SourceDataManager(IClipboardMonitor clipboardMonitor)
  {
    this.clipboardMonitor = clipboardMonitor;
  }

  public void addModelChangeListener(IModelChangeListener listener)
  {
    this.modelChangeListeners.add(listener);
  }

  public void removeModelChangeListener(IModelChangeListener listener)
  {
    this.modelChangeListeners.remove(listener);
  }
  
  public void addItemActivationListener(IItemActivationListener listener)
  {
    this.itemActivationListeners.add(listener);
  }

  public void removeItemActivationListener(IItemActivationListener listener)
  {
    this.itemActivationListeners.remove(listener);
  }
  
  public void addItemAddedListener(IItemAddedListener listener)
  {
    this.itemAddedListeners.add(listener);
  }
  
  public void removeItemAddedListener(IItemAddedListener listener)
  {
    this.itemAddedListeners.remove(listener);
  }
  
  /**
   * Add a listener that gets notified when the selection in the Clipboard Entry window changes.
   * @param l the listener
   */
  public void addSelectionChangedListener(ISelectionChangedListener l)
  {
    sourceDataDialogController.addSelectionChangedListener(l);
  }
  
  public void removeSelectionChangedListener(ISelectionChangedListener l)
  {
    sourceDataDialogController.removeSelectionChangedListener(l);
  }

  public List<IClipboardItem> getClipboardItems()
  {
    return Collections.unmodifiableList(clipboardItems);
  }

  public synchronized void sourceDataChanged(ISourceData data)
  {
//    System.out.println("Receive "+data.getTransportType()+" ("+data.getType()+") change for "+data.getSourceId()+": "+data.getText());
    Tupel<Integer, Boolean> itemFound = containsItem(getClipboardItems(), data);
//    System.out.println("Contains Item "+itemFound.getValue2()+" at pos "+itemFound.getValue1());
    int idx = itemFound.getValue1();
    boolean shouldReplace = itemFound.getValue2();
    if ((idx > -1) && (shouldReplace))
    {
//      System.out.println("Remove entry ("+idx+") "+data.getText());
      // move the found entry to the top of the list 
      clipboardItems.remove(idx);
    }
    if (shouldReplace)
    {
//      System.out.println("Insert At "+idx+": "+data.getText());
      List<IClipboardItem> old = getClipboardItems();    
      final Tupel<Integer, IClipboardItem> newItem = addItemTimeDesc(clipboardItems, data);
      if (idx < 0)
      {
        fireNewItemAdded(newItem.getValue1(), newItem.getValue2());
      }
      fireDataChanged(old, getClipboardItems());
    }
  }
  
  /**
   * Add the new item to the clipboard item list considering the items time stamp.
   * So new items would appear on the top of the list while items synchronized
   * from other peers would appear in their appropriate positions.
   * @param clipboardItems the list of clipboard items we maintain
   * @param data the new arrived ISourceData entry.
   */
  private synchronized Tupel<Integer, IClipboardItem> addItemTimeDesc(List<IClipboardItem> clipboardItems, ISourceData data)
  {
    Date dataStamp = data.getCreationDate();
    IClipboardItem clipItem = ClipboardItemFactory.createItemFromSourceData(this, data);
//    String clipStr = clipItem.getDescription();
    boolean inserted = false;
    int insertPos = 0;
    if (clipboardItems.size() > 0)
    {
      for (int i = 0; i < clipboardItems.size(); i++)
      {
        IClipboardItem item = clipboardItems.get(i);
        Date itemStamp = item.getSourceData().getCreationDate();
        String itemStr = item.getDescription();
//        System.out.println(itemStr+"("+DateUtils.date2string("HH:mm:ss SSS", dataStamp)+") vs "+itemStr+"("+DateUtils.date2string("HH:mm:ss SSS", itemStamp)+")");
        if (dataStamp.getTime() >= itemStamp.getTime())
        {
//          System.out.println("Inserting AT POS "+i+": "+data.getSourceId());
          inserted = true;
          insertPos = i;
          clipboardItems.add(insertPos, clipItem);
          break;
        }
      }
    }
    if (!inserted)
    {
      clipboardItems.add(clipItem);
//      clipboardItems.add(insertPos, clipItem);
//      System.out.println("Inserting ITEM "+data.getSourceId());
    }
    return new Tupel<Integer, IClipboardItem>(insertPos, clipItem);
  }

  private Tupel<Integer, Boolean> containsItem(List<IClipboardItem> clipboardItems, ISourceData sourceData)
  {
    for (int i = 0; i < clipboardItems.size(); i++)
    {
      IClipboardItem item = clipboardItems.get(i);
      if (item.getSourceData().getSourceId().equals(sourceData.getSourceId()))
      {
        return new Tupel<Integer, Boolean>(i, true);
      }
      if (item.getSourceData().equals(sourceData))
      {
        // we keep the newest item
        if (item.getSourceData().getCreationDate().getTime() < sourceData.getCreationDate().getTime())
        {
          return new Tupel<Integer, Boolean>(i, true);
        } else {
          return new Tupel<Integer, Boolean>(i, false);
        }
      }
    }
    return new Tupel<Integer, Boolean>(-1, true);
  }

//  /**
//   * Insert a new clipboard item at index 0.
//   * @param item new clipboard item
//   */
//  public void add(IClipboardItem item)
//  {
//    List<IClipboardItem> old = getClipboardItems();    
//    clipboardItems.add(0, item);
//    fireDataChanged(old, getClipboardItems());
//  }
  
  public synchronized void activateItem(int selectionIndex)
  {
    if (clipboardItems.size() > selectionIndex)
    {
      activateItem(clipboardItems.get(selectionIndex));
    }
  }
  
  public IClipboardItem getSelectedItem()
  {
    ClipboardTable clipboardTable = getSourceDataDialogController().getClipboardTable();
    TableItem[] tableItems = clipboardTable.getTable().getSelection();
    if ((tableItems != null) && (tableItems.length > 0))
    {
      return (IClipboardItem) tableItems[0].getData();
    }
    return null;
  }
  
  public int getSelectionIndex()
  {
    ClipboardTable clipboardTable = getSourceDataDialogController().getClipboardTable();
    return clipboardTable.getTable().getSelectionIndex();
  }
  
  /**
   * Activate the selected entry
   */
  public void activateItem()
  {
    IClipboardItem item = getSelectedItem();
    if (item != null)
    {
      activateItem(item);
    }
  }  
  
  public void activateItem(IClipboardItem item)
  {
    ISourceData sourceData = item.getSourceData();   
    
    try {    
      clipboardMonitor.itemActivated(sourceData);
      
      TextTransfer textTransfer   = TextTransfer.getInstance();
      FileTransfer fileTransfer   = FileTransfer.getInstance();
      ImageTransfer imageTransfer = ImageTransfer.getInstance();
      Object[] contents = null;
      Transfer[] transferTypes = null;
      switch (item.getType())
      {
        case FILE:
          FileSourceData fsd = (FileSourceData) item.getSourceData();
          String[] fnames = new String[] {fsd.getLocalFilename()};
          if (isRetrieveContentsNeeded(item))
          {
            contents = new Object[] {item.getDescription()};
            transferTypes = new Transfer[] {textTransfer};                        
          } else {
            contents = new Object[] {fnames, item.getDescription()};
            transferTypes = new Transfer[] {fileTransfer, textTransfer};            
          }
          break;
        case TEXT:          
          contents = new Object[] { item.getDescription() };
          transferTypes = new Transfer[] { textTransfer };
          break;
        case IMAGE:
          contents = new Object[] { ((ImageSourceData)item.getSourceData()).getImageData() };
          transferTypes = new Transfer[] { imageTransfer };          
          break;
      }     
      getClipboard().setContents(contents, transferTypes);
      fireDataActivated(item);
    } catch (Throwable t) {
      log.log(Level.SEVERE, "Error activating item of type "+sourceData.getType()+": "+sourceData.getText(), t);
    }
  }
  
  public IClipboardItem removeSelectedEntry()
  {
    // Get selection index.
    ClipboardTable clipboardTable = getSourceDataDialogController().getClipboardTable();
    TableItem[] tableItems = clipboardTable.getTable().getSelection();
    if ((tableItems != null) && (tableItems.length > 0))
    {
      IClipboardItem item = (IClipboardItem) tableItems[0].getData(); 
      return removeItem(item);
    }
    return null;
  }
  
  public IClipboardItem removeItem(IClipboardItem item)
  {
    if (item != null)
    {
      List<IClipboardItem> old = getClipboardItems();
      if (clipboardItems.remove(item))
      {
        removedClipboardItems.add(item.deleteContents());
        fireDataChanged(old, getClipboardItems());
      }
    }
    return item;
  }
  
  public IClipboardItem removeAndDontRemember(SourceDataStub stub)
  {
    IClipboardItem item = null;
    if (stub != null) {
      item = getClipboardItem(stub);
      if (item != null)
      {
        List<IClipboardItem> old = getClipboardItems();
        if (clipboardItems.remove(item))
        {
          fireDataChanged(old, getClipboardItems());
        }
      }      
    }
    return item;
  }
  
  public void removeAll()
  {    
    List<IClipboardItem> old = getClipboardItems();
    if (old.size() > 0)
    {
      for (IClipboardItem item : old)
      {
        removedClipboardItems.add(item.deleteContents());
      }
      clipboardItems.clear();
      fireDataChanged(old, getClipboardItems());
    }
  }

  public void contentsReceived(List<SourceDataContents> failedDownloads)
  {    
    fireDataChanged(getClipboardItems(), getClipboardItems());
    if (!failedDownloads.isEmpty()) {
      StringBuilder sb = new StringBuilder();
      for (SourceDataContents c : failedDownloads) {
        sb.append("File failed: "+c.getName());
        if (c.getException() != null) {
          sb.append(": "+getTopLevelException(c.getException()).getMessage()+"\n");
        }
      }
      sb.append("\n");
      PlatformUtils.showWarningMessage("Download Error", "Not all files could be downloaded successfully.", sb.toString());
    }
  }
  
  private Throwable getTopLevelException(Throwable t)
  {
    return (t.getCause() != null) ? getTopLevelException(t.getCause()) : t;
  }
  
  private void fireDataChanged(Object oldValue, Object newValue)
  {
    for (IModelChangeListener listener : modelChangeListeners)
    {
      listener.modelChanged(oldValue, newValue);
    }
  }
  
  private void fireDataActivated(IClipboardItem item)
  {
    for (IItemActivationListener listener : itemActivationListeners)
    {
      listener.itemActivated(item);
    }
  }
  
  private void fireNewItemAdded(int insertPos, IClipboardItem item)
  {
    for (IItemAddedListener listener : itemAddedListeners)
    {
      listener.itemAdded(insertPos, item);
    }    
  }

  private Clipboard getClipboard()
  {
    if (clipboard == null)
    {
      clipboard = ApplicationBase.getContext().getClipboard();      
    }
    return clipboard;
  }

  public void setClipboardMonitorEnabled(boolean enable)
  {
    clipboardMonitor.setClipboardMonitorEnabled(enable);
  }
  
  public boolean isClipboardMonitorEnabled()
  {
    return clipboardMonitor.isClipboardMonitorEnabled();
  }
  
  public void setClipboardMonitorTypeEnabled(SourceType sourceType, boolean enable)
  {
    clipboardMonitor.setClipboardMonitorTypeEnabled(sourceType, enable);    
  }

  
  public boolean isClipboardEmpty()
  {
    return clipboardMonitor.isClipboardEmpty();
  }

  public void emptyClipboard()
  {
    clipboardMonitor.emptyClipboard();
  }

  public List<SourceDataStub> getSourceDataStubEntries()
  {
    List<SourceDataStub> stubList = new ArrayList<SourceDataStub>();
    for (IClipboardItem item : clipboardItems)
    {
      stubList.add(item.getSourceData().getStub());
    }
    return stubList;
  }

  public List<SourceDataStub> getRemovedSourceDataStubEntries()
  {
    List<SourceDataStub> stubList = new ArrayList<SourceDataStub>();
    for (IClipboardItem item : removedClipboardItems)
    {
      stubList.add(item.getSourceData().getStub());
    }
    return stubList;
  }
  
  /**
   * Find the clipboard item for this stub
   * @param stub a stub instance describeing this clipboard item.
   * @return the found clipboard item or null if it can't be found.
   */
  public IClipboardItem getClipboardItem(SourceDataStub stub)
  {
    for (IClipboardItem item : clipboardItems)
    {
      if (item.getSourceData().getStub().equals(stub))
      {
        return item;
      }
    }
    
    // Not found here, maybe it was deleted recently
    for (IClipboardItem item : removedClipboardItems)
    {
      if (item.getSourceData().getStub().equals(stub))
      {
        return item;
      }
    }
   
    // Nothing found
    return null;
  }

  /**
   * Get the latest clipboard item from this machine.
   * @return an IClipboardItem instance or null if none was found.
   */
  public IClipboardItem getNewestClipboardItem()
  {
    ClientNode myOwnNode = ((SimidudeApplicationContext)ApplicationBase.getContext()).getRemoteConnector().getConnector().getMyOwnNode();
    return getNewestClipboardItem(myOwnNode);
  }

  /**
   * Get the latest clipboard item from the machine denoted by this ClientNode.
   * @return an IClipboardItem instance or null if none was found.
   */
  public IClipboardItem getNewestClipboardItem(ClientNode clientNode)
  {
    for (IClipboardItem item : getClipboardItems())
    {
      if (item.getSourceData().getSenderId().equals(clientNode.getNodeId()))
      {
        return item;
      }
    }
    return null;
  }
  

  
  public IClipboardItem getClipboardItem(int selectionIndex)
  {
    if (clipboardItems.size() > selectionIndex)
    {
      return clipboardItems.get(selectionIndex);
    } else {
      return null;
    }
  }
  
  /**
   * Checks if the current item needs to retrieve contents from a remote peer if the user wants
   * to drag the file or directory that this item represents.
   * @param item the current checked ICLipboardItem
   * @return true if we need to retrieve contents prior to a drag, false otherwise. 
   */
  public boolean isRetrieveContentsNeeded(IClipboardItem item)
  {
    if (item == null)
    {
      return false;
    }
    ModelProvider mp = ((SimidudeApplicationContext)ApplicationBase.getContext()).getModelProvider();
    ISourceData sourceData = item.getSourceData();
    if (sourceData.getSenderId().equals(mp.getSenderId())) // local data
    {
      return false;
    }
    if (sourceData.getType() == SourceType.FILE)
    {
      FileSourceData fsd = (FileSourceData) sourceData;
      if (!fsd.isCached())
      {
        return true;
      }
    }
    return false;
  }

  /**
   * Tell the SourceDataManager that a download is in progress
   * @param sourceData download for this item is in progress
   * @param isInProgress true if download is in progress, false if download has finished
   */
  public void setDownloadInProgress(ISourceData sourceData, boolean isInProgress)
  {
    if (isInProgress)
    {
      downloadsInProgress.add(sourceData);
    } else {
      downloadsInProgress.remove(sourceData);
    }      
  }

  public boolean isDownloadInProgress(ISourceData sourceData)
  {
    return downloadsInProgress.contains(sourceData);
  }

  public void setSourceDataDialogController(SourceDataDialogController sourceDataController)
  {
    this.sourceDataDialogController = sourceDataController;
  }
  
  public SourceDataDialogController getSourceDataDialogController()
  {
    return this.sourceDataDialogController;
  }

  public void filterClipboardItems(String searchStr)
  {
    TableViewer tv = getSourceDataDialogController().getClipboardTable().getTableViewer();
    if (searchStr == null)
    {
      if (currentViewerFilter != null)
      {
        tv.removeFilter(currentViewerFilter);
        currentViewerFilter = null;
      }
      return;
    }
    
    if (currentViewerFilter != null)
    {
      if (currentViewerFilter.getSearchString().equals(searchStr))
      {
        return;
      }
      tv.removeFilter(currentViewerFilter);
    }
    currentViewerFilter = new ClipboardViewerFilter(searchStr);
    tv.addFilter(currentViewerFilter);
  }
  
  public void saveClipboardItemAs(final IClipboardItem item, final boolean compress)
  {
    final Shell shell = sourceDataDialogController.getClipboardTable().getTable().getShell();
    String path;
    IConfiguration config = ApplicationBase.getContext().getConfiguration();
    String entryExtension = FileUtils.DEFAULT_TEXT_FILE_EXTENSION;
    
    if (item.getType() == SourceType.FILE)
    {
      FileSourceData fsd = (FileSourceData) item.getSourceData();
      if (fsd.isDirectory())
      {
        DirectoryDialog dialog = new DirectoryDialog(shell, SWT.SHEET);
        String lastTimeDir = config.getProperty(IPreferenceConstants.CLIPBOARDTABLE_LAST_SAVED_DIR_PATH);
        dialog.setFilterPath(lastTimeDir);
        String saveTo = dialog.open();
        if (saveTo != null)
        {
          config.setProperty(IPreferenceConstants.CLIPBOARDTABLE_LAST_SAVED_DIR_PATH, saveTo);
          saveTo = saveTo + File.separator + fsd.getFile().getName();
          if (compress)
          {
            saveTo += FileUtils.DEFAULT_ZIP_EXTENSION;
          }
          File f = new File(saveTo);
          if (f.exists())
          {
            if (PlatformUtils.showOverwriteFilesDialog(f))
            {
              path = saveTo;
            } else {
              path = null;
            }
          } else {
            path = saveTo;
          }
        } else {
          path = null;
        }
      } else {
        FileDialog dialog = new FileDialog(shell, SWT.SAVE | SWT.SHEET);
        String suggestion = fsd.getFile().getName();
        if (compress)
        {
          suggestion += FileUtils.DEFAULT_ZIP_EXTENSION;
        }
        dialog.setFilterPath(config.getProperty(IPreferenceConstants.CLIPBOARDTABLE_LAST_SAVED_FILE_PATH));
        dialog.setFileName(suggestion);
        dialog.setOverwrite(true);
        path = dialog.open();  
        if (path != null)
        {
          config.setProperty(IPreferenceConstants.CLIPBOARDTABLE_LAST_SAVED_FILE_PATH, new File(path).getParent());
        }
      }
    // Either Clipboard Text or Image  
    } else {
      String suggestedName = "";
      String[] suggestedExtensions = null;
      if (item.getType() == SourceType.TEXT)
      {
        suggestedName = "ClipboardText";
        if (PlatformUtils.isMacOs())
        {
          if (compress)
          {
            suggestedName += FileUtils.DEFAULT_ZIP_EXTENSION;
          } else {
            suggestedName += FileUtils.DEFAULT_TEXT_FILE_EXTENSION;          
          }
        }
        suggestedExtensions = FileUtils.getTextFileExtensions(compress);
      } else if (item.getType() == SourceType.IMAGE)
      {
        suggestedName = "ClipboardImage";
        if (PlatformUtils.isMacOs())
        {
          if (compress)
          {
            suggestedName += FileUtils.DEFAULT_ZIP_EXTENSION;
          } else {
            suggestedName += FileUtils.DEFAULT_IMAGE_FILE_EXTENSION;         
          }
        }
        suggestedExtensions = FileUtils.getImageFileExtensions();
        entryExtension = FileUtils.DEFAULT_IMAGE_FILE_EXTENSION;
      }
      FileDialog dialog = new FileDialog(shell, SWT.SAVE | SWT.SHEET);
      dialog.setFilterPath(config.getProperty(IPreferenceConstants.CLIPBOARDTABLE_LAST_SAVED_FILE_PATH));
      dialog.setFileName(suggestedName);
      dialog.setFilterExtensions(suggestedExtensions);
      dialog.setOverwrite(true);
      String saveTo = dialog.open();                              
      if (saveTo != null)
      {
//        String choosenExtension = suggestedExtensions[dialog.getFilterIndex()];
//        System.out.println(choosenExtension);
//        System.out.println(saveTo);
        path = saveTo;
        config.setProperty(IPreferenceConstants.CLIPBOARDTABLE_LAST_SAVED_FILE_PATH, new File(path).getParent());
      } else {
        path = null;
      }
    }
    if (path != null)
    {
      String entryName = path;
      if (compress)
      {
        entryName = FileUtils.replaceLastExtension(new File(path).getName(), entryExtension);
      }
      final String entryName2 = entryName;
      final String path2 = path;
      if ((isRetrieveContentsNeeded(item)) && (!isDownloadInProgress(item.getSourceData())))
      {
        ModelProvider mp = ((SimidudeApplicationContext)ApplicationBase.getContext()).getModelProvider();
        mp.retrieveContentsForProxyObject(item.getSourceData(), new Runnable(){
          public void run()
          {
            saveAs(path2, entryName2, item.getSourceData(), compress);
          }
        });
      } else {
        saveAs(path2, entryName2, item.getSourceData(), compress);
      }
    }
  }  

  /**
   * Save SourceData item to the given location. If the destination already exists, the user has already been
   * asked if he wishes to overwrite. If we are here, we know the user clicked yes so we do overwrite existing files
   * and directories.
   * @param path the destination path. 
   * @param sourceData the ISourceData entry that should be saved.
   */
  public void saveAs(final String path, String entryName, ISourceData sourceData, final boolean compress)
  {
    final File dest = new File(path);
    switch (sourceData.getType())
    {
      case FILE:
        final FileSourceData fsd = (FileSourceData) sourceData;
        final File src = new File(fsd.getLocalFilename());
          final IProgressMonitor pm = ApplicationBase.getContext().getApplicationGUI().getStatusLineManager().getProgressMonitor();
              PlatformUtils.safeSyncRunnable(new Runnable() {
                public void run()
                {
//                  System.out.println("before begin task");
                  pm.beginTask("Zipping up "+path, IProgressMonitor.UNKNOWN);
//                  System.out.println("After begin task");
                }
              });
//          pm.beginTask("Zipping up "+path, IProgressMonitor.UNKNOWN);
          Job copyFileJob = new Job("Zipping up "+path) {
            @Override
            protected IStatus run(final IProgressMonitor pm)
            {
              if (compress)
              {           
//                System.out.println("Vor ZIP");
                ZipUtils.zipFile(src, dest);
//                System.out.println("Nach Zip");
              } else {
                try
                {
                  FileUtils.copyRecursive(src, dest);
                } catch (IOException e)
                {
                  PlatformUtils.showErrorMessageWithException("An Error Occured", "An error occured while copying "+
                      fsd.getLocalFilename()+" to "+dest.getAbsolutePath(), e);
                }            
              }              
              return Status.OK_STATUS;
            }
          };
          copyFileJob.schedule();
          copyFileJob.addJobChangeListener(new JobChangeAdapter(){
            public void done(IJobChangeEvent arg0)
            {
              PlatformUtils.safeAsyncRunnable(new Runnable() {
                public void run()
                {
//                  System.out.println("Fertig");
                  pm.done();
                }
              });              
            }
          });
          break;
      case TEXT:
        TextSourceData tsd = (TextSourceData) sourceData;
        try
        {
          FileUtils.writeTextToFile(dest, entryName, tsd.getText(), compress);
        } catch (IOException e)
        {
          PlatformUtils.showErrorMessageWithException("An Error Occured", "An error occured while saving a clipboard text entry: "+e.getMessage(), e);
        }
        break;
      case IMAGE:
        ImageSourceData isd = (ImageSourceData) sourceData;
        try {
          FileUtils.writeImageToFile(dest, entryName, isd.getImageData(), compress);          
        } catch (Exception e)
        {
          PlatformUtils.showErrorMessageWithException("An Error Occured", "An error occured while saving a clipboard image entry: "+e.getMessage(), e);
        }
        break;
      default:
        PlatformUtils.showErrorMessage("An Error Occured", "Tried to save an element of unknown type: "+sourceData.getType().toString());
    }
  }

  public void selectPreviousEntry(int currentSelectionIndex)
  {
    if (currentSelectionIndex > -1)
    {
      if (currentSelectionIndex > 0)
      {
        currentSelectionIndex--;
      }
      saveSelectEntry(currentSelectionIndex);
    }
  }

  public void saveSelectEntry(int selectionIndex)
  {
    if (selectionIndex > -1)
    {
      ClipboardTable clipboardTable = getSourceDataDialogController().getClipboardTable();
      Table table = clipboardTable.getTable();
      if (table.getItemCount() <= selectionIndex)
      {
        selectionIndex = table.getItemCount() - 1;
      }
      if (selectionIndex >= 0)
      {
        table.setSelection(selectionIndex);
      }
    }
  }

}
