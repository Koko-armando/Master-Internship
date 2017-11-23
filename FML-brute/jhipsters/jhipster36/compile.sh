#!/bin/bash

sudo service mongodb start
./mvnw compile>> compile.log 2>&1