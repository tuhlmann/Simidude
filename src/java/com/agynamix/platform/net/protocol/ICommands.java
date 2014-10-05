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

public interface ICommands {

  String PROTOCOL_VERSION = "V1";

  int COMMAND_SIZE         = 4;
  int PAYLOAD_COUNTER_SIZE = 4; // Do not change!!!
  int PACKET_HEADER_SIZE   = COMMAND_SIZE + PAYLOAD_COUNTER_SIZE;

  /**
   * Versuche eine Authentifizierung am Server
   */
  String AUTH = "AUTH";
  
  /**
   * Akzeptiere eine Verbindungsaufnahme
   */
  String ACPT = "ACPT";
  
  /**
   * Lehne eine Verbindungsaufnahme ab.
   */
  String REJT = "REJT";

  /**
   * Send an Exception that occured on one end to the connected peer.
   */
  String EXPT = "EXPT";
  
  /**
   * OK
   */
  String ACKN = "ACKN";
  
  /**
   * Beende Verbindung
   */
  String QUIT = "QUIT";

  /**
   * Sende ein Objekt innerhalb eines benutzerspezifischen Paketes
   */
  String OBJT = "OBJT";

  /**
   * Sende Daten innerhalb eines benutzerspezifischen Paketes
   */
  String DATA = "DATA";
  
  /**
   * Invoke remote command
   */
  String RCMD = "RCMD";
  
  String REQ_SEND_CLIENT_LIST = "sendClientList";

  String EMPTY_FIELD = "";

  /**
   * Wie sind Felder eines Kommandozeile getrennt.
   */
  String FIELD_SEP = " ";

  String CMD_CHARSET = "ISO-8859-1";

  public static final int BC_BUFFER_SIZE = 512;









}
