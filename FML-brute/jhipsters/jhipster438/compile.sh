#!/bin/bash

service mysql restart
sleep 45
mysql -u root <<EOF
drop database if exists jhipster;
FLUSH TABLES;
create database if not exists jhipster;
\q
EOF
./gradlew compileJava>> compile.log 2>&1