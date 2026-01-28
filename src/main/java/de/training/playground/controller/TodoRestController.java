package de.training.playground.controller;

import de.training.playground.entity.Todo;
import de.training.playground.service.TodoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST-Controller fuer die Todo-API.
 *
 * <p>Stellt CRUD-Operationen unter {@code /api/todos} bereit.
 *
 * <h2>Endpunkte</h2>
 * <ul>
 *   <li>{@code GET /api/todos} - alle Todos</li>
 *   <li>{@code GET /api/todos/{id}} - einzelnes Todo</li>
 *   <li>{@code POST /api/todos} - neues Todo</li>
 *   <li>{@code PUT /api/todos/{id}} - Todo aktualisieren</li>
 *   <li>{@code PUT /api/todos/{id}/done} - als erledigt markieren</li>
 *   <li>{@code DELETE /api/todos/{id}} - Todo loeschen</li>
 *   <li>{@code GET /api/todos/open} - offene Todos</li>
 *   <li>{@code GET /api/todos/completed} - erledigte Todos</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/todos")
public class TodoRestController {

    private final TodoService service;

    public TodoRestController(TodoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Todo> getAll() {
        return service.findAll();
    }

    /**
     * Liefert ein einzelnes Todo.
     *
     * @param id die Todo-ID
     * @return 200 OK mit Todo oder 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Todo> getById(@PathVariable Long id) {
        return service.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Todo create(@RequestBody Todo todo) {
        return service.save(todo);
    }

    /**
     * Aktualisiert ein bestehendes Todo.
     *
     * @param id die Todo-ID
     * @param todo die neuen Daten
     * @return 200 OK mit aktualisiertem Todo oder 404 Not Found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Todo> update(@PathVariable Long id, @RequestBody Todo todo) {
        return service.findById(id)
            .map(existing -> {
                existing.setTitle(todo.getTitle());
                existing.setDescription(todo.getDescription());
                existing.setDueDate(todo.getDueDate());
                existing.setDone(todo.isDone());
                return ResponseEntity.ok(service.save(existing));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/done")
    public ResponseEntity<Todo> markDone(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.markDone(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Loescht ein Todo.
     *
     * @param id die Todo-ID
     * @return 204 No Content oder 404 Not Found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (service.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/open")
    public List<Todo> getOpen() {
        return service.findOpen();
    }

    @GetMapping("/completed")
    public List<Todo> getCompleted() {
        return service.findCompleted();
    }
}
