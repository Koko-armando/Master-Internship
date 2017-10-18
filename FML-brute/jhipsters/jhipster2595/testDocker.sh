#!/bin/bash

./mvnw test >> cucumberDocker.log 2>&1
shopt -s extglob
cd src/test/gatling/simulations
rm !(E*)
cd ../../../..
./mvnw gatling:execute >> testDockerGatling.log 2>&1
