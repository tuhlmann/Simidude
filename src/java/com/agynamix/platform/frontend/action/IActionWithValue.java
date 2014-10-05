package com.agynamix.platform.frontend.action;


public interface IActionWithValue {
  
  /**
   * Runs an action with the given selection value.
   * @param value the given selection value.
   */
  public void runWithValue(boolean value);
  
  public boolean getSelectionValue();

}
