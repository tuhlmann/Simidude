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
package com.agynamix.simidude.impl;

import com.agynamix.platform.infra.ApplicationContext;
import com.agynamix.platform.infra.IQueueManager;
import com.agynamix.simidude.clipboard.SourceDataManager;
import com.agynamix.simidude.frontend.ctrl.SourceDataDialogController;
import com.agynamix.simidude.infra.ModelProvider;
import com.agynamix.simidude.remote.RemoteConnector;

public class SimidudeApplicationContext extends ApplicationContext {
  
  public IQueueManager getQueueManager()
  {
    return (IQueueManager) getService(IQueueManager.SERVICE_NAME);
  }

  public SourceDataManager getSourceDataManager()
  {
    return (SourceDataManager) getService(SourceDataManager.SERVICE_NAME);
  }

  public ModelProvider getModelProvider()
  {
    return (ModelProvider) getService(ModelProvider.SERVICE_NAME);
  }

  public RemoteConnector getRemoteConnector()
  {
    return (RemoteConnector) getService(RemoteConnector.SERVICE_NAME);
  }

}
