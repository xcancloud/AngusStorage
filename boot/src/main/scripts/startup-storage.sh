#!/bin/sh

# ---------------------------------------------------------------------------
# Start script for the AngusStorage application (Initialize by Maven).
# Usage: ./startup-storage.sh
# Author: XiaoLong Liu
# ---------------------------------------------------------------------------

CURRENT_HOME=`dirname "$0"`
# Only set STORAGE_HOME if not already set
[ -z "$STORAGE_HOME" ] && STORAGE_HOME=`cd "$CURRENT_HOME" >/dev/null; pwd`
echo "App Home: $STORAGE_HOME"

# Init java environment
. ./init-jdk.sh
if [ -z "${JAVA_HOME}" ]; then
  echo "JAVA_HOME is not set"
  exit 2
fi

# Check that target jar exists
EXECUTABLE=@project.build.finalName@.jar
if [ ! -x "$STORAGE_HOME"/"$EXECUTABLE" ]; then
    echo "Cannot find $STORAGE_HOME/$EXECUTABLE"
    echo "The jar file is absent or does not have execute permission"
    exit 1
fi

# Define config path
if [ -z "$STORAGE_CONF_DIR" ] ; then
    STORAGE_CONF_DIR="$STORAGE_HOME"/conf
    STORAGE_CONF_LOG_FILE="$STORAGE_CONF_DIR"/@archive.name@-logback.xml
    # Create config path
    # mkdir -p "$STORAGE_CONF_DIR"
fi
echo "Conf Dir: $STORAGE_CONF_DIR"

# Define the console log path for the storage
if [ -z "$STORAGE_LOG_DIR" ] ; then
    STORAGE_LOG_DIR="$STORAGE_HOME"/logs
    STORAGE_CONSOLE_LOG="$STORAGE_LOG_DIR"/@archive.name@-console.log
    # Create logs path
    mkdir -p "$STORAGE_LOG_DIR" && touch "$STORAGE_CONSOLE_LOG"
fi
echo "Logs Dir: $STORAGE_LOG_DIR"

# Define the java.io.tmpdir to use for storage
if [ -z "$STORAGE_TMPDIR" ] ; then
    STORAGE_TMPDIR="$STORAGE_HOME"/tmp
    # Create temp path
    mkdir -p "$STORAGE_TMPDIR"
fi
echo "Temp Dir: $STORAGE_TMPDIR"

# Check the process exists
running_check(){
    PID=`ps -ef |grep $EXECUTABLE |grep -v grep |awk '{print $2}'`
    if [ ! -z "${PID}" ]; then
        echo $PID > "$STORAGE_PID"
        echo "AngusStorage appears to still be running with PID $PID. Startup aborted."
        echo "If the following process is not a AngusStorage process, remove the PID file and try again:"
        ps -f -p $PID
        return 0
    else
        return 1
    fi;
}

# Find storage process PID
STORAGE_PID="${STORAGE_HOME}/storage.pid"
if [ -e "$STORAGE_PID" ]; then
    if [ -s "$STORAGE_PID" ]; then
        echo "Existing PID file found during AngusStorage startup."
        if [ -r "$STORAGE_PID" ]; then
            PID=`cat "$STORAGE_PID"`
            ps -p $PID >/dev/null 2>&1
            if [ $? -eq 0 ] ; then
                echo "AngusStorage appears to still be running with PID $PID. Startup aborted."
                echo "If the following process is not a AngusStorage process, remove the PID file and try again:"
                ps -f -p $PID
                exit 1
            else
                echo "AngusStorage process does not exist and removing stale PID file."
                running_check
                if [ $? -eq "0" ]; then
                    echo $PID > "$STORAGE_PID"
                    exit 1
                else
                    rm -f $STORAGE_PID >/dev/null 2>&1
                    if [ $? != 0 ]; then
                        if [ -w "$STORAGE_PID" ]; then
                            cat /dev/null > $STORAGE_PID
                        else
                            echo "Unable to remove stale PID file. Startup aborted."
                            exit 1
                        fi
                    fi
                fi
            fi
        else
            echo "Unable to read PID file. Startup aborted."
            exit 1
        fi
    else # storage.pid is empty
        running_check
        if [ $? -eq "0" ]; then
            echo $PID > "$STORAGE_PID"
            exit 1
        else
            rm -f "$STORAGE_PID" >/dev/null 2>&1
            if [ ! -w "$STORAGE_PID" ]; then
                echo "Unable to delete empty PID file. Startup aborted."
                exit 1
            fi
        fi
    fi
else
    running_check
    if [ $? -eq "0" ]; then
        echo "AngusStorage PID file($STORAGE_PID) is missing. Update PID"
        echo $PID > "$STORAGE_PID" # touch $STORAGE_PID
        exit 1
    fi
fi

# Run storage
JAVA_OPTS="-server -Xnoagent -Djava.SECURITY.egd=file:/dev/./urandom -Dio.netty.tryReflectionSetAccstorageible=true"
nohup ${JAVA_HOME}/bin/java -jar $JAVA_OPTS \
  -DHOME_DIR=$STORAGE_HOME \
  -DCONF_DIR=$STORAGE_CONF_DIR \
  -DLOGS_DIR=$STORAGE_LOG_DIR \
  -DPLUGIN_DIR=$STORAGE_HOME/plugins \
  -Dlogback.configurationFile=$STORAGE_CONF_LOG_FILE \
  -Djava.io.tmpdir=$STORAGE_TMPDIR \
 $STORAGE_HOME/$EXECUTABLE >> "$STORAGE_CONSOLE_LOG" 2>&1 &
echo $! > "$STORAGE_PID"

echo "AngusStorage started, PID=$!"
