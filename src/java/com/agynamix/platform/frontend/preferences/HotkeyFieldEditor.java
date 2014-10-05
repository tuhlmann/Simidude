package com.agynamix.platform.frontend.preferences;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.agynamix.simidude.infra.SimidudeUtils;

public class HotkeyFieldEditor extends StringFieldEditor {

  final Composite parent;
  
  public HotkeyFieldEditor(String name, String labelText, Composite parent)
  {
    super(name, labelText, parent);
    this.parent = parent;
    initialize();
  }

  private void initialize()
  {
    final Text text = getTextControl(parent);
    text.setEditable(true);
    text.setBackground(text.getShell().getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
    text.addKeyListener(new KeyAdapter(){
      @Override
      public void keyReleased(KeyEvent e)
      {
        String s = getKeyText(e);
        if (s != null)
        {
          text.setText(s);
        }
      }
    });
  }

  private String getKeyText(KeyEvent e)
  {
    if ((e.keyCode == SWT.DEL) || (e.keyCode == 8))
    {
      return "";      
    }
    
    String keyStr = "";
    
    if ((e.keyCode > 31) && (e.keyCode < 256))
    {
      StringBuilder sb = new StringBuilder();
           
      if ((e.stateMask & SWT.COMMAND) > 0)
      {
        sb.append(SimidudeUtils.getKeyCodeName(SWT.COMMAND)).append("+");
      }
      
      if ((e.stateMask & SWT.CONTROL) > 0)
      {
        sb.append(SimidudeUtils.getKeyCodeName(SWT.CONTROL)).append("+");
      }
      
      if ((e.stateMask & SWT.SHIFT) > 0)
      {
        sb.append(SimidudeUtils.getKeyCodeName(SWT.SHIFT)).append("+");
      }
      
      if ((e.stateMask & SWT.ALT) > 0)
      {
        sb.append(SimidudeUtils.getKeyCodeName(SWT.ALT)).append("+");
      }
  
      if (isStringChar((char)e.keyCode))
      {
        keyStr = ""+(char)e.keyCode;
      } else {
        return null;
      }
      
      sb.append(keyStr.toUpperCase());
      
      return sb.toString();
    } else {
      return null;
    }
  }
  
  /**
   * Return true if the character is printable IN ASCII. Not using
   * Character.isLetterOrDigit(); applies to all unicode ranges
   */
  protected boolean isStringChar(char ch) {
    if (ch >= 'a' && ch <= 'z')
      return true;
    if (ch >= 'A' && ch <= 'Z')
      return true;
    if (ch >= '0' && ch <= '9')
      return true;
    switch (ch) {
    case '/':
    case '-':
    case ':':
    case '.':
    case ',':
    case '_':
    case '$':
    case '%':
    case '\'':
    case '(':
    case ')':
    case '[':
    case ']':
    case '<':
    case '>':
      return true;
    }
    return false;
  }
  
}
