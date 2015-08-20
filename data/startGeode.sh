#!/bin/sh

if pgrep -f "ServerLauncher" -l > /dev/null
then
	echo "Geode server already RUNNING - PID:" `pgrep -f "ServerLauncher"`
	exit
fi

gfsh run --file=setup.gfsh
