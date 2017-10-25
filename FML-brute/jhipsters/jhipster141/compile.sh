#!/bin/bash

service mongodb start
mvn compile>> compile.log 2>&1