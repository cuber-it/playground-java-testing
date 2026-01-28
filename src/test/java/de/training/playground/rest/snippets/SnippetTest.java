package de.training.playground.rest.snippets;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Snippet-Vorlage fuer REST Tests.
 */
@Epic("REST Tests")
@Feature("snippets")
@DisplayName("REST Snippets")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SnippetTest {

    @Test
    @DisplayName("Snippet Template - bereit zum Ausfuellen")
    @Disabled("Template - aktivieren und anpassen")
    void snippetTemplate() {
        assertTrue(true);
    }
}
