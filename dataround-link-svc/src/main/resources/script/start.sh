#!/usr/bin/env bash
BIN_PATH=$(dirname $0)
BASE_PATH=$(cd $(dirname $BIN_PATH); pwd)

echo "Prepare to start dataround link service..."
LOG_PATH=$BASE_PATH/logs
if [ ! -d $LOG_PATH ]; then
  mkdir -p $LOG_PATH
fi

if [ -z "$JAVA_HOME" ]; then
  echo "JAVA_HOME is not set"
  exit 1
else
  echo "JAVA_HOME is set to: $JAVA_HOME"
fi
JAVA_OPTS="" #"-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
nohup $JAVA_HOME/bin/java $JAVA_OPTS -jar $BASE_PATH/lib/dataround-link-1.0.jar \
      --spring.config.location=$BASE_PATH/conf/application.yaml > $LOG_PATH/datalink.log 2>&1 &
echo "dataround link service started."