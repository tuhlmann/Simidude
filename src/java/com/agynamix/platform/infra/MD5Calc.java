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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Calc {

  private final static String algorithm = "MD5";

  private MessageDigest       md5;
  private byte[]              digest;

  public MD5Calc()
  {
    try {
      md5 = MessageDigest.getInstance(algorithm);
    } catch (NoSuchAlgorithmException e)
    {
      e.printStackTrace();
    }
  }

  public String toHexString(byte b)
  {
    int value = (b & 0x7F) + (b < 0 ? 128 : 0);

    String ret = (value < 16 ? "0" : "");
    ret += Integer.toHexString(value).toUpperCase();

    return ret;
  }

  public String checksum(String data)
  {
    StringBuilder strbuf = new StringBuilder();

    md5.update(data.getBytes(), 0, data.length());
    digest = md5.digest();

    for (int i = 0; i < digest.length; i++)
    {
      strbuf.append(toHexString(digest[i]));
    }
    
    return strbuf.toString();
  }

}