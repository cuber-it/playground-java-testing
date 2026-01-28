package de.training.playground.rest.restassured;

import de.training.playground.entity.Todo;
import de.training.playground.repository.TodoRepository;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * REST-API Tests mit RestAssured.
 *
 * <p>RestAssured bietet eine fluente BDD-Syntax (Given-When-Then)
 * fuer HTTP-Tests gegen echte Server.
 *
 * <p>Demonstriert:
 * <ul>
 *   <li>{@code given().when().then()} - BDD-Syntax</li>
 *   <li>{@code RANDOM_PORT} - echter HTTP-Server auf zufaelligem Port</li>
 *   <li>{@code @LocalServerPort} - Port in Tests injizieren</li>
 *   <li>Response Body Assertions mit Hamcrest Matchers</li>
 *   <li>Response Time Assertions</li>
 *   <li>Content-Type Pruefung</li>
 *   <li>Path-Parameter in URIs</li>
 * </ul>
 *
 * <p><b>Unterschied zu MockMvc:</b> RestAssured testet gegen echten HTTP-Server,
 * ist langsamer, aber realitaetsnaeher (voller HTTP-Stack).
 *
 * @see RestAssured
 * @see SpringBootTest.WebEnvironment#RANDOM_PORT
 */
@Epic("REST Tests")
@Feature("restassured")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("RestAssured Tests")
class TodoRestAssuredTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TodoRepository repository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api";
        repository.deleteAll();
    }

    @Test
    @DisplayName("GET /api/todos gibt leere Liste")
    void getAllTodos_empty() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/todos")
        .then()
            .statusCode(200)
            .body("$", hasSize(0));
    }

    @Test
    @DisplayName("GET /api/todos gibt alle Todos")
    void getAllTodos() {
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
    @DisplayName("POST /api/todos erstellt neues Todo")
    void createTodo() {
        given()
            .contentType(ContentType.JSON)
            .body("{\"title\": \"RestAssured Test\", \"description\": \"Via RestAssured erstellt\"}")
        .when()
            .post("/todos")
        .then()
            .statusCode(200)
            .body("id", notNullValue())
            .body("title", equalTo("RestAssured Test"))
            .body("description", equalTo("Via RestAssured erstellt"))
            .body("done", equalTo(false));
    }

    @Test
    @DisplayName("GET /api/todos/{id} gibt einzelnes Todo")
    void getTodoById() {
        Todo saved = repository.save(createTodo("Find Me"));

        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/todos/{id}", saved.getId())
        .then()
            .statusCode(200)
            .body("title", equalTo("Find Me"));
    }

    @Test
    @DisplayName("GET /api/todos/{id} mit unbekannter ID gibt 404")
    void getTodoById_notFound() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/todos/{id}", 9999)
        .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("PUT /api/todos/{id} aktualisiert Todo")
    void updateTodo() {
        Todo saved = repository.save(createTodo("Original"));

        given()
            .contentType(ContentType.JSON)
            .body("{\"title\": \"Updated\", \"description\": \"Geaendert\"}")
        .when()
            .put("/todos/{id}", saved.getId())
        .then()
            .statusCode(200)
            .body("title", equalTo("Updated"))
            .body("description", equalTo("Geaendert"));
    }

    @Test
    @DisplayName("DELETE /api/todos/{id} loescht Todo")
    void deleteTodo() {
        Todo saved = repository.save(createTodo("Delete Me"));

        given()
            .contentType(ContentType.JSON)
        .when()
            .delete("/todos/{id}", saved.getId())
        .then()
            .statusCode(204);

        // Verify deleted
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/todos/{id}", saved.getId())
        .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("Response-Zeit unter 500ms")
    void responseTime() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/todos")
        .then()
            .time(lessThan(500L));
    }

    @Test
    @DisplayName("Content-Type ist application/json")
    void contentType() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/todos")
        .then()
            .contentType(ContentType.JSON);
    }

    private Todo createTodo(String title) {
        Todo todo = new Todo();
        todo.setTitle(title);
        return todo;
    }
}
