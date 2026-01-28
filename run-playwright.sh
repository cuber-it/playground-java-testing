#!/bin/bash
# Playwright Tests mit oder ohne Browser ausfuehren

if [ "$1" = "show" ]; then
    echo "=== Playwright Tests mit Browser ==="
    mvn test -Dtest="**/*Playwright*" -Dplaywright.headless=false -B
else
    echo "=== Playwright Tests headless ==="
    mvn test -Dtest="**/*Playwright*" -B
fi
