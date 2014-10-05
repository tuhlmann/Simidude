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
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;

import com.agynamix.platform.frontend.gui.HotkeyRegistrarFactory;
import com.agynamix.platform.frontend.gui.IHotkeyRegistrar;
import com.agynamix.platform.infra.IPreferenceConfigAdapter;
import com.agynamix.platform.infra.PlatformUtils;
import com.agynamix.platform.infra.PlatformUtils.OS;
import com.agynamix.simidude.frontend.action.SimidudeHotkeyActions;
import com.agynamix.simidude.infra.SimidudeUtils;
import com.install4j.api.update.UpdateSchedule;
import com.install4j.api.update.UpdateScheduleRegistry;


class GlobalPreferencePageDefaults extends PlatformFieldEditorPreferencePage {

  final IPreferenceConfigAdapter configAdapter;
  
  FieldEditor                    startMinimized;
  BooleanFieldEditor             restoreLatestEntry;
  BooleanFieldEditor             showBalloonTooltip;
  ComboFieldEditor               updateSchedule;
  
  ComboFieldEditor               modifierKey;
  
  ApplicationChooserFieldEditor  defaultTextEditor;
  ApplicationChooserFieldEditor  defaultImageViewer;
  ApplicationChooserFieldEditor  defaultFileBrowser;
  
  HotkeyFieldEditor              bringSimidudeToFront;
  HotkeyFieldEditor              activateLastEntry;

  BooleanFieldEditor             autoActivateNewEntry;
  BooleanFieldEditor             autoDownloadContents;
  
  BooleanFieldEditor             showDownloadErrorDialog;
  BooleanFieldEditor             showHostNotFoundDialog;
 
  
  String[][] modifierKeyNames = null;
  
  String oldBringSimidudeToFront;
  String oldActivateLastEntry;
  

  public GlobalPreferencePageDefaults(IPreferenceConfigAdapter configAdapter) {
    super("Application Settings", FieldEditorPreferencePage.FLAT);
    this.configAdapter = configAdapter;
    setDescription("Use this preference dialog to change Simidude's behavior.\n"
        + "\nPlease restart Simidude after making changes.");
  }
  
  protected void createFieldEditors() {
    startMinimized = new BooleanFieldEditor(IPreferenceConstants.START_MINIMIZED, "Start minimized",
        getFieldEditorParent());
    restoreLatestEntry = new BooleanFieldEditor(IPreferenceConstants.RESTORE_LATEST_ENTRY,
        "Restore the latest Clipboard entry after a system restart", getFieldEditorParent());
    showBalloonTooltip = new BooleanFieldEditor(IPreferenceConstants.SHOW_BALLOON_TOOLTIP,
        "Show tooltips when new items arrive", getFieldEditorParent());
    updateSchedule = new ComboFieldEditor(IPreferenceConstants.UPDATE_SCHEDULE, "Update Schedule", new String[][] {
        { "On Every Start", IPreferenceConstants.UPD_ON_EVERY_START }, { "Weekly", IPreferenceConstants.UPD_WEEKLY },
        { "Never", IPreferenceConstants.UPD_NEVER } }, getFieldEditorParent());
    
    modifierKeyNames = new String[][] {{SimidudeUtils.getKeyCodeName(SWT.SHIFT), ""+SWT.SHIFT}, 
        {SimidudeUtils.getKeyCodeName(SWT.MOD1), ""+SWT.MOD1}, {SimidudeUtils.getKeyCodeName(SWT.ALT), ""+SWT.ALT}};
    modifierKey = new ComboFieldEditor(IPreferenceConstants.MODIFIER_KEY, "The Modifier Key", modifierKeyNames, getFieldEditorParent());
    
    defaultTextEditor  = new ApplicationChooserFieldEditor(IPreferenceConstants.DEFAULT_TEXT_EDITOR , "Text Editor for text entries", getFieldEditorParent());
    defaultImageViewer = new ApplicationChooserFieldEditor(IPreferenceConstants.DEFAULT_IMAGE_EDITOR, "Image Editor for image entries", getFieldEditorParent());
    defaultFileBrowser = new ApplicationChooserFieldEditor(IPreferenceConstants.DEFAULT_FILE_BROWSER, "File Browser for files/directories", getFieldEditorParent());

    defaultTextEditor.setEmptyStringAllowed(true);
    defaultImageViewer.setEmptyStringAllowed(true);
    defaultFileBrowser.setEmptyStringAllowed(true);
    
    OS os = PlatformUtils.getOsName();

    switch (os)
    {
      case win32:
      case win64:
        defaultTextEditor.setFileExtensions(new String[] {"*.exe; *.com; *.bat; *.cmd", "*.*"});
        defaultImageViewer.setFileExtensions(new String[] {"*.exe; *.com; *.bat; *.cmd", "*.*"});
        defaultFileBrowser.setFileExtensions(new String[] {"*.exe; *.com; *.bat; *.cmd", "*.*"});
        break;
      case macosx:
      case macosx64:
        defaultTextEditor.setFileExtensions(new String[] {"*.app; *.sh", "*.*"});
        defaultImageViewer.setFileExtensions(new String[] {"*.app; *.sh", "*.*"});
        defaultFileBrowser.setFileExtensions(new String[] {"*.app; *.sh", "*.*"});
        break;
    }

    IHotkeyRegistrar hotkeyRegistrar = HotkeyRegistrarFactory.getHotkeyRegistrarInstance();
    
    if (hotkeyRegistrar.isEnabled())
    {
      bringSimidudeToFront = new HotkeyFieldEditor(IPreferenceConstants.HOTKEY_BRING_SIMIDUDE_TO_FRONT, 
          "HotKey to make Simidude visible (clear to disable)", getFieldEditorParent()); 
      
      String msg = "HotKey to paste the latest Simidude entry (clear to disable)";
//      if (os != OS.win32)
//      {
//        msg = "HotKey to activate the lastest Simidude entry (clear to disable)";      
//      }
         
      activateLastEntry = new HotkeyFieldEditor(IPreferenceConstants.HOTKEY_ACTIVATE_LAST_ENTRY, msg, getFieldEditorParent());
    }

    autoActivateNewEntry = new BooleanFieldEditor(IPreferenceConstants.AUTO_ACTIVATE_NEW_ENTRY,
        "Automatically copy items to clipboard when they arrive", getFieldEditorParent());
    
    autoDownloadContents = new BooleanFieldEditor(IPreferenceConstants.AUTO_DOWNLOAD_CONTENTS,
        "Automatically download contents from remote computers", getFieldEditorParent());
    
    showDownloadErrorDialog = new BooleanFieldEditor(IPreferenceConstants.DIALOG_DOWNLOAD_ERR_SHOW,
        "Do not show a message when a file cannot be downloaded", getFieldEditorParent());
    
    showHostNotFoundDialog = new BooleanFieldEditor(IPreferenceConstants.DIALOG_HOST_NOT_FOUND_ERR_SHOW,
        "Do not show a message when a host cannot be contacted", getFieldEditorParent());
    
    addField(startMinimized);
    addField(new SpacerFieldEditor(getFieldEditorParent()));
    addField(restoreLatestEntry);
    addField(new SpacerFieldEditor(getFieldEditorParent()));
    addField(showBalloonTooltip);
    addField(new SpacerFieldEditor(getFieldEditorParent()));
    addField(updateSchedule);
    addField(new SpacerFieldEditor(getFieldEditorParent()));
    addField(defaultTextEditor);
    addField(new SpacerFieldEditor(getFieldEditorParent()));
    addField(defaultImageViewer);
    addField(new SpacerFieldEditor(getFieldEditorParent()));
    addField(defaultFileBrowser);
    addField(new SpacerFieldEditor(getFieldEditorParent()));
    addField(modifierKey);
    
    
    if (hotkeyRegistrar.isEnabled())
    {
      addField(new SpacerFieldEditor(getFieldEditorParent()));
      addField(bringSimidudeToFront);
      addField(new SpacerFieldEditor(getFieldEditorParent()));
      addField(activateLastEntry);
    }

    addField(new SpacerFieldEditor(getFieldEditorParent()));
    addField(autoActivateNewEntry);
    addField(new SpacerFieldEditor(getFieldEditorParent()));
    addField(autoDownloadContents);

    addField(new SpacerFieldEditor(getFieldEditorParent()));
    addField(showDownloadErrorDialog);
    addField(new SpacerFieldEditor(getFieldEditorParent()));
    addField(showHostNotFoundDialog);
    
    addPropertyChangeListener(updateSchedule, new IPropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent event)
      {
        UpdateSchedule schedule = null;
        String v = (String) event.getNewValue();
        if (v.equals(IPreferenceConstants.UPD_ON_EVERY_START))
        {
          schedule = UpdateSchedule.ON_EVERY_START;
        } else if (v.equals(IPreferenceConstants.UPD_WEEKLY))
        {
          schedule = UpdateSchedule.WEEKLY;
        } else if (v.equals(IPreferenceConstants.UPD_NEVER))
        {
          schedule = UpdateSchedule.NEVER;
        }
        UpdateScheduleRegistry.setUpdateSchedule(schedule);
      }
    });
    
    if (hotkeyRegistrar.isEnabled())
    {
      //Listen to events from the PreferenceStore which are emitted when save or apply is pushed
      configAdapter.addPropertyChangeListener(new IPropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent event) {
          //System.out.println("propertyChanged Called");
          String newBringSimidudeToFront = bringSimidudeToFront.getStringValue();
          String newActivateLastEntry    = activateLastEntry.getStringValue();
          if ((!oldBringSimidudeToFront.equals(newBringSimidudeToFront)) || (!oldActivateLastEntry.equals(newActivateLastEntry)))
          {
  //          System.out.println("Unregister and register new hotkeys");
            SimidudeHotkeyActions.unregisterHotkeys();
            SimidudeHotkeyActions.registerHotkeys();
            oldBringSimidudeToFront = newBringSimidudeToFront;
            oldActivateLastEntry    = newActivateLastEntry;
          }
        }
      });
    }
  }    
  
  /**
   * @see org.eclipse.jface.preference.FieldEditorPreferencePage#initialize()
   */
  protected void initialize() {
    super.initialize();
    oldBringSimidudeToFront = getPreferenceStore().getString(IPreferenceConstants.HOTKEY_BRING_SIMIDUDE_TO_FRONT);
    oldActivateLastEntry    = getPreferenceStore().getString(IPreferenceConstants.HOTKEY_ACTIVATE_LAST_ENTRY);
    
    if (oldBringSimidudeToFront == null) oldBringSimidudeToFront = "";
    if (oldActivateLastEntry == null)    oldActivateLastEntry = "";
    
//    String textEditor = getPreferenceStore().getString(IPreferenceConstants.DEFAULT_TEXT_EDITOR);
//    if (isEmpty(textEditor))
//    {
//      Program p = Program.findProgram(".txt");
//      defaultTextEditor.setStringValue(p.getName());
//    }
//    String imageEditor = getPreferenceStore().getString(IPreferenceConstants.DEFAULT_IMAGE_EDITOR);
//    if (isEmpty(imageEditor))
//    {
//      Program p = Program.findProgram(".jpg");
//      defaultImageViewer.setStringValue(p.getName());
//    }
  }

}

