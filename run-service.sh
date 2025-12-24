#!/usr/bin/env bash
set -euo pipefail

# Extract module name from path or name
MODULE_ARG=${1:-gateway-service}
MODULE_NAME=$(basename "${MODULE_ARG%/}")

ENV_FILE=${2:-./env/${MODULE_NAME}.env}

if [ ! -f "$ENV_FILE" ]; then
  echo "Env file not found: $ENV_FILE"
  exit 1
fi

set -a
# shellcheck disable=SC1090
source "$ENV_FILE"
set +a

cd "$MODULE_ARG"
mvn spring-boot:run