/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package swt.snippets;

/*
 * Create a search text control
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 * 
 * @since 3.3
 */
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DetectKeyExample {
  
  public static void main(String[] args)
  {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new GridLayout(2, false));

    final Text text = new Text(shell, SWT.SEARCH | SWT.CANCEL);

    text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    
    text.setEditable(false);

    text.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e)
      {
      }

      @Override
      public void keyReleased(KeyEvent e)
      {
        System.out.println("Released: "+e.stateMask+", "+e.keyCode+", "+e.character);
//        String keyName = getKeyName(e);
        String s = getKeyText(e);
        if (s != null)
        {
          text.setText(s);
        }
//        setKeyChain(keyChainList, keyName, false);
      }

    });
    
    
    shell.pack();
    shell.open();
    while (!shell.isDisposed())
    {
      if (!display.readAndDispatch())
        display.sleep();
    }
    display.dispose();
  }
  
  private static String getKeyText(KeyEvent e)
  {
    if (e.keyCode < 256)
    {
      StringBuilder sb = new StringBuilder();
           
      if ((e.stateMask & SWT.MOD1) > 0)
      {
        sb.append("MOD+");
      }
      
//      if ((e.stateMask & SWT.CTRL) > 0)
//      {
//        sb.append("CTRL+");
//      }
      
      if ((e.stateMask & SWT.SHIFT) > 0)
      {
        sb.append("SHIFT+");
      }
      
      if ((e.stateMask & SWT.ALT) > 0)
      {
        sb.append("ALT+");
      }
  
      String c = ""+(char)e.keyCode;
      sb.append(c.toUpperCase());
      
      return sb.toString();
    } else {
      return null;
    }
  }
  
  
  
  
}
