#!/bin/bash

shopt -s extglob
cd src/test/gatling/simulations
rm !(E*)
cd ../../../..
printf 'empadlew gatlingRun -x cleanResources >> testDockerGatling.log 2>&1
./gradlew test >> cucumberDocker.log 2>&1