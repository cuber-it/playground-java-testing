# JUnit 5 Snippets

Copy-Paste fertige Code-Blöcke.

---

## Test-Klasse Grundgerüst

```java
import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.*;

class MyClassTest {

    @BeforeEach
    void setUp() {
        // Setup
    }

    @Test
    void shouldDoSomething() {
        // Arrange

        // Act

        // Assert
    }
}
```

---

## Test-Methode

```java
@Test
void shouldReturnTrueWhenInputIsValid() {
    // Arrange
    var input = "valid";

    // Act
    var result = sut.process(input);

    // Assert
    assertThat(result).isTrue();
}
```

---

## Parameterized Test - ValueSource

```java
@ParameterizedTest
@ValueSource(strings = {"a", "b", "c"})
void shouldAcceptValidInput(String input) {
    var result = sut.isValid(input);

    assertThat(result).isTrue();
}
```

### Weitere ValueSource-Typen

```java
@ValueSource(ints = {1, 2, 3})
@ValueSource(longs = {1L, 2L, 3L})
@ValueSource(doubles = {1.0, 2.5, 3.14})
@ValueSource(booleans = {true, false})
```

---

## Parameterized Test - CsvSource

```java
@ParameterizedTest
@CsvSource({
    "1, true",
    "0, false",
    "-1, false"
})
void shouldValidateNumber(int input, boolean expected) {
    var result = sut.isPositive(input);

    assertThat(result).isEqualTo(expected);
}
```

### Mit Strings

```java
@ParameterizedTest
@CsvSource({
    "hello, 5",
    "'hello, world', 12",
    "'', 0"
})
void shouldCalculateLength(String input, int expectedLength) {
    assertThat(input.length()).isEqualTo(expectedLength);
}
```

---

## Parameterized Test - MethodSource

```java
@ParameterizedTest
@MethodSource("provideTestData")
void shouldProcessData(String input, int expected) {
    var result = sut.process(input);

    assertThat(result).isEqualTo(expected);
}

static Stream<Arguments> provideTestData() {
    return Stream.of(
        Arguments.of("abc", 3),
        Arguments.of("hello", 5),
        Arguments.of("", 0)
    );
}
```

---

## Parameterized Test - EnumSource

```java
@ParameterizedTest
@EnumSource(Status.class)
void shouldHandleAllStatuses(Status status) {
    var result = sut.process(status);

    assertThat(result).isNotNull();
}

// Nur bestimmte Werte
@ParameterizedTest
@EnumSource(value = Status.class, names = {"ACTIVE", "PENDING"})
void shouldHandleActiveStatuses(Status status) {
    // ...
}

// Ausschließen
@ParameterizedTest
@EnumSource(value = Status.class, mode = EnumSource.Mode.EXCLUDE, names = {"DELETED"})
void shouldHandleNonDeletedStatuses(Status status) {
    // ...
}
```

---

## Nested Tests

```java
@Nested
@DisplayName("When user is authenticated")
class WhenAuthenticated {

    @BeforeEach
    void setUp() {
        // Login user
    }

    @Test
    void shouldAllowAccess() {
        // ...
    }

    @Test
    void shouldShowUserData() {
        // ...
    }
}

@Nested
@DisplayName("When user is not authenticated")
class WhenNotAuthenticated {

    @Test
    void shouldDenyAccess() {
        // ...
    }
}
```

---

## Exception Test

```java
@Test
void shouldThrowExceptionWhenInputIsNull() {
    assertThatThrownBy(() -> sut.process(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Input must not be null");
}

// Alternative
@Test
void shouldThrowException() {
    var exception = assertThrows(IllegalArgumentException.class,
        () -> sut.process(null));

    assertThat(exception.getMessage()).contains("null");
}
```

---

## Timeout Test

```java
@Test
@Timeout(5)  // 5 Sekunden
void shouldCompleteInTime() {
    sut.longRunningOperation();
}

@Test
@Timeout(value = 500, unit = TimeUnit.MILLISECONDS)
void shouldBefast() {
    sut.quickOperation();
}
```

---

## Bedingte Ausführung

```java
@Test
@EnabledOnOs(OS.LINUX)
void onlyOnLinux() {
    // ...
}

@Test
@DisabledOnOs(OS.WINDOWS)
void notOnWindows() {
    // ...
}

@Test
@EnabledIfEnvironmentVariable(named = "CI", matches = "true")
void onlyInCI() {
    // ...
}

@Test
@EnabledIfSystemProperty(named = "test.integration", matches = "true")
void onlyWhenPropertySet() {
    // ...
}
```

---

## Repeated Test

```java
@RepeatedTest(5)
void shouldBeConsistent(RepetitionInfo info) {
    System.out.println("Run " + info.getCurrentRepetition()
        + " of " + info.getTotalRepetitions());

    var result = sut.randomOperation();

    assertThat(result).isNotNull();
}
```

---

## DisplayName

```java
@Test
@DisplayName("Should return true when input is a valid email address")
void emailValidation() {
    // ...
}

@DisplayName("Email Validation Tests")
class EmailValidatorTest {
    // ...
}
```
