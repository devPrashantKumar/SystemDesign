# Singleton Design Pattern — UML Diagrams

Singleton has the simplest structure of any pattern — a single class that controls its
own instantiation. What varies across the six variants isn't the shape, it's *how the one
instance is created and guarded*. This file shows the common structure, then the
per-variant creation logic.

---

## The Common Structure (all variants)

```mermaid
classDiagram
    class Singleton {
        -static Singleton instance
        -Singleton()
        +static getInstance() Singleton
    }

    Singleton --> Singleton : holds & returns its own instance
```

Two fixed rules, visible in the diagram:

| Element | Notation | Why |
|---|---|---|
| `-Singleton()` | private constructor (`-`) | blocks `new` from outside |
| `+getInstance()` | public **static** (`+`, underlined = static) | the one global access point |
| `-instance` | private **static** field | stores the single instance |

The self-association (`Singleton --> Singleton`) is the giveaway: the class both *is* the
type and *holds* the instance.

---

## ASCII — the canonical shape

```
        ┌────────────────────────────────┐
        │           Singleton            │
        │────────────────────────────────│
        │ - static instance : Singleton  │  ◀── the one instance lives here
        │────────────────────────────────│
        │ - Singleton()                  │  ◀── PRIVATE: no outside `new`
        │ + static getInstance():Singleton│ ◀── the only public door
        └────────────────────────────────┘
                     ▲     │
             returns │     │ creates (once)
                     └─────┘
```

---

## Variant Comparison (decision view)

```mermaid
flowchart TD
    A[Need a singleton] --> B{Need lazy loading?}
    B -- No, cheap & always used --> C[Eager]
    B -- No, want max safety --> D[Enum<br/>reflection & serialization safe]
    B -- Yes --> E{Single-threaded only?}
    E -- Yes --> F[Lazy<br/>simple, NOT thread-safe]
    E -- No, multi-threaded --> G[Bill Pugh<br/>lazy + safe + no locking]
    G -.historical alternatives.-> H[Double-Checked Locking<br/>volatile + synchronized]
    H -.-> I[Synchronized method<br/>locks every call]
```

---

## Per-Variant Creation Logic

```mermaid
classDiagram
    direction LR

    class EagerSingleton {
        -static final EagerSingleton INSTANCE
        -EagerSingleton()
        +static getInstance() EagerSingleton
    }
    note for EagerSingleton "created at class load\n(JVM-safe, not lazy)"

    class LazySingleton {
        -static LazySingleton instance
        -LazySingleton()
        +static getInstance() LazySingleton
    }
    note for LazySingleton "created on first call\n(NOT thread-safe)"

    class BillPughSingleton {
        -BillPughSingleton()
        +static getInstance() BillPughSingleton
    }
    class Holder {
        -static final BillPughSingleton INSTANCE
    }
    BillPughSingleton *-- Holder : inner holder loaded on first getInstance()
    note for BillPughSingleton "lazy + thread-safe\nno lock, no volatile"

    class EnumSingleton {
        <<enumeration>>
        INSTANCE
        +doSomething() void
    }
    note for EnumSingleton "one instance guaranteed by JVM\nreflection + serialization safe"
```

---

## Sequence — Double-Checked Locking (the trickiest path)

```mermaid
sequenceDiagram
    participant T1 as Thread 1
    participant T2 as Thread 2
    participant S as DoubleCheckedSingleton

    T1->>S: getInstance()
    S->>S: instance == null? yes (1st check, no lock)
    T1->>S: enter synchronized block
    Note over T2: Thread 2 arrives, blocks on the lock
    S->>S: instance == null? yes (2nd check, inside lock)
    S->>S: instance = new DoubleCheckedSingleton()
    T1-->>S: exit block, return instance

    T2->>S: enters synchronized block now
    S->>S: instance == null? NO (2nd check saves us)
    Note over T2: does NOT create a second object
    T2-->>S: return the same instance
```

The **second null check** is exactly what stops Thread 2 from creating a duplicate after
it was blocked. The **fast path** (post-creation) skips the lock entirely: `instance` is
non-null, so the first check returns immediately.

---

## Key Structural Points

1. **The class controls its own instantiation.** Private constructor + static accessor =
   the class is the only thing allowed to create itself. That inward control is the whole
   pattern; everything else is *how* to do it safely.

2. **Lazy vs. eager is a creation-timing choice, not a structural one.** The class diagram
   is identical; only *when* the `instance` field gets assigned changes.

3. **Thread safety comes from one of three sources.** The JVM class loader (Eager, Bill
   Pugh, Enum), or explicit locking (synchronized method, DCL), or nothing (plain Lazy —
   which is why it's unsafe).

4. **Enum is structurally different — and safest.** It has no explicit `instance` field or
   `getInstance()`; the language guarantees one instance and blocks reflection and
   serialization attacks that the class-based variants must defend against manually.
