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
package com.agynamix.platform.frontend.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

public abstract class ProgressBarDialog extends TitleAreaDialog {

  private Label              processMessageLabel;              // info of process finish
  private Label              lineLabel;                        //
  private Composite          progressBarComposite;             //
  private CLabel             message;                          //
  private ProgressBar        progressBar     = null;           //

  protected volatile boolean isClosed        = false;          // closed state

  protected int              executeTime     = 50;             // process times
  protected Image            processImage    = null;           // FIXME: SWTUtil.getImageOfMessage();//image

  public ProgressBarDialog(Shell parent)
  {
    super(parent);
  }

  /* (non-Javadoc)
   * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
   */
  protected void createButtonsForButtonBar(Composite parent) 
  {
    createButton(parent, IDialogConstants.ABORT_ID, IDialogConstants.ABORT_LABEL, true);
  }
  
  @Override
  protected void buttonPressed(int buttonId)
  {
    if (buttonId == IDialogConstants.ABORT_ID)
    {
      isClosed = true;
    }
    super.buttonPressed(buttonId);
  }
  /**
   * @see org.eclipse.jface.dialogs.TitleAreaDialog#createContents(org.eclipse.swt.widgets.Composite)
   */
  protected Control createContents(Composite parent)
  {
    Control content = super.createContents(parent);
    getShell().setText("Please wait...");
    setTitle("Please wait...");
    setMessage("Please be patient while the requested operation is performed.");
    return content;
  }

  // public int open()
  // {
  // createContents(); // create window
  // shell.open();
  // shell.layout();
  //
  // // start work
  // new ProcessThread(executeTime).start();
  //
  // return result;
  // }

  protected Control createDialogArea(Composite parent)
  {
    final Composite parentComp = (Composite) super.createDialogArea(parent);
    final Composite composite = new Composite(parentComp, SWT.NONE);
    composite.setSize(parentComp.getSize());
    
    final GridLayout gridLayout = new GridLayout();
    gridLayout.verticalSpacing = 10;

    composite.setLayout(gridLayout);
    composite.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
    composite.setLayout(new GridLayout());

    message = new CLabel(composite, SWT.NONE);
//    message.setImage(processImage);
    message.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
    message.setText("Transfering data...");

    progressBarComposite = new Composite(parentComp, SWT.NONE);
    progressBarComposite.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
    progressBarComposite.setLayout(new FillLayout());

    progressBar = new ProgressBar(progressBarComposite, SWT.SMOOTH);
    progressBar.setMaximum(executeTime);

    processMessageLabel = new Label(parentComp, SWT.NONE);
    processMessageLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
    lineLabel = new Label(parentComp, SWT.HORIZONTAL | SWT.SEPARATOR);
    lineLabel.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));

    return composite;
  }

  protected abstract String process(int times);

  protected void cleanUp()
  {
  }

  protected void doBefore()
  {
  }

  protected void doAfter()
  {
  }

  class ProcessThread extends Thread {
    private int              max        = 0;
    private volatile boolean shouldStop = false;

    ProcessThread(int max)
    {
      this.max = max;
    }

    public void run()
    {
      doBefore();
      for (final int[] i = new int[] { 1 }; i[0] <= max; i[0]++)
      {
        //
        final String info = process(i[0]);
        if (getShell().getDisplay().isDisposed())
        {
          return;
        }
        getShell().getDisplay().syncExec(new Runnable() {
          public void run()
          {
            if (progressBar.isDisposed())
            {
              return;
            }
            //
            processMessageLabel.setText(info);
            //
            progressBar.setSelection(i[0]);
            //
            if (i[0] == max || isClosed)
            {
              if (isClosed)
              {
                shouldStop = true;//
                cleanUp();//
              }
            }
          }
        });

        if (shouldStop)
        {
          break;
        }
      }
      doAfter();
    }
  }

  public void setExecuteTime(int executeTime)
  {
    this.executeTime = executeTime;
  }

  public abstract void initGuage();

}
