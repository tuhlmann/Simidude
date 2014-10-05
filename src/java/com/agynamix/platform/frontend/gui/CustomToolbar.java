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
package com.agynamix.platform.frontend.gui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.simidude.clipboard.ClipboardTableSearchBox;
import com.agynamix.simidude.clipboard.SourceDataManager;
import com.agynamix.simidude.impl.SimidudeApplicationContext;

public class CustomToolbar {

  List<IContributionItem> toolbarItems = new ArrayList<IContributionItem>();
  Composite               toolbar      = null;
  
  final Composite parent;
  
  public CustomToolbar(Composite parent)
  {
    this.parent = parent;
  }

  public void add(IAction action)
  {
    toolbarItems.add(new ActionContributionItem(action));
  }

  public void add(IContributionItem item)
  {
    toolbarItems.add(item);
  }
  
  public Composite createContents(Composite parent)
  {
    final Composite toolbar = new Composite(parent, SWT.NONE);
    GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
    toolbar.setLayoutData(data);
//    toolbar.addListener(SWT.Resize, new Listener() {
//      public void handleEvent(Event event)
//      {
//        GradientHelper.applyGradientBG(toolbar);
//      }
//    });

    GridLayout tbLayout = new GridLayout();
    tbLayout.numColumns = 2;
    tbLayout.marginTop = 0;
    tbLayout.marginBottom = 0;
    tbLayout.marginLeft = 0;
    tbLayout.marginRight = 0;
    toolbar.setLayout(tbLayout);

    ToolBarManager tbm = new ToolBarManager(SWT.FLAT);
    for (IContributionItem item : toolbarItems)
    {
      tbm.add(item);
    }
    tbm.createControl(toolbar);    
    
    addSearchBar(toolbar);
    
    return toolbar;
  }

  private void addSearchBar(Composite parent)
  {
    SourceDataManager sdm = ((SimidudeApplicationContext)ApplicationBase.getContext()).getSourceDataManager();
    
    final Control search = new ClipboardTableSearchBox(sdm).createControl(parent);
    GridData data = new GridData();
    data.horizontalAlignment = SWT.RIGHT;
    data.grabExcessHorizontalSpace = true;
    data.widthHint = 190;
    search.setLayoutData(data);
  }

  public Composite getToolbar()
  {
    if (toolbar == null)
    {
      toolbar = createContents(parent);
    }
    return toolbar;
  }

}
