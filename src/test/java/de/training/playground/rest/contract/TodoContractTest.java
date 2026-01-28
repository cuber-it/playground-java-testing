package de.training.playground.rest.contract;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Consumer-Driven Contract Tests mit Pact.
 *
 * <p>Pact ermoeglicht Contract Testing zwischen Consumer und Provider.
 * Der Consumer definiert seine Erwartungen, die der Provider erfuellen muss.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Consumer definiert Pacts (diese Klasse)</li>
 *   <li>Tests generieren Pact-Files in {@code target/pacts/}</li>
 *   <li>Provider verifiziert Pact-Files gegen seine Implementierung</li>
 * </ol>
 *
 * <p>Demonstriert:
 * <ul>
 *   <li>{@code @Pact} - Vertrag definieren</li>
 *   <li>{@code PactDslJsonBody} - JSON-Response-Struktur beschreiben</li>
 *   <li>{@code @PactTestFor} - Test mit Pact verknuepfen</li>
 *   <li>{@code MockServer} - automatisch gestarteter Mock</li>
 *   <li>Verschiedene HTTP-Methoden (GET, POST, DELETE)</li>
 *   <li>Erfolgs- und Fehlerfaelle (200, 204, 404)</li>
 * </ul>
 *
 * <p><b>Generierte Pact-Files:</b> {@code target/pacts/TodoConsumer-TodoProvider.json}
 *
 * @see Pact
 * @see PactConsumerTestExt
 * @see PactTestFor
 */
@Epic("REST Tests")
@Feature("contract")
@ExtendWith(PactConsumerTestExt.class)
@DisplayName("Todo API Contract Tests")
class TodoContractTest {

    @Pact(consumer = "TodoConsumer", provider = "TodoProvider")
    public V4Pact getAllTodosPact(PactDslWithProvider builder) {
        return builder
            .given("Es existieren Todos")
            .uponReceiving("Request fuer alle Todos")
                .path("/api/todos")
                .method("GET")
            .willRespondWith()
                .status(200)
                .headers(java.util.Map.of("Content-Type", "application/json"))
                .body(new PactDslJsonBody()
                    .minArrayLike("", 1)
                        .integerType("id", 1)
                        .stringType("title", "Beispiel Todo")
                        .stringType("description", "Eine Beschreibung")
                        .booleanType("done", false)
                    .closeArray())
            .toPact(V4Pact.class);
    }

    @Pact(consumer = "TodoConsumer", provider = "TodoProvider")
    public V4Pact getTodoByIdPact(PactDslWithProvider builder) {
        return builder
            .given("Todo mit ID 1 existiert")
            .uponReceiving("Request fuer Todo mit ID 1")
                .path("/api/todos/1")
                .method("GET")
            .willRespondWith()
                .status(200)
                .headers(java.util.Map.of("Content-Type", "application/json"))
                .body(new PactDslJsonBody()
                    .integerType("id", 1)
                    .stringType("title", "Mein Todo")
                    .stringType("description", "Beschreibung")
                    .booleanType("done", false))
            .toPact(V4Pact.class);
    }

    @Pact(consumer = "TodoConsumer", provider = "TodoProvider")
    public V4Pact createTodoPact(PactDslWithProvider builder) {
        return builder
            .given("System ist bereit")
            .uponReceiving("Request zum Erstellen eines Todos")
                .path("/api/todos")
                .method("POST")
                .headers(java.util.Map.of("Content-Type", "application/json"))
                .body(new PactDslJsonBody()
                    .stringType("title", "Neues Todo")
                    .stringType("description", "Via Contract erstellt"))
            .willRespondWith()
                .status(200)
                .headers(java.util.Map.of("Content-Type", "application/json"))
                .body(new PactDslJsonBody()
                    .integerType("id")
                    .stringType("title", "Neues Todo")
                    .stringType("description", "Via Contract erstellt")
                    .booleanType("done", false))
            .toPact(V4Pact.class);
    }

    @Pact(consumer = "TodoConsumer", provider = "TodoProvider")
    public V4Pact deleteTodoPact(PactDslWithProvider builder) {
        return builder
            .given("Todo mit ID 1 existiert")
            .uponReceiving("Request zum Loeschen von Todo 1")
                .path("/api/todos/1")
                .method("DELETE")
            .willRespondWith()
                .status(204)
            .toPact(V4Pact.class);
    }

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

    // === Tests ===

    @Test
    @PactTestFor(pactMethod = "getAllTodosPact")
    @DisplayName("Contract: Alle Todos abrufen")
    void testGetAllTodos(MockServer mockServer) {
        RestClient client = RestClient.builder()
            .baseUrl(mockServer.getUrl())
            .build();

        ResponseEntity<String> response = client.get()
            .uri("/api/todos")
            .retrieve()
            .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("title");
    }

    @Test
    @PactTestFor(pactMethod = "getTodoByIdPact")
    @DisplayName("Contract: Einzelnes Todo abrufen")
    void testGetTodoById(MockServer mockServer) {
        RestClient client = RestClient.builder()
            .baseUrl(mockServer.getUrl())
            .build();

        ResponseEntity<String> response = client.get()
            .uri("/api/todos/1")
            .retrieve()
            .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Mein Todo");
    }

    @Test
    @PactTestFor(pactMethod = "createTodoPact")
    @DisplayName("Contract: Todo erstellen")
    void testCreateTodo(MockServer mockServer) {
        RestClient client = RestClient.builder()
            .baseUrl(mockServer.getUrl())
            .build();

        ResponseEntity<String> response = client.post()
            .uri("/api/todos")
            .header("Content-Type", "application/json")
            .body("{\"title\": \"Neues Todo\", \"description\": \"Via Contract erstellt\"}")
            .retrieve()
            .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Neues Todo");
    }

    @Test
    @PactTestFor(pactMethod = "deleteTodoPact")
    @DisplayName("Contract: Todo loeschen")
    void testDeleteTodo(MockServer mockServer) {
        RestClient client = RestClient.builder()
            .baseUrl(mockServer.getUrl())
            .build();

        ResponseEntity<Void> response = client.delete()
            .uri("/api/todos/1")
            .retrieve()
            .toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @PactTestFor(pactMethod = "todoNotFoundPact")
    @DisplayName("Contract: Todo nicht gefunden")
    void testTodoNotFound(MockServer mockServer) {
        RestClient client = RestClient.builder()
            .baseUrl(mockServer.getUrl())
            .build();

        try {
            client.get()
                .uri("/api/todos/999")
                .retrieve()
                .toEntity(String.class);
        } catch (Exception e) {
            // 404 erwartet - RestClient wirft Exception bei 4xx
            assertThat(e.getMessage()).contains("404");
        }
    }
}
