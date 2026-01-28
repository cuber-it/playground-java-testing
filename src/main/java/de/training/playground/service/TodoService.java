package de.training.playground.service;

import de.training.playground.entity.Todo;
import de.training.playground.repository.TodoRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * Service-Schicht fuer Todo-Operationen.
 *
 * <p>Kapselt die Geschaeftslogik und dient als Vermittler zwischen
 * Controllern und Repository.
 */
@Service
public class TodoService {

    private final TodoRepository repository;

    public TodoService(TodoRepository repository) {
        this.repository = repository;
    }

    /** Liefert alle Todos. */
    public List<Todo> findAll() {
        return repository.findAll();
    }

    /**
     * Sucht ein Todo anhand seiner ID.
     *
     * @param id die Todo-ID
     * @return das Todo oder empty, falls nicht vorhanden
     */
    public Optional<Todo> findById(Long id) {
        return repository.findById(id);
    }

    /**
     * Speichert ein Todo (neu oder aktualisiert).
     *
     * @param todo das zu speichernde Todo
     * @return das gespeicherte Todo mit generierter ID
     */
    public Todo save(Todo todo) {
        return repository.save(todo);
    }

    /**
     * Loescht ein Todo.
     *
     * @param id die ID des zu loeschenden Todos
     */
    public void delete(Long id) {
        repository.deleteById(id);
    }

    /** Liefert alle offenen (nicht erledigten) Todos. */
    public List<Todo> findOpen() {
        return repository.findByDone(false);
    }

    /** Liefert alle erledigten Todos. */
    public List<Todo> findCompleted() {
        return repository.findByDone(true);
    }

    /**
     * Markiert ein Todo als erledigt.
     *
     * @param id die Todo-ID
     * @return das aktualisierte Todo
     * @throws IllegalArgumentException wenn das Todo nicht existiert
     */
    public Todo markDone(Long id) {
        Todo todo = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Todo not found: " + id));
        todo.markDone();
        return repository.save(todo);
    }

    /**
     * Sucht Todos nach Schluesselwort im Titel.
     *
     * @param keyword der Suchbegriff
     * @return Liste der passenden Todos
     */
    public List<Todo> search(String keyword) {
        return repository.findByTitleContaining(keyword);
    }
}
