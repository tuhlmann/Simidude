package com.agynamix.platform.infra;

public class Tupel<S1, S2> {
  
  private final S1 value1;
  private final S2 value2;
  
  public Tupel(S1 value1, S2 value2)
  {
    this.value1 = value1;
    this.value2 = value2;
  }
  
  public S1 getValue1()
  {
    return value1;
  }

  public S2 getValue2()
  {
    return value2;
  }
  
}
