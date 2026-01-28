# Pact - Consumer-Driven Contract Testing

## Was ist Contract Testing?

Testet die **Schnittstelle** zwischen Services, ohne dass beide gleichzeitig laufen muessen.

```
┌──────────────┐                          ┌──────────────┐
│   Consumer   │  ──── Contract ────────> │   Provider   │
│   (Frontend) │       (Pact-File)        │   (Backend)  │
└──────────────┘                          └──────────────┘
       │                                         │
       ▼                                         ▼
┌──────────────┐                          ┌──────────────┐
│ Consumer-    │                          │ Provider-    │
│ Test         │                          │ Verification │
│ generiert    │                          │ prueft       │
│ Pact-File    │                          │ gegen Pact   │
└──────────────┘                          └──────────────┘
```

## Consumer-Driven?

1. **Consumer definiert** was er braucht (Pact-File)
2. **Provider verifiziert** dass er es liefern kann
3. **Vertrag** ist die Single Source of Truth

## Setup im Playground

```java
// src/test/java/.../rest/contract/TodoContractTest.java

@ExtendWith(PactConsumerTestExt.class)
@DisplayName("Todo API Contract Tests")
class TodoContractTest {

    // Pact-Definition
    @Pact(consumer = "TodoConsumer", provider = "TodoProvider")
    public V4Pact getAllTodosPact(PactDslWithProvider builder) {
        return builder
            .given("Es existieren Todos")           // Provider-State
            .uponReceiving("Request fuer alle Todos")
                .path("/api/todos")
                .method("GET")
            .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body(new PactDslJsonBody()
                    .minArrayLike("", 1)            // Array mit min. 1 Element
                        .integerType("id", 1)
                        .stringType("title", "Beispiel Todo")
                        .booleanType("done", false)
                    .closeArray())
            .toPact(V4Pact.class);
    }

    // Test gegen Mock-Server
    @Test
    @PactTestFor(pactMethod = "getAllTodosPact")
    void testGetAllTodos(MockServer mockServer) {
        RestClient client = RestClient.builder()
            .baseUrl(mockServer.getUrl())
            .build();

        ResponseEntity<String> response = client.get()
            .uri("/api/todos")
            .retrieve()
            .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
```

**Was wurde gemacht:** Consumer definiert Erwartungen an die Todo-API.

**Warum:** Contract wird unabhaengig vom echten Provider getestet.

## Pact-DSL - Response-Body definieren

```java
// Einfaches Objekt
new PactDslJsonBody()
    .integerType("id", 1)
    .stringType("title", "Test")
    .booleanType("done", false)

// Array
new PactDslJsonBody()
    .minArrayLike("todos", 1)       // Mindestens 1 Element
        .integerType("id")
        .stringType("title")
    .closeArray()

// Optionale Felder
new PactDslJsonBody()
    .stringType("title", "Required")
    .stringMatcher("description", ".*", "Optional")

// Datum
new PactDslJsonBody()
    .date("dueDate", "yyyy-MM-dd", new Date())
```

## Provider States

```java
.given("Es existieren Todos")           // Vorbedingung
.given("Todo mit ID 1 existiert")
.given("System ist bereit")
.given("Todo mit ID 999 existiert nicht")
```

**Was wurde gemacht:** Verschiedene Szenarien definiert.

**Warum:** Provider muss diese Zustaende beim Verifizieren einrichten.

## Generierte Pact-Files

Nach `mvn test` in `target/pacts/`:

```json
{
  "consumer": { "name": "TodoConsumer" },
  "provider": { "name": "TodoProvider" },
  "interactions": [
    {
      "description": "Request fuer alle Todos",
      "providerStates": [{ "name": "Es existieren Todos" }],
      "request": {
        "method": "GET",
        "path": "/api/todos"
      },
      "response": {
        "status": 200,
        "headers": { "Content-Type": "application/json" },
        "body": [{ "id": 1, "title": "Beispiel Todo", "done": false }]
      }
    }
  ]
}
```

## Provider-Verification (auf Provider-Seite)

```java
@Provider("TodoProvider")
@PactFolder("pacts")
class TodoProviderTest {

    @BeforeEach
    void setUp(PactVerificationContext context) {
        context.setTarget(new HttpTestTarget("localhost", 8080));
    }

    @State("Es existieren Todos")
    void todosExist() {
        // Testdaten anlegen
        repository.save(new Todo("Test Todo"));
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void verifyPact(PactVerificationContext context) {
        context.verifyInteraction();
    }
}
```

## Playground: Verschiedene Interaktionen

```java
// POST - Todo erstellen
@Pact(consumer = "TodoConsumer", provider = "TodoProvider")
public V4Pact createTodoPact(PactDslWithProvider builder) {
    return builder
        .given("System ist bereit")
        .uponReceiving("Request zum Erstellen eines Todos")
            .path("/api/todos")
            .method("POST")
            .headers(Map.of("Content-Type", "application/json"))
            .body(new PactDslJsonBody()
                .stringType("title", "Neues Todo"))
        .willRespondWith()
            .status(200)
            .body(new PactDslJsonBody()
                .integerType("id")
                .stringType("title", "Neues Todo"))
        .toPact(V4Pact.class);
}

// DELETE
@Pact(consumer = "TodoConsumer", provider = "TodoProvider")
public V4Pact deleteTodoPact(PactDslWithProvider builder) {
    return builder
        .given("Todo mit ID 1 existiert")
        .uponReceiving("Request zum Loeschen")
            .path("/api/todos/1")
            .method("DELETE")
        .willRespondWith()
            .status(204)
        .toPact(V4Pact.class);
}

// 404 Not Found
@Pact(consumer = "TodoConsumer", provider = "TodoProvider")
public V4Pact todoNotFoundPact(PactDslWithProvider builder) {
    return builder
        .given("Todo mit ID 999 existiert nicht")
        .uponReceiving("Request fuer nicht existierendes Todo")
            .path("/api/todos/999")
            .method("GET")
        .willRespondWith()
            .status(404)
        .toPact(V4Pact.class);
}
```

## Vorteile von Contract Testing

| Aspekt | Ohne Contract | Mit Contract |
|--------|---------------|--------------|
| Deployment | Abhaengig | Unabhaengig |
| Feedback | Spaet (Integration) | Frueh (Build) |
| Fehlersuche | Schwer | Einfach |
| Dokumentation | Manuell | Automatisch |

## Best Practices

1. **Consumer first** - Consumer definiert seine Beduerfnisse
2. **Minimale Contracts** - nur das Noetige, nicht alles
3. **Pact Broker** fuer zentrale Contract-Verwaltung
4. **CI/CD Integration** - Contracts automatisch pruefen
5. **Versionierung** - Contracts mit Code versionieren
