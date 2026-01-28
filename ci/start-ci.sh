#!/bin/bash
# CI/CD Umgebung starten

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "=== CI/CD Umgebung starten ==="
echo ""

# Docker Compose starten
docker compose up -d --build

echo ""
echo "=== Warte auf Services... ==="

# Warten auf SonarQube
echo -n "SonarQube: "
until curl -s http://localhost:9000/api/system/status | grep -q '"status":"UP"' 2>/dev/null; do
    echo -n "."
    sleep 5
done
echo " OK"

# Warten auf Jenkins
echo -n "Jenkins: "
until curl -s http://localhost:8082/login 2>/dev/null | grep -q "Jenkins" ; do
    echo -n "."
    sleep 5
done
echo " OK"

echo ""
echo "=== CI/CD Umgebung bereit ==="
echo ""
echo "Jenkins:   http://localhost:8082"
echo "           User: admin"
echo "           Pass: admin123"
echo ""
echo "SonarQube: http://localhost:9000"
echo "           User: admin"
echo "           Pass: admin (beim ersten Login aendern)"
echo ""
echo "=== Naechste Schritte ==="
echo "1. Jenkins oeffnen und einloggen"
echo "2. 'New Item' -> Pipeline -> 'playground' erstellen"
echo "3. Pipeline from SCM: Git, URL: file:///workspace/playground"
echo "   Oder: GitHub URL eintragen"
echo "4. SonarQube Token erstellen und in Jenkins eintragen"
echo ""
