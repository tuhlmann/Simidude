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

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.agynamix.platform.frontend.dialogs.SimpleInformationViewerDialog;
import com.agynamix.platform.icons.PlatformIcons;
import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.platform.infra.FileUtils;
import com.agynamix.platform.infra.PlatformColors;
import com.agynamix.platform.infra.PlatformUtils;
import com.agynamix.platform.log.ClipboardItemDebugInfo;
import com.agynamix.simidude.frontend.gui.ClipboardTableDragSource;
import com.agynamix.simidude.frontend.gui.ClipboardTableDropTarget;
import com.agynamix.simidude.impl.SimidudeApplicationContext;
import com.agynamix.simidude.infra.IModelChangeListener;
import com.agynamix.simidude.infra.ModelProvider;
import com.agynamix.simidude.infra.SimidudeUtils;
import com.agynamix.simidude.source.ISourceData;
import com.agynamix.simidude.source.ISourceData.SourceType;
import com.agynamix.simidude.source.impl.FileSourceData;
import com.agynamix.simidude.source.impl.ImageSourceData;
import com.agynamix.simidude.source.impl.TextSourceData;
import com.agynamix.simidude.source.impl.TextSourceData.TextType;

/**
 * Wraps the view part for the clipboard table
 * 
 * @author tuhlmann
 * 
 */
public class ClipboardTable {

  public final static int NAME_COL  = 0;
  
  public final static int columnCount = 1;

  final SourceDataManager    sourceDataManager;
  final TableViewer          tableViewer;
  
  TableViewerColumn          itemColumn;
  Composite                  tableParent;
  
  private final static int   KEY_BACKSPACE = 8;

  boolean dragInProgress = false;
  
  public ClipboardTable(SourceDataManager sourceDataMan, Composite parent, int style)
  {
    this.sourceDataManager = sourceDataMan;
    
    tableViewer = createTableViewer(parent);
    sourceDataManager.addModelChangeListener(new IModelChangeListener() {
      public void modelChanged(Object oldValue, Object newValue)
      {
        tableViewer.getTable().getDisplay().syncExec(new Runnable(){
          public void run()
          {
            tableViewer.setInput(sourceDataManager.getClipboardItems());
            if (tableViewer.getTable().getItemCount() > 0)
            {
              tableViewer.getTable().setSelection(-1);
              tableViewer.getTable().showItem(tableViewer.getTable().getItem(0));
            }
//            adjustTableSize();
          }
        });
      }
    });
    
    // hook drag support
    new ClipboardTableDragSource(this, sourceDataManager);    
    new ClipboardTableDropTarget(this, sourceDataManager);       
  }
  
  public void addSelectionChangeListener(ISelectionChangedListener l)
  {
    tableViewer.addSelectionChangedListener(l);
  }
  
  public void removeSelectionChangeListener(ISelectionChangedListener l)
  {
    tableViewer.removeSelectionChangedListener(l);
  }
  
  private TableViewer createTableViewer(final Composite parent)
  {    
    tableParent = parent;
    final TableViewer tableViewer = new TableViewer(tableParent, SWT.SINGLE | SWT.FULL_SELECTION | SWT.HIDE_SELECTION |
                                                      SWT.FLAT | SWT.BORDER | SWT.V_SCROLL);

    tableViewer.getTable().setLinesVisible(true);
    tableViewer.getTable().setHeaderVisible(false);

    ColumnViewerToolTipSupport.enableFor(tableViewer, ToolTip.NO_RECREATE);
    
    final OptimizedIndexSearcher searcher = new OptimizedIndexSearcher();

    itemColumn = new TableViewerColumn(tableViewer, SWT.LEFT, NAME_COL);
    itemColumn.getColumn().setText("Item");
    itemColumn.setLabelProvider(new ColumnLabelProvider() {
      boolean even = true;
      Color oddColor = null;
       
      @Override
      public String getText(Object element)
      {
        String text = "";
        IClipboardItem item = (IClipboardItem) element;
        text = item.getShortDescription();
        return text;
      }

      @Override
      /**
       * Display an icon depending of the type of clipboard entry.
       */
      public Image getImage(Object element)
      {
        return ((IClipboardItem)element).getImage();
      }
      
      public Color getBackground(Object element)
      {
        if (even)
        {
          return null;
        } else
        {
          if (oddColor == null)
          {
            oddColor = PlatformColors.get(PlatformColors.TRANSFER_TABLE_ALT_COLOR);
          }
          return oddColor;
        }
      }
      
      public String getToolTipText(Object element) {
        IClipboardItem item = (IClipboardItem) element;
        return item.getTooltip();
      }

      public Point getToolTipShift(Object object) {
        return new Point(5, 5);
      }

      public int getToolTipDisplayDelayTime(Object object) {
        return 200;
      }

      public int getToolTipTimeDisplayed(Object object) {
        return 10000;
      }

      public void update(ViewerCell cell)
      {
        even = searcher.isEven((TableItem) cell.getItem());
        super.update(cell);
      }
    });
      
    tableViewer.setContentProvider(new ClipboardTableContentProvider());
//    tableViewer.setLabelProvider(new ClipboardTableLabelProvider());

    tableParent.addControlListener(new ControlAdapter() {
      public void controlResized(ControlEvent e) {
        adjustTableSize();
      }
    });
    
    tableViewer.getTable().setMenu(createPopupMenu(tableViewer));
    hookListeners(tableViewer);
    
    return tableViewer;
  }

  public Table getTable()
  {
    return tableViewer.getTable();
  }

  public TableViewer getTableViewer()
  {
    return tableViewer;
  }

  public boolean isDragInProgress()
  {
    return this.dragInProgress;
  }
  
  public void setDragInProgress(boolean dragInProgress)
  {
    this.dragInProgress = dragInProgress;
  }
  
  public void adjustTableSize()
  {
    Rectangle area = tableParent.getClientArea();
    final Table table = tableViewer.getTable();
    Point preferredSize = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
    int width = area.width - 2*table.getBorderWidth();
    if (preferredSize.y > area.height + table.getHeaderHeight()) {
      // Subtract the scrollbar width from the total column width
      // if a vertical scrollbar will be required
      Point vBarSize = table.getVerticalBar().getSize();
      width -= vBarSize.x;
    }
    Point oldSize = table.getSize();
    if (oldSize.x > area.width) {
      // table is getting smaller so make the columns 
      // smaller first and then resize the table to
      // match the client area width
      itemColumn.getColumn().setWidth(width-10);
      table.setSize(area.width, area.height);
    } else {
      // table is getting bigger so make the table 
      // bigger first and then make the columns wider
      // to match the client area width
      table.setSize(area.width, area.height);
      itemColumn.getColumn().setWidth(width-10);
    }
  }
    
  private Menu createPopupMenu(final TableViewer tableViewer)
  {
    final Menu pop = new Menu(tableViewer.getTable().getShell(), SWT.POP_UP);
    final MenuItem activateItem = new MenuItem(pop, SWT.PUSH);
    activateItem.setText("Copy Item to Clipboard");
    activateItem.setImage(PlatformIcons.get(PlatformIcons.ACTIVATE_TABLE_ENTRY));
    activateItem.addSelectionListener(new SelectionAdapter() {
        public void widgetSelected(SelectionEvent e) 
        {
          if (e.getSource() instanceof MenuItem) 
          {
            TableItem[] selection = tableViewer.getTable().getSelection();
            if ((selection != null) && (selection.length > 0))
            {
              IClipboardItem item = (IClipboardItem) selection[0].getData();
//              System.out.println("Selected: "+ti[0].getText()+", Index: "+tableViewer.getTable().getSelectionIndex());
              sourceDataManager.activateItem(item);          
            }
          }
        }
    });
    
    new MenuItem(pop, SWT.SEPARATOR);

        
    final MenuItem openWithItem = new MenuItem(pop, SWT.PUSH);
    openWithItem.setText("Open With Standard Application");
    openWithItem.setImage(PlatformIcons.get(PlatformIcons.OPEN_TABLE_ENTRY));
    openWithItem.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) 
      {
        if (e.getSource() instanceof MenuItem) 
        {
          TableItem[] selection = tableViewer.getTable().getSelection();
          if ((selection != null) && (selection.length > 0))
          {
            IClipboardItem item = (IClipboardItem) selection[0].getData();
            openSelectedEntry(item);
          }
        }
      }
    });

    final MenuItem downloadContentsItem = new MenuItem(pop, SWT.PUSH);
    downloadContentsItem.setText("Download Contents");
    downloadContentsItem.setImage(PlatformIcons.get(PlatformIcons.DOWNLOAD_CONTENTS_TABLE_ENTRY));
    downloadContentsItem.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) 
      {
        if (e.getSource() instanceof MenuItem) 
        {
          TableItem[] selection = tableViewer.getTable().getSelection();
          if ((selection != null) && (selection.length > 0))
          {
            IClipboardItem item = (IClipboardItem) selection[0].getData();
            if ((sourceDataManager.isRetrieveContentsNeeded(item)) && (!sourceDataManager.isDownloadInProgress(item.getSourceData())))
            {
              ModelProvider mp = ((SimidudeApplicationContext)ApplicationBase.getContext()).getModelProvider();
              mp.retrieveContentsForProxyObject(item.getSourceData());
            }
          }
        }
      }
    });
    
    final MenuItem saveEntryAsItem = new MenuItem(pop, SWT.PUSH);
    saveEntryAsItem.setText("Save As...");
    saveEntryAsItem.setImage(PlatformIcons.get(PlatformIcons.SAVE_CONTENTS_AS_TABLE_ENTRY));
    saveEntryAsItem.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) 
      {
        if (e.getSource() instanceof MenuItem) 
        {
          TableItem[] selection = tableViewer.getTable().getSelection();
          if ((selection != null) && (selection.length > 0))
          {
            final IClipboardItem item = (IClipboardItem) selection[0].getData();
            if (item != null)
            {
              sourceDataManager.saveClipboardItemAs(item, false);
            }
          }
        }
      }
    });

    final MenuItem saveEntryAsCompressedItem = new MenuItem(pop, SWT.PUSH);
    saveEntryAsCompressedItem.setText("Save Compressed...");
    saveEntryAsCompressedItem.setImage(PlatformIcons.get(PlatformIcons.SAVE_CONTENTS_AS_TABLE_ENTRY));
    saveEntryAsCompressedItem.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) 
      {
        if (e.getSource() instanceof MenuItem) 
        {
          TableItem[] selection = tableViewer.getTable().getSelection();
          if ((selection != null) && (selection.length > 0))
          {
            final IClipboardItem item = (IClipboardItem) selection[0].getData();
            if (item != null)
            {
              sourceDataManager.saveClipboardItemAs(item, true);
            }
          }
        }
      }
    });
    
    new MenuItem(pop, SWT.SEPARATOR);
    final MenuItem removeSelectedEntry = new MenuItem(pop, SWT.PUSH);
    removeSelectedEntry.setText("Remove Selected Item");
    removeSelectedEntry.setImage(PlatformIcons.get(PlatformIcons.REMOVE_SELECTED_CLIPBOARD_ENTRY));
    removeSelectedEntry.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) 
      {
        if (e.getSource() instanceof MenuItem) 
        {
          TableItem[] selection = tableViewer.getTable().getSelection();
          if ((selection != null) && (selection.length > 0))
          {
            IClipboardItem item = (IClipboardItem) selection[0].getData();
            sourceDataManager.removeItem(item);
          }
        }
      }
    });
    
    new MenuItem(pop, SWT.SEPARATOR);
    final MenuItem debugSelectedEntry = new MenuItem(pop, SWT.PUSH);
    debugSelectedEntry.setText("Output Debug Info for Entry");
    debugSelectedEntry.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) 
      {
        if (e.getSource() instanceof MenuItem) 
        {
          TableItem[] selection = tableViewer.getTable().getSelection();
          if ((selection != null) && (selection.length > 0))
          {
            IClipboardItem item = (IClipboardItem) selection[0].getData();
            SimpleInformationViewerDialog d = new SimpleInformationViewerDialog(tableParent.getShell(), "Clipboard Entry Debug Info", 
                "Output debug information for this clipboard entry that helps diagnosing potential problems.", new ClipboardItemDebugInfo(item).toString());
            d.open();
          }
        }
      }
    });
    
    pop.addListener(SWT.Show, new Listener() {
      public void handleEvent(Event event)
      {
        downloadContentsItem.setEnabled(false);
        activateItem.setEnabled(false);
        int idx = tableViewer.getTable().getSelectionIndex();
        if (idx > -1)
        {
          IClipboardItem item = sourceDataManager.getClipboardItem(idx);
          if ((sourceDataManager.isRetrieveContentsNeeded(item)) && (!sourceDataManager.isDownloadInProgress(item.getSourceData())))
          {       
            // Prüfen, ob für dieses Item ein Download in Progress ist. Nur falls nicht auf true setzen.
            downloadContentsItem.setEnabled(true);
          }
          activateItem.setEnabled(true);
        }
      }
    });
    
    return pop;
  }

  private void hookListeners(final TableViewer tableViewer)
  {
//    tableViewer.addDoubleClickListener(new IDoubleClickListener(){
//      public void doubleClick(DoubleClickEvent event)
//      {
//        TableItem[] tableItems = tableViewer.getTable().getSelection();
//        if ((tableItems != null) && (tableItems.length > 0))
//        {
//          IClipboardItem item = (IClipboardItem) tableItems[0].getData();
//          sourceDataManager.activateItem(item); 
//        }
//      }
//    });
    
    tableViewer.addDoubleClickListener(new IDoubleClickListener(){
      public void doubleClick(DoubleClickEvent event)
      {
        if (event.getSelection() instanceof StructuredSelection)
        {
          StructuredSelection sel = (StructuredSelection) event.getSelection();
          IClipboardItem item = (IClipboardItem) sel.getFirstElement();
          if (SimidudeUtils.isModifierKeyPressed())
          {
            sourceDataManager.activateItem(item);             
          } else {
            openSelectedEntry(item);
          }
        }
      }
    });
    
    tableViewer.getTable().addKeyListener(new KeyAdapter(){
      @Override
      public void keyPressed(KeyEvent e)
      {
        if ((e.keyCode == SWT.DEL) || (e.keyCode == KEY_BACKSPACE))
        {
          TableItem[] selection = tableViewer.getTable().getSelection();
          if ((selection != null) && (selection.length > 0))
          {
            int currentSelectionIndex = sourceDataManager.getSelectionIndex();
            IClipboardItem item = (IClipboardItem) selection[0].getData();
            boolean isNetworkRemove = SimidudeUtils.isModifierKeyPressed();
            sourceDataManager.removeItem(item);
            if ((isNetworkRemove) && (item != null))
            {
              ((SimidudeApplicationContext)ApplicationBase.getContext()).getModelProvider().networkRemoveItem(item.getSourceData().getStub());              
            }
            if (e.keyCode == KEY_BACKSPACE)
            {
              sourceDataManager.selectPreviousEntry(currentSelectionIndex);
            } else {
              sourceDataManager.saveSelectEntry(currentSelectionIndex);
            }
          }
        }
      }
    });
  }
  
  protected void openSelectedEntry(IClipboardItem item)
  {
    ISourceData sourceData = item.getSourceData();
    if (sourceData.getType() == SourceType.FILE)
    {
      final FileSourceData fsd = (FileSourceData) sourceData;
      if (!sourceDataManager.isDownloadInProgress(fsd))
      {
        if ((sourceDataManager.isRetrieveContentsNeeded(item)))
        {
          ModelProvider mp = ((SimidudeApplicationContext)ApplicationBase.getContext()).getModelProvider();
          mp.retrieveContentsForProxyObject(item.getSourceData(), new Runnable() {
            public void run()
            {
              PlatformUtils.safeAsyncRunnable(new Runnable(){
                public void run()
                {
                  SimidudeUtils.launchDefaultFileBowser(fsd);
                }
              });
            }
          });
        } else {
          SimidudeUtils.launchDefaultFileBowser(fsd);                  
        }
      }
    } else if (sourceData.getType() == SourceType.TEXT)
    {
      TextSourceData tsd = (TextSourceData) sourceData;
      if (tsd.getTextType() == TextType.URI)
      {
        SimidudeUtils.launchURI(tsd);
      } else {
        try
        {
          File f = FileUtils.writeTextToTempFile(tsd);
          SimidudeUtils.launchDefaultTextEditor(f);
        } catch (IOException e)
        {
          PlatformUtils.showErrorMessageWithException("Error Opening Clipboard Text", "The clipboard text could not be written to a temporary file: "+e.getMessage(), e);
        }                
      }
    } else if (sourceData.getType() == SourceType.IMAGE)
    {
      ImageSourceData isd = (ImageSourceData) sourceData;
      try
      {
        File f = FileUtils.writeImageToTempFile(isd);
        SimidudeUtils.launchDefaultImageEditor(f);
      } catch (IOException e)
      {
        PlatformUtils.showErrorMessageWithException("Error Opening Clipboard Image", "The clipboard image could not be written to a temporary file: "+e.getMessage(), e);
      }                
    }
  }

}

