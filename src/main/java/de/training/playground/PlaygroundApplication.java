package de.training.playground;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hauptklasse der Playground-Anwendung.
 *
 * <p>Eine einfache Todo-Anwendung zum Erlernen von Testtechniken.
 *
 * @author Training Team
 * @version 1.0.0
 */
@SpringBootApplication
public class PlaygroundApplication {
    public static void main(String[] args) {
        SpringApplication.run(PlaygroundApplication.class, args);
    }
}
