#!/bin/bash

gfsh -e "connect --locator=192.168.56.10[10334]" -e "deploy --jar=/home/vagrant/incubator-geode/gemfire-spark-connector/gemfire-functions/target/scala-2.10/gemfire-functions_2.10-0.5.0.jar"
