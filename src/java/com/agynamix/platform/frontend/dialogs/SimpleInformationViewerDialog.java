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
package com.agynamix.platform.frontend.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.agynamix.platform.infra.PlatformUtils;

public class SimpleInformationViewerDialog extends TitleAreaDialog {
  
  final String title;
  final String headerText;
  Text text;
  final String finalContents;
  
  Font textFont = null;

  public SimpleInformationViewerDialog(Shell shell, String title, String headerText, String info)
  {
    super(shell);
    setShellStyle(getShellStyle() | SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
    
    this.finalContents = info;
    this.title         = title;
    this.headerText    = headerText;
  }
  
  @Override
  protected Control createContents(Composite parent)
  {
    Control contents = super.createContents(parent);
    setTitle(title);
    setMessage(headerText, IMessageProvider.INFORMATION);
    text.setText(finalContents);
    
    return contents;
  }
  
  @Override
  protected Control createDialogArea(Composite parent)
  {
    Composite parentComposite = (Composite) super.createDialogArea(parent);
    parentComposite.setLayout(new GridLayout());
        
    Composite composite = new Composite(parentComposite, parentComposite.getStyle());
    GridData compData = new GridData(SWT.FILL, SWT.FILL, true, true);
    composite.setLayoutData(compData);

    composite.setLayout(new GridLayout(1, false));
    
    text = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.READ_ONLY | SWT.V_SCROLL);
    GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
    data.minimumHeight = 80;
    text.setLayoutData(data);  
    FontData defaultFont = new FontData("Courier", 12, SWT.NONE);
    textFont = new Font(Display.getDefault(), defaultFont);
    text.setFont(textFont);
        
    return parentComposite;
  }
  
  
  @Override
  protected void createButtonsForButtonBar(Composite parent)
  {
    createButton(parent, IDialogConstants.OK_ID, IDialogConstants.CLOSE_LABEL, true);
//    super.createButtonsForButtonBar(parent);
  }
  
  @Override
  protected void buttonPressed(int buttonId)
  {
    if (buttonId == IDialogConstants.OK_ID)
    {
//      finalContents = text.getText();
    }
    super.buttonPressed(buttonId);
  }
  
  @Override
  public boolean close()
  {
    if ((textFont != null) && (!textFont.isDisposed()))
    {
      textFont.dispose(); 
      textFont = null;
    }
    return super.close();
  }

  public String getText()
  {
    return finalContents;
  }


}
