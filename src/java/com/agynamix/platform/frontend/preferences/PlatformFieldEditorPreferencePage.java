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
package com.agynamix.platform.frontend.preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

public abstract class PlatformFieldEditorPreferencePage extends FieldEditorPreferencePage implements IPreferenceDialogListenerSource  {

  List<IPreferenceDialogListener> preferenceDialogListeners = new ArrayList<IPreferenceDialogListener>();

  /**
   * Map<FieldEditor, List<IPropertyChangeListener>>
   */
  private Map<FieldEditor, List<IPropertyChangeListener>> listeners = new HashMap<FieldEditor, List<IPropertyChangeListener>>();

  public PlatformFieldEditorPreferencePage(String title, int style)
  {
    super(title, style);
  }

  public void addPropertyChangeListener(FieldEditor editor, IPropertyChangeListener listener)
  {
    List<IPropertyChangeListener> l = listeners.get(editor);
    if (l == null)
    {
      l = new ArrayList<IPropertyChangeListener>();
    }
    if (!l.contains(listener))
    {
      l.add(listener);
    }
    listeners.put(editor, l);
  }
  
  public void addPreferenceDialogListener(IPreferenceDialogListener dialogListener)
  {
    preferenceDialogListeners.add(dialogListener);
  }
  
  public void removePreferenceDialogListener(IPreferenceDialogListener dialogListener)
  {
    preferenceDialogListeners.remove(dialogListener);
  }  

  public void propertyChange(PropertyChangeEvent event)
  {
    super.propertyChange(event);
    if (!event.getNewValue().equals(event.getOldValue()))
    {
      FieldEditor fe = (FieldEditor) event.getSource();
      List<IPropertyChangeListener> l = listeners.get(fe);
      if (l != null)
      {
        for (IPropertyChangeListener listener : l)
        {
          listener.propertyChange(event);
        }
      }
      // call IPreferenceDialogListeners
      for (IPreferenceDialogListener listener : preferenceDialogListeners)
      {
        listener.propertyChange(event);
      }
    }
  }
  
  public boolean isEmpty(String string)
  {
    return ((string == null) || (string.length() == 0));
  }
  

}
