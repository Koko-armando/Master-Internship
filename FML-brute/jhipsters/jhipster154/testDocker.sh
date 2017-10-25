#!/bin/bash

xvfb-run gulp protractor >> testDockerProtractor.log 2>&1
./mvnw test >> cucumberDocker.log 2>&1
