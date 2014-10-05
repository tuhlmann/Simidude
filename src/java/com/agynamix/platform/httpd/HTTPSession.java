package com.agynamix.platform.httpd;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.agynamix.platform.frontend.preferences.IPreferenceConstants;
import com.agynamix.platform.infra.ApplicationBase;
import com.agynamix.platform.infra.FileUtils;
import com.agynamix.platform.infra.HtmlUtils;
import com.agynamix.platform.infra.ZipUtils;
import com.agynamix.platform.log.ApplicationLog;
import com.agynamix.simidude.clipboard.IClipboardItem;
import com.agynamix.simidude.clipboard.SourceDataManager;
import com.agynamix.simidude.impl.SimidudeApplicationContext;
import com.agynamix.simidude.infra.CacheManagerFactory;
import com.agynamix.simidude.infra.ModelProvider;
import com.agynamix.simidude.source.SourceDataStub;
import com.agynamix.simidude.source.impl.FileSourceData;
import com.agynamix.simidude.source.impl.ImageSourceData;
import com.agynamix.simidude.source.impl.TextSourceData;

public class HTTPSession extends HTTPSessionBase {
  
  Logger log = ApplicationLog.getLogger(HTTPSession.class);
  
  public final static String ctrl_none = "none";
  public final static String ctrl_list = "list";
  public final static String ctrl_download = "download";
  public final static String ctrl_compress = "compress";
  
  public final static int Thumbnail_Width = 100;
  
  private static String cssContents = null;
  private static Object cssLoadMutex = new Object();
  
  public HTTPSession(Properties environment, Socket s)
  {
    super(environment, s);
  }

  @Override
  protected boolean postProcessRequestHeader(Properties environment, Properties header) throws InterruptedException,
      IOException
  {
    String envUser = environment.getProperty(HTTPUtils.HTTP_USER, "guest");
    String envPw = environment.getProperty(HTTPUtils.HTTP_PASSWORD, "guest");

    String value = header.getProperty("authorization");
    if (value != null)
    {
      StringTokenizer st = new StringTokenizer(value);
      String method = st.nextToken();
      if (method.equalsIgnoreCase("basic"))
      {
        String auth = st.nextToken();
        if ((auth != null) && (auth.length() > 0))
        {
          String plain = new String(new BASE64Decoder().decodeBuffer(auth));
          int pos = plain.indexOf(':');
          if (pos > 0)
          {
            String user = plain.substring(0, pos);
            String pw = plain.substring(pos + 1);
            // Connect to real group name and password
            if ((envUser.equals(user)) && (envPw.equals(pw)))
            {
              return true;
            }
          }
        }
      }
    }
    httpUtils.sendNotAuthorized("Simidude");
    return false;
  }

  @Override
  /*
   * Override this to customize the server.<p>
   * 
   * (By default, this delegates to serveFile() and allows directory listing.)
   * 
   * @parm uri Percent-decoded URI without parameters, for example "/index.cgi"
   * 
   * @parm method "GET", "POST" etc.
   * 
   * @parm parms Parsed, percent decoded parameters from URI and, in case of
   * POST, data.
   * 
   * @parm header Header entries, percent decoded
   * 
   * @return HTTP response, see class Response for details
   */
  public HTTPResponse serve(String uri, String method, Properties header, Properties parms)
  {
//    System.out.println(method + " '" + uri + "' ");

    Enumeration e = header.propertyNames();
    while (e.hasMoreElements())
    {
      String value = (String) e.nextElement();
//      System.out.println("  HDR: '" + value + "' = '" + header.getProperty(value) + "'");
    }
    e = parms.propertyNames();
    while (e.hasMoreElements())
    {
      String value = (String) e.nextElement();
//      System.out.println("  PRM: '" + value + "' = '" + parms.getProperty(value) + "'");
    }

    // Remove URL arguments
    uri = uri.trim().replace(File.separatorChar, '/');
    if (uri.indexOf('?') >= 0)
    {
      uri = uri.substring(0, uri.indexOf('?'));
    }

    // Prohibit getting out of current directory
    if (uri.startsWith("..") || uri.endsWith("..") || uri.indexOf("../") >= 0)
    {
      return new HTTPResponse(HTTPUtils.HTTP_FORBIDDEN, HTTPUtils.MIME_PLAINTEXT, "FORBIDDEN: Won't serve ../ for security reasons.");
    }

    if (log != null) // Strange bug on Ubuntu 8.04, Sun Java 1.6 throws NPE here
    {
      log.fine("URI=|"+uri+"|");
    }

    if (uri.equals("") || (uri.equals("/")))
    {
      return serveAllEntries(header);
    } else {      
      String controller = getController(uri);
      if (controller.equals(ctrl_none))
      {
        return serveSpecialEntry(uri, header);
      } else if (controller.equals(ctrl_list))
      {
        return serveEntry(uri, header);
      } else if (controller.equals(ctrl_download)) {
        return serveDownloadEntry(uri, header, false);      
      } else if (controller.equals(ctrl_compress)) {
        return serveDownloadEntry(uri, header, true);      
      } else {
        return new HTTPResponse(HTTPUtils.HTTP_FORBIDDEN, HTTPUtils.MIME_PLAINTEXT, "FORBIDDEN: Action "+controller+" not allowed.");
      }
    }
    
  }

  private HTTPResponse serveAllEntries(Properties header)
  {
    SourceDataManager sourceDataManager = ((SimidudeApplicationContext)ApplicationBase.getContext()).getSourceDataManager();

    List<IClipboardItem> items = sourceDataManager.getClipboardItems();
    if (items == null)
    {
      items = new ArrayList<IClipboardItem>();
    }
    StringBuilder sb = new StringBuilder(htmlHeader("list_all"));
    sb.append("<h1>");
    sb.append(items.size()).append(" Simidude ");
    if (items.size() == 1)
    {
      sb.append("Entry ");
    } else {
      sb.append("Entries ");      
    }
    sb.append("found.</h1><p>");
    sb.append("<center><ul>");
    
    int count = 0;
    
    for (IClipboardItem item : items)
    {
      String rowClass = "even";
      if (count%2 != 0)
      {
        rowClass = "odd";
      }
      String tableClass = "table_"+rowClass;
      sb.append("<li class=\"li_"+rowClass+"\">");        
      String fragment = "";
      switch (item.getType())
      {
      case TEXT:
        fragment = serveListingText(header, item, tableClass, false);
        break;
      case IMAGE:
        fragment = serveListingImage(header, item, tableClass, false);
        break;
      case FILE:
        fragment = serveListingFile(header, item, tableClass, false);
        break;
      }
      
      sb.append(fragment);
      sb.append("</li>");
      
//      ISourceData sourceData = item.getSourceData();      
//      sb.append("<strong><a href=\"/" + sourceData.getSourceId() + "\">"+sourceData.getSourceId()+"</a></strong><br/>");
//      sb.append("<em>"+item.getShortDescription()+"</em>");
//      sb.append("</li>");
      
      count++;
    }
       
    sb.append("</ul></center></p></body></html>");
    return new HTTPResponse(HTTPUtils.HTTP_OK, HTTPUtils.MIME_HTML, sb.toString());
  }

  private String htmlHeader(String bodyClass)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("<html>");
    sb.append("<head>");
    sb.append("<title>Simidude Remote Access</title>");
    
    // read (cached) CSS and fill in
    sb.append("<style type=\"text/css\">");
    sb.append("<!--");
    
    sb.append(loadCssFile(IPreferenceConstants.CSS_FILE_PATH));
    
    sb.append("-->");
    sb.append("</style>");
    
    sb.append("</head>");
    sb.append("<body class=\""+bodyClass+"\">");
    return sb.toString();
  }

  private String loadCssFile(String cssFilePath)
  {
    synchronized (cssLoadMutex)
    {
      if (cssContents == null)
      {
        byte[] buffer = FileUtils.loadFile(this.getClass().getClassLoader().getResourceAsStream(cssFilePath), cssFilePath);
        cssContents = new String(buffer);        
      }
    }
    return cssContents;
  }

  /**
   * To serve an entry the uuid of that entry must be given after an initial /.
   * @param uri
   * @param header
   * @return
   */
  private HTTPResponse serveEntry(String uri, Properties header)
  {
    String uuidStr = getUUID(uri);
    
    try {
      UUID uuid = UUID.fromString(uuidStr);
      SourceDataManager sdm = ((SimidudeApplicationContext)ApplicationBase.getContext()).getSourceDataManager();
      IClipboardItem item = sdm.getClipboardItem(new SourceDataStub(uuid));
      if (item != null)
      {
        String fragment = "";
        switch (item.getType())
        {
        case TEXT:
          fragment = serveListingText(header, item, "table_single", true);
          break;
        case FILE:
          fragment = serveListingFile(header, item, "table_single", true);
          break;
        case IMAGE:
          fragment = serveListingImage(header, item, "table_single", true);    
          break;
        default:
          return new HTTPResponse(HTTPUtils.HTTP_FORBIDDEN, HTTPUtils.MIME_PLAINTEXT, "This Entry type is not implemented yet.");        
        }
        return new HTTPResponse(HTTPUtils.HTTP_OK, HTTPUtils.MIME_HTML, buildSingleListing(fragment));
      } else {
        return new HTTPResponse(HTTPUtils.HTTP_FORBIDDEN, HTTPUtils.MIME_PLAINTEXT, "Error: The specified item could not be found. Maybe it was deleted?");        
      }
      
    } catch (Exception e) {
      e.printStackTrace();
      return new HTTPResponse(HTTPUtils.HTTP_INTERNALERROR, HTTPUtils.MIME_PLAINTEXT, "INTERNAL ERRROR: "+uuidStr+" is not a valid identifier.");
    }
    
  }
  
  /**
   * To serve an entry the uuid of that entry must be given after an initial /.
   * @param uri
   * @param header
   * @return
   */
  private HTTPResponse serveDownloadEntry(String uri, Properties header, boolean compress)
  {
    String uuidStr = getUUID(uri);
    
    try {
      UUID uuid = UUID.fromString(uuidStr);
      SourceDataManager sdm = ((SimidudeApplicationContext)ApplicationBase.getContext()).getSourceDataManager();
      IClipboardItem item = sdm.getClipboardItem(new SourceDataStub(uuid));
      if (item != null)
      {
        switch (item.getType())
        {
        case TEXT:
          return serveDownloadText(header, item, compress);
        case IMAGE:
          return serveDownloadImage(header, item, compress);          
        case FILE:
          return serveDownloadFile(header, item, compress);
        default:
          return new HTTPResponse(HTTPUtils.HTTP_FORBIDDEN, HTTPUtils.MIME_PLAINTEXT, "Not yet.");        
        }
      } else {
        return new HTTPResponse(HTTPUtils.HTTP_FORBIDDEN, HTTPUtils.MIME_PLAINTEXT, "Error: The specified item could not be found. Maybe it was deleted?");        
      }
      
    } catch (Exception e) {
      e.printStackTrace();
      return new HTTPResponse(HTTPUtils.HTTP_INTERNALERROR, HTTPUtils.MIME_PLAINTEXT, "INTERNAL ERRROR: "+uuidStr+" is not a valid identifier.");
    }
    
  }
    
  private String buildSingleListing(String fragment)
  {
    StringBuilder sb = new StringBuilder();
    sb.append(htmlHeader("list_single"));
    sb.append("<h1>");
    sb.append("Simidude Entry listing.</h1>");
    sb.append("<h2><a href=\"/\">Back</a></h2>");
    sb.append("<center><p>");
    sb.append(fragment);
    sb.append("</center></p></body></html>");
    return sb.toString();
  }

  private String getUUID(String uri)
  {
    String neoUri = uri;
    String uuidStr = "";
    if (uri.lastIndexOf("/") == uri.length()-1)
    {
      neoUri = uri.substring(0, uri.length()-1);
    }
    
    int pos = neoUri.lastIndexOf("/");
    if (pos > -1)
    {
      uuidStr = neoUri.substring(pos+1);
//      System.out.println("UUID="+uuidStr);      
    }
    return uuidStr;    
  }

  private String getController(String uri)
  {
    String controller = "none";
    int pos1 = uri.indexOf("/");
    if (pos1 > -1)
    {
      int pos2 = uri.indexOf("/", pos1+1);
      if (pos2 > -1)
      {
//        System.out.println("uri="+uri+", pos1="+pos1+", pos2="+pos2);
        controller = uri.substring(pos1+1, pos2);
      }
    }
    return controller;    
  }
  
  private String serveListingText(Properties header, IClipboardItem item, String tableClass, boolean singleListing)
  {
    StringBuilder sb = new StringBuilder();
    if (singleListing)
    {
      sb.append("<div class=\"text_box_single\">");
    } else {
      sb.append("<div class=\"text_box_list\">");      
    }
    
    TextSourceData tsd = (TextSourceData) item.getSourceData();
    
    sb.append("<table class=\""+tableClass+"\"><tr><th width=\""+Thumbnail_Width+"\">");
    sb.append("<a href=\"/"+ctrl_list+"/" + tsd.getSourceId() + "\">");
    sb.append(embeddImage(item.getImage().getImageData(), "image_thumb", "Thumbnail"));
    sb.append("</a>");
    sb.append("</th><th colspan=\"2\">");    
    sb.append("<strong><a href=\"/"+ctrl_list+"/" + tsd.getSourceId() + "\">Clipboard Text"+"</a></strong>");
    sb.append("</th></tr>");
    if (singleListing)
    {
      sb.append("<tr>");
      sb.append("<td>&nbsp;</td>");
      sb.append("<td><a href=\"/"+ctrl_download+"/" + tsd.getSourceId() + "\">Download Text"+"</a></td>");
      sb.append("<td><a href=\"/"+ctrl_compress+"/" + tsd.getSourceId() + "\">Download Compressed"+"</a></td>");
      sb.append("</tr>");
      sb.append("<tr><td colspan=\"3\">");
      sb.append("<pre>");
      sb.append(HtmlUtils.escapeHtmlFull(tsd.getText()));      
    } else {
      sb.append("<tr>");
      sb.append("<td>&nbsp;</td>");
      sb.append("<td colspan=\"2\">");
      sb.append("<pre>");
      sb.append(HtmlUtils.escapeHtmlFull(item.getShortDescription()));      
    }
    sb.append("</pre>");
    sb.append("</td></tr>");
    sb.append("<tr>");
    sb.append("<td>&nbsp;</td>");
    sb.append("<td><a href=\"/"+ctrl_download+"/" + tsd.getSourceId() + "\">Download Text"+"</a></td>");
    sb.append("<td><a href=\"/"+ctrl_compress+"/" + tsd.getSourceId() + "\">Download Compressed"+"</a></td>");
    sb.append("</tr>");
    sb.append("</table>");
    sb.append("</div>");

    return sb.toString();
  }

  private String serveListingFile(Properties header, IClipboardItem item, String tableClass, boolean singleListing)
  {
    StringBuilder sb = new StringBuilder();
    if (singleListing)
    {
      sb.append("<div class=\"file_box_single\">");
    } else {
      sb.append("<div class=\"file_box_list\">");      
    }
    
    FileSourceData fsd = (FileSourceData) item.getSourceData();
    
    sb.append("<table class=\""+tableClass+"\"><tr><th width=\""+Thumbnail_Width+"\">");
    sb.append("<a href=\"/"+ctrl_list+"/" + fsd.getSourceId() + "\">");
    sb.append(embeddImage(item.getImage().getImageData(), "image_thumb", "Thumbnail"));
    sb.append("</a>");
    sb.append("</th><th colspan=\"2\">");
    sb.append("<strong><a href=\"/"+ctrl_list+"/" + fsd.getSourceId() + "\">"+fsd.getFilename()+"</a></strong>");
    sb.append("</th></tr>");
    sb.append("<tr>");
    sb.append("<td>&nbsp;</td>");
    sb.append("<td><a href=\"/"+ctrl_download+"/" + fsd.getSourceId() + "\">Download"+"</a></td>");
    sb.append("<td><a href=\"/"+ctrl_compress+"/" + fsd.getSourceId() + "\">Download Compressed"+"</a></td>");
    sb.append("</tr>");
    sb.append("</table>");
    sb.append("</div>");
    
    return sb.toString();
  }
  
  private String serveListingImage(Properties header, IClipboardItem item, String tableClass, boolean singleListing)
  {
    StringBuilder sb = new StringBuilder();
    if (singleListing)
    {
      sb.append("<div class=\"image_box_single\">");
    } else {
      sb.append("<div class=\"image_box_list\">");      
    }
    
    ImageSourceData isd = (ImageSourceData) item.getSourceData();
    
    sb.append("<table class=\""+tableClass+"\"><tr><th width=\""+Thumbnail_Width+"\">");
    sb.append("<a href=\"/"+ctrl_list+"/" + isd.getSourceId() + "\">");
    sb.append(embeddImage(item.getImage().getImageData(), "image_thumb", "Thumbnail"));
    sb.append("</a>");
    sb.append("</th><th colspan=\"2\">");    
    sb.append("<strong><a href=\"/"+ctrl_list+"/" + isd.getSourceId() + "\">Clipboard Image"+"</a></strong>");
    sb.append("</th></tr>");
    if (singleListing)
    {
      sb.append("<tr>");
      sb.append("<td>&nbsp;</td>");
      sb.append("<td><a href=\"/"+ctrl_download+"/" + isd.getSourceId() + "\">Download Image as JPG"+"</a></td>");
      sb.append("<td><a href=\"/"+ctrl_compress+"/" + isd.getSourceId() + "\">Download Compressed"+"</a></td>");
      sb.append("</tr>");
      sb.append("<tr><td colspan=\"3\">");
      sb.append("<a href=\"/"+ctrl_download+"/" + isd.getSourceId() + "\">");
      sb.append(embeddImage(isd.getImageData(), "image", "Full Image"));
      sb.append("</a>");
    }
    sb.append("</td></tr>");
    sb.append("<tr>");
    sb.append("<td>&nbsp;</td>");
    sb.append("<td colspan=\"2\">Dimension:&nbsp;");
    ImageData imgData = isd.getImageData();
    String dimStr = "?";
    if (imgData != null)
    {
      dimStr = ""+imgData.width + " x "+imgData.height + " pixel";
    }
    sb.append(dimStr).append("</td></tr>");
    sb.append("<tr>");
    sb.append("<td>&nbsp;</td>");
    sb.append("<td><a href=\"/"+ctrl_download+"/" + isd.getSourceId() + "\">Download Image as JPG"+"</a></td>");
    sb.append("<td><a href=\"/"+ctrl_compress+"/" + isd.getSourceId() + "\">Download Compressed"+"</a></td>");
    sb.append("</tr>");
    sb.append("</table>");
    sb.append("</div>");

    return sb.toString();
  }
  
  private HTTPResponse serveDownloadFile(Properties header, IClipboardItem item, boolean compress)
  {
    FileSourceData fsd = (FileSourceData) item.getSourceData();
    
    if (fsd.isDirectory())
    {
      compress = true;  
    }
    
    // Download from a remote Simidude if necessary
    SourceDataManager sourceDataManager = ((SimidudeApplicationContext)ApplicationBase.getContext()).getSourceDataManager();

    if ((sourceDataManager.isRetrieveContentsNeeded(item)) && (!sourceDataManager.isDownloadInProgress(item.getSourceData())))
    {
      ModelProvider mp = ((SimidudeApplicationContext)ApplicationBase.getContext()).getModelProvider();
      mp.retrieveContentsForProxyObject(item.getSourceData());
      // Wait until data arrived
      try
      {
        Thread.sleep(200);
        while ((sourceDataManager.isRetrieveContentsNeeded(item)) && (sourceDataManager.isDownloadInProgress(item.getSourceData())))
        {
          Thread.sleep(100);
        }
      } catch (InterruptedException e)
      {
        e.printStackTrace();
      }
      if (sourceDataManager.isRetrieveContentsNeeded(item))
      {
        return new HTTPResponse(HTTPUtils.HTTP_FORBIDDEN, HTTPUtils.MIME_PLAINTEXT, "A problem occured downloading "+fsd.getFilename()+" from a remote Simidude.");  
      }    
    }
    
    File f = new File(fsd.getLocalFilename());
    String filename = f.getName();
    
    try
    {
      if (compress)
      {
        filename = FileUtils.replaceLastExtension(f.getName(), ".zip");
        f = ZipUtils.zipFile(f, CacheManagerFactory.createTempFile(".zip"));
      }
      
      // Get MIME type from file name extension, if possible
      String mime = null;
      int dot = f.getCanonicalPath().lastIndexOf('.');
      if (dot >= 0)
      {
        mime = (String) httpUtils.theMimeTypes.get(f.getCanonicalPath().substring(dot + 1).toLowerCase());
      }
      if (mime == null)
      {
        mime = HTTPUtils.MIME_DEFAULT_BINARY;
      }

      // Support (simple) skipping:
      long startFrom = 0;
      String range = header.getProperty("Range");
      if (range != null)
      {
        if (range.startsWith("bytes="))
        {
          range = range.substring("bytes=".length());
          int minus = range.indexOf('-');
          if (minus > 0)
            range = range.substring(0, minus);
          try
          {
            startFrom = Long.parseLong(range);
          } catch (NumberFormatException nfe)
          {
          }
        }
      }

      FileInputStream fis = new FileInputStream(f);
      fis.skip(startFrom);
      HTTPResponse r = new HTTPResponse(HTTPUtils.HTTP_OK, mime, fis);
      r.addHeader("Content-Disposition", "attachment; filename=\""+filename+"\"");
      r.addHeader("Content-length", "" + (f.length() - startFrom));
      r.addHeader("Content-range", "" + startFrom + "-" + (f.length() - 1) + "/" + f.length());
      return r;
    } catch (IOException ioe)
    {
      return new HTTPResponse(HTTPUtils.HTTP_FORBIDDEN, HTTPUtils.MIME_PLAINTEXT, "FORBIDDEN: Reading file failed.");
    }
  }
  
  private HTTPResponse serveDownloadText(Properties header, IClipboardItem item, boolean compress)
  {
    TextSourceData tsd = (TextSourceData) item.getSourceData();
    
    String text = tsd.getText();
    String filename;
    byte[] buffer;
    
    if (compress)
    {
      buffer = ZipUtils.zipBuffer("ClipboardText.txt", text.getBytes());
      filename = "ClipboardText.zip";
    } else {
      buffer = text.getBytes();
      filename = "ClipboardText.txt";
    }
       
    return serveDownloadBuffer(header, buffer, filename);    
  }  

  private HTTPResponse serveDownloadImage(Properties header, IClipboardItem item, boolean compress)
  {
    ImageSourceData isd = (ImageSourceData) item.getSourceData();

    byte[] buf = FileUtils.readImage(isd.getImageData(), SWT.IMAGE_JPEG);
    String filename = "ClipboardImage.jpg";

    if (compress)
    {
      buf = ZipUtils.zipBuffer("ClipboardImage.jpg", buf);
      filename = "ClipboardImage.zip";
    }
    
    return serveDownloadBuffer(header, buf, filename);    
  }  
  
  private HTTPResponse serveDownloadBuffer(Properties header, byte[] buffer, String filename)
  {
    HTTPResponse r = new HTTPResponse(HTTPUtils.HTTP_OK, HTTPUtils.MIME_DEFAULT_BINARY, buffer);
    r.addHeader("Content-Disposition", "attachment; filename=\""+filename+"\"");
    r.addHeader("Content-length", "" + buffer.length);
    r.addHeader("Content-range", "" + 0 + "-" + (buffer.length - 1) + "/" + buffer.length);
    return r;        
  }
  
  private HTTPResponse serveSpecialEntry(String uri, Properties header)
  {
    if (uri.equalsIgnoreCase("/favicon.ico"))
    {
      byte[] buffer = FileUtils.loadFile(this.getClass().getClassLoader().getResourceAsStream(IPreferenceConstants.FAVICON_PATH), IPreferenceConstants.FAVICON_PATH);
      return serveDownloadBuffer(header, buffer, "favicon.ico");
    } else {
      return new HTTPResponse(HTTPUtils.HTTP_FORBIDDEN, HTTPUtils.MIME_PLAINTEXT, "FORBIDDEN: Unknown URI: "+uri);
    }
  }
  
  private String embeddImage(ImageData imgData, String cssClass, String altText)
  {
    BASE64Encoder encoder = new BASE64Encoder();
    String str = encoder.encode(FileUtils.readImage(imgData, SWT.IMAGE_JPEG));   
    String result = "<img class=\""+cssClass+"\" src=\"data: image/jpg;base64, "+str+"\" alt=\""+altText+"\"/>";
    return result;
  }
  
  

  
}