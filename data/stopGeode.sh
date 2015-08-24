#!/bin/bash
gfsh  -e "connect --locator=192.168.56.10[10334]" -e "shutdown --include-locators=true" -e "exit"
