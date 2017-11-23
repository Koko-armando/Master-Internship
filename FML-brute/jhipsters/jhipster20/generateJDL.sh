#!/bin/bash

cp ../../jhipster-jdl-cassandra.jh .
echo "a" | jhipster:import-jdl jhipster-jdl.jh >> generateJDL.log 2>&1
