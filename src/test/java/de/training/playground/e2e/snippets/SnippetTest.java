package de.training.playground.e2e.snippets;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Snippet-Vorlage fuer E2E Tests.
 */
@Epic("E2E Tests")
@Feature("snippets")
@DisplayName("E2E Snippets")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SnippetTest {

    @Test
    @DisplayName("Snippet Template - bereit zum Ausfuellen")
    @Disabled("Template - aktivieren und anpassen")
    void snippetTemplate() {
        assertTrue(true);
    }
}
