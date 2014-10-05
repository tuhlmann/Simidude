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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import com.agynamix.platform.icons.PlatformIcons;

public class ClipboardTableSearchBox {

  final SourceDataManager sourceDataManager;

  public ClipboardTableSearchBox(SourceDataManager sourceDataManager)
  {
    this.sourceDataManager = sourceDataManager;
  }

  public Control createControl(Composite parent)
  {
    Composite composite = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout();
    layout.numColumns = 3;
    composite.setLayout(layout);

    final Text searchBox = new Text(composite, SWT.SEARCH | SWT.CANCEL);
    GridData data = new GridData();
//    data.widthHint = 250;
    searchBox.setLayoutData(data);
        
    if ((searchBox.getStyle() & SWT.CANCEL) == 0)
    {
      Button searchBtn = new Button(composite, SWT.PUSH);
      searchBtn.setImage(PlatformIcons.get(PlatformIcons.SEARCH));
      searchBtn.addSelectionListener(new SelectionAdapter() {
        public void widgetSelected(SelectionEvent e)
        {
          String text = searchBox.getText();
          if ((text != null) && (text.length() > 0))
          {
            sourceDataManager.filterClipboardItems(text);
          }
        }
      });
      
      Button cancelBtn = new Button(composite, SWT.PUSH);
      cancelBtn.setImage(PlatformIcons.get(PlatformIcons.TRASH));
      cancelBtn.addSelectionListener(new SelectionAdapter() {
        public void widgetSelected(SelectionEvent e)
        {
          searchBox.setText("");
          sourceDataManager.filterClipboardItems(null);
        }
      });
    }
    searchBox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    searchBox.addSelectionListener(new SelectionAdapter() {
      public void widgetDefaultSelected(SelectionEvent e)
      {
        if (e.detail == SWT.CANCEL)
        {
          sourceDataManager.filterClipboardItems(null);
        } else
        {
          sourceDataManager.filterClipboardItems(searchBox.getText());
        }
      }
    });
    searchBox.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e)
      {
        if (e.character == SWT.ESC)
        {
          searchBox.setText("");
          sourceDataManager.filterClipboardItems(null);
        }
      }
      
      @Override
      public void keyReleased(KeyEvent e)
      {
        if (e.character != SWT.ESC)
        {
          String s = searchBox.getText();
          sourceDataManager.filterClipboardItems(s);          
        }                
      }
    });

    return composite;
  }

}
