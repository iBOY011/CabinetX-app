#!/bin/bash

# Script to start all CabinetX microservices
# Order: configuration-service -> discovery-service -> gateway-service -> other services

echo "🚀 Starting CabinetX Microservices..."
echo "====================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Array to track service status
declare -a services=(
    "configuration-service"
    "discovery-service"
    "gateway-service"
    "analytics-service"
    "appointment-service"
    "billing-service"
    "chatbot-service"
    "clinic-service"
    "consultation-service"
    "medical-record-service"
    "medication-service"
    "notification-service"
    "patient-service"
    "payment-service"
    "prescription-service"
    "queue-service"
    "user-service"
)

# Function to check if a service is running on a port
check_service() {
    local service_name=$1

    # Check if process is running
    if pgrep -f "$service_name" > /dev/null; then
        echo -e "${GREEN}✅ $service_name is running${NC}"
        return 0
    else
        echo -e "${RED}❌ $service_name is not running${NC}"
        return 1
    fi
}

# Function to start a service
start_service() {
    local service_name=$1

    echo -e "${YELLOW}🔄 Starting $service_name...${NC}"

    if [ -d "$service_name" ]; then
        cd "$service_name"
        nohup mvn spring-boot:run > "../logs/${service_name}.log" 2>&1 &
        sleep 5  # Wait a bit for service to start
        cd ..
    else
        echo -e "${RED}❌ Directory $service_name not found${NC}"
        return 1
    fi
}

# Create logs directory
mkdir -p logs

# Start services in order
echo "📋 Starting core services first..."
echo ""

# 1. Configuration Service
start_service "configuration-service"

# 2. Discovery Service
start_service "discovery-service"

# 3. Gateway Service
start_service "gateway-service"

echo ""
echo "📋 Starting business services..."
echo ""

# 4. Other services
start_service "analytics-service"
start_service "appointment-service"
start_service "billing-service"
start_service "chatbot-service"
start_service "clinic-service"
start_service "consultation-service"
start_service "medical-record-service"
start_service "medication-service"
start_service "notification-service"
start_service "patient-service"
start_service "payment-service"
start_service "prescription-service"
start_service "queue-service"
start_service "user-service"

echo ""
echo "⏳ Waiting for all services to fully start..."
sleep 30

echo ""
echo "🔍 Checking service status..."
echo "=============================="

# Check all services
failed_services=()
for service_name in "${services[@]}"; do
    if ! check_service "$service_name"; then
        failed_services+=("$service_name")
    fi
done

echo ""
echo "📊 Final Report"
echo "==============="

if [ ${#failed_services[@]} -eq 0 ]; then
    echo -e "${GREEN}🎉 All services are running successfully!${NC}"
else
    echo -e "${RED}⚠️  The following services failed to start:${NC}"
    for service in "${failed_services[@]}"; do
        echo -e "${RED}   - $service${NC}"
    done
    echo ""
    echo "💡 Check the logs in the 'logs/' directory for more details:"
    for service in "${failed_services[@]}"; do
        echo "   - logs/${service}.log"
    done
fi

echo ""
echo "🔗 Useful URLs:"
echo "   - Eureka Dashboard: http://localhost:8761"
echo "   - Config Server: http://localhost:8888"
echo "   - Gateway: http://localhost:8080"
echo ""
echo "📝 To stop all services: pkill -f 'spring-boot:run'"