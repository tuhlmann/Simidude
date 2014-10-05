package com.agynamix.platform.frontend.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.agynamix.platform.infra.ApplicationInfo;

/**
 * A dialog with a details button. Subclasses provide
 * details area content.
 */
public abstract class AbstractDetailsDialog extends Dialog
{
   private final String shortTitle; 
   private final String title;
   private final String message;
   private final Image image;

   private Button   detailsButton;
   private Button   reportBugButton;
   private Control  detailsArea;
   private Point    cachedWindowSize;

   public AbstractDetailsDialog(Shell parentShell, String shortTitle, String title, Image image, String message) {

      super(parentShell);

      this.shortTitle = shortTitle;
      this.title      = title;
      this.image      = image;
      this.message    = message;

      setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
   }

   protected void buttonPressed(int id) {
      if (id == IDialogConstants.DETAILS_ID)
         toggleDetailsArea();
      else if (id == IDialogConstants.OPEN_ID)
        reportBug();
      else
        super.buttonPressed(id);
   }

   protected void configureShell(Shell shell) {
      super.configureShell(shell);
      if (title != null)
         shell.setText(title);
   }

   protected void createButtonsForButtonBar(Composite parent) {
      createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,  true);
      detailsButton   = createButton(parent, IDialogConstants.DETAILS_ID, IDialogConstants.SHOW_DETAILS_LABEL, false);
      reportBugButton = createButton(parent, IDialogConstants.OPEN_ID, "Report Bug", false);
   }

   protected Control createDialogArea(Composite parent) {
      Composite composite =
         (Composite) super.createDialogArea(parent);
      composite.setLayoutData(
         new GridData(GridData.FILL_HORIZONTAL));

      if (image != null) 
      {
         ((GridLayout) composite.getLayout()).numColumns = 2;
         Label label = new Label(composite, 0);
         image.setBackground(label.getBackground());
         label.setImage(image);
         label.setLayoutData(new GridData( GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_BEGINNING));
      }

      Label label = new Label(composite, SWT.WRAP);
      if (message != null)
         label.setText(message);
      GridData data =
         new GridData(
            GridData.FILL_HORIZONTAL
               | GridData.VERTICAL_ALIGN_CENTER);
      data.widthHint =
         convertHorizontalDLUsToPixels(
            IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
      label.setLayoutData(data);
      label.setFont(parent.getFont());

      return composite;
   }

   /**
	 * Toggles the unfolding of the details area. This is
	 * triggered by the user pressing the details button.
	 */
   protected void toggleDetailsArea() {
      Point oldWindowSize = getShell().getSize();
      Point newWindowSize = cachedWindowSize;
      cachedWindowSize = oldWindowSize;

      // show the details area
      if (detailsArea == null) {
         detailsArea = createDetailsArea((Composite) getContents());
         detailsButton.setText(IDialogConstants.HIDE_DETAILS_LABEL);
      }

      // hide the details area
      else {
         detailsArea.dispose();
         detailsArea = null;
         detailsButton.setText(IDialogConstants.SHOW_DETAILS_LABEL);
      }

      /*
       * Must be sure to call
       * getContents().computeSize(SWT.DEFAULT,
       * SWT.DEFAULT) before calling
       * getShell().setSize(newWindowSize); 
       * since controls have been added or removed
       */

      // compute the new window size
      Point oldSize = getContents().getSize();
      Point newSize = getContents().computeSize(SWT.DEFAULT, SWT.DEFAULT);
      if (newWindowSize == null)
         newWindowSize = new Point(oldWindowSize.x, oldWindowSize.y + (newSize.y - oldSize.y));

      // crop new window size to screen
      Point windowLoc = getShell().getLocation();
      Rectangle screenArea = getContents().getDisplay().getClientArea();
      if (newWindowSize.y > screenArea.height - (windowLoc.y - screenArea.y))
         newWindowSize.y = screenArea.height - (windowLoc.y - screenArea.y);

      getShell().setSize(newWindowSize);
      ((Composite) getContents()).layout();
   }
   
   protected void reportBug()
   {
     // Bugtitle is exception message
     BugzScoutDialog dialog = new BugzScoutDialog(getShell(), shortTitle, getDetailsAsString(), ApplicationInfo.getApplicationInfo() );
     dialog.open();
   }

   /**
	 * Create the details area with content.
	 * 
	 * @param parent the parent of the details area
	 * @return the details area
	 */
   protected abstract Control createDetailsArea(Composite parent);
   
   /**
    * Provide the exception details and Plugin details as a string. This will be appended
    * to the bugreport the user might send.
    * @return a string containing the exception and plugin details 
    */
   protected abstract String getDetailsAsString();
}
