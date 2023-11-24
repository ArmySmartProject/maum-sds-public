#!/usr/bin/env bash
mvn clean
mvn install
./kill_all.sh
./run_all.sh &
