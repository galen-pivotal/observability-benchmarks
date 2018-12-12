#!/usr/bin/env bash

# This script does a full run of the Impact benchmark.

set -eux
mkdir -p results

# Parameters specific to the "impact" benchmark
number_of_meters=1500
sampling_interval=PT1S

# Run a full benchmark with one warmup iteration
number_of_forks=1
number_of_iterations=10
iteration_length=30m
number_of_warmup_iterations=1

# There's no way to parameterize the number of put threads, so we'll run the benchmark in a loop,
# each time with a different thread count.

for threads in 1 100; do
  java -jar target/benchmarks.jar \
    -i ${number_of_iterations} \
    -r ${iteration_length} \
    -wi ${number_of_warmup_iterations} \
    -psamplingInterval=${sampling_interval} \
    -pnumberOfMeters=${number_of_meters} \
    -t ${threads} \
    -f ${number_of_forks} \
    -rf csv -rff results/${threads}-thread-results.csv \
    org.apache.geode.observability.benchmarks.impact.ImpactOfMicrometerSamplingOnPutThroughput \
    | tee -a results/output.txt
done
