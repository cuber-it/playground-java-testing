package de.training.playground.unit.testdata;

import de.training.playground.entity.Todo;

import java.time.LocalDate;

/**
 * Builder Pattern fuer Todo-Testdaten
 * Ermoeglicht fluente Erstellung von Test-Todos
 *
 * Verwendung:
 *   Todo todo = TodoBuilder.aTodo()
 *       .withTitle("Einkaufen")
 *       .withDueDate(LocalDate.now().plusDays(7))
 *       .build();
 */
public class TodoBuilder {

    private Long id;
    private String title = "Default Todo";
    private String description;
    private LocalDate dueDate;
    private boolean done = false;

    private TodoBuilder() {}

    public static TodoBuilder aTodo() {
        return new TodoBuilder();
    }

    public static TodoBuilder aDefaultTodo() {
        return aTodo()
            .withTitle("Standard Todo")
            .withDescription("Automatisch erstellt");
    }

    public TodoBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public TodoBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public TodoBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public TodoBuilder withDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public TodoBuilder dueTomorrow() {
        this.dueDate = LocalDate.now().plusDays(1);
        return this;
    }

    public TodoBuilder dueInDays(int days) {
        this.dueDate = LocalDate.now().plusDays(days);
        return this;
    }

    public TodoBuilder overdue() {
        this.dueDate = LocalDate.now().minusDays(1);
        return this;
    }

    public TodoBuilder done() {
        this.done = true;
        return this;
    }

    public TodoBuilder notDone() {
        this.done = false;
        return this;
    }

    public Todo build() {
        Todo todo = new Todo();
        todo.setId(id);
        todo.setTitle(title);
        todo.setDescription(description);
        todo.setDueDate(dueDate);
        if (done) {
            todo.markDone();
        }
        return todo;
    }

    /**
     * Kopiert ein bestehendes Todo und erlaubt Modifikationen
     */
    public static TodoBuilder from(Todo source) {
        return aTodo()
            .withId(source.getId())
            .withTitle(source.getTitle())
            .withDescription(source.getDescription())
            .withDueDate(source.getDueDate());
    }
}
