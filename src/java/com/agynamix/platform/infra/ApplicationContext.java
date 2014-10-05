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

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Display;

import com.agynamix.platform.concurrent.ThreadManager;
import com.agynamix.platform.frontend.gui.ApplicationGUI;
import com.agynamix.platform.impl.ConfigurationImpl;

/**
 * This class holds all the context information for this application
 * 
 * @author tuhlmann
 * 
 */
public class ApplicationContext {

  ImageRegistry imageRegistry;
  FontRegistry fontRegistry;
  ColorRegistry colorRegistry;
  Clipboard clipboard;

  IConfiguration configuration;

  @SuppressWarnings("unchecked")
  protected Map locatorMap = new ConcurrentHashMap(10);

  public ApplicationContext()
  {
  }

  @SuppressWarnings("unchecked")
  public synchronized void registerService(String name, Object service)
  {
    if (!locatorMap.containsKey(name))
    {
      locatorMap.put(name, service);
    }
  }

  public synchronized void deregisterService(String name)
  {
    if (locatorMap.containsKey(name))
    {
      locatorMap.remove(name);
    }
  }

  public synchronized Object getService(String name)
  {
    if (locatorMap.containsKey(name))
    {
      return locatorMap.get(name);
    } else
    {
      throw new IllegalStateException("Der Dienst " + name + " wurde vom ServiceLocator nicht gefunden!");
    }
  }

  public ImageRegistry getImageRegistry()
  {
    if (imageRegistry == null)
    {
      imageRegistry = new ImageRegistry(Display.getDefault());
    }
    return imageRegistry;
  }

  public FontRegistry getFontRegistry()
  {
    if (fontRegistry == null)
    {
      fontRegistry = new FontRegistry(Display.getDefault());
    }
    return fontRegistry;
  }

  public ColorRegistry getColorRegistry()
  {
    if (colorRegistry == null)
    {
      colorRegistry = new ColorRegistry(Display.getDefault());
    }
    return colorRegistry;
  }

  public Clipboard getClipboard()
  {
    if (clipboard == null)
    {
      PlatformUtils.safeSyncRunnable(new Runnable() {
        public void run()
        {
          clipboard = new Clipboard(Display.getDefault());
        }
      });
    }
    return clipboard;
  }

  public IConfiguration getConfiguration()
  {
    if (configuration == null)
    {
      configuration = new ConfigurationImpl();
    }
    return configuration;
  }

  public void shutdown()
  {
    if (clipboard != null)
    {
      try
      {
        PlatformUtils.safeSyncRunnable(new Runnable() {
          public void run()
          {
            if ((clipboard != null) && (!clipboard.isDisposed()) && (!Display.getCurrent().isDisposed()))
            {
              clipboard.dispose();
              clipboard = null;
            }
          }
        });
      } catch (Exception e)
      {
        System.out.println("Exception while shutting down: " + e.getMessage());
      }
    }
  }

  public ThreadManager getThreadManager()
  {
    return (ThreadManager) getService(ThreadManager.SERVICE_NAME);
  }

  public ApplicationGUI getApplicationGUI()
  {
    return (ApplicationGUI) getService(ApplicationGUI.SERVICE_NAME);
  }

  /*
   * FIXME: See Ticket http://192.168.0.201/FogBugz/default.asp?4987
   */
  public String[] getHostAddresses()
  {
    List<String> ipAddresses = new ArrayList<String>();
    try
    {
      Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
      while (interfaces.hasMoreElements())
      {
        NetworkInterface ni = interfaces.nextElement();
        Enumeration<InetAddress> addresses = ni.getInetAddresses();
        while (addresses.hasMoreElements())
        {
          InetAddress addr = addresses.nextElement();
          if (!addr.isLoopbackAddress())
          {
            if (addr instanceof Inet4Address)
            {
              ipAddresses.add(addr.getHostAddress());
            }
          }
        }
      }
    } catch (SocketException e)
    {
      e.printStackTrace();
    }
    if (ipAddresses.size() == 0)
    {
      ipAddresses.add("localhost");
    }
    return ipAddresses.toArray(new String[ipAddresses.size()]);
  }

}
