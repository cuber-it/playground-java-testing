package de.training.playground.unit.testdata;

import de.training.playground.entity.Todo;

import java.time.LocalDate;
import java.util.List;

/**
 * Object Mother Pattern fuer Todo-Testdaten
 * Stellt vordefinierte Test-Objekte bereit
 *
 * Verwendung:
 *   Todo todo = TodoMother.einkaufsTodo();
 *   List<Todo> todos = TodoMother.mehrereOffeneTodos(5);
 */
public class TodoMother {

    private TodoMother() {}

    // === Einzelne Todos ===

    public static Todo einkaufsTodo() {
        return TodoBuilder.aTodo()
            .withTitle("Einkaufen gehen")
            .withDescription("Milch, Brot, Eier")
            .dueTomorrow()
            .build();
    }

    public static Todo sportTodo() {
        return TodoBuilder.aTodo()
            .withTitle("Sport machen")
            .withDescription("30 Minuten joggen")
            .dueInDays(0)
            .build();
    }

    public static Todo lernenTodo() {
        return TodoBuilder.aTodo()
            .withTitle("Java lernen")
            .withDescription("Kapitel 5: Exceptions")
            .dueInDays(3)
            .build();
    }

    public static Todo ueberfaelligesTodo() {
        return TodoBuilder.aTodo()
            .withTitle("Laengst faellig")
            .withDescription("Haette gestern erledigt sein sollen")
            .overdue()
            .build();
    }

    public static Todo erledigtesTodo() {
        return TodoBuilder.aTodo()
            .withTitle("Bereits erledigt")
            .withDescription("Wurde schon gemacht")
            .done()
            .build();
    }

    public static Todo todoOhneFaelligkeit() {
        return TodoBuilder.aTodo()
            .withTitle("Irgendwann")
            .withDescription("Kein Datum")
            .build();
    }

    public static Todo minimalTodo() {
        return TodoBuilder.aTodo()
            .withTitle("Minimal")
            .build();
    }

    // === Listen von Todos ===

    public static List<Todo> alleTagesaufgaben() {
        return List.of(
            einkaufsTodo(),
            sportTodo(),
            lernenTodo()
        );
    }

    public static List<Todo> gemischteTodos() {
        return List.of(
            einkaufsTodo(),
            erledigtesTodo(),
            ueberfaelligesTodo()
        );
    }

    public static List<Todo> mehrereOffeneTodos(int anzahl) {
        return java.util.stream.IntStream.rangeClosed(1, anzahl)
            .mapToObj(i -> TodoBuilder.aTodo()
                .withTitle("Todo " + i)
                .withDescription("Beschreibung " + i)
                .dueInDays(i)
                .build())
            .toList();
    }

    public static List<Todo> mehrereFaelligeHeute(int anzahl) {
        return java.util.stream.IntStream.rangeClosed(1, anzahl)
            .mapToObj(i -> TodoBuilder.aTodo()
                .withTitle("Heute faellig " + i)
                .dueInDays(0)
                .build())
            .toList();
    }

    // === Spezielle Szenarien ===

    public static Todo todoMitAllenFeldern() {
        return TodoBuilder.aTodo()
            .withId(42L)
            .withTitle("Vollstaendiges Todo")
            .withDescription("Hat alle Felder gesetzt")
            .withDueDate(LocalDate.of(2026, 12, 31))
            .build();
    }

    public static Todo todoFuerSuchtest(String suchbegriff) {
        return TodoBuilder.aTodo()
            .withTitle("Enthaelt " + suchbegriff + " im Titel")
            .withDescription("Fuer Suchtests")
            .build();
    }
}
