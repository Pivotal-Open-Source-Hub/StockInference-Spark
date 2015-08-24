#!/bin/bash

gfsh -e "connect --locator=geode-server[10334]" -e "deploy --jar=../lib/gemfire-functions_2.10-0.5.0.jar"
