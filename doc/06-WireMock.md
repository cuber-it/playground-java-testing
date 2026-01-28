# WireMock - Externe Services Mocken

## Was ist WireMock?

Ein HTTP-Mock-Server, der **externe Services simuliert**. Ermoeglicht Tests ohne Abhaengigkeit von echten externen APIs.

## Warum WireMock?

```
┌─────────────────┐     ┌──────────────────┐     ┌────────────────┐
│  Unser Service  │────>│  Externer Service │────>│  Nicht unter   │
│                 │     │  (Notification)   │     │  unserer       │
│                 │     │                   │     │  Kontrolle     │
└─────────────────┘     └──────────────────┘     └────────────────┘
        │                        │
        │                        ▼
        │               ┌──────────────────┐
        └──────────────>│    WireMock      │  <-- Kontrollierbar
                        │    (Mock-Server) │      Deterministisch
                        └──────────────────┘      Schnell
```

## Setup im Playground

```java
// src/test/java/.../rest/wiremock/ExternalServiceWireMockTest.java

class ExternalServiceWireMockTest {

    private static WireMockServer wireMockServer;
    private RestClient restClient;

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
        WireMock.configureFor("localhost", 8089);
    }

    @AfterAll
    static void stopWireMock() {
        wireMockServer.stop();
    }

    @BeforeEach
    void setUp() {
        wireMockServer.resetAll();  // Stubs zuruecksetzen
        restClient = RestClient.builder()
            .baseUrl("http://localhost:8089")
            .build();
    }
}
```

**Was wurde gemacht:** WireMock-Server startet auf Port 8089, RestClient greift darauf zu.

**Warum:** Simuliert externen Benachrichtigungsservice.

## Stubbing - Responses definieren

### Einfacher GET-Request

```java
@Test
void getStatus() {
    // Stub definieren
    stubFor(get(urlEqualTo("/status"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("{\"healthy\": true}")));

    // Request ausfuehren
    ResponseEntity<String> response = restClient.get()
        .uri("/status")
        .retrieve()
        .toEntity(String.class);

    // Pruefen
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).contains("healthy");
}
```

### POST-Request mit Body-Matching

```java
@Test
void sendNotification() {
    stubFor(post(urlEqualTo("/notifications"))
        .withHeader("Content-Type", containing("application/json"))
        .willReturn(aResponse()
            .withStatus(200)
            .withBody("{\"status\": \"sent\", \"id\": \"12345\"}")));

    ResponseEntity<String> response = restClient.post()
        .uri("/notifications")
        .header("Content-Type", "application/json")
        .body("{\"message\": \"Todo erledigt!\"}")
        .retrieve()
        .toEntity(String.class);

    assertThat(response.getBody()).contains("sent");
}
```

**Was wurde gemacht:** POST-Request an externen Service simuliert.

**Warum:** Test ist unabhaengig vom echten Notification-Service.

## URL-Matching

```java
// Exakte URL
stubFor(get(urlEqualTo("/api/todos")));

// URL-Pattern (Regex)
stubFor(get(urlPathMatching("/notifications/\\d+")));

// URL mit Query-Parametern
stubFor(get(urlPathEqualTo("/search"))
    .withQueryParam("q", equalTo("todo")));
```

## Error-Responses simulieren

```java
@Test
void serverError() {
    stubFor(post(urlEqualTo("/notifications"))
        .willReturn(aResponse()
            .withStatus(500)
            .withBody("{\"error\": \"Internal Server Error\"}")));

    assertThatThrownBy(() ->
        restClient.post()
            .uri("/notifications")
            .body("{}")
            .retrieve()
            .toEntity(String.class)
    ).isInstanceOf(RestClientException.class);
}
```

**Was wurde gemacht:** Server-Fehler simulieren.

**Warum:** Testen, wie unser Code mit Fehlern umgeht.

## Verzoegerungen simulieren (Timeout-Tests)

```java
@Test
void slowResponse() {
    stubFor(get(urlEqualTo("/slow"))
        .willReturn(aResponse()
            .withStatus(200)
            .withFixedDelay(2000)  // 2 Sekunden Verzoegerung
            .withBody("{\"result\": \"slow\"}")));

    // Timeout-Verhalten testen
}
```

## Verification - Aufrufe pruefen

```java
@Test
void verifyCallWasMade() {
    stubFor(post(urlEqualTo("/log")).willReturn(aResponse().withStatus(200)));

    // 3 Aufrufe machen
    for (int i = 0; i < 3; i++) {
        restClient.post().uri("/log").body("{}").retrieve().toBodilessEntity();
    }

    // Pruefen: genau 3 Aufrufe?
    verify(exactly(3), postRequestedFor(urlEqualTo("/log")));

    // Mit Body-Pruefung
    verify(postRequestedFor(urlEqualTo("/log"))
        .withRequestBody(containing("event")));
}
```

**Was wurde gemacht:** Pruefen, ob und wie oft externe API aufgerufen wurde.

**Warum:** Sicherstellen, dass Integration korrekt funktioniert.

## Scenarios - Zustandsabhaengige Responses

```java
@Test
void statefulBehavior() {
    // Erster Aufruf: "pending"
    stubFor(get("/status")
        .inScenario("Order")
        .whenScenarioStateIs(STARTED)
        .willReturn(aResponse().withBody("{\"state\": \"pending\"}"))
        .willSetStateTo("completed"));

    // Zweiter Aufruf: "completed"
    stubFor(get("/status")
        .inScenario("Order")
        .whenScenarioStateIs("completed")
        .willReturn(aResponse().withBody("{\"state\": \"done\"}")));
}
```

## Best Practices

1. **@BeforeEach: resetAll()** - Stubs zwischen Tests zuruecksetzen
2. **Port konfigurierbar** machen fuer parallele Tests
3. **Fehlerszenarien testen** - nicht nur Happy Path
4. **Timeouts simulieren** fuer Resilience-Tests
5. **Verification sparsam** - nur wenn wirklich relevant
