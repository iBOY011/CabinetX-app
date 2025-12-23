#!/usr/bin/env bash
set -euo pipefail

# List of services that need databases (excluding config-server, discovery, gateway)
declare -a services=(
  "analytics"
  "appointment"
  "billing"
  "chatbot"
  "clinic"
  "consultation"
  "medical_record"
  "medication"
  "notification"
  "patient"
  "payment"
  "prescription"
  "queue"
  "user"
)

echo "=========================================="
echo "Creating databases for CabinetX services"
echo "=========================================="

for svc in "${services[@]}"; do
  db="${svc}_db"
  role="${svc}_user"
  
  # Convert service name to env var format (e.g., medical_record -> MEDICAL_RECORD_PASSWORD)
  pass_var="$(echo "${svc}_PASSWORD" | tr '[:lower:]' '[:upper:]')"
  
  # Get password from environment variable
  pass="${!pass_var:-default_change_me}"
  
  echo "Setting up: $db (user: $role)"
  
  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname postgres <<-EOSQL
    -- Create role if not exists
    DO \$\$
    BEGIN
      IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = '${role}') THEN
        CREATE ROLE ${role} LOGIN PASSWORD '${pass}';
        RAISE NOTICE 'Created role: ${role}';
      ELSE
        RAISE NOTICE 'Role already exists: ${role}';
      END IF;
    END
    \$\$;

    -- Create database if not exists
    SELECT 'CREATE DATABASE ${db} OWNER ${role} ENCODING ''UTF8'' LC_COLLATE ''en_US.utf8'' LC_CTYPE ''en_US.utf8'''
    WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = '${db}')\gexec

    -- Grant privileges
    GRANT ALL PRIVILEGES ON DATABASE ${db} TO ${role};
EOSQL

  echo "✓ Completed: $db"
  echo ""
done

echo "=========================================="
echo "Database provisioning completed!"
echo "=========================================="
