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

public class FatalNetworkException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public FatalNetworkException()
  {
    super();
  }

  public FatalNetworkException(String message)
  {
    super(message);
  }

  public FatalNetworkException(Throwable cause)
  {
    super(cause);
  }

  public FatalNetworkException(String message, Throwable cause)
  {
    super(message, cause);
  }

}
