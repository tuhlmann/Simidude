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
package com.agynamix.platform.net;

import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.simidude.impl.SimidudeApplicationContext;
import com.agynamix.simidude.remote.RemoteConnector;

public class RequestClientIdCommand extends AbstractRemoteCommand {

  private static final long serialVersionUID = 1L;

  public RequestClientIdCommand()
  {
  }

  public Object invoke(IConnectorServerHandler connectorServerHandler)
  {
    RemoteConnector remoteConnector = ((SimidudeApplicationContext)ApplicationBase.getContext()).getRemoteConnector();
    return remoteConnector.getConnector().getNodeId();
  }

}
