package com.agynamix.platform.frontend.preferences;

/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

import java.io.File;

import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;

import com.agynamix.platform.infra.PlatformUtils;
import com.agynamix.simidude.infra.SimidudeUtils;

public class ApplicationChooserFieldEditor extends FileFieldEditor {

  /**
   * Indicates whether the path must be absolute;
   * <code>false</code> by default.
   */
  private boolean enforceAbsolute = false;

  /**
   * Creates a new file field editor
   */
  protected ApplicationChooserFieldEditor()
  {
  }

  /**
   * Creates a file field editor.
   * 
   * @param name
   *          the name of the preference this field editor works on
   * @param labelText
   *          the label text of the field editor
   * @param parent
   *          the parent of the field editor's control
   */
  public ApplicationChooserFieldEditor(String name, String labelText, Composite parent)
  {
    this(name, labelText, false, parent);
  }

  /**
   * Creates a file field editor.
   * 
   * @param name
   *          the name of the preference this field editor works on
   * @param labelText
   *          the label text of the field editor
   * @param enforceAbsolute
   *          <code>true</code> if the file path must be absolute, and <code>false</code> otherwise
   * @param parent
   *          the parent of the field editor's control
   */
  public ApplicationChooserFieldEditor(String name, String labelText, boolean enforceAbsolute, Composite parent)
  {
    this(name, labelText, enforceAbsolute, FileFieldEditor.VALIDATE_ON_FOCUS_LOST, parent);
  }

  /**
   * Creates a file field editor.
   * 
   * @param name
   *          the name of the preference this field editor works on
   * @param labelText
   *          the label text of the field editor
   * @param enforceAbsolute
   *          <code>true</code> if the file path must be absolute, and <code>false</code> otherwise
   * @param validationStrategy
   *          either {@link StringButtonFieldEditor#VALIDATE_ON_KEY_STROKE} to perform on the fly checking, or
   *          {@link StringButtonFieldEditor#VALIDATE_ON_FOCUS_LOST} (the default) to perform validation only after the
   *          text has been typed in
   * @param parent
   *          the parent of the field editor's control.
   * @since 3.4
   * @see StringButtonFieldEditor#VALIDATE_ON_KEY_STROKE
   * @see StringButtonFieldEditor#VALIDATE_ON_FOCUS_LOST
   */
  public ApplicationChooserFieldEditor(String name, String labelText, boolean enforceAbsolute, int validationStrategy,
      Composite parent)
  {
    super(name, labelText, enforceAbsolute, validationStrategy, parent);
    this.enforceAbsolute = enforceAbsolute;
    init(name, labelText);
  }

  /*
   * (non-Javadoc) Method declared on StringFieldEditor. Checks whether the text input field specifies an existing file.
   */
  protected boolean checkState()
  {

    String msg = null;

    String path = getTextControl().getText();
    if (path != null)
    {
      path = path.trim();
    } else
    {
      path = "";//$NON-NLS-1$
    }
    if (path.length() == 0)
    {
      if (!isEmptyStringAllowed())
      {
        msg = getErrorMessage();
      }
    } else
    {
      File file = new File(path);
      if ((file.isFile()) || ((PlatformUtils.isMacOs()) && (file.isDirectory())))
      {
        if (enforceAbsolute && !file.isAbsolute())
        {
          msg = JFaceResources.getString("FileFieldEditor.errorMessage2");//$NON-NLS-1$
        }
      } else {
    	  if (SimidudeUtils.findProgram(path) == null)
    	  {
          msg = getErrorMessage();
    	  }
      }
    }

    if (msg != null)
    { // error
      showErrorMessage(msg);
      return false;
    }

    // OK!
    clearErrorMessage();
    return true;
  }

}
