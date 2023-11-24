#!/usr/bin/env bash
mvn clean
mvn install
./kill_all.sh production
./run_production.sh &
