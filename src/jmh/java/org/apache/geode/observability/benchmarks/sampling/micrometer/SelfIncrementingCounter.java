package org.apache.geode.observability.benchmarks.sampling.micrometer;

import io.micrometer.core.instrument.Counter;

public class SelfIncrementingCounter implements Counter {
  private final Id id;
  int count = 0;

  public SelfIncrementingCounter(Id id) {
    this.id = id;
  }

  @Override
  public void increment(double amount) {
  }

  @Override
  public double count() {
    return count++;
  }

  @Override
  public Id getId() {
    return id;
  }
}
