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
package swt.snippets;

import java.net.InetAddress;

public class InetAddrFinder {

  /**
   * @param args
   */
  public static void main(String[] args) throws Exception
  {
    InetAddress addr = InetAddress.getLocalHost();
    System.out.println("Host="+addr.getHostAddress());
  }

}
