package swt.snippets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ArrowButtonExample {
  Display d;

  Shell s;

  ArrowButtonExample() {
    d = new Display();
    s = new Shell(d);
    s.setSize(250, 250);
    s.setText("A Button Example");
    final Button b1 = new Button(s, SWT.ARROW | SWT.UP);
    b1.setBounds(100, 55, 20, 15);
    final Button b2 = new Button(s, SWT.ARROW | SWT.DOWN);
    b2.setBounds(100, 70, 20, 15);
    final Text t1 = new Text(s, SWT.BORDER | SWT.SINGLE | SWT.CENTER);
    t1.setBounds(80, 55, 20, 30);
    t1.setText("1");
    t1.selectAll();
    b1.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        int n = new Integer(t1.getText()).intValue();
        n++;
        t1.setText(new Integer(n).toString());
        t1.selectAll();
      }
    });
    b2.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        int n = new Integer(t1.getText()).intValue();
        n--;
        t1.setText(new Integer(n).toString());
        t1.selectAll();
      }
    });
    s.open();
    while (!s.isDisposed()) {
      if (!d.readAndDispatch())
        d.sleep();
    }
    d.dispose();
  }

  public static void main(String[] argv) {
    new ArrowButtonExample();
  }

}