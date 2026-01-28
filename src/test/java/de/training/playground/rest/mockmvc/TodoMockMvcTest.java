package de.training.playground.rest.mockmvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.training.playground.entity.Todo;
import de.training.playground.repository.TodoRepository;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * REST-API Tests mit MockMvc.
 *
 * <p>MockMvc simuliert HTTP-Requests ohne echten HTTP-Server.
 * Die Requests werden direkt an den DispatcherServlet weitergeleitet.
 *
 * <p>Demonstriert:
 * <ul>
 *   <li>{@code @AutoConfigureMockMvc} - MockMvc automatisch konfigurieren</li>
 *   <li>HTTP-Methoden: GET, POST, PUT, DELETE</li>
 *   <li>{@code jsonPath()} - JSON-Response mit JsonPath pruefen</li>
 *   <li>Status-Code Assertions (200, 404, 204)</li>
 *   <li>Content-Type Pruefung</li>
 *   <li>ObjectMapper fuer Request-Body Serialisierung</li>
 * </ul>
 *
 * <p><b>Unterschied zu RestAssured:</b> MockMvc braucht keinen laufenden Server,
 * ist schneller, aber testet nicht den vollen HTTP-Stack.
 *
 * @see MockMvc
 * @see AutoConfigureMockMvc
 */
@Epic("REST Tests")
@Feature("mockmvc")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TodoMockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TodoRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("GET /api/todos gibt leere Liste zurueck")
    void getAllTodos_whenEmpty_returnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/todos"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("POST /api/todos erstellt neues Todo")
    void createTodo_returnsCreatedTodo() throws Exception {
        Todo todo = new Todo();
        todo.setTitle("REST Test");
        todo.setDescription("Via API erstellt");

        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todo)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.title").value("REST Test"))
            .andExpect(jsonPath("$.description").value("Via API erstellt"));
    }

    @Test
    @DisplayName("GET /api/todos/{id} gibt einzelnes Todo")
    void getTodoById_returnsTodo() throws Exception {
        Todo todo = new Todo();
        todo.setTitle("Find Me");
        Todo saved = repository.save(todo);

        mockMvc.perform(get("/api/todos/" + saved.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Find Me"));
    }

    @Test
    @DisplayName("GET /api/todos/{id} mit unbekannter ID gibt 404")
    void getTodoById_withUnknownId_returns404() throws Exception {
        mockMvc.perform(get("/api/todos/9999"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/todos/{id} aktualisiert Todo")
    void updateTodo_returnsUpdatedTodo() throws Exception {
        Todo todo = new Todo();
        todo.setTitle("Original");
        Todo saved = repository.save(todo);

        saved.setTitle("Updated");

        mockMvc.perform(put("/api/todos/" + saved.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(saved)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Updated"));
    }

    @Test
    @DisplayName("DELETE /api/todos/{id} loescht Todo")
    void deleteTodo_removesTodo() throws Exception {
        Todo todo = new Todo();
        todo.setTitle("Delete Me");
        Todo saved = repository.save(todo);

        mockMvc.perform(delete("/api/todos/" + saved.getId()))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/todos/" + saved.getId()))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/todos gibt mehrere Todos zurueck")
    void getAllTodos_returnsMultipleTodos() throws Exception {
        for (int i = 1; i <= 3; i++) {
            Todo todo = new Todo();
            todo.setTitle("Todo " + i);
            repository.save(todo);
        }

        mockMvc.perform(get("/api/todos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)));
    }
}
