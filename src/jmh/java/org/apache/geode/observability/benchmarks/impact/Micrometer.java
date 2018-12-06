package org.apache.geode.observability.benchmarks.impact;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.geode.distributed.ConfigurationProperties.LOCATORS;
import static org.apache.geode.distributed.ConfigurationProperties.LOG_LEVEL;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.push.PushMeterRegistry;
import io.micrometer.core.instrument.push.PushRegistryConfig;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionShortcut;
import org.apache.geode.observability.helpers.BlackHolePushMeterRegistry;
import org.apache.geode.observability.helpers.BlackHolePushRegistryConfig;

@Measurement(iterations = 5, time = 2, timeUnit = SECONDS)
@Warmup(iterations = 1, time = 10, timeUnit = SECONDS)
@Fork(1)
@OutputTimeUnit(SECONDS)
@Threads(1)
@BenchmarkMode(Mode.Throughput)
@SuppressWarnings("unused")
public class Micrometer {

  @State(Scope.Benchmark)
  public static class WithMicrometer {
    private PushMeterRegistry registry;
    private PushRegistryConfig config;
    private Clock clock = Clock.SYSTEM;
    private Cache cache;
    Region<Object, Object> region;

    @Setup
    public void startMicrometerRegistry(Blackhole blackhole) {
      PushRegistryConfig config = new BlackHolePushRegistryConfig() {
        @Override
        public Duration step() {
          return Duration.ofSeconds(1);
        }
        @Override
        public boolean enabled() {
          return true;
        }
      };
      registry = new BlackHolePushMeterRegistry(config, blackhole);

      IntStream.range(0, 5)
          .mapToObj(i -> "counter" + i)
          .forEach(registry::counter);
      IntStream.range(0, 5)
          .mapToObj(i -> "gauge" + i)
          .map(Counter::builder)
          .forEach(counterBuilder -> counterBuilder.register(registry));

      registry.start(Executors.defaultThreadFactory());

      cache = new CacheFactory()
          .set(LOCATORS, "")
          .set(LOG_LEVEL, "none")
          .create();
      region = cache.createRegionFactory(RegionShortcut.LOCAL).create("region");
    }

    @TearDown
    public void teardown() {
      registry.stop();
      cache.close();
    }
  }
  @State(Scope.Benchmark)
  public static class WithoutMicrometer {
    private Cache cache;
    Region<Object, Object> region;

    @Setup
    public void startMicrometerRegistry(Blackhole blackhole) {
      PushRegistryConfig config = new BlackHolePushRegistryConfig() {
        @Override
        public Duration step() {
          return Duration.ofMinutes(1);
        }
      };

      cache = new CacheFactory()
          .set(LOCATORS, "")
          .set(LOG_LEVEL, "none")
          .create();
      region = cache.createRegionFactory(RegionShortcut.LOCAL).create("region");
    }

    @TearDown
    public void teardown() {
      cache.close();
    }
  }

  @Benchmark
  public void putsWithMicrometer(WithMicrometer state) {
    state.region.put(2, "foo");
  }

  @Benchmark
  public void putsWithoutMicrometer(WithoutMicrometer state) {
    state.region.put(2, "foo");
  }
}
