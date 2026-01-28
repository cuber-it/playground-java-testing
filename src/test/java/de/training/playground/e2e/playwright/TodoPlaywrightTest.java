package de.training.playground.e2e.playwright;

import com.microsoft.playwright.*;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-End Tests mit Playwright.
 *
 * <p>Playwright ist eine moderne Alternative zu Selenium mit
 * besserer Performance und stabileren Tests.
 *
 * <p>Demonstriert:
 * <ul>
 *   <li>{@code Playwright} - Browser-Engine Management</li>
 *   <li>{@code Browser} - Browser-Instanz (Chromium, Firefox, WebKit)</li>
 *   <li>{@code BrowserContext} - isolierter Browser-Kontext</li>
 *   <li>{@code Page} - einzelne Browser-Seite</li>
 *   <li>{@code Locator} - Element-Selektion mit Auto-Wait</li>
 *   <li>{@code waitForSelector()}, {@code waitForFunction()} - explizite Waits</li>
 * </ul>
 *
 * <p><b>Vorteile gegenueber Selenium:</b>
 * <ul>
 *   <li>Schnellere Ausfuehrung</li>
 *   <li>Stabilere Tests (weniger Flakiness)</li>
 *   <li>Eingebautes Auto-Wait</li>
 *   <li>Moderne API</li>
 *   <li>Cross-Browser (Chromium, Firefox, WebKit)</li>
 * </ul>
 *
 * <p><b>Voraussetzung:</b> Playwright-Browser installieren
 * <pre>
 * npx playwright install chromium
 * </pre>
 *
 * @see Playwright
 * @see Browser
 * @see Page
 * @see Locator
 */
@Epic("E2E Tests")
@Feature("playwright")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TodoPlaywrightTest {

    @LocalServerPort
    private int port;

    private static Playwright playwright;
    private static Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        boolean headless = Boolean.parseBoolean(System.getProperty("playwright.headless", "true"));
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
            .setHeadless(headless));
    }

    @AfterAll
    static void closeBrowser() {
        browser.close();
        playwright.close();
    }

    @BeforeEach
    void createContextAndPage() {
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterEach
    void closeContext() {
        context.close();
    }

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    @DisplayName("Startseite zeigt Titel")
    void homePage_showsTitle() {
        page.navigate(baseUrl());

        String title = page.locator("h1").textContent();
        assertEquals("Meine Todos", title);
    }

    @Test
    @DisplayName("Neues Todo hinzufuegen")
    void addTodo_showsInList() {
        page.navigate(baseUrl());

        page.fill("input[placeholder='Titel']", "Playwright Test");
        page.fill("input[placeholder='Beschreibung']", "Via Playwright erstellt");
        page.click("button.btn-add");

        // Warten auf Todo in Liste
        page.waitForSelector("text=Playwright Test");

        String todoText = page.locator(".todo-title").first().textContent();
        assertEquals("Playwright Test", todoText);
    }

    @Test
    @DisplayName("Todo als erledigt markieren")
    void markDone_showsStrikethrough() {
        page.navigate(baseUrl());

        // Todo erstellen
        page.fill("input[placeholder='Titel']", "Zu erledigen");
        page.click("button.btn-add");
        page.waitForSelector(".btn-done");

        // Als erledigt markieren
        page.click(".btn-done");

        // Pruefen
        page.waitForSelector(".todo-title.done");
        assertTrue(page.locator(".todo-title.done").isVisible());
    }

    @Test
    @DisplayName("Todo loeschen")
    void deleteTodo_removesFromList() {
        page.navigate(baseUrl());

        // Todo erstellen
        page.fill("input[placeholder='Titel']", "Zu loeschen");
        page.click("button.btn-add");
        page.waitForSelector(".btn-delete");

        int countBefore = page.locator(".todo").count();

        // Loeschen
        page.click(".btn-delete");

        // Warten bis entfernt
        page.waitForFunction("document.querySelectorAll('.todo').length < " + countBefore);
    }

    @Test
    @DisplayName("Suche filtert Ergebnisse")
    void search_filtersResults() {
        page.navigate(baseUrl());

        // Todos erstellen
        page.fill("input[placeholder='Titel']", "Apfel kaufen");
        page.click("button.btn-add");
        page.waitForSelector("text=Apfel kaufen");

        page.fill("input[placeholder='Titel']", "Birne kaufen");
        page.click("button.btn-add");
        page.waitForSelector("text=Birne kaufen");

        // Suchen
        page.fill("input[placeholder='Suchen...']", "Apfel");
        page.click(".btn-search");

        // Nur Apfel sichtbar
        page.waitForFunction("document.querySelectorAll('.todo').length === 1");

        String result = page.locator(".todo-title").textContent();
        assertTrue(result.contains("Apfel"));
    }

    @Test
    @DisplayName("Todo bearbeiten")
    void editTodo_updatesTitle() {
        page.navigate(baseUrl());

        // Todo erstellen
        page.fill("input[placeholder='Titel']", "Original Titel");
        page.click("button.btn-add");
        page.waitForSelector("text=Original Titel");

        // Bearbeiten klicken
        page.click(".btn-edit");

        // Titel aendern
        page.fill("input[type='text']", "Neuer Titel");
        page.click("button.btn-save");

        // Pruefen
        page.waitForSelector("text=Neuer Titel");
        assertTrue(page.locator("text=Neuer Titel").isVisible());
    }

    @Test
    @DisplayName("Statistik zeigt korrekte Zahlen")
    void stats_showCorrectCounts() {
        page.navigate(baseUrl());

        // Todo erstellen
        page.fill("input[placeholder='Titel']", "Test Todo");
        page.click("button.btn-add");
        page.waitForSelector(".todo");

        String stats = page.locator(".stats").textContent();
        assertTrue(stats.contains("Gesamt: 1"));
        assertTrue(stats.contains("Offen: 1"));
    }

    @Test
    @DisplayName("Export Link vorhanden")
    void exportLink_exists() {
        page.navigate(baseUrl());

        Locator exportLink = page.locator("text=CSV Export");
        assertTrue(exportLink.isVisible());

        String href = exportLink.getAttribute("href");
        assertTrue(href.contains("/export"));
    }
}
