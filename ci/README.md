# CI/CD Umgebung

Dockerisierte CI/CD-Pipeline mit Jenkins, SonarQube, JaCoCo und Allure.

## Schnellstart

```bash
# CI/CD starten
./start-ci.sh

# CI/CD stoppen
./stop-ci.sh
```

## Services

| Service   | URL                    | Credentials        |
|-----------|------------------------|--------------------|
| Jenkins   | http://localhost:8082  | admin / admin123   |
| SonarQube | http://localhost:9000  | admin / admin      |

## Ersteinrichtung

### 1. SonarQube Token erstellen

1. SonarQube oeffnen: http://localhost:9000
2. Login mit admin/admin (Passwort aendern)
3. My Account -> Security -> Generate Token
4. Token kopieren

### 2. Jenkins konfigurieren

1. Jenkins oeffnen: http://localhost:8082
2. Login mit admin/admin123

**SonarQube Credentials anlegen:**
1. Manage Jenkins -> Credentials -> System -> Global credentials
2. Add Credentials:
   - Kind: Secret text
   - Secret: (SonarQube Token einfuegen)
   - ID: sonarqube-token
   - Description: SonarQube Token

**SonarQube Server konfigurieren:**
1. Manage Jenkins -> System -> SonarQube servers
2. Name: SonarQube
3. Server URL: http://sonarqube:9000
4. Server authentication token: sonarqube-token

**Allure Commandline installieren:**
1. Manage Jenkins -> Tools -> Allure Commandline
2. Add Allure Commandline
3. Name: Allure
4. Install automatically: aktivieren

### 3. Pipeline Job erstellen

1. New Item -> Pipeline -> Name: "playground"
2. Pipeline Definition: Pipeline script from SCM
3. SCM: Git
4. Repository URL:
   - Lokal: /workspace (Volume mappen)
   - GitHub: https://github.com/user/playground.git
5. Branch: */main
6. Script Path: Jenkinsfile
7. Save

### 4. Build starten

1. Job "playground" oeffnen
2. "Build Now" klicken

## Pipeline Stages

```
Checkout -> Build -> Unit Tests -> Integration Tests -> REST Tests
    -> All Tests + Coverage -> SonarQube Analysis -> Quality Gate
    -> Package -> Docker Build
```

## Reports

### Allure Report
- Nach jedem Build verfuegbar
- Klick auf "Allure Report" im Build
- Drilldown zu einzelnen Tests

### JaCoCo Coverage
- Coverage-Trend im Job
- Detaillierter Report im Build

### SonarQube
- Code Quality Dashboard
- Security Vulnerabilities
- Code Smells
- Coverage Integration

## Lokales Projekt in Jenkins testen

Um das lokale Projekt in Jenkins zu testen, das Projekt als Volume mappen:

```yaml
# In docker-compose.yml bei jenkins hinzufuegen:
volumes:
  - ../:/workspace/playground:ro
```

Dann in Jenkins als Repository URL: `file:///workspace/playground`

## Troubleshooting

### Jenkins startet nicht
```bash
docker logs jenkins
```

### SonarQube startet nicht
```bash
# Elasticsearch braucht mehr Memory
sudo sysctl -w vm.max_map_count=262144

docker logs sonarqube
```

### Plugins fehlen
```bash
# Container neu bauen
docker compose up -d --build --force-recreate jenkins
```

## Daten loeschen

```bash
# Alles loeschen inkl. Volumes
docker compose down -v
```
