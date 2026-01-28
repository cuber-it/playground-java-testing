# Allure - Test-Reporting

## Was ist Allure?

Ein **Test-Report-Framework** mit modernem UI und Drilldown-Funktionalitaet.

## Allure vs JUnit-Report

| Aspekt | JUnit | Allure |
|--------|-------|--------|
| UI | Einfach | Modern, interaktiv |
| Drilldown | Nein | Ja |
| Screenshots | Nein | Ja |
| Attachments | Nein | Ja |
| Trends | Nein | Ja |
| Kategorien | Nein | Ja |

## Setup im Playground

### pom.xml

```xml
<properties>
    <allure.version>2.29.0</allure.version>
</properties>

<dependencies>
    <dependency>
        <groupId>io.qameta.allure</groupId>
        <artifactId>allure-junit5</artifactId>
        <version>${allure.version}</version>
        <scope>test</scope>
    </dependency>
</dependencies>

<plugin>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-maven</artifactId>
    <version>2.13.0</version>
</plugin>
```

**Was wurde gemacht:** Allure JUnit5 Integration.

**Warum:** Bessere Reports als Standard-JUnit.

## Report generieren

```bash
# Tests ausfuehren
mvn test

# Report generieren und oeffnen
mvn allure:serve

# Nur generieren (ohne Browser)
mvn allure:report
# Report in: target/site/allure-maven-plugin/index.html
```

## Report-Uebersicht

```
┌─────────────────────────────────────────────────────────┐
│  Allure Report                                          │
├───────────────┬─────────────────────────────────────────┤
│  Overview     │  ┌─────┐  Tests: 137                   │
│  Categories   │  │ 95% │  Passed: 134                  │
│  Suites       │  │     │  Failed: 3                    │
│  Graphs       │  └─────┘  Skipped: 0                   │
│  Timeline     │                                         │
│  Behaviors    │  Duration: 45s                         │
│  Packages     │                                         │
└───────────────┴─────────────────────────────────────────┘
```

### Drilldown

```
Suites
└── de.training.playground.unit.basic
    └── TodoParameterizedTest
        └── testValidTitles(String)
            ├── "Einkaufen" ✓
            ├── "Meeting" ✓
            └── "Sport" ✓
```

## Annotationen

```java
import io.qameta.allure.*;

@Epic("Todo-Verwaltung")
@Feature("Todo erstellen")
@Story("Als Benutzer moechte ich Todos erstellen")
class TodoTest {

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Description("Prueft, ob ein neues Todo korrekt erstellt wird")
    void createTodo() {
        // ...
    }
}
```

### @Step - Schritte dokumentieren

```java
@Test
void createAndCompleteTodo() {
    Todo todo = createTodo("Test");
    markAsDone(todo);
    verifyCompleted(todo);
}

@Step("Todo erstellen mit Titel: {title}")
Todo createTodo(String title) {
    Todo todo = new Todo();
    todo.setTitle(title);
    return repository.save(todo);
}

@Step("Todo als erledigt markieren")
void markAsDone(Todo todo) {
    todo.markDone();
    repository.save(todo);
}

@Step("Pruefen, ob Todo erledigt ist")
void verifyCompleted(Todo todo) {
    assertThat(todo.isDone()).isTrue();
}
```

**Was wurde gemacht:** Testschritte im Report sichtbar.

**Warum:** Einfacheres Debugging bei Fehlern.

## Attachments

### Screenshots

```java
@Attachment(value = "Screenshot", type = "image/png")
public byte[] screenshot() {
    return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
}

@Test
void testWithScreenshot() {
    // ... Test ...
    screenshot();  // Wird an Report angehaengt
}
```

### Text/JSON

```java
@Attachment(value = "Response", type = "application/json")
public String attachResponse(String json) {
    return json;
}
```

## Selenide Integration

```xml
<dependency>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-selenide</artifactId>
    <version>${allure.version}</version>
    <scope>test</scope>
</dependency>
```

```java
// Screenshots bei Selenide automatisch
// Konfiguration in setUp()
SelenideLogger.addListener("allure", new AllureSelenide());
```

**Was wurde gemacht:** Automatische Screenshots bei Selenide-Tests.

**Warum:** Fehler visuell nachvollziehen.

## RestAssured Integration

```xml
<dependency>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-rest-assured</artifactId>
    <version>${allure.version}</version>
    <scope>test</scope>
</dependency>
```

```java
// Request/Response automatisch loggen
given()
    .filter(new AllureRestAssured())
    .when()
    .get("/api/todos")
// ...
```

**Was wurde gemacht:** HTTP-Requests im Allure-Report.

**Warum:** API-Kommunikation nachvollziehen.

## Jenkins Integration

```groovy
// Jenkinsfile
post {
    always {
        allure([
            includeProperties: false,
            results: [[path: 'target/allure-results']]
        ])
    }
}
```

**Was wurde gemacht:** Allure-Report nach jedem Jenkins-Build.

**Warum:** Test-Trends und History ueber Zeit.

## Report-Bereiche

### Categories

```
Categories
├── Product Defects (Bugs)
│   └── NullPointerException in Service
├── Test Defects (Testfehler)
│   └── Assertion failed: expected true
└── Broken Tests
    └── Connection refused
```

### Timeline

Zeigt parallele Test-Ausfuehrung:

```
Thread-1: ████████░░░░░░░░
Thread-2: ░░░░████████░░░░
Thread-3: ░░░░░░░░████████
```

### Graphs

- **Status:** Passed/Failed/Broken/Skipped
- **Severity:** Blocker/Critical/Normal/Minor/Trivial
- **Duration:** Testdauer-Verteilung
- **Trend:** Entwicklung ueber Builds

## Kategorien definieren

```json
// src/test/resources/categories.json
[
  {
    "name": "Ignored tests",
    "matchedStatuses": ["skipped"]
  },
  {
    "name": "Infrastructure problems",
    "matchedStatuses": ["broken", "failed"],
    "messageRegex": ".*Connection.*"
  },
  {
    "name": "Product defects",
    "matchedStatuses": ["failed"]
  }
]
```

## Best Practices

1. **@Step fuer wichtige Aktionen** - nicht fuer alles
2. **Screenshots bei UI-Tests** - automatisch bei Fehler
3. **Severity setzen** - Priorisierung bei Fehlern
4. **Trends beobachten** - Qualitaet ueber Zeit
5. **In CI/CD integrieren** - immer verfuegbar
