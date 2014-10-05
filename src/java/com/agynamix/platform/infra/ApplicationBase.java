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

import java.util.EventObject;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import com.agynamix.platform.concurrent.ThreadManager;
import com.agynamix.platform.frontend.gui.ApplicationGUI;
import com.agynamix.platform.log.ApplicationLog;

public abstract class ApplicationBase {

  static ApplicationBase itsApplicationInstance;

  private ApplicationContext applicationContext;
  String[] args;

  List<IExitListener> exitListeners = new CopyOnWriteArrayList<IExitListener>();

  static Logger log = ApplicationLog.getLogger(ApplicationBase.class);

  static boolean isNormalShutdown = false;

  public ApplicationBase()
  {
  }

  public static ApplicationContext getContext()
  {
    if (itsApplicationInstance == null)
    {
      throw new NullPointerException("No Application instance registered");
    }
    return itsApplicationInstance.applicationContext;
  }

  private void setContext(ApplicationContext contextInstance)
  {
    this.applicationContext = contextInstance;
  }

  public static ApplicationBase getInstance()
  {
    if (itsApplicationInstance == null)
    {
      throw new NullPointerException("No Application instance registered");
    }
    return itsApplicationInstance;
  }

  protected void setArgs(String[] args)
  {
    this.args = args;
  }

  public static void launch(Class<? extends ApplicationBase> applicationClass, ApplicationContext contextInstance,
      String[] args)
  {
    try
    {
      Object o = applicationClass.newInstance();
      itsApplicationInstance = (ApplicationBase) o;
      itsApplicationInstance.setContext(contextInstance);
      itsApplicationInstance.setArgs(args);
      Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
      Runtime.getRuntime().addShutdownHook(new ShutdownHandler());

      itsApplicationInstance.setup();
      itsApplicationInstance.initialize();
      itsApplicationInstance.run();

    } catch (InstantiationException e)
    {
      throw new PlatformException(e);
    } catch (IllegalAccessException e)
    {
      throw new PlatformException(e);
    }
  }

  protected abstract void applicationSetup();

  /**
   * First step in application initialization. During this step all required components should be created. All
   * initialization that possibly involves other components should be deferred until the initialization phase.
   */
  private final void setup()
  {
    // Out setup stuff
    ThreadManager tm = new ThreadManager();
    getContext().registerService(ThreadManager.SERVICE_NAME, tm);

    ApplicationGUI gui = createApplicationGUI();
    getContext().registerService(ApplicationGUI.SERVICE_NAME, gui);
    applicationSetup();
  }

  /**
   * Supposed to be overwritten by the implementing class.
   */
  protected void applicationInitialize()
  {

  }

  private final void initialize()
  {
    ApplicationGUI gui = getContext().getApplicationGUI();
    gui.initializeApplicationGUI();
    applicationInitialize();
  }

  /**
   * Starts base mechanisms
   */
  private void internalRun()
  {
    getContext().getThreadManager().startRegisteredServices();
  }

  protected abstract void startup();

  protected abstract void prepareRun();

  protected abstract ApplicationGUI createApplicationGUI();

  private void run()
  {
    internalRun();

    startup();

    getContext().getApplicationGUI().startup();

    prepareRun();

    getContext().getApplicationGUI().run();
  }

  /**
   * Called by the implementing application when the app is about to exit. Fires ExitListeners to all interested
   * parties. Eventually calls shutdown() to shut down the application.
   */
  public void exit()
  {
    if (!fireCanExit())
    {
      return;
    }
    fireWillExit();
    shutdown();
    System.exit(IPlatformConstants.RC_OK);
  }

  protected void shutdown()
  {
    getContext().getConfiguration().save();
    getContext().getThreadManager().shutdownNow();
    try
    {
      Thread.sleep(1000);
    } catch (InterruptedException ignore)
    {
    }
    getContext().shutdown();
    isNormalShutdown = true;
  }

  public void addExitListener(IExitListener listener)
  {
    this.exitListeners.add(listener);
  }

  public void removeExitListener(IExitListener listener)
  {
    this.exitListeners.remove(listener);
  }

  /**
   * Tell the Exit listeners that we want to exit and ask if they allow.
   * 
   * @return true if all ExitListeners allow exit, false otherwise.
   */
  private boolean fireCanExit()
  {
    for (int i = exitListeners.size() - 1; i >= 0; i--)
    {
      IExitListener l = exitListeners.get(i);
      if (!(l.canExit(new EventObject(this))))
      {
        return false;
      }
    }
    return true;
  }

  /**
   * Tell all ExitListeners that we will exit.
   */
  private void fireWillExit()
  {
    for (int i = exitListeners.size() - 1; i >= 0; i--)
    {
      IExitListener l = exitListeners.get(i);
      l.willExit(new EventObject(this));
    }
  }

  /**
   * The shutdown hook is called by the JVM when the system is going down. It will be used to cleanup and closed used
   * ressources.<br/>
   * However, the hook <b>MUST</b> be registered with the JVM through <code>Runtime.addShutdownHook()</code> in order to
   * be called!
   */
  static class ShutdownHandler extends Thread {

    public ShutdownHandler()
    {
      super();
    }

    public void run()
    {
      try
      {
        Thread.sleep(500);
        System.out.println("Shutting down Simidude...");
//        try
//        {
//          if (!isNormalShutdown)
//          {
//            System.out.println("Emergency Shutdown...");
//            if (ApplicationBase.getContext().getConfiguration().getBoolean(IPreferenceConstants.RESTORE_LATEST_ENTRY))
//            {
//              SourceDataManager sdm = ((SimidudeApplicationContext) ApplicationBase.getContext())
//                  .getSourceDataManager();
//              IClipboardItem item = sdm.getNewestClipboardItem();
//              FileUtils.serialize(item.getSourceData(), PlatformUtils.getApplicationDataDir() + "/"
//                  + IPreferenceConstants.SAVED_CLP_ITEM_FILE_NAME);
//            }
//            getContext().getConfiguration().save();
//            getContext().getThreadManager().shutdownNow();
//          }
//        } catch (Exception ee)
//        {
//          ee.printStackTrace();
//        }
      } catch (InterruptedException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  static class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    public void uncaughtException(Thread t, Throwable e)
    {
      e.printStackTrace();
      String err = String.format("Fatal error occured: {0}, {1}, {2}, {3}, {4}",
          t.getName(), e.toString(), e.getStackTrace()[0].getLineNumber(),
          e.getStackTrace()[0].getFileName());
      System.out.println(err);
      System.exit(1);
    }

  }

}
