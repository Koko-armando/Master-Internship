#!/bin/bash

docker-compose -f src/main/docker/app.yml up >> buildDocker2.log 2>&1
