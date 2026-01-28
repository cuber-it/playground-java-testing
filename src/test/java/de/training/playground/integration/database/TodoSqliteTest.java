package de.training.playground.integration.database;

import de.training.playground.entity.Todo;
import de.training.playground.repository.TodoRepository;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Datenbank-Tests mit SQLite.
 *
 * <p>SQLite ist eine dateibasierte Datenbank, die ohne Server auskommt.
 * Im Gegensatz zu H2 schreibt SQLite in eine echte Datei.
 *
 * <p>Demonstriert:
 * <ul>
 *   <li>{@code @ActiveProfiles("sqlite")} - Profil-basierte Konfiguration</li>
 *   <li>{@code @EnabledIfSystemProperty} - bedingte Testausfuehrung</li>
 *   <li>Alternative Datenbank-Konfiguration</li>
 * </ul>
 *
 * <p><b>Voraussetzungen:</b>
 * <ul>
 *   <li>Datei {@code application-sqlite.properties} mit SQLite-Konfiguration</li>
 *   <li>System Property {@code -Drun.sqlite.tests=true}</li>
 * </ul>
 *
 * <p><b>Beispiel application-sqlite.properties:</b>
 * <pre>
 * spring.datasource.url=jdbc:sqlite:test.db
 * spring.datasource.driver-class-name=org.sqlite.JDBC
 * spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect
 * spring.jpa.hibernate.ddl-auto=create-drop
 * </pre>
 *
 * <p>Diese Tests sind standardmaessig deaktiviert.
 *
 * @see EnabledIfSystemProperty
 * @see ActiveProfiles
 */
@Epic("Integration Tests")
@Feature("database")
@SpringBootTest
@ActiveProfiles("sqlite")
@Transactional
@DisplayName("SQLite Datenbank-Tests")
@EnabledIfSystemProperty(named = "run.sqlite.tests", matches = "true")
class TodoSqliteTest {

    @Autowired
    private TodoRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Todo in SQLite speichern")
    void saveToSqlite() {
        Todo todo = new Todo();
        todo.setTitle("SQLite Test");

        Todo saved = repository.save(todo);

        assertThat(saved.getId()).isNotNull();
    }

    @Test
    @DisplayName("Mehrere Todos in SQLite")
    void multipleTodos() {
        repository.save(createTodo("Erstes"));
        repository.save(createTodo("Zweites"));
        repository.save(createTodo("Drittes"));

        List<Todo> all = repository.findAll();

        assertThat(all).hasSize(3);
    }

    @Test
    @DisplayName("SQLite Suche funktioniert")
    void searchInSqlite() {
        repository.save(createTodo("Apfel kaufen"));
        repository.save(createTodo("Birne kaufen"));
        repository.save(createTodo("Sport machen"));

        List<Todo> found = repository.findByTitleContaining("kaufen");

        assertThat(found).hasSize(2);
    }

    private Todo createTodo(String title) {
        Todo todo = new Todo();
        todo.setTitle(title);
        return todo;
    }
}
