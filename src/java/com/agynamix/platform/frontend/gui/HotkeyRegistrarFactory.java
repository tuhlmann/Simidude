package com.agynamix.platform.frontend.gui;

import com.agynamix.platform.infra.PlatformUtils;

public class HotkeyRegistrarFactory {
  
  static IHotkeyRegistrar windowsHotkeyRegistrar;
  static IHotkeyRegistrar unixHotkeyRegistrar;
  static IHotkeyRegistrar osSupportHotkeyRegistrar;
  static IHotkeyRegistrar placeboHotkeyRegistrar;
  
  public static IHotkeyRegistrar getHotkeyRegistrarInstance() {
    IHotkeyRegistrar registrar;
    switch (PlatformUtils.getOsName())
    {
      case win32:
      case win64:
        registrar = getJIntellitypeHotkeyRegistrarInstance();
        break;
      case linux_x86:
      case linux_x86_64:
        registrar = getJXGrabKeyHotkeyRegistrarInstance();
        break;
      case macosx:
      case macosx64:
        registrar = getOsSupportHotkeyRegistrarInstance();
        break;
      default:
        registrar = getPlaceboHotkeyRegistrarInstance();
    }
    return registrar;
  }

  private static IHotkeyRegistrar getPlaceboHotkeyRegistrarInstance() {
    if (placeboHotkeyRegistrar == null)
    {
      placeboHotkeyRegistrar = new PlaceboHotkeyRegistrar();
    }
    return placeboHotkeyRegistrar;
  }

  private static IHotkeyRegistrar getOsSupportHotkeyRegistrarInstance() {
    if (osSupportHotkeyRegistrar == null)
    {
      osSupportHotkeyRegistrar = new OsSupportHotkeyRegistrar();
    }
    return osSupportHotkeyRegistrar;
  }
  
  private static IHotkeyRegistrar getJIntellitypeHotkeyRegistrarInstance() {
    if (windowsHotkeyRegistrar == null)
    {
      windowsHotkeyRegistrar = new JIntellitypeHotkeyRegistrar();
    }
    return windowsHotkeyRegistrar;
  }

  private static IHotkeyRegistrar getJXGrabKeyHotkeyRegistrarInstance() {
    if (unixHotkeyRegistrar == null)
    {
      unixHotkeyRegistrar = new JXGrabKeyHotkeyRegistrar();
    }
    return unixHotkeyRegistrar;
  }
  
}
