#!/bin/bash

docker-compose -f src/main/docker/cassandra.yml up jhipster-cassandra-migration >> cassandraMigration.log 2>&1./gradlew bootRepackage -x test -Pprod buildDocker >> buildDocker.log 2>&1
docker-compose -f src/main/docker/app.yml up >> buildDocker.log 2>&1
