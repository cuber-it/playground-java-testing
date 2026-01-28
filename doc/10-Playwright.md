# Playwright - Moderne Browser-Automatisierung

## Was ist Playwright?

Ein **modernes Testing-Framework** von Microsoft. Schneller, stabiler und einfacher als Selenium.

## Playwright vs Selenium

| Aspekt | Selenium | Playwright |
|--------|----------|------------|
| Geschwindigkeit | Langsam | Schnell |
| Auto-Wait | Nein | Ja |
| Multi-Browser | Ja | Ja + WebKit |
| API | Verbose | Modern |
| Debugging | Schwer | Inspector |

## Setup im Playground

```java
// src/test/java/.../e2e/playwright/TodoPlaywrightTest.java

@SpringBootTest(webEnvironment = RANDOM_PORT)
class TodoPlaywrightTest {

    @LocalServerPort
    private int port;

    private Playwright playwright;
    private Browser browser;
    private Page page;

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
            new BrowserType.LaunchOptions().setHeadless(true)
        );
        page = browser.newPage();
    }

    @AfterEach
    void tearDown() {
        browser.close();
        playwright.close();
    }
}
```

**Was wurde gemacht:** Playwright mit Chromium im Headless-Modus.

**Warum:** Moderne Alternative zu Selenium mit besserem API.

## Browser installieren

```bash
# Chromium installieren
npx playwright install chromium

# Alle Browser
npx playwright install
```

## Elemente selektieren

```java
// CSS-Selector
page.locator("#title")
page.locator(".btn-primary")
page.locator("button[type='submit']")

// Text
page.getByText("Speichern")
page.getByText("Teil", new Page.GetByTextOptions().setExact(false))

// Role (barrierefrei)
page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save"))
page.getByRole(AriaRole.TEXTBOX)

// Label
page.getByLabel("Titel")

// Placeholder
page.getByPlaceholder("Titel eingeben")

// Test-ID (empfohlen)
page.getByTestId("submit-button")
```

## Interaktionen

```java
// Text eingeben
page.locator("#title").fill("Neues Todo");

// Klicken
page.locator("button").click();

// Mehrere Aktionen
page.locator("#title").fill("Test");
page.locator("button").click();

// Checkbox
page.locator("#done").check();
page.locator("#done").uncheck();

// Select/Dropdown
page.locator("select").selectOption("option1");

// Datei hochladen
page.locator("input[type='file']").setInputFiles(Paths.get("test.pdf"));
```

## Automatische Waits

Playwright wartet **automatisch** auf:
- Element sichtbar
- Element enabled
- Element stabil (keine Animation)
- Netzwerk-Requests

```java
// Kein expliziter Wait noetig!
page.locator("#result").click();  // Wartet automatisch

// Falls doch noetig
page.waitForSelector("#dynamic-element");
page.waitForLoadState(LoadState.NETWORKIDLE);
```

## Assertions

```java
import static com.microsoft.playwright.assertions.PlaywrightAssertions.*;

// Element sichtbar
assertThat(page.locator("#title")).isVisible();

// Text
assertThat(page.locator("#message")).hasText("Erfolg");
assertThat(page.locator("#message")).containsText("Erfolg");

// Attribut
assertThat(page.locator("#input")).hasValue("Test");
assertThat(page.locator("#link")).hasAttribute("href", "/todos");

// CSS-Klasse
assertThat(page.locator("#element")).hasClass("active");

// Count
assertThat(page.locator(".todo-item")).hasCount(3);
```

## Playground: Todo erstellen

```java
@Test
@DisplayName("Todo erstellen via Web-UI")
void createTodo() {
    page.navigate("http://localhost:" + port);

    page.locator("#title").fill("Playwright Test");
    page.locator("#description").fill("Via Playwright erstellt");
    page.locator("button[type='submit']").click();

    assertThat(page.locator(".todo-item")).hasCount(1);
    assertThat(page.locator(".todo-item")).containsText("Playwright Test");
}
```

**Was wurde gemacht:** Todo per UI erstellen mit Playwright.

**Warum:** Zeigt modernes API mit automatischen Waits.

## Playground: Mehrere Todos

```java
@Test
@DisplayName("Mehrere Todos verwalten")
void multipleTodos() {
    page.navigate(baseUrl);

    // Erstes Todo
    page.locator("#title").fill("Erstes Todo");
    page.locator("button[type='submit']").click();

    // Zweites Todo
    page.locator("#title").fill("Zweites Todo");
    page.locator("button[type='submit']").click();

    // Pruefen
    assertThat(page.locator(".todo-item")).hasCount(2);

    // Erstes loeschen
    page.locator(".todo-item").first().locator(".delete-btn").click();

    assertThat(page.locator(".todo-item")).hasCount(1);
}
```

## Screenshots und Videos

```java
// Screenshot
page.screenshot(new Page.ScreenshotOptions()
    .setPath(Paths.get("screenshot.png")));

// Ganzer Seite
page.screenshot(new Page.ScreenshotOptions()
    .setPath(Paths.get("full-page.png"))
    .setFullPage(true));

// Video (beim Browser-Start aktivieren)
Browser browser = playwright.chromium().launch();
BrowserContext context = browser.newContext(new Browser.NewContextOptions()
    .setRecordVideoDir(Paths.get("videos/")));
```

## Network Interception

```java
// Requests abfangen
page.route("**/api/**", route -> {
    route.fulfill(new Route.FulfillOptions()
        .setStatus(200)
        .setBody("{\"mocked\": true}"));
});

// Oder weiterleiten mit Modifikation
page.route("**/api/todos", route -> {
    route.continue_();  // Original-Request ausfuehren
});
```

**Was wurde gemacht:** Network-Mocking direkt in Playwright.

**Warum:** Kein WireMock noetig fuer einfache Faelle.

## Debugging

```java
// Inspector oeffnen
page.pause();

// Trace aufzeichnen
BrowserContext context = browser.newContext();
context.tracing().start(new Tracing.StartOptions()
    .setScreenshots(true)
    .setSnapshots(true));
// ... Test ...
context.tracing().stop(new Tracing.StopOptions()
    .setPath(Paths.get("trace.zip")));
```

## Best Practices

1. **getByTestId** fuer stabile Selektoren
2. **Automatische Waits** nutzen (kein sleep)
3. **Headless fuer CI** - schneller und ressourcenschonend
4. **Tracing** fuer Debugging aktivieren
5. **Chromium** fuer schnellste Tests
