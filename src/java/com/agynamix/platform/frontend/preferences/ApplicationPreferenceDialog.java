/*
 * Copyright (c) 2004 agynamiX.com. All rights reserved. 
 * This file is made available under the terms of the
 * license this product is released under.
 * 
 * For details please see the license file you should have
 * received, or go to:
 * 
 * http://www.agynamix.com
 * 
 * Contributors: agynamiX.com (http://www.agynamix.com)
 */
package com.agynamix.platform.frontend.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Shell;

import com.agynamix.platform.impl.PreferenceConfigAdapterImpl;

/**
 * SimiDude application preference setup
 * 
 * @version $Revision: 23 $ $Date: 2004-11-20 14:36:31 +0100 (Sa, 20 Nov 2004) $
 * @author tuhlmann
 */
public class ApplicationPreferenceDialog implements IPreferenceDialogListener {

  Shell shell;
  
  List<IPreferenceDialogListener> preferenceDialogListeners = new ArrayList<IPreferenceDialogListener>();

  public ApplicationPreferenceDialog(Shell shell)
  {
    this.shell = shell;
    initialize();
  }

  protected void initialize()
  {
  }

  public int open()
  {
    PreferenceConfigAdapterImpl configAdapter = new PreferenceConfigAdapterImpl();
    ApplicationPreferenceStore store = new ApplicationPreferenceStore(configAdapter); // AppConfigUtil.getPreferencesFile());

    store.addPropertyChangeListener(configAdapter);

    PreferenceManager manager = new PreferenceManager();

    GlobalPreferencePageDefaults defaultPage = new GlobalPreferencePageDefaults(configAdapter);
    defaultPage.addPreferenceDialogListener(this);
    PreferenceNode defaultsNode = new PreferenceNode("defaultsPage");
    GlobalPreferencePageNetwork networkPage = new GlobalPreferencePageNetwork(configAdapter);
    networkPage.addPreferenceDialogListener(this);
    PreferenceNode networkNode = new PreferenceNode("networkPage");
    defaultsNode.setPage(defaultPage);
    manager.addToRoot(defaultsNode);
    networkNode.setPage(networkPage);
    manager.addToRoot(networkNode);

    PreferenceDialog dialog = new PreferenceDialog(shell, manager);
    dialog.setPreferenceStore(store);
    int result = dialog.open();
    for (IPreferenceDialogListener l : preferenceDialogListeners)
    {
      l.dialogClosed(result);
    }
    return result;
  }

  public void addPreferenceDialogListener(IPreferenceDialogListener preferenceDialogListener)
  {
    preferenceDialogListeners.add(preferenceDialogListener);
  }

  public void removePreferenceDialogListener(IPreferenceDialogListener preferenceDialogListener)
  {
    preferenceDialogListeners.remove(preferenceDialogListener);
  }
  
  public void propertyChange(PropertyChangeEvent event)
  {
    // propagate the change event
    for (IPreferenceDialogListener l : preferenceDialogListeners)
    {
      l.propertyChange(event);
    }
  }
  
  public void dialogClosed(int result)
  {
  }
  
}
