package org.apache.geode.observability.benchmarks.sampling;

import static java.lang.Math.*;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Random;
import com.sun.net.httpserver.HttpServer;
import io.micrometer.core.instrument.Counter;
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
  private PrometheusMeterRegistry registry;
  private HttpServer server;
  private Thread incrementingThread;
  private AtomicBoolean stopIncrementingThread;


  @Setup
  public void setup(){
    registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

    Map<String, Counter> theCounterMap = new HashMap<>();

    for (int i = 0; i < numberOfMeters; i++)
    {
      String meterName = "micrometer.counter" + i;
      theCounterMap.put(meterName, Counter.builder(meterName).register(registry));
    }

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

      stopIncrementingThread = new AtomicBoolean(false);
      Random r = new Random();
      incrementingThread = new Thread(() -> {
        while (!stopIncrementingThread.get()) {
          for (Map.Entry<String, Counter> entry : theCounterMap.entrySet()) {
            entry.getValue().increment();
            int value =  abs(r.nextInt()) % 2;
            if (value == 1) {
              entry.getValue().increment(10.0);
            }
          }
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      });
      incrementingThread.start();

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @TearDown
  public void stopSampling() {
    server.stop(1); // FIXME I have no idea what this number does
    registry.close();
    stopIncrementingThread.set(true);
  }

  /**
   * The number of meters to register. Half will be counters and half will be gauges.
   */
  @Param("250")
  public int numberOfMeters;
}