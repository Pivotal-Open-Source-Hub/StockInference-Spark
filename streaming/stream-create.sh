#!/bin/bash

XD_CONTAINER=`docker ps |grep -i xd-singlenode | awk '{print $1}'`

echo Using Spring XD container ID: $XD_CONTAINER

docker exec $XD_CONTAINER xd-shell --cmdfile streaming/stream-create.xd


