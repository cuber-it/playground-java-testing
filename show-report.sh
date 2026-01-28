#!/bin/bash
#
# Zeigt den Allure-Report an
#
# Verwendung:
#   ./show-report.sh          # Lokaler Live-Server
#   ./show-report.sh --static # Lokaler statischer Report
#   ./show-report.sh jenkins  # Jenkins Allure Report (letzter Build)
#

cd "$(dirname "$0")" || exit 1

if [ "$1" = "jenkins" ]; then
    BUILD="${2:-lastBuild}"
    URL="http://localhost:8082/job/playground-pipeline/$BUILD/allure/"
    echo "Oeffne Jenkins Allure Report (Build $BUILD): $URL"
    xdg-open "$URL" 2>/dev/null || open "$URL" 2>/dev/null || echo "$URL"
    exit 0
fi

RESULTS_DIR="target/allure-results"

if [ ! -d "$RESULTS_DIR" ]; then
    echo "Keine Allure-Results gefunden in $RESULTS_DIR"
    echo "Fuehre zuerst 'mvn test' oder './run-test.sh <Test>' aus."
    exit 1
fi

if [ "$1" = "--static" ]; then
    echo "Generiere statischen Report..."
    mvn allure:report -q
    REPORT="target/site/allure-maven-plugin/index.html"
    if [ -f "$REPORT" ]; then
        echo "Oeffne $REPORT"
        xdg-open "$REPORT" 2>/dev/null || open "$REPORT" 2>/dev/null || echo "Report: $REPORT"
    fi
else
    echo "Starte Allure Live-Server (Ctrl+C zum Beenden)..."
    mvn allure:serve
fi
