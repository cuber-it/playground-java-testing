package de.training.playground.integration.springboot;

import de.training.playground.entity.Todo;
import de.training.playground.repository.TodoRepository;
import de.training.playground.service.TodoService;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integrationstests mit vollstaendigem Spring-Kontext.
 *
 * <p>{@code @SpringBootTest} laedt die gesamte Spring-Anwendung inklusive
 * aller Beans, Konfigurationen und der eingebetteten Datenbank.
 *
 * <p>Demonstriert:
 * <ul>
 *   <li>{@code @SpringBootTest} - vollstaendiger Anwendungskontext</li>
 *   <li>{@code @Transactional} - automatischer Rollback nach jedem Test</li>
 *   <li>{@code @Autowired} - Dependency Injection in Tests</li>
 *   <li>End-to-End-Test durch alle Schichten (Service → Repository → DB)</li>
 * </ul>
 *
 * <p><b>Vorteile:</b> Realitaetsnah, testet echte Integrationen
 * <br><b>Nachteile:</b> Langsamer als Unit-Tests oder Slice-Tests
 *
 * @see SpringBootTest
 * @see Transactional
 */
@Epic("Integration Tests")
@Feature("springboot")
@SpringBootTest
@Transactional
class TodoSpringBootTest {

    @Autowired
    private TodoService service;

    @Autowired
    private TodoRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Todo speichern und wieder laden")
    void saveAndRetrieve() {
        Todo todo = new Todo();
        todo.setTitle("Integration Test");
        todo.setDescription("Testet Service + Repository");
        todo.setDueDate(LocalDate.now().plusDays(7));

        Todo saved = service.save(todo);

        assertNotNull(saved.getId());
        Todo loaded = service.findById(saved.getId()).orElseThrow();
        assertEquals("Integration Test", loaded.getTitle());
    }

    @Test
    @DisplayName("Mehrere Todos speichern und alle abrufen")
    void saveMultipleAndFindAll() {
        for (int i = 1; i <= 3; i++) {
            Todo todo = new Todo();
            todo.setTitle("Todo " + i);
            service.save(todo);
        }

        List<Todo> all = service.findAll();

        assertEquals(3, all.size());
    }

    @Test
    @DisplayName("Todo als erledigt markieren persistiert Status")
    void markDone_persistsStatus() {
        Todo todo = new Todo();
        todo.setTitle("Zu erledigen");
        Todo saved = service.save(todo);

        service.markDone(saved.getId());

        Todo reloaded = service.findById(saved.getId()).orElseThrow();
        assertTrue(reloaded.isDone());
    }

    @Test
    @DisplayName("Suche findet Todos nach Titelbestandteil")
    void search_findsByTitlePart() {
        Todo todo1 = new Todo();
        todo1.setTitle("Einkaufen gehen");
        service.save(todo1);

        Todo todo2 = new Todo();
        todo2.setTitle("Sport machen");
        service.save(todo2);

        List<Todo> result = service.search("Einkauf");

        assertEquals(1, result.size());
        assertEquals("Einkaufen gehen", result.get(0).getTitle());
    }

    @Test
    @DisplayName("Todo loeschen entfernt aus Datenbank")
    void delete_removesFromDatabase() {
        Todo todo = new Todo();
        todo.setTitle("Zu loeschen");
        Todo saved = service.save(todo);

        service.delete(saved.getId());

        assertTrue(service.findById(saved.getId()).isEmpty());
    }

    @Test
    @DisplayName("findOpen gibt nur unerledigte Todos")
    void findOpen_returnsOnlyOpenTodos() {
        Todo open = new Todo();
        open.setTitle("Offen");
        service.save(open);

        Todo done = new Todo();
        done.setTitle("Erledigt");
        done.markDone();
        service.save(done);

        List<Todo> openTodos = service.findOpen();

        assertEquals(1, openTodos.size());
        assertEquals("Offen", openTodos.get(0).getTitle());
    }
}
