/*
 * Copyright by AGYNAMIX(R). All rights reserved. 
 * This file is made available under the terms of the
 * license this product is released under.
 * 
 * For details please see the license file you should have
 * received, or go to:
 * 
 * http://www.agynamix.de
 */

package com.agynamix.simidude.source;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import com.agynamix.simidude.infra.IContentsCacheInfo;

/**
 * Abstract data transport container
 * @author tuhlmann
 *
 */
public interface ISourceData extends Serializable {

  /**
   * The SourceType distinguishes the different types of data that enter the system.
   * It does not matter if data comes though the clipboard or through a drop event. 
   */
  public enum SourceType { TEXT, FILE, IMAGE };
  public enum TransportType { local, remote };

  SourceType getType();
  
  String getText();
  
  Object getData();
  
  /**
   * The unique ID of the instance this data originated at. This Id does not change through the
   * lifecycle of the instance.
   * @return the UUID of the sender.
   */
  UUID getSenderId();
  
  /**
   * 
   * @return The unique ID of this specific packet.
   */
  UUID getSourceId();

  void setTransportType(TransportType transportType);
  
  TransportType getTransportType();
  
  /**
   * Large objects are not send over the wire, they are only sent after a request by the specific party.
   * If there are 5 clients participating in the network and only one needs the file then it would produce
   * to much overhead to send the file right away.
   * Instead we send a proxy object to all members. If one member requests the content we download it specifically.
   * @return true if this is a proxy object, false otherwise.
   */
  boolean isProxy();

  /**
   * Indicates that the contents for this object is either local or has been written to the ContentsCache already.
   * @return true if the contents for this object is either local or has been cached, false otherwise.
   */
  boolean isCached();

  /**
   * Stores a ContentsCacheInfo object inside ISourceData which holds information about the cached contents.
   * @param cacheInfo
   */
  void setContentsCacheInfo(IContentsCacheInfo cacheInfo);

  public IContentsCacheInfo getContentsCacheInfo();

  /**
   * Clone das aktuelle ISourceData Objekt mit allen Eigenschaften.
   * @return ein Clone des aktuellen Objektes.
   */
  ISourceData copy();

  /**
   * Clone das aktuelle ISourceData Objekt mit nur den Eigenschaften, die für einen equals-Vergleich benötigt werden.
   * @return ein Clone des aktuellen Objektes mit nur den Equals-Eigenschaften.
   */
  ISourceData equalsCopy();

  /**
   * 
   * @return a SourceDataStub instance that identifies this entry.
   */
  SourceDataStub getStub();

  Date getCreationDate();
  
  /**
   * Strip the item off its contents.
   * All items are remembered, even when they have been deleted from the list. To safe memory (images
   * can take a lot of space) we can strip the item from its contents.
   * @return a reference to this item.
   */
  public void deleteContents();
  


}
