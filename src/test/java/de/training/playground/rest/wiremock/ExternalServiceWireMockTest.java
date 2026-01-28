package de.training.playground.rest.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * WireMock-Tests fuer das Mocken externer HTTP-Services.
 *
 * <p>WireMock startet einen lokalen HTTP-Server, der konfigurierbare
 * Antworten liefert. Ideal fuer Tests, die von externen APIs abhaengen.
 *
 * <p>Szenario: Ein externer Benachrichtigungsservice wird simuliert.
 *
 * <p>Demonstriert:
 * <ul>
 *   <li>{@code WireMockServer} - embedded HTTP-Server</li>
 *   <li>{@code stubFor()} - Antworten definieren (Stubbing)</li>
 *   <li>{@code verify()} - Aufrufe pruefen (Verification)</li>
 *   <li>URL-Matching (exakt, Pattern, Path-Parameter)</li>
 *   <li>Request-Body und Header Matching</li>
 *   <li>Verzoegerte Antworten ({@code withFixedDelay})</li>
 *   <li>Fehler-Szenarien (500 Internal Server Error)</li>
 *   <li>Query-Parameter Matching</li>
 * </ul>
 *
 * <p><b>Typische Anwendungsfaelle:</b>
 * <ul>
 *   <li>Externe APIs mocken (Payment, Notification, etc.)</li>
 *   <li>Fehlerszenarien simulieren (Timeouts, 500er)</li>
 *   <li>Unabhaengig von externen Services testen</li>
 * </ul>
 *
 * @see WireMockServer
 * @see WireMock
 */
@Epic("REST Tests")
@Feature("wiremock")
@DisplayName("WireMock Tests")
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
        wireMockServer.resetAll();
        restClient = RestClient.builder()
            .baseUrl("http://localhost:8089")
            .build();
    }

    @Test
    @DisplayName("Erfolgreiche Benachrichtigung senden")
    void sendNotification_success() {
        // WireMock stubben
        stubFor(post(urlEqualTo("/notifications"))
            .withHeader("Content-Type", containing("application/json"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"status\": \"sent\", \"id\": \"12345\"}")));

        // Request ausfuehren
        ResponseEntity<String> response = restClient.post()
            .uri("/notifications")
            .header("Content-Type", "application/json")
            .body("{\"message\": \"Todo erledigt!\"}")
            .retrieve()
            .toEntity(String.class);

        // Pruefen
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("sent");
        assertThat(response.getBody()).contains("12345");

        // Verify WireMock wurde aufgerufen
        verify(postRequestedFor(urlEqualTo("/notifications"))
            .withRequestBody(containing("Todo erledigt")));
    }

    @Test
    @DisplayName("Externer Service antwortet mit Fehler")
    void sendNotification_serverError() {
        stubFor(post(urlEqualTo("/notifications"))
            .willReturn(aResponse()
                .withStatus(500)
                .withBody("{\"error\": \"Internal Server Error\"}")));

        assertThatThrownBy(() ->
            restClient.post()
                .uri("/notifications")
                .header("Content-Type", "application/json")
                .body("{\"message\": \"Test\"}")
                .retrieve()
                .toEntity(String.class)
        ).isInstanceOf(RestClientException.class);
    }

    @Test
    @DisplayName("GET Request an externen Service")
    void getStatus() {
        stubFor(get(urlEqualTo("/status"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"service\": \"notification\", \"healthy\": true}")));

        ResponseEntity<String> response = restClient.get()
            .uri("/status")
            .retrieve()
            .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("healthy");
    }

    @Test
    @DisplayName("Request mit Path-Parameter")
    void getNotificationById() {
        stubFor(get(urlPathMatching("/notifications/\\d+"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"id\": \"123\", \"status\": \"delivered\"}")));

        ResponseEntity<String> response = restClient.get()
            .uri("/notifications/123")
            .retrieve()
            .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("delivered");
    }

    @Test
    @DisplayName("Verzoegerte Antwort simulieren")
    void slowResponse() {
        stubFor(get(urlEqualTo("/slow"))
            .willReturn(aResponse()
                .withStatus(200)
                .withFixedDelay(100) // 100ms Verzoegerung
                .withBody("{\"result\": \"slow\"}")));

        long start = System.currentTimeMillis();

        ResponseEntity<String> response = restClient.get()
            .uri("/slow")
            .retrieve()
            .toEntity(String.class);

        long duration = System.currentTimeMillis() - start;

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(duration).isGreaterThanOrEqualTo(100);
    }

    @Test
    @DisplayName("Request-Matching mit Query-Parameter")
    void requestWithQueryParam() {
        stubFor(get(urlPathEqualTo("/search"))
            .withQueryParam("q", equalTo("todo"))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("{\"results\": [\"todo1\", \"todo2\"]}")));

        ResponseEntity<String> response = restClient.get()
            .uri("/search?q=todo")
            .retrieve()
            .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("todo1");
    }

    @Test
    @DisplayName("Mehrfache Aufrufe pruefen")
    void verifyMultipleCalls() {
        stubFor(post(urlEqualTo("/log"))
            .willReturn(aResponse().withStatus(200)));

        // 3 Aufrufe
        for (int i = 0; i < 3; i++) {
            restClient.post()
                .uri("/log")
                .body("{\"event\": \"test\"}")
                .retrieve()
                .toBodilessEntity();
        }

        verify(exactly(3), postRequestedFor(urlEqualTo("/log")));
    }
}
