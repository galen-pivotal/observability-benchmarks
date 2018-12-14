package org.apache.geode.observability.benchmarks.sampling;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import com.sun.net.httpserver.HttpServer;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.push.PushMeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Timeout;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import org.apache.geode.observability.registries.BlackHoleRegistry;


/**
 *
 */
@Measurement(iterations = 10, time = 10, timeUnit = SECONDS)
@Warmup(iterations = 1, time = 10, timeUnit = SECONDS)
@Timeout(time = 20, timeUnit = SECONDS)
@Fork(1)
@Threads(1)
@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
public class MicrometerToPrometheusSampling {
  private final Map<String, String> options = new HashMap<>();
  private PrometheusMeterRegistry registry;
  private HttpServer server;


  @Setup
  public void setup(){
    registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

    IntStream.range(0, 250)
        .mapToObj(i -> "counter" + i)
        .forEach(registry::counter);
    IntStream.range(0, 250)
        .mapToObj(i -> "gauge" + i)
        .map(Counter::builder)
        .forEach(counterBuilder -> counterBuilder.register(registry));

    try {
      server = HttpServer.create(new InetSocketAddress(8080), 0);
      server.createContext("/", httpExchange -> {
        String response = registry.scrape();
        httpExchange.sendResponseHeaders(200, response.getBytes().length);
        try (OutputStream os = httpExchange.getResponseBody()) {
          os.write(response.getBytes());
        }
      });

      new Thread(server::start).start();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @TearDown
  public void stopSampling() {
    server.stop(1); // FIXME I have no idea what this number does
    registry.close();
  }

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

}