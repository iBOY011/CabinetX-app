#!/bin/bash

# CabinetX Microservices Management Script
# A fully customizable and dynamic script to start, stop, monitor, and restart microservices

echo "🚀 Starting CabinetX Microservices Management..."
echo "==============================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Memory optimization settings
SMALL_SERVICE_OPTS="-Xms128m -Xmx256m -XX:MaxMetaspaceSize=128m"
MEDIUM_SERVICE_OPTS="-Xms256m -Xmx512m -XX:MaxMetaspaceSize=256m"
LARGE_SERVICE_OPTS="-Xms512m -Xmx1024m -XX:MaxMetaspaceSize=256m"
GATEWAY_SERVICE_OPTS="-Xms256m -Xmx512m -XX:MaxMetaspaceSize=256m"
COMMON_OPTS="-XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UseStringDeduplication"

# Array to track service status with memory configuration
declare -A service_memory=(
    ["configuration-service"]="$SMALL_SERVICE_OPTS"
    ["discovery-service"]="$SMALL_SERVICE_OPTS"
    ["gateway-service"]="$GATEWAY_SERVICE_OPTS"
    ["analytics-service"]="$MEDIUM_SERVICE_OPTS"
    ["appointment-service"]="$LARGE_SERVICE_OPTS"
    ["billing-service"]="$MEDIUM_SERVICE_OPTS"
    ["chatbot-service"]="$MEDIUM_SERVICE_OPTS"
    ["clinic-service"]="$MEDIUM_SERVICE_OPTS"
    ["consultation-service"]="$LARGE_SERVICE_OPTS"
    ["medical-record-service"]="$LARGE_SERVICE_OPTS"
    ["medication-service"]="$MEDIUM_SERVICE_OPTS"
    ["notification-service"]="$SMALL_SERVICE_OPTS"
    ["patient-service"]="$MEDIUM_SERVICE_OPTS"
    ["payment-service"]="$MEDIUM_SERVICE_OPTS"
    ["prescription-service"]="$LARGE_SERVICE_OPTS"
    ["queue-service"]="$MEDIUM_SERVICE_OPTS"
    ["user-service"]="$MEDIUM_SERVICE_OPTS"
)

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

# Log directory
LOG_DIR="logs"
mkdir -p "$LOG_DIR"

# Function to check if a service is running
check_service() {
    local service_name=$1
    if pgrep -f "$service_name" > /dev/null; then
        echo -e "${GREEN}✅ $service_name is running${NC}"
        return 0
    else
        echo -e "${RED}❌ $service_name is not running${NC}"
        return 1
    fi
}

# Function to start a service with retries and memory optimization
start_service() {
    local service_name=$1
    local retries=3
    local count=0
    local memory_opts="${service_memory[$service_name]}"

    echo -e "${YELLOW}🔄 Starting $service_name with memory: ${memory_opts}${NC}"

    # Kill existing process if running
    pkill -f "${service_name}.*jar" 2>/dev/null

    while [[ $count -lt $retries ]]; do
        if [ -d "$service_name" ]; then
            cd "$service_name"
            
            # Build jar if doesn't exist
            if [ ! -f "target/${service_name}-0.0.1-SNAPSHOT.jar" ]; then
                echo -e "${YELLOW}📦 Building $service_name...${NC}"
                mvn clean package -DskipTests > /dev/null 2>&1
            fi
            
            # Start with memory optimization using JAR
            nohup java ${memory_opts} ${COMMON_OPTS} \
                -jar "target/${service_name}-0.0.1-SNAPSHOT.jar" \
                > "../$LOG_DIR/${service_name}.log" 2>&1 &
            
            sleep 5  # Wait for the service to start
            cd ..
            
            # Check if the service started successfully
            if check_service "$service_name"; then
                return 0
            fi
        else
            echo -e "${RED}❌ Directory for $service_name not found${NC}"
            return 1
        fi

        count=$((count + 1))
        echo -e "${YELLOW}⏳ Retry $count/$retries...${NC}"
        sleep 5
    done

    echo -e "${RED}❌ $service_name failed to start after $retries attempts${NC}"
    return 1
}

# Function to stop a service
stop_service() {
    local service_name=$1
    echo -e "${BLUE}🛑 Stopping $service_name...${NC}"
    pkill -f "$service_name" && echo -e "${GREEN}✅ $service_name stopped${NC}" || echo -e "${RED}❌ Failed to stop $service_name${NC}"
}

# Function to restart a service
restart_service() {
    local service_name=$1
    stop_service "$service_name"
    start_service "$service_name"
}

# Function to monitor service status in real-time
monitor_services() {
    echo -e "${BLUE}🔍 Monitoring all services in real-time...${NC}"
    while true; do
        for service_name in "${services[@]}"; do
            check_service "$service_name"
        done
        echo -e "${YELLOW}⏳ Waiting for next check...${NC}"
        sleep 10
    done
}

# Function to check the status of all services and report
check_all_services() {
    echo ""
    echo "🔍 Checking all services status..."
    echo "=============================="
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
        echo "💡 Check the logs in the '$LOG_DIR/' directory for more details:"
        for service in "${failed_services[@]}"; do
            echo "   - $LOG_DIR/${service}.log"
        done
    fi
}

# Function to display helpful URLs for the system
display_urls() {
    echo ""
    echo "🔗 Useful URLs:"
    echo "   - Eureka Dashboard: http://localhost:8761"
    echo "   - Config Server: http://localhost:8888"
    echo "   - Gateway: http://localhost:8080"
    echo ""
    echo "💾 Memory Usage:"
    echo "   Expected total: ~6-8GB (Small: 256MB, Medium: 512MB, Large: 1GB)"
}

# Function to show memory usage
show_memory_usage() {
    echo ""
    echo "📊 Current Memory Usage:"
    echo "========================"
    printf "%-30s %10s %10s\n" "SERVICE" "MEM(MB)" "CPU(%)"
    printf "%-30s %10s %10s\n" "-------" "-------" "------"
    
    local total_mem=0
    for service_name in "${services[@]}"; do
        local pid=$(pgrep -f "${service_name}.*jar")
        if [ ! -z "$pid" ]; then
            local mem=$(ps -p $pid -o rss= 2>/dev/null | awk '{print $1/1024}')
            local cpu=$(ps -p $pid -o %cpu= 2>/dev/null | awk '{print $1}')
            if [ ! -z "$mem" ]; then
                printf "%-30s %10.0f %10s\n" "$service_name" "$mem" "$cpu"
                total_mem=$(echo "$total_mem + $mem" | bc 2>/dev/null || echo "$total_mem")
            fi
        fi
    done
    echo ""
    printf "%-30s %10.0f MB\n" "TOTAL" "$total_mem"
}

# Interactive Menu
while true; do
    echo ""
    echo "==================== CabinetX Microservices Management ====================="
    echo "1. Start All Services (with memory optimization)"
    echo "2. Stop All Services"
    echo "3. Restart All Services"
    echo "4. Check All Services Status"
    echo "5. Monitor Services in Real-time"
    echo "6. Start a Specific Service"
    echo "7. Stop a Specific Service"
    echo "8. Restart a Specific Service"
    echo "9. View Logs"
    echo "10. Display Useful URLs"
    echo "11. Show Memory Usage"
    echo "12. Exit"
    read -p "Enter your choice (1-12): " choice

    case $choice in
        1)
            echo "📋 Starting all services..."
            for service in "${services[@]}"; do
                start_service "$service"
            done
            ;;
        2)
            echo "🛑 Stopping all services..."
            for service in "${services[@]}"; do
                stop_service "$service"
            done
            ;;
        3)
            echo "🔄 Restarting all services..."
            for service in "${services[@]}"; do
                restart_service "$service"
            done
            ;;
        4)
            check_all_services
            ;;
        5)
            monitor_services
            ;;
        6)
            read -p "Enter the service name to start: " service_name
            start_service "$service_name"
            ;;
        7)
            read -p "Enter the service name to stop: " service_name
            stop_service "$service_name"
            ;;
        8)
            read -p "Enter the service name to restart: " service_name
            restart_service "$service_name"
            ;;
        9)
            echo "📋 View logs for all services"
            for service in "${services[@]}"; do
                echo "==================== Logs for $service ===================="
                cat "$LOG_DIR/${service}.log" || echo "No log file found for $service"
            done
            ;;
        10)
            display_urls
            ;;
        11)
            show_memory_usage
            ;;
        12)
            echo "👋 Exiting..."
            exit 0
            ;;
        *)
            echo "Invalid choice, please try again."
            ;;
    esac
done
