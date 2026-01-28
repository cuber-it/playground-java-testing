# Testdaten - Builder & Object Mother

## Das Problem

```java
// Schlechter Test: Viel Boilerplate
@Test
void testSomething() {
    Todo todo = new Todo();
    todo.setId(1L);
    todo.setTitle("Test");
    todo.setDescription("Beschreibung");
    todo.setDone(false);
    todo.setDueDate(LocalDate.now().plusDays(7));
    // ... mehr Felder ...

    // Eigentlicher Test
}
```

**Probleme:**
- Viel Code, wenig Aussagekraft
- Copy-Paste zwischen Tests
- Aenderungen an Entity erfordern Aenderungen ueberall

## Loesung 1: Builder Pattern

### TodoBuilder im Playground

```java
// src/test/java/.../unit/testdata/TodoBuilder.java

public class TodoBuilder {

    private Long id = null;
    private String title = "Standard Todo";
    private String description = "";
    private boolean done = false;
    private LocalDate dueDate = null;

    // Factory-Methode
    public static TodoBuilder aTodo() {
        return new TodoBuilder();
    }

    // Fluent Setter
    public TodoBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public TodoBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public TodoBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public TodoBuilder done() {
        this.done = true;
        return this;
    }

    public TodoBuilder notDone() {
        this.done = false;
        return this;
    }

    public TodoBuilder withDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    // Convenience-Methoden
    public TodoBuilder overdue() {
        this.dueDate = LocalDate.now().minusDays(1);
        return this;
    }

    public TodoBuilder dueTomorrow() {
        this.dueDate = LocalDate.now().plusDays(1);
        return this;
    }

    public TodoBuilder dueInDays(int days) {
        this.dueDate = LocalDate.now().plusDays(days);
        return this;
    }

    // Build-Methode
    public Todo build() {
        Todo todo = new Todo();
        todo.setId(id);
        todo.setTitle(title);
        todo.setDescription(description);
        todo.setDueDate(dueDate);
        if (done) {
            todo.markDone();
        }
        return todo;
    }
}
```

**Was wurde gemacht:** Fluent Builder fuer Todo-Objekte.

**Warum:** Lesbare, flexible Testdaten-Erstellung.

### Builder verwenden

```java
import static de.training.playground.unit.testdata.TodoBuilder.aTodo;

@Test
void testWithBuilder() {
    // Minimal
    Todo simple = aTodo().build();

    // Mit Titel
    Todo withTitle = aTodo()
        .withTitle("Einkaufen")
        .build();

    // Komplex
    Todo complex = aTodo()
        .withTitle("Wichtig")
        .withDescription("Sehr wichtig!")
        .overdue()
        .build();

    // Erledigt
    Todo completed = aTodo()
        .withTitle("Fertig")
        .done()
        .build();
}
```

## Loesung 2: Object Mother

### TodoMother im Playground

```java
// src/test/java/.../unit/testdata/TodoMother.java

public class TodoMother {

    // Vordefinierte Standard-Objekte
    public static Todo einkaufsTodo() {
        return TodoBuilder.aTodo()
            .withTitle("Einkaufen gehen")
            .withDescription("Milch, Brot, Eier")
            .dueInDays(1)
            .build();
    }

    public static Todo sportTodo() {
        return TodoBuilder.aTodo()
            .withTitle("Sport machen")
            .withDescription("30 Minuten joggen")
            .dueInDays(0)
            .build();
    }

    public static Todo ueberfaelligesTodo() {
        return TodoBuilder.aTodo()
            .withTitle("Ueberfaellig!")
            .overdue()
            .build();
    }

    public static Todo erledigtesTodo() {
        return TodoBuilder.aTodo()
            .withTitle("Erledigt")
            .done()
            .build();
    }

    // Listen
    public static List<Todo> dreiOffeneTodos() {
        return List.of(
            aTodo().withTitle("Erstes").build(),
            aTodo().withTitle("Zweites").build(),
            aTodo().withTitle("Drittes").build()
        );
    }

    public static List<Todo> mehrereOffeneTodos(int anzahl) {
        return IntStream.rangeClosed(1, anzahl)
            .mapToObj(i -> aTodo().withTitle("Todo " + i).build())
            .toList();
    }

    public static List<Todo> gemischteTodos() {
        return List.of(
            aTodo().withTitle("Offen").build(),
            aTodo().withTitle("Erledigt").done().build(),
            aTodo().withTitle("Ueberfaellig").overdue().build()
        );
    }
}
```

**Was wurde gemacht:** Factory-Methoden fuer haeufige Testszenarien.

**Warum:** Wiederverwendbare, benannte Testdaten.

### Object Mother verwenden

```java
import static de.training.playground.unit.testdata.TodoMother.*;

@Test
void testWithMother() {
    // Vordefinierte Objekte
    Todo einkauf = einkaufsTodo();
    Todo sport = sportTodo();
    Todo ueberfaellig = ueberfaelligesTodo();

    // Listen
    List<Todo> todos = dreiOffeneTodos();
    List<Todo> vieleTodos = mehrereOffeneTodos(10);
}
```

## Builder vs Object Mother

| Aspekt | Builder | Object Mother |
|--------|---------|---------------|
| Flexibilitaet | Hoch | Niedrig |
| Lesbarkeit | Gut | Sehr gut |
| Wiederverwendung | Mittel | Hoch |
| Anpassbarkeit | Pro Test | Vordefiniert |

**Empfehlung:** Beide kombinieren!

```java
// Object Mother nutzt Builder intern
public static Todo einkaufsTodo() {
    return TodoBuilder.aTodo()
        .withTitle("Einkaufen")
        .build();
}
```

## Beispiel-Test

```java
// src/test/java/.../unit/testdata/TestdataUsageTest.java

class TestdataUsageTest {

    @Test
    @DisplayName("Builder fuer individuelle Todos")
    void builderUsage() {
        Todo todo = aTodo()
            .withTitle("Custom")
            .withDescription("Mein Test")
            .dueInDays(3)
            .build();

        assertThat(todo.getTitle()).isEqualTo("Custom");
        assertThat(todo.isOverdue()).isFalse();
    }

    @Test
    @DisplayName("Object Mother fuer Standard-Szenarien")
    void motherUsage() {
        Todo ueberfaellig = ueberfaelligesTodo();

        assertThat(ueberfaellig.isOverdue()).isTrue();
    }

    @Test
    @DisplayName("Listen fuer Mengen-Tests")
    void listUsage() {
        List<Todo> todos = mehrereOffeneTodos(5);

        assertThat(todos).hasSize(5);
        assertThat(todos).allMatch(t -> !t.isDone());
    }
}
```

## Best Practices

### 1. Sinnvolle Defaults

```java
// Builder mit sinnvollen Standardwerten
private String title = "Standard Todo";  // Nicht null!
private boolean done = false;            // Logischer Default
```

### 2. Sprechende Methoden

```java
// Gut
.overdue()
.dueTomorrow()
.done()

// Schlecht
.setDueDate(LocalDate.now().minusDays(1))
```

### 3. Domänensprache nutzen

```java
// Object Mother mit Fachbegriffen
public static Todo dringendeAufgabe() { ... }
public static Todo abgeschlosseneAufgabe() { ... }
public static List<Todo> volleProjektliste() { ... }
```

### 4. Immutable wo moeglich

```java
// Build-Methode erstellt neues Objekt
public Todo build() {
    return new Todo(...);  // Neues Objekt, kein Sharing
}
```

### 5. Zentrale Stelle

```
src/test/java/
└── de.training.playground.unit.testdata/
    ├── TodoBuilder.java    # Builder
    └── TodoMother.java     # Factory-Methoden
```

## Vorteile

1. **Weniger Boilerplate** - Tests fokussieren auf Logik
2. **Bessere Lesbarkeit** - Intention klar
3. **Einfache Wartung** - Aenderungen an einer Stelle
4. **Konsistente Testdaten** - Gleiche Objekte wiederverwendet
5. **Dokumentation** - Methoden beschreiben Szenarien
