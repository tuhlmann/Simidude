package com.agynamix.platform.infra;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ExecutorUtils {
  
  private static ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(10);
  private static ExecutorService          fixedThreadPool     = Executors.newFixedThreadPool(20);
  private static ExecutorService          cachedThreadPool    = Executors.newCachedThreadPool();

  public static void addScheduledService(Runnable command, long initialDelay, long delay, TimeUnit timeUnit)
  {
    scheduledThreadPool.scheduleWithFixedDelay(command, initialDelay, delay, timeUnit);
  }

  public static void addFixedService(Runnable command)
  {
    fixedThreadPool.execute(command);
  }
  
  public static void addParallelTask(Runnable command)
  {
    cachedThreadPool.execute(command);
  }

  public static void shutdownScheduledService()
  {
    scheduledThreadPool.shutdownNow();
  }

  public static void shutdownFixedService()
  {
    fixedThreadPool.shutdownNow();
  }
  
  public static void shutdownParallelTasks()
  {
    cachedThreadPool.shutdownNow();
  }

}
