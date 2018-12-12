#!/usr/bin/env bash

# This script runs the Impact benchmark a few times with short iterations.
# The resulting measurements are very, very rough.

set -eux
mkdir -p results

# Parameters specific to the "impact" benchmark
number_of_meters=1500
sampling_interval=PT1S

# Run a short benchmark with no warmup
number_of_iterations=5
iteration_length=10s
number_of_warmup_iterations=0
number_of_forks=1

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
    -f ${number_of_forks} \
    -rf text -rff results/${threads}-thread-results.txt \
    org.apache.geode.observability.benchmarks.impact.ImpactOfMicrometerSamplingOnPutThroughput \
    | tee -a results/output.txt
done
