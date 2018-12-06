package org.apache.geode.observability.registries;

public class SelfIncrementingValue {
  int count = 0;

  public double get() {
    return count++;
  }
}
