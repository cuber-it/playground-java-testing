# Jenkins - CI/CD Pipeline

## Was ist Jenkins?

Ein **Automatisierungsserver** fuer Continuous Integration und Continuous Delivery.

## Jenkins im Playground

```bash
# CI/CD Umgebung starten
cd ci
./start-ci.sh

# Jenkins oeffnen
open http://localhost:8082
# Login: admin / admin123
```

**Was wurde gemacht:** Jenkins als Docker-Container mit allen Plugins.

**Warum:** Vollautomatische Build-Pipeline.

## Pipeline-Konzept

```
┌─────────┐   ┌─────────┐   ┌─────────┐   ┌─────────┐   ┌─────────┐
│Checkout │──>│  Build  │──>│  Test   │──>│ Analyse │──>│ Package │
└─────────┘   └─────────┘   └─────────┘   └─────────┘   └─────────┘
                                               │
                                               ▼
                                        ┌─────────────┐
                                        │ SonarQube   │
                                        │ JaCoCo      │
                                        │ Allure      │
                                        └─────────────┘
```

## Jenkinsfile im Playground

```groovy
// Jenkinsfile
pipeline {
    agent any

    tools {
        maven 'Maven-3.9'
        jdk 'JDK-21'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile -B'
            }
        }

        stage('Unit Tests') {
            steps {
                sh 'mvn test -Dtest="de.training.playground.unit.**" -B'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('All Tests + Coverage') {
            steps {
                sh 'mvn verify -B'
            }
            post {
                always {
                    jacoco(execPattern: '**/target/jacoco.exec')
                }
            }
        }

        stage('SonarQube') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn sonar:sonar -B'
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests -B'
            }
        }
    }

    post {
        always {
            allure results: [[path: 'target/allure-results']]
        }
    }
}
```

**Was wurde gemacht:** Deklarative Pipeline mit allen Stages.

**Warum:** Reproduzierbare, versionierte Build-Konfiguration.

## Pipeline-Syntax

### Stages und Steps

```groovy
stages {
    stage('Name') {
        steps {
            sh 'command'           // Shell-Befehl
            echo 'Message'         // Ausgabe
            script { /* Groovy */ } // Skript-Block
        }
    }
}
```

### Post-Actions

```groovy
post {
    always {
        // Immer ausfuehren
        cleanWs()
    }
    success {
        // Nur bei Erfolg
        echo 'Build successful!'
    }
    failure {
        // Nur bei Fehler
        echo 'Build failed!'
    }
}
```

### Bedingte Ausfuehrung

```groovy
stage('Docker') {
    when {
        branch 'main'              // Nur auf main-Branch
    }
    steps {
        sh 'docker build .'
    }
}

stage('Deploy') {
    when {
        expression { params.DEPLOY == true }
    }
    steps {
        // ...
    }
}
```

## Plugins im Playground

| Plugin | Funktion |
|--------|----------|
| **Git** | Source Code Management |
| **Maven** | Build-Tool Integration |
| **JaCoCo** | Coverage Reports |
| **SonarQube Scanner** | Code-Analyse |
| **Allure** | Test-Reports |
| **Blue Ocean** | Moderne UI |
| **Pipeline** | Jenkinsfile-Support |

## Job erstellen

### 1. Neuer Job

1. "New Item" klicken
2. Name: "playground"
3. Typ: "Pipeline"
4. "OK"

### 2. Pipeline konfigurieren

1. Pipeline → Definition: "Pipeline script from SCM"
2. SCM: Git
3. Repository URL: `file:///workspace/playground`
4. Branch: `*/master` oder `*/main`
5. Script Path: `Jenkinsfile`
6. "Save"

### 3. Build starten

1. "Build Now" klicken
2. Build-Verlauf beobachten
3. Console Output pruefen

## Build-Ergebnisse

### Console Output

```
[Pipeline] stage (Checkout)
[Pipeline] checkout
Cloning repository...
[Pipeline] stage (Build)
[Pipeline] sh
+ mvn clean compile -B
[INFO] BUILD SUCCESS
[Pipeline] stage (Unit Tests)
+ mvn test -Dtest="de.training.playground.unit.**" -B
Tests run: 50, Failures: 0, Errors: 0
[INFO] BUILD SUCCESS
```

### Test Results

- JUnit: Testfaelle und Fehler
- Allure: Detaillierter Report mit Drilldown
- JaCoCo: Coverage-Trend

### SonarQube Link

Nach der Analyse: Link zu SonarQube im Build

## Blue Ocean

Moderne Pipeline-Visualisierung:

```
http://localhost:8082/blue
```

```
┌──────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐
│ Checkout │──>│  Build   │──>│  Tests   │──>│ Package  │
│    ✓     │   │    ✓     │   │    ✓     │   │    ✓     │
└──────────┘   └──────────┘   └──────────┘   └──────────┘
```

## Troubleshooting

### Build schlaegt fehl

```bash
# Jenkins-Logs pruefen
docker logs jenkins

# In Container schauen
docker exec -it jenkins bash
```

### Tools nicht gefunden

1. Manage Jenkins → Tools
2. Maven/JDK konfigurieren
3. Namen muessen mit Jenkinsfile uebereinstimmen

### SonarQube-Fehler

1. Token pruefen
2. Server-URL pruefen (http://sonarqube:9000)
3. Netzwerk pruefen (ci_ci-network)

## Best Practices

1. **Jenkinsfile im Repo** - versioniert mit Code
2. **Schnelle Stages zuerst** - fruehes Feedback
3. **Parallele Stages** wo moeglich
4. **Artefakte archivieren** - JAR, Reports
5. **Workspace aufraeumen** - Speicher sparen
