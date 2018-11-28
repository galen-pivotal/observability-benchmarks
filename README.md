# Observability Benchmarks

Comparing Geode statistics to Micrometer meters.

## Run Configurations

**Quick Run:**
1 iteration, 100ms per iteration, no warmup

Program arguments: `-i 1 -r 100ms -wi 0`

**Medium Run**
1 iteration, 1s per iteration, no warmup

Program arguments: `-i 1 -r 1s -wi 0`

**Full Run:**
Defined by annotations in the benchmark code

Program arguments: <none>
