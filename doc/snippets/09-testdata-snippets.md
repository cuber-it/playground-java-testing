# Testdaten Snippets

Copy-Paste fertige Code-Blöcke für Builder Pattern und Object Mother.

---

## Builder Pattern - Grundgerüst

```java
public class TodoBuilder {

    private Long id = 1L;
    private String title = "Default Title";
    private boolean done = false;
    private LocalDateTime createdAt = LocalDateTime.now();
    private User assignee = null;

    // Private constructor - nur über static method
    private TodoBuilder() {}

    public static TodoBuilder aTodo() {
        return new TodoBuilder();
    }

    // Fluent Setter
    public TodoBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public TodoBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public TodoBuilder done() {
        this.done = true;
        return this;
    }

    public TodoBuilder notDone() {
        this.done = false;
        return this;
    }

    public TodoBuilder withCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public TodoBuilder assignedTo(User assignee) {
        this.assignee = assignee;
        return this;
    }

    // Build Method
    public Todo build() {
        Todo todo = new Todo();
        todo.setId(id);
        todo.setTitle(title);
        todo.setDone(done);
        todo.setCreatedAt(createdAt);
        todo.setAssignee(assignee);
        return todo;
    }
}
```

**Verwendung:**

```java
@Test
void shouldProcessTodo() {
    Todo todo = aTodo()
        .withTitle("Important Task")
        .done()
        .build();

    // ...
}
```

---

## Builder mit vorgefertigten Varianten

```java
public class TodoBuilder {

    // ... fields und setter wie oben ...

    // Vorgefertigte Konfigurationen
    public static TodoBuilder aTodo() {
        return new TodoBuilder();
    }

    public static TodoBuilder aCompletedTodo() {
        return new TodoBuilder()
            .withTitle("Completed Task")
            .done();
    }

    public static TodoBuilder anUrgentTodo() {
        return new TodoBuilder()
            .withTitle("URGENT: ")
            .notDone();
    }

    public static TodoBuilder aMinimalTodo() {
        return new TodoBuilder()
            .withId(null)
            .withTitle("Minimal");
    }
}
```

**Verwendung:**

```java
Todo completed = aCompletedTodo().build();
Todo urgent = anUrgentTodo().withTitle("URGENT: Fix bug").build();
```

---

## Builder mit Record (Java 16+)

```java
public record TodoBuilder(
    Long id,
    String title,
    boolean done
) {
    public TodoBuilder() {
        this(1L, "Default", false);
    }

    public TodoBuilder withId(Long id) {
        return new TodoBuilder(id, title, done);
    }

    public TodoBuilder withTitle(String title) {
        return new TodoBuilder(id, title, done);
    }

    public TodoBuilder done() {
        return new TodoBuilder(id, title, true);
    }

    public Todo build() {
        return new Todo(id, title, done);
    }

    public static TodoBuilder aTodo() {
        return new TodoBuilder();
    }
}
```

---

## Object Mother Pattern

```java
public class TodoMother {

    public static Todo simple() {
        return new Todo(1L, "Simple Todo", false);
    }

    public static Todo completed() {
        Todo todo = new Todo(2L, "Completed Todo", true);
        todo.setCompletedAt(LocalDateTime.now());
        return todo;
    }

    public static Todo withTitle(String title) {
        return new Todo(null, title, false);
    }

    public static Todo urgent() {
        Todo todo = new Todo(3L, "URGENT: Action Required", false);
        todo.setPriority(Priority.HIGH);
        todo.setDueDate(LocalDate.now());
        return todo;
    }

    public static Todo overdue() {
        Todo todo = new Todo(4L, "Overdue Task", false);
        todo.setDueDate(LocalDate.now().minusDays(7));
        return todo;
    }

    public static List<Todo> list(int count) {
        return IntStream.range(0, count)
            .mapToObj(i -> new Todo((long) i, "Todo " + i, false))
            .toList();
    }

    public static List<Todo> mixedList() {
        return List.of(
            simple(),
            completed(),
            urgent()
        );
    }
}
```

**Verwendung:**

```java
@Test
void shouldProcessTodo() {
    Todo todo = TodoMother.urgent();
    // ...
}

@Test
void shouldHandleList() {
    List<Todo> todos = TodoMother.list(5);
    // ...
}
```

---

## Kombinierter Ansatz: Mother + Builder

```java
public class Todos {

    // Quick access (Object Mother style)
    public static Todo simple() {
        return builder().build();
    }

    public static Todo completed() {
        return builder().done().build();
    }

    // Builder access
    public static TodoBuilder builder() {
        return new TodoBuilder();
    }

    // Nested Builder
    public static class TodoBuilder {
        private Long id = 1L;
        private String title = "Test Todo";
        private boolean done = false;

        public TodoBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public TodoBuilder withTitle(String title) {
            this.title = title;
            return this;
        }

        public TodoBuilder done() {
            this.done = true;
            return this;
        }

        public Todo build() {
            return new Todo(id, title, done);
        }
    }
}
```

**Verwendung:**

```java
// Schnell
Todo todo = Todos.simple();
Todo done = Todos.completed();

// Detailliert
Todo custom = Todos.builder()
    .withTitle("Custom")
    .done()
    .build();
```

---

## Builder für komplexe Objekte

```java
public class OrderBuilder {

    private Long id = 1L;
    private Customer customer = CustomerBuilder.aCustomer().build();
    private List<OrderItem> items = new ArrayList<>();
    private OrderStatus status = OrderStatus.PENDING;
    private LocalDateTime createdAt = LocalDateTime.now();

    public static OrderBuilder anOrder() {
        return new OrderBuilder();
    }

    public OrderBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public OrderBuilder forCustomer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public OrderBuilder forCustomer(CustomerBuilder customerBuilder) {
        this.customer = customerBuilder.build();
        return this;
    }

    public OrderBuilder withItem(OrderItem item) {
        this.items.add(item);
        return this;
    }

    public OrderBuilder withItem(OrderItemBuilder itemBuilder) {
        this.items.add(itemBuilder.build());
        return this;
    }

    public OrderBuilder withItems(OrderItem... items) {
        this.items.addAll(Arrays.asList(items));
        return this;
    }

    public OrderBuilder withStatus(OrderStatus status) {
        this.status = status;
        return this;
    }

    public OrderBuilder completed() {
        this.status = OrderStatus.COMPLETED;
        return this;
    }

    public Order build() {
        Order order = new Order();
        order.setId(id);
        order.setCustomer(customer);
        order.setItems(new ArrayList<>(items));
        order.setStatus(status);
        order.setCreatedAt(createdAt);
        return order;
    }
}
```

**Verwendung:**

```java
Order order = anOrder()
    .forCustomer(aCustomer().withName("Max"))
    .withItem(anOrderItem().withProduct("Book").withQuantity(2))
    .withItem(anOrderItem().withProduct("Pen").withQuantity(5))
    .completed()
    .build();
```

---

## Random Test Data

```java
public class RandomTestData {

    private static final Random random = new Random();
    private static final Faker faker = new Faker();  // JavaFaker library

    public static String randomString(int length) {
        return random.ints('a', 'z' + 1)
            .limit(length)
            .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
            .toString();
    }

    public static String randomEmail() {
        return randomString(8) + "@example.com";
    }

    public static Long randomId() {
        return random.nextLong(1, 10000);
    }

    // Mit JavaFaker
    public static String randomName() {
        return faker.name().fullName();
    }

    public static String randomAddress() {
        return faker.address().fullAddress();
    }

    public static Todo randomTodo() {
        return new Todo(
            randomId(),
            faker.lorem().sentence(),
            random.nextBoolean()
        );
    }
}
```

---

## JSON Test Data

```java
public class JsonTestData {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String todoJson() {
        return """
            {
                "id": 1,
                "title": "Test Todo",
                "done": false
            }
            """;
    }

    public static String todoJson(String title) {
        return """
            {
                "title": "%s",
                "done": false
            }
            """.formatted(title);
    }

    public static String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(String json, Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
```

---

## Test Fixtures mit @TempDir

```java
class FileProcessorTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldReadFile() throws IOException {
        // Arrange
        Path testFile = tempDir.resolve("test.txt");
        Files.writeString(testFile, "Test content");

        // Act
        String content = processor.read(testFile);

        // Assert
        assertThat(content).isEqualTo("Test content");
    }

    @Test
    void shouldWriteFile() {
        Path outputFile = tempDir.resolve("output.txt");

        processor.write(outputFile, "Hello");

        assertThat(outputFile).exists();
        assertThat(outputFile).hasContent("Hello");
    }
}
```

---

## Test Resources laden

```java
public class TestResources {

    public static String loadJson(String filename) {
        try {
            return Files.readString(
                Path.of(TestResources.class
                    .getResource("/testdata/" + filename)
                    .toURI())
            );
        } catch (Exception e) {
            throw new RuntimeException("Could not load: " + filename, e);
        }
    }

    public static InputStream loadStream(String filename) {
        return TestResources.class
            .getResourceAsStream("/testdata/" + filename);
    }

    public static <T> T loadJsonAs(String filename, Class<T> type) {
        try {
            return new ObjectMapper().readValue(
                loadJson(filename), type
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
```

**Verwendung:**

```java
String json = TestResources.loadJson("todo.json");
Todo todo = TestResources.loadJsonAs("todo.json", Todo.class);
```
