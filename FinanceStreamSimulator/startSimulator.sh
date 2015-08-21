#!/bin/bash

SIMULATOR_CONTAINER=`docker ps |grep -i java:7 | awk '{print $1}'`

echo Using Simulator container ID: $SIMULATOR_CONTAINER

docker exec $SIMULATOR_CONTAINER FinanceStreamSimulator/gradlew -p FinanceStreamSimulator run


