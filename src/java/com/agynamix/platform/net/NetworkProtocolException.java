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

public class NetworkProtocolException extends Exception {

  private static final long serialVersionUID = 1L;

  public NetworkProtocolException()
  {
    super();
  }

  public NetworkProtocolException(String message)
  {
    super(message);
  }

  public NetworkProtocolException(Throwable cause)
  {
    super(cause);
  }

  public NetworkProtocolException(String message, Throwable cause)
  {
    super(message, cause);
  }

}
