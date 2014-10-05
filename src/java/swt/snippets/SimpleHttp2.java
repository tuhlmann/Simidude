package swt.snippets;

/*------------------------------------------------------------------------
 SimpleHttpd2 ist ein Java-Server-Programm das einen sehr 
 kleinen Teil des http-Protokolls implementiert.

 Der Server implementiert die GET Methode ohne Auswertung von weiteren http-
 Header-Informationen. Er implementiert also einen "http-Fileserver"

 Der Quellcode zu diesem Programm ist bis auf unwesentliche Modifikationen
 dem Buch "JAVA Server und Servlets" von Peter Roßbach und 
 Hendrik Schreiber, Addison-Wesley 1999, Seite 24, ISBN: 3-8273-1408-9 entnommen.

 Aufruf des Programms 

 java SimpleHttpd2 doc-root port-number

 ------------------------------------------------------------------------*/

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;

public class SimpleHttp2 extends Thread {

  protected Socket            s              = null;
  protected static File       docRoot;
  protected static String     canonicalDocRoot;
  protected static int        port;

  public final static String  CRLF           = "\r\n";
  public final static String  PROTOCOL       = "HTTP/1.0 ";

  public final static String  SC_OK          = "200 OK";
  public final static String  SC_BAD_REQUEST = "400 Bad Request";
  public final static String  SC_FORBIDDEN   = "403 Forbidden";
  public final static String  SC_NOT_FOUND   = "404 Not Found";

  protected static Properties typeMap        = new Properties();
  protected String            statusCode     = SC_OK;
  protected Hashtable         myHeaders      = new Hashtable();

  public static void main(String args[])
  {

    try
    {
      if (args.length != 2)
      {
        throw new IllegalArgumentException("usage: java SimpleHttp2  ");
      }

      // typeMap.load(new FileInputStream("Mime.types"));
      docRoot = new File(args[0]);
      port = Integer.parseInt(args[1]);
      canonicalDocRoot = docRoot.getCanonicalPath();
      ServerSocket listen = new ServerSocket(port);

      System.out.println("http server started on: " + (InetAddress.getLocalHost()).getHostAddress());
      System.out.println("listening on port: " + listen.getLocalPort());
      System.out.println("document root is: " + canonicalDocRoot);

      while (true)
      {
        SimpleHttp2 aRequest = new SimpleHttp2(listen.accept());
      }
    } catch (Exception e)
    {
      System.err.println("Exception: " + e.toString());
    }
  }

  public SimpleHttp2(Socket s)
  {
    this.s = s;
    start();
  }

  public void run()
  {

    try
    {
      setHeader("Server", "Simidude");
      BufferedReader is = new BufferedReader(new InputStreamReader(s.getInputStream()));
      DataOutputStream os = new DataOutputStream(s.getOutputStream());
      String request = is.readLine();

      System.out.println(s.getInetAddress().getHostAddress() + " " + request);

      StringTokenizer st = new StringTokenizer(request);

      if ((st.countTokens() == 3) && st.nextToken().equals("GET"))
      {

        String filename = docRoot.getPath() + st.nextToken();

        // if ( filename.endsWith("/") || filename.equals("") ) {
        // filename += "index.html";
        // }

        File file = new File(filename);
        if (file.getCanonicalPath().startsWith(canonicalDocRoot))
        {
          if (file.isDirectory())
          {
            sendListing(os, file);            
          } else {
            sendDocument(os, file);            
          }
        } else
        {
          sendError(SC_FORBIDDEN, os);
        }
      } else
      {
        sendError(SC_BAD_REQUEST, os);
      }

      is.close();
      os.close();
      s.close();
    } catch (IOException e)
    {
      System.err.println("Exception: " + e.toString());
    }
  }

  protected void sendDocument(DataOutputStream os, File file) throws IOException
  {
    try
    {
      BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
      sendStatusLine(os);
      setHeader("Content-Length", (new Long(file.length())).toString());
      setHeader("Content-Type", guessType(file.getPath()));
      sendHeader(os);
      os.writeBytes(CRLF);

      byte[] buf = new byte[1024];
      int len;
      while ((len = in.read(buf, 0, 1024)) != -1)
      {
        os.write(buf, 0, len);
      }
      in.close();
    } catch (FileNotFoundException e)
    {
      sendError(SC_NOT_FOUND, os);
    }
  }

  protected void sendListing(DataOutputStream os, File file) throws IOException
  {
    try
    {
      sendStatusLine(os);
      setHeader("Content-Type", "text/html");
      sendHeader(os);
      os.writeBytes(CRLF);

      String s = "<head><title>Simidude Entries</title></head><body><h1>Simidude Entries</h1></body>";
      
      os.write(s.getBytes());
    } catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  protected void setStatusCode(String statusCode)
  {
    this.statusCode = statusCode;
  }

  protected String getStatusCode()
  {
    return statusCode;
  }

  /**
   * die Methode sendet eine Statuszeile an den Client.
   */
  protected void sendStatusLine(DataOutputStream out) throws IOException
  {
    out.writeBytes(PROTOCOL + getStatusCode() + CRLF);
  }

  /**
   * die Methode fügt der Header-Hashtable ein Name-Value-Paar hinzu.
   */
  protected void setHeader(String key, String value)
  {
    myHeaders.put(key, value);
  }

  /**
   * die Methode sendet die http-Header-Informationen zum Client. Die
   * Header-Informationen sind in einer Hashtable abgelegt.
   */
  protected void sendHeader(DataOutputStream out) throws IOException
  {
    String line;
    String key;
    Enumeration e = myHeaders.keys();
    while (e.hasMoreElements())
    {
      key = (String) e.nextElement();
      out.writeBytes(key + ": " + myHeaders.get(key) + CRLF);
    }
  }

  /**
   * die Methode sendet eine Fehlerseite mit Status-Code an den Client. Die
   * Fehlerseite besteht aus einer Statuszeile und einer HTML-Seite die durch
   * CRLF von der Statuszeile getrennt ist.
   */
  protected void sendError(String statusCode, DataOutputStream out) throws IOException
  {
    setStatusCode(statusCode);
    sendStatusLine(out);
    out.writeBytes(CRLF + "<html>" + "<head><title>" + getStatusCode() + "</title></head>" + "<body><hl>"
        + getStatusCode() + "</hl></body>" + "</html>");
    System.err.println(getStatusCode());
  }

  /**
   * die Methode bestimmt aus der Dateiendung den mimetype einer Datei.
   * 
   * Dazu vergleicht sie die Dateiendung mit den beim Programmstart in eine
   * Hashtable (Properties) geladenen Name-Value-Paare aus Dateiendung und
   * mimetype. Im Erfolgsfall gibt sie den mimetype der Datei als String zurück.
   * Sonst wird der String "unknown/unknown" als Rückgabewert verwendet.
   */
  public String guessType(String filename)
  {
    String type = null;
    int i = filename.lastIndexOf(".");
    if (i > 0)
    {
      type = typeMap.getProperty(filename.substring(i));
    }
    if (type == null)
    {
      type = "unknown/unknown";
    }
    return type;
  }
}
