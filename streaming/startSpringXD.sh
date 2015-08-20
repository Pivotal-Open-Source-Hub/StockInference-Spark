#!/bin/bash

if pgrep -f "xd-singlenode" -l > /dev/null
then
	echo "SpringXD already RUNNING - PID:" `pgrep -f "xd-singlenode"`
	exit
fi

nohup xd-singlenode &

echo "Starting..."
until nc -z -w5 localhost 9393
do
  echo -ne "."
  sleep 2
done
