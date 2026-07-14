# Memento Design Pattern

> **Without violating encapsulation, capture and externalize an object's internal state so
> that the object can be restored to this state later.**

In one line: **save an object's state without opening the object up.**

**Type:** Behavioural pattern.

Read the intent sentence again and notice that the interesting clause comes *first*. Anyone can
implement undo by making everything public. **Memento is the pattern for doing it without that.**
Take away "without violating encapsulation" and there is no pattern left — just a struct.

---

## The Problem — Undo Cuts a Hole in Your Object

An editor's state is three fields that must be restored **together**: the text, the cursor, and the
selection. Restore the text but not the cursor and you haven't undone anything — you've corrupted the
document.

To let some *other* class snapshot and restore that, the editor has to open itself up completely:

```java
public class Editor {
    private String content;
    private int cursor;
    private int selectionEnd;

    public String getContent() { ... }          // ⚠ these exist ONLY so that
    public int getCursor() { ... }              //    History can do its job.
    public int getSelectionEnd() { ... }        //    They are not features.

    public void setContent(String c) { ... }    // ⚠ and these are worse
    public void setCursor(int c) { ... }
    public void setSelectionEnd(int e) { ... }
}
```

**Those setters are the disease.** They were added for undo, but they are public, so *any* code can
now do this:

```java
editor.setContent("");
editor.setCursor(9999);        // empty document, cursor at character 9999
```

The editor can no longer defend its own invariants, because a hole was deliberately cut in it — for
a feature that has nothing to do with editing text.

### And then the bug that this design invites

The caretaker has to take the editor **apart** to store it, so it keeps parallel lists — one per
field:

```java
public class History {
    private final List<String>  contents = new ArrayList<>();
    private final List<Integer> cursors  = new ArrayList<>();
    // selectionEnd was added to Editor later. Nobody came back here.  ⚠
}
```

Run the "Without" project and watch undo half-work:

```
typed more   : "The quick brown fox jumps over the lazy dog" [cursor=43, selectionEnd=43]
after undo   : "The quick brown fox"                         [cursor=19, selectionEnd=43]
                                                                            ^^^^^^^^^^^^
               content is 19 characters. selectionEnd is 43. The editor is now in a state
               it could NEVER have reached by typing — and nothing threw, nothing warned.
```

**This is not a careless-programmer bug — it is a design bug.** Any design that makes one class
enumerate another class's fields by hand will eventually miss one, and it will miss it *silently*.

So the "Without" version manages to fail in both directions at once:

1. **Encapsulation is gone** — the editor is wide open to everybody, forever.
2. **The state is still restored incorrectly** — because the class doing the restoring isn't the
   class that owns the state.

---

## The Fix — the Object Snapshots *Itself*

The insight is small and it turns everything:

> **Nobody reaches into the editor. The editor hands out a sealed box.**

### 1. The Memento — a sealed box

```java
public static final class Memento {
    private final String label;
    private final String content;        // ← private. To EVERYONE except Editor.
    private final int cursor;
    private final int selectionEnd;

    public String getLabel() { return label; }   // ← the only public method
}
```

This is a **nested class inside `Editor`**, and that placement is doing real work. Java lets an
enclosing class read a nested class's private fields — and nobody else, **not even another class in
the same package**. That is a compiler guarantee, not a convention:

```
Probe.java:6: error: content has private access in Memento
        System.out.println(m.content);
```

### 2. The Originator — it saves and restores itself

```java
public Memento save(String label) {
    return new Memento(label, content, cursor, selectionEnd);
}

public void restore(Memento memento) {
    this.content      = memento.content;      // compiles HERE
    this.cursor       = memento.cursor;       // and nowhere else on earth
    this.selectionEnd = memento.selectionEnd;
}
```

**Look at what just became impossible.** The editor's state and the code that captures it now live in
the same class, three lines apart. Add a `fontSize` field and you will be staring straight at that
constructor call when you do. The "Without" project's silent partial restore isn't fixed here — it is
**structurally unavailable**.

And every setter is gone. `setCursor()` does not exist. The hole is closed, and undo still works.

### 3. The Caretaker — it holds boxes it cannot open

```java
public class History {
    private final Deque<Editor.Memento> undoStack = new ArrayDeque<>();
    private final Deque<Editor.Memento> redoStack = new ArrayDeque<>();

    public void undo(Editor editor) {
        redoStack.push(editor.save("before undo"));
        editor.restore(undoStack.pop());     // ← hands the WHOLE box back
    }
}
```

**Read that class and notice what isn't in it**: no `String content`, no `int cursor` — not one field
of the editor's, anywhere. It holds opaque values. Add a field to `Editor` and `History` does not
change; it cannot even tell that anything happened.

What's left is its *real* job — the **policy** of history: what order, how deep, when to discard,
undo versus redo. That is a genuine responsibility, and it is now the only one it has.

---

## Wide Interface vs. Narrow Interface

This is GoF's own terminology and it is the cleanest way to hold the pattern in your head. **The
memento has two audiences and shows each of them a different face:**

| | Sees | Can do |
|---|---|---|
| **Originator** (`Editor`) | the **wide** interface — every field | read the state back out, restore itself |
| **Caretaker** (`History`) | the **narrow** interface — `getLabel()` | hold it, stack it, count it, label it, hand it back |
| **Everyone else** | the narrow interface | the same, and nothing more |

Java's nested classes give you this for free, which is why the `Memento` class lives *inside*
`Editor`. In a language without that, you'd express the same split with two interfaces — a wide one
the originator downcasts to, and a narrow one everyone else holds.

---

## What This Buys You

| | Without Memento | With Memento |
|---|---|---|
| Setters on the object | one per field, **public forever** | **none** |
| Can outside code corrupt it? | Yes — `setCursor(9999)` | No — the state is unreachable |
| Who captures the state? | the caretaker, field by field | **the object itself**, all at once |
| Add a new field | update Editor **and** History; forget one → silent bug | update Editor. That's it. |
| Partial restore possible? | yes, and it happens (see the output) | **no — it's all-or-nothing** |
| What the caretaker knows | every field the editor has | that it is holding *something* |
| Redo | more parallel lists | move a box between two stacks |

---

## The Three Roles

| GoF role | This project | Its one job |
|---|---|---|
| **Originator** | `Editor` | owns the state; creates and accepts mementos |
| **Memento** | `Editor.Memento` | an immutable, opaque snapshot |
| **Caretaker** | `History` | stores mementos; **never looks inside one** |

The caretaker rule is the one people break, and it's worth saying flatly: **if your caretaker ever
reads a field out of a memento, you have written the "Without" project with extra steps.**

---

## Verify (this project)

The whole pattern shows up in one line of output. Same edit, same undo, both projects:

```
WITHOUT:  after undo : "The quick brown fox" [cursor=19, selectionEnd=43]   ⚠ inconsistent
WITH:     after undo : "The quick brown fox" [cursor=19, selectionEnd=19]   ✅ all three, together
```

And in the "With" project, redo falls out almost for free, because a snapshot is just a value:

```
    undo → restored: after first sentence
after undo   : "The quick brown fox" [cursor=19, selectionEnd=19]
    redo → restored: before undo
after redo   : "The quick brown fox jumps over the lazy dog" [cursor=43, selectionEnd=43]
```

---

## Trade-offs & Cautions

- **Mementos cost memory, and the caretaker is who pays.** A full snapshot per keystroke in a large
  document is a real problem. The standard answers: cap the stack depth, snapshot on coarse
  boundaries (per word, per command) rather than per character, or store **deltas** instead of full
  states — at which point you are leaving Memento for something closer to an event log.
- **Snapshot deeply, or don't bother.** `content` is a `String` — immutable, so copying the reference
  *is* a copy. The moment your state includes a `List` or a mutable object, storing the reference
  gives you a memento that changes as the originator changes, which is not a memento at all. Copy the
  contents, or make the state immutable. This is the single most common way to get Memento subtly,
  invisibly wrong.
- **Keep the memento immutable.** All fields `final`, no setters. A mutable snapshot can be edited
  after it was taken, which defeats the point.
- **It says nothing about *when* to snapshot.** That's the caretaker's problem, and getting it wrong
  (snapshotting after the mutation instead of before) produces an undo that is always one step out of
  phase — a classic, maddening bug that the pattern will not save you from.
- **Not everything needs it.** If an object is a bag of public data with no invariants to protect,
  copy it and move on. Memento earns its keep when the object has **state worth protecting** — which
  is exactly when a plain copy would have forced you to unprotect it.

---

## Where You've Already Used It

- **Undo/redo in every editor you've ever used** — the canonical case, and the one this project models.
- **Database transactions and savepoints.** `SAVEPOINT` / `ROLLBACK TO SAVEPOINT` is Memento at
  database scale: the engine snapshots itself, hands you an opaque handle, and takes it back later.
  You cannot read the snapshot; you can only name it.
- **Game save states / checkpoints** — the save file is a memento, and it is deliberately opaque.
- **`java.io.Serializable`** is a mechanism people press into this shape (serialize the state, hold
  the bytes, deserialize to restore). It works, and it's a good illustration of the *idea* — but the
  bytes are far less opaque than a proper memento, and Java serialization has enough problems of its
  own that it's a poor default.
- **Version control.** A commit is a memento of the working tree; `git checkout` is `restore()`.

---

## Memento vs. Its Neighbours

| Pattern | The actual difference |
|---|---|
| **Command** | **The classic pairing — undo is usually both.** A Command knows *what was done*; a Memento knows *what things were like before*. Two ways to undo: replay a Command's inverse (`undo()` on the command — cheap, but you must be able to write an exact inverse, and many operations don't have one), or restore a Memento (always works, costs memory). Real editors use both: Commands for the actions, Mementos for the states you can't invert. See `CommandDesignPattern/` in this repo — its `undo()` is the inverse-operation approach; this project is the snapshot approach. |
| **Prototype** | Both copy an object's state, and they look nearly identical from outside. The difference is **who the copy is for and what it's for**. A Prototype's clone is a **new, usable object** you go on to work with. A Memento is a **dead snapshot** — it isn't an editor, you can't type into it, and its only purpose is to be given back. (See `PrototypeDesignPattern/`.) |
| **Iterator** | An iterator holds a *position* in a collection; a memento holds a *state* of an object. Both are "an object that remembers something on your behalf" — but Iterator is a cursor you advance, Memento is a photograph you keep. |
| **State** | Easy to confuse by name alone, and they're unrelated. **State** changes what an object *does* as its situation changes. **Memento** doesn't change behaviour at all — it lets you rewind it. |
