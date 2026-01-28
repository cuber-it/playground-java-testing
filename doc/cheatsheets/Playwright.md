# Playwright Cheatsheet

## Dependency

**Maven:**
```xml
<dependency>
    <groupId>com.microsoft.playwright</groupId>
    <artifactId>playwright</artifactId>
    <version>1.47.0</version>
    <scope>test</scope>
</dependency>
```

**Gradle:**
```groovy
testImplementation 'com.microsoft.playwright:playwright:1.47.0'
```

**Browser installieren (nach Dependency-Download):**
```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
# oder
npx playwright install chromium
```

## Setup

```java
Playwright playwright = Playwright.create();
Browser browser = playwright.chromium().launch(
    new BrowserType.LaunchOptions().setHeadless(true));
Page page = browser.newPage();
```

## Browser installieren

```bash
npx playwright install chromium
npx playwright install           # alle
```

## Navigation

```java
page.navigate("http://localhost:8080");
page.navigate("http://localhost:8080",
    new Page.NavigateOptions().setTimeout(30000));
page.goBack();
page.goForward();
page.reload();
```

## Selektoren

```java
// CSS
page.locator("#id")
page.locator(".class")
page.locator("button")
page.locator("input[name='email']")
page.locator("div.container > p")

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

// XPath
page.locator("xpath=//button[@type='submit']")
```

## Interaktionen

```java
// Klicken
page.locator("#btn").click();
page.locator("#btn").dblclick();
page.locator("#btn").click(new Locator.ClickOptions().setButton(MouseButton.RIGHT));

// Text eingeben
page.locator("#input").fill("text");
page.locator("#input").clear();
page.locator("#input").type("text");   // Langsam tippen

// Checkbox / Radio
page.locator("#checkbox").check();
page.locator("#checkbox").uncheck();
page.locator("#checkbox").setChecked(true);

// Select
page.locator("select").selectOption("value");
page.locator("select").selectOption(new SelectOption().setLabel("Label"));

// Datei Upload
page.locator("input[type='file']").setInputFiles(Paths.get("file.pdf"));

// Hover
page.locator("#menu").hover();

// Drag & Drop
page.locator("#source").dragTo(page.locator("#target"));
```

## Assertions

```java
import static com.microsoft.playwright.assertions.PlaywrightAssertions.*;

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
assertThat(page.locator("#el")).hasId("myId");
assertThat(page.locator("input")).hasValue("text");

// Count
assertThat(page.locator(".item")).hasCount(5);

// Page
assertThat(page).hasTitle("Title");
assertThat(page).hasURL("http://localhost/page");
```

## Warten

```java
// Automatisch (Standard)
page.locator("#el").click();  // Wartet automatisch

// Explizit
page.waitForSelector("#dynamic");
page.waitForLoadState(LoadState.NETWORKIDLE);
page.waitForURL("**/success");
page.waitForTimeout(1000);  // Vermeiden!

// Auf Bedingung
page.locator("#el").waitFor();
page.locator("#el").waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
```

## Screenshots / Videos

```java
// Screenshot
page.screenshot(new Page.ScreenshotOptions()
    .setPath(Paths.get("screenshot.png")));

page.screenshot(new Page.ScreenshotOptions()
    .setPath(Paths.get("full.png"))
    .setFullPage(true));

// Element Screenshot
page.locator("#element").screenshot(new Locator.ScreenshotOptions()
    .setPath(Paths.get("element.png")));
```

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

## Cleanup

```java
page.close();
browser.close();
playwright.close();
```
