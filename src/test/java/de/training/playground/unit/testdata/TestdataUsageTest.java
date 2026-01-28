package de.training.playground.unit.testdata;

import de.training.playground.entity.Todo;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Demonstriert die Verwendung von Builder und Object Mother
 */
@Epic("Unit Tests")
@Feature("testdata")
@DisplayName("Testdaten-Verwendung")
class TestdataUsageTest {

    // === Builder-Verwendung ===

    @Test
    @DisplayName("Todo mit Builder erstellen")
    void createTodoWithBuilder() {
        Todo todo = TodoBuilder.aTodo()
            .withTitle("Mein Todo")
            .withDescription("Eine Beschreibung")
            .dueTomorrow()
            .build();

        assertThat(todo.getTitle()).isEqualTo("Mein Todo");
        assertThat(todo.getDescription()).isEqualTo("Eine Beschreibung");
        assertThat(todo.getDueDate()).isEqualTo(LocalDate.now().plusDays(1));
    }

    @Test
    @DisplayName("Ueberfaelliges Todo mit Builder")
    void createOverdueTodoWithBuilder() {
        Todo todo = TodoBuilder.aTodo()
            .withTitle("Ueberfaellig")
            .overdue()
            .build();

        assertThat(todo.isOverdue()).isTrue();
    }

    @Test
    @DisplayName("Erledigtes Todo mit Builder")
    void createDoneTodoWithBuilder() {
        Todo todo = TodoBuilder.aTodo()
            .withTitle("Erledigt")
            .done()
            .build();

        assertThat(todo.isDone()).isTrue();
    }

    // === Object Mother-Verwendung ===

    @Test
    @DisplayName("Vordefiniertes Einkaufs-Todo")
    void useEinkaufsTodo() {
        Todo todo = TodoMother.einkaufsTodo();

        assertThat(todo.getTitle()).contains("Einkaufen");
        assertThat(todo.getDescription()).isNotBlank();
        assertThat(todo.getDueDate()).isNotNull();
    }

    @Test
    @DisplayName("Liste von Todos erstellen")
    void useMultipleTodos() {
        List<Todo> todos = TodoMother.mehrereOffeneTodos(5);

        assertThat(todos).hasSize(5);
        assertThat(todos).allMatch(t -> !t.isDone());
    }

    @Test
    @DisplayName("Gemischte Todos fuer komplexe Tests")
    void useGemischteTodos() {
        List<Todo> todos = TodoMother.gemischteTodos();

        long offene = todos.stream().filter(t -> !t.isDone()).count();
        long erledigte = todos.stream().filter(Todo::isDone).count();
        long ueberfaellige = todos.stream().filter(Todo::isOverdue).count();

        assertThat(offene).isGreaterThan(0);
        assertThat(erledigte).isGreaterThan(0);
        assertThat(ueberfaellige).isGreaterThan(0);
    }

    // === Kombination ===

    @Test
    @DisplayName("Builder und Mother kombinieren")
    void combineBuilderAndMother() {
        // Existierendes Todo als Basis nehmen und modifizieren
        Todo original = TodoMother.einkaufsTodo();

        Todo modifiziert = TodoBuilder.from(original)
            .withTitle("Modifiziertes Einkaufen")
            .done()
            .build();

        assertThat(modifiziert.getTitle()).isEqualTo("Modifiziertes Einkaufen");
        assertThat(modifiziert.isDone()).isTrue();
        // Beschreibung wurde uebernommen
        assertThat(modifiziert.getDescription()).isEqualTo(original.getDescription());
    }
}
