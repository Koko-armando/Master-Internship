#!/bin/bash

sudo service  mssql-server restart
sleep 45
sqlcmd -S localhost -U SA -P 'root4242.'<<EOF
drop database if exists jhipster;
  GO 
FLUSH TABLES;
  GO 
if not exists(SELECT 1 FROM sys.databases WHERE name = N'jhipster') create database jhipster;
 GO 
\q
EOF
./gradlew compileJava>> compile.log 2>&1