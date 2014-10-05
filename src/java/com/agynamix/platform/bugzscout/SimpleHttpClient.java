package com.agynamix.platform.bugzscout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public class SimpleHttpClient {
  
  protected Proxy findProxy(URI uri)
  {
    try
    {
      ProxySelector selector = ProxySelector.getDefault();
      List<Proxy> proxyList = selector.select(uri);
      if (proxyList.size() > 1)
      {
        return proxyList.get(0);
      }
    } catch (IllegalArgumentException e)
    {
      System.out.println("Proxy not found: "+e.getMessage());
    }
    return Proxy.NO_PROXY;
  }  
  
  public Response sendRequest(String sUrl, Map<String, String> params)
  {
    Response response = null;
    
    StringBuffer sb = new StringBuffer();
    try
    {
      boolean isFirst = true;
      
      for (String key : params.keySet())
      {
        String pName = "&"; //$NON-NLS-1$
        if (isFirst)
        {
          isFirst = false;
          pName = ""; //$NON-NLS-1$
        }
        pName += URLEncoder.encode(key, "UTF-8") + "="; //$NON-NLS-1$ //$NON-NLS-2$
        sb.append(pName);
        sb.append(URLEncoder.encode(params.get(key), "UTF-8")); //$NON-NLS-1$
      }
    } catch (UnsupportedEncodingException e)
    {
      throw new IllegalStateException(e);
    }
    
    String formData = sb.toString();
//    System.out.println("REQ="+formData);

    HttpURLConnection urlcon = null;
    
    try
    {
      URL url = new URL(sUrl);
      Proxy itsProxy = findProxy(new URI(sUrl));
//      System.out.println("Proxy: "+itsProxy);
      urlcon = (HttpURLConnection) url.openConnection(itsProxy);
      urlcon.setRequestMethod("POST"); //$NON-NLS-1$
      urlcon.setRequestProperty("Content-type", "application/x-www-form-urlencoded"); //$NON-NLS-1$ //$NON-NLS-2$
      urlcon.setDoOutput(true);
      urlcon.setDoInput(true);
      PrintWriter pout = new PrintWriter(new OutputStreamWriter(urlcon.getOutputStream(), "UTF-8"), true); //$NON-NLS-1$
      pout.print(formData);
      pout.flush();
      pout.close();
      
      // read results...
      if (urlcon.getResponseCode() != HttpURLConnection.HTTP_OK)
      {
        response = new Response(Response.Status.SC_ERROR, urlcon.getResponseCode(), readFromStream(urlcon.getErrorStream()).getBytes());
        return response;
      }

      response = new Response(Response.Status.SC_OK, urlcon.getResponseCode(), readFromStream(urlcon.getInputStream()).getBytes());
    } catch (IOException e)
    {
      response = new Response(Response.Status.SC_ERROR, e);
    } catch (URISyntaxException e)
    {
      System.out.println("Could not convert URL to URI: "+sUrl+", Msg: "+e.getMessage());
      response = new Response(Response.Status.SC_ERROR, e);
    } finally {
      if (urlcon != null)
      {
        urlcon.disconnect();
      }
    }
    return response;
      
  }

  private String readFromStream(InputStream stream) throws IOException
  {
    if (stream == null) return ""; //$NON-NLS-1$
    StringBuilder sb = new StringBuilder();
    String line = ""; //$NON-NLS-1$
    BufferedReader rd = new BufferedReader(new InputStreamReader(stream));
    while ((line = rd.readLine()) != null)
    {
      sb.append(line);
    }
    rd.close();
    return sb.toString();
  }
  
  public static class Response {
    
    public enum Status { SC_UNKNOWN, SC_OK, SC_ERROR };
    
    final Status responseStatus;
    final String errorMsg;
    final int    responseCode;
    final byte[] body;
    
    public Response(Status responseStatus, int respCode, byte[] body)
    {
      this.responseStatus = responseStatus;
      this.responseCode   = respCode;
      this.body           = body;
      this.errorMsg       = ""; //$NON-NLS-1$
    }
    
    public Response(Status responseStatus, Throwable error)
    {
      this.responseStatus = responseStatus;
      this.responseCode   = -1;
      this.body           = new byte[0];
      this.errorMsg       = error.getMessage();      
    }
    
    public Status getStatusCode()
    {
      return responseStatus;
    }
    
    public String getErrorMessage()
    {
      return this.errorMsg;
    }

    public byte[] getResponseBody()
    {
      return body;
    }
    
  }
  
}
