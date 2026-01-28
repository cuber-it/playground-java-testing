# JUnit 5 Cheatsheet

## Dependency

**Maven:**
```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.11.0</version>
    <scope>test</scope>
</dependency>

<!-- Für Parameterized Tests -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-params</artifactId>
    <version>5.11.0</version>
    <scope>test</scope>
</dependency>
```

**Gradle:**
```groovy
testImplementation 'org.junit.jupiter:junit-jupiter:5.11.0'
testImplementation 'org.junit.jupiter:junit-jupiter-params:5.11.0'
```

## Annotationen

```java
@Test                     // Testmethode
@DisplayName("...")       // Lesbarer Name
@Disabled("grund")        // Test überspringen
@BeforeEach              // Vor jedem Test
@AfterEach               // Nach jedem Test
@BeforeAll               // Einmal vor allen (static)
@AfterAll                // Einmal nach allen (static)
@Nested                  // Gruppierte Tests
@Tag("integration")      // Test kategorisieren
@Timeout(5)              // Max 5 Sekunden
@RepeatedTest(3)         // 3x wiederholen
```

## Parameterisierte Tests

### @ValueSource - Einfache Werte

```java
@ParameterizedTest
@ValueSource(strings = {"a", "b", "c"})
void testStrings(String value) { }

@ParameterizedTest
@ValueSource(ints = {1, 2, 3})
void testInts(int value) { }

@ParameterizedTest
@ValueSource(longs = {1L, 2L, 3L})
void testLongs(long value) { }

@ParameterizedTest
@ValueSource(doubles = {1.0, 2.5, 3.14})
void testDoubles(double value) { }

@ParameterizedTest
@ValueSource(booleans = {true, false})
void testBooleans(boolean value) { }

@ParameterizedTest
@ValueSource(classes = {String.class, Integer.class})
void testClasses(Class<?> clazz) { }
```

### Null und Empty Sources

```java
@ParameterizedTest
@NullSource                    // null
void testNull(String value) { }

@ParameterizedTest
@EmptySource                   // "" oder leere Collection
void testEmpty(String value) { }

@ParameterizedTest
@NullAndEmptySource            // null und ""
void testNullAndEmpty(String value) { }

// Kombinierbar mit ValueSource
@ParameterizedTest
@NullAndEmptySource
@ValueSource(strings = {"a", "b"})
void testAll(String value) { }  // null, "", "a", "b"
```

### @CsvSource - Mehrere Parameter

```java
@ParameterizedTest
@CsvSource({
    "1, true",
    "0, false",
    "-1, false"
})
void testCsv(int input, boolean expected) { }

// Mit Strings (Quotes)
@ParameterizedTest
@CsvSource({
    "Hello, 5",
    "'Hello, World', 12",       // Komma in String
    "\"quoted\", 6"             // Anführungszeichen
})
void testWithStrings(String text, int length) { }

// Null-Werte
@ParameterizedTest
@CsvSource({
    "1, ",                      // Leerstring
    "2, ''",                    // Leerstring explizit
    "3, null"                   // null (nullValues default)
})
void testNullValues(int id, String name) { }

// Custom Delimiter
@ParameterizedTest
@CsvSource(value = {"1|true", "0|false"}, delimiter = '|')
void testDelimiter(int input, boolean expected) { }

// Null-Wert anpassen
@ParameterizedTest
@CsvSource(value = {"1,N/A"}, nullValues = "N/A")
void testCustomNull(int id, String name) { }
```

### @CsvFileSource - Externe CSV-Datei

```java
@ParameterizedTest
@CsvFileSource(resources = "/test-data.csv")
void testFromFile(String name, int age) { }

@ParameterizedTest
@CsvFileSource(
    resources = "/test-data.csv",
    numLinesToSkip = 1,         // Header überspringen
    delimiter = ';',
    encoding = "UTF-8"
)
void testWithOptions(String name, int age) { }
```

### @MethodSource - Dynamische Daten

```java
// Gleicher Klassenname
@ParameterizedTest
@MethodSource("stringProvider")
void testMethod(String value) { }

static Stream<String> stringProvider() {
    return Stream.of("a", "b", "c");
}

// Mehrere Parameter mit Arguments
@ParameterizedTest
@MethodSource("argumentsProvider")
void testMultipleArgs(String name, int age, boolean active) { }

static Stream<Arguments> argumentsProvider() {
    return Stream.of(
        Arguments.of("Max", 25, true),
        Arguments.of("Anna", 30, false)
    );
}

// Andere Klasse
@ParameterizedTest
@MethodSource("com.example.TestData#getStrings")
void testExternalSource(String value) { }

// Ohne Name = Methodenname wie Test
@ParameterizedTest
@MethodSource
void testWithData(String value) { }

static Stream<String> testWithData() {
    return Stream.of("a", "b");
}
```

### @EnumSource - Enum-Werte

```java
// Alle Werte
@ParameterizedTest
@EnumSource(Status.class)
void testAllEnums(Status status) { }

// Nur bestimmte
@ParameterizedTest
@EnumSource(value = Status.class, names = {"ACTIVE", "PENDING"})
void testSome(Status status) { }

// Ausschließen
@ParameterizedTest
@EnumSource(value = Status.class, mode = EXCLUDE, names = {"DELETED"})
void testExclude(Status status) { }

// Regex Pattern
@ParameterizedTest
@EnumSource(value = Status.class, mode = MATCH_ALL, names = "^.*ACTIVE$")
void testPattern(Status status) { }
```

### @ArgumentsSource - Eigener Provider

```java
@ParameterizedTest
@ArgumentsSource(MyArgumentsProvider.class)
void testCustom(String name, int age) { }

class MyArgumentsProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext ctx) {
        return Stream.of(
            Arguments.of("Max", 25),
            Arguments.of("Anna", 30)
        );
    }
}
```

### Implizite Typkonvertierung

```java
// String zu primitiven Typen
@ParameterizedTest
@ValueSource(strings = {"1", "2", "3"})
void testImplicit(int value) { }

// String zu Enum
@ParameterizedTest
@ValueSource(strings = {"ACTIVE", "PENDING"})
void testToEnum(Status status) { }

// String zu UUID, LocalDate, etc.
@ParameterizedTest
@ValueSource(strings = {"2024-01-15", "2024-06-30"})
void testToDate(LocalDate date) { }

// String zu File/Path
@ParameterizedTest
@ValueSource(strings = {"/tmp/test.txt"})
void testToPath(Path path) { }
```

### Explizite Typkonvertierung

```java
@ParameterizedTest
@ConvertWith(MyConverter.class)
@ValueSource(strings = {"custom:value"})
void testCustomConverter(MyObject obj) { }

class MyConverter extends SimpleArgumentConverter {
    @Override
    protected Object convert(Object source, Class<?> targetType) {
        return new MyObject(source.toString());
    }
}

// Oder als TypedArgumentConverter
class MyTypedConverter extends TypedArgumentConverter<String, MyObject> {
    protected MyTypedConverter() {
        super(String.class, MyObject.class);
    }
    @Override
    protected MyObject convert(String source) {
        return new MyObject(source);
    }
}
```

### Argument Aggregation

```java
@ParameterizedTest
@CsvSource({"Max, 25, true", "Anna, 30, false"})
void testAggregator(@AggregateWith(PersonAggregator.class) Person person) { }

class PersonAggregator implements ArgumentsAggregator {
    @Override
    public Object aggregateArguments(ArgumentsAccessor acc, ParameterContext ctx) {
        return new Person(
            acc.getString(0),
            acc.getInteger(1),
            acc.getBoolean(2)
        );
    }
}

// Oder direkt mit ArgumentsAccessor
@ParameterizedTest
@CsvSource({"Max, 25", "Anna, 30"})
void testAccessor(ArgumentsAccessor args) {
    String name = args.getString(0);
    int age = args.getInteger(1);
}
```

### DisplayName anpassen

```java
@ParameterizedTest(name = "{index}: {0} + {1} = {2}")
@CsvSource({"1, 2, 3", "5, 5, 10"})
void testAddition(int a, int b, int sum) { }

@ParameterizedTest(name = "[{index}] {displayName} with {arguments}")
@ValueSource(strings = {"a", "b"})
@DisplayName("Testing value")
void testNamed(String value) { }

// Placeholders:
// {index}        - Laufende Nummer (1-basiert)
// {arguments}    - Alle Argumente
// {0}, {1}, ...  - Einzelne Argumente
// {displayName}  - @DisplayName oder Methodenname
```

## Bedingte Ausführung

```java
@EnabledOnOs(OS.LINUX)
@DisabledOnOs(OS.WINDOWS)
@EnabledOnJre(JRE.JAVA_21)
@EnabledIfSystemProperty(named = "ci", matches = "true")
@EnabledIfEnvironmentVariable(named = "ENV", matches = "test")
```

## Assertions (JUnit)

```java
assertEquals(expected, actual);
assertNotEquals(a, b);
assertTrue(condition);
assertFalse(condition);
assertNull(obj);
assertNotNull(obj);
assertThrows(Exception.class, () -> code());
assertTimeout(Duration.ofSeconds(1), () -> code());
assertAll(
    () -> assertEquals(1, a),
    () -> assertEquals(2, b)
);
```

## Lifecycle

```
@BeforeAll  ──────────────────────┐
                                   │ einmal
@BeforeEach ────┐                 │
@Test           │ pro Test        │
@AfterEach  ────┘                 │
                                   │
@AfterAll   ──────────────────────┘
```

## Extensions

```java
@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
```
