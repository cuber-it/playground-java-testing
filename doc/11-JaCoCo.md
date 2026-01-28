# JaCoCo - Code Coverage

## Was ist JaCoCo?

**Ja**va **Co**de **Co**verage - misst, welcher Code von Tests ausgefuehrt wird.

## Coverage-Metriken

| Metrik | Beschreibung |
|--------|--------------|
| **Line Coverage** | Prozent ausgefuehrter Codezeilen |
| **Branch Coverage** | Prozent ausgefuehrter if/else-Zweige |
| **Method Coverage** | Prozent aufgerufener Methoden |
| **Class Coverage** | Prozent getesteter Klassen |

## Setup im Playground

```xml
<!-- pom.xml -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.12</version>
    <executions>
        <!-- Agent vor Tests starten -->
        <execution>
            <id>prepare-agent</id>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <!-- Report nach Tests generieren -->
        <execution>
            <id>report</id>
            <phase>verify</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

**Was wurde gemacht:** JaCoCo-Plugin in Maven konfiguriert.

**Warum:** Automatische Coverage-Messung bei jedem Build.

## Report generieren

```bash
# Tests mit Coverage ausfuehren
mvn verify

# Report oeffnen
open target/site/jacoco/index.html
```

## Report verstehen

```
target/site/jacoco/
├── index.html              # Hauptseite
├── jacoco.xml              # Fuer CI/CD Tools
├── de.training.playground/
│   ├── index.html          # Package-Uebersicht
│   ├── Todo.html           # Klassen-Detail
│   └── TodoService.html
```

### Farben im Report

- **Gruen:** Vollstaendig getestet
- **Gelb:** Teilweise getestet (Branches)
- **Rot:** Nicht getestet

### Beispiel-Ansicht

```
Package                         | Line Cov | Branch Cov
--------------------------------|----------|----------
de.training.playground.entity   |    95%   |    80%
de.training.playground.service  |    88%   |    75%
de.training.playground.controller|   72%   |    60%
```

## Coverage im Detail

```java
// Todo.java - Coverage-Beispiel

public boolean isOverdue() {           // Zeile getestet?
    if (dueDate == null) {             // Branch 1: null
        return false;                  // Nicht getestet!
    }
    return dueDate.isBefore(           // Branch 2: nicht null
        LocalDate.now()                // Getestet
    );
}
```

**Gelb markiert:** if-Zweig fuer `dueDate == null` nicht getestet.

## Coverage-Schwellwerte

```xml
<!-- pom.xml: Minimum Coverage erzwingen -->
<execution>
    <id>check</id>
    <goals>
        <goal>check</goal>
    </goals>
    <configuration>
        <rules>
            <rule>
                <element>BUNDLE</element>
                <limits>
                    <limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.80</minimum>  <!-- 80% -->
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</execution>
```

**Was wurde gemacht:** Build schlaegt fehl unter 80% Coverage.

**Warum:** Qualitaet automatisch sicherstellen.

## Klassen/Packages ausschliessen

```xml
<configuration>
    <excludes>
        <exclude>**/config/**</exclude>
        <exclude>**/dto/**</exclude>
        <exclude>**/*Application.class</exclude>
    </excludes>
</configuration>
```

## Integration mit SonarQube

```xml
<!-- pom.xml Properties -->
<properties>
    <sonar.coverage.jacoco.xmlReportPaths>
        ${project.build.directory}/site/jacoco/jacoco.xml
    </sonar.coverage.jacoco.xmlReportPaths>
</properties>
```

**Was wurde gemacht:** SonarQube liest JaCoCo-Report.

**Warum:** Coverage-Daten in zentralem Dashboard.

## Integration mit Jenkins

```groovy
// Jenkinsfile
stage('Tests + Coverage') {
    steps {
        sh 'mvn verify'
    }
    post {
        always {
            jacoco(
                execPattern: '**/target/jacoco.exec',
                classPattern: '**/target/classes',
                sourcePattern: '**/src/main/java'
            )
        }
    }
}
```

**Was wurde gemacht:** JaCoCo-Plugin in Jenkins.

**Warum:** Coverage-Trend im Jenkins-Dashboard.

## Coverage verbessern

### 1. Fehlende Tests identifizieren
```bash
# Report generieren und rote Stellen finden
mvn verify
open target/site/jacoco/index.html
```

### 2. Branches abdecken
```java
// Vorher: nur positiver Fall getestet
@Test
void overdueWhenPastDue() {
    todo.setDueDate(LocalDate.now().minusDays(1));
    assertThat(todo.isOverdue()).isTrue();
}

// Nachher: auch Edge-Cases
@Test
void notOverdueWhenNoDueDate() {
    todo.setDueDate(null);
    assertThat(todo.isOverdue()).isFalse();
}

@Test
void notOverdueWhenFuture() {
    todo.setDueDate(LocalDate.now().plusDays(1));
    assertThat(todo.isOverdue()).isFalse();
}
```

### 3. Realistische Ziele setzen
- **80%** ist ein guter Richtwert
- **100%** ist selten sinnvoll
- **Kritische Pfade** muessen abgedeckt sein

## Best Practices

1. **Coverage ist Metrik, nicht Ziel** - hohe Coverage garantiert keine Qualitaet
2. **Branch Coverage** beachten, nicht nur Lines
3. **Sinnvolle Ausschluesse** - Config, DTOs, Generated Code
4. **In CI/CD integrieren** - frueh Feedback bekommen
5. **Trend beobachten** - nicht nur aktuellen Wert
