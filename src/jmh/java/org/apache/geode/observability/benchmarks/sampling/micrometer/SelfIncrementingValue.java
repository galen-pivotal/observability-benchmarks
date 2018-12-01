package org.apache.geode.observability.benchmarks.sampling.micrometer;

public class SelfIncrementingValue {
  int count = 0;

  public double get() {
    return count++;
  }
}
