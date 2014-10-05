package com.agynamix.platform.httpd;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * HTTP response.
 * Return one of these from serve().
 */
public class HTTPResponse
{
  /**
   * Default constructor: response = HTTP_OK, data = mime = 'null'
   */
  public HTTPResponse()
  {
    this.status = HTTPUtils.HTTP_OK;
  }

  /**
   * Basic constructor.
   */
  public HTTPResponse( String status, String mimeType, InputStream data )
  {
    this.status = status;
    this.mimeType = mimeType;
    this.data = data;
  }

  /**
   * Convenience method that makes an InputStream out of
   * given text.
   */
  public HTTPResponse( String status, String mimeType, String txt )
  {
    this.status = status;
    this.mimeType = mimeType;
    this.data = new ByteArrayInputStream( txt.getBytes());
  }

  /**
   * Convenience method that makes an InputStream out of
   * given byte array.
   */
  public HTTPResponse( String status, String mimeType, byte[] buffer )
  {
    this.status = status;
    this.mimeType = mimeType;
    this.data = new ByteArrayInputStream( buffer);
  }
  
  /**
   * Adds given line to the header.
   */
  public void addHeader( String name, String value )
  {
    header.put( name, value );
  }

  /**
   * HTTP status code after processing, e.g. "200 OK", HTTP_OK
   */
  public String status;

  /**
   * MIME type of content, e.g. "text/html"
   */
  public String mimeType;

  /**
   * Data of the response, may be null.
   */
  public InputStream data;

  /**
   * Headers for the HTTP response. Use addHeader()
   * to add lines.
   */
  public Properties header = new Properties();
}
