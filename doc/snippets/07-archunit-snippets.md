# ArchUnit Snippets

Copy-Paste fertige Code-Blöcke.

---

## Test-Klasse Grundgerüst

```java
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.*;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.*;

class ArchitectureTest {

    static JavaClasses classes;

    @BeforeAll
    static void setUp() {
        classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("de.training.myapp");
    }

    @Test
    void layeredArchitectureIsRespected() {
        // ...
    }
}
```

---

## Mit JUnit 5 Extension (Alternative)

```java
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "de.training.myapp")
class ArchitectureTest {

    @ArchTest
    static final ArchRule layerRule = layeredArchitecture()
        .consideringAllDependencies()
        .layer("Controller").definedBy("..controller..")
        .layer("Service").definedBy("..service..")
        .layer("Repository").definedBy("..repository..")
        .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
        .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller")
        .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service");
}
```

---

## Schichten-Architektur

```java
@Test
void layeredArchitectureIsRespected() {
    layeredArchitecture()
        .consideringAllDependencies()

        .layer("Controller").definedBy("..controller..")
        .layer("Service").definedBy("..service..")
        .layer("Repository").definedBy("..repository..")
        .layer("Domain").definedBy("..domain..", "..model..")

        .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
        .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller")
        .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service")
        .whereLayer("Domain").mayOnlyBeAccessedByLayers("Service", "Repository", "Controller")

        .check(classes);
}
```

---

## Hexagonale Architektur (Ports & Adapters)

```java
@Test
void onionArchitectureIsRespected() {
    onionArchitecture()
        .domainModels("..domain.model..")
        .domainServices("..domain.service..")
        .applicationServices("..application..")
        .adapter("persistence", "..adapter.persistence..")
        .adapter("web", "..adapter.web..")
        .adapter("messaging", "..adapter.messaging..")

        .check(classes);
}
```

---

## Package-Abhängigkeiten

```java
@Test
void servicesShouldNotDependOnControllers() {
    noClasses()
        .that().resideInAPackage("..service..")
        .should().dependOnClassesThat().resideInAPackage("..controller..")
        .check(classes);
}

@Test
void repositoriesShouldOnlyBeAccessedByServices() {
    classes()
        .that().resideInAPackage("..repository..")
        .should().onlyBeAccessed().byAnyPackage("..service..", "..repository..")
        .check(classes);
}

@Test
void domainShouldNotDependOnSpring() {
    noClasses()
        .that().resideInAPackage("..domain..")
        .should().dependOnClassesThat()
            .resideInAnyPackage("org.springframework..")
        .check(classes);
}
```

---

## Naming Conventions

```java
@Test
void controllersShouldEndWithController() {
    classes()
        .that().resideInAPackage("..controller..")
        .should().haveSimpleNameEndingWith("Controller")
        .check(classes);
}

@Test
void servicesShouldEndWithService() {
    classes()
        .that().resideInAPackage("..service..")
        .and().areNotInterfaces()
        .should().haveSimpleNameEndingWith("Service")
        .orShould().haveSimpleNameEndingWith("ServiceImpl")
        .check(classes);
}

@Test
void repositoriesShouldEndWithRepository() {
    classes()
        .that().resideInAPackage("..repository..")
        .should().haveSimpleNameEndingWith("Repository")
        .check(classes);
}

@Test
void exceptionsShouldEndWithException() {
    classes()
        .that().areAssignableTo(Exception.class)
        .should().haveSimpleNameEndingWith("Exception")
        .check(classes);
}
```

---

## Annotations

```java
@Test
void controllersShouldBeAnnotatedWithRestController() {
    classes()
        .that().resideInAPackage("..controller..")
        .and().areNotInterfaces()
        .should().beAnnotatedWith(RestController.class)
        .check(classes);
}

@Test
void servicesShouldBeAnnotatedWithService() {
    classes()
        .that().resideInAPackage("..service..")
        .and().areNotInterfaces()
        .and().haveSimpleNameEndingWith("Service")
        .should().beAnnotatedWith(Service.class)
        .check(classes);
}

@Test
void repositoriesShouldBeAnnotatedWithRepository() {
    classes()
        .that().resideInAPackage("..repository..")
        .and().areInterfaces()
        .should().beAnnotatedWith(Repository.class)
        .orShould().beAssignableTo(JpaRepository.class)
        .check(classes);
}

@Test
void publicMethodsInControllersShouldReturnResponseEntity() {
    methods()
        .that().areDeclaredInClassesThat().resideInAPackage("..controller..")
        .and().arePublic()
        .and().areNotAnnotatedWith(ExceptionHandler.class)
        .should().haveRawReturnType(ResponseEntity.class)
        .check(classes);
}
```

---

## Vererbung und Interfaces

```java
@Test
void interfacesShouldNotHaveImplSuffix() {
    noClasses()
        .that().areInterfaces()
        .should().haveSimpleNameEndingWith("Impl")
        .check(classes);
}

@Test
void servicesShouldNotExtendOtherServices() {
    noClasses()
        .that().resideInAPackage("..service..")
        .and().haveSimpleNameEndingWith("Service")
        .should().beAssignableTo(
            classes().that().haveSimpleNameEndingWith("Service")
        )
        .check(classes);
}
```

---

## Zyklen vermeiden

```java
@Test
void noPackageCycles() {
    slices()
        .matching("de.training.myapp.(*)..")
        .should().beFreeOfCycles()
        .check(classes);
}

@Test
void noCyclesBetweenSlices() {
    slices()
        .matching("de.training.myapp.(**)")
        .should().notDependOnEachOther()
        .check(classes);
}
```

---

## Felder und Konstruktoren

```java
@Test
void fieldInjectionShouldNotBeUsed() {
    noFields()
        .should().beAnnotatedWith(Autowired.class)
        .check(classes);
}

@Test
void servicesShouldUseConstructorInjection() {
    classes()
        .that().resideInAPackage("..service..")
        .and().areAnnotatedWith(Service.class)
        .should().haveOnlyFinalFields()
        .check(classes);
}

@Test
void entitiesShouldHaveNoArgConstructor() {
    classes()
        .that().areAnnotatedWith(Entity.class)
        .should().haveOnlyPrivateConstructors()
        .orShould().haveModifier(JavaModifier.PROTECTED)
        .check(classes);
}
```

---

## Logging

```java
@Test
void loggersShouldBePrivateStaticFinal() {
    fields()
        .that().haveRawType(Logger.class)
        .should().bePrivate()
        .andShould().beStatic()
        .andShould().beFinal()
        .andShould().haveName("log").orShould().haveName("LOG")
        .check(classes);
}
```

---

## Generelle Regeln

```java
@Test
void classesShouldNotUseSystemOut() {
    noClasses()
        .should().accessClassesThat().belongToAnyOf(System.class)
        .orShould().callMethod(PrintStream.class, "println", String.class)
        .check(classes);
}

@Test
void noGenericExceptions() {
    noMethods()
        .should().declareThrowableOfType(Exception.class)
        .orShould().declareThrowableOfType(RuntimeException.class)
        .check(classes);
}

@Test
void utilityClassesShouldBeFinal() {
    classes()
        .that().haveSimpleNameEndingWith("Utils")
        .or().haveSimpleNameEndingWith("Util")
        .or().haveSimpleNameEndingWith("Helper")
        .should().beFinal()
        .andShould().haveOnlyPrivateConstructors()
        .check(classes);
}
```

---

## Custom Predicates

```java
// Eigene Bedingung
DescribedPredicate<JavaClass> areEntities =
    new DescribedPredicate<>("are JPA entities") {
        @Override
        public boolean test(JavaClass javaClass) {
            return javaClass.isAnnotatedWith(Entity.class);
        }
    };

@Test
void entitiesShouldResideInDomainPackage() {
    classes()
        .that(areEntities)
        .should().resideInAPackage("..domain..")
        .check(classes);
}
```
