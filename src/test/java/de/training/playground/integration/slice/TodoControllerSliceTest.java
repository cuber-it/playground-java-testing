package de.training.playground.integration.slice;

import de.training.playground.controller.TodoRestController;
import de.training.playground.entity.Todo;
import de.training.playground.service.TodoService;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Slice-Test fuer den {@link TodoRestController} mit {@code @WebMvcTest}.
 *
 * <p>{@code @WebMvcTest} laedt nur den Web-Layer:
 * <ul>
 *   <li>Controller</li>
 *   <li>Filter</li>
 *   <li>WebMvcConfigurer</li>
 *   <li>MockMvc (automatisch konfiguriert)</li>
 * </ul>
 *
 * <p>Demonstriert:
 * <ul>
 *   <li>{@code @WebMvcTest} - fokussierter Web-Kontext</li>
 *   <li>{@code @MockBean} - Service-Layer mocken</li>
 *   <li>{@code MockMvc} - HTTP-Requests simulieren</li>
 *   <li>{@code jsonPath()} - JSON-Response pruefen</li>
 *   <li>Status-Codes testen (200, 404, 204)</li>
 * </ul>
 *
 * <p><b>Vorteil:</b> Testet Controller isoliert ohne Service- und Repository-Layer.
 *
 * @see WebMvcTest
 * @see MockMvc
 * @see MockBean
 */
@Epic("Integration Tests")
@Feature("slice")
@WebMvcTest(TodoRestController.class)
@DisplayName("TodoRestController Slice-Test")
class TodoControllerSliceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;

    @Test
    @DisplayName("GET /api/todos gibt alle Todos")
    void getAllTodos() throws Exception {
        Todo todo1 = createTodo(1L, "Todo 1");
        Todo todo2 = createTodo(2L, "Todo 2");
        when(todoService.findAll()).thenReturn(List.of(todo1, todo2));

        mockMvc.perform(get("/api/todos"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].title").value("Todo 1"))
            .andExpect(jsonPath("$[1].title").value("Todo 2"));
    }

    @Test
    @DisplayName("GET /api/todos/{id} gibt einzelnes Todo")
    void getTodoById() throws Exception {
        Todo todo = createTodo(1L, "Mein Todo");
        when(todoService.findById(1L)).thenReturn(Optional.of(todo));

        mockMvc.perform(get("/api/todos/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Mein Todo"));
    }

    @Test
    @DisplayName("GET /api/todos/{id} mit unbekannter ID gibt 404")
    void getTodoById_notFound() throws Exception {
        when(todoService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/todos/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/todos erstellt neues Todo")
    void createTodo() throws Exception {
        Todo savedTodo = createTodo(1L, "Neues Todo");
        when(todoService.save(any(Todo.class))).thenReturn(savedTodo);

        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"Neues Todo\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Neues Todo"));
    }

    @Test
    @DisplayName("DELETE /api/todos/{id} loescht Todo")
    void deleteTodo() throws Exception {
        when(todoService.findById(1L)).thenReturn(Optional.of(createTodo(1L, "Test")));

        mockMvc.perform(delete("/api/todos/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Leere Todos-Liste")
    void emptyTodosList() throws Exception {
        when(todoService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/todos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

    private Todo createTodo(Long id, String title) {
        Todo todo = new Todo();
        todo.setId(id);
        todo.setTitle(title);
        return todo;
    }
}
