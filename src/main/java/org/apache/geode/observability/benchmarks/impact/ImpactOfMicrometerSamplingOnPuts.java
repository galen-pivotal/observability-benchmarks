package org.apache.geode.observability.benchmarks.impact;

import static java.util.concurrent.TimeUnit.MINUTES;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Timeout;
import org.openjdk.jmh.annotations.Warmup;

import org.apache.geode.observability.states.WithBlackHoleSampling;
import org.apache.geode.observability.states.WithCacheOpen;

@Measurement(iterations = 10, time = 10, timeUnit = MINUTES)
@Warmup(iterations = 1, time = 1, timeUnit = MINUTES)
@Timeout(time = 20, timeUnit = MINUTES)
@BenchmarkMode(Mode.Throughput)
@SuppressWarnings("unused")
public class ImpactOfMicrometerSamplingOnPuts {

  @Threads(1)
  @Benchmark
  public void putsWithMicrometer1Thread(WithCacheOpen cache, WithBlackHoleSampling micrometer) {
    cache.region.put(2, "foo");
  }

  @Threads(1)
  @Benchmark
  public void putsWithoutMicrometer1Thread(WithCacheOpen cache) {
    cache.region.put(2, "foo");
  }

  @Threads(-1)
  @Benchmark
  public void putsWithMicrometerAllAvailableThreads(WithCacheOpen cache,
                                                    WithBlackHoleSampling micrometer) {
    cache.region.put(2, "foo");
  }

  @Threads(-1)
  @Benchmark
  public void putsWithoutMicrometerAllAvailableThreads(WithCacheOpen cache) {
    cache.region.put(2, "foo");
  }

  @Threads(100)
  @Benchmark
  public void putsWithMicrometer100Threads(WithCacheOpen cache, WithBlackHoleSampling micrometer) {
    cache.region.put(2, "foo");
  }

  @Threads(100)
  @Benchmark
  public void putsWithoutMicrometer100Threads(WithCacheOpen cache) {
    cache.region.put(2, "foo");
  }
}
