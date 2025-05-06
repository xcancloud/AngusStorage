#!/bin/sh

# ---------------------------------------------------------------------------
# Stop script for the AngusStorage application (Initialize by Maven).
# Usage: ./shutdown-storage.sh
# Author: XiaoLong Liu
# ---------------------------------------------------------------------------

SLEEP=6
EXECUTABLE=@project.build.finalName@.jar

CURRENT_HOME=`dirname "$0"`
# Only set STORAGE_HOME if not already set
[ -z "$STORAGE_HOME" ] && STORAGE_HOME=`cd "$CURRENT_HOME" >/dev/null; pwd`
echo "Home Dir: $STORAGE_HOME"

STORAGE_PID="${STORAGE_HOME}/storage.pid"
if [ -f "$STORAGE_PID" ]; then
    if [ -s "$STORAGE_PID" ]; then
        kill -0 `cat "$STORAGE_PID"` >/dev/null 2>&1
        if [ $? -gt 0 ]; then
            echo "PID file found but no matching process was found."
        else
            PID=`cat "$STORAGE_PID"` # STORAGE process exists
            rm -f "$STORAGE_PID" >/dev/null 2>&1
        fi
    else
        echo "PID file is empty"
        rm -f "$STORAGE_PID" >/dev/null 2>&1
    fi
else
    echo "PID file not found"
fi

if [ -z "$PID" ]; then
    PID=`ps -ef |grep $EXECUTABLE |grep -v grep |awk '{print $2}'`
    if [ -z "$PID" ]; then
        echo "AngusStorage process not found, shutdown aborted."
        exit 0
    fi;
fi

echo "Attempting to stop the process through OS signal."
kill -15 $PID >/dev/null 2>&1
sleep 3

PID=`ps -ef |grep $EXECUTABLE |grep -v grep |awk '{print $2}'`
if [ ! -z "$PID" ]; then
    sleep $SLEEP
    kill -9 $PID >/dev/null 2>&1
    echo "AngusStorage process is killed, PID=$PID"
else
    echo "AngusStorage process is stopped"
fi;
