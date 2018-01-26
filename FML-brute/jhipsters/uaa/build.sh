#!/bin/bash

docker-compose -f src/main/docker/mysql.yml up -d &

./mvnw&
