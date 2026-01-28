# Mockito Cheatsheet

## Dependency

**Maven:**
```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>5.12.0</version>
    <scope>test</scope>
</dependency>

<!-- Für JUnit 5 Integration -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <version>5.12.0</version>
    <scope>test</scope>
</dependency>
```

**Gradle:**
```groovy
testImplementation 'org.mockito:mockito-core:5.12.0'
testImplementation 'org.mockito:mockito-junit-jupiter:5.12.0'
```

## Setup

```java
@ExtendWith(MockitoExtension.class)
class MyTest {
    @Mock
    private Repository repo;

    @InjectMocks
    private Service service;

    @Spy
    private Helper helper = new Helper();

    @Captor
    private ArgumentCaptor<Todo> captor;
}
```

## Mock erstellen

```java
// Mit Annotation
@Mock Repository repo;

// Programmatisch
Repository repo = mock(Repository.class);
Repository repo = Mockito.mock(Repository.class);
```

## Stubbing (when...thenReturn)

```java
// Rückgabewert
when(repo.findById(1L)).thenReturn(Optional.of(todo));
when(repo.findAll()).thenReturn(List.of(todo1, todo2));

// Exception
when(repo.findById(999L)).thenThrow(new RuntimeException());

// Mehrere Aufrufe
when(repo.count())
    .thenReturn(0L)      // 1. Aufruf
    .thenReturn(1L)      // 2. Aufruf
    .thenReturn(2L);     // 3.+ Aufruf

// Void-Methoden
doNothing().when(repo).delete(any());
doThrow(new RuntimeException()).when(repo).delete(any());

// Dynamische Antwort
when(repo.save(any())).thenAnswer(inv -> {
    Todo t = inv.getArgument(0);
    t.setId(1L);
    return t;
});
```

## Argument Matchers

```java
any()                    // Beliebiges Objekt
any(Todo.class)          // Beliebiges Todo
anyLong()                // Beliebiger Long
anyString()              // Beliebiger String
anyList()                // Beliebige Liste
eq("exact")              // Exakter Wert
isNull()                 // null
isNotNull()              // nicht null
argThat(t -> t.isDone()) // Custom Matcher
```

**Wichtig:** Entweder alle oder keine Matcher!
```java
// FALSCH
when(repo.find("exact", anyLong()))

// RICHTIG
when(repo.find(eq("exact"), anyLong()))
```

## Verification

```java
// Wurde aufgerufen?
verify(repo).save(any());

// Wie oft?
verify(repo, times(2)).save(any());
verify(repo, never()).delete(any());
verify(repo, atLeastOnce()).findAll();
verify(repo, atMost(3)).findById(anyLong());

// Mit welchen Argumenten?
verify(repo).save(argThat(t -> t.getTitle().equals("Test")));

// Reihenfolge
InOrder inOrder = inOrder(repo);
inOrder.verify(repo).findAll();
inOrder.verify(repo).save(any());

// Keine weiteren Aufrufe
verifyNoMoreInteractions(repo);
verifyNoInteractions(repo);
```

## ArgumentCaptor

```java
@Captor
ArgumentCaptor<Todo> captor;

@Test
void capture() {
    service.create("Test");

    verify(repo).save(captor.capture());

    Todo captured = captor.getValue();
    assertThat(captured.getTitle()).isEqualTo("Test");
}

// Mehrere Aufrufe
List<Todo> allValues = captor.getAllValues();
```

## Spy (Partial Mock)

```java
@Spy
List<String> list = new ArrayList<>();

@Test
void spy() {
    list.add("real");           // Echter Aufruf
    when(list.size()).thenReturn(100);  // Gemockt

    assertThat(list.get(0)).isEqualTo("real");
    assertThat(list.size()).isEqualTo(100);
}
```

## BDD Style

```java
// given
given(repo.findById(1L)).willReturn(Optional.of(todo));

// when
Todo result = service.findById(1L);

// then
then(repo).should().findById(1L);
```

## Vollständiges API-Mocking (Setup-Klasse)

Für wiederverwendbare Mocks kann man das komplette API einer Klasse im Vorfeld stubben:

```java
public class MockedTodoRepository {

    private final TodoRepository mock = mock(TodoRepository.class);
    private final List<Todo> todos = new ArrayList<>();
    private long idCounter = 1L;

    public MockedTodoRepository() {
        setupDefaultBehavior();
    }

    private void setupDefaultBehavior() {
        // findAll
        when(mock.findAll()).thenAnswer(inv -> new ArrayList<>(todos));

        // findById
        when(mock.findById(anyLong())).thenAnswer(inv -> {
            Long id = inv.getArgument(0);
            return todos.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst();
        });

        // save (create + update)
        when(mock.save(any(Todo.class))).thenAnswer(inv -> {
            Todo todo = inv.getArgument(0);
            if (todo.getId() == null) {
                todo.setId(idCounter++);
                todos.add(todo);
            } else {
                todos.removeIf(t -> t.getId().equals(todo.getId()));
                todos.add(todo);
            }
            return todo;
        });

        // saveAll
        when(mock.saveAll(anyList())).thenAnswer(inv -> {
            List<Todo> toSave = inv.getArgument(0);
            return toSave.stream()
                .map(mock::save)
                .toList();
        });

        // delete
        doAnswer(inv -> {
            Todo todo = inv.getArgument(0);
            todos.removeIf(t -> t.getId().equals(todo.getId()));
            return null;
        }).when(mock).delete(any(Todo.class));

        // deleteById
        doAnswer(inv -> {
            Long id = inv.getArgument(0);
            todos.removeIf(t -> t.getId().equals(id));
            return null;
        }).when(mock).deleteById(anyLong());

        // existsById
        when(mock.existsById(anyLong())).thenAnswer(inv -> {
            Long id = inv.getArgument(0);
            return todos.stream().anyMatch(t -> t.getId().equals(id));
        });

        // count
        when(mock.count()).thenAnswer(inv -> (long) todos.size());

        // Custom Query
        when(mock.findByTitleContaining(anyString())).thenAnswer(inv -> {
            String search = inv.getArgument(0);
            return todos.stream()
                .filter(t -> t.getTitle().contains(search))
                .toList();
        });
    }

    public TodoRepository getMock() {
        return mock;
    }

    // Hilfsmethoden für Tests
    public void addExisting(Todo todo) {
        if (todo.getId() == null) {
            todo.setId(idCounter++);
        }
        todos.add(todo);
    }

    public void clear() {
        todos.clear();
        idCounter = 1L;
    }

    public List<Todo> getAll() {
        return new ArrayList<>(todos);
    }
}
```

**Verwendung im Test:**

```java
@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    private MockedTodoRepository mockedRepo;
    private TodoService service;

    @BeforeEach
    void setUp() {
        mockedRepo = new MockedTodoRepository();
        service = new TodoService(mockedRepo.getMock());
    }

    @Test
    void createTodo() {
        // Act
        Todo created = service.create("Test");

        // Assert
        assertThat(created.getId()).isNotNull();
        assertThat(mockedRepo.getAll()).hasSize(1);
    }

    @Test
    void findExisting() {
        // Arrange
        mockedRepo.addExisting(new Todo(1L, "Existing"));

        // Act
        Optional<Todo> found = service.findById(1L);

        // Assert
        assertThat(found).isPresent();
    }

    @Test
    void deleteRemoves() {
        // Arrange
        mockedRepo.addExisting(new Todo(1L, "ToDelete"));

        // Act
        service.delete(1L);

        // Assert
        assertThat(mockedRepo.getAll()).isEmpty();
    }
}
```

## Kurzform mit Builder-Pattern

```java
public class MockBuilder<T> {

    private final T mock;

    private MockBuilder(Class<T> clazz) {
        this.mock = mock(clazz);
    }

    public static <T> MockBuilder<T> of(Class<T> clazz) {
        return new MockBuilder<>(clazz);
    }

    public <R> MockBuilder<T> when(
            Function<T, R> method,
            R returnValue) {
        Mockito.when(method.apply(mock)).thenReturn(returnValue);
        return this;
    }

    public T build() {
        return mock;
    }
}

// Verwendung
TodoRepository repo = MockBuilder.of(TodoRepository.class)
    .when(r -> r.findById(1L), Optional.of(todo))
    .when(r -> r.count(), 5L)
    .build();
```

## Lenient Mocking

```java
// Einzelne Stubbings die vielleicht nicht genutzt werden
lenient().when(repo.findById(999L)).thenReturn(Optional.empty());

// Ganzer Mock als lenient
@Mock(lenient = true)
Repository repo;
```

## Default-Antworten konfigurieren

```java
// Smart Nulls (hilfreichere Fehlermeldungen)
Repository repo = mock(Repository.class, RETURNS_SMART_NULLS);

// Deep Stubs (verkettete Aufrufe)
Config config = mock(Config.class, RETURNS_DEEP_STUBS);
when(config.getDatabase().getUrl()).thenReturn("jdbc:...");

// Selbst zurückgeben (Builder Pattern)
Builder builder = mock(Builder.class, RETURNS_SELF);
builder.setA("a").setB("b");  // Funktioniert ohne Setup
```
