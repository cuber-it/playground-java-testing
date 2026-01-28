# REST Assured & WireMock Snippets

Copy-Paste fertige Code-Blöcke.

---

# REST Assured

## Test-Klasse Grundgerüst

```java
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

class TodoApiTest {

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.basePath = "/api";
    }

    @Test
    void shouldReturnAllTodos() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/todos")
        .then()
            .statusCode(200)
            .body("$", hasSize(greaterThan(0)));
    }
}
```

---

## GET Request

```java
// Einfach
given()
    .contentType(ContentType.JSON)
.when()
    .get("/todos")
.then()
    .statusCode(200);

// Mit Path-Parameter
given()
.when()
    .get("/todos/{id}", 1)
.then()
    .statusCode(200)
    .body("id", equalTo(1));

// Mit Query-Parameter
given()
    .queryParam("status", "active")
    .queryParam("page", 0)
    .queryParam("size", 10)
.when()
    .get("/todos")
.then()
    .statusCode(200);
```

---

## POST Request

```java
given()
    .contentType(ContentType.JSON)
    .body("""
        {
            "title": "New Todo",
            "done": false
        }
        """)
.when()
    .post("/todos")
.then()
    .statusCode(201)
    .body("id", notNullValue())
    .body("title", equalTo("New Todo"));

// Mit Object (Jackson)
var todo = new Todo("New Todo");

given()
    .contentType(ContentType.JSON)
    .body(todo)
.when()
    .post("/todos")
.then()
    .statusCode(201);
```

---

## PUT Request

```java
given()
    .contentType(ContentType.JSON)
    .body("""
        {
            "title": "Updated",
            "done": true
        }
        """)
.when()
    .put("/todos/{id}", 1)
.then()
    .statusCode(200)
    .body("title", equalTo("Updated"));
```

---

## DELETE Request

```java
given()
.when()
    .delete("/todos/{id}", 1)
.then()
    .statusCode(204);
```

---

## Headers

```java
given()
    .header("Authorization", "Bearer " + token)
    .header("Accept-Language", "de")
.when()
    .get("/protected")
.then()
    .statusCode(200);

// Basic Auth
given()
    .auth().basic("user", "password")
.when()
    .get("/protected")
.then()
    .statusCode(200);
```

---

## JSON Path Assertions

```java
.body("title", equalTo("Test"))           // Einfaches Feld
.body("user.name", equalTo("Max"))        // Nested
.body("$", hasSize(3))                    // Array-Größe
.body("[0].title", equalTo("First"))      // Array-Index
.body("title", hasItems("A", "B"))        // Array enthält
.body("id", notNullValue())               // Nicht null
.body("count", greaterThan(0))            // Vergleich
.body("tags", hasItem("important"))       // Liste enthält
```

---

## Response extrahieren

```java
// Wert extrahieren
String title = given()
    .when().get("/todos/1")
    .then().extract().path("title");

// Ganzes Objekt
Todo todo = given()
    .when().get("/todos/1")
    .then().extract().as(Todo.class);

// Response-Objekt
Response response = given()
    .when().get("/todos")
    .then().extract().response();

int status = response.statusCode();
String body = response.body().asString();
```

---

## Logging (Debug)

```java
given()
    .log().all()              // Request loggen
.when()
    .get("/todos")
.then()
    .log().ifError()          // Response bei Fehler
    .log().ifValidationFails()
    .statusCode(200);
```

---

# WireMock

## Test-Klasse Grundgerüst

```java
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

class ExternalServiceTest {

    static WireMockServer wireMockServer;

    @BeforeAll
    static void startServer() {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
        WireMock.configureFor("localhost", 8089);
    }

    @AfterAll
    static void stopServer() {
        wireMockServer.stop();
    }

    @BeforeEach
    void reset() {
        wireMockServer.resetAll();
    }

    @Test
    void shouldCallExternalService() {
        // Arrange
        stubFor(get(urlEqualTo("/api/external"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"result\": true}")));

        // Act
        // ... call your service that uses the external API

        // Assert
        verify(getRequestedFor(urlEqualTo("/api/external")));
    }
}
```

---

## GET Stub

```java
stubFor(get(urlEqualTo("/api/users/1"))
    .willReturn(aResponse()
        .withStatus(200)
        .withHeader("Content-Type", "application/json")
        .withBody("""
            {
                "id": 1,
                "name": "Max Mustermann"
            }
            """)));
```

---

## POST Stub

```java
stubFor(post(urlEqualTo("/api/users"))
    .withHeader("Content-Type", containing("application/json"))
    .withRequestBody(containing("\"name\""))
    .willReturn(aResponse()
        .withStatus(201)
        .withBody("{\"id\": 1}")));
```

---

## URL Matching

```java
urlEqualTo("/exact/path")           // Exakt
urlPathEqualTo("/path")             // Ohne Query-Parameter
urlPathMatching("/users/\\d+")      // Regex
urlMatching("/api/.*")              // Alles unter /api
```

---

## Request Matching

```java
// Query-Parameter
stubFor(get(urlPathEqualTo("/search"))
    .withQueryParam("q", equalTo("test"))
    .withQueryParam("page", matching("\\d+"))
    .willReturn(aResponse().withStatus(200)));

// Header
stubFor(get(urlEqualTo("/protected"))
    .withHeader("Authorization", equalTo("Bearer token123"))
    .willReturn(aResponse().withStatus(200)));

// Body (JSON Path)
stubFor(post(urlEqualTo("/api"))
    .withRequestBody(matchingJsonPath("$.title"))
    .withRequestBody(matchingJsonPath("$.status", equalTo("active")))
    .willReturn(aResponse().withStatus(201)));
```

---

## Response konfigurieren

```java
.willReturn(aResponse()
    .withStatus(200)
    .withStatusMessage("OK")
    .withHeader("Content-Type", "application/json")
    .withHeader("X-Custom", "value")
    .withBody("{\"success\": true}")
    .withBodyFile("response.json")     // aus __files/
    .withFixedDelay(1000));            // 1s Verzögerung
```

---

## Fehler simulieren

```java
// HTTP Error
stubFor(get(urlEqualTo("/api"))
    .willReturn(aResponse()
        .withStatus(500)
        .withBody("{\"error\": \"Internal Server Error\"}")));

// Timeout
stubFor(get(urlEqualTo("/slow"))
    .willReturn(aResponse()
        .withFixedDelay(60000)));  // 60 Sekunden

// Verbindungsfehler
stubFor(get(urlEqualTo("/broken"))
    .willReturn(aResponse()
        .withFault(Fault.CONNECTION_RESET_BY_PEER)));
```

---

## Verification

```java
// Wurde aufgerufen
verify(getRequestedFor(urlEqualTo("/api/users")));

// Anzahl Aufrufe
verify(exactly(2), postRequestedFor(urlEqualTo("/api")));
verify(moreThan(0), getRequestedFor(urlPathMatching("/api/.*")));

// Mit bestimmtem Body
verify(postRequestedFor(urlEqualTo("/api"))
    .withRequestBody(containing("title")));

// Nie aufgerufen
verify(0, deleteRequestedFor(urlPathMatching(".*")));
```

---

## Scenarios (Stateful)

```java
// Erster Aufruf: pending
stubFor(get("/order/status")
    .inScenario("Order")
    .whenScenarioStateIs(Scenario.STARTED)
    .willReturn(aResponse().withBody("{\"state\":\"pending\"}"))
    .willSetStateTo("processed"));

// Zweiter Aufruf: done
stubFor(get("/order/status")
    .inScenario("Order")
    .whenScenarioStateIs("processed")
    .willReturn(aResponse().withBody("{\"state\":\"done\"}")));
```

---

## Response aus Datei

Datei ablegen in: `src/test/resources/__files/response.json`

```java
stubFor(get(urlEqualTo("/api/data"))
    .willReturn(aResponse()
        .withBodyFile("response.json")));
```
