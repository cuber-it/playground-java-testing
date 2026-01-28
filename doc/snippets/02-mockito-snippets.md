# Mockito Snippets

Copy-Paste fertige Code-Blöcke.

---

## Test-Klasse mit Mocks

```java
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MyServiceTest {

    @Mock
    MyRepository repository;

    @InjectMocks
    MyService sut;

    @Test
    void shouldDoSomething() {
        // Arrange
        when(repository.findAll()).thenReturn(List.of());

        // Act
        var result = sut.getAll();

        // Assert
        assertThat(result).isEmpty();
        verify(repository).findAll();
    }
}
```

---

## Stubbing - Rückgabewerte

```java
// Einfacher Wert
when(repository.findById(1L)).thenReturn(Optional.of(entity));

// Liste
when(repository.findAll()).thenReturn(List.of(entity1, entity2));

// Null
when(repository.findByName("unknown")).thenReturn(null);

// Optional.empty()
when(repository.findById(999L)).thenReturn(Optional.empty());
```

---

## Stubbing - Exceptions

```java
// Exception werfen
when(repository.findById(999L))
    .thenThrow(new EntityNotFoundException("Not found"));

// RuntimeException
when(repository.save(any()))
    .thenThrow(new RuntimeException("Database error"));
```

---

## Stubbing - Mehrere Aufrufe

```java
// Unterschiedliche Rückgaben pro Aufruf
when(repository.count())
    .thenReturn(0L)    // 1. Aufruf
    .thenReturn(1L)    // 2. Aufruf
    .thenReturn(2L);   // 3. und weitere
```

---

## Stubbing - Void-Methoden

```java
// Nichts tun (default)
doNothing().when(repository).delete(any());

// Exception werfen
doThrow(new RuntimeException("Error"))
    .when(repository).delete(any());

// Custom Logik
doAnswer(inv -> {
    Entity e = inv.getArgument(0);
    System.out.println("Deleting: " + e.getId());
    return null;
}).when(repository).delete(any());
```

---

## Stubbing - Dynamische Antworten

```java
// Argument zurückgeben
when(repository.save(any(Entity.class))).thenAnswer(inv -> {
    Entity entity = inv.getArgument(0);
    entity.setId(1L);
    return entity;
});

// Basierend auf Argument
when(repository.findById(anyLong())).thenAnswer(inv -> {
    Long id = inv.getArgument(0);
    if (id < 0) {
        return Optional.empty();
    }
    return Optional.of(new Entity(id));
});
```

---

## Argument Matchers

```java
// Beliebige Werte
when(repository.findById(anyLong())).thenReturn(Optional.empty());
when(repository.findByName(anyString())).thenReturn(List.of());
when(repository.save(any(Entity.class))).thenReturn(entity);

// Exakter Wert (mit eq() wenn andere Matcher verwendet)
when(repository.findByNameAndStatus(eq("test"), any())).thenReturn(List.of());

// Null / NotNull
when(repository.process(isNull())).thenThrow(new IllegalArgumentException());
when(repository.process(isNotNull())).thenReturn("OK");

// Custom Matcher
when(repository.save(argThat(e -> e.getName().startsWith("Test"))))
    .thenReturn(entity);
```

---

## Verification

```java
// Wurde aufgerufen
verify(repository).findAll();

// Anzahl Aufrufe
verify(repository, times(2)).save(any());
verify(repository, never()).delete(any());
verify(repository, atLeastOnce()).findById(anyLong());
verify(repository, atMost(3)).count();

// Mit bestimmten Argumenten
verify(repository).findById(1L);
verify(repository).save(argThat(e -> e.getName().equals("Test")));

// Keine Interaktionen
verifyNoInteractions(repository);
verifyNoMoreInteractions(repository);
```

---

## Reihenfolge prüfen

```java
InOrder inOrder = inOrder(repository, notificationService);

inOrder.verify(repository).save(any());
inOrder.verify(notificationService).sendNotification(any());
```

---

## ArgumentCaptor

```java
@Captor
ArgumentCaptor<Entity> entityCaptor;

@Test
void shouldCaptureArgument() {
    // Act
    service.create("Test");

    // Capture
    verify(repository).save(entityCaptor.capture());

    // Assert
    Entity captured = entityCaptor.getValue();
    assertThat(captured.getName()).isEqualTo("Test");
}

// Mehrere Aufrufe
@Test
void shouldCaptureMultiple() {
    service.createAll(List.of("A", "B", "C"));

    verify(repository, times(3)).save(entityCaptor.capture());

    List<Entity> allCaptured = entityCaptor.getAllValues();
    assertThat(allCaptured).hasSize(3);
}
```

---

## Spy (Partial Mock)

```java
@Spy
List<String> spyList = new ArrayList<>();

@Test
void shouldUseRealMethods() {
    spyList.add("one");
    spyList.add("two");

    // Real method
    assertThat(spyList.get(0)).isEqualTo("one");

    // Override
    when(spyList.size()).thenReturn(100);
    assertThat(spyList.size()).isEqualTo(100);
}
```

---

## BDD Style

```java
import static org.mockito.BDDMockito.*;

@Test
void shouldProcess() {
    // Given
    given(repository.findById(1L)).willReturn(Optional.of(entity));

    // When
    var result = service.process(1L);

    // Then
    then(repository).should().findById(1L);
    assertThat(result).isNotNull();
}
```

---

## Reset Mock

```java
@Test
void shouldResetMock() {
    when(repository.count()).thenReturn(5L);

    assertThat(repository.count()).isEqualTo(5L);

    reset(repository);  // Alle Stubbings entfernt

    assertThat(repository.count()).isEqualTo(0L);  // Default
}
```

---

## Lenient Mocking

```java
// Einzelnes Stubbing (wird vielleicht nicht genutzt)
lenient().when(repository.findById(999L)).thenReturn(Optional.empty());

// Ganzer Mock
@Mock(lenient = true)
Repository repository;
```
