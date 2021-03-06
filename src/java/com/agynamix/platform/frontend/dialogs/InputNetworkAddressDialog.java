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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.agynamix.platform.infra.PlatformUtils;

public class InputNetworkAddressDialog extends TitleAreaDialog {
  
  Text text;
  String finalContents;

  public InputNetworkAddressDialog(Shell shell)
  {
    super(shell);
  }
  
  @Override
  protected Control createContents(Composite parent)
  {
    Control contents = super.createContents(parent);
    setTitle("Input Network Address");
    String meta = PlatformUtils.isMacOs() ? "APPLE" : "CTRL";
    String text = "Please enter one IP address or network name.You can add a port by appending ':<portnr>'.\nPress 'Close' or "+meta+"+'Enter' to close the dialog.";
    setMessage(text, IMessageProvider.INFORMATION);
    
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
    
    Label l = new Label(composite, SWT.WRAP);
    l.setText("Enter Network Address:");    
    GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
    l.setLayoutData(data);

    text = new Text(composite, SWT.BORDER );
    data = new GridData(SWT.FILL, SWT.CENTER, true, false);
    text.setLayoutData(data);    
    
    text.addKeyListener(new KeyAdapter(){
      @Override
      public void keyPressed(KeyEvent e)
      {
        if (e.keyCode == 13)
        {
          if ((e.stateMask & SWT.MOD1) != 0)
          {
            buttonPressed(IDialogConstants.OK_ID);
          }
        }
      }
    });
    
    return parentComposite;
  }
  
  
  @Override
  protected void createButtonsForButtonBar(Composite parent)
  {
    createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
//    super.createButtonsForButtonBar(parent);
  }
  
  @Override
  protected void buttonPressed(int buttonId)
  {
    if (buttonId == IDialogConstants.OK_ID)
    {
      finalContents = text.getText();
    }
    super.buttonPressed(buttonId);
  }

  public String getText()
  {
    return finalContents;
  }


}
