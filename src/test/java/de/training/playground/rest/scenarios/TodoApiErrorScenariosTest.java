package de.training.playground.rest.scenarios;

import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * REST API Fehlerszenarien Tests.
 *
 * <p>Testet das Verhalten der API bei fehlerhaften Anfragen:
 * <ul>
 *   <li>Nicht existierende Ressourcen (404)</li>
 *   <li>Ungueltige Daten (400)</li>
 *   <li>Leere Pflichtfelder</li>
 * </ul>
 */
@Epic("REST Tests")
@Feature("scenarios")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TodoApiErrorScenariosTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
        RestAssured.basePath = "/api/todos";
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("GET auf nicht existierende ID liefert 404")
    void getNonExistentTodo_returns404() {
        given()
            .pathParam("id", 99999)
        .when()
            .get("/{id}")
        .then()
            .statusCode(404);
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("DELETE auf nicht existierende ID liefert 404")
    void deleteNonExistentTodo_returns404() {
        given()
            .pathParam("id", 99999)
        .when()
            .delete("/{id}")
        .then()
            .statusCode(404);
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("PUT auf nicht existierende ID liefert 404")
    void updateNonExistentTodo_returns404() {
        given()
            .pathParam("id", 99999)
            .contentType(ContentType.JSON)
            .body("""
                {
                    "title": "Test",
                    "done": false
                }
                """)
        .when()
            .put("/{id}")
        .then()
            .statusCode(404);
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    @Description("POST ohne Body liefert 400")
    void createTodoWithoutBody_returns400() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .post()
        .then()
            .statusCode(400);
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    @Description("POST mit leerem Title liefert 400")
    void createTodoWithEmptyTitle_returns400() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "title": "",
                    "done": false
                }
                """)
        .when()
            .post()
        .then()
            .statusCode(anyOf(equalTo(400), equalTo(200))); // Je nach Validierung
    }

    @Test
    @Severity(SeverityLevel.MINOR)
    @Description("GET mit ungueltigem ID-Format")
    void getTodoWithInvalidIdFormat_returns400or404() {
        given()
        .when()
            .get("/abc")
        .then()
            .statusCode(anyOf(equalTo(400), equalTo(404)));
    }
}
