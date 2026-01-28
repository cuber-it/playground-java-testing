package de.training.playground.integration.slice;

import de.training.playground.entity.Todo;
import de.training.playground.repository.TodoRepository;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Slice-Test fuer das {@link TodoRepository} mit {@code @DataJpaTest}.
 *
 * <p>{@code @DataJpaTest} laedt nur JPA-relevante Beans:
 * <ul>
 *   <li>Repositories</li>
 *   <li>EntityManager</li>
 *   <li>DataSource (H2 In-Memory)</li>
 * </ul>
 *
 * <p>Demonstriert:
 * <ul>
 *   <li>{@code @DataJpaTest} - fokussierter JPA-Kontext</li>
 *   <li>{@code TestEntityManager} - direkter Zugriff auf Persistence</li>
 *   <li>{@code flush()} und {@code clear()} - Kontrolle ueber Persistence Context</li>
 *   <li>Custom Query Methods testen</li>
 * </ul>
 *
 * <p><b>Vorteil:</b> Schneller als {@code @SpringBootTest}, da weniger Beans geladen werden.
 *
 * @see DataJpaTest
 * @see TestEntityManager
 */
@Epic("Integration Tests")
@Feature("slice")
@DataJpaTest
@DisplayName("TodoRepository Slice-Test")
class TodoRepositorySliceTest {

    @Autowired
    private TodoRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Todo speichern und per ID finden")
    void saveAndFindById() {
        Todo todo = new Todo();
        todo.setTitle("Slice Test");

        Todo saved = repository.save(todo);
        entityManager.flush();
        entityManager.clear();

        Todo found = repository.findById(saved.getId()).orElseThrow();

        assertThat(found.getTitle()).isEqualTo("Slice Test");
    }

    @Test
    @DisplayName("findByDone findet erledigte Todos")
    void findByDone() {
        Todo open1 = new Todo();
        open1.setTitle("Offen 1");
        entityManager.persist(open1);

        Todo open2 = new Todo();
        open2.setTitle("Offen 2");
        entityManager.persist(open2);

        Todo done = new Todo();
        done.setTitle("Erledigt");
        done.markDone();
        entityManager.persist(done);

        entityManager.flush();

        List<Todo> openTodos = repository.findByDone(false);
        List<Todo> doneTodos = repository.findByDone(true);

        assertThat(openTodos).hasSize(2);
        assertThat(doneTodos).hasSize(1);
        assertThat(doneTodos.get(0).getTitle()).isEqualTo("Erledigt");
    }

    @Test
    @DisplayName("findByTitleContaining findet Todos per Suchbegriff")
    void findByTitleContaining() {
        entityManager.persist(createTodo("Einkaufen gehen"));
        entityManager.persist(createTodo("Sport machen"));
        entityManager.persist(createTodo("Einkaufsliste erstellen"));
        entityManager.flush();

        List<Todo> result = repository.findByTitleContaining("Einkauf");

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Todo::getTitle)
            .allMatch(title -> title.contains("Einkauf"));
    }

    @Test
    @DisplayName("Leere Suche gibt leere Liste")
    void emptySearchReturnsEmptyList() {
        entityManager.persist(createTodo("Test"));
        entityManager.flush();

        List<Todo> result = repository.findByTitleContaining("NICHT_VORHANDEN");

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Todo mit allen Feldern speichern")
    void saveWithAllFields() {
        Todo todo = new Todo();
        todo.setTitle("Vollstaendig");
        todo.setDescription("Mit Beschreibung");
        todo.setDueDate(LocalDate.of(2026, 12, 31));

        Todo saved = repository.save(todo);
        entityManager.flush();
        entityManager.clear();

        Todo found = repository.findById(saved.getId()).orElseThrow();

        assertThat(found.getTitle()).isEqualTo("Vollstaendig");
        assertThat(found.getDescription()).isEqualTo("Mit Beschreibung");
        assertThat(found.getDueDate()).isEqualTo(LocalDate.of(2026, 12, 31));
    }

    private Todo createTodo(String title) {
        Todo todo = new Todo();
        todo.setTitle(title);
        return todo;
    }
}
