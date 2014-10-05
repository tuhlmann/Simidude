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
package com.agynamix.platform.net.protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.agynamix.platform.net.FatalNetworkException;

public abstract class SerializedDataCommand extends NodeCommand {

  final Object object;

  public SerializedDataCommand(String command, Object object)
  {
    super(command);
    this.object = object;
  }

  public SerializedDataCommand(String command, byte[] buffer, int offset, int packetSize)
  {
    super(command);
    try
    {
      if (packetSize > 0)
      {
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(buffer, offset, packetSize));
        object = ois.readObject();
        ois.close();
      } else {
        object = null;
      }
    } catch (IOException e)
    {
      throw new FatalNetworkException(e);
    } catch (ClassNotFoundException e)
    {
      throw new FatalNetworkException(e);
    }
  }

  @Override
  public byte[] toByteArray()
  {
    try
    {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(bos);
      if (object != null)
      {
        oos.writeObject(object);
      }
      oos.close();
      return bos.toByteArray();
    } catch (IOException e)
    {
      throw new FatalNetworkException(e);
    }
  }

  public Object getObject()
  {
    return this.object;
  }

}
