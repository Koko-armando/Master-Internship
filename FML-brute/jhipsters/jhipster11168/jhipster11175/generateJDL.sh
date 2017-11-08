#!/bin/bash

cp ../../jhipster-jdl.jh .
echo "a" | yo jhipster:import-jdl jhipster-jdl.jh >> generateJDL.log 2>&1
