# ArchUnit - Architektur-Tests

## Was ist ArchUnit?

Ein Framework zum **Testen von Architekturregeln** als Unit-Tests. Prueft Abhaengigkeiten, Namenskonventionen und Schichten.

## Warum Architektur testen?

```
┌───────────────────────────────────────────┐
│  Architektur-Dokumentation                │
│  "Controller duerfen nicht auf            │
│   Repository zugreifen"                   │
└───────────────────────────────────────────┘
                    │
                    ▼  Wird das eingehalten?
┌───────────────────────────────────────────┐
│  ArchUnit Test                            │
│  noClasses().that().resideIn("controller")│
│      .should().dependOn("repository")     │
└───────────────────────────────────────────┘
```

## Setup im Playground

```xml
<!-- pom.xml -->
<dependency>
    <groupId>com.tngtech.archunit</groupId>
    <artifactId>archunit-junit5</artifactId>
    <version>1.3.0</version>
    <scope>test</scope>
</dependency>
```

```java
// src/test/java/.../unit/architecture/ArchitectureTest.java

@DisplayName("Architektur-Tests")
class ArchitectureTest {

    private static JavaClasses classes;

    @BeforeAll
    static void setup() {
        classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("de.training.playground");
    }
}
```

**Was wurde gemacht:** Alle Produktionsklassen importieren, Tests ausschliessen.

**Warum:** Architektur automatisch bei jedem Build pruefen.

## Schichten-Architektur

```java
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

        .check(classes);
}
```

**Was wurde gemacht:** Schichten und erlaubte Zugriffe definiert.

**Warum:** Verhindert Spaghetti-Code und zirkulaere Abhaengigkeiten.

## Namenskonventionen

```java
@Test
@DisplayName("Controller enden auf 'Controller'")
void controllerNaming() {
    classes()
        .that().resideInAPackage("..controller..")
        .should().haveSimpleNameEndingWith("Controller")
        .check(classes);
}

@Test
@DisplayName("Services enden auf 'Service'")
void serviceNaming() {
    classes()
        .that().resideInAPackage("..service..")
        .and().areNotInterfaces()
        .should().haveSimpleNameEndingWith("Service")
        .check(classes);
}

@Test
@DisplayName("Repositories enden auf 'Repository'")
void repositoryNaming() {
    classes()
        .that().resideInAPackage("..repository..")
        .should().haveSimpleNameEndingWith("Repository")
        .check(classes);
}
```

**Was wurde gemacht:** Namenskonventionen als Tests.

**Warum:** Konsistente Benennung im gesamten Projekt.

## Annotationen pruefen

```java
@Test
@DisplayName("Controller sind annotiert")
void controllersAreAnnotated() {
    classes()
        .that().haveSimpleNameEndingWith("Controller")
        .should().beAnnotatedWith(RestController.class)
        .orShould().beAnnotatedWith(Controller.class)
        .check(classes);
}

@Test
@DisplayName("Services sind mit @Service annotiert")
void servicesAreAnnotated() {
    classes()
        .that().resideInAPackage("..service..")
        .and().areNotInterfaces()
        .should().beAnnotatedWith(Service.class)
        .check(classes);
}
```

**Was wurde gemacht:** Spring-Annotationen erzwingen.

**Warum:** Vergessene Annotationen frueh erkennen.

## Abhaengigkeits-Regeln

```java
@Test
@DisplayName("Services greifen nicht auf Controller zu")
void noCycles() {
    noClasses()
        .that().resideInAPackage("..service..")
        .should().dependOnClassesThat().resideInAPackage("..controller..")
        .check(classes);
}

@Test
@DisplayName("Entities greifen nicht auf Services zu")
void entitiesDontAccessServices() {
    noClasses()
        .that().resideInAPackage("..entity..")
        .should().dependOnClassesThat().resideInAPackage("..service..")
        .check(classes);
}
```

**Was wurde gemacht:** Verbotene Abhaengigkeiten definiert.

**Warum:** Entities sollen unabhaengig von Infrastruktur sein.

## Feld-Regeln

```java
@Test
@DisplayName("Keine oeffentlichen Felder in Entities")
void noPublicFieldsInEntities() {
    fields()
        .that().areDeclaredInClassesThat().resideInAPackage("..entity..")
        .and().areNotStatic()
        .should().notBePublic()
        .check(classes);
}
```

**Was wurde gemacht:** Kapselung in Entities erzwingen.

**Warum:** Zugriff nur ueber Getter/Setter.

## Konstruktor-Injection

```java
@Test
@DisplayName("Services verwenden Konstruktor-Injection")
void servicesUseConstructorInjection() {
    noFields()
        .that().areDeclaredInClassesThat().resideInAPackage("..service..")
        .should().beAnnotatedWith(Autowired.class)
        .check(classes);
}
```

**Was wurde gemacht:** Feld-Injection verbieten.

**Warum:** Konstruktor-Injection ist testbarer und expliziter.

## Typische Regeln

### Keine Zyklen zwischen Packages

```java
@Test
void noCyclicDependencies() {
    slices().matching("de.training.playground.(*)..")
        .should().beFreeOfCycles()
        .check(classes);
}
```

### Bestimmte Bibliotheken verbieten

```java
@Test
void noJodaTime() {
    noClasses()
        .should().dependOnClassesThat()
        .resideInAPackage("org.joda.time..")
        .check(classes);
}
```

### Interface-Implementierung

```java
@Test
void repositoriesExtendJpaRepository() {
    classes()
        .that().haveSimpleNameEndingWith("Repository")
        .should().beInterfaces()
        .andShould().beAssignableTo(JpaRepository.class)
        .check(classes);
}
```

## Fehler-Ausgabe

```
java.lang.AssertionError: Architecture Violation:
Rule 'classes that reside in a package '..controller..'
should have simple name ending with 'Controller'' was violated (1 times):

Class <de.training.playground.controller.TodoHelper>
does not have simple name ending with 'Controller'
```

## Best Practices

1. **Frueh einfuehren** - bei Projektstart, nicht nachtraeglich
2. **Regeln dokumentieren** - mit @DisplayName
3. **Ausnahmen explizit** - nicht einfach ignorieren
4. **In CI/CD** - Architektur bei jedem Build pruefen
5. **Team-Konsens** - Regeln gemeinsam definieren
