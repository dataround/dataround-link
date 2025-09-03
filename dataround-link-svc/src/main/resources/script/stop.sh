#!/usr/bin/env bash

BIN_PATH=$(dirname $0)
BASE_PATH=$(cd $(dirname $BIN_PATH); pwd)
LOG_PATH=$BASE_PATH/logs

echo "Stopping Dataround Link services..."

# Stop Dataround Link
if [ -f "$LOG_PATH/datalink.pid" ]; then
    DATALINK_PID=$(cat $LOG_PATH/datalink.pid)
    if ps -p $DATALINK_PID > /dev/null 2>&1; then
        kill $DATALINK_PID
        echo "Dataround Link (PID: $DATALINK_PID) stopped"
    else
        echo "Dataround Link process (PID: $DATALINK_PID) not found"
    fi
    rm -f $LOG_PATH/datalink.pid
else
    # Fallback: find by process name
    pid=`ps aux |grep "dataround-link" |grep -v grep |awk '{print $2}'`
    if [ ! -z "$pid" ]; then
        kill $pid
        echo "Dataround Link (PID: $pid) stopped"
    else
        echo "Dataround Link process not found"
    fi
fi

# Stop SeaTunnel
if [ -f "$LOG_PATH/seatunnel.pid" ]; then
    SEATUNNEL_PID=$(cat $LOG_PATH/seatunnel.pid)
    if ps -p $SEATUNNEL_PID > /dev/null 2>&1; then
        kill $SEATUNNEL_PID
        echo "SeaTunnel (PID: $SEATUNNEL_PID) stopped"
    else
        echo "SeaTunnel process (PID: $SEATUNNEL_PID) not found"
    fi
    rm -f $LOG_PATH/seatunnel.pid
else
    # Fallback: find by process name
    pid=`ps aux |grep "seatunnel" |grep -v grep |awk '{print $2}'`
    if [ ! -z "$pid" ]; then
        kill $pid
        echo "SeaTunnel (PID: $pid) stopped"
    else
        echo "SeaTunnel process not found"
    fi
fi

# Clean up any remaining Java processes related to SeaTunnel
pkill -f "seatunnel"

echo "All services stopped"