#!/bin/bash

service mongodb start
./gradlew compileJava>> compile.log 2>&1