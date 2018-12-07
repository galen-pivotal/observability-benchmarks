package org.apache.geode.observability.benchmarks.impact;

import static java.util.concurrent.TimeUnit.MINUTES;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Timeout;
import org.openjdk.jmh.annotations.Warmup;

import org.apache.geode.observability.states.WithBlackHoleSampling;
import org.apache.geode.observability.states.WithCacheOpen;

/**
 * Measures the effect of micrometer sampling on cache put throughput. Each benchmark measures the
 * throughput of cache puts. Half of the benchmarks start a Micrometer meter registry that
 * periodically samples dummy meters. The other half do not start a Micrometer registry.
 */
@Measurement(iterations = 10, time = 10, timeUnit = MINUTES)
@Warmup(iterations = 1, time = 10, timeUnit = MINUTES)
@Timeout(time = 20, timeUnit = MINUTES)
@Fork(1)
@BenchmarkMode(Mode.Throughput)
@SuppressWarnings("unused")
public class ImpactOfMicrometerSamplingOnPutThroughput {

  @Threads(1)
  @Benchmark
  public void putsOn1Thread_withMicrometerSampling(WithCacheOpen cache,
                                                   WithBlackHoleSampling micrometer) {
    cache.region.put(2, "foo");
  }

  @Threads(1)
  @Benchmark
  public void putsOn1Thread_withoutMicrometerSampling(WithCacheOpen cache) {
    cache.region.put(2, "foo");
  }

  /**
   * The number of threads is determined by {@link Runtime#availableProcessors()}
   */
  @Threads(-1)
  @Benchmark
  public void putsOnAllAvailableThreads_withMicrometerSampling(WithCacheOpen cache,
                                                               WithBlackHoleSampling micrometer) {
    cache.region.put(2, "foo");
  }

  /**
   * The number of threads is determined by {@link Runtime#availableProcessors()}
   */
  @Threads(-1)
  @Benchmark
  public void putsOnAllAvailableThreads_withoutMicrometerSampling(WithCacheOpen cache) {
    cache.region.put(2, "foo");
  }

  @Threads(100)
  @Benchmark
  public void putsOn100Threads_withMicrometerSampling(WithCacheOpen cache,
                                                      WithBlackHoleSampling micrometer) {
    cache.region.put(2, "foo");
  }

  @Threads(100)
  @Benchmark
  public void putsOn100Threads_withoutMicrometerSampling(WithCacheOpen cache) {
    cache.region.put(2, "foo");
  }
}
