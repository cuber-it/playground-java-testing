# Mutation Testing - Testqualitaet pruefen

> **Hinweis:** Nicht im Playground implementiert, aber wertvolles Werkzeug.

## Was ist Mutation Testing?

Prueft, ob Tests **wirklich Fehler finden**. Das Tool veraendert (mutiert) den Code und schaut, ob Tests fehlschlagen.

## Das Problem mit Code Coverage

```java
// 100% Line Coverage, aber...
public boolean isAdult(int age) {
    return age >= 18;
}

@Test
void testIsAdult() {
    assertTrue(isAdult(25));  // Nur ein Fall!
}
```

**Coverage: 100%** - aber was wenn jemand `>=` zu `>` aendert?

## Mutation Testing findet das

```java
// Original
return age >= 18;

// Mutant 1: Boundary
return age > 18;   // Test muss fehlschlagen!

// Mutant 2: Return value
return false;      // Test muss fehlschlagen!

// Mutant 3: Negation
return age < 18;   // Test muss fehlschlagen!
```

**Ergebnis:** Test erkennt Mutant 2 und 3, aber NICHT Mutant 1!

## PIT (PITest) - Das Tool

```xml
<!-- pom.xml -->
<plugin>
    <groupId>org.pitest</groupId>
    <artifactId>pitest-maven</artifactId>
    <version>1.17.0</version>
    <dependencies>
        <dependency>
            <groupId>org.pitest</groupId>
            <artifactId>pitest-junit5-plugin</artifactId>
            <version>1.2.1</version>
        </dependency>
    </dependencies>
    <configuration>
        <targetClasses>
            <param>de.training.playground.*</param>
        </targetClasses>
        <targetTests>
            <param>de.training.playground.*</param>
        </targetTests>
    </configuration>
</plugin>
```

## Ausfuehrung

```bash
mvn pitest:mutationCoverage

# Report in: target/pit-reports/index.html
```

## Report verstehen

```
================================================================================
- Statistics
================================================================================
>> Line Coverage: 85% (170/200)
>> Mutation Coverage: 72% (144/200)
>> Test Strength: 85% (144/170)

================================================================================
- Mutants
================================================================================
>> KILLED: 144
>> SURVIVED: 26    <-- Diese sind problematisch!
>> NO_COVERAGE: 30
```

### Mutation Score

```
Mutation Score = Killed Mutants / Total Mutants

72% = 144 / 200
```

**Ziel:** > 80% Mutation Score

## Typische Mutatoren

| Mutator | Original | Mutation |
|---------|----------|----------|
| Conditionals | `>=` | `>`, `<`, `==` |
| Math | `+` | `-`, `*`, `/` |
| Negate | `true` | `false` |
| Return | `return x` | `return 0/null` |
| Void | `method()` | (entfernt) |
| Increment | `i++` | `i--` |

## Beispiel: Surviving Mutant

```java
// Code
public void markDone() {
    this.done = true;
    this.completedAt = LocalDateTime.now();  // <-- Mutant: Zeile entfernen
}

// Test - UNZUREICHEND!
@Test
void markDone() {
    todo.markDone();
    assertTrue(todo.isDone());  // completedAt wird nicht geprueft!
}

// Besserer Test
@Test
void markDoneSetsTimestamp() {
    todo.markDone();
    assertTrue(todo.isDone());
    assertThat(todo.getCompletedAt()).isNotNull();  // Jetzt wird Mutant getoetet
}
```

## Vorteile

- **Findet schwache Tests** die Coverage vortaeuschen
- **Objektive Metrik** fuer Testqualitaet
- **Zeigt konkrete Luecken** im Report

## Nachteile

- **Langsam** - jeder Mutant braucht Test-Durchlauf
- **Aequivalente Mutanten** - manche Aenderungen sind semantisch gleich
- **Aufwand** - alle Survivors zu fixen kann dauern

## Konfiguration fuer CI/CD

```xml
<configuration>
    <!-- Nur bei Nacht laufen lassen -->
    <mutationThreshold>75</mutationThreshold>
    <coverageThreshold>80</coverageThreshold>

    <!-- Schneller: weniger Mutatoren -->
    <mutators>
        <mutator>DEFAULTS</mutator>
    </mutators>

    <!-- Parallelisierung -->
    <threads>4</threads>
</configuration>
```

## Best Practices

1. **Nicht fuer 100% optimieren** - 80% ist gut
2. **Langsam einfuehren** - erstmal nur kritische Klassen
3. **Nightly Builds** - zu langsam fuer jeden Commit
4. **Survivors analysieren** - nicht blind Tests schreiben
5. **Mit Coverage kombinieren** - beide Metriken nutzen
