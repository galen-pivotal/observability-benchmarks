#!/usr/bin/env bash

set -eux

# This script does a full run of the Impact benchmark.

# Parameters specific to the "impact" benchmark
number_of_meters=100,1500
sampling_interval=PT1S,PT30S

# Run a short benchmark with no warmup
number_of_iterations=10
iteration_length=10m
number_of_warmup_iterations=1

# There's no way to parameterize the number of put threads, so we'll run the benchmark in a loop,
# each time with a different thread count.

for threads in 1 100 max; do
  java -jar target/benchmarks.jar \
    -i ${number_of_iterations} \
    -r ${iteration_length} \
    -wi ${number_of_warmup_iterations} \
    -psamplingInterval=${sampling_interval} \
    -pnumberOfMeters=${number_of_meters} \
    -t ${threads} \
    -rf text -rff ${threads}-thread-results.txt \
    -prof stack \
    org.apache.geode.observability.benchmarks.impact.ImpactOfMicrometerSamplingOnPutThroughput \
    | tee -a output.txt
done
