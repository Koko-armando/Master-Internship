#!/bin/bash

shopt -s extglob
cd src/test/gatling/simulations
rm !(E*)
cd ../../../..
./mvnw gatling:execute >> testGatling.log 2>&1
./mvnw test >> cucumber.log 2>&1