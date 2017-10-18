#!/bin/bash

./gradlew test >> cucumber.log 2>&1
shopt -s extglob
cd src/test/gatling/simulations
rm !(E*)
cd ../../../..
printf 'empadlew gatlingRun -x cleanResources >> testGatling.log 2>&1
