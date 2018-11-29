# Observability Benchmarks

Comparing Geode statistics to Micrometer meters.

## Running on the Command Line

- **Full Run:**
  Defined by annotations in the benchmark code
  - `java -jar ./target/benchmarks.jar`

- **Quick Run:**
  1 iteration, 100ms per iteration, no warmup
  - `java -jar ./target/benchmarks.jar` _other arguments TBD_
  - Note to self: Note: --iterations 1 --timeOnIteration 100ms --warmupIterations 0

- **Medium Run**
  1 iteration, 1s per iteration, no warmup
  - `java -jar ./target/benchmarks.jar` _other arguments TBD_

## Running in IntelliJ

Create run configurations, specifying the program arguments as follows.

- **Full Run:**
  Defined by annotations in the benchmark code
  - Do not specify program arguments.
- **Quick Run:**
  1 iteration, 100ms per iteration, no warmup
  - Program arguments: `-i 1 -r 100ms -wi 0`
- **Medium Run**
  1 iteration, 1s per iteration, no warmup
  - Program arguments: `-i 1 -r 1s -wi 0`

