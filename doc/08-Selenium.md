# Selenium - Browser-Automatisierung

## Was ist Selenium?

Das **Standard-Framework** fuer Browser-Automatisierung. Steuert echte Browser (Chrome, Firefox, etc.) per WebDriver.

## Architektur

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│  Test-Code   │────>│  WebDriver   │────>│   Browser    │
│  (Java)      │     │  (Chrome)    │     │   (Chrome)   │
└──────────────┘     └──────────────┘     └──────────────┘
```

## Setup im Playground

```java
// src/test/java/.../e2e/selenium/TodoSeleniumTest.java

@SpringBootTest(webEnvironment = RANDOM_PORT)
class TodoSeleniumTest {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        // WebDriverManager konfiguriert ChromeDriver automatisch
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");  // Ohne GUI
        options.addArguments("--no-sandbox");

        driver = new ChromeDriver(options);
        baseUrl = "http://localhost:" + port;
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
```

**Was wurde gemacht:** Chrome im Headless-Modus, Spring-App auf Random Port.

**Warum:** Vollstaendiger E2E-Test mit echtem Browser.

## Elemente finden

```java
// By ID
WebElement element = driver.findElement(By.id("title"));

// By CSS-Selector
WebElement button = driver.findElement(By.cssSelector(".btn-primary"));

// By XPath
WebElement link = driver.findElement(By.xpath("//a[@href='/todos']"));

// By Name
WebElement input = driver.findElement(By.name("description"));

// Mehrere Elemente
List<WebElement> todos = driver.findElements(By.cssSelector(".todo-item"));
```

## Interaktionen

```java
// Text eingeben
WebElement titleInput = driver.findElement(By.id("title"));
titleInput.clear();
titleInput.sendKeys("Neues Todo");

// Button klicken
driver.findElement(By.cssSelector("button[type='submit']")).click();

// Link klicken
driver.findElement(By.linkText("Bearbeiten")).click();

// Checkbox
WebElement checkbox = driver.findElement(By.id("done"));
if (!checkbox.isSelected()) {
    checkbox.click();
}
```

## Waits - Auf Elemente warten

```java
// Expliziter Wait (empfohlen)
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
WebElement element = wait.until(
    ExpectedConditions.visibilityOfElementLocated(By.id("result"))
);

// Auf Klickbarkeit warten
wait.until(ExpectedConditions.elementToBeClickable(By.id("button")));

// Auf Text warten
wait.until(ExpectedConditions.textToBePresentInElementLocated(
    By.id("message"), "Erfolgreich"
));
```

**Was wurde gemacht:** Explizite Waits statt Thread.sleep().

**Warum:** Robust gegen Timing-Probleme, nicht zu lange warten.

## Playground: Todo erstellen

```java
@Test
@DisplayName("Todo ueber Web-UI erstellen")
void createTodoViaUI() {
    driver.get(baseUrl);

    // Formular ausfuellen
    driver.findElement(By.id("title")).sendKeys("Selenium Test");
    driver.findElement(By.id("description")).sendKeys("Via Selenium erstellt");

    // Absenden
    driver.findElement(By.cssSelector("button[type='submit']")).click();

    // Warten und pruefen
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.xpath("//td[contains(text(), 'Selenium Test')]")
    ));

    String pageSource = driver.getPageSource();
    assertThat(pageSource).contains("Selenium Test");
}
```

## Playground: Todo als erledigt markieren

```java
@Test
@DisplayName("Todo als erledigt markieren")
void markTodoAsDone() {
    // Testdaten vorbereiten
    Todo todo = repository.save(createTodo("Zum Abhaken"));

    driver.get(baseUrl);

    // Checkbox finden und klicken
    WebElement checkbox = driver.findElement(
        By.cssSelector("input[data-todo-id='" + todo.getId() + "']")
    );
    checkbox.click();

    // Seite neu laden und pruefen
    driver.navigate().refresh();
    WebElement row = driver.findElement(
        By.xpath("//tr[contains(., 'Zum Abhaken')]")
    );
    assertThat(row.getAttribute("class")).contains("done");
}
```

## Assertions

```java
// Titel pruefen
assertThat(driver.getTitle()).isEqualTo("Todo App");

// Element sichtbar?
assertThat(element.isDisplayed()).isTrue();

// Text pruefen
assertThat(element.getText()).contains("Erfolgreich");

// Attribut pruefen
assertThat(element.getAttribute("class")).contains("active");

// URL pruefen
assertThat(driver.getCurrentUrl()).endsWith("/todos");
```

## Screenshots bei Fehlern

```java
@AfterEach
void takeScreenshotOnFailure(TestInfo testInfo) {
    if (driver != null) {
        File screenshot = ((TakesScreenshot) driver)
            .getScreenshotAs(OutputType.FILE);
        // Screenshot speichern...
    }
}
```

## Best Practices

1. **Headless fuer CI** - `--headless` Flag
2. **Explizite Waits** - nicht `Thread.sleep()`
3. **Page Object Pattern** - UI-Logik kapseln
4. **Testdaten isolieren** - jeder Test raeumt auf
5. **Screenshots bei Fehlern** - Debugging erleichtern

## Nachteile von Selenium

- Viel Boilerplate-Code
- Explizite Waits ueberall noetig
- Langsam im Vergleich zu Alternativen
- WebDriver-Versionsprobleme

**Alternativen:** Selenide (siehe naechstes Dokument), Playwright
