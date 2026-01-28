# REST Assured Cheatsheet

## Dependency

**Maven:**
```xml
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <version>5.5.0</version>
    <scope>test</scope>
</dependency>

<!-- Optional: JSON Schema Validation -->
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>json-schema-validator</artifactId>
    <version>5.5.0</version>
    <scope>test</scope>
</dependency>
```

**Gradle:**
```groovy
testImplementation 'io.rest-assured:rest-assured:5.5.0'
testImplementation 'io.rest-assured:json-schema-validator:5.5.0'
```

## Imports

```java
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
```

## Setup

```java
@BeforeEach
void setUp() {
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = 8080;
    RestAssured.basePath = "/api";
}
```

## GET Request

```java
given()
    .contentType(ContentType.JSON)
.when()
    .get("/todos")
.then()
    .statusCode(200)
    .body("$", hasSize(2));
```

## POST Request

```java
given()
    .contentType(ContentType.JSON)
    .body("{\"title\":\"New Todo\"}")
.when()
    .post("/todos")
.then()
    .statusCode(201)
    .body("id", notNullValue());
```

## PUT Request

```java
given()
    .contentType(ContentType.JSON)
    .body("{\"title\":\"Updated\"}")
.when()
    .put("/todos/{id}", 1)
.then()
    .statusCode(200);
```

## DELETE Request

```java
given()
.when()
    .delete("/todos/{id}", 1)
.then()
    .statusCode(204);
```

## Path & Query Parameter

```java
// Path Parameter
.get("/todos/{id}", 123)
.get("/users/{userId}/todos/{todoId}", 1, 5)

// Query Parameter
.queryParam("page", 1)
.queryParam("size", 10)
.queryParams(Map.of("sort", "asc", "filter", "active"))
```

## Headers

```java
.header("Authorization", "Bearer token")
.header("Accept-Language", "de")
.headers(Map.of("X-Custom", "value"))
```

## Body

```java
// String
.body("{\"title\":\"Test\"}")

// Object (Jackson)
.body(new Todo("Test"))

// Aus Datei
.body(new File("request.json"))
```

## JSON Path Assertions

```java
.body("title", equalTo("Test"))           // Einfach
.body("user.name", equalTo("Max"))        // Nested
.body("$", hasSize(3))                    // Array Größe
.body("[0].title", equalTo("First"))      // Array Index
.body("title", hasItems("A", "B"))        // Array enthält
.body("findAll{it.done==true}", hasSize(2))  // Filter
```

## Hamcrest Matchers

```java
equalTo(value)
not(equalTo(value))
nullValue()
notNullValue()
hasSize(3)
greaterThan(5)
lessThan(10)
hasItems("a", "b")
hasItem("a")
containsString("text")
startsWith("Hello")
endsWith("World")
```

## Response extrahieren

```java
// Wert extrahieren
String title = given()
    .when().get("/todos/1")
    .then().extract().path("title");

// Response Object
Response response = given()
    .when().get("/todos")
    .then().extract().response();

int status = response.statusCode();
String body = response.body().asString();
List<Todo> todos = response.jsonPath().getList(".", Todo.class);
```

## Authentication

```java
.auth().basic("user", "password")
.auth().oauth2("token")
.header("Authorization", "Bearer " + token)
```

## Response Zeit

```java
.then()
    .time(lessThan(500L));  // < 500ms
```

## Content-Type prüfen

```java
.then()
    .contentType(ContentType.JSON);
```

## Logging (Debugging)

```java
given()
    .log().all()           // Request loggen
.when()
    .get("/todos")
.then()
    .log().ifError()       // Response bei Fehler
    .log().ifValidationFails()
    .log().body();         // Immer Body
```

## Mehrere Assertions

```java
.then()
    .statusCode(200)
    .contentType(ContentType.JSON)
    .header("X-Custom", notNullValue())
    .body("id", equalTo(1))
    .body("title", equalTo("Test"))
    .body("done", equalTo(false));
```
