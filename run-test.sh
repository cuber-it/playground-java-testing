#!/bin/bash
#
# Fuehrt einen einzelnen Test aus
#
# Verwendung:
#   ./run-test.sh TodoUnitTest              # Klasse
#   ./run-test.sh TodoUnitTest#markDone*    # Einzelne Methode
#   ./run-test.sh *Selenium*                # Pattern
#
# Beispiele:
#   ./run-test.sh TodoServiceTest
#   ./run-test.sh TodoSeleniumTest#homePage_showsTitle
#   ./run-test.sh "de.training.playground.unit.*"
#

if [ -z "$1" ]; then
    echo "Verwendung: $0 <Testname>"
    echo ""
    echo "Beispiele:"
    echo "  $0 TodoUnitTest                    # Ganze Testklasse"
    echo "  $0 TodoUnitTest#markDone*          # Einzelne Methode"
    echo "  $0 '*Selenium*'                    # Pattern (in Quotes!)"
    echo "  $0 'de.training.playground.unit.*' # Package"
    exit 1
fi

cd "$(dirname "$0")" || exit 1

mvn test -Dtest="$1" -DfailIfNoTests=false
