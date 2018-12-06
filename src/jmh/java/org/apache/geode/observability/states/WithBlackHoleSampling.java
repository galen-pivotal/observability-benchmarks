package org.apache.geode.observability.states;

import java.time.Duration;
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

@State(Scope.Benchmark)
public class WithBlackHoleSampling {
  public Map<String,String> options = new HashMap<>();
  private PushMeterRegistry registry;

  @Param({"10", "100", "1000"})
  public int numberOfMeters;

  @Setup
  public void startSampling(Blackhole blackhole) {
    options.put(".step", Duration.ofSeconds(1).toString());

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
