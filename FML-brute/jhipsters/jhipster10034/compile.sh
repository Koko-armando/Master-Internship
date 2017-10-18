#!/bin/bash

service postgresql start
psql -U postgres <<EOF
drop database jhipster;
create role jhipster login;
create database jhipster;
\q
EOF
mvn compile>> compile.log 2>&1