#!/bin/bash

service cassandra restart
sleep 45
export CASSANDRA_CONTACT_POINT="127.0.0.1"
cqlsh -f src/main/resources/config/cql/drop-keyspace.cql
cqlsh -f src/main/resources/config/cql/create-keyspace.cql
 cqlsh -f src/main/resources/config/cql/changelog/00000000000000_create-tables.cql -k jhipster
cqlsh -f src/main/resources/config/cql/changelog/00000000000001_insert_default_users.cql -k jhipster
./gradlew compileJava>> compile.log 2>&1