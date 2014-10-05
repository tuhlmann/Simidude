/*
 * Copyright (c) 2004 agynamiX.com. All rights reserved. 
 * This file is made available under the terms of the
 * license this product is released under.
 * 
 * For details please see the license file you should have
 * received, or go to:
 * 
 * http://www.agynamix.com
 * 
 * Contributors: agynamiX.com (http://www.agynamix.com)
 */
package com.agynamix.platform.frontend.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import com.agynamix.platform.infra.IPreferenceConfigAdapter;
import com.agynamix.platform.infra.PlatformUtils;
import com.install4j.api.update.UpdateSchedule;
import com.install4j.api.update.UpdateScheduleRegistry;

class GlobalPreferencePageNetwork extends PlatformFieldEditorPreferencePage {

  final IPreferenceConfigAdapter configAdapter;

  StringFieldEditor              groupName;
  PasswordFieldEditor            groupPassword;
  IntegerFieldEditor             helloPort;
  IntegerFieldEditor             serverPort;
  BooleanFieldEditor             startHttpServer;
  IntegerFieldEditor             httpServerPort;
  StringFieldEditor              myOwnIPAddress;
  
  
  NetworkAddressListFieldEditor  networkAddrList;
  NetworkAddressListFieldEditor  ignoreAddrList;

  public GlobalPreferencePageNetwork(IPreferenceConfigAdapter configAdapter)
  {
    super("Network Settings", FieldEditorPreferencePage.GRID);
    this.configAdapter = configAdapter;
    setDescription("Use this preference dialog to change Simidude's network behavior.\n"
        + "The default settings work out in most cases, "
        + "anyway you might want to check the group name and password settings, "
        + "especially if you are using Simidude in a corporate network.\n\nPlease restart Simidude after making changes.");
  }

  protected void createFieldEditors()
  {
    groupName = new StringFieldEditor(IPreferenceConstants.NODE_GROUP_NAME, "Name of the group you want to join",
        getFieldEditorParent());
    groupPassword = new PasswordFieldEditor(IPreferenceConstants.NODE_GROUP_PWD,
        "Password for the group you want to join", getFieldEditorParent());
    helloPort = new IntegerFieldEditor(IPreferenceConstants.HELLO_PORT, "The Port where Broadcasts are sent to",
        getFieldEditorParent());
    helloPort.setValidRange(IPreferenceConstants.MIN_ALLOWED_PORT, IPreferenceConstants.MAX_ALLOWED_PORT);
    helloPort.setErrorMessage("The value for this port must be between " + IPreferenceConstants.MIN_ALLOWED_PORT
        + " and " + IPreferenceConstants.MAX_ALLOWED_PORT);
    serverPort = new IntegerFieldEditor(IPreferenceConstants.SERVER_PORT, "The communication port",
        getFieldEditorParent());
    serverPort.setValidRange(IPreferenceConstants.MIN_ALLOWED_PORT, IPreferenceConstants.MAX_ALLOWED_PORT);
    serverPort.setErrorMessage("The value for this port must be between " + IPreferenceConstants.MIN_ALLOWED_PORT
        + " and " + IPreferenceConstants.MAX_ALLOWED_PORT);
    startHttpServer = new BooleanFieldEditor(IPreferenceConstants.START_HTTP_SERVER, "Access Simidude via Browser",
        getFieldEditorParent());
    httpServerPort = new IntegerFieldEditor(IPreferenceConstants.HTTP_SERVER_PORT, "The Port used by the HTTP server",
        getFieldEditorParent());
    httpServerPort.setValidRange(IPreferenceConstants.MIN_ALLOWED_PORT, IPreferenceConstants.MAX_ALLOWED_PORT);
    httpServerPort.setErrorMessage("The value for this port must be between " + IPreferenceConstants.MIN_ALLOWED_PORT
        + " and " + IPreferenceConstants.MAX_ALLOWED_PORT);
    
    myOwnIPAddress = new StringFieldEditor(IPreferenceConstants.OWN_IP_ADRESS, "This machine's IP address. Leave empty for auto detect.", getFieldEditorParent());    
    
    networkAddrList = new NetworkAddressListFieldEditor(IPreferenceConstants.PERMANENT_NETWORK_ADDRESSES, "Add permanent IP addresses or network names here:", "Enter IP address or name", getFieldEditorParent());

    ignoreAddrList = new NetworkAddressListFieldEditor(IPreferenceConstants.IGNORE_NETWORK_ADDRESSES, "Ignore these IP addresses or network names:", "Enter IP address or name", getFieldEditorParent());

    addField(new SpacerFieldEditor(getFieldEditorParent()));
    addField(groupName);
    addField(new SpacerFieldEditor(getFieldEditorParent()));
    addField(groupPassword);
    addField(new SpacerFieldEditor(getFieldEditorParent()));
    addField(helloPort);
    addField(new SpacerFieldEditor(getFieldEditorParent()));
    addField(serverPort);
    addField(new SpacerFieldEditor(getFieldEditorParent()));
    addField(startHttpServer);
    addField(httpServerPort);
    addField(new SpacerFieldEditor(getFieldEditorParent()));
    addField(myOwnIPAddress);
    addField(new SpacerFieldEditor(getFieldEditorParent()));
    addField(networkAddrList);
    addField(new SpacerFieldEditor(getFieldEditorParent()));
    addField(ignoreAddrList);

    addPropertyChangeListener(startHttpServer, new IPropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent event)
      {
        httpServerPort.setEnabled(startHttpServer.getBooleanValue(), getFieldEditorParent());
      }
    });

    addPropertyChangeListener(helloPort, new IPropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent event)
      {
        setValid(checkPortsUnique());
      }
    });

    addPropertyChangeListener(serverPort, new IPropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent event)
      {
        setValid(checkPortsUnique());
      }
    });
    
    addPropertyChangeListener(httpServerPort, new IPropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent event)
      {
        setValid(checkPortsUnique());
      }
    });
    
  }

  private boolean checkPortsUnique()
  {
    boolean unique = true;
    boolean oldValid = isValid();
    try
    {
      int a = helloPort.getIntValue();
      int b = serverPort.getIntValue();
      int c = httpServerPort.getIntValue();

      if ((a == b) || (a == c) || (b == c))
      {
        unique = false;
      }

      if ((!unique) && (oldValid))
      {
        showPortNotUniqueError();
      }

    } catch (Exception e)
    {
      unique = false;
    }

    return unique;
  }

  private void showPortNotUniqueError()
  {
    PlatformUtils.showErrorMessage("Port not unique", "Please change to port settings so that each one is unique.");
    setValid(false);
  }

  protected void initializeUpdateSchedule()
  {
    if (UpdateScheduleRegistry.getUpdateSchedule() == null)
    {
      UpdateScheduleRegistry.setUpdateSchedule(UpdateSchedule.ON_EVERY_START);
    }
  }

  /**
   * @see org.eclipse.jface.preference.FieldEditorPreferencePage#initialize()
   */
  protected void initialize()
  {
    super.initialize();
    // boolean isStartAsServer =
    // startAsServer.getPreferenceStore().getBoolean(IPreferenceConstants.START_AS_SERVER);
    initializeUpdateSchedule();
    boolean isStartHttpServer = startHttpServer.getPreferenceStore().getBoolean(IPreferenceConstants.START_HTTP_SERVER);
    httpServerPort.setEnabled(isStartHttpServer, getFieldEditorParent());

  }

}
