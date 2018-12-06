package org.apache.geode.observability.helpers;

public class SelfIncrementingValue {
  int count = 0;

  public double get() {
    return count++;
  }
}
