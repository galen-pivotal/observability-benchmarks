package org.apache.geode.observability.benchmarks.sampling.micrometer;

import io.micrometer.core.instrument.push.PushRegistryConfig;

public class BlackHolePushRegistryConfig implements PushRegistryConfig {
  @Override
  public String prefix() {
    return null;
  }

  @Override
  public String get(String key) {
    return null;
  }
}
