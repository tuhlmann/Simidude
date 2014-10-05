package com.agynamix.platform.httpd;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.StringTokenizer;

import java.io.BufferedReader;

/**
 * Handles one session, i.e. parses the HTTP request
 * and returns the response.
 */
public abstract class HTTPSessionBase implements Runnable
{
  
  protected HTTPUtils  httpUtils;
  protected Socket     mySocket;
  
  protected Properties environment;

  protected abstract boolean postProcessRequestHeader(Properties environment, Properties header) throws InterruptedException, IOException;
  protected abstract HTTPResponse serve(String uri, String method, Properties header, Properties parms);
  
  public HTTPSessionBase( Properties environment, Socket s )
  {
    mySocket = s;
    this.environment = environment;
    httpUtils = new HTTPUtils(s);
    Thread t = new Thread( this );
    t.setDaemon( true );
    t.start();
  }

  public void run()
  {
    try
    {
      InputStream is = mySocket.getInputStream();
      if ( is == null) return;
      BufferedReader in = new BufferedReader( new InputStreamReader( is ));

      // Read the request line
      StringTokenizer st = new StringTokenizer( in.readLine());
      if ( !st.hasMoreTokens())
        httpUtils.sendError( HTTPUtils.HTTP_BADREQUEST, "BAD REQUEST: Syntax error. Usage: GET /example/file.html" );

      String method = st.nextToken();

      if ( !st.hasMoreTokens())
        httpUtils.sendError( HTTPUtils.HTTP_BADREQUEST, "BAD REQUEST: Missing URI. Usage: GET /example/file.html" );

      String uri = st.nextToken();

      // Decode parameters from the URI
      Properties parms = new Properties();
      int qmi = uri.indexOf( '?' );
      if ( qmi >= 0 )
      {
        decodeParms( uri.substring( qmi+1 ), parms );
        uri = decodePercent( uri.substring( 0, qmi ));
      }
      else uri = decodePercent(uri);


      // If there's another token, it's protocol version,
      // followed by HTTP headers. Ignore version but parse headers.
      // NOTE: this now forces header names uppercase since they are
      // case insensitive and vary by client.
      Properties header = new Properties();
      if ( st.hasMoreTokens())
      {
        String line = in.readLine();
        while ( line.trim().length() > 0 )
        {
          int p = line.indexOf( ':' );
          header.put( line.substring(0,p).trim().toLowerCase(), line.substring(p+1).trim());
          line = in.readLine();
        }
      }

      // If the method is POST, there may be parameters
      // in data section, too, read it:
      if ( method.equalsIgnoreCase( "POST" ))
      {
        long size = 0x7FFFFFFFFFFFFFFFl;
        String contentLength = header.getProperty("content-length");
        if (contentLength != null)
        {
          try { size = Integer.parseInt(contentLength); }
          catch (NumberFormatException ex) {}
        }
        String postLine = "";
        char buf[] = new char[512];
        int read = in.read(buf);
        while ( read >= 0 && size > 0 && !postLine.endsWith("\r\n") )
        {
          size -= read;
          postLine += String.valueOf(buf, 0, read);
          if ( size > 0 )
            read = in.read(buf);
        }
        postLine = postLine.trim();
        decodeParms( postLine, parms );
      }
      
      if (postProcessRequestHeader(environment, header))
      {      
        // Ok, now do the serve()
        HTTPResponse r = serve( uri, method, header, parms );
        if ( r == null )
        {
          httpUtils.sendError( HTTPUtils.HTTP_INTERNALERROR, "SERVER INTERNAL ERROR: Serve() returned a null response." );
        } else {
          httpUtils.sendResponse( r.status, r.mimeType, r.header, r.data );
        }
      }

      in.close();
    }
    catch ( IOException ioe )
    {
      try
      {
        httpUtils.sendError( HTTPUtils.HTTP_INTERNALERROR, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
      }
      catch ( Throwable t ) {}
    }
    catch ( InterruptedException ie )
    {
      // Thrown by sendError, ignore and exit the thread.
    }
  }

  /**
   * Decodes the percent encoding scheme. <br/>
   * For example: "an+example%20string" -> "an example string"
   */
  protected String decodePercent( String str ) throws InterruptedException
  {
    try
    {
      StringBuffer sb = new StringBuffer();
      for( int i=0; i<str.length(); i++ )
      {
          char c = str.charAt( i );
          switch ( c )
        {
              case '+':
                  sb.append( ' ' );
                  break;
              case '%':
                    sb.append((char)Integer.parseInt( str.substring(i+1,i+3), 16 ));
                  i += 2;
                  break;
              default:
                  sb.append( c );
                  break;
          }
      }
      return new String( sb.toString().getBytes());
    }
    catch( Exception e )
    {
      httpUtils.sendError( HTTPUtils.HTTP_BADREQUEST, "BAD REQUEST: Bad percent-encoding." );
      return null;
    }
  }

  /**
   * Decodes parameters in percent-encoded URI-format
   * ( e.g. "name=Jack%20Daniels&pass=Single%20Malt" ) and
   * adds them to given Properties.
   */
  protected void decodeParms( String parms, Properties p )
    throws InterruptedException
  {
    if ( parms == null )
      return;

    StringTokenizer st = new StringTokenizer( parms, "&" );
    while ( st.hasMoreTokens())
    {
      String e = st.nextToken();
      int sep = e.indexOf( '=' );
      if ( sep >= 0 )
        p.put( decodePercent( e.substring( 0, sep )).trim(),
             decodePercent( e.substring( sep+1 )));
    }
  }
  
  /**
   * URL-encodes everything between "/"-characters.
   * Encodes spaces as '%20' instead of '+'.
   */
  protected String encodeUri( String uri )
  {
    String newUri = "";
    StringTokenizer st = new StringTokenizer( uri, "/ ", true );
    while ( st.hasMoreTokens())
    {
      String tok = st.nextToken();
      if ( tok.equals( "/" ))
        newUri += "/";
      else if ( tok.equals( " " ))
        newUri += "%20";
      else
      {
        newUri += URLEncoder.encode( tok );
        // For Java 1.4 you'll want to use this instead:
        // try { newUri += URLEncoder.encode( tok, "UTF-8" ); } catch ( UnsupportedEncodingException uee )
      }
    }
    return newUri;
  }

}
