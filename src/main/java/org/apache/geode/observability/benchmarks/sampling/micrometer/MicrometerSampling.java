package org.apache.geode.observability.benchmarks.sampling.micrometer;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.stream.IntStream;

import io.micrometer.core.instrument.Counter;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import org.apache.geode.observability.registries.BlackHoleRegistry;

/**
 * Measures the sampling throughput of a simple Micrometer push registry.
 *
 * The registry "publishes" by sampling each measurement of each meter, and writing the sampled
 * values to a black hole.
 *
 * The benchmark prepares the registry by adding 1000 gauges and 1000 counters.
 */
@Measurement(iterations = 10, time = 10, timeUnit = SECONDS)
@Warmup(iterations = 1, time = 10, timeUnit = SECONDS)
@Fork(1)
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
@SuppressWarnings("unused")
public class MicrometerSampling {
  private BlackHoleRegistry registry;

  @Setup
  public void populateRegistry(Blackhole blackHole) {
    registry = new BlackHoleRegistry(blackHole);
    IntStream.range(0, 250)
        .mapToObj(i -> "counter" + i)
        .forEach(registry::counter);
    IntStream.range(0, 250)
        .mapToObj(i -> "gauge" + i)
        .map(Counter::builder)
        .forEach(counterBuilder -> counterBuilder.register(registry));
  }

  @Benchmark
  public void sample() {
    registry.publish();
  }
}
