package de.training.playground.unit.basic;

import de.training.playground.entity.Todo;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Parameterisierte Tests fuer die {@link Todo}-Entity.
 *
 * <p>Demonstriert alle wichtigen Parameterquellen von JUnit 5:
 * <ul>
 *   <li>{@code @ValueSource} - einfache Werte (Strings, Zahlen, etc.)</li>
 *   <li>{@code @CsvSource} - mehrere Parameter als CSV</li>
 *   <li>{@code @CsvFileSource} - Parameter aus CSV-Datei</li>
 *   <li>{@code @EnumSource} - alle oder ausgewaehlte Enum-Werte</li>
 *   <li>{@code @MethodSource} - komplexe Daten aus Methode</li>
 *   <li>{@code @NullSource}, {@code @EmptySource} - Grenzfaelle</li>
 * </ul>
 *
 * <p>Parameterisierte Tests reduzieren Code-Duplikation und erhoehen
 * die Testabdeckung durch systematisches Testen verschiedener Eingaben.
 *
 * @see ParameterizedTest
 * @see ValueSource
 * @see CsvSource
 * @see MethodSource
 * @see EnumSource
 */
@Epic("Unit Tests")
@Feature("basic")
@DisplayName("Parameterisierte Todo-Tests")
class TodoParameterizedTest {

    // === @ValueSource ===

    @ParameterizedTest(name = "Titel ''{0}'' ist gueltig")
    @ValueSource(strings = {"Einkaufen", "Sport", "Lernen", "Arbeiten"})
    @DisplayName("Todo mit verschiedenen Titeln erstellen")
    void createTodoWithDifferentTitles(String title) {
        Todo todo = new Todo(title);

        assertThat(todo.getTitle()).isEqualTo(title);
        assertThat(todo.isDone()).isFalse();
    }

    // === @CsvSource ===

    @ParameterizedTest(name = "Titel=''{0}'', Beschreibung=''{1}''")
    @CsvSource({
        "Einkaufen, Milch und Brot",
        "Sport, Joggen im Park",
        "Lernen, Java Kapitel 5"
    })
    @DisplayName("Todo mit Titel und Beschreibung aus CSV")
    void createTodoFromCsv(String title, String description) {
        Todo todo = new Todo();
        todo.setTitle(title);
        todo.setDescription(description);

        assertThat(todo.getTitle()).isEqualTo(title);
        assertThat(todo.getDescription()).isEqualTo(description);
    }

    // === @CsvFileSource ===
    // Wuerde aus src/test/resources/todos.csv lesen
    // @ParameterizedTest
    // @CsvFileSource(resources = "/todos.csv", numLinesToSkip = 1)
    // void createTodoFromCsvFile(String title, String description) { ... }

    // === @EnumSource ===

    enum Priority { LOW, MEDIUM, HIGH, CRITICAL }

    @ParameterizedTest(name = "Prioritaet {0}")
    @EnumSource(Priority.class)
    @DisplayName("Alle Prioritaeten durchlaufen")
    void testAllPriorities(Priority priority) {
        // Beispiel: Todo koennte eine Prioritaet haben
        assertThat(priority).isNotNull();
    }

    @ParameterizedTest(name = "Prioritaet {0} (nur hohe)")
    @EnumSource(value = Priority.class, names = {"HIGH", "CRITICAL"})
    @DisplayName("Nur hohe Prioritaeten")
    void testHighPrioritiesOnly(Priority priority) {
        assertThat(priority.name()).containsAnyOf("HIGH", "CRITICAL");
    }

    // === @MethodSource ===

    @ParameterizedTest(name = "Faellig in {0} Tagen -> ueberfaellig={1}")
    @MethodSource("provideDueDates")
    @DisplayName("Ueberfaelligkeitspruefung mit verschiedenen Daten")
    void testOverdueWithDifferentDates(int daysFromNow, boolean expectedOverdue) {
        Todo todo = new Todo();
        todo.setTitle("Test");
        todo.setDueDate(LocalDate.now().plusDays(daysFromNow));

        assertThat(todo.isOverdue()).isEqualTo(expectedOverdue);
    }

    static Stream<Arguments> provideDueDates() {
        return Stream.of(
            Arguments.of(-7, true),   // 7 Tage in der Vergangenheit
            Arguments.of(-1, true),   // Gestern
            Arguments.of(0, false),   // Heute
            Arguments.of(1, false),   // Morgen
            Arguments.of(7, false)    // In einer Woche
        );
    }

    // === @NullSource und @EmptySource ===

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("Leere oder Whitespace-Titel")
    void testBlankTitles(String title) {
        Todo todo = new Todo();
        todo.setTitle(title);

        // Titel kann null, leer oder Whitespace sein
        assertThat(todo.getTitle()).satisfiesAnyOf(
            t -> assertThat(t).isNull(),
            t -> assertThat(t).isBlank()
        );
    }

    // === Kombinierter Test ===

    @ParameterizedTest(name = "Todo ''{0}'' mit {1} Tagen Faelligkeit")
    @CsvSource({
        "Wichtig, -1, true",
        "Normal, 0, false",
        "Spaeter, 7, false"
    })
    @DisplayName("Kompletter Todo-Test aus CSV")
    void testCompleteTodoFromCsv(String title, int daysFromNow, boolean expectedOverdue) {
        Todo todo = new Todo();
        todo.setTitle(title);
        todo.setDueDate(LocalDate.now().plusDays(daysFromNow));

        assertThat(todo.getTitle()).isEqualTo(title);
        assertThat(todo.isOverdue()).isEqualTo(expectedOverdue);
    }
}
