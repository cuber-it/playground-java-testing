package de.training.playground.experiments.snippets;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Snippet-Vorlage fuer Experimente.
 */
@Epic("Experiments")
@Feature("snippets")
@DisplayName("Experiment Snippets")
class SnippetTest {

    @Test
    @DisplayName("Snippet Template - bereit zum Ausfuellen")
    @Disabled("Template - aktivieren und anpassen")
    void snippetTemplate() {
        assertTrue(true);
    }
}
