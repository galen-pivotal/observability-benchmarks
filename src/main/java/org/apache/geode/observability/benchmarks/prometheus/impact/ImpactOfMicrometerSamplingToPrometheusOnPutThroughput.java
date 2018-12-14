package org.apache.geode.observability.benchmarks.prometheus.impact;

import static java.util.concurrent.TimeUnit.MINUTES;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Timeout;
import org.openjdk.jmh.annotations.Warmup;

import org.apache.geode.observability.states.KeysAndValues;
import org.apache.geode.observability.states.WithCacheOpen;
import org.apache.geode.observability.benchmarks.sampling.MicrometerToPrometheusSampling;

/**
 * Measures the throughput of cache puts with and without micrometer sampling in the background.
 */
@Measurement(iterations = 2, time = 10, timeUnit = MINUTES)
@Warmup(iterations = 1, time = 1, timeUnit = MINUTES)
@Timeout(time = 20, timeUnit = MINUTES)
@Fork(5)
@Threads(1)
@BenchmarkMode(Mode.Throughput)
public class ImpactOfMicrometerSamplingToPrometheusOnPutThroughput {
  /**
   * Measures the throughput of cache puts with micrometer sampling in the background.
   */
  @Benchmark
  public void puts_withMicrometerSampling(WithCacheOpen cache, KeysAndValues data,
                                          @SuppressWarnings("unused") MicrometerToPrometheusSampling background) {
    cache.region.put(data.key, data.value);
  }

  /**
   * Measures the throughput of cache puts without micrometer sampling in the background.
   */
  @Benchmark
  public void puts_withoutMicrometerSampling(WithCacheOpen cache, KeysAndValues data) {
    cache.region.put(data.key, data.value);
  }
}
