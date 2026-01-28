package de.training.playground.rest.journey;

import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * REST API User Journey Test.
 *
 * <p>Simuliert einen kompletten Benutzer-Workflow ueber die REST API:
 * <ol>
 *   <li>Neues Todo erstellen</li>
 *   <li>Todo abrufen und pruefen</li>
 *   <li>Todo als erledigt markieren</li>
 *   <li>Alle Todos auflisten</li>
 *   <li>Todo loeschen</li>
 *   <li>Pruefen dass Todo weg ist</li>
 * </ol>
 */
@Epic("REST Tests")
@Feature("journey")
@Story("Kompletter Todo-Workflow")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TodoApiJourneyTest {

    @LocalServerPort
    private int port;

    private static Long createdTodoId;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
        RestAssured.basePath = "/api/todos";
    }

    @Test
    @Order(1)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Erstelle ein neues Todo ueber die API")
    void step1_createTodo() {
        createdTodoId = given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "title": "REST Journey Test",
                    "description": "Automatisch erstelltes Todo"
                }
                """)
        .when()
            .post()
        .then()
            .statusCode(200)
            .body("title", equalTo("REST Journey Test"))
            .body("done", equalTo(false))
            .extract()
            .jsonPath()
            .getLong("id");

        Allure.step("Todo erstellt mit ID: " + createdTodoId);
    }

    @Test
    @Order(2)
    @Severity(SeverityLevel.NORMAL)
    @Description("Rufe das erstellte Todo ab und pruefe die Daten")
    void step2_getTodo() {
        given()
            .pathParam("id", createdTodoId)
        .when()
            .get("/{id}")
        .then()
            .statusCode(200)
            .body("id", equalTo(createdTodoId.intValue()))
            .body("title", equalTo("REST Journey Test"))
            .body("done", equalTo(false));
    }

    @Test
    @Order(3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Markiere das Todo als erledigt")
    void step3_markTodoAsDone() {
        given()
            .pathParam("id", createdTodoId)
            .contentType(ContentType.JSON)
            .body("""
                {
                    "title": "REST Journey Test",
                    "description": "Automatisch erstelltes Todo",
                    "done": true
                }
                """)
        .when()
            .put("/{id}")
        .then()
            .statusCode(200)
            .body("done", equalTo(true));
    }

    @Test
    @Order(4)
    @Severity(SeverityLevel.NORMAL)
    @Description("Liste alle Todos auf - unser Todo muss dabei sein")
    void step4_listAllTodos() {
        given()
        .when()
            .get()
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("find { it.id == " + createdTodoId + " }.done", equalTo(true));
    }

    @Test
    @Order(5)
    @Severity(SeverityLevel.NORMAL)
    @Description("Loesche das Todo")
    void step5_deleteTodo() {
        given()
            .pathParam("id", createdTodoId)
        .when()
            .delete("/{id}")
        .then()
            .statusCode(204);
    }

    @Test
    @Order(6)
    @Severity(SeverityLevel.NORMAL)
    @Description("Pruefe dass das Todo nicht mehr existiert")
    void step6_verifyTodoDeleted() {
        given()
            .pathParam("id", createdTodoId)
        .when()
            .get("/{id}")
        .then()
            .statusCode(404);
    }
}
