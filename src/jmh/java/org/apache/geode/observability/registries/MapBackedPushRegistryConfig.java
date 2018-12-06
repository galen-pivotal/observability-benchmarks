package org.apache.geode.observability.registries;

import java.util.Map;

import io.micrometer.core.instrument.push.PushRegistryConfig;

public class MapBackedPushRegistryConfig implements PushRegistryConfig {
  private final String prefix;
  private final Map<String, String> options;

  public MapBackedPushRegistryConfig(String prefix, Map<String,String> options) {
    this.prefix = prefix;
    this.options = options;
  }

  public MapBackedPushRegistryConfig(Map<String,String> options) {
    this("", options);
  }

  @Override
  public String prefix() {
    return prefix;
  }

  @Override
  public String get(String key) {
    return options.get(key);
  }
}
