package de.training.playground.unit.mock;

import de.training.playground.entity.Todo;
import de.training.playground.repository.TodoRepository;
import de.training.playground.service.TodoService;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Erweiterte Unit-Tests fuer den {@link TodoService} mit Mockito.
 *
 * <p>Ergaenzt {@link TodoServiceTest} mit zusaetzlichen Testfaellen
 * und demonstriert:
 * <ul>
 *   <li>Setup mit {@code @BeforeEach} fuer wiederverwendbare Testdaten</li>
 *   <li>JUnit 5 Assertions statt AssertJ (zum Vergleich)</li>
 *   <li>Exception-Tests mit {@code assertThrows}</li>
 *   <li>Verification von Methodenaufrufen</li>
 * </ul>
 *
 * <p>Beide Klassen ({@link TodoServiceTest} und diese) zeigen verschiedene
 * Stile fuer Mock-Tests - AssertJ vs. JUnit Assertions.
 *
 * @see TodoServiceTest
 * @see TodoService
 */
@Epic("Unit Tests")
@Feature("mock")
@ExtendWith(MockitoExtension.class)
class TodoServiceUnitTest {

    @Mock
    private TodoRepository repository;

    @InjectMocks
    private TodoService service;

    private Todo testTodo;

    @BeforeEach
    void setUp() {
        testTodo = new Todo();
        testTodo.setId(1L);
        testTodo.setTitle("Test");
    }

    @Test
    @DisplayName("findAll gibt alle Todos zurueck")
    void findAll_returnsAllTodos() {
        when(repository.findAll()).thenReturn(Arrays.asList(testTodo));

        List<Todo> result = service.findAll();

        assertEquals(1, result.size());
        verify(repository).findAll();
    }

    @Test
    @DisplayName("findById gibt Optional mit Todo zurueck")
    void findById_returnsTodo() {
        when(repository.findById(1L)).thenReturn(Optional.of(testTodo));

        Optional<Todo> result = service.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Test", result.get().getTitle());
    }

    @Test
    @DisplayName("markDone markiert Todo als erledigt")
    void markDone_setsStatusAndSaves() {
        when(repository.findById(1L)).thenReturn(Optional.of(testTodo));
        when(repository.save(any())).thenReturn(testTodo);

        service.markDone(1L);

        assertTrue(testTodo.isDone());
        verify(repository).save(testTodo);
    }

    @Test
    @DisplayName("markDone wirft Exception bei unbekannter ID")
    void markDone_withUnknownId_throwsException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.markDone(99L));
    }

    @Test
    @DisplayName("search findet Todos nach Titel")
    void search_findsByTitle() {
        when(repository.findByTitleContaining("Test")).thenReturn(Arrays.asList(testTodo));

        List<Todo> result = service.search("Test");

        assertEquals(1, result.size());
        verify(repository).findByTitleContaining("Test");
    }
}
