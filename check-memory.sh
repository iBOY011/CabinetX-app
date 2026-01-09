#!/bin/bash

# Monitor memory usage of CabinetX services

echo "=== CabinetX Services Memory Usage ==="
echo ""

# Header
printf "%-30s %10s %10s %8s\n" "SERVICE" "MEM(MB)" "CPU(%)" "PID"
printf "%-30s %10s %10s %8s\n" "-------" "-------" "------" "---"

# Get all Java processes and calculate total
TOTAL_MEM=0

for service in discovery configuration gateway user clinic patient appointment queue consultation medical-record prescription medication notification chatbot analytics billing payment; do
    PID=$(pgrep -f "${service}-service.*jar")
    if [ ! -z "$PID" ]; then
        MEM=$(ps -p $PID -o rss= 2>/dev/null | awk '{print $1/1024}')
        CPU=$(ps -p $PID -o %cpu= 2>/dev/null | awk '{print $1}')
        
        if [ ! -z "$MEM" ]; then
            printf "%-30s %10.0f %10s %8s\n" "${service}-service" "$MEM" "$CPU" "$PID"
            TOTAL_MEM=$(echo "$TOTAL_MEM + $MEM" | bc)
        fi
    fi
done

echo ""
printf "%-30s %10.0f MB\n" "TOTAL" "$TOTAL_MEM"
echo ""

# System memory info
echo "=== System Memory ==="
free -h | grep -E 'Mem|Swap'
