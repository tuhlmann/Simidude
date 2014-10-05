package com.agynamix.platform.frontend.gui;

public class PlaceboHotkeyRegistrar extends AbstractHotkeyRegistrar {

  public boolean isEnabled()
  {
    return false;
  }
  
  public void addHotkeyListener(String hotkeyCombination, IHotkeyListener listener)
  {
//    System.out.println("PlaceboHotkeyRegistrar.addHotkeyListener called");
  }

  public void unregisterHotkeys()
  {
//    System.out.println("PlaceboHotkeyRegistrar.unregisterHotkeys called");
  }
  
  public void activateGlobalPaste(HotkeyListenerInfo listenerInfo)
  {
//    System.out.println("PlaceboHotkeyRegistrar.activateGlobalPaste called");
  }

  @Override
  protected int tokenToModifier(String token)
  {
    return 0;
  }

}
