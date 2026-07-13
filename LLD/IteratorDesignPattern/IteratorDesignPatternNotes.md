# Iterator Design Pattern

> **Provide a way to access the elements of an aggregate object sequentially without exposing its
> underlying representation.**

In one line: **let people walk your collection without telling them how you built it.**

**Type:** Behavioural pattern.

This is the pattern you have used more than any other, and almost certainly without noticing. Every
`for (String s : list)` you have ever written is the Iterator pattern executing.

---

## The Problem — the Collection Leaks Its Guts

Two playlists. Same idea, same data, stored differently — one in an array, one in a chain of nodes:

```java
public class ArrayPlaylist {
    private final Song[] songs = new Song[10];
    private int count = 0;

    public Song[] getSongs() { return songs; }   // ⚠ "here are my internals"
    public int getCount()    { return count; }
}

public class LinkedPlaylist {
    public static class Node {                   // ⚠ this had to become PUBLIC
        public final Song song;
        public Node next;
    }
    public Node getHead() { return head; }       // ⚠ "here is my chain"
}
```

Neither collection can be read without handing out its own structure. And so the client must know
that structure:

```java
// one loop...
Song[] songs = playlist.getSongs();
for (int i = 0; i < playlist.getCount(); i++) { System.out.println(songs[i]); }

// ...and the SAME job, written again, only because the storage differs
for (LinkedPlaylist.Node n = playlist.getHead(); n != null; n = n.next) { System.out.println(n.song); }
```

Four things are now wrong:

1. **The client is coupled to the storage.** `printArrayPlaylist` and `printLinkedPlaylist` do the
   identical job. The client's code grows with the number of **storage types**, which is absurd —
   printing songs has nothing to do with how songs are stored.
2. **Encapsulation is gone, and not just for reading.** `getSongs()` returns the live array. Any
   caller can write through it: `playlist.getSongs()[0] = null;` — and the "Without" `Main` does
   exactly that, corrupting the playlist from the outside. The collection cannot defend its own
   invariants.
3. **You can't change your mind.** Swap the array for an `ArrayList` and every client breaks. The
   representation is now part of your public API, permanently.
4. **You get one traversal, and only one at a time.** Want to walk it backwards? Walk only the long
   songs? Walk it in two places at once? Every one of those means more methods on the collection, or
   a position field inside the collection — and a position field inside the collection means two
   simultaneous walks are simply impossible.

---

## The Fix — Make Traversal Its Own Object

### 1. The Iterator — a cursor, extracted into a thing

```java
public interface Iterator<T> {        // ITERATOR  (this is java.util.Iterator)
    boolean hasNext();
    T next();
}
```

Two methods. That is the entire pattern's public surface, and it is enough to walk an array, a linked
list, a tree, a database cursor, a paginated HTTP API, or an infinite sequence.

### 2. The Aggregate — a collection that can produce one

```java
public interface Playlist extends Iterable<Song> {    // AGGREGATE
    void add(Song song);

    @Override
    Iterator<Song> iterator();                        // GoF calls this createIterator()
}
```

**Look at what this interface does not say.** No array. No node. No size. No index. A caller cannot
tell how a `Playlist` stores anything — which is precisely the point of the intent sentence.

### 3. The Concrete Iterator — and the one detail that matters

```java
public class ArrayPlaylist implements Playlist {
    private Song[] songs = new Song[4];               // genuinely private now
    private int count = 0;

    @Override
    public Iterator<Song> iterator() { return new ArrayIterator(); }

    private class ArrayIterator implements Iterator<Song> {
        private int index = 0;                        // ← THE CURSOR LIVES HERE

        public boolean hasNext() { return index < count; }
        public Song next() {
            if (!hasNext()) throw new NoSuchElementException();
            return songs[index++];
        }
    }
}
```

**`index` is a field of the iterator, not of the playlist.** That single decision is what the whole
pattern turns on. Because the position belongs to the *walk* and not to the *collection*, you can
start two walks over one collection and they will not interfere:

```java
Iterator<Song> ahead  = workout.iterator();
Iterator<Song> behind = workout.iterator();
ahead.next(); ahead.next();
ahead.next();    // → Stronger
behind.next();   // → Thunderstruck    (untouched by the other iterator)
```

If the playlist held a `currentIndex` field — the naive design everyone writes first — that nested
loop is unwritable. Every collection would support exactly one traversal at a time, forever.

### 4. The Client — written once, for every collection that will ever exist

```java
public void play(Playlist playlist) {
    for (Song song : playlist) {          // works on ArrayPlaylist. And LinkedPlaylist.
        System.out.println("▶ " + song);  // And the one you write next year.
    }
}
```

`LinkedPlaylist` walks a chain of nodes; `ArrayPlaylist` indexes an array. `Player` cannot tell the
difference, and never asks.

---

## What This Buys You

| | Without Iterator | With Iterator |
|---|---|---|
| Client loops needed | **one per storage type** | **one, ever** |
| Adding a 3rd collection type | every client gains a loop | no client changes |
| Are the internals private? | No — array and `Node` are public | Yes — nothing escapes |
| Can a caller corrupt it? | Yes — `getSongs()[0] = null` | No |
| Can you change the storage? | No — it's in your public API | Yes — it's invisible |
| Traversing backwards | new method on the collection | new iterator, collection untouched |
| Two walks at once | impossible | natural — two iterator objects |

---

## Traversal Becomes Composable

Once "walking" is an interface rather than a loop, walks can be **built out of other walks**:

```java
public class FilteringIterator implements Iterator<Song> {
    private final Iterator<Song> source;      // ← knows NOTHING about any playlist
    private final Predicate<Song> keep;
    ...
}

player.play(new FilteringIterator(chill.iterator(), song -> song.getSeconds() > 300));
```

`FilteringIterator` has never heard of `ArrayPlaylist` or `LinkedPlaylist`. It only knows `Iterator`.
So it works over the array, over the linked list, over the *reversed* array, and over every
collection added in future — including ones written after it.

This is the whole idea behind `java.util.stream`: `filter`, `map`, `limit` and friends are iterators
wrapping iterators. Iterator is the primitive that makes streams possible.

---

## The Four Roles

| GoF role | This project | Java's name for it |
|---|---|---|
| **Iterator** | `java.util.Iterator<Song>` | `Iterator` |
| **Concrete Iterator** | `ArrayIterator`, `LinkedIterator`, `ReverseArrayIterator`, `FilteringIterator` | — |
| **Aggregate** | `Playlist` | `Iterable` |
| **Concrete Aggregate** | `ArrayPlaylist`, `LinkedPlaylist` | `ArrayList`, `HashSet`, … |

Two naming notes worth having straight:

- **GoF's `createIterator()` is Java's `iterator()`.** Same method, different decade.
- **GoF's `Aggregate` is Java's `Iterable`.** If your class implements `Iterable`, you have
  implemented the Aggregate role — and the compiler rewards you by letting `for (Song s : playlist)`
  compile. The enhanced for loop is *defined* in terms of this pattern.

---

## Verify (this project)

The client method is the same one in both blocks below — only the data structure differs:

```
--- Workout (array-backed) ---
    ▶ Thunderstruck - AC/DC (292s)
    ▶ Lose Yourself - Eminem (326s)
    ▶ Stronger - Kanye West (312s)

--- Chill (linked-node-backed) ---
    ▶ Weightless - Marconi Union (485s)
    ▶ Intro - The xx (128s)
    ▶ Redbone - Childish Gambino (327s)

--- two simultaneous iterators, independent positions ---
    ahead  is at: Stronger
    behind is at: Thunderstruck
```

And in the "Without" version, watch the playlist get corrupted from the outside:

```
--- a caller reaches into the array and breaks the playlist ---
    null                        ← someone did playlist.getSongs()[0] = null
    Lose Yourself - Eminem
```

---

## Where You've Already Used It

- **`for (String s : list)`** — the enhanced for loop. It compiles down to `iterator()`, `hasNext()`,
  `next()`. You cannot write Java without using this pattern.
- **The entire Collections Framework.** `ArrayList`, `HashSet`, `TreeMap`, `LinkedList` — wildly
  different internals, one traversal interface. That is why `Collections.sort`, `String.join`, and
  every for-each loop work on all of them.
- **`java.util.Scanner`, `BufferedReader.lines()`, `Files.lines()`** — iterators over things that
  aren't collections at all.
- **`java.util.stream.Stream`** — iterators composed onto iterators, plus laziness.
- **Database cursors and paginated APIs.** `ResultSet.next()` is `hasNext()`/`next()` wearing a
  different name, walking rows that aren't in memory yet. This is Iterator's best trick: the
  collection **need not exist**.

---

## Trade-offs & Cautions

- **Don't write one when the language gives you one.** In Java, implement `Iterable` and return a
  `java.util.Iterator`. Hand-rolling a `MyIterator` interface with `hasNext()`/`next()` gets you a
  class that `for`-each cannot use — you've reimplemented the pattern and lost the language support.
  Write your own interface only to *learn* it (as the notes above do) or when you genuinely need
  something `Iterator` can't express.
- **An iterator is a snapshot of a position, not of the data.** If the collection changes underneath
  it, the iterator is looking at a moving target. Java's answer is **fail-fast**: `ArrayList`'s
  iterator tracks a `modCount` and throws `ConcurrentModificationException` if you mutate the list
  mid-loop. That exception is not a bug in your loop; it is this pattern's known weak spot being
  policed. If you must remove while iterating, use `iterator.remove()` — the only safe way in.
- **Overkill for a fixed, simple, internal structure.** If a class holds a `List` and only its own
  package walks it, a getter is fine. The pattern earns its keep when the *storage* might change,
  when there are several storages, or when clients are outside your control.
- **`next()` must throw `NoSuchElementException` when exhausted**, not return `null`. Returning
  `null` makes the iterator lie about its contract and pushes the bug downstream.

---

## Iterator vs. Its Neighbours

| Pattern | The actual difference |
|---|---|
| **Composite** | The classic pairing. A Composite is a *tree*; an Iterator is how you *walk* it — and the walk is where depth-first vs breadth-first lives. Keeping traversal out of the tree means one tree can be walked several ways. (See `CompositeDesignPattern/` in this repo — its `FileSystem` tree is exactly the kind of structure an iterator belongs on.) |
| **Visitor** | Both traverse a structure. **Iterator answers "what's the next element?"; Visitor answers "what do I do with this element, given its type?"** Iterator hides the structure from the client; Visitor hides the *operation* from the structure. They compose: iterate to reach elements, visit to act on them. |
| **Factory Method** | `createIterator()` **is** a Factory Method: the aggregate decides which concrete iterator to build. Iterator is one of the clearest real uses of Factory Method in the GoF catalogue. |
| **Strategy** | A `FilteringIterator` or a `ReverseArrayIterator` is close to a traversal *strategy* — a swappable algorithm for walking. The line: Strategy varies **how you do a job**; Iterator specifically varies **how you reach the elements**, and additionally holds the position. |
| **Observer** | Both decouple a producer from a consumer, but the direction is opposite. **Iterator is pull** — the client asks for the next element. **Observer is push** — the subject decides when to hand something over. |
