#!/bin/bash

service postgresql start
psql -U postgres <<EOF
drop database jhipster;
create role jhipster login;
create database jhipster;
\q
EOF
./gradlew compileJava>> compile.log 2>&1