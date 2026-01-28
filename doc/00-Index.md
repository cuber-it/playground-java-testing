# Testing & CI/CD - Dokumentation

Diese Dokumentation begleitet das **Playground**-Projekt - eine Spring Boot Anwendung zum Erlernen verschiedener Testtechniken.

---

## Schnellstart

```bash
# Anwendung starten
mvn spring-boot:run

# Alle Tests ausfuehren
mvn test

# CI/CD Umgebung starten
cd ci && ./start-ci.sh
```

---

## Inhaltsverzeichnis

### Grundlagen

| Dokument | Beschreibung |
|----------|--------------|
| [[01-Playground-Uebersicht]] | Projektstruktur, Architektur, Einstieg |
| [[02-JUnit5-Grundlagen]] | Annotationen, Lifecycle, Parameterisierte Tests |
| [[03-Mockito]] | Mocking, Stubbing, Verification |
| [[16-Testdaten]] | Builder Pattern, Object Mother |

### Spring Boot Testing

| Dokument | Beschreibung |
|----------|--------------|
| [[04-Spring-Boot-Tests]] | @SpringBootTest, Slice-Tests, @MockBean |
| [[15-ArchUnit]] | Architekturregeln als Tests |

### REST API Testing

| Dokument | Beschreibung |
|----------|--------------|
| [[05-RestAssured]] | Fluent API fuer HTTP-Tests |
| [[06-WireMock]] | Externe Services mocken |
| [[07-Pact-Contract-Testing]] | Consumer-Driven Contracts |

### E2E / UI Testing

| Dokument | Beschreibung |
|----------|--------------|
| [[08-Selenium]] | Browser-Automatisierung (Klassiker) |
| [[09-Selenide]] | Eleganter Selenium-Wrapper |
| [[10-Playwright]] | Moderne Alternative |

### CI/CD & Reporting

| Dokument | Beschreibung |
|----------|--------------|
| [[11-JaCoCo]] | Code Coverage |
| [[12-SonarQube]] | Code-Qualitaetsanalyse |
| [[13-Jenkins]] | CI/CD Pipeline |
| [[14-Allure]] | Test-Reporting mit Drilldown |
| [[17-TestContainers]] | Echte Datenbanken in Docker-Containern |

### Weiterfuehrend (nicht implementiert)

| Dokument | Beschreibung |
|----------|--------------|
| [[18-Mutation-Testing]] | Testqualitaet mit PIT pruefen |
| [[19-Performance-Tests]] | Last- und Stresstests |
| [[20-Security-Tests]] | OWASP ZAP, Dependency-Check |

### Hilfestellung

| Dokument | Beschreibung |
|----------|--------------|
| [[21-AI-Prompts-Testing]] | AI-Assistenten sinnvoll nutzen, Beispiel-Prompts |

### Referenz

| Dokument | Beschreibung |
|----------|--------------|
| [[96-Styleguide-Tests]] | Namenskonventionen, Struktur, Best Practices |
| [[97-Antipatterns]] | 13 haeufige Fehler und wie man sie vermeidet |
| [[98-Links-Ressourcen]] | Offizielle Docs, Artikel, Buecher |
| [[99-Glossar]] | Begriffe und Abkuerzungen |

---

## Cheatsheets

Kompakte Referenzkarten zum schnellen Nachschlagen:

| Cheatsheet | Thema |
|------------|-------|
| [[cheatsheets/JUnit5]] | Annotationen, Parameterized Tests, Lifecycle |
| [[cheatsheets/AssertJ]] | Fluent Assertions fuer alle Datentypen |
| [[cheatsheets/Mockito]] | Mocks, Stubs, Verification, Captors |
| [[cheatsheets/SpringBootTest]] | @SpringBootTest, Slice-Tests, MockMvc |
| [[cheatsheets/RestAssured]] | REST API Tests, JSON Path, Hamcrest |
| [[cheatsheets/WireMock]] | HTTP Mocking, Stubbing, Verification |
| [[cheatsheets/Selenide]] | Browser-Automation, Selektoren, Conditions |
| [[cheatsheets/Playwright]] | Moderne E2E-Tests, Locators, Assertions |
| [[cheatsheets/Maven]] | Lifecycle, Tests, Profile, Plugins |
| [[cheatsheets/Docker]] | Images, Container, Compose, Cleanup |

## Code Snippets

IDE Live Templates und Copy-Paste Codeblöcke: [[snippets/README|Snippets Übersicht]]

### Grundlagen
| Datei | Inhalt |
|-------|--------|
| [[snippets/01-junit5-snippets]] | Test-Klassen, Parameterized, Nested, Exceptions |
| [[snippets/02-mockito-snippets]] | Mocks, Stubbing, Verification, Captors |
| [[snippets/03-spring-snippets]] | SpringBootTest, WebMvcTest, DataJpaTest, MockMvc |

### API & E2E
| Datei | Inhalt |
|-------|--------|
| [[snippets/04-rest-snippets]] | REST Assured, WireMock |
| [[snippets/05-e2e-snippets]] | Selenide, Playwright |

### Erweitert
| Datei | Inhalt |
|-------|--------|
| [[snippets/06-assertj-snippets]] | Collections, Soft Assertions, Extracting |
| [[snippets/07-archunit-snippets]] | Layer-Rules, Naming, Dependencies |
| [[snippets/08-pact-snippets]] | Consumer & Provider Tests |

### Patterns & Tools
| Datei | Inhalt |
|-------|--------|
| [[snippets/09-testdata-snippets]] | Builder, Object Mother, Fixtures |
| [[snippets/10-allure-snippets]] | @Step, @Story, @Severity, Attachments |
| [[snippets/11-junit5-extensions-snippets]] | Custom Extensions, Parameter Resolver |
| [[snippets/12-testcontainers-snippets]] | PostgreSQL, MySQL, Kafka, Docker Compose |

**IDE Templates (26 Snippets):**
- `intellij-live-templates.xml` - Import unter Settings → Live Templates
- `eclipse-templates.xml` - Import unter Preferences → Templates
- `vscode.code-snippets` - Kopieren nach `.vscode/`

---

## Teststruktur

```
src/test/java/de/training/playground/
├── unit/
│   ├── basic/          # JUnit 5, Parameterisierte Tests
│   ├── mock/           # Mockito
│   ├── testdata/       # Builder, Object Mother
│   └── snippets/       # Uebungsvorlagen
├── integration/
│   ├── springboot/     # @SpringBootTest
│   ├── slice/          # @DataJpaTest, @WebMvcTest
│   ├── database/       # H2, SQLite
│   ├── testcontainers/ # PostgreSQL in Docker
│   └── snippets/       # Uebungsvorlagen
├── rest/
│   ├── mockmvc/        # MockMvc
│   ├── restassured/    # RestAssured
│   ├── contract/       # Pact
│   ├── wiremock/       # WireMock
│   ├── journey/        # User Journeys
│   ├── scenarios/      # Fehler- und Bulk-Szenarien
│   └── snippets/       # Uebungsvorlagen
├── e2e/
│   ├── selenium/
│   ├── selenide/
│   ├── playwright/
│   └── snippets/       # Uebungsvorlagen
├── architecture/       # ArchUnit
│   └── snippets/       # Uebungsvorlagen
└── experiments/        # Kurs-Experimente
    └── snippets/       # Uebungsvorlagen
```

---

## CI/CD Umgebung

| Service | URL | Credentials |
|---------|-----|-------------|
| Jenkins | http://localhost:8082 | admin / admin123 |
| SonarQube | http://localhost:9000 | admin / admin |
| Anwendung | http://localhost:4711 | - |

### Jenkins Ordnerstruktur

```
Jenkins
├── 01-Unit/           # basic, mock, testdata, snippets
├── 02-Integration/    # springboot, slice, database, testcontainers, snippets
├── 03-REST/           # mockmvc, restassured, wiremock, contract, journey, scenarios, snippets
├── 04-E2E/            # selenium, selenide, playwright, snippets
├── 10-Architecture/   # snippets
├── 99-Experimente/    # snippets
└── playground-gesamt  # Vollstaendige Pipeline
```

---

## Befehle

### Tests

```bash
# Alle Tests
mvn test

# Nach Kategorie
mvn test -Dtest="de.training.playground.unit.**"
mvn test -Dtest="de.training.playground.integration.**"
mvn test -Dtest="de.training.playground.rest.**"
mvn test -Dtest="de.training.playground.e2e.**"
mvn test -Dtest="de.training.playground.architecture.**"

# Nach Sub-Kategorie
mvn test -Dtest="de.training.playground.unit.mock.**"
mvn test -Dtest="de.training.playground.rest.restassured.**"

# Einzelner Test
./run-test.sh TodoUnitTest
./run-test.sh '*Playwright*'
```

### Reports

```bash
# Allure Report
mvn allure:serve

# JaCoCo Coverage
mvn verify
open target/site/jacoco/index.html

# SonarQube (CI/CD muss laufen)
mvn sonar:sonar -Dsonar.host.url=http://localhost:9000
```

### Docker

```bash
# Anwendung
docker build -t playground .
docker run -p 8080:8080 playground

# CI/CD
cd ci
./start-ci.sh
./stop-ci.sh
```

---

## Tags

#testing #junit #mockito #spring-boot #selenium #selenide #playwright #restassured #wiremock #pact #jacoco #sonarqube #jenkins #allure #archunit #ci-cd

---

*Erstellt fuer den Testing-Workshop*
