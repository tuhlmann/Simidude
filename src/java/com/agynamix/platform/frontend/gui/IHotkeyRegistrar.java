package com.agynamix.platform.frontend.gui;


public interface IHotkeyRegistrar {
  
  /**
   * True if the HotkeyRegistrar has been enabled, false otherwise
   * @return
   */
  boolean isEnabled();
  
  void addHotkeyListener(String hotkeyCombination, IHotkeyListener listener);

  void unregisterHotkeys();

  void activateGlobalPaste(HotkeyListenerInfo listenerInfo);

  /**
   * Parse the Hotkey definition from the preferences into the platform
   * dependent representation.
   * @param hotkeyCombination Hotkey in the form 'Shift+Alt+L'
   * @return a tuple of the parsed hotkey. The first value if the modifier key, the second is the hotkey value.
   */
  //Tupel<Integer, Integer> parseHotkeyDefinition(String hotkeyCombination);
  


}
