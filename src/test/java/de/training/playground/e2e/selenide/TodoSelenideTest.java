package de.training.playground.e2e.selenide;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;

/**
 * End-to-End Tests mit Selenide.
 *
 * <p>Selenide ist ein eleganter Wrapper um Selenium mit deutlich
 * kompakterer Syntax und automatischen Waits.
 *
 * <p>Demonstriert:
 * <ul>
 *   <li>{@code $("selector")} - einzelnes Element finden</li>
 *   <li>{@code $$("selector")} - mehrere Elemente finden</li>
 *   <li>{@code shouldHave()}, {@code shouldBe()} - fluente Assertions</li>
 *   <li>{@code Configuration} - globale Einstellungen</li>
 *   <li>Automatische Waits (kein explizites Warten noetig)</li>
 *   <li>Collection Conditions fuer Listen</li>
 * </ul>
 *
 * <p><b>Vorteile gegenueber Selenium:</b>
 * <ul>
 *   <li>Kuerzerer, lesbarerer Code</li>
 *   <li>Eingebaute Smart Waits</li>
 *   <li>Automatisches Screenshot bei Fehlern</li>
 *   <li>Keine WebDriverManager-Konfiguration noetig</li>
 * </ul>
 *
 * <p><b>Vergleich Selenium vs Selenide:</b>
 * <pre>
 * // Selenium:
 * wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".todo")));
 * driver.findElement(By.cssSelector(".todo")).click();
 *
 * // Selenide:
 * $(".todo").shouldBe(visible).click();
 * </pre>
 *
 * @see com.codeborne.selenide.Selenide
 * @see com.codeborne.selenide.Condition
 */
@Epic("E2E Tests")
@Feature("selenide")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TodoSelenideTest {

    @LocalServerPort
    private int port;

    @BeforeAll
    static void setupAll() {
        Configuration.browser = "chrome";
        Configuration.headless = true;
        Configuration.timeout = 10000;
    }

    @BeforeEach
    void setup() {
        Configuration.baseUrl = "http://localhost:" + port;
    }

    @AfterEach
    void tearDown() {
        Selenide.closeWebDriver();
    }

    @Test
    @DisplayName("Startseite zeigt Titel")
    void homePage_showsTitle() {
        open("/");

        $("h1").shouldHave(text("Meine Todos"));
    }

    @Test
    @DisplayName("Neues Todo hinzufuegen")
    void addTodo_showsInList() {
        open("/");

        // Formular ausfuellen - Selenide Syntax ist viel kuerzer
        $("input[placeholder='Titel']").setValue("Selenide Test");
        $("input[placeholder='Beschreibung']").setValue("Via Selenide erstellt");
        $("button.btn-add").click();

        // Pruefen - automatisches Warten eingebaut
        $(".todo-title").shouldHave(text("Selenide Test"));
    }

    @Test
    @DisplayName("Todo als erledigt markieren")
    void markDone_showsStrikethrough() {
        open("/");

        // Todo erstellen
        $("input[placeholder='Titel']").setValue("Zu erledigen");
        $("button.btn-add").click();

        // Erledigt markieren
        $(".btn-done").click();

        // Pruefen
        $(".todo-title.done").should(exist);
    }

    @Test
    @DisplayName("Todo loeschen")
    void deleteTodo_removesFromList() {
        open("/");

        // Todo erstellen
        $("input[placeholder='Titel']").setValue("Zu loeschen");
        $("button.btn-add").click();
        $(".todo").should(exist);

        // Loeschen
        $(".btn-delete").click();

        // Pruefen dass "Keine Todos" erscheint
        $(byText("Keine Todos vorhanden.")).should(exist);
    }

    @Test
    @DisplayName("Suche filtert Todos")
    void search_filtersResults() {
        open("/");

        // Zwei Todos erstellen
        $("input[placeholder='Titel']").setValue("Apfel kaufen");
        $("button.btn-add").click();
        $(".todo").shouldHave(text("Apfel"));

        $("input[placeholder='Titel']").setValue("Birne kaufen");
        $("button.btn-add").click();
        $$(".todo").shouldHave(com.codeborne.selenide.CollectionCondition.size(2));

        // Suchen
        $("input[name='q']").setValue("Apfel");
        $(".btn-search").click();

        // Nur Apfel sichtbar
        $$(".todo").shouldHave(com.codeborne.selenide.CollectionCondition.size(1));
        $(".todo-title").shouldHave(text("Apfel"));
    }

    @Test
    @DisplayName("Todo bearbeiten")
    void editTodo_updatesTitle() {
        open("/");

        // Todo erstellen
        $("input[placeholder='Titel']").setValue("Original");
        $("button.btn-add").click();
        $(".todo-title").shouldHave(text("Original"));

        // Bearbeiten
        $(".btn-edit").click();

        // Neuen Titel eingeben
        $("input[type='text']").clear();
        $("input[type='text']").setValue("Geaendert");
        $("button.btn-save").click();

        // Pruefen
        $(".todo-title").shouldHave(text("Geaendert"));
    }

    @Test
    @DisplayName("Export Link vorhanden")
    void exportLink_exists() {
        open("/");

        $(byText("CSV Export")).shouldBe(visible);
        $(byText("CSV Export")).shouldHave(attributeMatching("href", ".*\\/export$"));
    }
}
