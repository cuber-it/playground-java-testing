package de.training.playground.unit.mock;

import de.training.playground.entity.Todo;
import de.training.playground.repository.TodoRepository;
import de.training.playground.service.TodoService;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Unit-Tests fuer den {@link TodoService} mit Mockito.
 *
 * <p>Demonstriert die wichtigsten Mockito-Konzepte:
 * <ul>
 *   <li>{@code @Mock} - erstellt Mock-Objekt fuer Dependency</li>
 *   <li>{@code @InjectMocks} - injiziert Mocks in das SUT (System Under Test)</li>
 *   <li>{@code @ExtendWith(MockitoExtension.class)} - aktiviert Mockito fuer JUnit 5</li>
 *   <li>{@code when(...).thenReturn(...)} - Stubbing: definiert Rueckgabewerte</li>
 *   <li>{@code verify(...)} - Verification: prueft ob Methode aufgerufen wurde</li>
 * </ul>
 *
 * <p>Durch das Mocken des {@link TodoRepository} wird der Service isoliert
 * getestet, ohne echte Datenbankzugriffe.
 *
 * @see Mock
 * @see InjectMocks
 * @see MockitoExtension
 */
@Epic("Unit Tests")
@Feature("mock")
@ExtendWith(MockitoExtension.class)
@DisplayName("TodoService")
class TodoServiceTest {

    @Mock
    TodoRepository repository;

    @InjectMocks
    TodoService service;

    @Test
    @DisplayName("findAll gibt alle Todos zurueck")
    void findAllReturnsList() {
        when(repository.findAll()).thenReturn(List.of(
            new Todo("Eins"),
            new Todo("Zwei")
        ));

        List<Todo> result = service.findAll();

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("markDone wirft Exception wenn nicht gefunden")
    void markDoneThrowsWhenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.markDone(99L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("99");
    }

    @Test
    @DisplayName("markDone setzt done und speichert")
    void markDoneSavesUpdatedTodo() {
        Todo todo = new Todo("Test");
        when(repository.findById(1L)).thenReturn(Optional.of(todo));
        when(repository.save(any())).thenReturn(todo);

        Todo result = service.markDone(1L);

        assertThat(result.isDone()).isTrue();
        verify(repository).save(todo);
    }
}
