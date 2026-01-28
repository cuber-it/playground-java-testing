# Security Tests - Sicherheit pruefen

> **Hinweis:** Nicht im Playground implementiert, aber essentiell fuer Produktion.

## Testarten

| Art | Tool | Prueft |
|-----|------|--------|
| **SAST** | SonarQube, Checkmarx | Quellcode |
| **DAST** | OWASP ZAP, Burp | Laufende App |
| **SCA** | OWASP Dependency-Check | Abhaengigkeiten |
| **Secrets** | GitLeaks, TruffleHog | Credentials im Code |

## OWASP Top 10 (2021)

1. **Broken Access Control** - Berechtigungen
2. **Cryptographic Failures** - Verschluesselung
3. **Injection** - SQL, XSS, Command
4. **Insecure Design** - Architektur
5. **Security Misconfiguration** - Fehlkonfiguration
6. **Vulnerable Components** - Abhaengigkeiten
7. **Auth Failures** - Authentifizierung
8. **Data Integrity Failures** - Integritaet
9. **Logging Failures** - Monitoring
10. **SSRF** - Server-Side Request Forgery

## OWASP ZAP - Dynamic Testing

### Docker Setup

```yaml
# docker-compose.yml
services:
  zap:
    image: ghcr.io/zaproxy/zaproxy:stable
    command: zap-baseline.py -t http://app:8080 -r report.html
    volumes:
      - ./zap-reports:/zap/wrk
    depends_on:
      - app
```

### Baseline Scan

```bash
docker run -t ghcr.io/zaproxy/zaproxy:stable \
    zap-baseline.py -t http://localhost:8080
```

### API Scan

```bash
docker run -t ghcr.io/zaproxy/zaproxy:stable \
    zap-api-scan.py -t http://localhost:8080/v3/api-docs -f openapi
```

## OWASP Dependency-Check

### Maven Plugin

```xml
<plugin>
    <groupId>org.owasp</groupId>
    <artifactId>dependency-check-maven</artifactId>
    <version>10.0.4</version>
    <executions>
        <execution>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <failBuildOnCVSS>7</failBuildOnCVSS>
    </configuration>
</plugin>
```

### Ausfuehrung

```bash
mvn dependency-check:check
# Report in: target/dependency-check-report.html
```

### Beispiel-Report

```
Dependency          | CVE            | Severity
--------------------|----------------|----------
log4j-core:2.14.1   | CVE-2021-44228 | CRITICAL
spring-web:5.3.9    | CVE-2021-22118 | HIGH
jackson:2.12.0      | CVE-2020-36518 | MEDIUM
```

## Spring Security Tests

```java
@WebMvcTest(TodoController.class)
@WithMockUser(roles = "USER")
class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void authenticatedUserCanAccess() throws Exception {
        mockMvc.perform(get("/api/todos"))
            .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    void anonymousUserIsRejected() throws Exception {
        mockMvc.perform(get("/api/todos"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanDelete() throws Exception {
        mockMvc.perform(delete("/api/todos/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    void userCannotDelete() throws Exception {
        mockMvc.perform(delete("/api/todos/1"))
            .andExpect(status().isForbidden());
    }
}
```

## Secrets Detection

### GitLeaks

```bash
# Installation
brew install gitleaks

# Scan
gitleaks detect --source . --verbose

# In Pre-Commit Hook
gitleaks protect --staged
```

### Typische Findings

```
Finding: AWS Access Key
Secret:  AKIAIOSFODNN7EXAMPLE
File:    src/main/resources/application.properties
Line:    15

Finding: Database Password
Secret:  password123
File:    docker-compose.yml
Line:    8
```

## CI/CD Integration

```yaml
# GitHub Actions
security-scan:
  runs-on: ubuntu-latest
  steps:
    - uses: actions/checkout@v4

    # Dependency Check
    - name: OWASP Dependency Check
      uses: dependency-check/Dependency-Check_Action@main
      with:
        path: '.'
        format: 'HTML'

    # Secrets Scan
    - name: Gitleaks
      uses: gitleaks/gitleaks-action@v2

    # SAST mit SonarQube (bereits integriert)
    - name: SonarQube Scan
      run: mvn sonar:sonar

    # DAST mit ZAP
    - name: ZAP Scan
      uses: zaproxy/action-baseline@v0.12.0
      with:
        target: 'http://localhost:8080'
```

## Security Headers pruefen

```java
@Test
void securityHeaders() throws Exception {
    mockMvc.perform(get("/api/todos"))
        .andExpect(header().exists("X-Content-Type-Options"))
        .andExpect(header().string("X-Frame-Options", "DENY"))
        .andExpect(header().exists("X-XSS-Protection"))
        .andExpect(header().exists("Strict-Transport-Security"));
}
```

## Best Practices

1. **Shift Left** - Sicherheit frueh im Prozess
2. **Automatisieren** - in CI/CD Pipeline
3. **Abhaengigkeiten aktuell** - regelmaessig updaten
4. **Secrets nie im Code** - Environment Variables
5. **Least Privilege** - minimale Berechtigungen
6. **Defense in Depth** - mehrere Schichten
