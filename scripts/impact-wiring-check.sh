#!/usr/bin/env bash

# This script is intended to run the Impact benchmark very quickly, for the purpose of making sure
# it's wired up correctly. The resulting measurements are not meaningful in any way.

# Parameters specific to the "impact" benchmark
number_of_meters=1500
sampling_interval=PT1S

number_of_iterations=1
iteration_length=5
iteration_unit=s

number_of_warmups=1
warmup_length=1
warmup_unit=s

number_of_forks=1

for number_of_threads in 4; do
  java -jar target/benchmarks.jar \
    -i ${number_of_iterations} \
    -r ${iteration_length}${iteration_unit} \
    -wi ${number_of_warmups} \
    -w ${warmup_length}${warmup_unit} \
    -to $((${iteration_length}*2))m \
    -psamplingInterval=${sampling_interval} \
    -pnumberOfMeters=${number_of_meters} \
    -t ${number_of_threads} \
    -f ${number_of_forks} \
    -rf text -rff results/${number_of_threads}-thread-results.txt \
    org.apache.geode.observability.benchmarks.impact.ImpactOfMicrometerSamplingOnPutThroughput \
    | tee -a results/output.txt
done
