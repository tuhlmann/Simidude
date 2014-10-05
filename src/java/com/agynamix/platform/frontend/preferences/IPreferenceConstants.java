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
package com.agynamix.platform.frontend.preferences;


public interface IPreferenceConstants {
  
  String START_MINIMIZED                      = "app.start_minimized";    
  String SHOW_SPLASH                          = "app.show_splash";    
  String RESTORE_LATEST_ENTRY                 = "app.restore_latest_clp_entry";
  String SHOW_BALLOON_TOOLTIP                 = "app.show_balloon_tooltip";
  String SERVER_PORT                          = "app.server_port";    
  String UPDATE_SCHEDULE                      = "app.update_schedule";

  String TOGGLE_CLIPBOARD_MONITOR             = "app.is_monitor_clipboard";
  String TOGGLE_CLIPBOARD_MONITOR_TEXT        = "app.is_monitor_clipboard_text";
  String TOGGLE_CLIPBOARD_MONITOR_IMAGES      = "app.is_monitor_clipboard_images";
  String TOGGLE_CLIPBOARD_MONITOR_FILES       = "app.is_monitor_clipboard_files";
  String TOGGLE_SHOW_TOOLBAR                  = "app.is_toolbar_shown";
  String TOGGLE_SHOW_STATUSLINE               = "app.is_statusline_shown";
  String IS_FIRST_RUN                         = "app.is_first_run";
  String MODIFIER_KEY                         = "app.modifier_key";

  String NODE_GROUP_NAME                      = "app.node_group_name";
  String NODE_GROUP_PWD                       = "app.node_group_pwd";
  String HELLO_PORT                           = "app.broadcast_port";
  String CLIPBOARDTABLE_LAST_SAVED_DIR_PATH   = "ClipboardTable.Last.Saved.Directory.Path";
  String CLIPBOARDTABLE_LAST_SAVED_FILE_PATH  = "ClipboardTable.Last.Saved.File.Path";
  String UPD_ON_EVERY_START                   = "ON_EVERY_START";
  String UPD_WEEKLY                           = "WEEKLY";
  String UPD_NEVER                            = "NEVER";
  String GUI_POSITION                         = "app.window.position";
  String START_HTTP_SERVER                    = "app.start_http_server";
  String HTTP_SERVER_PORT                     = "app.http_server_port";
  String PERMANENT_NETWORK_ADDRESSES          = "app.permanent_network_addresses";
  String IGNORE_NETWORK_ADDRESSES             = "app.ignore_network_addresses";
  String OWN_IP_ADRESS                        = "app.own_ip_address";
  
  String DEFAULT_TEXT_EDITOR                  = "app.default_text_editor";
  String DEFAULT_IMAGE_EDITOR                 = "app.default_image_editor";
  String DEFAULT_FILE_BROWSER                 = "app.default_file_browser";

  String HOTKEY_BRING_SIMIDUDE_TO_FRONT       = "app.hotkey.activate_simidude_window";
  String HOTKEY_ACTIVATE_LAST_ENTRY           = "app.hotkey.activate_last_entry";

  String AUTO_ACTIVATE_NEW_ENTRY              = "app.auto.activate_last_entry";
  String AUTO_DOWNLOAD_CONTENTS               = "app.auto.download_contents";
  
  String DIALOG_DOWNLOAD_ERR_SHOW             = "app.dialog.download_error.show";
  String DIALOG_HOST_NOT_FOUND_ERR_SHOW       = "app.dialog.host_not_found_error.show";

  /**
   * Path to the css file used for serving http. Searched for in the CLASSPATH
   */
  String CSS_FILE_PATH                        = "style.css";
  String FAVICON_PATH                         = "favicon.ico";
  String SAVED_CLP_ITEM_FILE_NAME             = "clpitem.sav";
  int MIN_ALLOWED_PORT                        = 1;
  int MAX_ALLOWED_PORT                        = 65535;
  
  String CACHE_DIR_NAME                       = "cache";

}
