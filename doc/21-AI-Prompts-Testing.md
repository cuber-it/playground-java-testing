# AI-Assistenten für Testing - Praktischer Guide

Ein Leitfaden für den sinnvollen Einsatz von AI-Tools (ChatGPT, Claude, Copilot, etc.) beim Schreiben von Tests.

---

## Häufige Bedenken

### "Wird mein Code gestohlen?"

**Fakten:**
- Firmeninterne AI-Lösungen (Azure OpenAI, selbst gehostete Modelle) speichern nichts
- Bei öffentlichen Tools: Keine Geschäftsgeheimnisse, Passwörter oder Kundendaten eingeben
- Für Tests meist unproblematisch: Test-Code ist selten geheim
- **Tipp:** Klassennamen anonymisieren wenn nötig (`UserService` → `MyService`)

### "Dem Ergebnis kann man nicht trauen"

**Richtig ist:**
- AI macht Fehler - genau wie Menschen
- Code muss IMMER geprüft werden
- **Aber:** Perfekt für Boilerplate und Grundgerüste
- Tests verifizieren sich selbst: Wenn sie laufen und sinnvoll sind, passt es

### "Das ist doch Betrug"

**Realität:**
- AI ist ein Werkzeug wie IDE, StackOverflow oder Google
- Profis nutzen alle verfügbaren Werkzeuge
- Wichtig: Verstehen was der Code tut, nicht blind kopieren

---

## Wofür AI gut geeignet ist

| Aufgabe | Eignung | Warum |
|---------|---------|-------|
| Test-Boilerplate generieren | Sehr gut | Immer gleiche Struktur |
| Testfälle vorschlagen | Sehr gut | Edge Cases finden |
| Mocking-Setup | Gut | Syntax ist komplex |
| Assertions formulieren | Gut | Viele Optionen |
| Fehler erklären | Sehr gut | Stack Traces analysieren |
| Best Practices erfragen | Gut | Schneller als Googlen |
| Code refactoren | Gut | Muster erkennen |

## Wofür AI weniger geeignet ist

| Aufgabe | Warum |
|---------|-------|
| Fachliche Testlogik | Kennt eure Domäne nicht |
| Aktuelle Library-Versionen | Wissen kann veraltet sein |
| Firmenspezifische Patterns | Kennt euren Code nicht |
| Security-kritischer Code | Immer selbst prüfen |

---

## Prompts für Testing

### 1. Test-Grundgerüst erstellen

```
Erstelle eine JUnit 5 Testklasse für einen UserService.
Der Service hat Methoden: createUser(String name, String email),
findById(Long id), deleteUser(Long id).
Nutze Mockito für das UserRepository und AssertJ für Assertions.
```

**Variante mit mehr Kontext:**
```
Ich habe diese Service-Klasse:

[Code einfügen]

Erstelle eine Testklasse mit JUnit 5 und Mockito.
Teste alle public Methoden mit positiven und negativen Fällen.
```

---

### 2. Testfälle vorschlagen lassen

```
Welche Testfälle sollte ich für diese Methode schreiben?

public BigDecimal calculateDiscount(Order order, Customer customer) {
    // ... implementation
}

Denke an Edge Cases, Grenzwerte und Fehlerfälle.
```

**Antwort enthält typischerweise:**
- Null-Inputs
- Leere Collections
- Grenzwerte (0, negative Zahlen, MAX_VALUE)
- Spezialfälle der Businesslogik

---

### 3. Mocking-Hilfe

```
Wie mocke ich mit Mockito eine Methode die:
- void zurückgibt
- eine Exception wirft
- unterschiedliche Werte bei mehreren Aufrufen zurückgibt
- das übergebene Argument modifiziert zurückgibt
```

```
Mein Repository hat diese Methode:
Optional<User> findByEmail(String email);

Zeige mir verschiedene Mockito-Stubbings dafür:
- User gefunden
- User nicht gefunden
- Exception werfen
```

---

### 4. Assertion-Hilfe

```
Wie prüfe ich mit AssertJ:
- ob eine Liste genau diese Elemente in beliebiger Reihenfolge enthält
- ob ein Objekt bestimmte Feldwerte hat
- ob eine Exception mit bestimmter Message geworfen wird
- ob alle Elemente einer Liste eine Bedingung erfüllen
```

```
Ich habe eine Liste von Person-Objekten.
Zeige mir AssertJ Assertions um zu prüfen:
- Alle sind älter als 18
- Mindestens einer heißt "Max"
- Die Namen sind alphabetisch sortiert
```

---

### 5. Parametrisierte Tests

```
Wandle diesen Test in einen parametrisierten Test um:

@Test
void shouldValidateEmail() {
    assertTrue(validator.isValid("test@example.com"));
    assertFalse(validator.isValid("invalid"));
    assertFalse(validator.isValid(""));
    assertFalse(validator.isValid(null));
}

Nutze @ParameterizedTest mit @CsvSource oder @MethodSource.
```

---

### 6. Spring Boot Test Setup

```
Ich brauche einen @WebMvcTest für diesen Controller:

[Controller-Code einfügen]

Der Controller nutzt TodoService und UserService.
Zeige mir das Test-Setup mit @MockBean und einen Beispieltest.
```

```
Erstelle einen @DataJpaTest für ein TodoRepository.
Das Repository hat eine Custom Query: findByTitleContaining(String search).
Nutze TestEntityManager für Test-Setup.
```

---

### 7. REST Assured Tests

```
Erstelle REST Assured Tests für diese API-Endpunkte:
- GET /api/users - Liste aller User
- GET /api/users/{id} - Einzelner User
- POST /api/users - User anlegen (JSON Body)
- DELETE /api/users/{id} - User löschen

Prüfe Status Codes und Response Body.
```

---

### 8. Fehler verstehen

```
Ich bekomme diesen Fehler in meinem Test:

[Stack Trace einfügen]

Was bedeutet das und wie behebe ich es?
```

```
Mein Mockito-Test schlägt fehl mit:
"Wanted but not invoked: repository.save()"

Der Test sieht so aus:
[Test-Code einfügen]

Was ist falsch?
```

---

### 9. Test verbessern

```
Ist dieser Test gut geschrieben? Was kann ich verbessern?

[Test-Code einfügen]

Achte auf: Lesbarkeit, Wartbarkeit, Best Practices,
fehlende Assertions, Edge Cases.
```

```
Dieser Test ist sehr lang und unübersichtlich.
Wie kann ich ihn refactoren?

[Test-Code einfügen]
```

---

### 10. Builder/Testdaten

```
Erstelle einen Test Data Builder für diese Entity:

@Entity
public class Order {
    private Long id;
    private Customer customer;
    private List<OrderItem> items;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private BigDecimal totalAmount;
}

Mit Fluent API und sinnvollen Defaults.
```

---

### 11. WireMock Stubs

```
Erstelle WireMock Stubs für diese externe API:
- GET /api/weather?city=Berlin → JSON mit Temperatur
- POST /api/notifications → 201 Created
- GET /api/unknown → 404 Not Found
- Timeout simulieren

Zeige auch die Verification.
```

---

### 12. ArchUnit Regeln

```
Erstelle ArchUnit Regeln die prüfen:
- Services dürfen nicht auf Controller zugreifen
- Alle Klassen in ..repository.. enden mit "Repository"
- Keine Klasse verwendet java.util.Date (nur java.time.*)
- Es gibt keine zyklischen Abhängigkeiten zwischen Packages
```

---

## Prompt-Patterns die gut funktionieren

### Kontext geben
```
Ich arbeite mit:
- Java 21
- Spring Boot 3.2
- JUnit 5
- Mockito 5
- AssertJ

[Dann die eigentliche Frage]
```

### Einschränkungen nennen
```
Erstelle einen Test, aber:
- Ohne Spring Context (reiner Unit Test)
- Nur mit Standard-JUnit-Assertions
- Maximal 20 Zeilen
```

### Beispiel-Output vorgeben
```
Ich möchte Tests in diesem Stil:

@Test
void shouldDoSomething_whenCondition() {
    // Arrange
    ...
    // Act
    ...
    // Assert
    ...
}

Erstelle Tests für [...]
```

### Schritt für Schritt
```
Erkläre Schritt für Schritt, wie ich:
1. WireMock in mein Projekt einbinde
2. Einen einfachen Stub erstelle
3. Den Stub in einem Test verwende
4. Verifiziere dass der Stub aufgerufen wurde
```

---

## Workflow-Beispiel

**Aufgabe:** Neuen Service testen

1. **Grundgerüst generieren lassen:**
   ```
   Erstelle eine Testklasse für OrderService mit Mockito.
   Dependencies: OrderRepository, PaymentGateway, NotificationService
   ```

2. **Testfälle vorschlagen lassen:**
   ```
   Welche Testfälle brauche ich für eine placeOrder() Methode
   die: Bestellung speichert, Zahlung durchführt, Benachrichtigung sendet?
   ```

3. **Einzelne Tests ausarbeiten:**
   ```
   Schreibe den Test für: "sollte Bestellung zurückrollen wenn Zahlung fehlschlägt"
   ```

4. **Review:**
   ```
   Prüfe diesen Test auf Vollständigkeit und Best Practices:
   [generierten Code einfügen]
   ```

5. **Manuell anpassen:** Fachliche Details, Firmenvorgaben, etc.

---

## Checkliste: AI-generierten Code prüfen

- [ ] Kompiliert der Code?
- [ ] Laufen die Tests durch?
- [ ] Verstehe ich was jede Zeile tut?
- [ ] Sind die Assertions sinnvoll (nicht trivial)?
- [ ] Fehlen wichtige Testfälle?
- [ ] Passt der Stil zu unserem Projekt?
- [ ] Sind die Mock-Setups realistisch?
- [ ] Keine hardcodierten Werte die nicht passen?

---

## Fazit

AI-Assistenten sind **Werkzeuge**, keine Ersatz für Verständnis.

**Gut für:**
- Schnelles Scaffolding
- Syntax nachschlagen
- Ideen für Testfälle
- Boilerplate reduzieren

**Immer selbst:**
- Code verstehen
- Fachliche Korrektheit prüfen
- An Projekt-Standards anpassen
- Kritisch hinterfragen

**Faustregel:** Wenn du den generierten Code nicht erklären kannst, nutze ihn nicht.
