package org.apache.geode.observability.benchmarks.sampling.geode;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.stream.IntStream;

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

import org.apache.geode.CancelCriterion;
import org.apache.geode.StatisticDescriptor;
import org.apache.geode.Statistics;
import org.apache.geode.StatisticsType;
import org.apache.geode.internal.statistics.LocalStatisticsFactory;
import org.apache.geode.internal.statistics.SampleCollector;
import org.apache.geode.internal.statistics.SimpleStatSampler;
import org.apache.geode.internal.statistics.StatisticsManager;
import org.apache.geode.internal.statistics.StatisticsSampler;

@Measurement(iterations = 10, time = 10, timeUnit = SECONDS)
@Warmup(iterations = 1, time = 10, timeUnit = SECONDS)
@Fork(1)
@OutputTimeUnit(SECONDS)
@State(Scope.Benchmark)
@SuppressWarnings("unused")
public class GeodeSamplerBenchmarks {
  SampleCollector collector;

  @Setup
  public void setup() {
    CancelCriterion cancelCriterion = new CancelCriterion() {
      @Override
      public String cancelInProgress() {
        return null;
      }

      @Override
      public RuntimeException generateCancelledException(Throwable throwable) {
        return null;
      }
    };
    StatisticsManager manager = new LocalStatisticsFactory(cancelCriterion);
    StatisticsSampler sampler = new SimpleStatSampler(cancelCriterion, manager);
    collector = new SampleCollector(sampler);
    StatisticDescriptor[] intCounterDescriptors = IntStream.range(0, 250)
        .mapToObj(i -> manager.createIntCounter("intcounter" + i, "", ""))
        .toArray(StatisticDescriptor[]::new);
    StatisticsType intCounterStatisticsType = manager.createType("intcounters", "", intCounterDescriptors);
    Statistics intCounterStatistics = manager.createStatistics(intCounterStatisticsType);
    StatisticDescriptor[] intGaugeDescriptors = IntStream.range(0, 250)
        .mapToObj(i -> manager.createIntGauge("intgauge" + i, "", ""))
        .toArray(StatisticDescriptor[]::new);
    StatisticsType intGaugeStatisticsType = manager.createType("intgauges", "", intGaugeDescriptors);
    Statistics intGaugeStatistics = manager.createStatistics(intCounterStatisticsType);
  }

  @BenchmarkMode(Mode.Throughput)
  @Benchmark
  public void sample() {
    collector.sample(0);
  }
}
