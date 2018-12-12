package org.apache.geode.observability.states;

import java.util.concurrent.atomic.AtomicInteger;

import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Thread)
public class KeysAndValues {
  private final static AtomicInteger seed = new AtomicInteger();
  public Object key = seed.getAndIncrement();
  public Object value = "value";
}
