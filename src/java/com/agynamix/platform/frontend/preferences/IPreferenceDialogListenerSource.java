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

public interface IPreferenceDialogListenerSource {
  
  void addPreferenceDialogListener(IPreferenceDialogListener dialogListener);

  void removePreferenceDialogListener(IPreferenceDialogListener dialogListener);

}
