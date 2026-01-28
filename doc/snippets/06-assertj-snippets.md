# AssertJ Erweiterte Snippets

Copy-Paste fertige Code-Blöcke.

---

## Listen-Assertions

```java
// Größe
assertThat(list).isEmpty();
assertThat(list).isNotEmpty();
assertThat(list).hasSize(3);
assertThat(list).hasSizeGreaterThan(0);
assertThat(list).hasSizeBetween(1, 10);

// Enthält
assertThat(list).contains("a", "b");
assertThat(list).containsExactly("a", "b", "c");  // Reihenfolge wichtig
assertThat(list).containsExactlyInAnyOrder("c", "a", "b");
assertThat(list).containsOnly("a", "b");  // Nur diese, beliebige Reihenfolge
assertThat(list).doesNotContain("x", "y");

// Null-Handling
assertThat(list).containsNull();
assertThat(list).doesNotContainNull();

// Position
assertThat(list).startsWith("a", "b");
assertThat(list).endsWith("y", "z");
assertThat(list).first().isEqualTo("a");
assertThat(list).last().isEqualTo("z");
assertThat(list).element(2).isEqualTo("c");
```

---

## Extracting (Objekt-Listen)

```java
List<Person> persons = List.of(
    new Person("Max", 25),
    new Person("Anna", 30)
);

// Ein Feld extrahieren
assertThat(persons)
    .extracting(Person::getName)
    .containsExactly("Max", "Anna");

// Mehrere Felder
assertThat(persons)
    .extracting("name", "age")
    .containsExactly(
        tuple("Max", 25),
        tuple("Anna", 30)
    );

// Mit Lambda
assertThat(persons)
    .extracting(p -> p.getName().toUpperCase())
    .containsExactly("MAX", "ANNA");
```

---

## Filtering

```java
assertThat(persons)
    .filteredOn(p -> p.getAge() > 25)
    .hasSize(1)
    .extracting(Person::getName)
    .containsOnly("Anna");

// Mit Property
assertThat(persons)
    .filteredOn("age", 25)
    .hasSize(1);

// Mit Condition
assertThat(persons)
    .filteredOn(new Condition<>(p -> p.getAge() >= 18, "adult"))
    .hasSize(2);
```

---

## Soft Assertions

```java
// Alle Fehler sammeln, am Ende ausgeben
SoftAssertions.assertSoftly(soft -> {
    soft.assertThat(person.getName()).isEqualTo("Max");
    soft.assertThat(person.getAge()).isEqualTo(25);
    soft.assertThat(person.getEmail()).contains("@");
    soft.assertThat(person.isActive()).isTrue();
});

// Alternative: AutoCloseable
try (var soft = new SoftAssertions()) {
    soft.assertThat(a).isEqualTo(1);
    soft.assertThat(b).isEqualTo(2);
}  // assertAll() wird automatisch aufgerufen

// Mit JUnit 5 Extension
@ExtendWith(SoftAssertionsExtension.class)
class MyTest {
    @InjectSoftAssertions
    SoftAssertions soft;

    @Test
    void test() {
        soft.assertThat(a).isEqualTo(1);
        soft.assertThat(b).isEqualTo(2);
    }
}
```

---

## Exception Assertions

```java
// Standard
assertThatThrownBy(() -> service.process(null))
    .isInstanceOf(IllegalArgumentException.class)
    .hasMessage("Input must not be null")
    .hasMessageContaining("null")
    .hasMessageStartingWith("Input")
    .hasNoCause();

// Mit Cause
assertThatThrownBy(() -> service.process(null))
    .isInstanceOf(ServiceException.class)
    .hasCauseInstanceOf(IOException.class)
    .hasRootCauseInstanceOf(SocketException.class);

// Spezifische Exception-Typen
assertThatIllegalArgumentException()
    .isThrownBy(() -> service.process(null))
    .withMessage("Input must not be null");

assertThatNullPointerException()
    .isThrownBy(() -> service.process(null));

assertThatIOException()
    .isThrownBy(() -> service.readFile("missing.txt"));

// Keine Exception
assertThatCode(() -> service.safeMethod())
    .doesNotThrowAnyException();
```

---

## Optional Assertions

```java
Optional<String> opt = Optional.of("value");

assertThat(opt).isPresent();
assertThat(opt).isNotEmpty();
assertThat(opt).hasValue("value");
assertThat(opt).hasValueSatisfying(v -> assertThat(v).startsWith("val"));
assertThat(opt).containsInstanceOf(String.class);

Optional<String> empty = Optional.empty();
assertThat(empty).isEmpty();
assertThat(empty).isNotPresent();
```

---

## Map Assertions

```java
Map<String, Integer> map = Map.of("a", 1, "b", 2);

assertThat(map).isEmpty();
assertThat(map).isNotEmpty();
assertThat(map).hasSize(2);

assertThat(map).containsKey("a");
assertThat(map).containsKeys("a", "b");
assertThat(map).doesNotContainKey("x");

assertThat(map).containsValue(1);
assertThat(map).containsValues(1, 2);

assertThat(map).containsEntry("a", 1);
assertThat(map).contains(entry("a", 1), entry("b", 2));

assertThat(map).containsOnly(entry("a", 1), entry("b", 2));
assertThat(map).containsExactly(entry("a", 1), entry("b", 2));
```

---

## String Assertions

```java
assertThat(str).isEmpty();
assertThat(str).isNotEmpty();
assertThat(str).isBlank();
assertThat(str).isNotBlank();
assertThat(str).isNullOrEmpty();

assertThat(str).isEqualTo("expected");
assertThat(str).isEqualToIgnoringCase("EXPECTED");
assertThat(str).isEqualToIgnoringWhitespace("expected  ");

assertThat(str).contains("sub");
assertThat(str).containsIgnoringCase("SUB");
assertThat(str).containsOnlyOnce("unique");
assertThat(str).doesNotContain("bad");

assertThat(str).startsWith("Hello");
assertThat(str).endsWith("World");

assertThat(str).matches("\\d{4}-\\d{2}-\\d{2}");
assertThat(str).containsPattern("\\d+");

assertThat(str).hasSize(10);
assertThat(str).hasSizeBetween(5, 15);
assertThat(str).hasLineCount(3);
```

---

## Numeric Assertions

```java
assertThat(num).isZero();
assertThat(num).isNotZero();
assertThat(num).isOne();
assertThat(num).isPositive();
assertThat(num).isNegative();
assertThat(num).isNotPositive();
assertThat(num).isNotNegative();

assertThat(num).isEqualTo(42);
assertThat(num).isNotEqualTo(0);
assertThat(num).isGreaterThan(10);
assertThat(num).isGreaterThanOrEqualTo(10);
assertThat(num).isLessThan(100);
assertThat(num).isLessThanOrEqualTo(100);
assertThat(num).isBetween(10, 100);
assertThat(num).isStrictlyBetween(10, 100);

// Floating Point
assertThat(3.14).isCloseTo(Math.PI, within(0.01));
assertThat(3.14).isCloseTo(Math.PI, withinPercentage(1.0));
```

---

## Date/Time Assertions

```java
LocalDate date = LocalDate.of(2024, 6, 15);

assertThat(date).isToday();
assertThat(date).isBefore(LocalDate.now());
assertThat(date).isAfter(LocalDate.of(2024, 1, 1));
assertThat(date).isBetween(
    LocalDate.of(2024, 1, 1),
    LocalDate.of(2024, 12, 31)
);

assertThat(date).hasYear(2024);
assertThat(date).hasMonth(Month.JUNE);
assertThat(date).hasDayOfMonth(15);

LocalDateTime dateTime = LocalDateTime.now();
assertThat(dateTime).isCloseTo(
    LocalDateTime.now(),
    within(1, ChronoUnit.SECONDS)
);
```

---

## Object Assertions

```java
// Recursive Comparison (für DTOs ohne equals)
assertThat(actual)
    .usingRecursiveComparison()
    .isEqualTo(expected);

// Felder ignorieren
assertThat(actual)
    .usingRecursiveComparison()
    .ignoringFields("id", "createdAt")
    .isEqualTo(expected);

// Nur bestimmte Felder
assertThat(actual)
    .usingRecursiveComparison()
    .comparingOnlyFields("name", "email")
    .isEqualTo(expected);

// Field by Field
assertThat(actual)
    .hasFieldOrPropertyWithValue("name", "Max")
    .hasFieldOrPropertyWithValue("age", 25);
```

---

## Custom Conditions

```java
// Inline Condition
Condition<String> startsWithA = new Condition<>(
    s -> s.startsWith("A"),
    "starts with A"
);

assertThat("Anna").is(startsWithA);
assertThat(List.of("Anna", "Alex")).are(startsWithA);
assertThat(List.of("Anna", "Bob")).areAtLeast(1, startsWithA);

// Reusable Condition
public class AdultCondition extends Condition<Person> {
    public AdultCondition() {
        super(p -> p.getAge() >= 18, "adult (age >= 18)");
    }
}

assertThat(person).is(new AdultCondition());
```

---

## File Assertions

```java
File file = new File("test.txt");

assertThat(file).exists();
assertThat(file).doesNotExist();
assertThat(file).isFile();
assertThat(file).isDirectory();
assertThat(file).canRead();
assertThat(file).canWrite();

assertThat(file).hasName("test.txt");
assertThat(file).hasExtension("txt");
assertThat(file).hasParent("src/test/resources");

// Inhalt
assertThat(file).hasContent("expected content");
assertThat(file).hasSameContentAs(new File("expected.txt"));

// Path
Path path = Paths.get("test.txt");
assertThat(path).exists();
assertThat(path).isRegularFile();
assertThat(path).hasFileName("test.txt");
```
