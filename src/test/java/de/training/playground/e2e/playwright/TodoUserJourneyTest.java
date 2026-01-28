package de.training.playground.e2e.playwright;

import com.microsoft.playwright.*;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Playwright User Journey Test.
 *
 * <p>Simuliert einen kompletten Benutzer-Workflow durch die Web-App:
 * <ol>
 *   <li>App oeffnen und Uebersicht sehen</li>
 *   <li>Erstes Todo erstellen</li>
 *   <li>Zweites Todo erstellen</li>
 *   <li>Todo bearbeiten</li>
 *   <li>Todo als erledigt markieren</li>
 *   <li>Suche verwenden</li>
 *   <li>Todo loeschen</li>
 *   <li>CSV Export pruefen</li>
 * </ol>
 */
@Epic("E2E Tests")
@Feature("playwright")
@Story("Kompletter Todo-Workflow im Browser")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TodoUserJourneyTest {

    @LocalServerPort
    private int port;

    private static Playwright playwright;
    private static Browser browser;
    private static BrowserContext context;
    private static Page page;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        boolean headless = Boolean.parseBoolean(System.getProperty("playwright.headless", "true"));
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
            .setHeadless(headless)
            .setSlowMo(headless ? 0 : 500)); // Bei Browser langsamer fuer Sichtbarkeit
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterAll
    static void closeBrowser() {
        context.close();
        browser.close();
        playwright.close();
    }

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    @Order(1)
    @Severity(SeverityLevel.BLOCKER)
    @Description("App oeffnen und Startseite pruefen")
    void step01_openAppAndVerifyHomepage() {
        page.navigate(baseUrl());

        Allure.step("Navigiere zu " + baseUrl());

        String title = page.locator("h1").textContent();
        assertEquals("Meine Todos", title);

        Allure.step("Titel geprueft: " + title);
    }

    @Test
    @Order(2)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Erstes Todo 'Einkaufen gehen' erstellen")
    void step02_createFirstTodo() {
        page.fill("input[placeholder='Titel']", "Einkaufen gehen");
        page.fill("input[placeholder='Beschreibung']", "Milch, Brot, Eier");
        page.click("button.btn-add");

        page.waitForSelector("text=Einkaufen gehen");

        Allure.step("Todo 'Einkaufen gehen' erstellt");
        assertTrue(page.locator("text=Einkaufen gehen").isVisible());
    }

    @Test
    @Order(3)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Zweites Todo 'Sport machen' erstellen")
    void step03_createSecondTodo() {
        page.fill("input[placeholder='Titel']", "Sport machen");
        page.fill("input[placeholder='Beschreibung']", "30 Min Joggen");
        page.click("button.btn-add");

        page.waitForSelector("text=Sport machen");

        Allure.step("Todo 'Sport machen' erstellt");

        int todoCount = page.locator(".todo").count();
        assertTrue(todoCount >= 2, "Mindestens 2 Todos erwartet");
    }

    @Test
    @Order(4)
    @Severity(SeverityLevel.NORMAL)
    @Description("Erstes Todo bearbeiten - Titel aendern")
    void step04_editFirstTodo() {
        // Erstes Todo finden und bearbeiten
        page.locator(".todo").filter(new Locator.FilterOptions().setHasText("Einkaufen"))
            .locator(".btn-edit").click();

        page.fill("input[type='text']", "Einkaufen gehen (dringend!)");
        page.click("button.btn-save");

        page.waitForSelector("text=dringend");

        Allure.step("Todo geaendert zu 'Einkaufen gehen (dringend!)'");
        assertTrue(page.locator("text=dringend").isVisible());
    }

    @Test
    @Order(5)
    @Severity(SeverityLevel.NORMAL)
    @Description("'Sport machen' als erledigt markieren")
    void step05_markTodoAsDone() {
        page.locator(".todo").filter(new Locator.FilterOptions().setHasText("Sport"))
            .locator(".btn-done").click();

        page.waitForSelector(".todo-title.done");

        Allure.step("Todo 'Sport machen' als erledigt markiert");
        assertTrue(page.locator(".todo-title.done").isVisible());
    }

    @Test
    @Order(6)
    @Severity(SeverityLevel.NORMAL)
    @Description("Nach 'Einkaufen' suchen")
    void step06_searchForTodo() {
        page.fill("input[placeholder='Suchen...']", "Einkaufen");
        page.click(".btn-search");

        page.waitForFunction("document.querySelectorAll('.todo').length === 1");

        Allure.step("Suche nach 'Einkaufen' - 1 Ergebnis");

        String result = page.locator(".todo-title").first().textContent();
        assertTrue(result.contains("Einkaufen"));

        // Suche zuruecksetzen
        page.fill("input[placeholder='Suchen...']", "");
        page.click(".btn-search");
        page.waitForFunction("document.querySelectorAll('.todo').length >= 2");
    }

    @Test
    @Order(7)
    @Severity(SeverityLevel.NORMAL)
    @Description("Erledigtes Todo loeschen")
    void step07_deleteDoneTodo() {
        int countBefore = page.locator(".todo").count();

        page.locator(".todo").filter(new Locator.FilterOptions().setHasText("Sport"))
            .locator(".btn-delete").click();

        page.waitForFunction("document.querySelectorAll('.todo').length < " + countBefore);

        Allure.step("Todo 'Sport machen' geloescht");

        assertFalse(page.locator("text=Sport machen").isVisible());
    }

    @Test
    @Order(8)
    @Severity(SeverityLevel.MINOR)
    @Description("CSV Export Link pruefen")
    void step08_verifyExportLink() {
        Locator exportLink = page.locator("text=CSV Export");
        assertTrue(exportLink.isVisible());

        String href = exportLink.getAttribute("href");
        assertTrue(href.contains("/export"));

        Allure.step("CSV Export Link vorhanden: " + href);
    }

    @Test
    @Order(9)
    @Severity(SeverityLevel.MINOR)
    @Description("Statistik pruefen - 1 offenes Todo")
    void step09_verifyStats() {
        String stats = page.locator(".stats").textContent();

        Allure.step("Statistik: " + stats);

        assertTrue(stats.contains("Offen: 1") || stats.contains("Offen:1"),
            "Erwartet: 1 offenes Todo");
    }

    @Test
    @Order(10)
    @Severity(SeverityLevel.NORMAL)
    @Description("Aufraumen - letztes Todo loeschen")
    void step10_cleanup() {
        while (page.locator(".btn-delete").count() > 0) {
            page.locator(".btn-delete").first().click();
            page.waitForTimeout(300);
        }

        Allure.step("Alle Todos geloescht - Aufgeraeumt");
    }
}
