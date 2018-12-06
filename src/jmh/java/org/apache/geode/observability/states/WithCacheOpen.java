package org.apache.geode.observability.states;

import static org.apache.geode.distributed.ConfigurationProperties.LOCATORS;
import static org.apache.geode.distributed.ConfigurationProperties.LOG_LEVEL;

import java.time.Duration;

import io.micrometer.core.instrument.push.PushRegistryConfig;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionShortcut;

@State(Scope.Benchmark)
public class WithCacheOpen {
  private Cache cache;
  public Region<Object, Object> region;

  @Setup
  public void createCache() {
    cache = new CacheFactory()
        .set(LOCATORS, "")
        .set(LOG_LEVEL, "none")
        .create();
    region = cache.createRegionFactory(RegionShortcut.LOCAL).create("region");
  }

  @TearDown
  public void closeCache() {
    cache.close();
  }
}
