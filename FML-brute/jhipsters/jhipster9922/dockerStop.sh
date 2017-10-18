#!/bin/bash

docker-compose -f src/main/docker/app.yml stop >> dockerStop.log 2>&1
docker stop jhipster-mysql >> dockerStop.log 2>&1
docker rm $(docker ps -a -q) >> dockerStop.log 2>&1
docker rmi $(docker images -q) >> dockerStop.log 2>&1
