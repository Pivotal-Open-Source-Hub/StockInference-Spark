#!/bin/bash
$GEODE_HOME/bin/gfsh  -e "connect --locator=geode-server[10334]" -e "shutdown --include-locators=true" -e "exit"
