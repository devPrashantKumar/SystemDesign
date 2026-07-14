# Template Method Design Pattern

> **Define the skeleton of an algorithm in an operation, deferring some steps to subclasses.
> Template Method lets subclasses redefine certain steps of an algorithm without changing the
> algorithm's structure.**

In one line: **write the algorithm once; let subclasses fill in the blanks.**

**Type:** Behavioural pattern.

The load-bearing phrase is the last one — *without changing the algorithm's structure*. Plenty of
designs let a subclass customise behaviour. Template Method is the one that lets a subclass customise
behaviour **while making it impossible for that subclass to get the sequence wrong.**

---

## The Problem — an Algorithm That Exists Nowhere

Importing employees takes seven steps, always in this order:

```
open → read → parse → validate → save → close → audit
```

That order *is* the business rule. You must not save a record you haven't validated. You must not
leave the file handle open.

So `CsvImporter.importData()` writes those seven steps out. Six months later someone needs JSON, and
does the only thing anyone ever does: **copies the CSV importer and edits the parts they care about.**
Only two of the seven steps are genuinely different between the formats. The rest were supposed to be
identical.

```java
public class JsonImporter {
    public void importData(String path) {
        // 1. open      ✅
        // 2. read      ✅ (legitimately different)
        // 3. parse     ✅ (legitimately different)
        // 4. validate  ⚠ never written
        // 5. save      ✅
        // 6. close     ⚠ never written
        // 7. audit     ⚠ never written
    }
}
```

Run the "Without" project:

```
--- what actually landed in the database ---
    Alice (90000)
    Carol (120000)
    Dave (85000)
    Eve (-1)   ⚠ INVALID — should never have been saved

--- file handles ---
    csv  file still open? false
    json file still open? true   ⚠ LEAKED
```

An employee with a salary of **-1** is now in the database, and a file handle is leaking. Nothing
threw. Nothing warned. It compiled cleanly.

**And here is the thing worth sitting with: nothing was violated.** No rule was broken, because there
was no rule anywhere in the code to break. The algorithm was never written down — it existed only as
a *habit*, copied by hand from one class into the next, and **habits drift**.

Then the audit team asks for a checksum step. Now you get to add it to every importer, and hope you
don't miss one. Again.

---

## The Fix — Write the Algorithm Down, Once

Put the sequence in a base class and let it call *down* into the subclasses:

```java
public abstract class DataImporter {

    public final void importData(String path) {     // ← THE TEMPLATE METHOD
        open(path);                                 // abstract — per format
        List<String> raw = readRaw();               // abstract — per format
        List<Employee> parsed = parse(raw);         // abstract — per format

        List<Employee> valid = validate(parsed);    // private  — ALWAYS runs
        List<Employee> records = transform(valid);  // hook     — optional

        if (shouldDeduplicate()) {                  // hook     — optional
            records = deduplicate(records);
        }

        save(records);                              // private  — ALWAYS runs
        close();                                    // abstract — per format
        audit(parsed.size(), records.size());       // private  — ALWAYS runs
    }
}
```

### The inversion

Notice who is calling whom. In the "Without" version, each importer *called* the steps. Here, **nobody
calls the steps — the base class calls them.** A subclass no longer decides *when* to validate or
*whether* to close. It is asked what "open" means for its format, and nothing else.

This is the **Hollywood Principle**: *don't call us, we'll call you.* The subclass supplies parts; it
does not drive the flow. That's why it's a behavioural pattern and not just code reuse.

### The three kinds of step

The whole pattern is in this distinction:

| Kind | Java | Who supplies it | Can a subclass skip it? |
|---|---|---|---|
| **Primitive operation** | `abstract` | subclass **must** | No — it won't compile without it |
| **Invariant step** | `private` (or `final`) | base class | **No — it can't even see it** |
| **Hook** | `protected`, with a default | base class; subclass **may** override | Yes — that's the point |

`open`, `readRaw`, `parse`, `close` are primitives — they genuinely differ per format.
`validate`, `save`, `audit` are invariant — they are the same for everybody.
`transform` and `shouldDeduplicate` are hooks — optional steps most subclasses ignore.

### `final` and `private` are the enforcement, not decoration

This is the part most write-ups skip, and it's the reason the pattern actually *works* rather than
merely *suggests*.

- `importData()` is **`final`** → a subclass cannot override it, so it cannot reorder the steps.
- `validate()` is **`private`** → a subclass cannot override it, cannot call it, cannot see it.

Not a convention. A compiler guarantee. I wrote a `RogueImporter` that tries both and compiled it:

```
error: importData(String) in RogueImporter cannot override importData(String) in DataImporter
    public final void importData(String path) { }
                      ^
  overridden method is final

error: method does not override or implement a method from a supertype
    @Override
    ^
```

The bug from the "Without" project isn't discouraged here. It is **unrepresentable**.

---

## The Payoff, Demonstrated

`XmlImporter` was written last. It cost five short methods, every one of them genuinely about XML —
not one line about validation, saving, closing, auditing, or the order of steps:

```
=== employees.xml ===
  [xml]  opening employees.xml
  [xml]  read 2 raw elements
  [xml]  ✗ rejected invalid record: Grace (0)     ← it never wrote this check. It inherited it.
  [db]   inserted Frank (77000)
  [xml]  closed                                   ← nor this.
  [xml]  AUDIT: imported 1 of 2                   ← nor this.
```

Its author could not have introduced JsonImporter's bugs if they had tried.

---

## What This Buys You

| | Without Template Method | With Template Method |
|---|---|---|
| Where the algorithm lives | copied by hand into every importer | **written down once**, in `importData()` |
| A subclass skipping validation | happened, silently | **won't compile** |
| A subclass reordering steps | trivially | **won't compile** (`final`) |
| Adding a 4th format | copy 7 steps, hope you copied a correct one | implement 5 methods that are all about the format |
| Adding a checksum step to all | edit every importer, miss one | **one line in the base class** |
| Optional per-format behaviour | more copy-paste | a **hook** with a default |

---

## The Two Roles

Template Method is unusually small — it has only two participants, and one of them is a method.

| GoF role | This project | Its job |
|---|---|---|
| **Abstract Class** | `DataImporter` | defines the **template method** + the primitives + the hooks |
| **Concrete Class** | `CsvImporter`, `JsonImporter`, `XmlImporter` | implements the primitives; may override a hook |

The acceptance test: **is your template method `final`?** If a subclass can override the algorithm,
you don't have a Template Method — you have a base class with some helper methods and a naming
convention.

---

## Verify (this project)

```
WITHOUT:  Eve (-1) is in the database   |  json file still open? true    ⚠
WITH:     no invalid rows               |  all three files closed        ✅
```

And the JSON `transform` hook shows the *flexibility* half of the deal — only that importer needed to
clean up sloppy names, and only that importer overrode it:

```
  [json] transform hook: normalised 1 name(s)     ("  dave " → "Dave")
```

---

## Trade-offs & Cautions

- **It costs you your one inheritance slot.** This is the real price in Java. A class can extend
  exactly one thing, and Template Method spends it. If you need to vary two dimensions
  independently, you're stuck — reach for **Strategy** (composition) instead. This is the single
  biggest reason the pattern is less popular now than in 1994.
- **The base class can become a dumping ground.** Every new "just one more optional step" tends to be
  added as a hook, and the abstract class slowly turns into a 400-line god object with fifteen hooks
  nobody can keep straight. Watch the hook count.
- **Inheritance means the subclass is coupled to the base class's internals.** Change a primitive's
  signature and every subclass breaks. This is the fragile base class problem, and it's the reason
  "favour composition over inheritance" exists.
- **Don't make the primitives public.** They are `protected` for a reason: they are steps in *your*
  algorithm, not a public API. A caller invoking `parse()` directly is bypassing the whole point.
- **A hook with a non-trivial default is a trap.** If overriding a hook can break the algorithm's
  invariants, it isn't a hook — it's a hole. Keep defaults safe and keep hooks genuinely optional.
- **Modern Java softens it.** A `default` method on an interface can hold a template method, giving
  you the skeleton without spending the inheritance slot — though it can't hold state or make steps
  private in the same way (interface methods can be `private` since Java 9, but there are no fields).

---

## Where You've Already Used It

- **`java.util.AbstractList` / `AbstractMap`** — the classic. `AbstractList` implements the whole
  `List` contract on top of two primitives you supply: `get(int)` and `size()`. Everything else —
  `contains`, `indexOf`, `iterator` — is inherited algorithm.
- **`java.io.InputStream`** — `read(byte[], int, int)` is a template built on the abstract `read()`.
- **Servlets** — `HttpServlet.service()` is the template method; it dispatches to `doGet`, `doPost`,
  `doPut`. You never override `service()`; you fill in the blanks.
- **JUnit** — `@Before` / `@Test` / `@After` is a template method the framework runs. You supply the
  steps; the runner owns the sequence. (Old JUnit 3 made this literal: `setUp()` / `runTest()` /
  `tearDown()`.)
- **Spring's `*Template` classes** — `JdbcTemplate`, `RestTemplate`, `TransactionTemplate`. Named
  after the pattern, though most now take a callback (Strategy) rather than requiring a subclass —
  which is a good illustration of the trade-off above.
- **Android `Activity`** — `onCreate` / `onStart` / `onResume`. The framework owns the lifecycle; you
  fill in the hooks.

The common thread: **a framework that owns the flow, and lets you supply the parts.** Template Method
is what makes a framework a framework rather than a library.

---

## Template Method vs. Its Neighbours

| Pattern | The actual difference |
|---|---|
| **Strategy** | **The one to really understand — they solve the same problem two ways.** Template Method varies a step by **subclassing** (compile-time, one inheritance slot, the algorithm's skeleton is fixed in the base class). Strategy varies a step by **composition** (runtime-swappable, no inheritance cost, the algorithm holds an object). Strategy replaces the *whole* algorithm; Template Method replaces *steps within* one. Rule of thumb: if the steps vary independently, or you need to swap them at runtime, or you're already extending something — use Strategy. See `StrategyDesignPattern/` in this repo. |
| **Factory Method** | **Factory Method is usually a step inside a template method.** GoF say this directly. The template method calls `createProduct()`, and the subclass decides what to make. In this project, `parse()` is exactly that shape — the base class calls it; the subclass decides how to build the objects. |
| **Hook vs. Observer** | Both are "call me at the right moment", but a hook is *inside* the algorithm and can influence it, while an observer is *notified after the fact* and shouldn't. If your hook is really just a notification, use `ObserverDesignPattern/` instead. |
| **Decorator** | Decorator adds behaviour *around* an object from the outside, at runtime, stackably. Template Method adds behaviour *into* a fixed slot, at compile time, once. Decorator can't change the middle of an algorithm; Template Method can't be stacked. |
| **Bridge** | Both split "the stable part" from "the varying part". Bridge does it with **composition across two hierarchies** that can each grow; Template Method does it with **inheritance in one**. If both axes vary, you want Bridge — see `BridgeDesignPattern/`. |
