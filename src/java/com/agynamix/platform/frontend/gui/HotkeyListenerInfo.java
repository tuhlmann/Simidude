package com.agynamix.platform.frontend.gui;

import com.agynamix.ossupport.HotKeyDesc;

public class HotkeyListenerInfo {

  private final IHotkeyRegistrar hotkeyRegistrar;
  private final int hotkeyId;
  private final HotKeyDesc hotkeyDesc;
  private final IHotkeyListener listener;
  
  public HotkeyListenerInfo(IHotkeyRegistrar hotkeyRegistrar, int hotkeyId, HotKeyDesc hotkeyDesc, IHotkeyListener listener)
  {
    this.hotkeyRegistrar = hotkeyRegistrar;
    this.hotkeyId        = hotkeyId;
    this.hotkeyDesc      = hotkeyDesc;
    this.listener        = listener;
  }

  public IHotkeyListener getListener()
  {
    return listener;
  }
  
  public IHotkeyRegistrar getHotkeyRegistrar()
  {
    return hotkeyRegistrar;
  }

  public int getHotkeyId()
  {
    return hotkeyId;
  }

  public HotKeyDesc getHotKeyDesc()
  {
    return hotkeyDesc;
  }

  public void onHotkey(int hotkeyId)
  {
    listener.onHotkey(this);
  }

}
