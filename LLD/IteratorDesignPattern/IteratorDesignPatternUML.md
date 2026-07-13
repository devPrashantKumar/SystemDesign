# Iterator Design Pattern — UML Diagrams

Iterator's structure is two small parallel hierarchies — **collections** and **walks over
collections** — joined by one factory method.

The thing to look for in the diagrams below is where the **cursor** lives. That is the entire
pattern, and it is one field.

---

## 1. The Canonical Structure

```mermaid
classDiagram
    class Aggregate {
        <<interface>>
        +createIterator() Iterator
    }

    class ConcreteAggregate {
        -elements
        +createIterator() Iterator
    }

    class Iterator {
        <<interface>>
        +hasNext() boolean
        +next() Object
    }

    class ConcreteIterator {
        -cursor
        +hasNext() boolean
        +next() Object
    }

    Aggregate <|.. ConcreteAggregate
    Iterator <|.. ConcreteIterator
    ConcreteAggregate ..> ConcreteIterator : creates
    ConcreteIterator --> ConcreteAggregate : reads privately

    note for ConcreteIterator "the CURSOR lives here —<br/>not in the collection.<br/>This is the whole pattern."
```

`createIterator()` is a **Factory Method**: the collection decides which walk to build. In Java these
are spelled `Iterable.iterator()` and `java.util.Iterator`.

---

## 2. The Problem — `WithoutIteratorDesignPattern`

```mermaid
classDiagram
    class ArrayPlaylist {
        -Song[] songs
        -int count
        +getSongs() Song[]
        +getCount() int
    }

    class LinkedPlaylist {
        -Node head
        +getHead() Node
    }

    class Node {
        <<public — it had to be>>
        +Song song
        +Node next
    }

    class Client {
        +printArrayPlaylist(ArrayPlaylist)
        +printLinkedPlaylist(LinkedPlaylist)
    }

    LinkedPlaylist *-- Node
    Client --> ArrayPlaylist : knows it is an ARRAY
    Client --> LinkedPlaylist : knows it is a CHAIN
    Client --> Node : must walk the nodes itself

    note for Client "TWO methods that do the SAME job.<br/>A 3rd storage type = a 3rd method.<br/>Client grows with STORAGE types."
    note for ArrayPlaylist "getSongs() returns the LIVE array.<br/>Callers can write through it:<br/>getSongs()[0] = null"
```

The client has an arrow to the *internals* of both collections. That is the disease; the duplicated
loop is only the symptom.

---

## 3. The Fix — `WithIteratorDesignPattern`

```mermaid
classDiagram
    class Playlist {
        <<interface>>
        +add(Song) void
        +iterator() Iterator~Song~
    }

    class ArrayPlaylist {
        -Song[] songs
        -int count
        +iterator() Iterator~Song~
        +reverseIterator() Iterator~Song~
    }

    class LinkedPlaylist {
        -Node head
        +iterator() Iterator~Song~
    }

    class Iterator~Song~ {
        <<interface>>
        +hasNext() boolean
        +next() Song
    }

    class ArrayIterator {
        -int index
    }
    class ReverseArrayIterator {
        -int index
    }
    class LinkedIterator {
        -Node current
    }
    class FilteringIterator {
        -Iterator~Song~ source
        -Predicate~Song~ keep
    }

    class Player {
        +play(Playlist) void
        +play(Iterator~Song~) void
    }

    Playlist <|.. ArrayPlaylist
    Playlist <|.. LinkedPlaylist
    Iterator~Song~ <|.. ArrayIterator
    Iterator~Song~ <|.. ReverseArrayIterator
    Iterator~Song~ <|.. LinkedIterator
    Iterator~Song~ <|.. FilteringIterator

    ArrayPlaylist ..> ArrayIterator : creates
    ArrayPlaylist ..> ReverseArrayIterator : creates
    LinkedPlaylist ..> LinkedIterator : creates
    FilteringIterator o--> Iterator~Song~ : wraps ANY iterator

    Player --> Playlist
    Player --> Iterator~Song~

    note for Player "ONE client. Zero knowledge of<br/>arrays, nodes, or sizes."
    note for FilteringIterator "knows no playlist at all —<br/>works over every one of them,<br/>including future ones"
```

**Note the arrows the client does *not* have.** `Player` reaches nothing but two interfaces. The
array and the `Node` chain are now unreachable from outside.

| Role | This project |
|---|---|
| **Iterator** | `java.util.Iterator<Song>` |
| **Concrete Iterator** | `ArrayIterator`, `ReverseArrayIterator`, `LinkedIterator`, `FilteringIterator` |
| **Aggregate** | `Playlist` (extends `Iterable<Song>`) |
| **Concrete Aggregate** | `ArrayPlaylist`, `LinkedPlaylist` |

---

## 4. ASCII — Where the Cursor Lives

```
   WITHOUT ITERATOR                        WITH ITERATOR
   ────────────────                        ─────────────

   ┌──────────────────┐                    ┌──────────────────┐
   │  ArrayPlaylist   │                    │  ArrayPlaylist   │
   │  ───────────────  │                    │  ───────────────  │
   │  Song[] songs    │                    │  Song[] songs    │  ← private, always
   │  int    count    │                    │  int    count    │
   │                  │                    └────────┬─────────┘
   │  getSongs()  ────┼──▶ leaks                    │ creates
   └────────┬─────────┘                             ▼
            │                              ┌──────────────────┐  ┌──────────────────┐
            ▼                              │  ArrayIterator   │  │  ArrayIterator   │
   ┌──────────────────┐                    │  int index = 2   │  │  int index = 0   │
   │      Client      │                    └──────────────────┘  └──────────────────┘
   │  ──────────────  │                       "ahead"               "behind"
   │  knows it's an   │                              ▲   ▲
   │  ARRAY. Writes   │                               ╲ ╱
   │  the loop itself │                                │
   │                  │                        ┌──────────────┐
   │  + a 2nd loop    │                        │    Player    │  ← ONE method
   │    for the chain │                        │  play(...)   │     for every
   └──────────────────┘                        └──────────────┘     collection


   cursor: in the CLIENT's loop            cursor: in the ITERATOR
   → one walk at a time                    → as many simultaneous walks as you like
   → client grows with STORAGE types       → client never changes
```

The naive fix — putting a `currentIndex` field on the *playlist* — looks like it hides the array, and
it does. But it also makes two simultaneous walks **impossible**, because there is only one position
to go around. Moving the cursor into a separate object is what buys both encapsulation *and*
concurrent traversal, and it is the reason the iterator is a class rather than a method.

---

## 5. Sequence — `for (Song s : playlist)`

```mermaid
sequenceDiagram
    participant C as Player (client)
    participant P as LinkedPlaylist (aggregate)
    participant I as LinkedIterator

    C->>P: iterator()
    activate P
    Note over P: Factory Method — the collection<br/>chooses which walk to build
    P->>I: new LinkedIterator()
    Note over I: cursor = head
    P-->>C: Iterator~Song~
    deactivate P

    loop until exhausted
        C->>I: hasNext()
        I-->>C: true
        C->>I: next()
        activate I
        Note over I: read current.song,<br/>advance current = current.next
        I-->>C: Song
        deactivate I
    end

    C->>I: hasNext()
    I-->>C: false
```

This is not a diagram of some framework you might one day use. **This is what the Java compiler
generates for every enhanced `for` loop you write.** The client asks; it never reaches in. Iterator
is a **pull** protocol — which is exactly what separates it from Observer, where the subject pushes.

---

## Key Structural Points

1. **The cursor lives in the iterator, not in the collection.** One field, and everything else
   follows from it. If you find a `currentIndex` on the collection, the pattern is not there.

2. **Two iterators over one collection do not interfere.** They are two objects with two positions.
   The nested-loop / compare-every-pair traversal is only possible because of this.

3. **`createIterator()` is a Factory Method.** The concrete aggregate chooses the concrete iterator.
   The client is handed an interface and never learns which one it got.

4. **The client's arrows stop at the interfaces.** `Player` touches `Playlist` and `Iterator` and
   nothing else — no array, no `Node`, no `count`. That is the intent sentence ("without exposing its
   underlying representation") drawn as a diagram.

5. **Iterators compose.** `FilteringIterator` wraps *any* `Iterator`, so it works over the array, the
   chain, the reversed array, and collections that don't exist yet. This is the seed of
   `java.util.stream`.

6. **In Java, the Aggregate role is `Iterable` and the Iterator role is `java.util.Iterator`.**
   Implement those two and the language hands you the for-each loop. Roll your own interface and you
   get the pattern but lose the syntax — which is almost always the wrong trade outside of learning.
