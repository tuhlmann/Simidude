package sb;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class CheckNetwork {

  public static List<InetAddress> getHostAddresses()
  {
    List<InetAddress> ipAddresses = new ArrayList<InetAddress>();

    try
    {
      Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
      while (interfaces.hasMoreElements())
      {
        NetworkInterface ni = interfaces.nextElement();
        Enumeration<InetAddress> addresses = ni.getInetAddresses();
        while (addresses.hasMoreElements())
        {
          InetAddress addr = addresses.nextElement();
          if (!addr.isLoopbackAddress())
          {
            if (addr instanceof Inet4Address) // Fixme: Only IP4 at this time.
            {
              // if (addr.isReachable(10000))
              // {
              // System.out.println("Address "+addr+" is reachable");
              System.out.println("My address: " + addr.getHostAddress());
              ipAddresses.add(addr);
              // }
            }
          }
        }
      }
      // addCustomAddressFromPreferences(ipAddresses);
    } catch (SocketException e)
    {
      e.printStackTrace();
    } catch (IOException e)
    {
      e.printStackTrace();
    }
    if (ipAddresses.size() == 0)
    {
      try
      {
        ipAddresses.add(InetAddress.getLocalHost());
      } catch (UnknownHostException e)
      {
        e.printStackTrace();
        throw new IllegalStateException(e);
      }
    }
    return ipAddresses;
  }

  public static InetAddress getPrimaryHostAddress()
  {
    try
    {
      return getHostAddresses().get(0);
    } catch (Exception e)
    {
      try
      {
        return Inet4Address.getByName("127.0.0.1");
      } catch (UnknownHostException e1)
      {
        return null;
      }
    }
  }

  public static String getLocalHostAddress()
  {
    try
    {
      // InetAddress addr = InetAddress.getLocalHost();
      // System.out.println("Found local address: "+addr.getHostAddress());
      // return addr.getHostAddress();
      List<InetAddress> addresses = getHostAddresses();
      // for (InetAddress a : addresses)
      // {
      // System.out.println("Address: "+a.getHostAddress());
      // }
      InetAddress hostname = addresses.get(0);
      return hostname.getHostAddress();
    } catch (Exception e)
    {
      e.printStackTrace();
      return "127.0.0.1";
    }
  }


  /**
   * @param args
   */
  public static void main(String[] args)
  {
    System.out.println("The main host address: "+getLocalHostAddress());

  }

}
