# Selenide - Elegantes Selenium

## Was ist Selenide?

Ein **Wrapper um Selenium** mit eleganterem API. Automatische Waits, weniger Boilerplate, bessere Lesbarkeit.

## Selenium vs Selenide

```java
// Selenium - viel Code
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
WebElement element = wait.until(
    ExpectedConditions.visibilityOfElementLocated(By.id("title"))
);
element.clear();
element.sendKeys("Test");
driver.findElement(By.cssSelector("button")).click();

// Selenide - kurz und lesbar
$("#title").setValue("Test");
$("button").click();
```

## Setup im Playground

```java
// src/test/java/.../e2e/selenide/TodoSelenideTest.java

@SpringBootTest(webEnvironment = RANDOM_PORT)
class TodoSelenideTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        Configuration.browser = "chrome";
        Configuration.headless = true;
        Configuration.baseUrl = "http://localhost:" + port;
        Configuration.timeout = 5000;  // Auto-Wait Timeout
    }
}
```

**Was wurde gemacht:** Selenide-Konfiguration, kein WebDriver-Management noetig.

**Warum:** Selenide kuemmert sich um alles (Browser, Waits, Cleanup).

## Elemente selektieren

```java
// CSS-Selector (am haeufigsten)
$("#title")                      // By ID
$(".btn-primary")                // By Class
$("button[type='submit']")       // By Attribute
$("input[name='description']")   // By Name

// XPath
$x("//a[@href='/todos']")

// Text
$(byText("Speichern"))
$(withText("Teil"))              // Enthaelt Text

// Mehrere Elemente
$$(".todo-item")                 // Collection
$$(".todo-item").first()
$$(".todo-item").last()
$$(".todo-item").get(2)          // Index
```

## Interaktionen

```java
// Text eingeben
$("#title").setValue("Neues Todo");

// Klicken
$("button").click();
$("button").doubleClick();
$("button").contextClick();      // Rechtsklick

// Hover
$(".menu").hover();

// Drag & Drop
$("#source").dragAndDropTo("#target");

// Datei hochladen
$("#file").uploadFile(new File("test.pdf"));

// Scrollen
$("#element").scrollIntoView(true);
```

## Automatische Waits

**Selenide wartet automatisch** bis:
- Element sichtbar ist
- Element klickbar ist
- Text erscheint

```java
// Selenium: Expliziter Wait noetig
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("result")));

// Selenide: Automatisch!
$("#result").shouldBe(visible);
```

## Assertions (Conditions)

```java
// Sichtbarkeit
$("#element").shouldBe(visible);
$("#element").shouldNotBe(visible);

// Text
$("#title").shouldHave(text("Todo"));
$("#title").shouldHave(exactText("Mein Todo"));

// Attribute
$("#input").shouldHave(value("Test"));
$("#link").shouldHave(attribute("href", "/todos"));

// CSS
$("#element").shouldHave(cssClass("active"));

// Existenz
$("#element").should(exist);
$("#element").shouldNot(exist);
```

## Collection Assertions

```java
// Groesse
$$(".todo-item").shouldHave(size(3));
$$(".todo-item").shouldHave(sizeGreaterThan(0));

// Texte
$$(".todo-item").shouldHave(texts("A", "B", "C"));
$$(".todo-item").shouldHave(exactTexts("A", "B", "C"));

// Mindestens einer
$$(".todo-item").findBy(text("Wichtig")).shouldBe(visible);
```

## Playground: Todo erstellen

```java
@Test
@DisplayName("Todo ueber Web-UI erstellen")
void createTodoViaUI() {
    open("/");

    $("#title").setValue("Selenide Test");
    $("#description").setValue("Via Selenide erstellt");
    $("button[type='submit']").click();

    $$(".todo-item").shouldHave(sizeGreaterThan(0));
    $(".todo-item").shouldHave(text("Selenide Test"));
}
```

**Was wurde gemacht:** Gleicher Test wie Selenium, aber viel kuerzer.

**Warum:** Zeigt den Unterschied in Lesbarkeit und Codelaenge.

## Playground: Todo loeschen

```java
@Test
@DisplayName("Todo loeschen")
void deleteTodo() {
    // Testdaten
    repository.save(createTodo("Zum Loeschen"));

    open("/");

    // Vor dem Loeschen
    $$(".todo-item").shouldHave(size(1));

    // Loeschen
    $(".todo-item .delete-btn").click();

    // Nach dem Loeschen
    $$(".todo-item").shouldHave(size(0));
}
```

## Screenshots und Videos

```java
// Automatisch bei Fehlern (Konfiguration)
Configuration.screenshots = true;
Configuration.savePageSource = true;

// Manuell
screenshot("custom-name");
```

## Allure Integration

```java
// pom.xml: allure-selenide Dependency
@Test
void testWithAllure() {
    Selenide.open("/");
    // Screenshots automatisch in Allure Report
}
```

**Was wurde gemacht:** Allure-Selenide Integration in pom.xml.

**Warum:** Screenshots und Steps automatisch im Report.

## Vorteile gegenueber Selenium

| Aspekt | Selenium | Selenide |
|--------|----------|----------|
| Waits | Manuell | Automatisch |
| Syntax | Verbose | Fluent |
| Browser-Management | Manuell | Automatisch |
| Screenshots | Manuell | Automatisch |
| Codezeilen | Viele | Wenige |

## Best Practices

1. **CSS-Selektoren** bevorzugen (schneller als XPath)
2. **Fluent Assertions** - `shouldBe`, `shouldHave`
3. **Collections** fuer mehrere Elemente
4. **Keine Thread.sleep()** - Selenide wartet automatisch
5. **Page Objects** fuer komplexe Seiten
