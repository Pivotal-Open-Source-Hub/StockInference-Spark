#!/bin/bash

BOOT2DOCKER_IP=`boot2docker ip`
echo boot2docker ip: $BOOT2DOCKER_IP

sed "s/BOOT2DOCKER_IP/${BOOT2DOCKER_IP}/g" src/main/resources/client.xml.template > src/main/resources/client.xml

./gradlew run
