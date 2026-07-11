# Singleton Design Pattern

> **Ensure a class has exactly one instance, and provide a global point of access to it.**

Some things should exist only once in an application — a configuration registry, a logger, a thread pool, a database connection pool. Singleton guarantees that no matter how many times you ask, you always get the *same* object.

**Type:** Creational pattern (it's about *how objects are created* — here, how their creation is *restricted*).

---

## The Two Non-Negotiable Ingredients

Every variant, no matter how it differs, does these two things:

1. **A `private` constructor** — so no outside code can call `new`.
2. **A `static` accessor** (`getInstance()` or an enum constant) — the single controlled door to the one instance.

Everything below is just *different ways to safely create and hand out that one instance*.

---

## The Six Variants (all in this project)

| # | Variant | Lazy? | Thread-safe? | Locking cost | File |
|---|---|---|---|---|---|
| 1 | **Eager** | ❌ (at class load) | ✅ (JVM class load) | none | `EagerSingleton.java` |
| 2 | **Lazy** | ✅ | ❌ **broken** | none | `LazySingleton.java` |
| 3 | **Thread-safe (synchronized)** | ✅ | ✅ | on **every** call | `ThreadSafeSingleton.java` |
| 4 | **Double-Checked Locking** | ✅ | ✅ | only on first creation | `DoubleCheckedSingleton.java` |
| 5 | **Bill Pugh (static holder)** | ✅ | ✅ | none | `BillPughSingleton.java` |
| 6 | **Enum** | ❌ | ✅ | none | `EnumSingleton.java` |

---

### 1. Eager Initialization

```java
private static final EagerSingleton INSTANCE = new EagerSingleton();
private EagerSingleton() {}
public static EagerSingleton getInstance() { return INSTANCE; }
```

Instance is built when the class loads. The JVM makes class loading thread-safe, so this is safe with **no locking**. Downside: it's created even if never used, and you can't easily handle construction failure. **Use when** the object is cheap and always needed.

---

### 2. Lazy Initialization (NOT thread-safe)

```java
private static LazySingleton instance;
public static LazySingleton getInstance() {
    if (instance == null) {              // ⚠ race condition
        instance = new LazySingleton();
    }
    return instance;
}
```

Created on first use. **Broken under threads:** two threads can both see `instance == null` and create two objects. Shown to motivate the safe variants — **use only in single-threaded code**.

---

### 3. Thread-Safe (synchronized method)

```java
public static synchronized ThreadSafeSingleton getInstance() {
    if (instance == null) instance = new ThreadSafeSingleton();
    return instance;
}
```

`synchronized` closes the race. But **every** call pays the lock cost — even reads after the instance already exists. Correct but slow under heavy concurrency.

---

### 4. Double-Checked Locking (DCL)

```java
private static volatile DoubleCheckedSingleton instance;
public static DoubleCheckedSingleton getInstance() {
    if (instance == null) {                       // 1st check — no lock (fast path)
        synchronized (DoubleCheckedSingleton.class) {
            if (instance == null) {               // 2nd check — inside the lock
                instance = new DoubleCheckedSingleton();
            }
        }
    }
    return instance;
}
```

Locks **only on first creation**; every later call takes the lock-free fast path. Two subtle must-haves:

- **`volatile`** — without it, instruction reordering during `new` could let another thread see a *partially-constructed* object.
- **The second null check** — two threads may both pass the first check; only one must actually create.

---

### 5. Bill Pugh (static inner-class holder) ✅ *best classic approach*

```java
private BillPughSingleton() {}
private static class Holder {
    private static final BillPughSingleton INSTANCE = new BillPughSingleton();
}
public static BillPughSingleton getInstance() { return Holder.INSTANCE; }
```

The `Holder` class isn't loaded until `getInstance()` first touches it — so the instance is created lazily, **on demand**. And because class loading is JVM-guaranteed thread-safe, you get thread safety with **no `synchronized`, no `volatile`, no locking cost**. Lazy + safe + fast, all for free. **The go-to class-based singleton.**

---

### 6. Enum ✅ *Effective Java's recommendation (Item 3)*

```java
public enum EnumSingleton {
    INSTANCE;
    public void doSomething() { ... }
}
// usage: EnumSingleton.INSTANCE.doSomething();
```

A single-element enum *is* a singleton. It's the **only** variant automatically safe against:

- **Reflection** — you can't reflectively call an enum constructor to forge a second instance (all the class-based variants can be broken this way).
- **Serialization** — enums deserialize back to the same constant; the class-based variants would create a *new* object on deserialization unless you add a `readResolve()` method.

Downsides: not lazy (created at enum load) and can't extend another class. **Use when** you want the most bullet-proof singleton and don't need lazy loading.

---

## How Singletons Get Broken (and which variants resist it)

| Attack | Class-based variants (1–5) | Enum (6) |
|---|---|---|
| **Reflection** — `setAccessible(true)` on the private ctor, then `new` | Vulnerable (add a guard in the ctor) | **Immune** |
| **Serialization** — serialize then deserialize → second object | Vulnerable (add `readResolve()`) | **Immune** |
| **Cloning** — if it implements `Cloneable` | Vulnerable (don't implement, or throw) | **Immune** (enums aren't cloneable) |
| **Multiple classloaders** — each loader = its own instance | Vulnerable | Vulnerable |

This is why enum is considered the safest.

---

## Which One Should I Use?

- **Default to Bill Pugh** for a normal class-based singleton — lazy, thread-safe, no locking, simple.
- **Use Enum** when you want maximum safety (reflection/serialization) and don't need lazy loading.
- **Eager** is fine when the object is cheap and always used.
- **DCL** is mostly historical/interview material now — Bill Pugh achieves the same goal more cleanly.
- **Plain Lazy** and **synchronized-method** are teaching steps, rarely the right production choice.

---

## Verify (this project)

```
1. Eager          : true
2. Lazy           : true
3. Thread-safe    : true
4. Double-checked : true
5. Bill Pugh      : true
6. Enum           : true
```

Each `true` means two `getInstance()` calls (or `INSTANCE` references) returned the **same** object (`==` identity).

---

## Trade-offs & Cautions

- **Singletons are effectively global state** — they can hide dependencies, make unit testing harder (shared mutable state across tests), and couple code to a concrete class. Prefer **dependency injection** (pass the single instance in) over calling `getInstance()` everywhere.
- A singleton holding **mutable** state must make that state thread-safe itself — the pattern only guarantees one *instance*, not that its methods are safe to call concurrently.

---

## Real-World Examples in Java

- `java.lang.Runtime.getRuntime()` — one Runtime per JVM.
- `java.awt.Desktop.getDesktop()`.
- Spring beans default to **singleton scope** (one instance per container).
- Loggers (e.g. a `LoggerFactory`-produced logger), connection pools, and configuration holders are classic singleton use cases.
