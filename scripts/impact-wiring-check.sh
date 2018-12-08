#!/usr/bin/env bash

# This script is intended to run the Impact benchmark very quickly, for the purpose of making sure
# it's wired up correctly. The resulting measurements are not meaningful in any way.

# Parameters specific to the "impact" benchmark
number_of_meters=100
sampling_interval=PT1S

# Run a very, very quick benchmark with no warmup
number_of_iterations=1
iteration_length=5s
number_of_warmup_iterations=0
number_of_threads=1

java -jar target/benchmarks.jar \
  -i ${number_of_iterations} \
  -r ${iteration_length} \
  -wi ${number_of_warmup_iterations} \
  -psamplingInterval=${sampling_interval} \
  -pnumberOfMeters=${number_of_meters} \
  -t ${number_of_threads} \
  org.apache.geode.observability.benchmarks.impact.ImpactOfMicrometerSamplingOnPutThroughput
