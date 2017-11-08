#!/bin/bash

./gradlew test >> cucumberDocker.log 2>&1
xvfb-run gulp protractor >> testDockerProtractor.log 2>&1
