# Template Method Design Pattern — UML Diagrams

Template Method is the smallest pattern in the book: **two classes and one method.**

The method is the pattern. Everything below is about which direction the arrows point — and the
answer is the one thing that makes this pattern interesting: **they point down.** The base class
calls the subclass, not the other way round.

---

## 1. The Canonical Structure

```mermaid
classDiagram
    class AbstractClass {
        +templateMethod() final
        #primitiveOperation1()* abstract
        #primitiveOperation2()* abstract
        #hook()
        -invariantStep()
    }

    class ConcreteClassA {
        #primitiveOperation1()
        #primitiveOperation2()
    }

    class ConcreteClassB {
        #primitiveOperation1()
        #primitiveOperation2()
        #hook()
    }

    AbstractClass <|-- ConcreteClassA
    AbstractClass <|-- ConcreteClassB

    note for AbstractClass "templateMethod() is FINAL.<br/>It calls the primitives in the ONE correct order.<br/>Subclasses supply the steps —<br/>they never own the sequence."
    note for ConcreteClassB "Overrides a HOOK.<br/>Optional. Most subclasses don't."
```

The arrow that matters isn't drawn here, because UML has no notation for it: **`templateMethod()`
calls down into methods that don't exist yet.** The base class depends on code written after it.

---

## 2. The Problem — `WithoutTemplateMethodDesignPattern`

```mermaid
classDiagram
    class CsvImporter {
        +importData(path) void
        ..the 7 steps, written out by hand..
        1. open ✅
        2. read ✅
        3. parse ✅
        4. validate ✅
        5. save ✅
        6. close ✅
        7. audit ✅
    }

    class JsonImporter {
        +importData(path) void
        ..the 7 steps, copied by hand..
        1. open ✅
        2. read ✅
        3. parse ✅
        4. validate ⚠ MISSING
        5. save ✅
        6. close ⚠ MISSING
        7. audit ⚠ MISSING
    }

    class Database

    CsvImporter ..> Database : insert (validated)
    JsonImporter ..> Database : insert (NOT validated) 😈

    note for CsvImporter "The algorithm lives HERE.<br/>And only here.<br/>It is not written down anywhere<br/>that this order is THE order."
    note for JsonImporter "Copied from CsvImporter six months later,<br/>then edited. Only steps 2 and 3 needed to change.<br/>Three others were quietly lost.<br/>⚠ It compiles. It runs. Nothing warns."
```

**No rule was broken here — because there was no rule.** The sequence existed only as a habit that got
copied by hand, and habits drift. `Eve (-1)` is now in the database and a file handle is leaking.

---

## 3. The Fix — `WithTemplateMethodDesignPattern`

```mermaid
classDiagram
    class DataImporter {
        <<abstract>>
        +importData(path)$ FINAL
        #open(path)* abstract
        #readRaw()* abstract
        #parse(raw)* abstract
        #close()* abstract
        #transform(records) HOOK
        #shouldDeduplicate() HOOK
        -validate(records) PRIVATE
        -deduplicate(records) PRIVATE
        -save(records) PRIVATE
        -audit(read, saved) PRIVATE
    }

    class CsvImporter {
        #open(path)
        #readRaw()
        #parse(raw)
        #close()
        #shouldDeduplicate() true
    }

    class JsonImporter {
        #open(path)
        #readRaw()
        #parse(raw)
        #close()
        #transform(records)
    }

    class XmlImporter {
        #open(path)
        #readRaw()
        #parse(raw)
        #close()
    }

    DataImporter <|-- CsvImporter
    DataImporter <|-- JsonImporter
    DataImporter <|-- XmlImporter

    note for DataImporter "importData() is FINAL → the order is not negotiable.<br/>validate/save/audit are PRIVATE → a subclass<br/>cannot override, call, or even SEE them.<br/>Verified: 'overridden method is final'"
    note for XmlImporter "Written last. 5 methods, all about XML.<br/>Zero lines about validation, closing or ordering —<br/>and it could not have skipped them if it tried."
```

| Role | This project |
|---|---|
| **Abstract Class** | `DataImporter` — owns `importData()`, the primitives, the hooks |
| **Concrete Class** | `CsvImporter`, `JsonImporter`, `XmlImporter` |
| **Template Method** | `importData()` — `final` |
| **Primitive operations** | `open`, `readRaw`, `parse`, `close` — `abstract` |
| **Hooks** | `transform`, `shouldDeduplicate` — `protected`, with defaults |
| **Invariant steps** | `validate`, `save`, `audit`, `deduplicate` — `private` |

---

## 4. ASCII — Who Owns the Sequence?

```
   WITHOUT TEMPLATE METHOD                     WITH TEMPLATE METHOD
   ───────────────────────                     ────────────────────

   ┌──────────────┐  ┌──────────────┐          ┌───────────────────────────────┐
   │ CsvImporter  │  │ JsonImporter │          │        DataImporter           │
   │ ──────────── │  │ ──────────── │          │  ───────────────────────────  │
   │ 1. open      │  │ 1. open      │          │  importData()  ← FINAL        │
   │ 2. read      │  │ 2. read      │          │  ┌─────────────────────────┐  │
   │ 3. parse     │  │ 3. parse     │          │  │ 1. open()      ────────────┼──┐
   │ 4. validate  │  │ ⚠ ······     │          │  │ 2. readRaw()   ────────────┼──┤ abstract:
   │ 5. save      │  │ 5. save      │          │  │ 3. parse()     ────────────┼──┤ the subclass
   │ 6. close     │  │ ⚠ ······     │          │  │ 4. validate()   [private]│  │  │ MUST fill
   │ 7. audit     │  │ ⚠ ······     │          │  │ 5. transform()  [hook]   │  │  │ these in
   └──────────────┘  └──────────────┘          │  │ 6. save()       [private]│  │  │
          │                 │                  │  │ 7. close()     ────────────┼──┘
          │                 │                  │  │ 8. audit()      [private]│  │
          ▼                 ▼                  │  └─────────────────────────┘  │
   the algorithm is COPIED.                    └───────────────┬───────────────┘
   Two copies. They drifted.                                   │ calls DOWN
                                                     ┌─────────┼─────────┐
   Add an 8th step?                                  ▼         ▼         ▼
   → edit every class, miss one.                  ┌─────┐  ┌──────┐  ┌─────┐
                                                  │ Csv │  │ Json │  │ Xml │
   Who owns the sequence?                         └─────┘  └──────┘  └─────┘
   → nobody. That's the bug.                   they own STEPS. Never the SEQUENCE.

                                               Add an 8th step?
                                               → one line, in one place. All three get it.
```

**The inversion is the pattern.** On the left, each subclass calls the steps — so each subclass can
get the order wrong, and one did. On the right, nobody calls the steps: **the base class calls them.**
A subclass is never asked *when* to validate, only what "open" means for its format.

That's the **Hollywood Principle** — *don't call us, we'll call you* — and it is the difference
between a library (you call it) and a framework (it calls you).

---

## 5. Sequence — One Import

```mermaid
sequenceDiagram
    participant C as Client
    participant T as DataImporter<br/>(template method)
    participant J as JsonImporter
    participant DB as Database

    C->>T: importData("employees.json")
    activate T
    Note over T: the ONE entry point.<br/>final — nobody can rewrite this.

    T->>J: open(path)
    T->>J: readRaw()
    J-->>T: raw JSON objects
    T->>J: parse(raw)
    J-->>T: List~Employee~

    Note over T: validate() — PRIVATE.<br/>JsonImporter never wrote it,<br/>cannot see it, cannot skip it.
    T->>T: validate()
    Note over T: ✗ rejected Eve (-1)

    T->>J: transform(records)
    Note over J: HOOK — this importer opted in<br/>("  dave " → "Dave").<br/>The others didn't override it.
    J-->>T: cleaned records

    T->>T: shouldDeduplicate()?  → false (default hook)
    T->>DB: save()
    T->>J: close()
    Note over J: abstract → it MUST exist,<br/>and the template ALWAYS calls it.<br/>No leaked handle this time.
    T->>T: audit()
    deactivate T
```

Compare with the "Without" run, where the same import saved `Eve (-1)` and left the file open.

---

## Key Structural Points

1. **The template method is `final`.** This is the acceptance test for the pattern. If a subclass can
   override the algorithm, you don't have a Template Method — you have a base class with helper
   methods and a naming convention. Verified against the compiler:
   `error: overridden method is final`.

2. **Control is inverted.** The base class calls down into the subclass. The subclass supplies steps
   and never owns the sequence — which is exactly why it cannot get the sequence wrong.

3. **Three kinds of step, and the distinction is the whole design.**
   `abstract` = the subclass **must** supply it. `private`/`final` = the subclass **cannot touch** it.
   `protected` with a default = a **hook**, optional. Choosing which bucket each step goes in *is*
   designing the pattern.

4. **Invariant steps should be `private`, not `protected`.** `validate()` isn't merely "not meant to
   be overridden" — it is invisible to subclasses. Encapsulation is what makes the guarantee real
   rather than aspirational.

5. **Hooks are how it stays flexible.** `JsonImporter` normalises names; `CsvImporter` deduplicates;
   `XmlImporter` wants neither and overrides nothing. All three run the identical algorithm.

6. **The price is your one inheritance slot.** Java gives each class exactly one superclass, and this
   pattern spends it. If you need to vary two things independently, or swap behaviour at runtime, use
   **Strategy** (composition) instead — same problem, different lever. That trade-off is why modern
   frameworks like Spring name their classes `*Template` but hand you a callback rather than demanding
   a subclass.
