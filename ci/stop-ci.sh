#!/bin/bash
# CI/CD Umgebung stoppen

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "=== CI/CD Umgebung stoppen ==="
docker compose down

echo "=== Gestoppt ==="
echo ""
echo "Zum vollstaendigen Loeschen (inkl. Daten):"
echo "  docker compose down -v"
