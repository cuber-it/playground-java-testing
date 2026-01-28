# TestContainers Snippets

Copy-Paste fertige Code-Blöcke für TestContainers.

---

## Dependency

**Maven:**
```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers</artifactId>
    <version>1.20.0</version>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>1.20.0</version>
    <scope>test</scope>
</dependency>

<!-- Datenbankspezifisch -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <version>1.20.0</version>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>mysql</artifactId>
    <version>1.20.0</version>
    <scope>test</scope>
</dependency>
```

**Gradle:**
```groovy
testImplementation 'org.testcontainers:testcontainers:1.20.0'
testImplementation 'org.testcontainers:junit-jupiter:1.20.0'
testImplementation 'org.testcontainers:postgresql:1.20.0'
```

---

## PostgreSQL Container

```java
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class PostgresTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    @Test
    void shouldConnectToPostgres() {
        String jdbcUrl = postgres.getJdbcUrl();
        String username = postgres.getUsername();
        String password = postgres.getPassword();

        // Use connection...
        assertThat(postgres.isRunning()).isTrue();
    }
}
```

---

## MySQL Container

```java
import org.testcontainers.containers.MySQLContainer;

@Testcontainers
class MySQLTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    @Test
    void shouldConnectToMySQL() {
        String jdbcUrl = mysql.getJdbcUrl();
        // ...
    }
}
```

---

## Mit Spring Boot

```java
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class UserRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    UserRepository userRepository;

    @Test
    void shouldSaveUser() {
        User user = new User("Max", "max@example.com");

        User saved = userRepository.save(user);

        assertThat(saved.getId()).isNotNull();
    }
}
```

---

## Singleton Container (Schneller)

```java
// Abstrakte Basisklasse für alle DB-Tests
public abstract class AbstractDatabaseTest {

    static final PostgreSQLContainer<?> postgres;

    static {
        postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
        postgres.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}

// Tests erben von der Basisklasse
@SpringBootTest
class UserRepositoryTest extends AbstractDatabaseTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void test() {
        // Container läuft bereits
    }
}
```

---

## Generic Container

```java
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
class RedisTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(
        DockerImageName.parse("redis:7"))
        .withExposedPorts(6379);

    @Test
    void shouldConnectToRedis() {
        String host = redis.getHost();
        Integer port = redis.getMappedPort(6379);

        // Connect to redis://host:port
    }
}
```

---

## Container mit Init-Script

```java
@Container
static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
    .withDatabaseName("testdb")
    .withUsername("test")
    .withPassword("test")
    .withInitScript("init.sql");  // src/test/resources/init.sql
```

**init.sql:**
```sql
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100)
);

INSERT INTO users (name, email) VALUES ('Test User', 'test@example.com');
```

---

## Kafka Container

```java
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
class KafkaTest {

    @Container
    static KafkaContainer kafka = new KafkaContainer(
        DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Test
    void shouldProduceAndConsume() {
        String bootstrapServers = kafka.getBootstrapServers();
        // ...
    }
}
```

---

## Elasticsearch Container

```java
import org.testcontainers.elasticsearch.ElasticsearchContainer;

@Testcontainers
class ElasticsearchTest {

    @Container
    static ElasticsearchContainer elastic = new ElasticsearchContainer(
        DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch:8.9.0"))
        .withEnv("xpack.security.enabled", "false");

    @Test
    void shouldIndexDocument() {
        String httpHost = elastic.getHttpHostAddress();
        // Connect to http://httpHost
    }
}
```

---

## MongoDB Container

```java
import org.testcontainers.containers.MongoDBContainer;

@Testcontainers
class MongoDBTest {

    @Container
    static MongoDBContainer mongo = new MongoDBContainer(
        DockerImageName.parse("mongo:6"));

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
    }

    @Test
    void shouldSaveDocument() {
        String connectionString = mongo.getReplicaSetUrl();
        // ...
    }
}
```

---

## LocalStack (AWS Services)

```java
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.*;

@Testcontainers
class S3Test {

    @Container
    static LocalStackContainer localstack = new LocalStackContainer(
        DockerImageName.parse("localstack/localstack:2.2"))
        .withServices(S3, SQS, SNS);

    @Test
    void shouldUploadToS3() {
        var s3Client = S3Client.builder()
            .endpointOverride(localstack.getEndpointOverride(S3))
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(
                    localstack.getAccessKey(),
                    localstack.getSecretKey())))
            .region(Region.of(localstack.getRegion()))
            .build();

        // Use S3 client...
    }
}
```

---

## Docker Compose

```java
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

@Testcontainers
class ComposeTest {

    @Container
    static DockerComposeContainer<?> compose = new DockerComposeContainer<>(
        new File("src/test/resources/docker-compose-test.yml"))
        .withExposedService("db", 5432,
            Wait.forListeningPort())
        .withExposedService("redis", 6379,
            Wait.forListeningPort());

    @Test
    void shouldStartServices() {
        String dbHost = compose.getServiceHost("db", 5432);
        Integer dbPort = compose.getServicePort("db", 5432);
        // ...
    }
}
```

---

## Wait Strategies

```java
// Port ist erreichbar
new GenericContainer<>("image")
    .waitingFor(Wait.forListeningPort());

// HTTP Endpoint
new GenericContainer<>("image")
    .waitingFor(Wait.forHttp("/health")
        .forStatusCode(200)
        .withStartupTimeout(Duration.ofSeconds(60)));

// Log Message
new GenericContainer<>("image")
    .waitingFor(Wait.forLogMessage(".*Ready.*\\n", 1));

// Kombiniert
new GenericContainer<>("image")
    .waitingFor(Wait.forListeningPort()
        .withStartupTimeout(Duration.ofMinutes(2)));
```

---

## Container mit Netzwerk

```java
import org.testcontainers.containers.Network;

@Testcontainers
class NetworkTest {

    static Network network = Network.newNetwork();

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withNetwork(network)
        .withNetworkAliases("db");

    @Container
    static GenericContainer<?> app = new GenericContainer<>("myapp:latest")
        .withNetwork(network)
        .withEnv("DB_HOST", "db")
        .withEnv("DB_PORT", "5432")
        .dependsOn(postgres);

    @Test
    void shouldCommunicate() {
        // app kann postgres über "db:5432" erreichen
    }
}
```

---

## Reusable Containers (Dev Mode)

```java
// Container wird nicht gestoppt - schnellere Entwicklung
@Container
static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
    .withReuse(true);
```

**In `~/.testcontainers.properties`:**
```properties
testcontainers.reuse.enable=true
```
