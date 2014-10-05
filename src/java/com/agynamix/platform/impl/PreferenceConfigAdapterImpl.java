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
package com.agynamix.platform.impl;

import java.util.Properties;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.platform.infra.IConfiguration;
import com.agynamix.platform.infra.IPreferenceConfigAdapter;

/**
 * Adapter that takes changed properties and writes them back to the application property store
 * 
 * @version $Revision: 23 $ $Date: 2004-11-20 14:36:31 +0100 (Sa, 20 Nov 2004) $
 * @author tuhlmann
 * @since V0404
 */
public class PreferenceConfigAdapterImpl implements IPropertyChangeListener, IPreferenceConfigAdapter {

  final IConfiguration config;

  static final String  SPECIAL_CHAR = ":\\";

  /**
   * List of registered listeners (element type: <code>IPropertyChangeListener</code>). These listeners are to be
   * informed when the current value of a preference changes.
   */
  private ListenerList listeners    = new ListenerList();

  public PreferenceConfigAdapterImpl()
  {
    config = ApplicationBase.getContext().getConfiguration();
  }

  /**
   * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
   */
  public void propertyChange(PropertyChangeEvent event)
  {
    String property = event.getProperty();
    config.setProperty(property, event.getNewValue());
    propagatePropertyChangeEvent(event);
  }

  public Properties loadDefaultProperties()
  {
    return config.exportDefaultProperties();
  }
  
  public Properties loadProperties()
  {
    return config.exportProperties();
  }

  public void saveProperties(Properties p)
  {
    // Properties are already written into XML through the Listener, now persist them
    config.save();
  }

  protected String escapeSpecials(String in)
  {
    StringBuffer out = new StringBuffer();
    for (int i = 0, j = in.length(); i < j; i++)
    {
      if (SPECIAL_CHAR.indexOf(in.charAt(i)) != -1)
      {
        out.append("\\");
      }
      out.append(in.charAt(i));
    }
    return out.toString();
  }

  /**
   * Forward this event to other registered listeners
   * 
   * @param event
   *          PropertyChangeEvent
   */
  void propagatePropertyChangeEvent(PropertyChangeEvent event)
  {
    final Object[] finalListeners = this.listeners.getListeners();
    for (int i = 0; i < finalListeners.length; ++i)
    {
      IPropertyChangeListener l = (IPropertyChangeListener) finalListeners[i];
      l.propertyChange(event);
    }
  }

  /**
   * @see com.agynamix.simidude.infra.PreferenceConfigAdapter#addPropertyChangeListener(org.eclipse.jface.util.IPropertyChangeListener)
   */
  public void addPropertyChangeListener(IPropertyChangeListener listener)
  {
    listeners.add(listener);
  }

  public void removePropertyChangeListener(IPropertyChangeListener listener)
  {
    listeners.remove(listener);
  }

}
