# Styleguide für Tests

Einheitliche Konventionen für lesbare, wartbare Tests.

---

## Namenskonventionen

### Testklassen

```java
// Klasse + Test
UserServiceTest
OrderRepositoryTest
TodoControllerTest

// Oder mit Suffix für Testtyp
UserServiceUnitTest
UserServiceIntegrationTest
```

### Testmethoden

**Pattern:** `should[Ergebnis]_when[Bedingung]` oder `should[Ergebnis]When[Bedingung]`

```java
// Gut
void shouldReturnUser_whenIdExists()
void shouldThrowException_whenEmailIsInvalid()
void shouldSendNotification_whenOrderIsPlaced()

// Auch OK: Ohne Unterstrich
void shouldReturnUserWhenIdExists()
void shouldThrowExceptionWhenEmailIsInvalid()

// Akzeptabel: Einfache Form für simple Tests
void shouldCreateUser()
void shouldDeleteOrder()
```

**Nicht:**
```java
// Zu vage
void testUser()
void test1()
void userTest()

// Implementierungsdetails
void shouldCallRepositorySaveMethod()

// Negativ formuliert (schwer lesbar)
void shouldNotNotReturnNull()
```

---

## Struktur

### AAA-Pattern (Arrange-Act-Assert)

```java
@Test
void shouldCalculateTotal_whenItemsInCart() {
    // Arrange
    Cart cart = new Cart();
    cart.add(new Item("Book", 20.00));
    cart.add(new Item("Pen", 5.00));

    // Act
    double total = cart.calculateTotal();

    // Assert
    assertThat(total).isEqualTo(25.00);
}
```

**Regeln:**
- Leerzeile zwischen den Abschnitten
- Kommentare `// Arrange`, `// Act`, `// Assert` bei komplexen Tests
- Bei einfachen Tests optional

### Given-When-Then (BDD-Stil)

```java
@Test
void shouldApplyDiscount_whenCustomerIsPremium() {
    // Given
    Customer customer = aPremiumCustomer().build();
    Order order = anOrder().withTotal(100.00).build();

    // When
    double finalPrice = discountService.calculate(order, customer);

    // Then
    assertThat(finalPrice).isEqualTo(90.00);
}
```

---

## Ein Konzept pro Test

```java
// Gut: Ein Test, ein Konzept
@Test
void shouldRejectEmptyUsername() {
    assertThatThrownBy(() -> new User("", "mail@example.com"))
        .isInstanceOf(IllegalArgumentException.class);
}

@Test
void shouldRejectNullUsername() {
    assertThatThrownBy(() -> new User(null, "mail@example.com"))
        .isInstanceOf(IllegalArgumentException.class);
}

// Schlecht: Mehrere Konzepte vermischt
@Test
void shouldValidateUser() {
    // Testet leeren Namen
    assertThrows(...);
    // Testet null Namen
    assertThrows(...);
    // Testet ungültige Email
    assertThrows(...);
    // Testet erfolgreiche Erstellung
    assertNotNull(...);
}
```

---

## Assertions

### Eine logische Assertion pro Test

```java
// Gut: Mehrere Assertions für EIN Ergebnis
@Test
void shouldCreateUser() {
    User user = service.create("Max", "max@example.com");

    assertThat(user.getId()).isNotNull();
    assertThat(user.getName()).isEqualTo("Max");
    assertThat(user.getEmail()).isEqualTo("max@example.com");
    assertThat(user.getCreatedAt()).isNotNull();
}

// Besser mit Soft Assertions (alle Fehler auf einmal)
@Test
void shouldCreateUser() {
    User user = service.create("Max", "max@example.com");

    SoftAssertions.assertSoftly(soft -> {
        soft.assertThat(user.getId()).isNotNull();
        soft.assertThat(user.getName()).isEqualTo("Max");
        soft.assertThat(user.getEmail()).isEqualTo("max@example.com");
    });
}
```

### Sprechende Assertions

```java
// Gut: AssertJ - liest sich wie ein Satz
assertThat(users).hasSize(3);
assertThat(user.getName()).startsWith("Max");
assertThat(result).isPresent().hasValue(expected);

// Weniger gut: JUnit mit assertTrue
assertTrue(users.size() == 3);  // Fehlermeldung: "expected true"
```

### Keine Logik in Assertions

```java
// Schlecht
assertThat(result).isEqualTo(a + b * c);

// Gut
int expected = 42;
assertThat(result).isEqualTo(expected);
```

---

## Testdaten

### Sprechende Variablennamen

```java
// Gut
User activeUser = aUser().active().build();
User inactiveUser = aUser().inactive().build();
Order highValueOrder = anOrder().withTotal(10000).build();

// Schlecht
User u1 = new User();
User u2 = new User();
Order o = new Order();
```

### Nur relevante Daten

```java
// Gut: Nur was für den Test relevant ist
@Test
void shouldApplyDiscount_whenTotalOver100() {
    Order order = anOrder()
        .withTotal(150.00)  // Relevant!
        .build();

    double discount = calculator.calculate(order);

    assertThat(discount).isEqualTo(15.00);
}

// Schlecht: Irrelevante Details
@Test
void shouldApplyDiscount_whenTotalOver100() {
    Order order = new Order();
    order.setId(12345L);              // Irrelevant
    order.setCustomerId(67890L);      // Irrelevant
    order.setCreatedAt(LocalDateTime.now());  // Irrelevant
    order.setStatus(OrderStatus.NEW); // Irrelevant
    order.setTotal(150.00);           // Relevant
    // ...
}
```

### Builder und Object Mother nutzen

```java
// Mit Builder
User user = aUser()
    .withName("Max")
    .withEmail("max@example.com")
    .active()
    .build();

// Mit Object Mother
User user = Users.anActiveUser();
Order order = Orders.aPendingOrder();
```

---

## Mocking

### Nur das Nötigste mocken

```java
// Gut: Nur externe Abhängigkeit mocken
@Mock
UserRepository repository;

@InjectMocks
UserService service;  // Echte Logik wird getestet

// Schlecht: Alles mocken
@Mock
UserService service;  // Was testet man dann noch?
```

### Stub vs. Verify

```java
// Stub: Wenn Rückgabewert benötigt wird
when(repository.findById(1L)).thenReturn(Optional.of(user));

// Verify: Wenn Seiteneffekt geprüft werden soll
verify(emailService).sendWelcomeMail(user);

// Nicht: Verify für alles
verify(repository).findById(1L);  // Überflüssig wenn Ergebnis geprüft wird
```

---

## Test-Isolation

### Kein Zustand zwischen Tests

```java
// Schlecht: Statische Variable
static List<User> users = new ArrayList<>();

@Test
void test1() {
    users.add(new User("Max"));
}

@Test
void test2() {
    // Schlägt fehl wenn test1 zuerst läuft!
    assertThat(users).isEmpty();
}

// Gut: Frischer Zustand pro Test
@BeforeEach
void setUp() {
    users = new ArrayList<>();
}
```

### Keine Test-Reihenfolge-Abhängigkeit

Tests müssen in beliebiger Reihenfolge laufen können.

---

## Lesbarkeit

### Keine Magic Numbers

```java
// Schlecht
assertThat(result).isEqualTo(42);
when(service.calculate(100, 0.15)).thenReturn(85.0);

// Gut
int expectedCount = 42;
assertThat(result).isEqualTo(expectedCount);

double originalPrice = 100.0;
double discountRate = 0.15;
double expectedPrice = 85.0;
when(service.calculate(originalPrice, discountRate)).thenReturn(expectedPrice);
```

### Konstanten für wiederkehrende Werte

```java
class UserServiceTest {

    private static final String VALID_EMAIL = "test@example.com";
    private static final String INVALID_EMAIL = "not-an-email";
    private static final Long USER_ID = 1L;

    @Test
    void shouldFindUser() {
        when(repository.findById(USER_ID)).thenReturn(Optional.of(user));
        // ...
    }
}
```

### Hilfsmethoden für komplexes Setup

```java
@Test
void shouldProcessOrder() {
    Order order = createOrderWithItems(3);
    Customer customer = createPremiumCustomer();

    Result result = service.process(order, customer);

    assertThat(result.isSuccess()).isTrue();
}

private Order createOrderWithItems(int itemCount) {
    Order order = new Order();
    for (int i = 0; i < itemCount; i++) {
        order.addItem(new Item("Item " + i, 10.0));
    }
    return order;
}

private Customer createPremiumCustomer() {
    return aCustomer().premium().build();
}
```

---

## Kommentare

### Wann Kommentare sinnvoll sind

```java
// Gut: Erklärt WARUM, nicht WAS
@Test
void shouldReturnCachedValue_whenCalledTwice() {
    // First call populates cache
    service.getValue();

    // Second call should use cache, not repository
    service.getValue();

    verify(repository, times(1)).findValue();  // Only one DB call
}
```

### Wann keine Kommentare nötig sind

```java
// Überflüssig - Code ist selbsterklärend
@Test
void shouldCreateUser() {
    // Create a user  <- Überflüssig
    User user = service.create("Max", "max@example.com");

    // Assert user is not null  <- Überflüssig
    assertThat(user).isNotNull();
}
```

---

## Zusammenfassung

| Regel | Beispiel |
|-------|----------|
| Sprechende Namen | `shouldThrowException_whenEmailInvalid` |
| AAA-Struktur | Arrange → Act → Assert |
| Ein Konzept pro Test | Nicht mehrere Szenarien mischen |
| Relevante Testdaten | Nur was für Test wichtig ist |
| Builder nutzen | `aUser().withName("Max").build()` |
| Keine Magic Numbers | Konstanten mit Namen |
| Isolation | Kein Zustand zwischen Tests |
