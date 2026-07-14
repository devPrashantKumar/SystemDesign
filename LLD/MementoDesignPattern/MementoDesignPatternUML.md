# Memento Design Pattern — UML Diagrams

Memento is three objects and one rule.

The three objects are the **Originator** (has the state), the **Memento** (holds a copy of it) and
the **Caretaker** (keeps the mementos). The rule is: **the caretaker never opens the box.**

The thing to look for in the diagrams below is which arrows point *into* the originator's state. In
the "Without" version there are many. In the fix there are none.

---

## 1. The Canonical Structure

```mermaid
classDiagram
    class Originator {
        -state
        +save() Memento
        +restore(Memento) void
    }

    class Memento {
        -state
        +getMetadata() ~narrow~
    }

    class Caretaker {
        -List~Memento~ history
    }

    Originator ..> Memento : creates
    Originator ..> Memento : reads (WIDE interface)
    Caretaker o--> Memento : holds (NARROW interface)
    Caretaker ..> Originator : save() / restore()

    note for Memento "TWO FACES:<br/>WIDE for the Originator — every field.<br/>NARROW for everyone else — metadata only.<br/>In Java: a nested class. The enclosing class<br/>can read its privates; nobody else can."
    note for Caretaker "Holds the box.<br/>CANNOT open it.<br/>Knows zero fields of the Originator."
```

The two arrows from `Originator` to `Memento` are the pattern: it **creates** the box and it is the
only thing that can **read** it. The caretaker's arrow is a `holds` and nothing more.

---

## 2. The Problem — `WithoutMementoDesignPattern`

```mermaid
classDiagram
    class Editor {
        -String content
        -int cursor
        -int selectionEnd
        +getContent() String
        +getCursor() int
        +getSelectionEnd() int
        +setContent(String) void
        +setCursor(int) void
        +setSelectionEnd(int) void
    }

    class History {
        -List~String~ contents
        -List~Integer~ cursors
        +save(Editor) void
        +undo(Editor) void
    }

    class AnyOtherCode {
        the rest of the program
    }

    History ..> Editor : reads EVERY field
    History ..> Editor : writes EVERY field
    AnyOtherCode ..> Editor : setCursor(9999) 😈

    note for Editor "6 accessors that exist ONLY for undo.<br/>They are not features — they are a HOLE,<br/>and it is open to the whole program."
    note for History "Stores the editor's state as PARALLEL LISTS.<br/>It had to take the object apart to hold it.<br/>⚠ selectionEnd was added to Editor later.<br/>Nobody came back here. Undo now half-works,<br/>silently."
```

Two separate failures, and they share one cause — the state was pulled *out* of the object:

- **the hole**: `setCursor()` was added for undo, but it is public, so anyone can put the editor into
  a state it could never reach by typing;
- **the silent bug**: `History` enumerates the editor's fields by hand, so it can forget one — and
  did.

---

## 3. The Fix — `WithMementoDesignPattern`

```mermaid
classDiagram
    class Editor {
        -String content
        -int cursor
        -int selectionEnd
        +type(String) void
        +selectLastWord() void
        +save(String label) Memento
        +restore(Memento) void
    }

    class Memento {
        <<nested inside Editor>>
        -String label
        -String content
        -int cursor
        -int selectionEnd
        +getLabel() String
    }

    class History {
        -Deque~Memento~ undoStack
        -Deque~Memento~ redoStack
        +save(Editor, String) void
        +undo(Editor) void
        +redo(Editor) void
    }

    Editor *-- Memento : creates & is the ONLY reader
    History o--> Memento : holds — opaque
    History ..> Editor : save() / restore()

    note for Editor "ZERO setters.<br/>The hole is closed —<br/>and undo still works."
    note for Memento "getLabel() is the ENTIRE public surface.<br/>memento.content does not compile<br/>outside Editor. Verified:<br/>'error: content has private access in Memento'"
    note for History "No String content. No int cursor.<br/>Not one field of the Editor's.<br/>Add a field to Editor → this class<br/>does not change. It cannot even tell."
```

| Role | This project |
|---|---|
| **Originator** | `Editor` |
| **Memento** | `Editor.Memento` (nested, immutable) |
| **Caretaker** | `History` |

---

## 4. ASCII — Who Can See the State?

```
   WITHOUT MEMENTO                          WITH MEMENTO
   ───────────────                          ────────────

   ┌───────────────────────┐                ┌───────────────────────┐
   │       Editor          │                │       Editor          │
   │  ───────────────────  │                │  ───────────────────  │
   │  content              │                │  content              │
   │  cursor          ┌────┼── get ──┐      │  cursor               │  ← private. Full stop.
   │  selectionEnd    │    │         │      │  selectionEnd         │
   │                  └────┼── set ──┤      │                       │
   │  ⚠ 6 accessors        │         │      │  save()    ──────┐    │
   │    cut into the class │         │      │  restore(m) ◀──┐ │    │
   └───────────────────────┘         │      └────────────────┼─┼────┘
              ▲                      │                       │ │
              │ setCursor(9999)      ▼                       │ ▼
   ┌──────────┴─────────┐   ┌────────────────┐               │  ┌──────────────────┐
   │  ANY code at all   │   │    History     │               │  │     Memento      │
   │  can corrupt it 😈 │   │  ────────────  │               │  │  ──────────────  │
   └────────────────────┘   │ List<String>   │               │  │  content         │
                            │ List<Integer>  │               │  │  cursor          │
                            │ ⚠ forgot       │               │  │  selectionEnd    │
                            │   selectionEnd │               │  │  ─────────────   │
                            └────────────────┘               │  │  getLabel() ← ALL
                                                             │  │   anyone else gets
                            History KNOWS the fields.        │  └────────┬─────────┘
                            It has to. That's why it         │           │ holds, opaque
                            can forget one.                  │           ▼
                                                             │  ┌──────────────────┐
                                                             └──│     History      │
                                                                │  ──────────────  │
                                                                │ Deque<Memento>   │
                                                                │ Deque<Memento>   │
                                                                │                  │
                                                                │ knows NOTHING    │
                                                                │ about Editor's   │
                                                                │ fields           │
                                                                └──────────────────┘

   state is pulled OUT of the object          the object copies ITSELF into a sealed box
   → the object must be opened up             → the object stays closed
   → the copier can forget a field            → forgetting a field is impossible
```

**The whole pattern is the direction of that copy.** In the "Without" design the caretaker *pulls*
state out, so the editor must open up and the caretaker must know the fields. In the fix the editor
*pushes* a copy of itself into a box, so it stays closed and nobody else ever needs to know what's
inside.

---

## 5. Sequence — Type, Snapshot, Undo

```mermaid
sequenceDiagram
    participant C as Client
    participant H as History (caretaker)
    participant E as Editor (originator)
    participant M as Memento

    C->>H: save(editor, "after first sentence")
    activate H
    H->>E: save("after first sentence")
    activate E
    Note over E: the editor snapshots ITSELF —<br/>all 3 fields, in one place,<br/>3 lines from where they're declared
    E->>M: new Memento(label, content, cursor, selectionEnd)
    E-->>H: Memento
    deactivate E
    Note over H: pushes the box onto undoStack.<br/>Never opens it. CANNOT open it.
    deactivate H

    C->>E: type(" jumps over the lazy dog")
    Note over E: state changes

    C->>H: undo(editor)
    activate H
    H->>E: save("before undo")
    E-->>H: Memento
    Note over H: (that one goes on redoStack —<br/>redo is free once state is a value)
    H->>E: restore(undoStack.pop())
    activate E
    Note over E,M: only the Editor can read m.content,<br/>m.cursor, m.selectionEnd.<br/>ALL THREE come back, or none do.
    E->>M: read (WIDE interface)
    M-->>E: state
    deactivate E
    deactivate H

    Note over E: "The quick brown fox"<br/>[cursor=19, selectionEnd=19]  ✅ consistent
```

Compare the last note with the "Without" run, where the same undo produced
`[cursor=19, selectionEnd=43]` — a document 19 characters long with a selection running to
character 43.

---

## Key Structural Points

1. **The originator snapshots itself.** That single reversal is the pattern. State and the code that
   captures it live in the same class, so a field cannot be forgotten — the way `History` forgot
   `selectionEnd` in the "Without" project.

2. **Restore is all-or-nothing.** The caretaker hands the whole box back; it cannot restore two
   fields out of three, because it cannot see fields at all. Partial restore isn't discouraged here —
   it's **unrepresentable**.

3. **Wide interface for the originator, narrow for everyone else.** In Java a nested class gives you
   this with no ceremony: the enclosing class reads its privates, and nobody else can — verified,
   not assumed:
   `error: content has private access in Memento`.

4. **The caretaker holds values, not fields.** `History` has no `String content` and no `int cursor`.
   Add a field to `Editor` and `History` doesn't change; it cannot even detect that anything did.

5. **The memento is immutable and dead.** All fields `final`, no setters — a snapshot that can be
   edited after the fact is not a snapshot. And unlike a Prototype's clone, it is not a usable
   object: you cannot type into a `Memento`. Its only purpose is to be handed back.

6. **Zero setters on the originator.** This is the acceptance test for the pattern. If your
   originator still has a setter that exists "for undo", the hole is still open and you have written
   the "Without" project with more classes.
