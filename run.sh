#!/bin/bash

# Function to check service health
wait_for_services() {
  echo "Waiting for all services to become healthy..."
  while ! docker compose ps | grep -q "healthy"; do
    echo -n "."
    sleep 2
  done
  echo -e "\nAll services are healthy."
}

# Function to monitor Redis keys
monitor_redis_keys() {
  echo -e "\nMonitoring Redis keys. Press Ctrl+C to stop...\n"
  while true; do
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] Redis Key Monitoring:"
    for service in Scala Python Java; do
      lowercase_service=$(echo "$service" | tr '[:upper:]' '[:lower:]')  # Convert service name to lowercase
      echo "$service:"
      redis-cli get "${lowercase_service}_total_messages" | awk '{printf "  Total Messages: %s\n", $0}'
      redis-cli get "${lowercase_service}_total_sum" | awk '{printf "  Total Sum: %s\n", $0}'
    done
    echo "-------------------------"
    sleep 5
  done
}

# Function to clean up and stop services
cleanup() {
  echo -e "\nStopping all services..."
  docker compose down
  echo "Done."
  exit 0
}

# Trap Ctrl+C (SIGINT) to stop monitoring and clean up
trap cleanup SIGINT

# Start the services
echo "Starting services..."
docker compose up -d

# Wait for services to be ready
wait_for_services

# Start monitoring Redis keys
monitor_redis_keys
