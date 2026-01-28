# Code Snippets & Live Templates

Dieses Verzeichnis enthält vorgefertigte Code-Snippets für verschiedene IDEs sowie Copy-Paste-fertige Codeblöcke.

---

## Verfügbare Snippets

### JUnit 5 & AssertJ
| Kürzel | Beschreibung |
|--------|--------------|
| `test` | JUnit 5 Test-Methode |
| `testclass` | JUnit 5 Test-Klasse mit Setup |
| `ptest` | Parameterized Test (ValueSource) |
| `ptestcsv` | Parameterized Test (CsvSource) |
| `assertthat` | AssertJ Assertion |
| `assertex` | AssertJ Exception-Test |
| `softassert` | AssertJ Soft Assertions |

### Mockito
| Kürzel | Beschreibung |
|--------|--------------|
| `mockclass` | Mockito Test-Klasse |
| `mock` | Mock-Feld |
| `whenthen` | when().thenReturn() |
| `verifym` | verify() |

### Spring Boot
| Kürzel | Beschreibung |
|--------|--------------|
| `sbtest` | Spring Boot Test-Klasse |
| `webmvc` | WebMvcTest-Klasse |
| `datajpa` | DataJpaTest-Klasse |

### REST & WireMock
| Kürzel | Beschreibung |
|--------|--------------|
| `raget` | REST Assured GET |
| `rapost` | REST Assured POST |
| `raclass` | REST Assured Test-Klasse |
| `wmstub` | WireMock Stub |
| `wmclass` | WireMock Test-Klasse |

### ArchUnit
| Kürzel | Beschreibung |
|--------|--------------|
| `archclass` | ArchUnit Test-Klasse mit Layer-Check |
| `archrule` | ArchUnit Regel |

### Allure
| Kürzel | Beschreibung |
|--------|--------------|
| `alluretest` | Test mit Allure Annotationen |
| `allurestep` | Allure @Step Methode |

### TestContainers
| Kürzel | Beschreibung |
|--------|--------------|
| `tcpostgres` | PostgreSQL Container |
| `tcdynamic` | @DynamicPropertySource |

### Testdaten
| Kürzel | Beschreibung |
|--------|--------------|
| `builder` | Test Data Builder Klasse |

---

## Installation

### IntelliJ IDEA

1. **File** → **Manage IDE Settings** → **Import Settings...**
2. Datei `intellij-live-templates.xml` auswählen
3. IDE neu starten

**Oder manuell:**

1. **File** → **Settings** → **Editor** → **Live Templates**
2. Rechtsklick → **Import**
3. XML-Datei auswählen

**Verwendung:** Kürzel tippen (z.B. `test`) + **Tab**

---

### Eclipse

1. **Window** → **Preferences**
2. **Java** → **Editor** → **Templates**
3. **Import...** klicken
4. Datei `eclipse-templates.xml` auswählen
5. **Apply and Close**

**Verwendung:** Kürzel tippen (z.B. `test`) + **Ctrl+Space**

---

### VS Code

**Option A: Projekt-Snippets (empfohlen)**

1. Datei `vscode.code-snippets` kopieren nach:
   ```
   .vscode/testing.code-snippets
   ```
2. Fertig - funktioniert sofort im Projekt

**Option B: Globale Snippets**

1. **Ctrl+Shift+P** → "Snippets: Configure User Snippets"
2. **New Global Snippets file...** wählen
3. Inhalt von `vscode.code-snippets` einfügen

**Verwendung:** Kürzel tippen (z.B. `test`) + **Tab** oder **Ctrl+Space**

---

## Copy-Paste Snippets

Falls keine IDE-Integration gewünscht: Siehe die Markdown-Dateien in diesem Verzeichnis für sofort einsatzbereite Code-Blöcke.

### Grundlagen
- [01-junit5-snippets.md](01-junit5-snippets.md) - JUnit 5 Grundgerüste
- [02-mockito-snippets.md](02-mockito-snippets.md) - Mockito Patterns
- [03-spring-snippets.md](03-spring-snippets.md) - Spring Boot Tests

### API & E2E
- [04-rest-snippets.md](04-rest-snippets.md) - REST Assured & WireMock
- [05-e2e-snippets.md](05-e2e-snippets.md) - Selenide & Playwright

### Erweitert
- [06-assertj-snippets.md](06-assertj-snippets.md) - AssertJ erweitert (Collections, Soft Assertions, Exceptions)
- [07-archunit-snippets.md](07-archunit-snippets.md) - Architektur-Tests
- [08-pact-snippets.md](08-pact-snippets.md) - Contract Testing

### Patterns & Tools
- [09-testdata-snippets.md](09-testdata-snippets.md) - Builder, Object Mother, Fixtures
- [10-allure-snippets.md](10-allure-snippets.md) - Allure Reporting Annotationen
- [11-junit5-extensions-snippets.md](11-junit5-extensions-snippets.md) - Eigene JUnit 5 Extensions
- [12-testcontainers-snippets.md](12-testcontainers-snippets.md) - Docker Container in Tests
