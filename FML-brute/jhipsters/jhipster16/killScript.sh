#!/bin/bash

fuser -k 8080/tcp
fuser -k 8761/tcp
fuser -k 9999/tcp
