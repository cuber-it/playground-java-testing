# Glossar

Wichtige Begriffe rund um Testing, alphabetisch sortiert.

---

## A

**AAA-Pattern (Arrange-Act-Assert)**
Strukturierungsmuster für Tests: Vorbereitung → Ausführung → Prüfung. Macht Tests lesbar und einheitlich.

**Acceptance Test**
Test aus Anwendersicht, prüft ob Anforderungen erfüllt sind. Oft End-to-End.

**Assertion**
Prüfung/Behauptung im Test. Schlägt fehl wenn die Bedingung nicht erfüllt ist. Beispiel: `assertThat(result).isEqualTo(expected)`

**AssertJ**
Fluent Assertion-Library für Java. Lesbarere Assertions als JUnit-Standard.

---

## B

**BDD (Behavior Driven Development)**
Entwicklungsansatz der Verhalten in natürlicher Sprache beschreibt (Given-When-Then). Tools: Cucumber, JBehave.

**Black-Box-Test**
Test ohne Kenntnis der internen Implementierung. Nur Input/Output wird geprüft.

**Boundary Testing**
Testen von Grenzwerten. Beispiel: Bei erlaubtem Bereich 1-100 teste 0, 1, 100, 101.

---

## C

**Code Coverage**
Maß für den Anteil des Codes der durch Tests ausgeführt wird. Arten: Line, Branch, Method, Class Coverage.

**Consumer (Pact)**
In Contract Testing: Der Service der eine API aufruft/konsumiert.

**Contract Test**
Test der das Schnittstellenformat zwischen Services prüft. Verhindert Breaking Changes.

---

## D

**Data-Driven Test**
Test der mit verschiedenen Datensätzen ausgeführt wird. In JUnit 5: `@ParameterizedTest`.

**Dependency Injection**
Design Pattern bei dem Abhängigkeiten von außen übergeben werden. Ermöglicht einfaches Mocking.

**Double**
Oberbegriff für Test-Ersatzobjekte (Mock, Stub, Fake, Spy, Dummy).

**DRY (Don't Repeat Yourself)**
Prinzip: Keine Code-Duplikation. In Tests aber: Lesbarkeit vor DRY.

---

## E

**E2E-Test (End-to-End)**
Test des gesamten Systems von UI bis Datenbank. Simuliert echten Benutzer.

**Edge Case**
Grenzfall, Sonderfall. Beispiel: Leere Liste, null, sehr große Zahlen.

---

## F

**Fake**
Test-Double mit vereinfachter, aber funktionierender Implementierung. Beispiel: In-Memory-Datenbank statt echter DB.

**Fixture**
Testdaten und -umgebung die vor Tests aufgebaut wird. Setup für konsistenten Ausgangszustand.

**Flaky Test**
Test der manchmal fehlschlägt, manchmal nicht (ohne Code-Änderung). Meist durch Timing, Reihenfolge oder externe Abhängigkeiten.

---

## G

**Given-When-Then**
BDD-Struktur: Gegeben (Ausgangszustand) - Wenn (Aktion) - Dann (erwartetes Ergebnis).

**Green Bar**
Alle Tests erfolgreich (grüner Balken in IDE). Ziel bei TDD.

---

## H

**Happy Path**
Der normale, fehlerfreie Ablauf. Gegenteil: Sad Path, Error Path.

**Headless Browser**
Browser ohne grafische Oberfläche. Schneller für automatisierte Tests.

---

## I

**Integration Test**
Test der das Zusammenspiel mehrerer Komponenten prüft. Beispiel: Service + Repository + Datenbank.

**Isolation**
Tests beeinflussen sich nicht gegenseitig. Jeder Test ist unabhängig.

---

## J

**JaCoCo**
Java Code Coverage Library. Misst welcher Code durch Tests ausgeführt wird.

---

## L

**Locator**
Selektor für UI-Elemente in E2E-Tests. CSS, XPath, Test-ID, etc.

---

## M

**Mock**
Test-Double das Aufrufe aufzeichnet und vordefinierte Antworten liefert. Wird nach dem Test verifiziert.

**Mockito**
Populärstes Mocking-Framework für Java.

**Mutation Testing**
Testet die Tests: Ändert Code (Mutanten) und prüft ob Tests das erkennen. Tool: PIT.

---

## N

**Negative Test**
Test der Fehlerfälle prüft. Beispiel: Ungültige Eingaben, Exceptions.

---

## O

**Object Mother**
Pattern für Testdaten: Zentrale Klasse die vorkonfigurierte Testobjekte liefert.

---

## P

**Parameterized Test**
Ein Test der mit verschiedenen Parametern mehrfach ausgeführt wird.

**Provider (Pact)**
In Contract Testing: Der Service der eine API bereitstellt.

---

## R

**Red-Green-Refactor**
TDD-Zyklus: Test schreiben (rot) → Code schreiben (grün) → Aufräumen (refactor).

**Regression Test**
Test der sicherstellt, dass bestehende Funktionalität nicht kaputt geht.

**REST Assured**
Java-Library für REST-API-Tests mit fluent API.

---

## S

**Sad Path**
Fehlerfall, Ausnahmefall. Gegenteil: Happy Path.

**Slice Test**
Spring Boot Test der nur einen Teil des Kontexts lädt. `@WebMvcTest`, `@DataJpaTest`, etc.

**Smoke Test**
Schneller Test ob das System grundsätzlich funktioniert. Vor detaillierten Tests.

**Spy**
Test-Double das echte Methoden aufruft, aber Aufrufe aufzeichnet. Partial Mock.

**Stub**
Test-Double das vordefinierte Antworten liefert, ohne Verifikation.

**SUT (System Under Test)**
Das zu testende System/Objekt. Auch: CUT (Class Under Test).

---

## T

**TDD (Test Driven Development)**
Entwicklungsansatz: Erst Test schreiben, dann Code. Red-Green-Refactor.

**Test Data Builder**
Pattern für Testdaten: Fluent API zum Erstellen von Testobjekten.

**Test Pyramid**
Modell für Test-Verteilung: Viele Unit Tests, weniger Integration Tests, wenige E2E Tests.

```
       /\
      /E2E\
     /------\
    /  Integ \
   /----------\
  /    Unit    \
 ----------------
```

**TestContainers**
Library die Docker-Container in Tests startet. Echte Datenbanken, Message Broker, etc.

---

## U

**Unit Test**
Test einer einzelnen Einheit (Klasse/Methode) in Isolation. Schnell, keine externen Abhängigkeiten.

---

## V

**Verification (Mockito)**
Prüfung ob ein Mock wie erwartet aufgerufen wurde. `verify(mock).method()`

---

## W

**White-Box-Test**
Test mit Kenntnis der internen Implementierung. Prüft Codestruktur.

**WireMock**
Library zum Mocken von HTTP-Services. Simuliert externe APIs.

---

## Abkürzungen

| Abkürzung | Bedeutung |
|-----------|-----------|
| AAA | Arrange-Act-Assert |
| API | Application Programming Interface |
| BDD | Behavior Driven Development |
| CI/CD | Continuous Integration / Continuous Delivery |
| CRUD | Create, Read, Update, Delete |
| DTO | Data Transfer Object |
| E2E | End-to-End |
| HTTP | Hypertext Transfer Protocol |
| IDE | Integrated Development Environment |
| JSON | JavaScript Object Notation |
| JPA | Java Persistence API |
| REST | Representational State Transfer |
| SUT | System Under Test |
| TDD | Test Driven Development |
| UI | User Interface |
| XML | Extensible Markup Language |
