#!/bin/bash

./gradlew test >> test.log 2>&1
gulp test >> testKarmaJS.log 2>&1
