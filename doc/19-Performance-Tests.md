# Performance Tests - Last und Stress

> **Hinweis:** Nicht im Playground implementiert, aber wichtig fuer Produktion.

## Testarten

| Art | Ziel | Dauer |
|-----|------|-------|
| **Load Test** | Normallast simulieren | Minuten |
| **Stress Test** | Grenzen finden | Bis Absturz |
| **Soak Test** | Langzeitstabilitaet | Stunden/Tage |
| **Spike Test** | Plotzliche Last | Kurz |

## Tools im Ueberblick

### JMeter (Apache)

- GUI + CLI
- Verbreitet, viele Plugins
- XML-basierte Testplaene

### Gatling

- Scala/Java DSL
- Moderne Reports
- Code-basierte Tests

### k6

- JavaScript
- Leichtgewichtig
- Cloud-native

## Gatling Beispiel

### Setup

```xml
<!-- pom.xml -->
<plugin>
    <groupId>io.gatling</groupId>
    <artifactId>gatling-maven-plugin</artifactId>
    <version>4.9.6</version>
</plugin>

<dependency>
    <groupId>io.gatling.highcharts</groupId>
    <artifactId>gatling-charts-highcharts</artifactId>
    <version>3.10.5</version>
    <scope>test</scope>
</dependency>
```

### Simulation

```java
// src/test/java/simulations/TodoSimulation.java
public class TodoSimulation extends Simulation {

    HttpProtocolBuilder httpProtocol = http
        .baseUrl("http://localhost:8080")
        .acceptHeader("application/json");

    ScenarioBuilder scenario = scenario("Todo API Load Test")
        .exec(
            http("Get all todos")
                .get("/api/todos")
                .check(status().is(200))
        )
        .pause(1)
        .exec(
            http("Create todo")
                .post("/api/todos")
                .header("Content-Type", "application/json")
                .body(StringBody("{\"title\": \"Load Test Todo\"}"))
                .check(status().is(200))
        );

    {
        setUp(
            scenario.injectOpen(
                rampUsers(100).during(Duration.ofSeconds(30))  // 100 User in 30s
            )
        ).protocols(httpProtocol);
    }
}
```

### Ausfuehrung

```bash
mvn gatling:test
# Report in: target/gatling/
```

## k6 Beispiel

```javascript
// load-test.js
import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '30s', target: 50 },   // Ramp-up
        { duration: '1m', target: 50 },    // Stay
        { duration: '30s', target: 0 },    // Ramp-down
    ],
    thresholds: {
        http_req_duration: ['p(95)<500'],  // 95% unter 500ms
        http_req_failed: ['rate<0.01'],    // Fehlerrate < 1%
    },
};

export default function () {
    const res = http.get('http://localhost:8080/api/todos');

    check(res, {
        'status is 200': (r) => r.status === 200,
        'response time < 200ms': (r) => r.timings.duration < 200,
    });

    sleep(1);
}
```

```bash
k6 run load-test.js
```

## Metriken

### Response Time

```
┌─────────────────────────────────┐
│  Response Time Distribution     │
├─────────────────────────────────┤
│  Min:     12ms                  │
│  Avg:     89ms                  │
│  p50:     75ms   (Median)       │
│  p90:    150ms                  │
│  p95:    230ms   <- Wichtig!    │
│  p99:    450ms                  │
│  Max:   1250ms                  │
└─────────────────────────────────┘
```

### Throughput

- **Requests/second** - wie viele Anfragen verarbeitet
- **Transactions/second** - komplette Geschaeftsvorgaenge

### Error Rate

- Sollte unter **1%** bleiben
- Bei Stress-Tests: wo bricht System ein?

## CI/CD Integration

```yaml
# GitHub Actions Beispiel
performance-test:
  runs-on: ubuntu-latest
  steps:
    - name: Start Application
      run: docker compose up -d

    - name: Wait for startup
      run: sleep 30

    - name: Run k6
      uses: grafana/k6-action@v0.3.1
      with:
        filename: load-test.js
        flags: --out json=results.json

    - name: Check thresholds
      run: |
        if grep -q '"thresholds":{".*":false}' results.json; then
          echo "Performance thresholds failed!"
          exit 1
        fi
```

## Ergebnisse interpretieren

### Gut

```
✓ http_req_duration....: avg=45ms  p(95)=120ms
✓ http_req_failed......: 0.00%
✓ iterations...........: 15000
```

### Problematisch

```
✗ http_req_duration....: avg=850ms p(95)=2500ms  <- Zu langsam
✗ http_req_failed......: 5.2%                    <- Zu viele Fehler
```

## Best Practices

1. **Realistische Szenarien** - nicht nur GET-Requests
2. **Produktionsaehnliche Umgebung** - nicht lokal testen
3. **Baseline etablieren** - was ist "normal"?
4. **Regelmaessig testen** - Performance-Regression frueh erkennen
5. **Monitoring integrieren** - CPU, Memory, DB waehrend Test
