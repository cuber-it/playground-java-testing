# Spring Boot Test Snippets

Copy-Paste fertige Code-Blöcke.

---

## @SpringBootTest - Voller Kontext

```java
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class MyServiceIntegrationTest {

    @Autowired
    MyService service;

    @Test
    void shouldDoSomething() {
        var result = service.process("input");

        assertThat(result).isNotNull();
    }
}
```

---

## @SpringBootTest mit echtem Server

```java
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class MyControllerIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void shouldReturnData() {
        var response = restTemplate.getForEntity("/api/data", String.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }
}
```

---

## @WebMvcTest - Controller Test

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TodoService service;

    @Test
    void shouldReturnAllTodos() throws Exception {
        when(service.findAll()).thenReturn(List.of(
            new Todo(1L, "Test")
        ));

        mockMvc.perform(get("/api/todos"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].title").value("Test"));
    }
}
```

---

## MockMvc - GET Request

```java
// Einfach
mockMvc.perform(get("/api/todos"))
    .andExpect(status().isOk());

// Mit Path-Variable
mockMvc.perform(get("/api/todos/{id}", 1))
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.id").value(1));

// Mit Query-Parameter
mockMvc.perform(get("/api/todos")
        .param("status", "active")
        .param("page", "0"))
    .andExpect(status().isOk());
```

---

## MockMvc - POST Request

```java
mockMvc.perform(post("/api/todos")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
                "title": "New Todo",
                "done": false
            }
            """))
    .andExpect(status().isCreated())
    .andExpect(jsonPath("$.id").exists())
    .andExpect(jsonPath("$.title").value("New Todo"));
```

---

## MockMvc - PUT Request

```java
mockMvc.perform(put("/api/todos/{id}", 1)
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {
                "title": "Updated",
                "done": true
            }
            """))
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.title").value("Updated"));
```

---

## MockMvc - DELETE Request

```java
mockMvc.perform(delete("/api/todos/{id}", 1))
    .andExpect(status().isNoContent());
```

---

## MockMvc - Assertions

```java
// Status
.andExpect(status().isOk())           // 200
.andExpect(status().isCreated())      // 201
.andExpect(status().isNoContent())    // 204
.andExpect(status().isBadRequest())   // 400
.andExpect(status().isNotFound())     // 404
.andExpect(status().is5xxServerError())

// Content-Type
.andExpect(content().contentType(MediaType.APPLICATION_JSON))

// JSON Path
.andExpect(jsonPath("$.title").value("Test"))
.andExpect(jsonPath("$.id").exists())
.andExpect(jsonPath("$.id").isNumber())
.andExpect(jsonPath("$").isArray())
.andExpect(jsonPath("$", hasSize(3)))
.andExpect(jsonPath("$[0].title").value("First"))

// Header
.andExpect(header().exists("Location"))
.andExpect(header().string("X-Custom", "value"))
```

---

## @DataJpaTest - Repository Test

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class TodoRepositoryTest {

    @Autowired
    TestEntityManager em;

    @Autowired
    TodoRepository repository;

    @Test
    void shouldFindByTitle() {
        // Arrange
        var todo = new Todo();
        todo.setTitle("Test Todo");
        em.persistAndFlush(todo);

        // Act
        var found = repository.findByTitleContaining("Test");

        // Assert
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getTitle()).isEqualTo("Test Todo");
    }

    @Test
    void shouldSaveEntity() {
        var todo = new Todo();
        todo.setTitle("New");

        var saved = repository.save(todo);

        assertThat(saved.getId()).isNotNull();
    }
}
```

---

## @MockBean vs @Mock

```java
// @MockBean - ersetzt Bean im Spring Context
@WebMvcTest(TodoController.class)
class ControllerTest {
    @MockBean
    TodoService service;  // Spring kennt diesen Mock
}

// @Mock - reiner Mockito Mock (kein Spring)
@ExtendWith(MockitoExtension.class)
class ServiceTest {
    @Mock
    TodoRepository repository;  // Spring weiß nichts davon

    @InjectMocks
    TodoService service;
}
```

---

## Test Properties überschreiben

```java
@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:test",
    "my.custom.property=testvalue"
})
class MyTest {
    // ...
}

// Oder mit Datei
@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
class MyTest {
    // ...
}
```

---

## @Sql - Daten laden

```java
@DataJpaTest
@Sql("/test-data.sql")
class RepositoryTest {

    @Test
    void shouldFindData() {
        // Daten aus test-data.sql sind verfügbar
    }
}

// Vor und nach Test
@Sql(scripts = "/setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
```

---

## Active Profile setzen

```java
@SpringBootTest
@ActiveProfiles("test")
class MyTest {
    // Nutzt application-test.properties/yml
}
```

---

## @Transactional - Rollback

```java
@SpringBootTest
@Transactional  // Rollback nach jedem Test
class IntegrationTest {

    @Autowired
    TodoRepository repository;

    @Test
    void shouldSaveAndRollback() {
        repository.save(new Todo("Test"));
        // Wird nach dem Test zurückgerollt
    }
}
```
