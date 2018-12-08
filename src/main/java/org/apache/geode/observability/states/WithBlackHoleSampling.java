package org.apache.geode.observability.states;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.push.PushMeterRegistry;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;

import org.apache.geode.observability.registries.BlackHoleRegistry;

/**
 * Starts a push meter registry that periodically samples each registered meter and writes the
 * sampled value to a black hole.
 * <p>
 * Parameters:
 * <ul>
 * <li>{@link #numberOfMeters the number of meters to add to the registry}</li>
 * <li>{@link #samplingInterval the sampling interval}</li>
 * </ul>
 * The meters are not connected to anything. Half of the meters are counters and half are gauges.
 */
@State(Scope.Benchmark)
public class WithBlackHoleSampling {
  private final Map<String, String> options = new HashMap<>();
  private PushMeterRegistry registry;

  /**
   * The number of meters to register. Half will be counters and half will be gauges.
   */
  @Param("100")
  public int numberOfMeters;

  /**
   * The sampling interval, represented in the format accepted by{@link java.time.Duration#parse}.
   */
  @Param("PT30S")
  public String samplingInterval;

  @Setup
  public void startSampling(Blackhole blackhole) {
    options.put(".step", samplingInterval);

    registry = new BlackHoleRegistry(options, blackhole);

    IntStream.range(0, numberOfMeters / 2)
        .mapToObj(i -> "counter" + i)
        .forEach(registry::counter);
    IntStream.range(0, numberOfMeters / 2)
        .mapToObj(i -> "gauge" + i)
        .map(Counter::builder)
        .forEach(counterBuilder -> counterBuilder.register(registry));

    registry.start(Executors.defaultThreadFactory());
  }

  @TearDown
  public void stopSampling() {
    registry.stop();
  }
}
