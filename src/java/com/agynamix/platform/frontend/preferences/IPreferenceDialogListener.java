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

import org.eclipse.jface.util.PropertyChangeEvent;

public interface IPreferenceDialogListener {

  /**
   * Tell the world that some property has been changed.
   * @param event
   */
  void propertyChange(PropertyChangeEvent event);

  /**
   * The dialog has been closed.
   * @param result the close result, either Window.OK or Windows.CANCEL
   */
  void dialogClosed(int result);

}
