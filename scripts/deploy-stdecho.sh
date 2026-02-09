#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
WASM_FILE="${1:-src/test/resources/wasm/stdecho.wasm}"

if [ ! -f "$WASM_FILE" ]; then
  echo "Error: $WASM_FILE not found" >&2
  exit 1
fi

TMPFILE=$(mktemp)
trap 'rm -f "$TMPFILE"' EXIT

WASM_B64=$(base64 < "$WASM_FILE")
printf '{"name":"stdecho","description":"Echoes stdin to stdout","wasmBytes":"%s"}' "$WASM_B64" > "$TMPFILE"

RESPONSE=$(curl -s -w "\n%{http_code}" \
  -X POST "$BASE_URL/api/v1/functions" \
  -H "Content-Type: application/json" \
  -d @"$TMPFILE")

HTTP_CODE=$(echo "$RESPONSE" | tail -1)
BODY=$(echo "$RESPONSE" | sed '$d')

echo "HTTP $HTTP_CODE"
echo "$BODY"

if [ "$HTTP_CODE" = "201" ]; then
  FUNC_ID=$(echo "$BODY" | grep -o '"id":"[^"]*"' | head -1 | cut -d'"' -f4)
  echo ""
  echo "Invoke with:"
  echo "  echo 'hello' | curl -s -X POST $BASE_URL/invoke/$FUNC_ID -H 'Content-Type: application/octet-stream' --data-binary @-"
fi
