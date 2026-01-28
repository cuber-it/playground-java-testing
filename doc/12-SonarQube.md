# SonarQube - Code-Qualitaetsanalyse

## Was ist SonarQube?

Eine Plattform fuer **kontinuierliche Code-Qualitaet**. Analysiert Code auf:
- Bugs
- Vulnerabilities (Sicherheitsluecken)
- Code Smells (Wartbarkeitsprobleme)
- Duplications (Code-Duplikate)
- Coverage (Testabdeckung)

## SonarQube im Playground

```bash
# CI/CD Umgebung starten
cd ci
./start-ci.sh

# SonarQube oeffnen
open http://localhost:9000
# Login: admin / admin
```

**Was wurde gemacht:** SonarQube als Docker-Container in CI/CD.

**Warum:** Zentrale Code-Qualitaetsueberwachung.

## Dashboard verstehen

```
┌─────────────────────────────────────────────────────────┐
│  Playground                                    PASSED   │
├─────────────────────────────────────────────────────────┤
│  Bugs: 0    Vulnerabilities: 0    Code Smells: 5       │
│  Coverage: 82%    Duplications: 1.2%                    │
├─────────────────────────────────────────────────────────┤
│  Quality Gate: ✓ Passed                                 │
└─────────────────────────────────────────────────────────┘
```

### Metriken

| Metrik | Beschreibung | Ziel |
|--------|--------------|------|
| **Bugs** | Fehler im Code | 0 |
| **Vulnerabilities** | Sicherheitsprobleme | 0 |
| **Code Smells** | Wartbarkeitsprobleme | Minimieren |
| **Coverage** | Testabdeckung | > 80% |
| **Duplications** | Duplizierter Code | < 3% |

## Quality Gate

Definiert **Mindestanforderungen** fuer neuen Code:

```
Quality Gate: Sonar way
├── Coverage on new code > 80%
├── Duplicated Lines on new code < 3%
├── Maintainability Rating = A
├── Reliability Rating = A
└── Security Rating = A
```

**Passed:** Code erfuellt alle Kriterien
**Failed:** Mindestens ein Kriterium nicht erfuellt

## Analyse ausfuehren

### Lokal (ohne Jenkins)

```bash
mvn sonar:sonar \
    -Dsonar.host.url=http://localhost:9000 \
    -Dsonar.token=YOUR_TOKEN
```

### In Jenkins (automatisch)

```groovy
// Jenkinsfile
stage('SonarQube Analysis') {
    steps {
        withSonarQubeEnv('SonarQube') {
            sh 'mvn sonar:sonar -B'
        }
    }
}

stage('Quality Gate') {
    steps {
        timeout(time: 5, unit: 'MINUTES') {
            waitForQualityGate abortPipeline: true
        }
    }
}
```

**Was wurde gemacht:** SonarQube-Analyse in Jenkins-Pipeline.

**Warum:** Automatische Qualitaetspruefung bei jedem Build.

## Setup im Playground

### pom.xml

```xml
<properties>
    <sonar.java.coveragePlugin>jacoco</sonar.java.coveragePlugin>
    <sonar.coverage.jacoco.xmlReportPaths>
        ${project.build.directory}/site/jacoco/jacoco.xml
    </sonar.coverage.jacoco.xmlReportPaths>
</properties>

<plugin>
    <groupId>org.sonarsource.scanner.maven</groupId>
    <artifactId>sonar-maven-plugin</artifactId>
    <version>4.0.0.4121</version>
</plugin>
```

### Token erstellen

1. SonarQube oeffnen: http://localhost:9000
2. My Account → Security → Generate Token
3. Token in Jenkins hinterlegen

## Typische Findings

### Bug

```java
// NullPointerException moeglich
public String getTitle() {
    return title.toUpperCase();  // title kann null sein!
}

// Fix
public String getTitle() {
    return title != null ? title.toUpperCase() : "";
}
```

### Vulnerability

```java
// SQL Injection!
String query = "SELECT * FROM todos WHERE title = '" + userInput + "'";

// Fix: Prepared Statement
@Query("SELECT t FROM Todo t WHERE t.title = :title")
List<Todo> findByTitle(@Param("title") String title);
```

### Code Smell

```java
// Magic Number
if (todos.size() > 100) { ... }

// Fix: Konstante
private static final int MAX_TODOS = 100;
if (todos.size() > MAX_TODOS) { ... }
```

## Code-Analyse Konfiguration

### Dateien ausschliessen

```properties
# sonar-project.properties
sonar.exclusions=**/generated/**,**/dto/**
sonar.coverage.exclusions=**/config/**
```

### Regeln anpassen

1. Quality Profiles → Java
2. Regel suchen
3. Aktivieren/Deaktivieren/Severity aendern

## Integration mit IDE

### IntelliJ: SonarLint Plugin

1. Plugin installieren: SonarLint
2. Mit SonarQube verbinden
3. Echtzeit-Feedback beim Coden

## Security Hotspots

```
┌─────────────────────────────────────────────┐
│  Security Hotspot: Hardcoded Password       │
├─────────────────────────────────────────────┤
│  Risk: Credentials im Code                  │
│  Fix: Environment Variables nutzen          │
│  Review: Safe / To Review / Not Safe        │
└─────────────────────────────────────────────┘
```

**Was wurde gemacht:** Sicherheitsrelevante Stellen markiert.

**Warum:** Manuelle Pruefung erforderlich (nicht automatisch Bug).

## Best Practices

1. **Quality Gate erzwingen** - Build soll fehlschlagen bei Problemen
2. **Regelmaessig pruefen** - nicht nur vor Release
3. **Findings sofort beheben** - nicht ansammeln lassen
4. **Team einbinden** - Code Smells gemeinsam diskutieren
5. **IDE-Plugin nutzen** - Feedback vor Commit
