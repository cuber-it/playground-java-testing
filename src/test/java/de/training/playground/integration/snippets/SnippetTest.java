package de.training.playground.integration.snippets;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Snippet-Vorlage fuer Integration Tests.
 */
@Epic("Integration Tests")
@Feature("snippets")
@DisplayName("Integration Snippets")
@SpringBootTest
class SnippetTest {

    @Test
    @DisplayName("Snippet Template - bereit zum Ausfuellen")
    @Disabled("Template - aktivieren und anpassen")
    void snippetTemplate() {
        assertTrue(true);
    }
}
