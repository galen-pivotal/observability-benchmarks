package org.apache.geode.observability.benchmarks.sampling;

import static java.lang.Math.abs;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
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
 * /**
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
  public void setup()
      throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, IOException,
      KeyManagementException, KeyStoreException {
    registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

    Map<String, Counter> theCounterMap = new HashMap<>();

    for (int i = 0; i < numberOfMeters; i++) {
      String meterName = "micrometer.counter" + i;
      theCounterMap.put(meterName, Counter.builder(meterName).register(registry));
    }

    try {
      // setup the socket address
      InetSocketAddress address = new InetSocketAddress(8000);

      // initialise the HTTPS server
      HttpsServer httpsServer = HttpsServer.create(address, 0);
      SSLContext sslContext = SSLContext.getInstance("TLS");

      // initialise the keystore
      char[] password = "password".toCharArray();
      KeyStore ks = KeyStore.getInstance("JKS");
      FileInputStream fis = new FileInputStream("/Users/mhanson/testkey.jks");
      ks.load(fis, password);

      // setup the key manager factory
      KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
      kmf.init(ks, password);

      // setup the trust manager factory
      TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
      tmf.init(ks);

      // setup the HTTPS context and parameters
      sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
      httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
        public void configure(HttpsParameters params) {
          try {
            // initialise the SSL context
            SSLContext c = getSSLContext();
            SSLEngine engine = c.createSSLEngine();
            params.setNeedClientAuth(false);
            params.setCipherSuites(engine.getEnabledCipherSuites());
            params.setProtocols(engine.getEnabledProtocols());

            // Set the SSL parameters
            SSLParameters sslParameters = c.getSupportedSSLParameters();
            params.setSSLParameters(sslParameters);

          } catch (Exception ex) {
            System.out.println("Failed to create HTTPS port");
          }
        }
      });

      httpsServer.createContext("/", t -> {

        String response = registry.scrape();
        t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        t.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        System.out.println("Got a callback");
        os.close();
      });
      httpsServer.setExecutor(null); // creates a default executor
      httpsServer.start();
      System.out.println("httpsServer started on port 8081");

      stopIncrementingThread = new AtomicBoolean(false);
      Random r = new Random();
      Thread incrementingThread = new Thread(() -> {
        while (!stopIncrementingThread.get()) {
          for (Map.Entry<String, Counter> entry : theCounterMap.entrySet()) {
            entry.getValue().increment();
            int value = abs(r.nextInt()) % 2;
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

    } catch (Exception exception) {
      System.out.println("Failed to create HTTPS server on port " + 443 + " of localhost");
      throw exception;
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