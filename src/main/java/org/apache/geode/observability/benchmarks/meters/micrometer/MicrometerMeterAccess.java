package org.apache.geode.observability.benchmarks.meters.micrometer;

import static java.util.concurrent.TimeUnit.SECONDS;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

/**
 * Measures the throughput of accessing Micrometer meters and gauges.
 */
@Measurement(iterations = 10, time = 10, timeUnit = SECONDS)
@Warmup(iterations = 1, time = 10, timeUnit = SECONDS)
@Fork(1)
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
@SuppressWarnings("unused")
public class MicrometerMeterAccess {
  private int gaugeSubject;
  private final MeterRegistry registry = new SimpleMeterRegistry();
  private Counter counter;
  private Gauge gauge;

  @Setup
  public void start() {
    counter = Counter.builder("micrometer.counter").register(registry);
    gauge = Gauge.builder("micrometer.gauge", () -> ++gaugeSubject).register(registry);
  }

  @Benchmark
  public void updateCounter() {
    counter.increment();
  }

  @Benchmark
  public double updateGauge() {
    return gauge.value();
  }
}
