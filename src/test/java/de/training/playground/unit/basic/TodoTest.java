package de.training.playground.unit.basic;

import de.training.playground.entity.Todo;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Grundlegende Unit-Tests fuer die {@link Todo}-Entity.
 *
 * <p>Demonstriert:
 * <ul>
 *   <li>Einfache JUnit 5 Tests mit {@code @Test}</li>
 *   <li>Lesbare Testnamen mit {@code @DisplayName}</li>
 *   <li>Fluent Assertions mit AssertJ</li>
 * </ul>
 *
 * <p>Diese Tests pruefen die Geschaeftslogik der Entity isoliert,
 * ohne Spring-Kontext oder Datenbank.
 *
 * @see Todo
 * @see org.assertj.core.api.Assertions
 */
@Epic("Unit Tests")
@Feature("basic")
@DisplayName("Todo Entity")
class TodoTest {

    @Test
    @DisplayName("neues Todo ist nicht erledigt")
    void newTodoIsNotDone() {
        Todo todo = new Todo("Einkaufen");

        assertThat(todo.isDone()).isFalse();
    }

    @Test
    @DisplayName("markDone setzt done auf true")
    void markDoneSetsFlag() {
        Todo todo = new Todo("Einkaufen");

        todo.markDone();

        assertThat(todo.isDone()).isTrue();
    }

    @Test
    @DisplayName("ueberfaelliges Todo erkennen")
    void detectsOverdue() {
        Todo todo = new Todo("Gestern faellig", "Test", LocalDate.now().minusDays(1));

        assertThat(todo.isOverdue()).isTrue();
    }

    @Test
    @DisplayName("erledigtes Todo ist nicht ueberfaellig")
    void doneIsNotOverdue() {
        Todo todo = new Todo("Gestern faellig", "Test", LocalDate.now().minusDays(1));
        todo.markDone();

        assertThat(todo.isOverdue()).isFalse();
    }
}
