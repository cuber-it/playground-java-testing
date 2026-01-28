# AssertJ Cheatsheet

## Dependency

**Maven:**
```xml
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <version>3.26.0</version>
    <scope>test</scope>
</dependency>
```

**Gradle:**
```groovy
testImplementation 'org.assertj:assertj-core:3.26.0'
```

## Import

```java
import static org.assertj.core.api.Assertions.*;
```

## Basis

```java
assertThat(actual).isEqualTo(expected);
assertThat(actual).isNotEqualTo(other);
assertThat(actual).isNull();
assertThat(actual).isNotNull();
assertThat(actual).isSameAs(other);      // ==
assertThat(actual).isInstanceOf(String.class);
```

## Strings

```java
assertThat(str).isEmpty();
assertThat(str).isNotEmpty();
assertThat(str).isBlank();
assertThat(str).contains("text");
assertThat(str).containsIgnoringCase("TEXT");
assertThat(str).startsWith("Hello");
assertThat(str).endsWith("World");
assertThat(str).matches("\\d+");
assertThat(str).hasSize(10);
```

## Zahlen

```java
assertThat(num).isZero();
assertThat(num).isPositive();
assertThat(num).isNegative();
assertThat(num).isGreaterThan(5);
assertThat(num).isGreaterThanOrEqualTo(5);
assertThat(num).isLessThan(10);
assertThat(num).isBetween(1, 10);
assertThat(num).isCloseTo(3.14, within(0.01));
```

## Boolean

```java
assertThat(flag).isTrue();
assertThat(flag).isFalse();
```

## Collections

```java
assertThat(list).isEmpty();
assertThat(list).isNotEmpty();
assertThat(list).hasSize(3);
assertThat(list).contains("a", "b");
assertThat(list).containsExactly("a", "b", "c");
assertThat(list).containsExactlyInAnyOrder("c", "a", "b");
assertThat(list).containsOnly("a", "b");
assertThat(list).doesNotContain("x");
assertThat(list).containsNull();
assertThat(list).doesNotContainNull();
assertThat(list).first().isEqualTo("a");
assertThat(list).last().isEqualTo("c");
assertThat(list).element(1).isEqualTo("b");
```

## Extracting (aus Objekten)

```java
assertThat(todos)
    .extracting(Todo::getTitle)
    .contains("A", "B");

assertThat(todos)
    .extracting("title", "done")
    .contains(tuple("A", false), tuple("B", true));

assertThat(todos)
    .filteredOn(t -> !t.isDone())
    .hasSize(2);
```

## Maps

```java
assertThat(map).isEmpty();
assertThat(map).hasSize(2);
assertThat(map).containsKey("key");
assertThat(map).containsValue("value");
assertThat(map).containsEntry("key", "value");
assertThat(map).doesNotContainKey("x");
```

## Exceptions

```java
assertThatThrownBy(() -> service.doSomething())
    .isInstanceOf(RuntimeException.class)
    .hasMessage("error")
    .hasMessageContaining("err")
    .hasCauseInstanceOf(IOException.class);

assertThatCode(() -> safe()).doesNotThrowAnyException();

assertThatExceptionOfType(IllegalArgumentException.class)
    .isThrownBy(() -> code())
    .withMessage("msg");
```

## Optional

```java
assertThat(optional).isPresent();
assertThat(optional).isEmpty();
assertThat(optional).hasValue("expected");
assertThat(optional).containsInstanceOf(String.class);
```

## Soft Assertions (alle prüfen)

```java
SoftAssertions.assertSoftly(soft -> {
    soft.assertThat(a).isEqualTo(1);
    soft.assertThat(b).isEqualTo(2);
    soft.assertThat(c).isEqualTo(3);
});
```
