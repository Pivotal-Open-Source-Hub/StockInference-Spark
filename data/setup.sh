#!/bin/bash

SERVER=`docker ps |grep -i startServer | awk '{print $1}'`

echo Using Geode container ID: $SERVER

sed "s/CONTAINER_ID/${SERVER}/g" setup.gfsh.template > setup.gfsh  

docker exec -t $SERVER gfsh run --file=data/setup.gfsh



