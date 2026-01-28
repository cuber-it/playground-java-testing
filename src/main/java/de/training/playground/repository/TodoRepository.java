package de.training.playground.repository;

import de.training.playground.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository fuer {@link Todo}-Entitaeten.
 *
 * <p>Bietet CRUD-Operationen ueber {@link JpaRepository} sowie
 * benutzerdefinierte Abfragen.
 */
public interface TodoRepository extends JpaRepository<Todo, Long> {

    /**
     * Findet alle Todos mit dem angegebenen Erledigungsstatus.
     *
     * @param done {@code true} fuer erledigte, {@code false} fuer offene Todos
     * @return Liste der passenden Todos
     */
    List<Todo> findByDone(boolean done);

    /**
     * Findet alle Todos, deren Titel den Suchbegriff enthaelt.
     *
     * @param keyword der Suchbegriff (case-sensitive)
     * @return Liste der passenden Todos
     */
    List<Todo> findByTitleContaining(String keyword);
}
