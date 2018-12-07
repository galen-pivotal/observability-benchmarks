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
 * Measures the throughput of cache puts with and without micrometer sampling in the background.
 */
@Measurement(iterations = 10, time = 10, timeUnit = MINUTES)
@Warmup(iterations = 1, time = 10, timeUnit = MINUTES)
@Timeout(time = 20, timeUnit = MINUTES)
@Fork(1)
@Threads(1)
@BenchmarkMode(Mode.Throughput)
public class ImpactOfMicrometerSamplingOnPutThroughput {
  /**
   * Measures the throughput of cache puts with micrometer sampling in the background.
   */
  @Benchmark
  public void puts_withMicrometerSampling(WithCacheOpen state,
                                          @SuppressWarnings("unused") WithBlackHoleSampling background) {
    state.region.put(state.key, state.value);
  }

  /**
   * Measures the throughput of cache puts without micrometer sampling in the background.
   */
  @Benchmark
  public void puts_withoutMicrometerSampling(WithCacheOpen state) {
    state.region.put(state.key, state.value);
  }
}
