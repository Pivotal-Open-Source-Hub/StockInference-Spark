#!/bin/bash
PID=`pgrep -f "xd-singlenode"`
kill -9 $PID
wait $PID
echo "Done."
