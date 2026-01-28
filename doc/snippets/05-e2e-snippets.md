# E2E Test Snippets (Selenide & Playwright)

Copy-Paste fertige Code-Blöcke.

---

# Selenide

## Test-Klasse Grundgerüst

```java
import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.CollectionCondition.*;

class TodoPageTest {

    @BeforeAll
    static void setUp() {
        Configuration.browser = "chrome";
        Configuration.headless = true;
        Configuration.baseUrl = "http://localhost:8080";
        Configuration.timeout = 5000;
    }

    @BeforeEach
    void openPage() {
        open("/todos");
    }

    @Test
    void shouldDisplayTodos() {
        $$("#todo-list li").shouldHave(sizeGreaterThan(0));
    }
}
```

---

## Selektoren

```java
// CSS
$("#id")
$(".class")
$("button")
$("input[name='email']")
$("div.container > p")
$(".form #submit")

// Text
$(byText("Exact Text"))
$(withText("Partial"))

// XPath
$x("//button[@type='submit']")

// Attribute
$(byAttribute("data-testid", "submit-btn"))
$(byValue("value"))
$(byName("fieldname"))
```

---

## Mehrere Elemente

```java
$$(".item")                     // Collection
$$(".item").first()
$$(".item").last()
$$(".item").get(2)              // Index
$$(".item").findBy(text("A"))
$$(".item").filterBy(visible)
$$(".item").excludeWith(hidden)
```

---

## Interaktionen

```java
// Klick
$("#btn").click();
$("#btn").doubleClick();
$("#btn").contextClick();

// Text eingeben
$("#input").setValue("text");
$("#input").append(" more");
$("#input").clear();

// Checkbox / Radio
$("#checkbox").setSelected(true);

// Select
$("#select").selectOption("value");
$("#select").selectOptionByValue("val");

// Datei Upload
$("#file").uploadFile(new File("test.pdf"));

// Hover
$("#menu").hover();
```

---

## Assertions (Conditions)

```java
// Sichtbarkeit
$("#el").shouldBe(visible);
$("#el").shouldNotBe(visible);
$("#el").shouldBe(hidden);

// Text
$("#el").shouldHave(text("Hello"));
$("#el").shouldHave(exactText("Hello World"));

// Attribute
$("#el").shouldHave(attribute("href", "/link"));
$("#el").shouldHave(value("input text"));
$("#el").shouldHave(cssClass("active"));

// Zustand
$("#el").shouldBe(enabled);
$("#el").shouldBe(disabled);
$("#el").shouldBe(checked);
$("#el").should(exist);
```

---

## Collection Assertions

```java
$$(".item").shouldHave(size(3));
$$(".item").shouldHave(sizeGreaterThan(0));
$$(".item").shouldHave(texts("A", "B", "C"));
$$(".item").shouldHave(itemWithText("A"));
$$(".item").shouldBe(empty);
```

---

## Warten

```java
// Automatisch (Selenide wartet immer)
$("#el").click();  // Wartet bis klickbar

// Explizit mit Timeout
$("#el").shouldBe(visible, Duration.ofSeconds(10));
```

---

## Beispiel: Todo-App Test

```java
@Test
void shouldAddNewTodo() {
    // Arrange
    open("/todos");

    // Act
    $("#new-todo-input").setValue("Buy milk");
    $("#add-button").click();

    // Assert
    $$("#todo-list li").shouldHave(itemWithText("Buy milk"));
}

@Test
void shouldMarkTodoAsDone() {
    open("/todos");

    $$(".todo-item").findBy(text("Buy milk"))
        .$(".checkbox").click();

    $$(".todo-item").findBy(text("Buy milk"))
        .shouldHave(cssClass("completed"));
}

@Test
void shouldDeleteTodo() {
    open("/todos");
    int initialSize = $$(".todo-item").size();

    $$(".todo-item").first().$(".delete-btn").click();

    $$(".todo-item").shouldHave(size(initialSize - 1));
}
```

---

# Playwright (Java)

## Test-Klasse Grundgerüst

```java
import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.*;

class TodoPageTest {

    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
            new BrowserType.LaunchOptions().setHeadless(true));
    }

    @AfterAll
    static void closeBrowser() {
        browser.close();
        playwright.close();
    }

    @BeforeEach
    void createContext() {
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterEach
    void closeContext() {
        context.close();
    }

    @Test
    void shouldDisplayTodos() {
        page.navigate("http://localhost:8080/todos");

        assertThat(page.locator(".todo-item")).hasCount(3);
    }
}
```

---

## Selektoren (Locators)

```java
// CSS
page.locator("#id")
page.locator(".class")
page.locator("button")
page.locator("input[name='email']")

// Text
page.getByText("Click me")
page.getByText("Click", new Page.GetByTextOptions().setExact(false))

// Role (Accessibility)
page.getByRole(AriaRole.BUTTON)
page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit"))

// Label
page.getByLabel("Email")

// Placeholder
page.getByPlaceholder("Enter email")

// Test-ID (empfohlen)
page.getByTestId("submit-btn")
```

---

## Interaktionen

```java
// Klicken
page.locator("#btn").click();
page.locator("#btn").dblclick();

// Text eingeben
page.locator("#input").fill("text");
page.locator("#input").clear();

// Checkbox
page.locator("#checkbox").check();
page.locator("#checkbox").uncheck();

// Select
page.locator("select").selectOption("value");

// Datei Upload
page.locator("input[type='file']").setInputFiles(Paths.get("file.pdf"));

// Hover
page.locator("#menu").hover();
```

---

## Assertions

```java
// Sichtbarkeit
assertThat(page.locator("#el")).isVisible();
assertThat(page.locator("#el")).isHidden();
assertThat(page.locator("#el")).isEnabled();
assertThat(page.locator("#el")).isDisabled();

// Text
assertThat(page.locator("#el")).hasText("exact");
assertThat(page.locator("#el")).containsText("partial");

// Attribute
assertThat(page.locator("#el")).hasAttribute("href", "/link");
assertThat(page.locator("#el")).hasClass("active");
assertThat(page.locator("input")).hasValue("text");

// Anzahl
assertThat(page.locator(".item")).hasCount(5);

// Page
assertThat(page).hasTitle("Title");
assertThat(page).hasURL("http://localhost/page");
```

---

## Warten

```java
// Automatisch (Playwright wartet)
page.locator("#el").click();

// Explizit
page.waitForSelector("#dynamic");
page.waitForLoadState(LoadState.NETWORKIDLE);
page.waitForURL("**/success");

// Auf Element warten
page.locator("#el").waitFor();
page.locator("#el").waitFor(new Locator.WaitForOptions()
    .setState(WaitForSelectorState.HIDDEN));
```

---

## Mehrere Elemente

```java
Locator items = page.locator(".item");
int count = items.count();
items.first().click();
items.last().click();
items.nth(2).click();

for (Locator item : items.all()) {
    item.click();
}
```

---

## Screenshot

```java
page.screenshot(new Page.ScreenshotOptions()
    .setPath(Paths.get("screenshot.png")));

// Full Page
page.screenshot(new Page.ScreenshotOptions()
    .setPath(Paths.get("full.png"))
    .setFullPage(true));

// Element
page.locator("#element").screenshot(new Locator.ScreenshotOptions()
    .setPath(Paths.get("element.png")));
```

---

## Beispiel: Todo-App Test

```java
@Test
void shouldAddNewTodo() {
    page.navigate("http://localhost:8080/todos");

    page.getByPlaceholder("New todo").fill("Buy milk");
    page.getByRole(AriaRole.BUTTON,
        new Page.GetByRoleOptions().setName("Add")).click();

    assertThat(page.locator(".todo-item")).containsText("Buy milk");
}

@Test
void shouldMarkTodoAsDone() {
    page.navigate("http://localhost:8080/todos");

    page.locator(".todo-item").filter(
        new Locator.FilterOptions().setHasText("Buy milk"))
        .getByRole(AriaRole.CHECKBOX).check();

    assertThat(page.locator(".todo-item").filter(
        new Locator.FilterOptions().setHasText("Buy milk")))
        .hasClass("completed");
}

@Test
void shouldDeleteTodo() {
    page.navigate("http://localhost:8080/todos");
    int initialCount = page.locator(".todo-item").count();

    page.locator(".todo-item").first()
        .getByRole(AriaRole.BUTTON,
            new Page.GetByRoleOptions().setName("Delete")).click();

    assertThat(page.locator(".todo-item")).hasCount(initialCount - 1);
}
```
