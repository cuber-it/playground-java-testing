# JUnit 5 Extensions Snippets

Copy-Paste fertige Code-Blöcke für eigene JUnit 5 Extensions.

---

## Extension Grundgerüst

```java
import org.junit.jupiter.api.extension.*;

public class MyExtension implements
    BeforeAllCallback,
    AfterAllCallback,
    BeforeEachCallback,
    AfterEachCallback,
    BeforeTestExecutionCallback,
    AfterTestExecutionCallback {

    @Override
    public void beforeAll(ExtensionContext context) {
        System.out.println("Before all tests in: " + context.getDisplayName());
    }

    @Override
    public void afterAll(ExtensionContext context) {
        System.out.println("After all tests");
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        System.out.println("Before: " + context.getDisplayName());
    }

    @Override
    public void afterEach(ExtensionContext context) {
        System.out.println("After: " + context.getDisplayName());
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        System.out.println("Executing: " + context.getDisplayName());
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        System.out.println("Executed: " + context.getDisplayName());
    }
}
```

**Verwendung:**

```java
@ExtendWith(MyExtension.class)
class MyTest {
    @Test
    void test() { }
}
```

---

## Timing Extension

```java
public class TimingExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private static final String START_TIME = "start_time";

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        getStore(context).put(START_TIME, System.currentTimeMillis());
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        long startTime = getStore(context).remove(START_TIME, long.class);
        long duration = System.currentTimeMillis() - startTime;

        System.out.printf("Test '%s' took %d ms%n",
            context.getDisplayName(), duration);

        // Optional: Fail if too slow
        if (duration > 1000) {
            System.err.println("WARNING: Test took longer than 1 second!");
        }
    }

    private ExtensionContext.Store getStore(ExtensionContext context) {
        return context.getStore(ExtensionContext.Namespace.create(
            getClass(), context.getRequiredTestMethod()));
    }
}
```

---

## Retry Extension (Flaky Tests)

```java
// Annotation
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RetryOnFailure {
    int maxAttempts() default 3;
}

// Extension
public class RetryExtension implements TestExecutionExceptionHandler {

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable)
            throws Throwable {

        RetryOnFailure retry = context.getRequiredTestMethod()
            .getAnnotation(RetryOnFailure.class);

        if (retry == null) {
            throw throwable;
        }

        int maxAttempts = retry.maxAttempts();
        ExtensionContext.Store store = getStore(context);
        int attempts = store.getOrDefault("attempts", Integer.class, 1);

        if (attempts < maxAttempts) {
            store.put("attempts", attempts + 1);
            System.out.printf("Retry attempt %d/%d for: %s%n",
                attempts + 1, maxAttempts, context.getDisplayName());

            // Re-run test
            context.getRequiredTestMethod().invoke(context.getRequiredTestInstance());
        } else {
            throw throwable;
        }
    }

    private ExtensionContext.Store getStore(ExtensionContext context) {
        return context.getStore(ExtensionContext.Namespace.create(
            getClass(), context.getRequiredTestMethod()));
    }
}
```

**Verwendung:**

```java
@ExtendWith(RetryExtension.class)
class FlakyTest {

    @Test
    @RetryOnFailure(maxAttempts = 3)
    void flakyTest() {
        // May fail sometimes
    }
}
```

---

## Parameter Resolver - Custom Injection

```java
// Annotation
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RandomInt {
    int min() default 0;
    int max() default 100;
}

// Extension
public class RandomIntExtension implements ParameterResolver {

    private final Random random = new Random();

    @Override
    public boolean supportsParameter(ParameterContext paramCtx, ExtensionContext extCtx) {
        return paramCtx.isAnnotated(RandomInt.class)
            && paramCtx.getParameter().getType() == int.class;
    }

    @Override
    public Object resolveParameter(ParameterContext paramCtx, ExtensionContext extCtx) {
        RandomInt annotation = paramCtx.findAnnotation(RandomInt.class).orElseThrow();
        return random.nextInt(annotation.min(), annotation.max() + 1);
    }
}
```

**Verwendung:**

```java
@ExtendWith(RandomIntExtension.class)
class RandomTest {

    @Test
    void testWithRandomInt(@RandomInt(min = 1, max = 10) int value) {
        assertThat(value).isBetween(1, 10);
    }
}
```

---

## Database Cleanup Extension

```java
public class DatabaseCleanupExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        // Get Spring context
        ApplicationContext appContext = SpringExtension.getApplicationContext(context);

        // Get DataSource
        DataSource dataSource = appContext.getBean(DataSource.class);

        // Clean tables
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM order_items");
            stmt.execute("DELETE FROM orders");
            stmt.execute("DELETE FROM users");
        }
    }
}
```

---

## Conditional Execution Extension

```java
// Annotation
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(DockerRequiredCondition.class)
public @interface RequiresDocker { }

// Condition
public class DockerRequiredCondition implements ExecutionCondition {

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        if (isDockerAvailable()) {
            return ConditionEvaluationResult.enabled("Docker is available");
        }
        return ConditionEvaluationResult.disabled("Docker is not available");
    }

    private boolean isDockerAvailable() {
        try {
            Process process = Runtime.getRuntime().exec("docker info");
            return process.waitFor() == 0;
        } catch (Exception e) {
            return false;
        }
    }
}
```

**Verwendung:**

```java
@RequiresDocker
class DockerTest {

    @Test
    void testWithDocker() {
        // Only runs if Docker is available
    }
}
```

---

## Test Instance PostProcessor

```java
public class LoggerInjectionExtension implements TestInstancePostProcessor {

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
        for (Field field : testInstance.getClass().getDeclaredFields()) {
            if (field.getType() == Logger.class) {
                field.setAccessible(true);
                try {
                    Logger logger = LoggerFactory.getLogger(testInstance.getClass());
                    field.set(testInstance, logger);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
```

**Verwendung:**

```java
@ExtendWith(LoggerInjectionExtension.class)
class MyTest {

    private Logger log;  // Wird automatisch injiziert

    @Test
    void test() {
        log.info("Test running");
    }
}
```

---

## Test Watcher - Reporting

```java
public class TestResultLogger implements TestWatcher {

    @Override
    public void testSuccessful(ExtensionContext context) {
        log("PASSED", context);
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        log("FAILED", context);
        System.err.println("  Cause: " + cause.getMessage());
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        log("ABORTED", context);
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        log("DISABLED", context);
        reason.ifPresent(r -> System.out.println("  Reason: " + r));
    }

    private void log(String status, ExtensionContext context) {
        System.out.printf("[%s] %s::%s%n",
            status,
            context.getRequiredTestClass().getSimpleName(),
            context.getDisplayName());
    }
}
```

---

## Composite Extension

```java
// Kombiniert mehrere Extensions
public class IntegrationTestExtension implements
    BeforeAllCallback,
    AfterAllCallback,
    ParameterResolver {

    private WireMockServer wireMock;

    @Override
    public void beforeAll(ExtensionContext context) {
        wireMock = new WireMockServer(8089);
        wireMock.start();
    }

    @Override
    public void afterAll(ExtensionContext context) {
        wireMock.stop();
    }

    @Override
    public boolean supportsParameter(ParameterContext paramCtx, ExtensionContext extCtx) {
        return paramCtx.getParameter().getType() == WireMockServer.class;
    }

    @Override
    public Object resolveParameter(ParameterContext paramCtx, ExtensionContext extCtx) {
        return wireMock;
    }
}
```

**Verwendung:**

```java
@ExtendWith(IntegrationTestExtension.class)
class IntegrationTest {

    @Test
    void test(WireMockServer wireMock) {
        wireMock.stubFor(get("/api").willReturn(ok()));
        // ...
    }
}
```

---

## Meta-Annotation mit Extension

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(TimingExtension.class)
@ExtendWith(TestResultLogger.class)
@ExtendWith(DatabaseCleanupExtension.class)
@SpringBootTest
@Transactional
public @interface IntegrationTest { }
```

**Verwendung:**

```java
@IntegrationTest
class UserServiceTest {

    @Test
    void test() {
        // Alle Extensions und Annotations aktiv
    }
}
```

---

## Store für Extension-State

```java
public class SharedResourceExtension implements BeforeAllCallback, ParameterResolver {

    private static final ExtensionContext.Namespace NAMESPACE =
        ExtensionContext.Namespace.create(SharedResourceExtension.class);

    @Override
    public void beforeAll(ExtensionContext context) {
        // Resource nur einmal erstellen (für alle Tests)
        context.getRoot().getStore(NAMESPACE)
            .getOrComputeIfAbsent("resource", k -> createExpensiveResource());
    }

    @Override
    public boolean supportsParameter(ParameterContext paramCtx, ExtensionContext extCtx) {
        return paramCtx.getParameter().getType() == ExpensiveResource.class;
    }

    @Override
    public Object resolveParameter(ParameterContext paramCtx, ExtensionContext extCtx) {
        return extCtx.getRoot().getStore(NAMESPACE).get("resource");
    }

    private ExpensiveResource createExpensiveResource() {
        System.out.println("Creating expensive resource...");
        return new ExpensiveResource();
    }
}
```
