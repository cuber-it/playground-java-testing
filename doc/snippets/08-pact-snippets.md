# Pact Contract Testing Snippets

Copy-Paste fertige Code-Blöcke.

---

## Consumer Test Grundgerüst

```java
import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "TodoProvider")
class TodoConsumerTest {

    @Pact(consumer = "TodoConsumer")
    public V4Pact getAllTodos(PactDslWithProvider builder) {
        return builder
            .given("todos exist")
            .uponReceiving("a request for all todos")
                .path("/api/todos")
                .method("GET")
            .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body(new PactDslJsonBody()
                    .minArrayLike("todos", 1)
                        .integerType("id", 1)
                        .stringType("title", "Test Todo")
                        .booleanType("done", false)
                    .closeArray())
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "getAllTodos")
    void shouldGetAllTodos(MockServer mockServer) {
        // Arrange
        var client = new TodoClient(mockServer.getUrl());

        // Act
        var todos = client.getAllTodos();

        // Assert
        assertThat(todos).isNotEmpty();
        assertThat(todos.get(0).getTitle()).isEqualTo("Test Todo");
    }
}
```

---

## Consumer - GET Request

```java
@Pact(consumer = "MyConsumer")
public V4Pact getTodoById(PactDslWithProvider builder) {
    return builder
        .given("todo with id 1 exists")
        .uponReceiving("a request for todo with id 1")
            .path("/api/todos/1")
            .method("GET")
        .willRespondWith()
            .status(200)
            .headers(Map.of("Content-Type", "application/json"))
            .body(new PactDslJsonBody()
                .integerType("id", 1)
                .stringType("title", "Test Todo")
                .booleanType("done", false))
        .toPact(V4Pact.class);
}

@Test
@PactTestFor(pactMethod = "getTodoById")
void shouldGetTodoById(MockServer mockServer) {
    var client = new TodoClient(mockServer.getUrl());

    var todo = client.getTodoById(1L);

    assertThat(todo).isNotNull();
    assertThat(todo.getId()).isEqualTo(1L);
}
```

---

## Consumer - POST Request

```java
@Pact(consumer = "MyConsumer")
public V4Pact createTodo(PactDslWithProvider builder) {
    return builder
        .given("server is available")
        .uponReceiving("a request to create a todo")
            .path("/api/todos")
            .method("POST")
            .headers(Map.of("Content-Type", "application/json"))
            .body(new PactDslJsonBody()
                .stringType("title", "New Todo"))
        .willRespondWith()
            .status(201)
            .headers(Map.of("Content-Type", "application/json"))
            .body(new PactDslJsonBody()
                .integerType("id")
                .stringType("title", "New Todo")
                .booleanType("done", false))
        .toPact(V4Pact.class);
}

@Test
@PactTestFor(pactMethod = "createTodo")
void shouldCreateTodo(MockServer mockServer) {
    var client = new TodoClient(mockServer.getUrl());

    var todo = client.createTodo("New Todo");

    assertThat(todo.getId()).isNotNull();
    assertThat(todo.getTitle()).isEqualTo("New Todo");
}
```

---

## Consumer - Error Response

```java
@Pact(consumer = "MyConsumer")
public V4Pact todoNotFound(PactDslWithProvider builder) {
    return builder
        .given("todo with id 999 does not exist")
        .uponReceiving("a request for non-existing todo")
            .path("/api/todos/999")
            .method("GET")
        .willRespondWith()
            .status(404)
            .headers(Map.of("Content-Type", "application/json"))
            .body(new PactDslJsonBody()
                .stringType("error", "Not Found")
                .stringType("message", "Todo not found"))
        .toPact(V4Pact.class);
}

@Test
@PactTestFor(pactMethod = "todoNotFound")
void shouldHandle404(MockServer mockServer) {
    var client = new TodoClient(mockServer.getUrl());

    assertThatThrownBy(() -> client.getTodoById(999L))
        .isInstanceOf(NotFoundException.class);
}
```

---

## JSON Body DSL

```java
// Einfache Felder
new PactDslJsonBody()
    .integerType("id", 1)                    // Integer mit Beispielwert
    .stringType("name", "Test")              // String mit Beispielwert
    .booleanType("active", true)             // Boolean
    .numberType("price", 19.99)              // Number (decimal)
    .stringValue("status", "ACTIVE")         // Exakter Wert (kein Matcher)
    .date("createdAt", "yyyy-MM-dd")         // Datum
    .datetime("timestamp", "yyyy-MM-dd'T'HH:mm:ss")
    .uuid("uuid")                            // UUID

// Nullable
    .nullValue("deletedAt")                  // null

// Arrays
    .array("tags")
        .stringValue("important")
        .stringValue("urgent")
    .closeArray()

// Array mit Objekten
    .minArrayLike("items", 1)                // Mind. 1 Element
        .integerType("id")
        .stringType("name")
    .closeArray()

    .eachLike("items")                       // 1+ Elemente
        .integerType("id")
    .closeObject()
    .closeArray()

// Nested Object
    .object("address")
        .stringType("street")
        .stringType("city")
    .closeObject()
```

---

## Provider Test Grundgerüst

```java
import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Provider("TodoProvider")
@PactFolder("pacts")  // oder @PactBroker(...)
class TodoProviderTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp(PactVerificationContext context) {
        context.setTarget(new HttpTestTarget("localhost", port));
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void verifyPact(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @State("todos exist")
    void todosExist() {
        // Testdaten anlegen
        todoRepository.save(new Todo(1L, "Test Todo", false));
    }

    @State("todo with id 1 exists")
    void todoWithId1Exists() {
        todoRepository.save(new Todo(1L, "Test Todo", false));
    }

    @State("todo with id 999 does not exist")
    void todoNotExists() {
        todoRepository.deleteAll();
    }
}
```

---

## Provider mit Pact Broker

```java
@Provider("TodoProvider")
@PactBroker(
    url = "https://your-pact-broker.com",
    authentication = @PactBrokerAuth(
        username = "${PACT_BROKER_USERNAME}",
        password = "${PACT_BROKER_PASSWORD}"
    )
)
class TodoProviderTest {
    // ...
}

// Oder mit Token
@PactBroker(
    url = "https://your-pact-broker.com",
    authentication = @PactBrokerAuth(token = "${PACT_BROKER_TOKEN}")
)
```

---

## State mit Parametern

```java
@State("a todo exists with id")
void todoExistsWithId(Map<String, Object> params) {
    Long id = ((Number) params.get("id")).longValue();
    String title = (String) params.get("title");

    todoRepository.save(new Todo(id, title, false));
}

// Im Consumer:
.given("a todo exists with id", Map.of("id", 42, "title", "My Todo"))
```

---

## Pact mit Matchers

```java
// Regex Matcher
new PactDslJsonBody()
    .stringMatcher("email", ".*@.*\\..*", "test@example.com")
    .stringMatcher("phone", "\\d{3}-\\d{4}", "123-4567")

// Numerische Matcher
    .integerMatching("id", "\\d+", 1)
    .decimalMatching("price", "\\d+\\.\\d{2}", 19.99)

// Datum Matcher
    .date("date", "yyyy-MM-dd", new Date())
    .datetime("timestamp", "yyyy-MM-dd'T'HH:mm:ss.SSS")
    .time("time", "HH:mm:ss")

// Min/Max Array
    .minArrayLike("items", 1)        // Mindestens 1
    .maxArrayLike("items", 10)       // Maximal 10
    .minMaxArrayLike("items", 1, 5)  // 1-5 Elemente
```

---

## Pact Publish (Maven)

```xml
<plugin>
    <groupId>au.com.dius.pact.provider</groupId>
    <artifactId>maven</artifactId>
    <version>4.6.0</version>
    <configuration>
        <pactBrokerUrl>https://your-pact-broker.com</pactBrokerUrl>
        <pactBrokerToken>${PACT_BROKER_TOKEN}</pactBrokerToken>
        <tags>
            <tag>main</tag>
            <tag>${git.branch}</tag>
        </tags>
    </configuration>
</plugin>
```

```bash
mvn pact:publish
```
