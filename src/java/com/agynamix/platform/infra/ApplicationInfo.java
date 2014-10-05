package com.agynamix.platform.infra;

import java.io.IOException;
import java.util.Properties;

public class ApplicationInfo {
  
  public final static String APPLICATION_NAME     = "Application-Name";
  public final static String APPLICATION_VERSION  = "Application-Version";
  public final static String APPLICATION_YEARS    = "Application-Years";
  public final static String COMPANY_NAME         = "Company-Name";
  public final static String COMPANY_EMAIL        = "Company-Email";
  public final static String COMPANY_WWW          = "Company-WWW";
  public final static String COMPANY_ORDER_URL    = "Company-Order-Url";
  public final static String REPOSITORY_REV       = "Repository-Revision";
  public final static String BUILD_NUMBER         = "Build-Number";
  public final static String BUILD_TIME           = "Build-Time";  

  public final static String VALUE_FOR_UNKNOWN_KEY = "";
  
  public final static String APP_INFO_FILE        = "/appinfo.txt";
  
  static String[] systemProps = new String[] { "java.runtime.name", "java.vm.version", "java.vm.vendor", "java.vendor.url",
      "java.vm.name", "user.country", "java.runtime.version", "os.arch", "os.name", "sun.jnu.encoding",
      "sun.management.compiler", "os.version", "http.nonProxyHosts", "file.encoding", "user.language",
      "java.version", "java.vendor" };
  
  final static Properties properties;

  static
  {
    properties = new Properties();
    try
    {
      properties.load(ApplicationInfo.class.getResourceAsStream(APP_INFO_FILE));
    } catch (IOException e)
    {
    }
  }

  private ApplicationInfo()
  {
  }

  public static String getApplicationName()
  {
    return saveGet(APPLICATION_NAME);
  }

  public static String getApplicationVersion()
  {
    return saveGet(APPLICATION_VERSION);
  }

  public static String getApplicationYears()
  {
    return saveGet(APPLICATION_YEARS);
  }
  
  public static String getCompanyName()
  {
    return saveGet(COMPANY_NAME);
  }
  
  public static String getCompanyEmail()
  {
    return saveGet(COMPANY_EMAIL);
  }
  
  public static String getCompanyWww()
  {
    return saveGet(COMPANY_WWW);
  }
  
  public static String getCompanyOrderUrl()
  {
    return saveGet(COMPANY_ORDER_URL);
  }
  
  public static String getRepoRevision()
  {
    return saveGet(REPOSITORY_REV);
  }
  
  public static String getBuildNumber()
  {
    return saveGet(BUILD_NUMBER);
  }
  
  public static String getBuildTime()
  {
    return saveGet(BUILD_TIME);
  }
  
  private static String saveGet(String key)
  {
    String value = properties.getProperty(key);
    if (value != null)
    {
      return value.trim();
    } else {
      return VALUE_FOR_UNKNOWN_KEY;
    }
  }

  /**
   * @return Copy all of the application info into a string
   */
  public static String getApplicationInfo()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("== Application Information: ==");
    for (Object key : properties.keySet())
    {
      sb.append("\n");
      sb.append(key).append("=").append(properties.get(key));
    }
    sb.append("\n\n== System Properties: ==");
    for (String key : systemProps)
    {
      sb.append("\n");
      sb.append(key).append("=").append(System.getProperty(key));
    }
    return sb.toString();
  }
  
}