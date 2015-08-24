#!/bin/bash


nohup xd-singlenode &

echo "Starting..."
until nc -z -w5 localhost 9393
do
  echo -ne "."
  sleep 2
done
