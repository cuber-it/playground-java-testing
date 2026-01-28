package de.training.playground.unit.basic;

import de.training.playground.entity.Todo;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit-Tests fuer die {@link Todo}-Entity mit JUnit 5 Lifecycle-Methoden.
 *
 * <p>Demonstriert:
 * <ul>
 *   <li>{@code @BeforeAll} - einmalig vor allen Tests</li>
 *   <li>{@code @BeforeEach} - vor jedem einzelnen Test</li>
 *   <li>{@code @AfterEach} - nach jedem einzelnen Test</li>
 *   <li>{@code @AfterAll} - einmalig nach allen Tests</li>
 * </ul>
 *
 * <p>Die Lifecycle-Methoden ermoeglichen:
 * <ul>
 *   <li>Gemeinsames Setup fuer alle Tests (DRY-Prinzip)</li>
 *   <li>Sauberes Aufräumen nach Tests</li>
 *   <li>Isolation zwischen Tests</li>
 * </ul>
 *
 * @see BeforeAll
 * @see BeforeEach
 * @see AfterEach
 * @see AfterAll
 */
@Epic("Unit Tests")
@Feature("basic")
class TodoUnitTest {

    private Todo todo;

    @BeforeAll
    static void beforeAll() {
        Allure.step("Unit Tests starten");
    }

    @BeforeEach
    void setUp() {
        todo = new Todo();
        todo.setTitle("Test Todo");
        todo.setDescription("Test Beschreibung");
    }

    @AfterEach
    void tearDown() {
        todo = null;
    }

    @AfterAll
    static void afterAll() {
        Allure.step("Unit Tests beendet");
    }

    @Test
    @DisplayName("Todo markDone setzt done auf true")
    @Story("Todo als erledigt markieren")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Testet die markDone()-Methode der Todo-Entity")
    void markDone_setsStatusToTrue() {
        assertFalse(todo.isDone());

        todo.markDone();

        assertTrue(todo.isDone());
    }

    @Test
    @DisplayName("Todo ohne Datum ist nicht ueberfaellig")
    @Story("Ueberfaelligkeitspruefung")
    @Severity(SeverityLevel.NORMAL)
    void isOverdue_withoutDate_returnsFalse() {
        todo.setDueDate(null);

        assertFalse(todo.isOverdue());
    }

    @Test
    @DisplayName("Todo mit zukuenftigem Datum ist nicht ueberfaellig")
    @Story("Ueberfaelligkeitspruefung")
    @Severity(SeverityLevel.NORMAL)
    void isOverdue_withFutureDate_returnsFalse() {
        todo.setDueDate(LocalDate.now().plusDays(7));

        assertFalse(todo.isOverdue());
    }

    @Test
    @DisplayName("Todo mit vergangenem Datum ist ueberfaellig")
    @Story("Ueberfaelligkeitspruefung")
    @Severity(SeverityLevel.NORMAL)
    void isOverdue_withPastDate_returnsTrue() {
        todo.setDueDate(LocalDate.now().minusDays(1));

        assertTrue(todo.isOverdue());
    }

    @Test
    @DisplayName("Erledigtes Todo ist nie ueberfaellig")
    @Story("Ueberfaelligkeitspruefung")
    @Severity(SeverityLevel.CRITICAL)
    void isOverdue_whenDone_returnsFalse() {
        todo.setDueDate(LocalDate.now().minusDays(1));
        todo.markDone();

        assertFalse(todo.isOverdue());
    }
}
