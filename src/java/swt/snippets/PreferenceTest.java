package swt.snippets;

import java.util.prefs.Preferences;

public class PreferenceTest {

  /**
   * @param args
   */
  public static void main(String[] args)
  {
    Preferences prefs = Preferences.userNodeForPackage(PreferenceTest.class);
    prefs.put("my_pref_key", "This is a sample pref value");
    
    String retrieved = prefs.get("my_pref_key", "no value");
    
    System.out.println("Returned: "+retrieved);

  }

}
