# TestContainers - Echte Datenbanken in Tests

## Playground-Beispiele

```
src/test/java/de/training/playground/integration/testcontainers/
├── TodoPostgresContainerTest.java  # PostgreSQL mit Spring Boot
└── PostgresPlainJdbcTest.java      # PostgreSQL mit Plain JDBC
```

**Voraussetzung:** Docker muss laufen!

```bash
# TestContainers-Tests ausfuehren
./run-test.sh '*Container*'
./run-test.sh '*Postgres*'
```

---

## Was ist TestContainers?

Ein Framework, das **Docker-Container in Tests** startet. Ermoeglicht Tests gegen echte Datenbanken, Message Broker, etc.

## Warum TestContainers?

```
┌─────────────────┐     ┌─────────────────┐
│  H2 In-Memory   │     │  PostgreSQL     │
│  (Test)         │     │  (Produktion)   │
└────────┬────────┘     └────────┬────────┘
         │                       │
         └───── Unterschiede ────┘
               SQL-Dialekt
               Features
               Verhalten
```

**Problem:** H2 verhält sich anders als PostgreSQL/MySQL.

**Loesung:** TestContainers startet echte Datenbank in Docker.

## Setup

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <version>1.20.4</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <version>1.20.4</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>1.20.4</version>
    <scope>test</scope>
</dependency>
```

## Beispiel: PostgreSQL

```java
@SpringBootTest
@Testcontainers
class TodoPostgresTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private TodoRepository repository;

    @Test
    void saveAndFind() {
        Todo todo = new Todo();
        todo.setTitle("TestContainers Test");

        Todo saved = repository.save(todo);

        assertThat(saved.getId()).isNotNull();
    }
}
```

## Weitere Container

```java
// MySQL
@Container
static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8");

// MongoDB
@Container
static MongoDBContainer mongo = new MongoDBContainer("mongo:7");

// Redis
@Container
static GenericContainer<?> redis = new GenericContainer<>("redis:7")
    .withExposedPorts(6379);

// Kafka
@Container
static KafkaContainer kafka = new KafkaContainer(
    DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

// LocalStack (AWS)
@Container
static LocalStackContainer localstack = new LocalStackContainer(
    DockerImageName.parse("localstack/localstack:3"))
    .withServices(LocalStackContainer.Service.S3, LocalStackContainer.Service.SQS);
```

## Vorteile

| Aspekt | H2 | TestContainers |
|--------|-----|----------------|
| Geschwindigkeit | Schnell | Langsamer (Container-Start) |
| Realitaetsnähe | Gering | Hoch |
| SQL-Kompatibilitaet | Eingeschraenkt | Voll |
| Setup | Einfach | Docker noetig |

## Wann einsetzen?

- **Komplexe SQL-Queries** die H2 nicht unterstuetzt
- **Datenbank-spezifische Features** (JSON, Arrays, etc.)
- **Integrationstests** vor Produktion
- **Migrationen testen** (Flyway, Liquibase)

## Best Practices

1. **Singleton-Container** fuer schnellere Tests (nicht pro Test neu starten)
2. **Reusable Containers** in Entwicklung (`testcontainers.reuse.enable=true`)
3. **CI/CD:** Docker-in-Docker oder Podman konfigurieren
4. **Nur wo noetig** - H2 fuer einfache Tests weiterhin nutzen
