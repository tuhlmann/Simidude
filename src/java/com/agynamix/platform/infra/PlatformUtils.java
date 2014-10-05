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
package com.agynamix.platform.infra;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.SubStatusLineManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

import com.agynamix.platform.frontend.dialogs.ExceptionDetailsDialog;
import com.agynamix.platform.frontend.preferences.IPreferenceConstants;

public class PlatformUtils {
  
  public enum OS {
    unknown("Unknown", "Unknown"),
    win32("Windows", "x86"),
    win64(new String[] { "Windows" }, new String[] { "x86_64", "amd64" }),
    macosx(new String[] { "Mac OS X" }, new String[] { "i386", "ppc" }),
    macosx64("Mac OS X", "x86_64"),
    linux_x86("Linux", "i386"),
    linux_x86_64(new String[] { "Linux" }, new String[] { "x86_64", "amd64" }),
    solaris_x86( "SunOS" , "x86");

    final List<String> osname = new ArrayList<String>();
    final List<String> osarch = new ArrayList<String>();

    OS(String osname, String osarch)
    {
      this.osname.add(osname.toLowerCase());
      this.osarch.add(osarch.toLowerCase());
    }

    OS(String[] osname, String[] osarch)
    {
      for (String s : osname)
      {
        this.osname.add(s.toLowerCase());
      }
      for (String s : osarch)
      {
        this.osarch.add(s.toLowerCase());
      }
    }

    private boolean isCompatible(String osname, String osarch)
    {
      if (isOsNameCompatible(osname))
      {
        if (isOsArchCompatible(osarch))
        {
          return true;
        }
      }
      return false;
    }

    private boolean isOsNameCompatible(String osname)
    {
      for (String s : this.osname)
      {
        if (osname.toLowerCase().indexOf(s) > -1)
        {
          return true;
        }
      }
      return false;
    }

    private boolean isOsArchCompatible(String osarch)
    {
      for (String s : this.osarch)
      {
        if (osarch.toLowerCase().indexOf(s) > -1)
        {
          return true;
        }
      }
      return false;
    }

    public static OS parseOs(String osname, String osarch)
    {
      for (OS os : OS.values())
      {
        if (os.isCompatible(osname, osarch))
        {
          return os;
        }
      }
      return OS.unknown;
    }

    @Override
    public String toString()
    {
      return osname + "/" + osarch;
    }
  };

  public static OS getOsName()
  {
    String osname = System.getProperty("os.name");
    String osarch = System.getProperty("os.arch");
    OS os = OS.parseOs(osname, osarch);
    // System.out.println("OS="+os);
    return os;
  }

  public static boolean isMacOs()
  {
    OS os = getOsName();
    if ((os == OS.macosx) || (os == OS.macosx64))
    {
      return true;
    } else
    {
      return false;
    }
  }

  static Object syncThreadResult = null;

  /**
   * 
   * @return The root of the application data directory. This is for instance .Simidude on Unix or somewhere in Library
   *         on the Mac.
   */
  public static synchronized String getApplicationDataDir()
  {
    String commonPath = System.getProperty("user.home") + File.separator;
    String extensionPath = ApplicationInfo.getApplicationName() + File.separator;
    String platformPath = ".";
    if (PlatformUtils.isMacOs())
    {
      platformPath = "Library" + File.separator + "Application Support" + File.separator;
    }

    String dataDir = commonPath + platformPath + extensionPath;
    File fDataDir = new File(dataDir);
    if (!fDataDir.exists())
    {
      if (!fDataDir.mkdirs())
      {
        throw new IllegalStateException("Can not create application data directory " + dataDir);
      }
    }
    return dataDir;
  }
  
  public static String getApplicationCacheDir()
  {
    String extensionPath = IPreferenceConstants.CACHE_DIR_NAME + File.separator;
    return PlatformUtils.getApplicationDataDir() + extensionPath;
  }

  public static String getApplicationBasedir()
  {
    return System.getProperty("user.dir");
  }
  
  public static void setStatusLineContribution(final String msg)
  {
    safeAsyncRunnable(new Runnable() {
      public void run()
      {
        IStatusLineManager statusLine = getStatusLine();
        if (statusLine != null)
        {
          statusLine.setMessage(msg);
        }
      }
    });
  }

  public static void setStatusLineErrorMsg(final String msg)
  {
    safeAsyncRunnable(new Runnable() {
      public void run()
      {
        IStatusLineManager statusLine = getStatusLine();
        if (statusLine != null)
        {
          statusLine.setErrorMessage(msg);
        }
      }
    });
  }

  protected static IStatusLineManager getStatusLine()
  {
    IStatusLineManager statusLine = getStatusLineManager();
    if (statusLine != null)
    {
      while (statusLine instanceof SubStatusLineManager)
      {
        IContributionManager cb = ((SubStatusLineManager) statusLine).getParent();
        if (!(cb instanceof IStatusLineManager))
        {
          break;
        }
        statusLine = (IStatusLineManager) cb;
      }
    }
    return statusLine;
  }

  protected static IStatusLineManager getStatusLineManager()
  {
    return ApplicationBase.getContext().getApplicationGUI().getStatusLineManager();
  }

  public static void setConnectionBroken()
  {
    setStatusLineErrorMsg("Connection broken.");
  }

  public static void setConnected(String client)
  {
    setStatusLineErrorMsg(null);
    setStatusLineContribution("Connected to " + client);
  }

  public static void setConnected(int clientCount)
  {
    setStatusLineErrorMsg(null);
    setStatusLineContribution("Connected to " + clientCount + " clients");
  }

  public static void setNotConnected()
  {
    setStatusLineErrorMsg(null);
    setStatusLineContribution("Not connected.");
  }

  // public static void showConnectionBrokenDialog(final
  // ConnectionBrokenException e)
  // {
  // safeAsyncRunnable(new Runnable() {
  // public void run()
  // {
  //        new ExceptionDetailsDialog(Display.getDefault().getActiveShell(), "Verbindung zeitweilig unterbrochen", null,  //$NON-NLS-1$
  //            e.getMessage(), e).open(); //$NON-NLS-1$            
  // }
  // });
  // }

  public static void showErrorMessageWithException(final String title, final String msg, final Throwable t)
  {
    safeAsyncRunnable(new Runnable() {
      public void run()
      {
        new ExceptionDetailsDialog(Display.getDefault().getActiveShell(), title, null, msg, new Status(IStatus.ERROR,
            ApplicationInfo.getApplicationName(), IStatus.OK, msg, t)).open();
      }
    });
  }

  public static void showWarningMessageWithException(final String title, final String msg, final Throwable t)
  {
    safeAsyncRunnable(new Runnable() {
      public void run()
      {
        new ExceptionDetailsDialog(Display.getDefault().getActiveShell(), title, null, msg, new Status(IStatus.WARNING,
            ApplicationInfo.getApplicationName(), IStatus.OK, msg, t)).open();
      }
    });
  }

  public static void showInfoMessage(final String title, final String msg)
  {
    safeAsyncRunnable(new Runnable() {
      public void run()
      {
        new ExceptionDetailsDialog(Display.getDefault().getActiveShell(), title, null, msg, new Status(IStatus.INFO,
            ApplicationInfo.getApplicationName(), IStatus.OK, msg, null)).open();
      }
    });
  }

  public static void showWarningMessage(final String title, final String msg)
  {
    safeAsyncRunnable(new Runnable() {
      public void run()
      {
        new ExceptionDetailsDialog(Display.getDefault().getActiveShell(), title, null, msg, new Status(IStatus.WARNING,
            ApplicationInfo.getApplicationName(), IStatus.OK, msg, null)).open();
      }
    });
  }
  
  public static void showWarningMessage(final String title, final String msg, final String strDetails)
  {
    safeAsyncRunnable(new Runnable() {
      public void run()
      {
        new ExceptionDetailsDialog(Display.getDefault().getActiveShell(), title, null, msg, new Status(IStatus.WARNING,
            ApplicationInfo.getApplicationName(), IStatus.OK, msg, null), strDetails).open();
      }
    });
  }
 
  public static void showErrorMessage(final String title, final String msg)
  {
    safeAsyncRunnable(new Runnable() {
      public void run()
      {
        MessageDialog.openError(Display.getDefault().getActiveShell(), title, msg);
        // new ExceptionDetailsDialog(Display.getDefault().getActiveShell(),
        // title, null, msg,
        // new Status(IStatus.ERROR, ApplicationInfo.getApplicationName(),
        // IStatus.OK, msg, null)).open();
      }
    });
  }

  public static void showToggleErrorMessage(final String title, final String msg, final String configKey)
  {
    if (!ApplicationBase.getContext().getConfiguration().getBoolean(configKey)) {
      safeAsyncRunnable(new Runnable() {
        public void run()
        {
          MessageDialogWithToggle d = MessageDialogWithToggle.openError(Display.getDefault().getActiveShell(), title, msg, 
              "Don't show this message again", false, null, null);
          ApplicationBase.getContext().getConfiguration().setBoolean(configKey, d.getToggleState());
        }
      });
    }
  }
  
  public static boolean showOverwriteFilesDialog(final File dest)
  {
    safeSyncRunnable(new Runnable() {
      public void run()
      {
        String whatUpper = dest.isDirectory() ? "Directory" : "File";
        String what = dest.isDirectory() ? "directory" : "file";
        MessageBox box = new MessageBox(Display.getDefault().getActiveShell(), SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
        box.setText("Overwrite Existing " + whatUpper + "?");
        box.setMessage("The " + what + " " + dest.getAbsolutePath()
            + " already exists. Click \"OK\" to overwrite all existing contents.");
        int rc = box.open();
        if (rc == SWT.OK)
        {
          syncThreadResult = Boolean.TRUE;
        } else
        {
          syncThreadResult = Boolean.FALSE;
        }
      }
    });
    Boolean result = (Boolean) syncThreadResult;
    return result.booleanValue();
  }

  public static void safeAsyncRunnable(final Runnable runnable)
  {
    Display display = Display.getCurrent();
    if (display == null)
    {
      display = Display.getDefault();
    }
    if (display != null)
    {
      if (!display.isDisposed())
      {
        try
        {
          display.asyncExec(runnable);
        } catch (final Exception e)
        {
          System.out.println("Error while executing: " + e.getMessage());
          e.printStackTrace();
        }
      }
    }
  }

  public static void safeSyncRunnable(final Runnable runnable)
  {
    Display display = Display.getCurrent();
    if (display == null)
    {
      display = Display.getDefault();
    }
    if (display != null)
    {
      if (!display.isDisposed())
      {
        try
        {
          display.syncExec(runnable);
        } catch (final Exception e)
        {
          System.out.println("Error while executing: " + e.getMessage());
          e.printStackTrace();
        }
      }
    }
  }

}
