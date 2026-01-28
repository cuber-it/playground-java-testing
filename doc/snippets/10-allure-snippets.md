# Allure Report Snippets

Copy-Paste fertige Code-Blöcke für Allure Annotationen und Features.

---

## Dependency

**Maven:**
```xml
<dependency>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-junit5</artifactId>
    <version>2.25.0</version>
    <scope>test</scope>
</dependency>
```

**Gradle:**
```groovy
testImplementation 'io.qameta.allure:allure-junit5:2.25.0'
```

---

## Test-Klasse mit Allure Annotationen

```java
import io.qameta.allure.*;
import org.junit.jupiter.api.*;

@Epic("User Management")
@Feature("User Registration")
class UserRegistrationTest {

    @Test
    @Story("New user can register")
    @Description("Verifies that a new user can successfully register with valid data")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("Max Mustermann")
    @Link(name = "Requirements", url = "https://wiki.example.com/requirements/user-reg")
    @Issue("JIRA-123")
    @TmsLink("TC-456")
    void shouldRegisterNewUser() {
        // Test implementation
    }
}
```

---

## @Step - Testschritte dokumentieren

```java
@Test
void shouldCompleteCheckout() {
    loginAsUser("max@example.com");
    addProductToCart("Book");
    proceedToCheckout();
    enterShippingAddress();
    confirmOrder();
}

@Step("Login as user {email}")
void loginAsUser(String email) {
    // ...
}

@Step("Add product '{productName}' to cart")
void addProductToCart(String productName) {
    // ...
}

@Step("Proceed to checkout")
void proceedToCheckout() {
    // ...
}

@Step("Enter shipping address")
void enterShippingAddress() {
    // ...
}

@Step("Confirm order")
void confirmOrder() {
    // ...
}
```

---

## @Step in Service-Klassen

```java
public class UserActions {

    @Step("Create user with name '{name}' and email '{email}'")
    public User createUser(String name, String email) {
        User user = new User(name, email);
        userRepository.save(user);
        return user;
    }

    @Step("Delete user with id {userId}")
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Step("Find user by email '{email}'")
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
```

---

## Attachments

```java
import io.qameta.allure.Allure;

@Test
void shouldGenerateReport() {
    String reportContent = service.generateReport();

    // Text Attachment
    Allure.addAttachment("Report Content", "text/plain", reportContent);

    // JSON Attachment
    Allure.addAttachment("Response", "application/json", jsonResponse);

    // Screenshot (Bytes)
    byte[] screenshot = takeScreenshot();
    Allure.addAttachment("Screenshot", "image/png",
        new ByteArrayInputStream(screenshot), "png");

    // From File
    Allure.addAttachment("Log File", "text/plain",
        Files.newInputStream(Path.of("app.log")), "log");
}

// Als Step mit Attachment
@Step("Take screenshot")
@Attachment(value = "Page Screenshot", type = "image/png")
public byte[] takeScreenshot() {
    return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
}
```

---

## Severity Levels

```java
@Test
@Severity(SeverityLevel.BLOCKER)    // Blocker - System unusable
void criticalSecurityTest() { }

@Test
@Severity(SeverityLevel.CRITICAL)   // Critical - Major feature broken
void paymentProcessingTest() { }

@Test
@Severity(SeverityLevel.NORMAL)     // Normal - Default
void regularFeatureTest() { }

@Test
@Severity(SeverityLevel.MINOR)      // Minor - Small issue
void uiAlignmentTest() { }

@Test
@Severity(SeverityLevel.TRIVIAL)    // Trivial - Cosmetic
void typoInLabelTest() { }
```

---

## Epics, Features, Stories (BDD Hierarchy)

```java
// Auf Klassen-Ebene
@Epic("E-Commerce Platform")
@Feature("Shopping Cart")
class ShoppingCartTest {

    @Test
    @Story("Add item to cart")
    void shouldAddItemToCart() { }

    @Test
    @Story("Remove item from cart")
    void shouldRemoveItemFromCart() { }

    @Test
    @Story("Update quantity")
    void shouldUpdateQuantity() { }
}

// Mehrere Features/Stories
@Test
@Stories({
    @Story("User can login"),
    @Story("Session is created")
})
void shouldLoginAndCreateSession() { }
```

---

## Links

```java
// Issue Tracker
@Test
@Issue("JIRA-123")
@Issue("JIRA-456")
void shouldFixBug() { }

// Test Management System
@Test
@TmsLink("TC-789")
void shouldMatchTestCase() { }

// Custom Links
@Test
@Link(name = "Documentation", url = "https://docs.example.com/feature")
@Link(name = "Design", url = "https://figma.com/design/123")
@Links({
    @Link(name = "API Spec", url = "https://api.example.com/docs"),
    @Link(name = "Wiki", url = "https://wiki.example.com")
})
void shouldFollowSpec() { }
```

---

## Owner und Tags

```java
@Test
@Owner("team-backend")
@Tag("smoke")
@Tag("regression")
void importantTest() { }

// Auf Klassen-Ebene
@Owner("Max Mustermann")
class UserServiceTest {
    // Alle Tests haben diesen Owner
}
```

---

## Description

```java
@Test
@Description("""
    This test verifies the user registration flow:
    1. User enters valid registration data
    2. System validates the data
    3. User account is created
    4. Confirmation email is sent

    Preconditions:
    - Email server is available
    - Database is empty
    """)
void shouldRegisterUser() { }

// HTML Description
@Test
@Description(useJavaDoc = true)
/**
 * <h3>User Login Test</h3>
 * <p>Verifies that users can login with valid credentials.</p>
 * <ul>
 *   <li>Enter username</li>
 *   <li>Enter password</li>
 *   <li>Click login</li>
 * </ul>
 */
void shouldLogin() { }
```

---

## Flaky Tests markieren

```java
@Test
@Flaky
@Description("This test is known to be flaky due to timing issues")
void flakyNetworkTest() { }
```

---

## Muted Tests

```java
@Test
@Muted
@Description("Temporarily muted - waiting for fix in JIRA-999")
void temporarilyDisabledTest() { }
```

---

## Allure mit Selenide

```java
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;

@BeforeAll
static void setupAllure() {
    SelenideLogger.addListener("allure", new AllureSelenide()
        .screenshots(true)
        .savePageSource(true));
}

@Test
@Feature("Login")
@Story("Valid credentials")
void shouldLogin() {
    open("/login");
    $("#username").setValue("user");
    $("#password").setValue("pass");
    $("#login-btn").click();

    $(".welcome").shouldBe(visible);
}
```

---

## Allure mit REST Assured

```java
import io.qameta.allure.restassured.AllureRestAssured;

@BeforeAll
static void setup() {
    RestAssured.filters(new AllureRestAssured());
}

@Test
@Feature("User API")
@Story("Get user by ID")
void shouldGetUser() {
    given()
        .pathParam("id", 1)
    .when()
        .get("/api/users/{id}")
    .then()
        .statusCode(200);
}
```

---

## Programmatische Annotations

```java
@Test
void dynamicTest() {
    // Dynamic labels
    Allure.epic("Dynamic Epic");
    Allure.feature("Dynamic Feature");
    Allure.story("Dynamic Story");

    // Dynamic links
    Allure.link("Dynamic Link", "https://example.com");
    Allure.issue("JIRA-999", "https://jira.example.com/JIRA-999");
    Allure.tms("TC-123", "https://tms.example.com/TC-123");

    // Dynamic parameters
    Allure.parameter("Environment", "Production");
    Allure.parameter("Browser", "Chrome");

    // Dynamic description
    Allure.description("This description was added programmatically");
}
```

---

## Report generieren

```bash
# Mit Maven
mvn clean test
mvn allure:serve      # Öffnet Browser
mvn allure:report     # Generiert in target/site/allure-maven-plugin

# Report-Verzeichnis
target/allure-results/   # Raw results
target/site/allure-maven-plugin/  # HTML Report
```
