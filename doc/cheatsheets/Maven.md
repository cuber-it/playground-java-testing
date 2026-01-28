# Maven Cheatsheet

## Lifecycle Phasen

```
validate → compile → test → package → verify → install → deploy
```

## Häufige Befehle

```bash
# Kompilieren
mvn compile

# Tests ausführen
mvn test

# Tests überspringen
mvn package -DskipTests
mvn package -Dmaven.test.skip=true

# JAR/WAR erstellen
mvn package

# Mit Integration Tests
mvn verify

# Lokal installieren
mvn install

# Projekt aufräumen
mvn clean

# Kombiniert
mvn clean install
mvn clean verify -DskipTests
```

## Tests

```bash
# Alle Tests
mvn test

# Einzelne Testklasse
mvn test -Dtest=TodoTest

# Einzelne Methode
mvn test -Dtest=TodoTest#testMethod

# Pattern
mvn test -Dtest="*Unit*"
mvn test -Dtest="de.training.playground.unit.**"

# Mehrere
mvn test -Dtest="TodoTest,ServiceTest"

# Ausschließen
mvn test -Dtest="!*Slow*"
```

## Profile

```bash
# Profil aktivieren
mvn test -Pintegration
mvn package -Pprod

# Mehrere
mvn package -Pdev,local
```

## Properties

```bash
# Property setzen
mvn test -Dspring.profiles.active=test
mvn package -Dproject.version=1.0.0

# Verbose
mvn test -X
mvn test --debug
```

## Abhängigkeiten

```bash
# Dependency Tree
mvn dependency:tree

# Unused/Undeclared finden
mvn dependency:analyze

# Dependencies aktualisieren
mvn versions:display-dependency-updates
mvn versions:display-plugin-updates

# Download erzwingen
mvn dependency:purge-local-repository
```

## Plugins ausführen

```bash
# Spring Boot
mvn spring-boot:run
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# JaCoCo
mvn jacoco:report

# SonarQube
mvn sonar:sonar -Dsonar.host.url=http://localhost:9000

# Allure
mvn allure:serve
mvn allure:report

# Surefire Report
mvn surefire-report:report
```

## Wrapper

```bash
# Wrapper erzeugen
mvn wrapper:wrapper

# Mit Wrapper ausführen
./mvnw test
./mvnw clean install
```

## Offline / Repository

```bash
# Offline Modus
mvn -o test

# Repo leeren
rm -rf ~/.m2/repository/

# Nur Dependencies laden
mvn dependency:go-offline
```

## Nützliche Flags

```bash
-q          # Quiet (weniger Output)
-B          # Batch Mode (keine Interaktion)
-T 4        # 4 Threads parallel
-pl module  # Nur bestimmtes Modul
-am         # Also make (Dependencies)
-amd        # Also make dependents
-rf :module # Resume from
-U          # Force Update Snapshots
```

## pom.xml Snippets

```xml
<!-- Test Dependency -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>

<!-- Property -->
<properties>
    <java.version>21</java.version>
</properties>

<!-- Plugin -->
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <configuration>
                <includes>
                    <include>**/*Test.java</include>
                </includes>
            </configuration>
        </plugin>
    </plugins>
</build>
```
