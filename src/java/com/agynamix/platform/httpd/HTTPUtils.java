package com.agynamix.platform.httpd;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TimeZone;

public class HTTPUtils {
  
  public final static String HTTP_PORT       = "port";
  public final static String HTTP_USER       = "user";
  public final static String HTTP_PASSWORD   = "password";

  /**
   * Some HTTP response status codes
   */
  public static final String HTTP_OK             = "200 OK";
  public static final String HTTP_REDIRECT       = "301 Moved Permanently";
  public static final String HTTP_UNAUTHORIZED   = "401 Unauthorized";
  public static final String HTTP_FORBIDDEN      = "403 Forbidden";
  public static final String HTTP_NOTFOUND       = "404 Not Found";
  public static final String HTTP_BADREQUEST     = "400 Bad Request";
  public static final String HTTP_INTERNALERROR  = "500 Internal Server Error";
  public static final String HTTP_NOTIMPLEMENTED = "501 Not Implemented";

  /**
   * Common mime types for dynamic content
   */
  public static final String MIME_PLAINTEXT      = "text/plain";
  public static final String MIME_HTML           = "text/html";
  public static final String MIME_DEFAULT_BINARY = "application/octet-stream";

  /**
   * GMT date formatter
   */
  private static java.text.SimpleDateFormat gmtFrmt;
  
  private Socket mySocket;
  
  /**
   * Hashtable mapping (String)FILENAME_EXTENSION -> (String)MIME_TYPE
   */
  public static Hashtable theMimeTypes = new Hashtable();
  
  static
  {
    gmtFrmt = new java.text.SimpleDateFormat("E, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
    gmtFrmt.setTimeZone(TimeZone.getTimeZone("GMT"));
    
    StringTokenizer st = new StringTokenizer(
        "htm    text/html "+
        "html   text/html "+
        "txt    text/plain "+
        "asc    text/plain "+
        "gif    image/gif "+
        "jpg    image/jpeg "+
        "jpeg   image/jpeg "+
        "png    image/png "+
        "mp3    audio/mpeg "+
        "m3u    audio/mpeg-url " +
        "pdf    application/pdf "+
        "doc    application/msword "+
        "ogg    application/x-ogg "+
        "zip    application/octet-stream "+
        "exe    application/octet-stream "+
        "class    application/octet-stream " );
      while ( st.hasMoreTokens())
      {
        theMimeTypes.put( st.nextToken(), st.nextToken());
      }

  }

  public HTTPUtils(Socket s)
  {
    mySocket = s;
  }

  /**
   * Returns an error message as a HTTP response and throws InterruptedException
   * to stop further request processing.
   */
  public void sendError(String status, String msg) throws InterruptedException
  {
    sendResponse(status, HTTPUtils.MIME_PLAINTEXT, null, new ByteArrayInputStream(msg.getBytes()));
    throw new InterruptedException();
  }
  
  public void sendNotAuthorized(String realm)
  {
    Properties p = new Properties();
    p.setProperty("WWW-Authenticate", "Basic realm=\""+realm+"\"");
    sendResponse(HTTPUtils.HTTP_UNAUTHORIZED, HTTPUtils.MIME_PLAINTEXT, p, new ByteArrayInputStream("Not Authorized".getBytes()));    
  }

  /**
   * Sends given response to the socket.
   */
  public void sendResponse(String status, String mime, Properties header, InputStream data)
  {
    try
    {
      if (status == null)
        throw new Error("sendResponse(): Status can't be null.");

      OutputStream out = mySocket.getOutputStream();
      PrintWriter pw = new PrintWriter(out);
      pw.print("HTTP/1.0 " + status + " \r\n");

      if (mime != null)
        pw.print("Content-Type: " + mime + "\r\n");

      if (header == null || header.getProperty("Date") == null)
        pw.print("Date: " + gmtFrmt.format(new Date()) + "\r\n");

      if (header != null)
      {
        Enumeration e = header.keys();
        while (e.hasMoreElements())
        {
          String key = (String) e.nextElement();
          String value = header.getProperty(key);
          pw.print(key + ": " + value + "\r\n");
        }
      }

      pw.print("\r\n");
      pw.flush();

      if (data != null)
      {
        byte[] buff = new byte[2048];
        while (true)
        {
          int read = data.read(buff, 0, 2048);
          if (read <= 0)
            break;
          out.write(buff, 0, read);
        }
      }
      out.flush();
      out.close();
      if (data != null)
        data.close();
    } catch (IOException ioe)
    {
      // Couldn't write? No can do.
      try
      {
        mySocket.close();
      } catch (Throwable t)
      {
      }
    }
  }

}
