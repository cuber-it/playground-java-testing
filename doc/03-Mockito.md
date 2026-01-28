# Mockito - Mocking Framework

## Was ist Mockito?

Ein Framework zum Erstellen von **Mock-Objekten** fuer Unit-Tests. Ermoeglicht das Isolieren der zu testenden Klasse von ihren Abhaengigkeiten.

## Warum Mocking?

```
┌─────────────────┐     ┌──────────────────┐     ┌────────────────┐
│  TodoService    │────>│  TodoRepository  │────>│   Datenbank    │
│  (zu testen)    │     │  (Dependency)    │     │   (langsam)    │
└─────────────────┘     └──────────────────┘     └────────────────┘
                                 │
                                 ▼
                        ┌──────────────────┐
                        │   Mock-Objekt    │  <-- Simuliert Repository
                        │   (schnell)      │      ohne echte DB
                        └──────────────────┘
```

## Grundlegende Annotationen

```java
@Mock                   // Erstellt ein Mock-Objekt
@InjectMocks            // Injiziert Mocks in die zu testende Klasse
@Spy                    // Partieller Mock (echte Methoden + ueberschreibbare)
@Captor                 // Faengt Argumente ab
```

## Beispiel aus dem Playground

```java
// src/test/java/.../unit/mock/TodoServiceTest.java

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository repository;  // Mock des Repositories

    @InjectMocks
    private TodoService service;        // Service mit injiziertem Mock

    @Test
    @DisplayName("findAll() gibt alle Todos zurueck")
    void findAllReturnsTodos() {
        // Arrange: Mock-Verhalten definieren
        List<Todo> mockTodos = List.of(
            createTodo(1L, "Erstes"),
            createTodo(2L, "Zweites")
        );
        when(repository.findAll()).thenReturn(mockTodos);

        // Act: Service aufrufen
        List<Todo> result = service.findAll();

        // Assert: Ergebnis pruefen
        assertThat(result).hasSize(2);
        verify(repository).findAll();  // Wurde aufgerufen?
    }
}
```

**Was wurde gemacht:** TodoService wird isoliert getestet, das Repository wird gemockt.

**Warum:** Der Test ist schnell (keine DB) und testet nur die Service-Logik.

## Stubbing - Verhalten definieren

```java
// Rueckgabewert festlegen
when(repository.findById(1L)).thenReturn(Optional.of(todo));

// Exception werfen
when(repository.findById(999L)).thenThrow(new RuntimeException());

// Verschiedene Rueckgaben bei mehreren Aufrufen
when(repository.count())
    .thenReturn(0L)     // Erster Aufruf
    .thenReturn(1L);    // Zweiter Aufruf

// Dynamische Antwort basierend auf Argument
when(repository.findById(anyLong()))
    .thenAnswer(invocation -> {
        Long id = invocation.getArgument(0);
        return Optional.of(createTodo(id, "Todo " + id));
    });
```

## Verification - Aufrufe pruefen

```java
// Wurde genau einmal aufgerufen?
verify(repository).save(any(Todo.class));

// Wurde nie aufgerufen?
verify(repository, never()).delete(any());

// Wurde genau 3x aufgerufen?
verify(repository, times(3)).findAll();

// Wurde mindestens einmal aufgerufen?
verify(repository, atLeastOnce()).findById(anyLong());

// Argument pruefen
verify(repository).save(argThat(todo ->
    todo.getTitle().equals("Test")
));
```

## Argument Captor

```java
@Captor
ArgumentCaptor<Todo> todoCaptor;

@Test
void captureArgument() {
    service.create("Neues Todo");

    verify(repository).save(todoCaptor.capture());

    Todo captured = todoCaptor.getValue();
    assertThat(captured.getTitle()).isEqualTo("Neues Todo");
}
```

## Playground: Service mit Exception-Handling

```java
// src/test/java/.../unit/mock/TodoServiceUnitTest.java

@Test
@DisplayName("findById wirft Exception wenn nicht gefunden")
void findByIdThrowsWhenNotFound() {
    when(repository.findById(999L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.findById(999L))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("nicht gefunden");
}
```

**Was wurde gemacht:** Test prueft, dass Service korrekt Exception wirft.

**Warum:** Fehlerbehandlung ist genauso wichtig wie der Happy Path.

## Best Practices

1. **Nur das Noetige mocken** - nicht alles
2. **Behavior Verification** nur wenn relevant
3. **Keine Implementierungsdetails testen** - nur Verhalten
4. **Mocks in @BeforeEach zuruecksetzen** (automatisch mit MockitoExtension)
5. **Lesbare Tests** - when/then liest sich wie Spezifikation
