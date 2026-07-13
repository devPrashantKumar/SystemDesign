# Visitor Design Pattern

> **Represent an operation to be performed on the elements of an object structure. Visitor
> lets you define a new operation without changing the classes of the elements on which it
> operates.**

In one line: **when the operations keep multiplying but the classes don't, move the operations out.**

**Type:** Behavioural pattern.

This is the hardest of the GoF patterns to love, and the easiest to misuse. It is worth learning
properly for two reasons: it is *everywhere* in compilers, linters and AST tooling, and it is the
only pattern that will teach you what **double dispatch** actually means.

---

## The Problem — the Stable Classes Are the Ones You Keep Editing

A document is made of elements: `Paragraph`, `Image`, `Table`, `CodeBlock`. Product wants to export
it as HTML. Then as Markdown. Then count the words. So:

```java
public interface DocumentElement {
    String toHtml();
    String toMarkdown();
    int    wordCount();      // ⚠ each new feature = a new method HERE
}                            //    and an edit in EVERY element class
```

Now look at what a `Paragraph` has become: text data, plus HTML knowledge, plus Markdown knowledge,
plus counting logic. It has four reasons to change, and only one of them is *"what a paragraph is"*.

**Notice which way round this is failing.** A paragraph has been a paragraph for forty years — the
element hierarchy is the **stable** part. The operations are what churn: today HTML, next quarter
PDF, then spell-check, then accessibility audit. And yet it is the stable classes we are forced to
open and edit, every single time. That is the Open/Closed Principle failing exactly backwards.

### The escape hatch that isn't

Fine — keep the new operation *outside* the hierarchy. Without Visitor, there is only one way to do
that:

```java
for (DocumentElement element : document) {
    if (element instanceof Paragraph) { ... }
    else if (element instanceof Image) { ... }
    else if (element instanceof Table) { ... }
    else { /* ⚠ a CodeBlock lands here. Silently. In production. */ }
}
```

Run the "Without" project and watch it happen — the `CodeBlock` **disappears from the export**, and
the compiler said nothing:

```
--- Plain-text export (via instanceof ladder) ---
    The Visitor pattern separates an algorithm from the objects it runs on.
    [image]
    [table]
    [unknown element — SILENTLY IGNORED]
```

Three things are wrong with the ladder, and they compound:

1. **The compiler cannot help you.** Add an element type and every ladder still compiles. The bug
   surfaces at runtime, in whichever export nobody was watching.
2. **Every operation needs its own ladder**, and they all have to be updated in lockstep.
3. **You are hand-writing type dispatch that the language already does for free** on every virtual
   call. That is the real tell. Visitor's whole trick is getting that dispatch *back*.

---

## The Fix — Make the Operation an Object, and Let the Element Introduce Itself

### 1. The Element — one method, forever

```java
public interface DocumentElement {
    <R> R accept(DocumentVisitor<R> visitor);     // ← that's it. It never grows.
}
```

Compare with the "Without" version, which grew a method per feature. This one is **closed for
modification**, permanently, no matter how many operations get written.

### 2. The Visitor — one method per element type

```java
public interface DocumentVisitor<R> {
    R visit(Paragraph paragraph);
    R visit(Image image);
    R visit(Table table);
    R visit(CodeBlock codeBlock);
}
```

A class implementing this **is an operation over the entire document**. `R` is whatever the operation
produces: `String` for the exporters, `Integer` for the word count. (GoF's original returned `void`
and had visitors accumulate state; returning a value is the modern form and is usually cleaner.)

### 3. `accept()` — and the mechanism the pattern actually rests on

```java
public class Paragraph implements DocumentElement {
    private final String text;

    @Override
    public <R> R accept(DocumentVisitor<R> visitor) {
        return visitor.visit(this);      // ← `this` is a Paragraph. The compiler knows it.
    }
}
```

Those two lines look like nothing. They are **double dispatch**, and they are the pattern:

| | What happens | Resolved |
|---|---|---|
| **1st dispatch** | `element.accept(visitor)` — a virtual call, so Java picks `Paragraph.accept` / `Image.accept` / … | at **runtime**, on the **element's** type |
| **2nd dispatch** | inside that method, `visitor.visit(this)` — we are lexically inside `Paragraph`, so the compiler binds the `visit(Paragraph)` overload | overload chosen at **compile time**; which visitor runs is chosen at **runtime** |

Two dispatches — one on the element's type, one on the visitor's type — and the correct
**(element, operation)** pair meets. Selecting a method on *two* types at once is the thing single
dispatch cannot do, and it is precisely what the `instanceof` ladder was faking by hand.

> **The one-sentence version:** the element already knows its own type, so instead of the operation
> interrogating the element (`instanceof`), the element *tells* the operation (`visit(this)`).

### 4. The Object Structure — the role everyone forgets

Somebody must walk the elements and hand each one to the visitor:

```java
public <R> List<R> accept(DocumentVisitor<R> visitor) {
    List<R> results = new ArrayList<>();
    for (DocumentElement element : elements) {   // ← Iterator
        results.add(element.accept(visitor));    // ← Visitor
    }
    return results;
}
```

Here it is a flat list. In a compiler it is an AST; in this repo's `CompositeDesignPattern` it would
be the `FileSystem` tree. **The traversal is written once and every visitor ever written reuses it.**

---

## The Payoff, Demonstrated

`PlainTextExportVisitor` in this project was written **last**, after the element classes were
finished. Adding that whole feature required:

- zero edits to `DocumentElement`
- zero edits to `Paragraph`, `Image`, `Table`, `CodeBlock`
- zero edits to the other three visitors
- **one new file.**

And the compiler now *enforces* what the ladder merely hoped for: `PlainTextExportVisitor` cannot
compile until it handles every element type. The silent-fall-through bug is not unlikely — it is
**unrepresentable**.

```
--- Plain-text export (a feature added with ZERO element edits) ---
    The Visitor pattern separates an algorithm from the objects it runs on.
    [image: Visitor structure]
    [table: Pattern, Type — 2 rows]
    [java code: element.accept(visitor);]     ← the one the ladder dropped
```

---

## What This Buys You

| | Without Visitor | With Visitor |
|---|---|---|
| Adding an **operation** | edit the interface + every element class | **one new file** |
| Adding an **element** | edit every element-shaped `instanceof` ladder | edit every visitor — *the price* |
| Forgetting a case | compiles, fails silently at runtime | **won't compile** |
| Where one feature lives | smeared across 4 element classes | gathered in 1 visitor class |
| What an element knows | HTML, Markdown, counting, PDF, … | what it *is* |
| Type dispatch | hand-written `instanceof` | the language's, via double dispatch |

That second row is not a footnote — it is the deal you are signing. Read on.

---

## The Trade-off — Be Honest About It

**Visitor makes operations easy to add and elements hard to add.** Every new element type is a new
method on `DocumentVisitor`, and therefore an edit to *every visitor that already exists*. It is the
exact mirror image of the "Without" design, and if you pick the wrong side of that mirror you have
made things worse, not better.

This trade is sometimes called the **expression problem**, and the rule that falls out of it is
simple:

> **Use Visitor when the element hierarchy is stable and the operations keep multiplying.**
> If elements are added often and operations rarely, do the ordinary thing — put the method on the
> class — and walk away.

That condition is exactly the shape of an **AST**, a **document model**, a **shape hierarchy**, a
**bytecode instruction set**: the node types were fixed years ago; the passes over them never stop
coming.

Other costs worth knowing before you commit:

- **Visitors need access to element state.** Either the elements expose getters (a real weakening of
  encapsulation — the elements got more public, not less) or the visitor lives close enough to see
  their internals. There is no free lunch here; the pattern moves the coupling, it doesn't delete it.
- **It's a lot of ceremony.** Two interfaces and an `accept()` in every class, before a single line of
  useful work. For three element types and one operation this is pure loss.
- **Java's `sealed` interfaces + pattern-matching `switch` (Java 17+/21) are a serious rival.** A
  `switch` over a sealed hierarchy gives you the *exhaustiveness check* — the compiler-enforced
  "you forgot `CodeBlock`" — without `accept()` or the visitor interface. That was Visitor's headline
  safety benefit, and modern Java hands it to you for free. Visitor still wins when the hierarchy
  isn't sealed, isn't yours to seal, or when a visitor needs to carry state across an entire
  traversal. **Learn Visitor to understand double dispatch; reach for sealed + `switch` when the
  language allows it.**

---

## The Four Roles

| GoF role | This project |
|---|---|
| **Visitor** | `DocumentVisitor<R>` |
| **Concrete Visitor** | `HtmlExportVisitor`, `MarkdownExportVisitor`, `WordCountVisitor`, `PlainTextExportVisitor` |
| **Element** | `DocumentElement` (the `accept()` method) |
| **Concrete Element** | `Paragraph`, `Image`, `Table`, `CodeBlock` |
| **Object Structure** | `Document` (walks the elements, hands each to the visitor) |

---

## Verify (this project)

The HTML, Markdown and word-count output is **byte-identical** in both projects — the refactor
changed the design, not the behaviour. The only divergence is the plain-text export, which is exactly
where the bug lived:

```
WITHOUT (instanceof ladder)            WITH (visitor)
    [image]                                [image: Visitor structure]
    [table]                                [table: Pattern, Type — 2 rows]
    [unknown element — IGNORED]  ⚠         [java code: element.accept(visitor);]  ✅
```

---

## Where You've Already Used It

- **Compilers and interpreters.** The canonical home. `javac`'s AST, ANTLR's generated visitors,
  Babel's plugins — a parse tree of stable node types, with an endless queue of passes over it:
  type-check, optimise, generate code, format, lint.
- **`java.nio.file.FileVisitor`** — `Files.walkFileTree(path, visitor)` is Visitor with the name on
  the tin, and the object structure is the filesystem.
- **`javax.lang.model.element.ElementVisitor`** — annotation processors visit your source elements.
- **ASM's `ClassVisitor` / `MethodVisitor`** — the bytecode library that most of the JVM ecosystem is
  quietly built on.
- **Static analysers and linters**, near-universally: one AST, dozens of rules, each rule a visitor.

Spot the common thread: in every case, **the node types are stable and the passes are unbounded**.

---

## Visitor vs. Its Neighbours

| Pattern | The actual difference |
|---|---|
| **Iterator** | **Partners, not rivals — and the confusion is worth clearing up.** Iterator answers *"what is the next element?"*; Visitor answers *"what do I do with this element, given its type?"* Iterator hides the *structure* from the client; Visitor hides the *operation* from the structure. `Document.accept()` in this project uses both in three lines: the for-each is Iterator, the `accept()` inside it is Visitor. (See `IteratorDesignPattern/` in this repo.) |
| **Composite** | The classic partner. Composite gives you the tree of stable node types; Visitor is how you run an operation over it without stuffing that operation into every node. Any recursive structure — AST, filesystem, scene graph — tends to attract both. |
| **Strategy** | Both pass behaviour in as an object. But a Strategy is **one** interchangeable algorithm applied to **one** context (`sort(comparator)`). A Visitor is a **family of methods**, one per element type, applied across a **whole structure**. Strategy varies *how*; Visitor varies *what happens to each type*. |
| **Decorator** | Decorator adds behaviour by *wrapping* an object and keeping its interface. Visitor adds behaviour by *visiting* objects and keeping them untouched. Decorator changes what an object does; Visitor never changes the object at all. |
| **Command** | Both make an action into an object. A Command is one action, fully packaged, that you can queue, log or undo. A Visitor is one action *per element type*, and it exists to solve a dispatch problem — not an invocation-timing problem. |
