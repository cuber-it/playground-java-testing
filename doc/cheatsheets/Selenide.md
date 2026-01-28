# Selenide Cheatsheet

## Dependency

**Maven:**
```xml
<dependency>
    <groupId>com.codeborne</groupId>
    <artifactId>selenide</artifactId>
    <version>7.5.1</version>
    <scope>test</scope>
</dependency>
```

**Gradle:**
```groovy
testImplementation 'com.codeborne:selenide:7.5.1'
```

## Imports

```java
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.CollectionCondition.*;
```

## Konfiguration

```java
Configuration.browser = "chrome";
Configuration.headless = true;
Configuration.baseUrl = "http://localhost:8080";
Configuration.timeout = 5000;  // Auto-Wait Timeout
```

## Navigation

```java
open("/");
open("http://example.com");
open("/page", "", "", "user", "pass");  // Basic Auth
back();
forward();
refresh();
```

## Selektoren

```java
// CSS (am häufigsten)
$("#id")
$(".class")
$("button")
$("input[name='email']")
$("div.container > p")

// XPath
$x("//button[@type='submit']")

// Text
$(byText("Exact Text"))
$(withText("Partial"))

// Attribute
$(byAttribute("data-id", "123"))
$(byValue("value"))
$(byName("fieldname"))

// Kombiniert
$(".form").$("#submit")
$(".container").$(".button")
```

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
$("#input").sendKeys("text");

// Checkbox / Radio
$("#checkbox").setSelected(true);
$("#checkbox").shouldBe(checked);

// Select
$("#select").selectOption("value");
$("#select").selectOptionByValue("val");
$("#select").selectOptionContainingText("part");

// Datei Upload
$("#file").uploadFile(new File("test.pdf"));

// Hover
$("#menu").hover();

// Scrollen
$("#element").scrollIntoView(true);
```

## Conditions (Assertions)

```java
// Sichtbarkeit
$("#el").shouldBe(visible);
$("#el").shouldNotBe(visible);
$("#el").shouldBe(hidden);

// Text
$("#el").shouldHave(text("Hello"));
$("#el").shouldHave(exactText("Hello World"));
$("#el").shouldHave(textCaseSensitive("Hello"));

// Attribute
$("#el").shouldHave(attribute("href", "/link"));
$("#el").shouldHave(value("input text"));
$("#el").shouldHave(cssClass("active"));
$("#el").shouldHave(id("myId"));

// Zustand
$("#el").shouldBe(enabled);
$("#el").shouldBe(disabled);
$("#el").shouldBe(readonly);
$("#el").shouldBe(checked);
$("#el").shouldBe(selected);
$("#el").should(exist);
$("#el").shouldNot(exist);

// Leer
$("#el").shouldBe(empty);
$("#el").shouldNotBe(empty);
```

## Collection Conditions

```java
$$(".item").shouldHave(size(3));
$$(".item").shouldHave(sizeGreaterThan(0));
$$(".item").shouldHave(sizeLessThan(10));

$$(".item").shouldHave(texts("A", "B", "C"));
$$(".item").shouldHave(exactTexts("A", "B", "C"));

$$(".item").shouldHave(itemWithText("A"));
$$(".item").shouldBe(empty);
```

## Warten

```java
// Automatisch (Selenide wartet immer)
$("#el").click();  // Wartet bis klickbar

// Explizit
$("#el").shouldBe(visible, Duration.ofSeconds(10));

// Auf Bedingung
Wait().until(() -> $("#el").isDisplayed());
```

## Screenshots

```java
// Automatisch bei Fehler (Konfiguration)
Configuration.screenshots = true;

// Manuell
screenshot("custom-name");
Screenshot s = Screenshots.takeScreenShotAsFile();
```

## Browser Actions

```java
// Fenster
switchTo().window("windowName");
switchTo().window(1);

// Frames
switchTo().frame("frameName");
switchTo().frame($("#iframe"));
switchTo().defaultContent();

// Alerts
confirm();
dismiss();
prompt("input");

// Cookies
getWebDriver().manage().getCookies();
```

## Mit Selenium kombinieren

```java
WebDriver driver = WebDriverRunner.getWebDriver();
WebElement element = $("#el").toWebElement();
```
