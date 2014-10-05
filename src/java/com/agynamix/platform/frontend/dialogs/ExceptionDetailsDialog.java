package com.agynamix.platform.frontend.dialogs;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * A dialog to display an error message, product and
 * provider information, plus the details of the exception
 * itself. A details button shows or hides an error details
 * viewer.
 * TODO: Rename into MessageDetailsDialog
 */
public class ExceptionDetailsDialog extends AbstractDetailsDialog {

  public enum MessageClass { OK, INFO, WARNING, ERROR };
  
  IStatus a;
  
  /**
   * The details to be shown ({@link Exception},
   * {@link IStatus}, or <code>null</code> if no details)
   */
  private final Throwable details;
  private final String strDetails;

  public ExceptionDetailsDialog(Shell parentShell, String title, Image image, String message, IStatus details)
  {
    super(parentShell, title, getTitle(title, details), getImage(image, details), getMessage(message, details));    
    this.details = details.getException();
    strDetails = null;
  }

  public ExceptionDetailsDialog(Shell parentShell, String title, Image image, String message, Throwable details)
  {
    super(parentShell, details.getLocalizedMessage(), getTitle(title, details), getImage(image, details), 
        getMessage(message, details));
    
    this.details = details;
    strDetails = null;
  }
  
  public ExceptionDetailsDialog(Shell parentShell, String title, Image image, String message, IStatus details, String strDetails)
  {
    super(parentShell, title, getTitle(title, details), getImage(image, details), getMessage(message, details));    
    this.details = details.getException();
    this.strDetails = strDetails;
  }

  protected Control createDetailsArea(Composite parent)
  {
    // create the details area
    Composite panel = new Composite(parent, SWT.NONE);
    panel.setLayoutData(new GridData(GridData.FILL_BOTH));
    GridLayout layout = new GridLayout();
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    panel.setLayout(layout);

    // create the details content
    createProductInfoArea(panel);
    createDetailsViewer(panel);

    return panel;
  }

  protected Composite createProductInfoArea(Composite parent)
  {
    Composite composite = new Composite(parent, SWT.NULL);
    composite.setLayoutData(new GridData());
    GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
    composite.setLayout(layout);

    new Label(composite, SWT.NONE).setText("Provider:");
    new Label(composite, SWT.NONE).setText("AGYNAMIX");
    return composite;
  }

  protected Control createDetailsViewer(Composite parent)
  {
    if ((details == null) && (strDetails == null))
      return null;

    Text text = new Text(parent, SWT.COLOR_WHITE | SWT.MULTI | SWT.READ_ONLY | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);

    text.setLayoutData(new GridData(GridData.FILL_BOTH));

    text.setText(getDetailsAsString());
    text.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

    return text;
  }

  @Override
  protected String getDetailsAsString()
  {
    if ((details == null) && (strDetails == null))
    {
      return ""; //$NON-NLS-1$
    }

    // Create the content
    StringWriter writer = new StringWriter(1000);
    
    if (details != null)
    {
      if (details instanceof Throwable)
      {
        appendException(new PrintWriter(writer), (Throwable) details);
      } else if (details instanceof IStatus)
      {
        appendStatus(new PrintWriter(writer), (IStatus) details, 0);
      }
    }
    
    if (strDetails != null) {
      writer.append("\n");
      writer.append(strDetails);
    }

    return writer.toString();

  }

  /**
   * Answer the title based upon the provided title and
   * the details object.
   */
  public static String getTitle(String title, Object details)
  {
    if (title != null)
      return title;
    if (details instanceof Throwable)
    {
      Throwable e = (Throwable) details;
      while (e instanceof InvocationTargetException)
        e = ((InvocationTargetException) e).getTargetException();
      String name = e.getClass().getName();
      return name.substring(name.lastIndexOf('.') + 1);
    }
    return "Exception";
  }

  /**
   * Answer the image based upon the provided image and
   * the details object.
   */
  public static Image getImage(Image image, Object details)
  {
    if (image != null)
      return image;
    ImageRegistry imageRegistry = JFaceResources.getImageRegistry();
    if (details instanceof IStatus)
    {
      switch (((IStatus) details).getSeverity())
      {
        case IStatus.ERROR:
          return imageRegistry.get(Dialog.DLG_IMG_ERROR);
        case IStatus.WARNING:
          return imageRegistry.get(Dialog.DLG_IMG_WARNING);
        case IStatus.INFO:
          return imageRegistry.get(Dialog.DLG_IMG_INFO);
        case IStatus.OK:
          return null;
      }
    }
    return imageRegistry.get(Dialog.DLG_IMG_ERROR);
  }

  /**
   * Answer the message based upon the provided message
   * and the details object.
   */
  public static String getMessage(String message, Object details)
  {
    if (details instanceof Throwable)
    {
      Throwable e = (Throwable) details;
      while (e instanceof InvocationTargetException)
        e = ((InvocationTargetException) e).getTargetException();
      if (message == null)
        return e.toString();
      return MessageFormat.format(message, new Object[] { e.toString() });
    }
    if (details instanceof IStatus)
    {
      String statusMessage = ((IStatus) details).getMessage();
      if (message == null)
        return statusMessage;
      return MessageFormat.format(message, new Object[] { statusMessage });
    }
    if (message != null)
      return message;
    return "An exception has occured.";
  }

  public static void appendException(PrintWriter writer, Throwable ex)
  {
    if (ex instanceof CoreException)
    {
      appendStatus(writer, ((CoreException) ex).getStatus(), 0);
      writer.println();
    }
    appendStackTrace(writer, ex);
    if (ex instanceof InvocationTargetException)
      appendException(writer, ((InvocationTargetException) ex).getTargetException());
  }

  public static void appendStatus(PrintWriter writer, IStatus status, int nesting)
  {
    for (int i = 0; i < nesting; i++)
      writer.print("  "); //$NON-NLS-1$
    writer.println(status.getMessage());
    IStatus[] children = status.getChildren();
    for (int i = 0; i < children.length; i++)
      appendStatus(writer, children[i], nesting + 1);
  }

  public static void appendStackTrace(PrintWriter writer, Throwable ex)
  {
    ex.printStackTrace(writer);
  }
}