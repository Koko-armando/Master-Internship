#!/bin/bash

./gradlew bootRepackage -x test -Pprod buildDocker >> buildDocker.log 2>&1
docker-compose -f src/main/docker/app.yml up >> buildDocker.log 2>&1
