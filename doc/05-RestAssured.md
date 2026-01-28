# REST Assured - REST API Testing

## Was ist REST Assured?

Ein Java-Framework fuer das Testen von REST-APIs mit einer **fluent DSL** (Domain Specific Language). Liest sich fast wie natuerliche Sprache.

## Grundstruktur: Given-When-Then

```java
given()     // Vorbedingungen (Header, Body, Auth, ...)
    .contentType(JSON)
    .body(requestBody)
.when()     // Aktion (HTTP-Methode + URL)
    .post("/api/todos")
.then()     // Assertions (Status, Body, Header, ...)
    .statusCode(200)
    .body("title", equalTo("Test"));
```

## Setup im Playground

```java
// src/test/java/.../rest/restassured/TodoRestAssuredTest.java

@SpringBootTest(webEnvironment = RANDOM_PORT)
class TodoRestAssuredTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api";
    }
}
```

**Was wurde gemacht:** Spring startet auf zufaelligem Port, RestAssured wird konfiguriert.

**Warum:** Echter HTTP-Server fuer realistische Tests.

## GET Requests

```java
@Test
@DisplayName("GET /api/todos gibt alle Todos")
void getAllTodos() {
    // Testdaten vorbereiten
    repository.save(createTodo("Erstes Todo"));
    repository.save(createTodo("Zweites Todo"));

    given()
        .contentType(ContentType.JSON)
    .when()
        .get("/todos")
    .then()
        .statusCode(200)
        .body("$", hasSize(2))
        .body("title", hasItems("Erstes Todo", "Zweites Todo"));
}

@Test
@DisplayName("GET /api/todos/{id} mit Path-Parameter")
void getTodoById() {
    Todo saved = repository.save(createTodo("Find Me"));

    given()
        .contentType(ContentType.JSON)
    .when()
        .get("/todos/{id}", saved.getId())  // Path-Parameter
    .then()
        .statusCode(200)
        .body("title", equalTo("Find Me"));
}
```

## POST Requests

```java
@Test
@DisplayName("POST /api/todos erstellt neues Todo")
void createTodo() {
    String requestBody = """
        {
            "title": "RestAssured Test",
            "description": "Via RestAssured erstellt"
        }
        """;

    given()
        .contentType(ContentType.JSON)
        .body(requestBody)
    .when()
        .post("/todos")
    .then()
        .statusCode(200)
        .body("id", notNullValue())
        .body("title", equalTo("RestAssured Test"))
        .body("done", equalTo(false));
}
```

**Was wurde gemacht:** Todo per POST erstellen und Response validieren.

**Warum:** Zeigt JSON-Request-Body und Response-Validierung.

## PUT und DELETE

```java
@Test
void updateTodo() {
    Todo saved = repository.save(createTodo("Original"));

    given()
        .contentType(ContentType.JSON)
        .body("{\"title\": \"Updated\"}")
    .when()
        .put("/todos/{id}", saved.getId())
    .then()
        .statusCode(200)
        .body("title", equalTo("Updated"));
}

@Test
void deleteTodo() {
    Todo saved = repository.save(createTodo("Delete Me"));

    given()
    .when()
        .delete("/todos/{id}", saved.getId())
    .then()
        .statusCode(204);  // No Content
}
```

## JSON Path Assertions

```java
// Einfacher Wert
.body("title", equalTo("Test"))

// Nested Object
.body("user.name", equalTo("Max"))

// Array-Groesse
.body("$", hasSize(3))

// Array-Element
.body("[0].title", equalTo("Erstes"))

// Alle Elemente im Array
.body("title", hasItems("A", "B", "C"))

// Beliebiges Element im Array
.body("title", hasItem("Gesuchtes"))
```

## Response-Zeit pruefen

```java
@Test
@DisplayName("Response unter 500ms")
void responseTime() {
    given()
    .when()
        .get("/todos")
    .then()
        .time(lessThan(500L));  // Millisekunden
}
```

**Was wurde gemacht:** Performance-Assertion.

**Warum:** Fruehes Erkennen von Performance-Problemen.

## Content-Type pruefen

```java
@Test
void contentType() {
    given()
    .when()
        .get("/todos")
    .then()
        .contentType(ContentType.JSON);
}
```

## Response extrahieren

```java
@Test
void extractResponse() {
    String title = given()
        .when()
            .get("/todos/1")
        .then()
            .extract()
            .path("title");

    assertThat(title).isEqualTo("Mein Todo");
}
```

## Best Practices

1. **Fluent API nutzen** - liest sich wie Spezifikation
2. **Hamcrest-Matcher** fuer komplexe Assertions
3. **Base-URL in @BeforeEach** setzen
4. **Testdaten isolieren** - Repository vor jedem Test leeren
5. **JSON-Strings** als Text-Blocks (Java 15+) fuer Lesbarkeit
