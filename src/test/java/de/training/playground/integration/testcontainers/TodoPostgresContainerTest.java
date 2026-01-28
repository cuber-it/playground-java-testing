package de.training.playground.integration.testcontainers;

import de.training.playground.entity.Todo;
import de.training.playground.repository.TodoRepository;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TestContainers-Integration mit PostgreSQL und Spring Boot.
 *
 * <p>TestContainers startet eine echte PostgreSQL-Datenbank in Docker,
 * sodass Tests gegen die Produktionsdatenbank-Technologie laufen.
 *
 * <p>Demonstriert:
 * <ul>
 *   <li>{@code @Testcontainers} - JUnit 5 Extension aktivieren</li>
 *   <li>{@code @Container} - Container-Lifecycle verwalten</li>
 *   <li>{@code @DynamicPropertySource} - Spring mit Container verbinden</li>
 *   <li>{@code static}-Container fuer Wiederverwendung zwischen Tests</li>
 *   <li>Alle Repository-Operationen gegen echte PostgreSQL</li>
 * </ul>
 *
 * <p><b>Voraussetzung:</b> Docker muss laufen!
 *
 * <p><b>Wann TestContainers statt H2?</b>
 * <ul>
 *   <li>Komplexe SQL-Queries (JSON, Arrays, Window Functions)</li>
 *   <li>Datenbank-spezifische Features</li>
 *   <li>Migrations testen (Flyway, Liquibase)</li>
 *   <li>Produktionsnaehe wichtiger als Geschwindigkeit</li>
 * </ul>
 *
 * @see Testcontainers
 * @see Container
 * @see DynamicPropertySource
 * @see PostgreSQLContainer
 */
@Epic("Integration Tests")
@Feature("testcontainers")
@SpringBootTest
@Testcontainers
@DisplayName("TestContainers: PostgreSQL")
class TodoPostgresContainerTest {

    /**
     * Container wird einmal pro Testklasse gestartet (static)
     * Bei nicht-static wuerde er pro Test neu starten
     */
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private TodoRepository repository;

    /**
     * Verbindet Spring mit dem TestContainer
     * Wird VOR dem ApplicationContext-Start aufgerufen
     */
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Container laeuft und ist erreichbar")
    void containerIsRunning() {
        assertThat(postgres.isRunning()).isTrue();
        assertThat(postgres.getJdbcUrl()).contains("postgres");
    }

    @Test
    @DisplayName("Todo in PostgreSQL speichern und laden")
    void shouldSaveAndLoadTodo() {
        // Arrange
        Todo todo = new Todo("PostgreSQL Test", "Laeuft auf echtem Postgres", LocalDate.now());

        // Act
        Todo saved = repository.save(todo);
        Todo loaded = repository.findById(saved.getId()).orElseThrow();

        // Assert
        assertThat(loaded.getTitle()).isEqualTo("PostgreSQL Test");
        assertThat(loaded.getId()).isNotNull();
    }

    @Test
    @DisplayName("Mehrere Todos speichern")
    void shouldSaveMultipleTodos() {
        // Arrange
        repository.save(new Todo("Todo 1"));
        repository.save(new Todo("Todo 2"));
        repository.save(new Todo("Todo 3"));

        // Act
        List<Todo> all = repository.findAll();

        // Assert
        assertThat(all).hasSize(3);
    }

    @Test
    @DisplayName("Todos nach Status filtern")
    void shouldFilterByDone() {
        // Arrange
        Todo open1 = new Todo("Offen 1");
        Todo open2 = new Todo("Offen 2");
        Todo done = new Todo("Erledigt");
        done.markDone();

        repository.saveAll(List.of(open1, open2, done));

        // Act
        List<Todo> openTodos = repository.findByDone(false);
        List<Todo> doneTodos = repository.findByDone(true);

        // Assert
        assertThat(openTodos).hasSize(2);
        assertThat(doneTodos).hasSize(1);
    }

    @Test
    @DisplayName("Todos per Titelsuche finden")
    void shouldFindByTitleContaining() {
        // Arrange
        repository.save(new Todo("Einkaufen gehen"));
        repository.save(new Todo("Sport machen"));
        repository.save(new Todo("Einkaufsliste schreiben"));

        // Act
        List<Todo> results = repository.findByTitleContaining("Einkauf");

        // Assert
        assertThat(results).hasSize(2);
        assertThat(results).extracting(Todo::getTitle)
                .allMatch(title -> title.contains("Einkauf"));
    }

    @Test
    @DisplayName("Todo loeschen")
    void shouldDeleteTodo() {
        // Arrange
        Todo todo = repository.save(new Todo("Wird geloescht"));
        Long id = todo.getId();

        // Act
        repository.deleteById(id);

        // Assert
        assertThat(repository.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("Todo aktualisieren")
    void shouldUpdateTodo() {
        // Arrange
        Todo todo = repository.save(new Todo("Original"));

        // Act
        todo.setTitle("Aktualisiert");
        todo.markDone();
        repository.save(todo);

        // Assert
        Todo updated = repository.findById(todo.getId()).orElseThrow();
        assertThat(updated.getTitle()).isEqualTo("Aktualisiert");
        assertThat(updated.isDone()).isTrue();
    }
}
