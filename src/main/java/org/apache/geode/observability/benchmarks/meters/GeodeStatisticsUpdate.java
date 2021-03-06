package org.apache.geode.observability.benchmarks.meters;

import static java.util.concurrent.TimeUnit.SECONDS;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Timeout;
import org.openjdk.jmh.annotations.Warmup;

import org.apache.geode.StatisticDescriptor;
import org.apache.geode.Statistics;
import org.apache.geode.StatisticsFactory;
import org.apache.geode.StatisticsType;
import org.apache.geode.StatisticsTypeFactory;
import org.apache.geode.internal.statistics.LocalStatisticsFactory;
import org.apache.geode.internal.statistics.StatisticsTypeFactoryImpl;

/**
 * Measures the throughput of updating selected kinds of Geode statistics.
 */
@Measurement(iterations = 10, time = 10, timeUnit = SECONDS)
@Warmup(iterations = 1, time = 10, timeUnit = SECONDS)
@Timeout(time = 20, timeUnit = SECONDS)
@Fork(1)
@Threads(1)
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
public class GeodeStatisticsUpdate {
  private Statistics atomicIntStatistics;
  private Statistics atomicLongStatistics;
  private Statistics localDoubleStatistics;
  private Statistics localIntStatistics;
  private Statistics localLongStatistics;
  private int doubleCounterId;
  private int doubleGaugeId;
  private int intCounterId;
  private int intGaugeId;
  private int longCounterId;
  private int longGaugeId;

  @Setup
  public void setup() {
    StatisticsTypeFactory statisticsTypeFactory = StatisticsTypeFactoryImpl.singleton();
    StatisticsType
        doubleStatisticsType =
        statisticsTypeFactory.createType("int", "", new StatisticDescriptor[]{
            statisticsTypeFactory.createDoubleCounter("counter", "", ""),
            statisticsTypeFactory.createDoubleGauge("gauge", "", ""),
        });
    doubleCounterId = doubleStatisticsType.nameToId("counter");
    doubleGaugeId = doubleStatisticsType.nameToId("gauge");

    StatisticsType
        intStatisticsType =
        statisticsTypeFactory.createType("double", "", new StatisticDescriptor[]{
            statisticsTypeFactory.createIntCounter("counter", "", ""),
            statisticsTypeFactory.createIntGauge("gauge", "", ""),
        });
    intCounterId = intStatisticsType.nameToId("counter");
    intGaugeId = intStatisticsType.nameToId("gauge");

    StatisticsType
        longStatisticsType =
        statisticsTypeFactory.createType("long", "", new StatisticDescriptor[]{
            statisticsTypeFactory.createLongCounter("counter", "", ""),
            statisticsTypeFactory.createLongGauge("gauge", "", ""),
        });
    longCounterId = longStatisticsType.nameToId("counter");
    longGaugeId = longStatisticsType.nameToId("gauge");

    StatisticsFactory statisticsFactory = new LocalStatisticsFactory(null);
    atomicIntStatistics =
        statisticsFactory.createAtomicStatistics(intStatisticsType, "geode.atomic.int");
    atomicLongStatistics =
        statisticsFactory.createAtomicStatistics(longStatisticsType, "geode.atomic.long");
    localDoubleStatistics =
        statisticsFactory.createStatistics(doubleStatisticsType, "geode.local.double");
    localIntStatistics =
        statisticsFactory.createStatistics(intStatisticsType, "geode.local.int");
    localLongStatistics =
        statisticsFactory.createStatistics(longStatisticsType, "geode.local.long");
  }

  @Benchmark
  public void updateAtomicIntCounter() {
    atomicIntStatistics.incInt(intCounterId, 1);
  }

  @Benchmark
  public void updateAtomicIntGauge() {
    atomicIntStatistics.incInt(intGaugeId, 1);
  }

  @Benchmark
  public void updateAtomicLongCounter() {
    atomicLongStatistics.incLong(longCounterId, 1);
  }

  @Benchmark
  public void updateAtomicLongGauge() {
    atomicLongStatistics.incLong(longGaugeId, 1);
  }

  @Benchmark
  public void updateLocalDoubleCounter() {
    localDoubleStatistics.incDouble(doubleCounterId, 1);
  }

  @Benchmark
  public void updateLocalDoubleGauge() {
    localDoubleStatistics.incDouble(doubleGaugeId, 1);
  }

  @Benchmark
  public void updateLocalIntCounter() {
    localIntStatistics.incInt(intCounterId, 1);
  }

  @Benchmark
  public void updateLocalIntGauge() {
    localIntStatistics.incInt(intGaugeId, 1);
  }

  @Benchmark
  public void updateLocalLongCounter() {
    localLongStatistics.incLong(longCounterId, 1);
  }

  @Benchmark
  public void updateLocalLongGauge() {
    localLongStatistics.incLong(longGaugeId, 1);
  }
}
