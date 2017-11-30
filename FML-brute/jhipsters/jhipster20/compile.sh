#!/bin/bash

sudo service postgresql start
psql -U postgres <<EOF
drop database if exists  jhipster;
create role jhipster login;
create database jhipster;
\q
EOF
./gradlew compileJava>> compile.log 2>&1