package de.training.playground.e2e.selenium;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-End Tests mit Selenium WebDriver.
 *
 * <p>Selenium ist der Klassiker fuer Browser-Automatisierung und
 * testet die Anwendung aus Benutzersicht im echten Browser.
 *
 * <p>Demonstriert:
 * <ul>
 *   <li>{@code WebDriver} - Browser-Steuerung</li>
 *   <li>{@code WebDriverWait} - explizite Waits fuer asynchrone Elemente</li>
 *   <li>{@code ExpectedConditions} - Bedingungen fuer Waits</li>
 *   <li>{@code By}-Selektoren (CSS, XPath, Tag, Class)</li>
 *   <li>Headless-Modus fuer CI/CD</li>
 * </ul>
 *
 * <p><b>Voraussetzung:</b> ChromeDriver muss installiert sein
 * <pre>
 * # Ubuntu: sudo apt install chromium-chromedriver
 * # Mac:    brew install chromedriver
 * </pre>
 *
 * <p><b>Vergleich mit Selenide/Playwright:</b>
 * Selenium ist flexibel, aber verbose. Selenide und Playwright bieten
 * kompaktere APIs mit automatischen Waits.
 *
 * @see WebDriver
 * @see WebDriverWait
 * @see ExpectedConditions
 */
@Epic("E2E Tests")
@Feature("selenium")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TodoSeleniumTest {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    @DisplayName("Startseite zeigt Titel 'Meine Todos'")
    void homePage_showsTitle() {
        driver.get(baseUrl());

        WebElement heading = driver.findElement(By.tagName("h1"));
        assertEquals("Meine Todos", heading.getText());
    }

    @Test
    @DisplayName("Neues Todo hinzufuegen")
    void addTodo_showsInList() {
        driver.get(baseUrl());

        // Formular ausfuellen
        driver.findElement(By.cssSelector("input[placeholder='Titel']"))
            .sendKeys("Selenium Test Todo");
        driver.findElement(By.cssSelector("input[placeholder='Beschreibung']"))
            .sendKeys("Erstellt via Selenium");
        driver.findElement(By.cssSelector("button.btn-add")).click();

        // Warten bis Todo erscheint
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
            By.className("todo-title"), "Selenium Test Todo"));

        WebElement todoTitle = driver.findElement(By.className("todo-title"));
        assertEquals("Selenium Test Todo", todoTitle.getText());
    }

    @Test
    @DisplayName("Todo als erledigt markieren")
    void markTodoDone_showsStrikethrough() {
        // Zuerst Todo erstellen
        driver.get(baseUrl());
        driver.findElement(By.cssSelector("input[placeholder='Titel']"))
            .sendKeys("Zu erledigen");
        driver.findElement(By.cssSelector("button.btn-add")).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("btn-done")));

        // Erledigt-Button klicken
        driver.findElement(By.className("btn-done")).click();

        // Pruefen dass durchgestrichen
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector(".todo-title.done")));

        WebElement doneTitle = driver.findElement(By.cssSelector(".todo-title.done"));
        assertNotNull(doneTitle);
    }

    @Test
    @DisplayName("Todo loeschen entfernt aus Liste")
    void deleteTodo_removesFromList() {
        driver.get(baseUrl());

        // Todo erstellen
        driver.findElement(By.cssSelector("input[placeholder='Titel']"))
            .sendKeys("Zu loeschen");
        driver.findElement(By.cssSelector("button.btn-add")).click();

        // Warten bis Todo und Delete-Button erscheinen
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("todo")));
        wait.until(ExpectedConditions.elementToBeClickable(By.className("btn-delete"))).click();

        // Nach Redirect: Pruefen dass "Keine Todos vorhanden" erscheint
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//*[contains(text(),'Keine Todos')]")));
    }

    @Test
    @DisplayName("Suche filtert Todos")
    void search_filtersResults() {
        driver.get(baseUrl());

        // Erstes Todo erstellen
        driver.findElement(By.cssSelector("input[placeholder='Titel']"))
            .sendKeys("Apfel kaufen");
        driver.findElement(By.cssSelector("button.btn-add")).click();

        // Warten auf Redirect und neues Todo
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("todo")));

        // Zweites Todo erstellen - warten bis Input wieder leer ist
        wait.until(ExpectedConditions.attributeToBe(
            By.cssSelector("input[placeholder='Titel']"), "value", ""));
        driver.findElement(By.cssSelector("input[placeholder='Titel']"))
            .sendKeys("Birne kaufen");
        driver.findElement(By.cssSelector("button.btn-add")).click();

        // Warten bis 2 Todos da sind
        wait.until(ExpectedConditions.numberOfElementsToBe(By.className("todo"), 2));

        // Suchen
        driver.findElement(By.cssSelector("input[name='q']")).sendKeys("Apfel");
        driver.findElement(By.className("btn-search")).click();

        // Warten bis nur 1 Todo sichtbar
        wait.until(ExpectedConditions.numberOfElementsToBe(By.className("todo"), 1));

        // Pruefen
        WebElement todo = driver.findElement(By.className("todo-title"));
        assertTrue(todo.getText().contains("Apfel"));
    }

    @Test
    @DisplayName("CSV Export Download")
    void exportCsv_triggersDownload() {
        driver.get(baseUrl());

        WebElement exportLink = driver.findElement(By.linkText("CSV Export"));
        String href = exportLink.getAttribute("href");

        assertTrue(href.contains("/export"));
    }
}
