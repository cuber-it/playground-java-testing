package de.training.playground.rest.scenarios;

import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * REST API Bulk Operations Test.
 *
 * <p>Testet das Verhalten der API bei mehreren Operationen:
 * <ul>
 *   <li>Mehrere Todos erstellen</li>
 *   <li>Filtern und Suchen</li>
 *   <li>Bulk-Loeschung</li>
 * </ul>
 */
@Epic("REST Tests")
@Feature("scenarios")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TodoApiBulkOperationsTest {

    @LocalServerPort
    private int port;

    private static List<Long> createdIds = new ArrayList<>();

    @BeforeEach
    void setup() {
        RestAssured.port = port;
        RestAssured.basePath = "/api/todos";
    }

    @Test
    @Order(1)
    @Severity(SeverityLevel.NORMAL)
    @Description("Erstelle 5 Todos nacheinander")
    void createMultipleTodos() {
        for (int i = 1; i <= 5; i++) {
            Long id = given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                    {
                        "title": "Bulk Todo %d",
                        "description": "Beschreibung %d"
                    }
                    """, i, i))
            .when()
                .post()
            .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getLong("id");

            createdIds.add(id);
        }

        Allure.step("5 Todos erstellt: " + createdIds);
    }

    @Test
    @Order(2)
    @Severity(SeverityLevel.NORMAL)
    @Description("Alle Todos abrufen - mindestens 5 vorhanden")
    void getAllTodos_atLeast5() {
        given()
        .when()
            .get()
        .then()
            .statusCode(200)
            .body("size()", greaterThanOrEqualTo(5));
    }

    @Test
    @Order(3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Markiere alle Bulk-Todos als erledigt")
    void markAllBulkTodosAsDone() {
        for (Long id : createdIds) {
            given()
                .pathParam("id", id)
                .contentType(ContentType.JSON)
                .body(String.format("""
                    {
                        "title": "Bulk Todo",
                        "done": true
                    }
                    """))
            .when()
                .put("/{id}")
            .then()
                .statusCode(200)
                .body("done", equalTo(true));
        }

        Allure.step("Alle 5 Todos als erledigt markiert");
    }

    @Test
    @Order(4)
    @Severity(SeverityLevel.NORMAL)
    @Description("Loesche alle erstellten Todos")
    void deleteAllCreatedTodos() {
        for (Long id : createdIds) {
            given()
                .pathParam("id", id)
            .when()
                .delete("/{id}")
            .then()
                .statusCode(204);
        }

        Allure.step("Alle 5 Todos geloescht");
        createdIds.clear();
    }
}
