# Flyweight Design Pattern

> **Use sharing to support large numbers of fine-grained objects efficiently.**

Flyweight is the odd one out among the GoF patterns. Most patterns are about *design* — coupling, extensibility, testability. Flyweight is about **memory**, and nothing else. It is a pure optimisation, and you should only reach for it once you have a measured problem.

**Type:** Structural pattern (it's about how objects are composed to share state).

---

## The Core Idea — Split the State in Two

Take any object and ask of each field: *would this value be identical across thousands of instances, or is it unique to this one?*

| State | Meaning | Example (a tree in a game) | Lives where |
|---|---|---|---|
| **Intrinsic** | Shared, context-independent, **immutable** | species name, colour, 1 KB texture/sprite | In the **flyweight**, created **once** |
| **Extrinsic** | Unique per instance, context-dependent | x/y position on the map | In the **context**, passed **in** |

The pattern is that split. Everything else — the factory, the cache — is plumbing to enforce it.

A forest has 100,000 trees but only **3 species**. Storing the texture on every tree means storing the same 1 KB image 100,000 times. Store it once per species, hand each tree a *reference*, and the 100 MB becomes 3 KB.

---

## The Problem It Solves

See `WithoutFlyweightDesignPattern/Tree.java` — one fat object, every field held per-instance:

```java
public class Tree {
    String name;        // ⚠ "Oak", repeated 33,000 times
    String color;       // ⚠ "Green", repeated 33,000 times
    byte[] texture;     // ⚠ 1 KB image, LOADED AND STORED 100,000 TIMES
    int positionX;      // ✅ genuinely unique
    int positionY;      // ✅ genuinely unique

    Tree(String name, String color, int positionX, int positionY) {
        this.texture = loadTexture(name);   // ⚠ a fresh copy for every single tree
        ...
    }
}
```

Nothing here is *wrong*. It compiles, it runs, it draws the right forest. It just costs **103 MB** to represent data whose actual information content is 100,000 × (2 ints + a species tag).

---

## The Fix — Share the Intrinsic State

### 1. The Flyweight — intrinsic state only, immutable

```java
public class TreeType {                 // FLYWEIGHT
    private final String name;          // final: shared state MUST NOT change
    private final String color;
    private final byte[] texture;

    public void draw(int positionX, int positionY) {   // extrinsic state PASSED IN
        System.out.println("Drawing " + color + " " + name + " at (" + positionX + ", " + positionY + ")");
    }
}
```

Note `draw()` takes the position as a **parameter**. It cannot store it — the object is shared by 33,000 trees, so it has no single position. **A flyweight cannot know where it is.**

### 2. The Factory — the only way to get one

```java
public class TreeTypeFactory {                                  // FLYWEIGHT FACTORY
    private static final Map<String, TreeType> treeTypeCache = new HashMap<>();

    public static TreeType getTreeType(String name, String color) {
        String key = name + "-" + color;
        return treeTypeCache.computeIfAbsent(key, k -> new TreeType(name, color));  // reuse or create
    }
}
```

`computeIfAbsent` is the whole cache: *return the existing one, or make it once and remember it.*

The `TreeType` constructor is deliberately **not `public`** — it signals "get these from the factory, don't `new` them." An un-shared flyweight silently defeats the pattern, so you want that door shut. Be aware, though, that in this project every class sits in the *same package*, so package-private is a convention here rather than a hard barrier — `Main` could still call `new TreeType(...)` if it tried. Move `TreeType` and `TreeTypeFactory` into their own package and the compiler enforces it for real. That is how you'd do it in production.

### 3. The Context — extrinsic state + a reference

```java
public class Tree {                     // CONTEXT
    int positionX;                      // extrinsic: unique
    int positionY;                      // extrinsic: unique
    TreeType treeType;                  // a REFERENCE to shared state — 8 bytes, not 1 KB

    public void draw() {
        treeType.draw(positionX, positionY);   // hands its own extrinsic state to the flyweight
    }
}
```

There are still 100,000 `Tree` objects. That's fine — they're now tiny. The 1 KB texture exists **3 times**, not 100,000 times.

---

## The Four Roles

| GoF role | This project (`WithFlyweightDesignPattern`) | Job |
|---|---|---|
| **Flyweight** | `TreeType` | Holds intrinsic (shared, immutable) state; receives extrinsic state as method args |
| **Flyweight Factory** | `TreeTypeFactory` | Caches and returns flyweights; **guarantees** sharing |
| **Context** | `Tree` | Holds extrinsic (unique) state + a reference to a flyweight |
| **Client** | `Forest` / `Main` | Creates contexts; never instantiates a flyweight itself |

---

## Verify (this project)

`WithoutFlyweightDesignPattern` — 100,000 trees, 3 species:

```
Trees planted        : 100000
Textures loaded      : 100000
Memory used          : 103 MB
```

`WithFlyweightDesignPattern` — the same 100,000 trees, same forest, same output from `draw()`:

```
Trees planted        : 100000
TreeType flyweights  : 3
Textures loaded      : 3
Memory used          : 2 MB
```

**103 MB → 2 MB. 100,000 texture loads → 3.** Identical behaviour; ~50× less memory. (The MB figure is a live-heap reading, so it will wobble a little run to run; the *counts* are exact.)

That is the entire pitch for the pattern, and it's why it lives or dies on measurement.

---

## Variant 2 — The Word Processor (`WithFlyweightDesignPattern02`)

This is the **original GoF example**, and it's the one that best shows *why* the pattern was invented. A document with 130,000 characters in it — how many objects does it really need?

The state splits exactly the same way:

| State | Field | Why |
|---|---|---|
| **Intrinsic** | `symbol`, `fontFamily`, `fontSize`, `color`, `rasterBitmap` | An `'e'` in Times 12pt Black looks *identical* everywhere it appears |
| **Extrinsic** | `row`, `column` | Where on the page this particular `'e'` sits |

```java
public class Glyph {                    // FLYWEIGHT
    private final char symbol;
    private final String fontFamily;
    private final int fontSize;
    private final String color;
    private final byte[] rasterBitmap;  // the rendered image of this character

    public void draw(int row, int column) { ... }   // position passed IN
}

public class TextCharacter {            // CONTEXT
    int row;                            // extrinsic
    int column;                         // extrinsic
    Glyph glyph;                        // shared reference
}
```

`GlyphFactory` caches on `symbol + font + size + color` — the full intrinsic tuple.

**Run it:**

```
Characters typed     : 130015
Glyph flyweights     : 33
Bitmaps rasterized   : 33
Memory used          : 3820 KB

--- first 6 characters of the document ---
Drawing 'D' at (row 0, col 0) [Arial 24pt Red]
Drawing 'e' at (row 0, col 1) [Arial 24pt Red]
...
```

**130,015 characters → 33 objects.** The document is a heading in Arial 24pt Red (11 distinct characters) plus a paragraph in Times 12pt Black, repeated 2,000 times (22 distinct characters). 11 + 22 = 33. Every one of the ~6,000 lowercase `'e'`s in the body is *the same object*.

### The detail worth pausing on

The glyph for `'e'` in **Arial 24pt Red** and the glyph for `'e'` in **Times 12pt Black** are *two different flyweights* — because font and size and colour are all intrinsic, and they differ. This is why the cache key is the whole tuple, not just the character.

And it shows you the pattern's real failure mode: **if you make too much state intrinsic, sharing collapses.** Suppose you decided `color` should be intrinsic *and* you let users colour individual characters arbitrarily — you'd get one flyweight per character per colour, the cache would balloon, and you'd have all the complexity of Flyweight with none of the saving. Whether a field is intrinsic isn't a property of the field; it's a bet about how much your data actually varies. Get the bet wrong and the pattern turns into overhead.

This is also the honest reason real word processors don't do this per-character any more: they store *runs* of styled text instead. Flyweight is still the right teaching example, and the right pattern for glyph rasterisation caches specifically.

---

## The Immutability Rule

**Flyweight state must be immutable, and this is not a stylistic preference — it's a correctness requirement.**

`TreeType`'s fields are `final`. If they weren't, a single line like:

```java
oakType.color = "Red";     // if this compiled...
```

…would turn **every Oak in the forest red at once**, from 33,000 different call sites' point of view, with no local evidence of why. Shared mutable state is the oldest bug in the book, and Flyweight deliberately creates a *lot* of sharing. Immutability is what makes that sharing safe.

Same reason the map key is built from *all* the intrinsic fields (`name + "-" + color`), not just the name: two flyweights that differ in any intrinsic field must be different objects.

---

## Where You've Already Used It

Flyweight is everywhere in the JDK — you have been using it without naming it:

- **`Integer.valueOf(int)`** caches −128..127. This is why `Integer a = 127, b = 127; a == b` is `true`, but at `128` it's `false`. That surprise *is* the Flyweight pattern leaking.
- **String literal pool** — `"hello" == "hello"` is `true`. One shared instance, interned by the compiler.
- **`Boolean.valueOf()`**, **`Character.valueOf()`** — same caching trick.
- Text editors: one `Glyph` per character *shape*, not per character *on screen* (the original GoF example).
- Game engines / particle systems: one sprite, thousands of positions.

---

## Trade-offs & Cautions

- **It buys memory with CPU and complexity.** Every access to intrinsic state is now an indirection, and extrinsic state has to be passed around on every call. Signatures get uglier (`draw(x, y)` instead of `draw()`).
- **Do not apply it speculatively.** If you have 50 trees, the factory and the state-splitting are pure overhead for zero gain. **Profile first.** This is the one GoF pattern where "it made the design cleaner" is not a valid justification — if it isn't saving measurable memory, it's making things worse.
- **The cache is a leak if it's unbounded.** A `HashMap` of flyweights keyed on user input grows forever. Bound it, or use weak references.
- **Thread safety.** A `HashMap`-backed factory is not thread-safe; concurrent `getTreeType()` calls need a `ConcurrentHashMap` (whose `computeIfAbsent` is atomic) or synchronisation. The *flyweights themselves* are thread-safe for free — because they're immutable.
- **It fights the debugger.** Object identity stops meaning what you expect, since thousands of contexts point at one instance.

---

## Flyweight vs. Its Neighbours

| Pattern | Relationship |
|---|---|
| **Singleton** | One shared instance, *globally*. Flyweight is many shared instances, *one per unique intrinsic state*. A Flyweight with exactly one possible state collapses into a Singleton. |
| **Object Pool** | Also reuses objects — but a pool **lends out** objects one borrower at a time, and they're **mutable**. Flyweights are used by everyone **simultaneously** and are **immutable**. Opposite disciplines. |
| **Factory Method** | The Flyweight Factory *is* a factory, but its job is caching, not choosing a subclass. |
| **Composite** | Flyweights are often the **leaves** of a Composite tree — a Glyph in a document, a node in a scene graph. They pair naturally. |
| **Prototype** | Prototype **copies** to avoid re-construction cost. Flyweight **shares** to avoid memory cost. Opposite answers to "don't build this again". |
