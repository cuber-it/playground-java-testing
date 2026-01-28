# Test-Antipatterns

Häufige Fehler beim Testen und wie man sie vermeidet.

---

## 1. The Giant

**Problem:** Ein Test der alles auf einmal testet.

```java
// Schlecht
@Test
void testUserService() {
    // Test create
    User created = service.create("Max", "max@example.com");
    assertThat(created).isNotNull();

    // Test find
    User found = service.findById(created.getId());
    assertThat(found).isEqualTo(created);

    // Test update
    found.setName("Moritz");
    User updated = service.update(found);
    assertThat(updated.getName()).isEqualTo("Moritz");

    // Test delete
    service.delete(updated.getId());
    assertThat(service.findById(updated.getId())).isEmpty();

    // Test validation
    assertThrows(Exception.class, () -> service.create("", ""));
}
```

**Besser:** Separate Tests für jede Funktionalität.

```java
@Test void shouldCreateUser() { ... }
@Test void shouldFindUserById() { ... }
@Test void shouldUpdateUser() { ... }
@Test void shouldDeleteUser() { ... }
@Test void shouldRejectInvalidInput() { ... }
```

---

## 2. The Mockery

**Problem:** Alles wird gemockt, nichts wird wirklich getestet.

```java
// Schlecht: Was testet man hier eigentlich?
@Test
void shouldProcessOrder() {
    when(orderService.process(any())).thenReturn(result);

    Result actual = orderService.process(order);  // Ruft den Mock auf!

    assertThat(actual).isEqualTo(result);  // Prüft was wir reingesteckt haben
}
```

**Besser:** Mock nur externe Abhängigkeiten, nicht das SUT.

```java
@Test
void shouldProcessOrder() {
    when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    when(paymentGateway.charge(any())).thenReturn(PaymentResult.SUCCESS);

    Result actual = orderService.process(order);  // Echte Logik!

    assertThat(actual.isSuccess()).isTrue();
}
```

---

## 3. The Inspector

**Problem:** Test prüft Implementierungsdetails statt Verhalten.

```java
// Schlecht: Testet WIE es gemacht wird
@Test
void shouldCreateUser() {
    service.create("Max", "max@example.com");

    verify(repository).save(any());
    verify(validator).validate(any());
    verify(encoder).encode(any());
    verify(logger).log(any());
    verify(cache).invalidate(any());
}
```

**Besser:** Testen WAS das Ergebnis ist.

```java
@Test
void shouldCreateUser() {
    User user = service.create("Max", "max@example.com");

    assertThat(user.getId()).isNotNull();
    assertThat(user.getName()).isEqualTo("Max");
}

@Test
void shouldSendWelcomeEmail_whenUserCreated() {
    service.create("Max", "max@example.com");

    verify(emailService).sendWelcomeMail(any());  // Nur relevanter Seiteneffekt
}
```

---

## 4. The Liar

**Problem:** Test läuft grün, testet aber nicht wirklich.

```java
// Schlecht: Assertion fehlt oder ist trivial
@Test
void shouldCalculateDiscount() {
    double result = calculator.calculate(100, 0.1);
    // Keine Assertion!
}

@Test
void shouldCalculateDiscount() {
    double result = calculator.calculate(100, 0.1);
    assertThat(result).isNotNull();  // Trivial - sagt nichts aus
}

@Test
void shouldCalculateDiscount() {
    double result = calculator.calculate(100, 0.1);
    assertThat(result).isEqualTo(result);  // Immer wahr!
}
```

**Besser:** Konkrete erwartete Werte prüfen.

```java
@Test
void shouldCalculateDiscount() {
    double result = calculator.calculate(100, 0.1);

    assertThat(result).isEqualTo(90.0);
}
```

---

## 5. The Slow Poke

**Problem:** Tests sind unnötig langsam.

```java
// Schlecht
@Test
void shouldTimeout() {
    Thread.sleep(5000);  // Warum?
    // ...
}

@SpringBootTest  // Voller Context für Unit Test
class SimpleCalculatorTest {
    @Test
    void shouldAdd() {
        assertThat(1 + 1).isEqualTo(2);
    }
}
```

**Besser:**
- Unit Tests ohne Spring Context
- `@WebMvcTest` / `@DataJpaTest` statt `@SpringBootTest`
- WireMock statt echter externer Services
- TestContainers nur wo nötig

---

## 6. The Chain Gang

**Problem:** Tests hängen voneinander ab.

```java
// Schlecht: Test 2 braucht Ergebnis von Test 1
static Long createdUserId;

@Test
@Order(1)
void shouldCreateUser() {
    User user = service.create("Max", "max@example.com");
    createdUserId = user.getId();  // Für nächsten Test
}

@Test
@Order(2)
void shouldFindUser() {
    User user = service.findById(createdUserId);  // Abhängigkeit!
    assertThat(user).isNotNull();
}
```

**Besser:** Jeder Test ist unabhängig.

```java
@Test
void shouldFindUser() {
    // Eigenes Setup
    User created = service.create("Max", "max@example.com");

    User found = service.findById(created.getId());

    assertThat(found).isNotNull();
}
```

---

## 7. The Flickering Test (Flaky)

**Problem:** Test schlägt manchmal fehl, manchmal nicht.

```java
// Schlecht: Timing-abhängig
@Test
void shouldProcessAsync() {
    service.processAsync(data);
    Thread.sleep(100);  // Hoffentlich fertig?
    assertThat(result).isNotNull();
}

// Schlecht: Reihenfolge-abhängig
@Test
void shouldReturnUsers() {
    List<User> users = service.findAll();
    assertThat(users.get(0).getName()).isEqualTo("Max");  // Reihenfolge?
}

// Schlecht: Zeit-abhängig
@Test
void shouldBeCreatedToday() {
    User user = service.create("Max", "max@example.com");
    assertThat(user.getCreatedAt().toLocalDate())
        .isEqualTo(LocalDate.now());  // Mitternacht?
}
```

**Besser:**
```java
// Warten bis fertig
await().atMost(5, SECONDS).until(() -> result != null);

// Unabhängig von Reihenfolge
assertThat(users).extracting(User::getName).contains("Max");

// Zeit mocken oder Toleranz
assertThat(user.getCreatedAt()).isCloseTo(LocalDateTime.now(), within(1, MINUTES));
```

---

## 8. The Secret Catcher

**Problem:** Test fängt Exceptions und ignoriert sie.

```java
// Schlecht
@Test
void shouldHandleError() {
    try {
        service.doSomething();
    } catch (Exception e) {
        // Test ist grün, aber Exception wurde vielleicht erwartet?
    }
}
```

**Besser:** Explizit testen.

```java
@Test
void shouldThrowException_whenInvalid() {
    assertThatThrownBy(() -> service.doSomething())
        .isInstanceOf(IllegalArgumentException.class);
}

@Test
void shouldNotThrowException_whenValid() {
    assertThatCode(() -> service.doSomething())
        .doesNotThrowAnyException();
}
```

---

## 9. The Loudmouth

**Problem:** Test gibt massiv viel aus auf die Konsole.

```java
// Schlecht
@Test
void shouldProcess() {
    System.out.println("Starting test...");
    System.out.println("Input: " + input);
    System.out.println("Processing...");
    Result result = service.process(input);
    System.out.println("Result: " + result);
    System.out.println("Expected: " + expected);
    System.out.println("Test finished!");
}
```

**Besser:** Keine Ausgaben. Bei Bedarf Logging nutzen das man ein/ausschalten kann.

---

## 10. The Free Ride

**Problem:** Test testet zu viel implizit mit.

```java
// Schlecht: Testet auch Validation, Repository, etc.
@Test
void shouldCreateUser() {
    User user = service.create("Max", "max@example.com");

    assertThat(user).isNotNull();
    // Wenn das fehlschlägt - wo ist der Bug?
    // Service? Validation? Repository? Encoder?
}
```

**Besser:** Fokussierte Tests mit Mocks.

```java
// Unit Test: Nur Service-Logik
@Test
void shouldCreateUser() {
    when(repository.save(any())).thenAnswer(inv -> {
        User u = inv.getArgument(0);
        u.setId(1L);
        return u;
    });

    User user = service.create("Max", "max@example.com");

    assertThat(user.getId()).isEqualTo(1L);
}

// Separater Test für Validation
@Test
void shouldRejectInvalidEmail() {
    assertThatThrownBy(() -> service.create("Max", "invalid"))
        .isInstanceOf(ValidationException.class);
}
```

---

## 11. The Nitpicker

**Problem:** Test prüft irrelevante Details.

```java
// Schlecht: Exakter String-Match
@Test
void shouldReturnError() {
    Exception e = assertThrows(Exception.class, () -> service.process(null));

    assertThat(e.getMessage())
        .isEqualTo("Invalid input: value must not be null. Please provide a valid value. Error code: ERR-001");
    // Bricht bei jeder Textänderung
}
```

**Besser:** Nur relevante Teile prüfen.

```java
@Test
void shouldReturnError() {
    Exception e = assertThrows(Exception.class, () -> service.process(null));

    assertThat(e.getMessage()).contains("null");
    // Oder gar nicht prüfen wenn Message egal
}
```

---

## 12. The Optimist

**Problem:** Nur Happy Path, keine Fehlerfälle.

```java
// Unvollständig
class UserServiceTest {
    @Test void shouldCreateUser() { ... }
    @Test void shouldFindUser() { ... }
    @Test void shouldUpdateUser() { ... }
    // Keine Tests für:
    // - Ungültige Eingaben
    // - Nicht gefundene User
    // - Duplikate
    // - Null-Werte
}
```

**Besser:** Auch Fehlerfälle testen.

```java
@Test void shouldCreateUser() { ... }
@Test void shouldRejectEmptyName() { ... }
@Test void shouldRejectDuplicateEmail() { ... }
@Test void shouldReturnEmpty_whenUserNotFound() { ... }
@Test void shouldThrowException_whenIdIsNull() { ... }
```

---

## 13. The Copy-Paster

**Problem:** Massive Code-Duplikation in Tests.

```java
// Schlecht: Gleicher Setup-Code überall
@Test
void test1() {
    User user = new User();
    user.setId(1L);
    user.setName("Max");
    user.setEmail("max@example.com");
    user.setActive(true);
    // ... Test
}

@Test
void test2() {
    User user = new User();
    user.setId(1L);
    user.setName("Max");
    user.setEmail("max@example.com");
    user.setActive(true);
    // ... Test
}
```

**Besser:** Builder, @BeforeEach, Hilfsmethoden.

```java
private User defaultUser;

@BeforeEach
void setUp() {
    defaultUser = aUser().withName("Max").active().build();
}

@Test
void test1() {
    // Nutzt defaultUser oder Variante davon
}
```

---

## Zusammenfassung

| Antipattern | Problem | Lösung |
|-------------|---------|--------|
| The Giant | Test testet alles | Ein Konzept pro Test |
| The Mockery | Alles gemockt | Nur Dependencies mocken |
| The Inspector | Testet Implementierung | Verhalten testen |
| The Liar | Keine echte Assertion | Konkrete Werte prüfen |
| The Slow Poke | Zu langsam | Slice Tests, Mocks |
| The Chain Gang | Tests abhängig | Isolation |
| The Flickering | Flaky Tests | Determinismus |
| The Secret Catcher | Exceptions ignoriert | Explizit testen |
| The Loudmouth | Viel Console Output | Keine Ausgaben |
| The Free Ride | Zu viel implizit | Fokussierte Tests |
| The Nitpicker | Irrelevantes geprüft | Nur Relevantes |
| The Optimist | Nur Happy Path | Auch Fehler testen |
| The Copy-Paster | Duplikation | Builder, Setup |
