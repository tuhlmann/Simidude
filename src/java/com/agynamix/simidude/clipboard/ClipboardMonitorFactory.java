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
package com.agynamix.simidude.clipboard;

import com.agynamix.platform.infra.PlatformUtils;

public class ClipboardMonitorFactory {

  /**
   * Factory method to create a platform specific clipboard monitor instance.
   * @return
   */
  public static IClipboardMonitor newClipboardMonitor()
  {
    IClipboardMonitor clipboardMonitor = null;
    switch (PlatformUtils.getOsName())
    {
      case win32:
      case win64:
        clipboardMonitor = new PollingClipboardMonitor();
//        clipboardMonitor = new WindowsClipboardMonitor();
        break;
      case linux_x86:
      case linux_x86_64:
        clipboardMonitor = new PollingClipboardMonitor();
        break;
      case macosx:
      case macosx64:
        clipboardMonitor = new PollingClipboardMonitor();
        break;
      case solaris_x86:
    	  clipboardMonitor = new PollingClipboardMonitor();
    	  break;
      default:
        // FIXME: This should open a Bugzscout dialog so that we know about unexpected platforms.
        throw new IllegalStateException("Unknown operating system or architecture: os.name="+System.getProperty("os.name")+", os.arch="+System.getProperty("os.arch"));
    }
    return clipboardMonitor;
  }

}
