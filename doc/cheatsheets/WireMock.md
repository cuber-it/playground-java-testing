# WireMock Cheatsheet

## Dependency

**Maven:**
```xml
<dependency>
    <groupId>org.wiremock</groupId>
    <artifactId>wiremock-standalone</artifactId>
    <version>3.9.1</version>
    <scope>test</scope>
</dependency>

<!-- Oder mit JUnit 5 Extension -->
<dependency>
    <groupId>org.wiremock</groupId>
    <artifactId>wiremock</artifactId>
    <version>3.9.1</version>
    <scope>test</scope>
</dependency>
```

**Gradle:**
```groovy
testImplementation 'org.wiremock:wiremock-standalone:3.9.1'
// oder
testImplementation 'org.wiremock:wiremock:3.9.1'
```

## Setup

```java
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
```

## Imports

```java
import static com.github.tomakehurst.wiremock.client.WireMock.*;
```

## Stubbing - GET

```java
stubFor(get(urlEqualTo("/api/todos"))
    .willReturn(aResponse()
        .withStatus(200)
        .withHeader("Content-Type", "application/json")
        .withBody("[{\"id\":1,\"title\":\"Test\"}]")));
```

## Stubbing - POST

```java
stubFor(post(urlEqualTo("/api/todos"))
    .withHeader("Content-Type", containing("application/json"))
    .withRequestBody(containing("title"))
    .willReturn(aResponse()
        .withStatus(201)
        .withBody("{\"id\":1}")));
```

## URL Matching

```java
urlEqualTo("/exact/path")           // Exakt
urlPathEqualTo("/path")             // Ohne Query
urlPathMatching("/todos/\\d+")      // Regex
urlMatching("/api/.*")              // Alles unter /api
```

## Query Parameter

```java
stubFor(get(urlPathEqualTo("/search"))
    .withQueryParam("q", equalTo("test"))
    .withQueryParam("page", matching("\\d+"))
    .willReturn(aResponse().withStatus(200)));
```

## Header Matching

```java
.withHeader("Authorization", equalTo("Bearer token"))
.withHeader("Content-Type", containing("json"))
.withHeader("Accept", matching("application/.*"))
```

## Body Matching

```java
.withRequestBody(equalTo("{\"exact\":\"match\"}"))
.withRequestBody(containing("partial"))
.withRequestBody(matchingJsonPath("$.title"))
.withRequestBody(matchingJsonPath("$.id", equalTo("1")))
```

## Response

```java
.willReturn(aResponse()
    .withStatus(200)
    .withStatusMessage("OK")
    .withHeader("Content-Type", "application/json")
    .withBody("{\"success\":true}")
    .withBodyFile("response.json")     // aus __files/
    .withFixedDelay(1000)              // 1s Verzögerung
    .withLogNormalRandomDelay(90, 0.1) // Zufällig
);
```

## Fehler simulieren

```java
// HTTP Error
.willReturn(aResponse().withStatus(500))
.willReturn(aResponse().withStatus(404))

// Verbindungsfehler
.willReturn(aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER))
.willReturn(aResponse().withFault(Fault.EMPTY_RESPONSE))

// Timeout
.willReturn(aResponse().withFixedDelay(60000))
```

## Verification

```java
// Wurde aufgerufen?
verify(getRequestedFor(urlEqualTo("/api/todos")));

// Wie oft?
verify(exactly(2), postRequestedFor(urlEqualTo("/api/todos")));
verify(moreThan(0), getRequestedFor(urlPathMatching("/api/.*")));

// Mit welchem Body?
verify(postRequestedFor(urlEqualTo("/api/todos"))
    .withRequestBody(containing("title")));

// Nie aufgerufen
verify(0, deleteRequestedFor(urlPathMatching(".*")));
```

## Scenarios (Stateful)

```java
stubFor(get("/status")
    .inScenario("Order")
    .whenScenarioStateIs(STARTED)
    .willReturn(aResponse().withBody("{\"state\":\"pending\"}"))
    .willSetStateTo("processed"));

stubFor(get("/status")
    .inScenario("Order")
    .whenScenarioStateIs("processed")
    .willReturn(aResponse().withBody("{\"state\":\"done\"}")));
```

## JSON aus Datei

```
src/test/resources/__files/response.json
```

```java
.willReturn(aResponse()
    .withBodyFile("response.json"));
```
