package de.training.playground.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * Architektur-Tests mit ArchUnit.
 *
 * <p>ArchUnit ermoeglicht es, Architekturregeln als Unit-Tests zu formulieren.
 * Diese Tests werden bei jedem Build ausgefuehrt und verhindern
 * Architekturverletzungen.
 *
 * <p>Demonstriert:
 * <ul>
 *   <li><b>Schichten-Architektur</b> - Controller → Service → Repository → Entity</li>
 *   <li><b>Namenskonventionen</b> - Klassen enden auf Controller, Service, Repository</li>
 *   <li><b>Annotationsregeln</b> - Controller sind mit @Controller annotiert</li>
 *   <li><b>Dependency-Regeln</b> - Keine Zyklen, keine verbotenen Zugriffe</li>
 *   <li><b>Feld-Regeln</b> - Keine oeffentlichen Felder in Entities</li>
 *   <li><b>Injection-Regeln</b> - Konstruktor-Injection bevorzugen</li>
 * </ul>
 *
 * <p>Die Tests importieren alle Klassen aus dem Produktionscode
 * (ohne Tests) und pruefen sie gegen die definierten Regeln.
 *
 * @see com.tngtech.archunit.lang.syntax.ArchRuleDefinition
 * @see com.tngtech.archunit.library.Architectures
 */
@Epic("Unit Tests")
@Feature("architecture")
@DisplayName("Architektur-Tests")
class ArchitectureTest {

    private static JavaClasses classes;

    @BeforeAll
    static void setup() {
        classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("de.training.playground");
    }

    // === Schichten-Architektur ===

    @Test
    @DisplayName("Schichten-Architektur wird eingehalten")
    void layeredArchitectureIsRespected() {
        layeredArchitecture()
            .consideringAllDependencies()
            .layer("Controller").definedBy("..controller..")
            .layer("Service").definedBy("..service..")
            .layer("Repository").definedBy("..repository..")
            .layer("Entity").definedBy("..entity..")

            .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
            .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller")
            .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service")
            .whereLayer("Entity").mayOnlyBeAccessedByLayers("Controller", "Service", "Repository")

            .check(classes);
    }

    // === Namenskonventionen ===

    @Test
    @DisplayName("Controller-Klassen enden auf 'Controller'")
    void controllerNaming() {
        classes()
            .that().resideInAPackage("..controller..")
            .should().haveSimpleNameEndingWith("Controller")
            .check(classes);
    }

    @Test
    @DisplayName("Service-Klassen enden auf 'Service'")
    void serviceNaming() {
        classes()
            .that().resideInAPackage("..service..")
            .and().areNotInterfaces()
            .should().haveSimpleNameEndingWith("Service")
            .check(classes);
    }

    @Test
    @DisplayName("Repository-Interfaces enden auf 'Repository'")
    void repositoryNaming() {
        classes()
            .that().resideInAPackage("..repository..")
            .should().haveSimpleNameEndingWith("Repository")
            .check(classes);
    }

    // === Annotationen ===

    @Test
    @DisplayName("Controller sind mit @Controller oder @RestController annotiert")
    void controllersAreAnnotated() {
        classes()
            .that().haveSimpleNameEndingWith("Controller")
            .should().beAnnotatedWith(org.springframework.stereotype.Controller.class)
            .orShould().beAnnotatedWith(org.springframework.web.bind.annotation.RestController.class)
            .check(classes);
    }

    @Test
    @DisplayName("Services sind mit @Service annotiert")
    void servicesAreAnnotated() {
        classes()
            .that().resideInAPackage("..service..")
            .and().areNotInterfaces()
            .should().beAnnotatedWith(org.springframework.stereotype.Service.class)
            .check(classes);
    }

    // === Dependency-Regeln ===

    @Test
    @DisplayName("Keine Zyklen zwischen Packages")
    void noCycles() {
        noClasses()
            .that().resideInAPackage("..service..")
            .should().dependOnClassesThat().resideInAPackage("..controller..")
            .check(classes);
    }

    @Test
    @DisplayName("Entities haben keinen Zugriff auf Services")
    void entitiesDontAccessServices() {
        noClasses()
            .that().resideInAPackage("..entity..")
            .should().dependOnClassesThat().resideInAPackage("..service..")
            .check(classes);
    }

    // === Feld-Regeln ===

    @Test
    @DisplayName("Keine oeffentlichen Felder in Entities (ausser statische)")
    void noPublicFieldsInEntities() {
        fields()
            .that().areDeclaredInClassesThat().resideInAPackage("..entity..")
            .and().areNotStatic()
            .should().notBePublic()
            .check(classes);
    }

    // === Konstruktor-Injection ===

    @Test
    @DisplayName("Services verwenden Konstruktor-Injection")
    void servicesUseConstructorInjection() {
        noFields()
            .that().areDeclaredInClassesThat().resideInAPackage("..service..")
            .should().beAnnotatedWith(org.springframework.beans.factory.annotation.Autowired.class)
            .check(classes);
    }
}
