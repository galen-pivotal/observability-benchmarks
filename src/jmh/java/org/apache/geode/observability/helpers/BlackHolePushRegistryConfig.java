package org.apache.geode.observability.helpers;

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
