# JUnit 5 - Grundlagen

## Was ist JUnit 5?

Das Standard-Test-Framework fuer Java. Besteht aus:
- **JUnit Platform:** Basis fuer Test-Frameworks
- **JUnit Jupiter:** Neue API (JUnit 5)
- **JUnit Vintage:** Kompatibilitaet mit JUnit 4

## Grundlegende Annotationen

```java
@Test                    // Markiert eine Testmethode
@DisplayName("...")      // Lesbarer Name im Report
@BeforeEach              // Vor jedem Test
@AfterEach               // Nach jedem Test
@BeforeAll               // Einmal vor allen Tests (static)
@AfterAll                // Einmal nach allen Tests (static)
@Disabled                // Test ueberspringen
```

## Beispiel aus dem Playground

```java
// src/test/java/.../unit/basic/TodoUnitTest.java

@DisplayName("Todo Unit Tests")
class TodoUnitTest {

    private Todo todo;

    @BeforeEach
    void setUp() {
        todo = new Todo();
        todo.setTitle("Test Todo");
    }

    @Test
    @DisplayName("Neues Todo ist nicht erledigt")
    void newTodoIsNotDone() {
        assertThat(todo.isDone()).isFalse();
    }

    @Test
    @DisplayName("markDone() setzt done auf true")
    void markDoneSetsFlag() {
        todo.markDone();
        assertThat(todo.isDone()).isTrue();
    }
}
```

**Was wurde gemacht:** Einfache Unit-Tests fuer die Todo-Entity ohne Framework-Abhaengigkeiten.

**Warum:** Zeigt den Lebenszyklus von Tests und grundlegende Assertions.

## Parameterisierte Tests

Gleiche Testlogik mit verschiedenen Eingabewerten:

```java
// src/test/java/.../unit/basic/TodoParameterizedTest.java

@ParameterizedTest(name = "Titel ''{0}'' ist gueltig")
@ValueSource(strings = {"Einkaufen", "Meeting", "Sport"})
void validTitles(String title) {
    Todo todo = new Todo();
    todo.setTitle(title);
    assertThat(todo.getTitle()).isEqualTo(title);
}

@ParameterizedTest
@CsvSource({
    "1, true",      // 1 Tag in Zukunft -> nicht ueberfaellig
    "0, false",     // Heute -> nicht ueberfaellig
    "-1, true"      // Gestern -> ueberfaellig
})
void overdueCheck(int daysOffset, boolean expectedOverdue) {
    todo.setDueDate(LocalDate.now().plusDays(daysOffset));
    assertThat(todo.isOverdue()).isEqualTo(expectedOverdue);
}

@ParameterizedTest
@MethodSource("provideDueDates")
void overdueWithMethodSource(int days, boolean expected) {
    // ...
}

static Stream<Arguments> provideDueDates() {
    return Stream.of(
        Arguments.of(-5, true),
        Arguments.of(0, false),
        Arguments.of(5, false)
    );
}
```

**Was wurde gemacht:** Verschiedene Parameter-Quellen demonstriert.

**Warum:** Reduziert Code-Duplikation bei aehnlichen Testfaellen.

## Assertions mit AssertJ

JUnit 5 hat eigene Assertions, aber AssertJ ist lesbarer:

```java
// JUnit 5
assertEquals("Test", todo.getTitle());
assertTrue(todo.isDone());

// AssertJ (empfohlen)
assertThat(todo.getTitle()).isEqualTo("Test");
assertThat(todo.isDone()).isTrue();
assertThat(todos).hasSize(3)
                 .extracting(Todo::getTitle)
                 .contains("A", "B", "C");
```

## Lifecycle-Methoden

```
@BeforeAll  ─────────────────────────────────────┐
                                                  │
    @BeforeEach ──────┐                          │
    @Test             │ wiederholt               │ einmal
    @AfterEach  ──────┘ pro Test                 │
                                                  │
@AfterAll   ─────────────────────────────────────┘
```

## Bedingte Ausfuehrung

```java
@EnabledOnOs(OS.LINUX)
@DisabledOnOs(OS.WINDOWS)
@EnabledIfSystemProperty(named = "run.slow.tests", matches = "true")
@EnabledIfEnvironmentVariable(named = "CI", matches = "true")
```

**Playground-Beispiel:**
```java
// SQLite-Tests nur wenn explizit aktiviert
@EnabledIfSystemProperty(named = "run.sqlite.tests", matches = "true")
class TodoSqliteTest { ... }
```

## Best Practices

1. **Ein Assert pro Test** (wenn moeglich)
2. **Aussagekraeftige Namen** mit `@DisplayName`
3. **AAA-Pattern:** Arrange, Act, Assert
4. **Keine Logik in Tests** - nur Assertions
5. **Tests muessen unabhaengig sein** - keine Reihenfolge-Abhaengigkeit
