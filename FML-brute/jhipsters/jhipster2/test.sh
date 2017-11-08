#!/bin/bash

./gradlew test >> cucumber.log 2>&1
xvfb-run gulp protractor >> testProtractor.log 2>&1
