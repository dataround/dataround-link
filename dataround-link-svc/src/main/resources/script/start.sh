#!/usr/bin/env bash
BIN_PATH=$(dirname $0)
BASE_PATH=$(cd $(dirname $BIN_PATH); pwd)

# Check for profile && foreground
SPRING_PROFILE="prod"
FOREGROUND_MODE=false

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --foreground)
            FOREGROUND_MODE=true
            shift
            ;;
        --spring.profiles.active=*)
            SPRING_PROFILE="${1#*=}"
            shift
            ;;
        *)
            shift
            ;;
    esac
done

echo "Prepare to start dataround link services..."

# Create logs directory
LOG_PATH=$BASE_PATH/logs
if [ ! -d $LOG_PATH ]; then
  mkdir -p $LOG_PATH
fi

# Check JAVA_HOME
if [ -z "$JAVA_HOME" ]; then
  echo "JAVA_HOME is not set"
  exit 1
else
  echo "JAVA_HOME is set to: $JAVA_HOME"
fi

# Configure SeaTunnel environment variables
export SEATUNNEL_HOME=$BASE_PATH/seatunnel
export SEATUNNEL_ENGINE=${SEATUNNEL_ENGINE:-"seatunnel"}
export SEATUNNEL_API_PORT=${SEATUNNEL_API_PORT:-8080}

echo "Starting seatunnel service on port $SEATUNNEL_API_PORT..."
# Start SeaTunnel cluster
cd $SEATUNNEL_HOME
nohup ./bin/seatunnel-cluster.sh > $LOG_PATH/seatunnel.log 2>&1 &
SEATUNNEL_PID=$!
echo $SEATUNNEL_PID > $LOG_PATH/seatunnel.pid
echo "seatunnel cluster started with PID: $SEATUNNEL_PID"

# Wait for seatunnel to start (check API availability)
echo "Waiting for seatunnel to start..."
for i in {1..10}; do
    if curl -s -f http://localhost:$SEATUNNEL_API_PORT/hazelcast/rest/maps/system || [ $? -eq 0 ]; then
        echo "seatunnel is ready!"
        break
    fi
    if [ $i -eq 10 ]; then
        echo "Warning: seatunnel may not have started properly"
    fi
    sleep 2
done

echo "Starting dataround link service on port $DATALINK_PORT..."
cd $BASE_PATH
JAVA_OPTS="-Ddataround.link.homeDir=$BASE_PATH" 

if [ "$FOREGROUND_MODE" = true ]; then
    # Run in foreground for Docker
    $JAVA_HOME/bin/java $JAVA_OPTS -jar $BASE_PATH/lib/dataround-link-*.jar \
      --spring.config.location=$BASE_PATH/conf/ \
      --spring.profiles.active=$SPRING_PROFILE 
else
    # Run in background (default behavior)
    nohup $JAVA_HOME/bin/java $JAVA_OPTS -jar $BASE_PATH/lib/dataround-link-*.jar \
      --spring.config.location=$BASE_PATH/conf/ \
      --spring.profiles.active=$SPRING_PROFILE > $LOG_PATH/datalink.log 2>&1 &
    DATALINK_PID=$!
    echo $DATALINK_PID > $LOG_PATH/datalink.pid
    echo "Dataround link service started with PID: $DATALINK_PID"
    echo "All services started successfully!"
    echo "Dataround link: http://localhost:$DATALINK_PORT/datalink"
fi