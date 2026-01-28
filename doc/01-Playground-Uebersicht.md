# Playground - Beispielanwendung

## Was ist das?

Eine einfache **Todo-Anwendung** mit Spring Boot, die als Lernprojekt fuer verschiedene Testtechniken dient.

## Technologie-Stack

| Komponente | Technologie | Version |
|------------|-------------|---------|
| Backend | Spring Boot | 3.4.1 |
| Sprache | Java | 21 |
| Datenbank | H2 (In-Memory) | - |
| Frontend | Thymeleaf | - |
| Build | Maven | 3.9 |
| API-Doku | Swagger/OpenAPI | 2.7.0 |

## Projektstruktur

```
playground/
├── src/main/java/
│   └── de.training.playground/
│       ├── controller/          # REST + Web Controller
│       ├── service/             # Business-Logik
│       ├── repository/          # Datenzugriff (JPA)
│       └── entity/              # Domain-Objekte
├── src/test/java/               # Tests (siehe unten)
├── ci/                          # CI/CD Konfiguration
├── doc/                         # Diese Dokumentation
├── docker/                      # Docker-Konfiguration
│   ├── Dockerfile
│   ├── docker-compose.yml
│   └── .dockerignore
├── Jenkinsfile                  # CI/CD Pipeline
└── pom.xml                      # Maven Build
```

## Anwendung starten

```bash
# Lokal
mvn spring-boot:run

# Mit Docker
docker build -f docker/Dockerfile -t playground .
docker run -p 8080:8080 playground

# Oder mit docker compose
cd docker && docker compose up -d
```

**URLs:**
- Web-UI: http://localhost:4711
- REST-API: http://localhost:4711/api/todos
- Swagger: http://localhost:4711/swagger-ui.html
- H2-Console: http://localhost:4711/h2-console

## Teststruktur

```
src/test/java/
├── unit/                    # Schnelle, isolierte Tests
│   ├── basic/               # Grundlegende Unit-Tests
│   ├── mock/                # Tests mit Mockito
│   ├── testdata/            # Builder + Object Mother
│   └── architecture/        # ArchUnit
├── integration/             # Tests mit Spring-Kontext
│   ├── springboot/          # Volle Integration
│   ├── slice/               # @DataJpaTest, @WebMvcTest
│   └── database/            # H2, SQLite
├── rest/                    # REST-API Tests
│   ├── mockmvc/             # MockMvc
│   ├── restassured/         # RestAssured
│   ├── contract/            # Pact
│   └── wiremock/            # Externe Services mocken
└── e2e/                     # Browser-Tests
    ├── selenium/
    ├── selenide/
    └── playwright/
```

## Was wurde implementiert und warum?

### Entity: Todo
- Einfaches Domain-Objekt mit `id`, `title`, `description`, `done`, `dueDate`
- Enthaelt Business-Logik: `markDone()`, `isOverdue()`
- **Warum:** Zeigt, dass Logik in Entities gehoert, nicht nur in Services

### Repository: TodoRepository
- Erweitert `JpaRepository`
- Custom Query: `findByTitleContaining()`
- **Warum:** Demonstriert Spring Data JPA und Custom Queries

### Service: TodoService
- Business-Logik zwischen Controller und Repository
- Exception-Handling fuer "nicht gefunden"
- **Warum:** Zeigt Schichtenarchitektur und Dependency Injection

### Controller
- `TodoRestController`: REST-API mit CRUD
- `TodoWebController`: Thymeleaf Web-UI
- **Warum:** Zeigt beide Ansaetze (API vs. Server-Side Rendering)

## Tests ausfuehren

```bash
# Alle Tests
mvn test

# Nur Unit-Tests
mvn test -Dtest="de.training.playground.unit.**"

# Nur Integration-Tests
mvn test -Dtest="de.training.playground.integration.**"

# Einzelner Test
./run-test.sh TodoUnitTest
```

## CI/CD

```bash
cd ci
./start-ci.sh
```

- Jenkins: http://localhost:8082 (admin/admin123)
- SonarQube: http://localhost:9000 (admin/admin)

## Wesentliche Design-Entscheidungen

1. **H2 In-Memory:** Keine externe DB noetig, Tests sind schnell
2. **Schichtenarchitektur:** Controller -> Service -> Repository
3. **Teststruktur nach Kategorie:** Klare Trennung nach Testtyp
4. **Docker:** Einfaches Deployment, CI/CD-kompatibel
