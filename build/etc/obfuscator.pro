-injars /Users/tuhlmann/entw/aktuell/Simidude/target/distribution/Simidude/lib/simidude-naked.jar
-outjars /Users/tuhlmann/entw/aktuell/Simidude/target/distribution/Simidude/lib/simidude.jar

-libraryjars /Users/tuhlmann/entw/aktuell/Simidude/lib
-libraryjars /System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Classes/charsets.jar
-libraryjars /System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Classes/classes.jar
-libraryjars /System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Classes/dt.jar
-libraryjars /System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Classes/jce.jar
-libraryjars /System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Classes/jconsole.jar
-libraryjars /System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Classes/jsse.jar
-libraryjars /System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Classes/laf.jar
-libraryjars /System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Classes/ui.jar

-target 1.5
-printmapping out.map
# -keepattributes 'SourceFile,LineNumberTable'


-keep class *MBean

-keep class * extends com.agynamix.platform.infra.IPluginMarker

-keep class com.agynamix.platform.icons.PlatformIcons

# Keep - Applications. Keep all application classes, along with their 'main'
# methods.
-keepclasseswithmembers public class * {
    public static void main(java.lang.String[]);
}

# Also keep - Enumerations. Keep the special static methods that are required in
# enumeration classes.
-keepclassmembers enum  * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Also keep - Database drivers. Keep all implementations of java.sql.Driver.
-keep class * extends java.sql.Driver

# Also keep - Swing UI L&F. Keep all extensions of javax.swing.plaf.ComponentUI,
# along with the special 'createUI' method.
-keep class * extends javax.swing.plaf.ComponentUI {
    public static javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent);
}

# Keep names - Native method names. Keep all native class/method names.
-keepclasseswithmembers,allowshrinking class * {
    native <methods>;
}
