#!/bin/bash

./mvnw test >> cucumber.log 2>&1
xvfb-run gulp protractor >> testProtractor.log 2>&1
shopt -s extglob
cd src/test/gatling/simulations
rm !(E*)
cd ../../../..
./mvnw gatling:execute >> testGatling.log 2>&1
