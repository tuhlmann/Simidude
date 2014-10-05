/*
 * Copyright by AGYNAMIX(R). All rights reserved. 
 * This file is made available under the terms of the
 * license this product is released under.
 * 
 * For details please see the license file you should have
 * received, or go to:
 * 
 * http://www.agynamix.de
 */

package com.agynamix.simidude.clipboard;

import com.agynamix.simidude.source.ISource;
import com.agynamix.simidude.source.ISourceData;
import com.agynamix.simidude.source.ISourceData.SourceType;

/**
 * Interfaces whose implementers monitor the system clipboard and inform about changes.
 * @author tuhlmann
 *
 */
public interface IClipboardMonitor extends ISource {

  /**
   * Called when the system itself put something on the clipboard. We don't want these items
   * to reoccur.
   */
  void itemActivated(ISourceData sourceData);

  /**
   * Enable or disable the monitoring of the clipboard
   * @param enable true- monitoring will be enabled, false- monitoring will be disabled.
   */
  void setClipboardMonitorEnabled(boolean enable);

  /**
   * Enable or disable the monitoring of specific item types in the clipboard
   * @param enable true- monitoring of the selected type will be enabled, false- monitoring will be disabled.
   */
  void setClipboardMonitorTypeEnabled(SourceType sourceType, boolean enable);

  /**
   * 
   * @return true if the monitoring of the clipboard is enabled, false otherwise.
   */
  boolean isClipboardMonitorEnabled();

  /**
   * Checks to see if the clipboard currently is empty.
   * @return true if the clipboard is empty, false otherwise.
   */
  boolean isClipboardEmpty();

  /**
   * Remove the current contents from the clipboard.
   */
  void emptyClipboard();


}
