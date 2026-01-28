# Playground - Testprojekt

Spring Boot Projekt zum Erlernen verschiedener Testtechniken.

## Projekt starten

```bash
mvn spring-boot:run
```

Die App laeuft auf **http://localhost:4711**

## Endpunkte

| URL | Beschreibung |
|-----|--------------|
| http://localhost:4711 | Web-Oberflaeche |
| http://localhost:4711/swagger-ui.html | Swagger UI |
| http://localhost:4711/h2-console | H2 Datenbank-Konsole |
| http://localhost:4711/api/todos | REST API |
| http://localhost:4711/export | CSV Export |
| http://localhost:4711/actuator/health | Health-Check |
| http://localhost:4711/actuator/info | App-Info |

## Tests ausfuehren

```bash
# Alle Tests
mvn test

# Einzelnen Test ausfuehren (via Script)
./run-test.sh TodoUnitTest                    # Ganze Testklasse
./run-test.sh TodoUnitTest#markDone*          # Einzelne Methode
./run-test.sh '*Selenium*'                    # Pattern
./run-test.sh 'de.training.playground.unit.*' # Package

# Nur Unit-Tests
mvn test -Dtest="de.training.playground.unit.*"

# Nur Integrationstests
mvn test -Dtest="de.training.playground.integration.*"

# Nur REST-Tests
mvn test -Dtest="de.training.playground.rest.*"

# Nur E2E-Tests
mvn test -Dtest="de.training.playground.e2e.**"
```

## Shell-Skripte

| Skript | Beschreibung |
|--------|--------------|
| `./run-test.sh <Pattern>` | Einzelne Tests ausfuehren |
| `./run-playwright.sh` | Playwright E2E Tests (headless) |
| `./run-playwright.sh show` | Playwright E2E Tests mit Browser |
| `./show-report.sh` | Lokaler Allure Report (Live-Server) |
| `./show-report.sh jenkins [Nr]` | Jenkins Allure Report (letzter oder Build Nr) |

## Teststruktur

```
src/test/java/de/training/playground/
├── unit/                           # Unit-Tests (ohne Spring-Kontext)
│   ├── basic/                      # Grundlegende Unit-Tests
│   │   ├── TodoTest.java           # Entity-Tests mit AssertJ
│   │   ├── TodoUnitTest.java       # Tests mit Lifecycle-Methoden
│   │   └── TodoParameterizedTest.java # Parameterisierte Tests
│   ├── mock/                       # Tests mit Mockito
│   │   ├── TodoServiceTest.java
│   │   └── TodoServiceUnitTest.java
│   ├── testdata/                   # Testdaten-Bereitstellung
│   │   ├── TodoBuilder.java        # Builder Pattern
│   │   ├── TodoMother.java         # Object Mother Pattern
│   │   └── TestdataUsageTest.java
│   └── snippets/                   # Snippet-Vorlagen zum Ausprobieren
│
├── integration/                    # Integrationstests
│   ├── springboot/                 # Vollstaendige SpringBoot-Tests
│   │   └── TodoSpringBootTest.java
│   ├── slice/                      # Slice-Tests
│   │   ├── TodoRepositorySliceTest.java  # @DataJpaTest
│   │   └── TodoControllerSliceTest.java  # @WebMvcTest
│   ├── database/                   # Datenbank-Tests
│   │   ├── TodoH2Test.java         # H2 In-Memory
│   │   └── TodoSqliteTest.java     # SQLite
│   ├── testcontainers/             # TestContainers (Docker)
│   │   ├── TodoPostgresContainerTest.java  # PostgreSQL mit Spring
│   │   └── PostgresPlainJdbcTest.java      # PostgreSQL mit JDBC
│   └── snippets/                   # Snippet-Vorlagen zum Ausprobieren
│
├── rest/                           # REST-API Tests
│   ├── mockmvc/                    # MockMvc Tests
│   │   └── TodoMockMvcTest.java
│   ├── restassured/                # RestAssured Tests
│   │   └── TodoRestAssuredTest.java
│   ├── contract/                   # Contract Testing (Pact)
│   │   └── TodoContractTest.java
│   ├── wiremock/                   # WireMock Tests
│   │   └── ExternalServiceWireMockTest.java
│   ├── journey/                    # User Journey Tests
│   │   └── TodoApiJourneyTest.java
│   ├── scenarios/                  # Szenarien-Tests
│   │   ├── TodoApiErrorScenariosTest.java
│   │   └── TodoApiBulkOperationsTest.java
│   └── snippets/                   # Snippet-Vorlagen zum Ausprobieren
│
├── e2e/                            # End-to-End Tests
│   ├── selenium/
│   │   └── TodoSeleniumTest.java
│   ├── selenide/
│   │   └── TodoSelenideTest.java
│   ├── playwright/
│   │   ├── TodoPlaywrightTest.java
│   │   └── TodoUserJourneyTest.java
│   └── snippets/                   # Snippet-Vorlagen zum Ausprobieren
│
├── architecture/                   # Architektur-Tests (ArchUnit)
│   ├── ArchitectureTest.java
│   └── snippets/                   # Snippet-Vorlagen zum Ausprobieren
│
└── experiments/                    # Experimente (Kurs)
    ├── ExperimentTest.java
    └── snippets/                   # Snippet-Vorlagen zum Ausprobieren
```

## Snippets (Uebungsvorlagen)

Jede Testkategorie enthaelt ein `snippets/` Package mit Vorlagen zum Ausprobieren:

```java
@Test
@DisplayName("Snippet Template - bereit zum Ausfuellen")
@Disabled("Template - aktivieren und anpassen")
void snippetTemplate() {
    // TODO: Hier experimentieren
    assertTrue(true);
}
```

**Workflow im Kurs:**
1. Snippet-Test oeffnen
2. `@Disabled` entfernen
3. Code schreiben und testen
4. Pipeline in Jenkins starten (z.B. `01-Unit/snippets`)

## Testtechniken im Ueberblick

### Unit-Tests (basic)
- Isolierte Tests ohne Framework
- AssertJ fuer lesbare Assertions
- `@DisplayName` fuer Testbeschreibungen
- Lifecycle: `@BeforeAll`, `@BeforeEach`, `@AfterEach`, `@AfterAll`
- **Parameterisierte Tests**: `@ParameterizedTest`, `@ValueSource`, `@CsvSource`, `@MethodSource`

### Mock-Tests (mock)
- Mockito fuer Abhaengigkeiten
- `@Mock`, `@InjectMocks`
- `when().thenReturn()`, `verify()`

### Testdaten (testdata)
- **Builder Pattern**: Fluent API fuer Testobjekte
- **Object Mother**: Vordefinierte Standard-Testobjekte
- Wiederverwendbare Testdaten

### Architektur-Tests (architecture)
- **ArchUnit** fuer Architekturregeln
- Schichten-Architektur pruefen
- Namenskonventionen durchsetzen
- Dependency-Regeln validieren

### Integrationstests (springboot)
- `@SpringBootTest` laedt den kompletten Kontext
- `@Transactional` fuer Rollback nach jedem Test
- Echte Datenbank-Interaktion

### Slice-Tests (slice)
- `@DataJpaTest` fuer Repository-Tests
- `@WebMvcTest` fuer Controller-Tests
- Schneller als vollstaendige SpringBoot-Tests

### Datenbank-Tests (database)
- H2 In-Memory fuer schnelle Tests
- SQLite fuer Datei-basierte Tests

### TestContainers (testcontainers)
- **Echte Datenbanken** in Docker-Containern
- PostgreSQL, MySQL, etc. statt H2
- `@Testcontainers` + `@Container` Annotationen
- `@DynamicPropertySource` fuer Spring-Integration
- Voraussetzung: Docker muss laufen

### REST-Tests (mockmvc)
- `@AutoConfigureMockMvc` fuer MockMvc
- `MockMvc.perform()` fuer HTTP-Requests
- `jsonPath()` fuer JSON-Assertions

### REST-Tests (restassured)
- Fluent DSL fuer HTTP-Tests
- `given().when().then()` Syntax
- Response-Validierung

### Contract-Tests (contract)
- **Pact** fuer Consumer-Driven Contracts
- Vertrag zwischen Consumer und Provider
- Generierte Pact-Files in `target/pacts/`

### WireMock-Tests (wiremock)
- Externe HTTP-Services mocken
- Stubbing und Verification
- Verzoegerte Antworten simulieren

### E2E-Tests (Selenium)
- Browser-Automatisierung
- WebDriver fuer Chrome/Firefox
- Explizite Waits fuer asynchrone Elemente
- Page Object Pattern moeglich

### E2E-Tests (Selenide)
- Eleganter Wrapper um Selenium
- Automatische Waits eingebaut
- Fluent API: `$(".btn").click()`
- Weniger Boilerplate-Code
- Collection-Assertions: `$$(".todo").shouldHave(size(2))`

### E2E-Tests (Playwright)
- Moderne Alternative zu Selenium
- Schneller und stabiler
- Auto-Wait eingebaut
- Einfachere API

## Selenium einrichten

ChromeDriver wird automatisch vom System verwendet wenn installiert:
```bash
# Ubuntu/Debian
sudo apt install chromium-chromedriver

# Mac
brew install chromedriver
```

Tests ausfuehren:
```bash
./run-test.sh '*Selenium*'
```

## Selenide einrichten

Selenide nutzt automatisch WebDriverManager - keine zusaetzliche Installation noetig.

Tests ausfuehren:
```bash
./run-test.sh '*Selenide*'
```

## Playwright einrichten

Playwright-Browser installieren:
```bash
# Via npx (empfohlen)
npx playwright@1.49.0 install chromium
```

Tests ausfuehren:
```bash
./run-test.sh '*Playwright*'
```

### Playwright Troubleshooting

Falls Probleme auftreten:
```bash
# Alle Browser installieren
npx playwright install

# Nur Chromium
npx playwright install chromium
```

Browser werden installiert in:
- Linux: `~/.cache/ms-playwright`
- Mac: `~/Library/Caches/ms-playwright`
- Windows: `%USERPROFILE%\AppData\Local\ms-playwright`

## Docker

Docker-Konfiguration liegt im `docker/` Ordner.

### Image bauen und starten

```bash
# Image bauen (aus Projekt-Root)
docker build -f docker/Dockerfile -t playground:latest .

# Container starten
docker run -d -p 8080:8080 playground:latest

# Oder mit docker compose (aus docker/ Ordner)
cd docker
docker compose up -d
```

Die Anwendung ist dann unter **http://localhost:8080** erreichbar.

### Docker-Befehle

```bash
cd docker

# Container stoppen
docker compose down

# Logs anzeigen
docker compose logs -f

# Container neu bauen
docker compose up -d --build
```

## CI/CD Pipeline

Vollstaendige CI/CD-Umgebung mit Jenkins, SonarQube, JaCoCo und Allure.

### CI/CD starten

```bash
cd ci
./start-ci.sh
```

### Services

| Service   | URL                    | Credentials        |
|-----------|------------------------|--------------------|
| Jenkins   | http://localhost:8082  | admin / admin123   |
| SonarQube | http://localhost:9000  | admin / admin      |

### Jenkins Ordnerstruktur

Die Pipelines sind in Ordnern organisiert:

```
Jenkins
├── 01-Unit/                    # Unit-Tests
│   ├── basic
│   ├── mock
│   ├── testdata
│   └── snippets
├── 02-Integration/             # Integrationstests
│   ├── springboot
│   ├── slice
│   ├── database
│   ├── testcontainers
│   └── snippets
├── 03-REST/                    # REST-API Tests
│   ├── mockmvc
│   ├── restassured
│   ├── wiremock
│   ├── contract
│   ├── journey
│   ├── scenarios
│   └── snippets
├── 04-E2E/                     # End-to-End Tests
│   ├── selenium
│   ├── selenide
│   ├── playwright
│   └── snippets
├── 10-Architecture/            # Architektur-Tests
│   └── snippets
├── 99-Experimente/             # Experimente
│   └── snippets
└── playground-gesamt           # Vollstaendige Pipeline
```

### Vollstaendige Pipeline (playground-gesamt)

1. **Checkout** - Source Code kopieren
2. **Build** - Projekt kompilieren
3. **SonarQube Analysis** - Code-Qualitaetsanalyse
4. **Unit Tests** - Unit-Tests ausfuehren
5. **Integration Tests** - Integrationstests ausfuehren
6. **REST Tests** - REST API Tests ausfuehren
7. **All Tests + Coverage** - JaCoCo Coverage Report
8. **E2E Tests (Playwright)** - Browser-Tests
9. **Package** - JAR erstellen

### Einzelne Pipelines

Jede Sub-Pipeline testet nur ihren Bereich:

```bash
# Beispiel: Nur Mockito-Tests
01-Unit/mock → de.training.playground.unit.mock.**

# Beispiel: Nur RestAssured-Tests
03-REST/restassured → de.training.playground.rest.restassured.**
```

Details siehe `ci/README.md`.

## Allure Reports

Allure ist integriert fuer umfangreiche Testreports mit Drilldown.

```bash
# Tests mit Allure ausfuehren
mvn test

# Report generieren und oeffnen
mvn allure:serve

# Report nur generieren (in target/site/allure-maven-plugin)
mvn allure:report
```

In Jenkins wird der Allure Report automatisch nach jedem Build generiert.

## JaCoCo Coverage

Code Coverage mit JaCoCo.

```bash
# Coverage Report generieren
mvn verify

# Report oeffnen
open target/site/jacoco/index.html
```

## Dokumentation

Umfangreiche Dokumentation im `doc/` Ordner (optimiert fuer Obsidian):

| Bereich | Beschreibung |
|---------|--------------|
| [Hauptindex](doc/00-Index.md) | Einstiegspunkt mit Inhaltsverzeichnis |
| [Cheatsheets](doc/cheatsheets/) | Kompakte Referenzkarten (JUnit5, Mockito, AssertJ, Spring, REST, Docker, Maven) |
| [Code Snippets](doc/snippets/) | Copy-Paste Codebeispiele und IDE Live Templates |
| [AI Prompts](doc/21-AI-Prompts-Testing.md) | Hilfreiche Prompts fuer AI-gestuetztes Testen |
| [Styleguide](doc/96-Styleguide-Tests.md) | Namenskonventionen und Best Practices |
| [Antipatterns](doc/97-Antipatterns.md) | 13 haeufige Testfehler vermeiden |
| [Links & Ressourcen](doc/98-Links-Ressourcen.md) | Offizielle Docs, Artikel, Buecher |
| [Glossar](doc/99-Glossar.md) | Testing-Begriffe von A-Z |

### IDE Live Templates

Fertige Snippets fuer schnelles Scaffolding:

```bash
# IntelliJ: Settings → Editor → Live Templates → Import
doc/snippets/intellij-live-templates.xml

# Eclipse: Preferences → Java → Editor → Templates → Import
doc/snippets/eclipse-templates.xml

# VS Code: Kopieren nach .vscode/
cp doc/snippets/vscode.code-snippets .vscode/
```

## Technologie-Stack

| Kategorie | Technologie | Version |
|-----------|-------------|---------|
| **Runtime** | Java | 21 |
| | Spring Boot | 3.4.1 |
| | Spring Data JPA | (managed) |
| | H2 Database | (managed) |
| | Thymeleaf | (managed) |
| **Test-Frameworks** | JUnit 5 | (managed) |
| | Mockito | 5.14.2 |
| | AssertJ | (managed) |
| | ArchUnit | 1.3.0 |
| **REST-Testing** | RestAssured | (managed) |
| | WireMock | 3.10.0 |
| | Pact | 4.6.16 |
| **E2E-Testing** | Selenium | 4.27.0 |
| | Selenide | 7.7.3 |
| | Playwright | 1.49.0 |
| **Datenbanken** | TestContainers | 1.21.4 |
| | SQLite | 3.47.1.0 |
| | PostgreSQL | (managed) |
| **Reporting** | Allure | 2.29.0 |
| | JaCoCo | 0.8.12 |
| **Code-Qualitaet** | SonarQube Plugin | 4.0.0.4121 |
| **API-Doku** | Springdoc OpenAPI | 2.7.0 |
