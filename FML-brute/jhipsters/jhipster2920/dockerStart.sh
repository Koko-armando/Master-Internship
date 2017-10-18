#!/bin/bash

docker-compose -f src/main/docker/cassandra.yml up jhipster-cassandra-migration >> cassandraMigration.log 2>&1./mvnw -DskipTests package -Pprod docker:build >> buildDocker.log 2>&1
docker-compose -f src/main/docker/app.yml up >> buildDocker.log 2>&1
