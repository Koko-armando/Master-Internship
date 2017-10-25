#!/bin/bash

xvfb-run gulp protractor >> testProtractor.log 2>&1
./mvnw test >> cucumber.log 2>&1
