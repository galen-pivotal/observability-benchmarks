# Observability Benchmarks

Comparing Geode statistics to Micrometer meters.

## Building

Build with maven:

> `mvn clean package`

## Running Benchmarks from the Command Line

The basic command line format:

> `java -jar target/benchmarks.jar [regex*] [options]`

For example:

> `java -jar target/benchmarks.jar -i 100 -r 1m -wi 1 org.apache.geode.observability.benchmarks.meters.*`

To get help:

> `java -jar target/benchmarks.jar -h`

Also see below for command line options.

To list all benchmarks:

> `java -jar target/benchmarks.jar -l`

To list all benchmarks and their parameters:
> `java -jar target/benchmarks.jar -lp`

### Common Command Line Options

```
Measurement
-i n	: # iterations
-r dur	: Duration of each iteration (e.g. 10m)
-to dur	: Timeout for each iteration

Benchmark Parameters
-p name=value,value,...

Warmup
-wi	: # warmup iterations

Output
-o f	: Redirect output to file f
-v mode	: Verbosity mode (SILENT, NORMAL, EXTRA)
-rff f	: Write machine readable results to f
-rf fmt	: Use fmt to format machine readable results

Threads and JVMs
-t n	: # threads
-f n	: # forks

Help
-h	: display help
-l	: list benchmarks and exit
-lp	: list benchmarks and parameters, then exit

```
To see additional command line options, run

> `java -jar target/benchmarks.jar -h`
 
## Running Benchmarks in IntelliJ

First, install the JMH plugin.
You'll find it in the "Marketplace" section of the plugins.

Right-click a benchmark class or method,
then select one of the `Run` options from the menu.

Edit the run configuration's _Program arguments_ field
to specify command line arguments,
the same as if you were running on the command line.
