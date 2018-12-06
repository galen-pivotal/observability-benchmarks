package org.apache.geode.observability.benchmarks.impact;

import static java.util.concurrent.TimeUnit.SECONDS;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Warmup;

import org.apache.geode.observability.states.WithCacheOpen;
import org.apache.geode.observability.states.WithBlackHoleSampling;

@Measurement(iterations = 10, time = 10, timeUnit = SECONDS)
@Warmup(iterations = 1, time = 10, timeUnit = SECONDS)
@Fork(1)
//@Threads(1)
@BenchmarkMode(Mode.Throughput)
@SuppressWarnings("unused")
public class ImpactOfMicrometerSamplingOnPuts {

  @Benchmark
  public void putsWithMicrometer(WithCacheOpen cache, WithBlackHoleSampling micrometer) {
    cache.region.put(2, "foo");
  }

  @Benchmark
  public void putsWithoutMicrometer(WithCacheOpen cache) {
    cache.region.put(2, "foo");
  }
}
