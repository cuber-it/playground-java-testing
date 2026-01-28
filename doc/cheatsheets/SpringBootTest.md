# Spring Boot Test Cheatsheet

## Dependency

**Maven:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- Enthält bereits: JUnit 5, Mockito, AssertJ, Hamcrest, JSONPath -->
```

**Gradle:**
```groovy
testImplementation 'org.springframework.boot:spring-boot-starter-test'
```

**Für Security Tests:**
```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
```

## Test-Annotationen

```java
// Voller Kontext
@SpringBootTest

// Mit Server
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)  // Default
@SpringBootTest(webEnvironment = WebEnvironment.NONE)

// Slice Tests
@WebMvcTest(TodoController.class)  // Nur Web-Layer
@DataJpaTest                        // Nur JPA
@RestClientTest                     // REST Clients
@JsonTest                           // JSON Serialisierung
```

## Wichtige Annotationen

```java
@Autowired           // Bean injizieren
@MockBean            // Mock im Spring-Kontext
@SpyBean             // Spy im Spring-Kontext
@Transactional       // Rollback nach Test
@Sql("data.sql")     // SQL vor Test ausführen
@ActiveProfiles("test")
@TestPropertySource(properties = "key=value")
@DirtiesContext      // Kontext neu laden (langsam!)
```

## @MockBean vs @Mock

```java
// @Mock - Unit Test (kein Spring)
@ExtendWith(MockitoExtension.class)
class ServiceTest {
    @Mock
    Repository repo;           // Mockito Mock
}

// @MockBean - Spring Test
@WebMvcTest
class ControllerTest {
    @MockBean
    Service service;           // Ersetzt echte Bean
}
```

## @WebMvcTest

```java
@WebMvcTest(TodoController.class)
class ControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TodoService service;

    @Test
    void getAll() throws Exception {
        when(service.findAll()).thenReturn(List.of(todo));

        mockMvc.perform(get("/api/todos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("Test"));
    }
}
```

## @DataJpaTest

```java
@DataJpaTest
class RepositoryTest {

    @Autowired
    TodoRepository repo;

    @Autowired
    TestEntityManager em;

    @Test
    void findByTitle() {
        em.persist(new Todo("Test"));
        em.flush();

        List<Todo> found = repo.findByTitleContaining("Test");
        assertThat(found).hasSize(1);
    }
}
```

## MockMvc

```java
// GET
mockMvc.perform(get("/api/todos"))
    .andExpect(status().isOk());

// GET mit Path-Variable
mockMvc.perform(get("/api/todos/{id}", 1))
    .andExpect(status().isOk());

// POST mit Body
mockMvc.perform(post("/api/todos")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"title\":\"Test\"}"))
    .andExpect(status().isCreated());

// PUT
mockMvc.perform(put("/api/todos/{id}", 1)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"title\":\"Updated\"}"))
    .andExpect(status().isOk());

// DELETE
mockMvc.perform(delete("/api/todos/{id}", 1))
    .andExpect(status().isNoContent());
```

## MockMvc Assertions

```java
.andExpect(status().isOk())              // 200
.andExpect(status().isCreated())         // 201
.andExpect(status().isNoContent())       // 204
.andExpect(status().isBadRequest())      // 400
.andExpect(status().isNotFound())        // 404
.andExpect(status().is5xxServerError())

.andExpect(content().contentType(MediaType.APPLICATION_JSON))
.andExpect(content().string("text"))

.andExpect(jsonPath("$.title").value("Test"))
.andExpect(jsonPath("$[0].id").exists())
.andExpect(jsonPath("$", hasSize(2)))
```

## TestRestTemplate

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class IntegrationTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void getAll() {
        ResponseEntity<String> response =
            restTemplate.getForEntity("/api/todos", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
```

## Port ermitteln

```java
@LocalServerPort
int port;

// oder
@Value("${local.server.port}")
int port;
```

## Properties überschreiben

```java
@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:test",
    "custom.property=value"
})
class Test { }

// oder
@SpringBootTest(properties = "key=value")
```

## SQL ausführen

```java
@Sql("/test-data.sql")
@Sql(scripts = "/cleanup.sql", executionPhase = AFTER_TEST_METHOD)
class Test { }
```

## Security

```java
@WithMockUser
@WithMockUser(username = "admin", roles = {"ADMIN"})
@WithAnonymousUser
```
