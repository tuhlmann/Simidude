package com.agynamix.platform.frontend.preferences;

/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.swt.widgets.Composite;

import com.agynamix.platform.frontend.dialogs.InputNetworkAddressDialog;

/**
 * A field editor to edit directory paths.
 */
public class NetworkAddressListFieldEditor extends ListEditor {

  /**
   * Creates a new path field editor
   */
  protected NetworkAddressListFieldEditor()
  {
  }

  /**
   * Creates a path field editor.
   * 
   * @param name
   *          the name of the preference this field editor works on
   * @param labelText
   *          the label text of the field editor
   * @param dirChooserLabelText
   *          the label text displayed for the directory chooser
   * @param parent
   *          the parent of the field editor's control
   */
  public NetworkAddressListFieldEditor(String name, String labelText, String dirChooserLabelText, Composite parent)
  {
    init(name, labelText);
    createControl(parent);
  }

  /*
   * (non-Javadoc) Method declared on ListEditor. Creates a single string from the given array by separating each string
   * with the appropriate OS-specific path separator.
   */
  protected String createList(String[] items)
  {
    StringBuilder networkAddresses = new StringBuilder("");//$NON-NLS-1$

    for (String s : items)
    {
      networkAddresses.append(s);
      networkAddresses.append(",");
    }
    return networkAddresses.toString();
  }

  /*
   * (non-Javadoc) Method declared on ListEditor. Creates a new path element by means of a directory dialog.
   */
  protected String getNewInputObject()
  {
    InputNetworkAddressDialog addrDialog = new InputNetworkAddressDialog(getShell());
    String address = null;
    if (addrDialog.open() == IDialogConstants.OK_ID)
    {
      address = addrDialog.getText();
      if (address != null)
      {
        address = address.trim();
        if (address.length() == 0)
        {
          return null;
        }
      }
    }
    return address;
  }

  /*
   * (non-Javadoc) Method declared on ListEditor.
   */
  protected String[] parseString(String stringList)
  {
    StringTokenizer st = new StringTokenizer(stringList, ",");//$NON-NLS-1$
    List<String> v = new ArrayList<String>();
    while (st.hasMoreTokens())
    {
      v.add(st.nextToken());
    }
    return (String[]) v.toArray(new String[v.size()]);
  }
}
