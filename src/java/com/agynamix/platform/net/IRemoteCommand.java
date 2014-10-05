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

import java.io.Serializable;

public interface IRemoteCommand extends Serializable {
  
  /**
   * The invoke method is called on the remote client.
   * @return the result of the invoke method is returned to the caller.
   */
  Object invoke(IConnectorServerHandler connectorServerHandler);

}
