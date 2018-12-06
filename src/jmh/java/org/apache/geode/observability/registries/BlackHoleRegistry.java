package org.apache.geode.observability.registries;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.FunctionCounter;
import io.micrometer.core.instrument.FunctionTimer;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.cumulative.CumulativeCounter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.core.instrument.distribution.pause.PauseDetector;
import io.micrometer.core.instrument.internal.DefaultGauge;
import io.micrometer.core.instrument.noop.NoopDistributionSummary;
import io.micrometer.core.instrument.noop.NoopFunctionCounter;
import io.micrometer.core.instrument.noop.NoopFunctionTimer;
import io.micrometer.core.instrument.noop.NoopLongTaskTimer;
import io.micrometer.core.instrument.noop.NoopMeter;
import io.micrometer.core.instrument.noop.NoopTimer;
import io.micrometer.core.instrument.push.PushMeterRegistry;
import io.micrometer.core.instrument.push.PushRegistryConfig;
import org.openjdk.jmh.infra.Blackhole;

/**
 * Samples every measurement of every meter and writes it to a black hole.
 */
public class BlackHoleRegistry extends PushMeterRegistry {
  private static final Map<String,String> DEFAULT_CONFIG = new HashMap<>();
  private final Blackhole blackHole;

  public BlackHoleRegistry(Blackhole blackhole) {
    this(DEFAULT_CONFIG, blackhole);
  }

  public BlackHoleRegistry(Map<String,String> options, Blackhole blackhole) {
    this(new MapBackedPushRegistryConfig(options), blackhole);
  }

  public BlackHoleRegistry(PushRegistryConfig config, Blackhole blackhole) {
    super(config, Clock.SYSTEM);
    this.blackHole = blackhole;
  }

  @Override
  public void publish() {
    forEachMeter(this::sampleToBlackHole);
  }

  private void sampleToBlackHole(Meter meter) {
    meter.measure().forEach(this::sampleToBlackHole);
  }

  private void sampleToBlackHole(Measurement measurement) {
    blackHole.consume(measurement.getValue());
  }

  @Override
  protected <T> Gauge newGauge(Meter.Id id, T obj, ToDoubleFunction<T> valueFunction) {
    return new DefaultGauge<>(id, new SelfIncrementingValue(), SelfIncrementingValue::get);
  }

  @Override
  protected Counter newCounter(Meter.Id id) {
    return new CumulativeCounter(id);
  }

  @Override
  protected LongTaskTimer newLongTaskTimer(Meter.Id id) {
        return new NoopLongTaskTimer(id);
  }

  @Override
  protected Timer newTimer(Meter.Id id, DistributionStatisticConfig distributionStatisticConfig,
                           PauseDetector pauseDetector) {
    return new NoopTimer(id);
  }

  @Override
  protected DistributionSummary newDistributionSummary(Meter.Id id,
                                                       DistributionStatisticConfig distributionStatisticConfig,
                                                       double scale) {
    return new NoopDistributionSummary(id);
  }

  @Override
  protected Meter newMeter(Meter.Id id, Meter.Type type, Iterable<Measurement> measurements) {
    return new NoopMeter(id);
  }

  @Override
  protected <T> FunctionTimer newFunctionTimer(Meter.Id id, T obj, ToLongFunction<T> countFunction,
                                               ToDoubleFunction<T> totalTimeFunction,
                                               TimeUnit totalTimeFunctionUnit) {
    return new NoopFunctionTimer(id);
  }

  @Override
  protected <T> FunctionCounter newFunctionCounter(Meter.Id id, T obj,
                                                   ToDoubleFunction<T> countFunction) {
    return new NoopFunctionCounter(id);
  }

  @Override
  protected TimeUnit getBaseTimeUnit() {
    return MILLISECONDS;
  }

  @Override
  protected DistributionStatisticConfig defaultHistogramConfig() {
    return new DistributionStatisticConfig();
  }
}
