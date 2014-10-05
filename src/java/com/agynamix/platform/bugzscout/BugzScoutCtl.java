package com.agynamix.platform.bugzscout;

import java.util.HashMap;
import java.util.Map;

public class BugzScoutCtl {
  
//  static {
//    String logging = "org.apache.commons.logging";
//    
//    System.setProperty(logging + ".Log", logging + ".impl.SimpleLog");
//    System.setProperty(logging + ".logging.simplelog.showdatetime", "true");
//    System.setProperty(logging + ".simplelog.log.httpclient.wire", "debug");
//    System.setProperty(logging + ".simplelog.log.org.apache.commons.httpclient", "debug");
//  }

  /** URL to send Request to */
  String url;

  /** the FogBugz user */
  String userName;

  /** the FogBugz project */
  String project;

  /** the FogBugz Area within the project */
  String area;

  /** the default message to show to the user */
  String defaultMessage;

  /**
   * Submit a bug to BugzScout
   * 
   * @param description
   *          the description of the bug (should be generated by the software. Same bugs should have same description
   *          line so FogBugz will identify them as duplicates
   * @param extraInfo
   *          Info the user entered
   * @param email
   *          the email address of the user
   * @param isForceNewBug
   *          do we want FogBugz to create a new bug report even if it is a duplicate?
   * @return true if submission was successful, false otherwise
   */
  public ScoutAnswer submitBug(String description, String extraInfo, String email, boolean isForceNewBug)
  {
    Map<String, String> params = new HashMap<String, String>();

    params.put("Description", description); //$NON-NLS-1$
    params.put("Extra", extraInfo); //$NON-NLS-1$
    params.put("Email", email); //$NON-NLS-1$
    params.put("ScoutUserName", getUserName()); //$NON-NLS-1$
    params.put("ScoutProject", getProject()); //$NON-NLS-1$
    params.put("ScoutArea", getArea()); //$NON-NLS-1$
    params.put("ScoutDefaultMessage", getDefaultMessage()); //$NON-NLS-1$
 
    if (isForceNewBug)
    {
      params.put("ForceNewBug", "1"); //$NON-NLS-1$ //$NON-NLS-2$
    } else {
      params.put("ForceNewBug", "0"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    SimpleHttpClient client = new SimpleHttpClient();
    
    String result = ""; //$NON-NLS-1$
    try {
      SimpleHttpClient.Response response = client.sendRequest( url, params );
      
      if (response.getStatusCode() == SimpleHttpClient.Response.Status.SC_OK) 
      {
        byte[] r = response.getResponseBody();
        result = new String(r, "utf-8"); //$NON-NLS-1$
//        System.out.println("RES="+result); //$NON-NLS-1$
      } else {
        return new ScoutAnswer(ScoutAnswer.ReturnCode.SYSERROR, 
            response.getStatusCode() + ": "+response.getErrorMessage()); //+ response.getStatusText()+", "+response.getStatusLine()); //$NON-NLS-1$
      }
    } catch (Exception e)
    {
      e.printStackTrace();
      return new ScoutAnswer(ScoutAnswer.ReturnCode.SYSERROR, e.getMessage());
    }
        
    return new ScoutAnswer(result);    
  }

  public String getArea()
  {
    return area;
  }

  public void setArea(String area)
  {
    this.area = area;
  }

  public String getDefaultMessage()
  {
    return defaultMessage;
  }

  public void setDefaultMessage(String defaultMessage)
  {
    this.defaultMessage = defaultMessage;
  }

  public String getProject()
  {
    return project;
  }

  public void setProject(String project)
  {
    this.project = project;
  }

  public String getUrl()
  {
    return url;
  }

  public void setUrl(String url)
  {
    this.url = url;
  }

  public String getUserName()
  {
    return userName;
  }

  public void setUserName(String userName)
  {
    this.userName = userName;
  }

  public static class ScoutAnswer {
    
    public enum ReturnCode { UNKNOWN, SUCCESS, ERROR, SYSERROR }
    
    ReturnCode returnCode;
    String     message;
    
    public ScoutAnswer(ReturnCode rc, String message)
    {
      this.returnCode = rc;
      this.message = message;
    }
    
    /**
     * Strip the answer from the xml response. This is a bit buggy but I want not to use an xml parser
     * for this bit as it would increase dependencies.
     * @param xmlAnswer the string received with the http response.
     */
    public ScoutAnswer(String xmlAnswer)
    {
      int sStart = xmlAnswer.indexOf("<Success>"); //$NON-NLS-1$
      int sEnd   = xmlAnswer.indexOf("</Success>"); //$NON-NLS-1$
      int fStart = xmlAnswer.indexOf("<Error>"); //$NON-NLS-1$
      int fEnd   = xmlAnswer.indexOf("</Error>"); //$NON-NLS-1$
      if ((sStart > -1) && (sEnd > -1))
      {
        returnCode = ScoutAnswer.ReturnCode.SUCCESS;
        message = xmlAnswer.substring(sStart+9, sEnd);
      } else {
        if ((fStart > -1) && (fEnd > -1))
        {
          returnCode = ScoutAnswer.ReturnCode.ERROR;
          message = xmlAnswer.substring(fStart+7, fEnd);
        } else {
          returnCode = ScoutAnswer.ReturnCode.UNKNOWN;
          message = xmlAnswer;
        }
      }
    }
    
    public String getMessage()
    {
      return message;
    }
    
    public ReturnCode getReturnCode()
    {
      return returnCode;
    }
    
  }
  
  public static void main(String[] args)
  {
//    System.out.println("FogBugz Test");
    BugzScoutCtl ctl = new BugzScoutCtl();
    ctl.setArea("BugzScout"); //$NON-NLS-1$
    ctl.setDefaultMessage("Vielen Dank!"); //$NON-NLS-1$
    ctl.setProject("Simidude"); //$NON-NLS-1$
    ctl.setUrl("https://agynamix.fogbugz.com/scoutSubmit.asp"); //$NON-NLS-1$
    ctl.setUserName("Torsten Uhlmann"); //$NON-NLS-1$
    ScoutAnswer answer = ctl.submitBug("Fehler 443", "extra Info", "tuhlmann@gmx.de", false); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
//    System.out.println("RC="+answer.getReturnCode()+", Message="+answer.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
  }

}
