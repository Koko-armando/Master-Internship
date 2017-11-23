#!/bin/bash

cp ../../jhipster-jdl-mongodb.jh .
echo "a" | jhipster:import-jdl jhipster-jdl.jh >> generateJDL.log 2>&1
