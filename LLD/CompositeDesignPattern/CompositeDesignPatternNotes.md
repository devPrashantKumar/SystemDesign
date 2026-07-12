# Composite Design Pattern

> **Compose objects into tree structures to represent part-whole hierarchies. Composite lets clients treat individual objects and compositions of objects uniformly.**

A folder contains files *and other folders*. An expression contains numbers *and other expressions*. A UI panel contains buttons *and other panels*. In every case the container and the thing contained are, from the client's point of view, **the same kind of thing** — and Composite is what makes that literally true in the type system.

**Type:** Structural pattern (it's about *how objects are composed* into larger structures).

---

## The Problem It Solves

Without the pattern, a container must know every concrete type it can hold, and must branch on that type. See `WithoutCompositeDesignPattern/Directory.java`:

```java
List<Object> directoryContent = new ArrayList<>();   // ⚠ untyped

public void ls(int indentation) {
    System.out.println(" ".repeat(indentation) + "Directory Name : " + directoryName);
    for (Object item : directoryContent) {
        if (item instanceof Directory) ((Directory) item).ls(indentation + 1);   // ⚠ type check
        if (item instanceof File)      ((File) item).ls(indentation + 1);        // ⚠ type check
    }
}
```

Three separate problems, and they compound:

| Problem | Consequence |
|---|---|
| `List<Object>` | Nothing stops you adding a `String`. No compile-time safety. |
| `instanceof` chain | `Directory` is coupled to **every** concrete node type. |
| Must edit `ls()` to add a type | Adding `Symlink` means changing existing, working code — an **Open/Closed violation**. |

The `instanceof` chain is the smell. Whenever you see a loop that asks "what *are* you?" before deciding what to do, you're doing by hand what polymorphism does for free.

---

## The Fix — Give Them a Shared Contract

Introduce a **Component** interface that both the leaf and the container implement. See `WithCompositeDesignPattern/`:

```java
public interface FileSystem {          // COMPONENT — the shared contract
    void ls(int indentation);
}
```

```java
public class File implements FileSystem {          // LEAF — does the work, no children
    public void ls(int indentation) {
        System.out.println(" ".repeat(indentation) + "File Name : " + fileName);
    }
}
```

```java
public class Directory implements FileSystem {     // COMPOSITE — holds children
    List<FileSystem> directoryContent = new ArrayList<>();   // ✅ typed to the COMPONENT

    public void add(FileSystem item) { directoryContent.add(item); }

    public void ls(int indentation) {
        System.out.println(" ".repeat(indentation) + "Directory Name : " + directoryName);
        for (FileSystem item : directoryContent) {
            item.ls(indentation + 1);              // ✅ one line. no instanceof. no cast.
        }
    }
}
```

The entire `instanceof` chain collapses into `item.ls(...)`. `Directory` no longer knows — or cares — whether a child is a file or another directory.

### The single most important line

```java
List<FileSystem> directoryContent;    // NOT List<File>, NOT List<Directory>
```

Typing the collection to the **component interface** is the whole trick. Because `Directory` *is a* `FileSystem`, a directory can hold a directory, which can hold a directory… The tree nests to any depth, and **recursion falls out for free**:

- `Directory.ls()` calls `ls()` on each child → recursive step
- `File.ls()` just prints → **base case**, terminates the recursion

If you type the list to `List<File>`, you get a flat list, not a tree. The pattern dies right there.

---

## The Four Roles

| GoF role | This project (`WithCompositeDesignPattern`) | Job |
|---|---|---|
| **Component** | `FileSystem` | The shared contract (`ls`) both node kinds honour |
| **Leaf** | `File` | Does the real work. Has **no** children. |
| **Composite** | `Directory` | Holds `List<FileSystem>`, delegates the operation to its children |
| **Client** | `Main` | Builds the tree, then calls the operation on the **root only** |

Note what the client does *not* do: it never walks the tree. It calls `directory1.ls(0)` once, and the structure walks itself.

---

## Variant 2 — Composite Without a Collection (`WithCompositeDesignPattern02`)

This is the example that proves Composite is **not** about lists. An expression tree:

```java
public interface EvaluateExpression {        // COMPONENT
    Integer evaluate();
}

public class Number implements EvaluateExpression {     // LEAF
    public Integer evaluate() { return operand; }        // base case
}

public class Expression implements EvaluateExpression {  // COMPOSITE
    EvaluateExpression leftExpression;                    // ✅ typed to COMPONENT
    EvaluateExpression rightExpression;                   // ✅ typed to COMPONENT
    Operator operator;

    public Integer evaluate() {
        return switch (operator) {
            case ADD      -> leftExpression.evaluate() + rightExpression.evaluate();
            case SUBTRACT -> leftExpression.evaluate() - rightExpression.evaluate();
            case MULTIPLY -> leftExpression.evaluate() * rightExpression.evaluate();
            case DIVIDE   -> leftExpression.evaluate() / rightExpression.evaluate();
        };
    }
}
```

There is **no `List` anywhere** — just two fixed children. It's still a textbook Composite, because the children are typed to the component interface. That's the only requirement. A composite needs *children of the component type*; whether there are two of them or n of them is incidental.

Because `leftExpression` is an `EvaluateExpression`, you can slot a whole sub-expression where a plain number would go, and `Expression` cannot tell the difference:

```java
Expression expression1 = new Expression(number1, number2, Operator.ADD);        // 5 + 6
Expression expression2 = new Expression(expression1, number3, Operator.SUBTRACT); // (5+6) - 7
```

**Run it:** `(5 + 6) - 7 = 4`, `8 * 9 = 72`, `4 + 72` → prints **`76`**.

This shape — leaf = literal, composite = operation over sub-expressions — is how real parsers model an **AST (abstract syntax tree)**, and it's a hair's breadth from the **Interpreter** pattern.

### A nice detail in this code

The `switch` is an **exhaustive arrow-switch over an enum** with no `default`. If you add `MODULO` to `Operator`, the code **stops compiling** until you handle it. That's the good kind of coupling — the compiler enforces completeness instead of a silent `default` swallowing the new case at runtime.

---

## Design Decision: Where Do `add()` / `remove()` Live?

This is the classic Composite trade-off, and GoF discusses it explicitly.

| Approach | `add()`/`remove()` declared in | Gain | Cost |
|---|---|---|---|
| **Transparency** | The **Component** (`FileSystem`) | Client treats leaf and composite *identically* — truly uniform | `File.add()` is meaningless; must throw `UnsupportedOperationException`. **Unsafe.** |
| **Safety** ✅ | The **Composite** only (`Directory`) | You *cannot* call `add()` on a `File` — compiler stops you | Client must know it holds a `Directory` to add to it. Slightly less uniform. |

**This project chose safety** — `add(FileSystem)` lives on `Directory`, not on `FileSystem`. That's the right call and the one most modern code makes: a compile-time error beats a runtime exception. The uniformity that matters (`ls()` on any node) is preserved regardless.

---

## Verify (this project)

Both `Without` and `With` print the same tree — the *output* is identical; the *design* is not:

```
Directory Name : Directory01
 File Name : File02
 File Name : File01
 Directory Name : Directory02
  File Name : File04
  File Name : File03
  Directory Name : Directory03
   File Name : File05
```

`WithCompositeDesignPattern02` prints `76`.

The point of the "Without" version is that it *works* — the design cost isn't a bug, it's the code you'll be forced to edit the next time a requirement changes.

---

## When To Use It

- The data is a **part-whole hierarchy** — a tree.
- Clients should be able to **ignore the difference** between a single object and a group.
- Reach for it whenever you find yourself writing `if (x instanceof Container) … else …` inside a loop.

**Real-world examples:**
- File systems (files & directories) — this project
- UI toolkits: a `Panel` contains `Button`s *and* other `Panel`s (AWT `Component`/`Container`, Android `View`/`ViewGroup`)
- ASTs / expression trees — `WithCompositeDesignPattern02`
- Org charts (an `Employee` who is a `Manager` has reports who are `Employee`s)
- `java.util.List` itself can hold a `List`

---

## Trade-offs & Cautions

- **The component interface tends to get too general.** To make leaf and composite interchangeable, the shared interface must cover both — which pushes it toward the lowest common denominator, and toward methods that only half the implementors can honour (see the `add()` discussion above).
- **Type safety is deliberately weakened.** By design, you can't easily restrict *which* components a composite accepts (e.g. "a `Directory` may only hold text files"). Enforcing that means runtime checks — the very thing you removed.
- **Cycles are your problem.** Nothing in the pattern stops you adding a directory to itself. `ls()` would then recurse forever. If the tree is built from untrusted input, guard it.
- **Deep trees mean deep recursion** — a pathological tree can blow the stack.

---

## Composite vs. Its Neighbours

| Pattern | Relationship |
|---|---|
| **Decorator** | Also wraps a component in the same interface — but a decorator has **exactly one** child and *adds behaviour*. A composite has **many** children and *aggregates* them. Structurally close cousins. |
| **Iterator** | Often used *with* Composite, to traverse the tree. |
| **Visitor** | Lets you add new *operations* over a composite tree without touching the node classes. The natural next step when `ls()` and `evaluate()` multiply into ten operations. |
| **Interpreter** | Essentially Composite applied to a grammar — `WithCompositeDesignPattern02` is one step away from it. |
