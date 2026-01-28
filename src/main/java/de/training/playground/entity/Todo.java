package de.training.playground.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entitaet fuer eine Aufgabe (Todo).
 *
 * <p>Repraesentiert eine einzelne Aufgabe mit Titel, optionaler Beschreibung,
 * Faelligkeitsdatum und Erledigungsstatus.
 */
@Entity
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private boolean done;
    private LocalDate dueDate;

    /** Default-Konstruktor fuer JPA. */
    public Todo() {}

    /**
     * Erstellt ein neues Todo mit Titel.
     *
     * @param title der Titel der Aufgabe
     */
    public Todo(String title) {
        this.title = title;
        this.done = false;
    }

    /**
     * Erstellt ein neues Todo mit allen Feldern.
     *
     * @param title Titel der Aufgabe
     * @param description optionale Beschreibung
     * @param dueDate Faelligkeitsdatum (kann null sein)
     */
    public Todo(String title, String description, LocalDate dueDate) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.done = false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    /** Markiert dieses Todo als erledigt. */
    public void markDone() { this.done = true; }

    /**
     * Prueft, ob dieses Todo ueberfaellig ist.
     *
     * <p>Ein Todo gilt als ueberfaellig, wenn es ein Faelligkeitsdatum hat,
     * noch nicht erledigt ist und das Datum in der Vergangenheit liegt.
     *
     * @return {@code true} wenn ueberfaellig
     */
    public boolean isOverdue() {
        return dueDate != null && !done && LocalDate.now().isAfter(dueDate);
    }
}
