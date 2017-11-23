#!/bin/bash

./mvnw -DskipTests package -Pprod docker:build >> buildDocker.log 2>&1
docker-compose -f src/main/docker/app.yml up >> buildDocker.log 2>&1
