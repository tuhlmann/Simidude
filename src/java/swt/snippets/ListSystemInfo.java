package swt.snippets;

import java.util.Properties;

public class ListSystemInfo {

  /**
   * @param args
   */
  public static void main(String[] args)
  {
    Properties p = System.getProperties();
    for (Object key : p.keySet())
    {
      System.out.println(key+" = "+p.getProperty(key.toString()));
    }
    
    
/*
java.runtime.name = Java(TM) 2 Runtime Environment, Standard Edition
java.vm.version = 1.5.0_16-134
java.vm.vendor = Apple Inc.
java.vendor.url = http://www.apple.com/
java.vm.name = Java HotSpot(TM) Client VM
user.country = DE
java.runtime.version = 1.5.0_16-b06-290
os.arch = i386
os.name = Mac OS X
sun.jnu.encoding = MacRoman
java.class.version = 49.0
sun.management.compiler = HotSpot Client Compiler
os.version = 10.5.7
http.nonProxyHosts = local|*.local|169.254/16|*.169.254/16
file.encoding = UTF-8
java.home = /System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Home
sun.arch.data.model = 32
user.language = de
java.version = 1.5.0_16
java.vendor = Apple Inc.
 */

  }

}
