# Object Pool Design Pattern

> **Reuse a fixed set of expensive-to-create objects instead of creating and destroying them on
> every use. Clients borrow an object from the pool and return it when done.**

In one line: **the pool owns the object's lifecycle; the client only borrows.**

**Type:** Creational pattern.

**⚠ First, an honesty note: this is not a GoF pattern.** It isn't in *Design Patterns* (1994). It comes
out of the resource-management literature and is usually credited to Kirk/Grand and the Pooling pattern
in POSA. It's included in every modern pattern catalogue because it's genuinely everywhere — JDBC
connection pools, thread pools, buffer pools — but if someone asks you to name the 23 GoF patterns,
this isn't one of them.

---

## The Problem — Nobody Owns the Lifecycle

Some objects are **expensive to create** and **scarce**. A database connection is the canonical case:
opening one means a TCP handshake, a TLS negotiation and an auth round-trip — call it 120ms — and the
server has a hard cap on how many can exist at once.

The obvious code does the obvious thing:

```java
for (int i = 1; i <= 8; i++) {
    DatabaseConnection connection = new DatabaseConnection();   // ⚠ 120ms. Every time.
    connection.query("SELECT * FROM orders WHERE id = " + i);
    connection.close();                                          // ⚠ and thrown away
}
```

Run it:

```
  connections created : 8 for 8 requests
  wall time           : 1080ms
  ⚠ ~960ms of that was handshakes. The actual queries took ~80ms.
```

**Nine tenths of the time went into building and destroying objects.** Each request paid the full setup
cost of a thing the *previous* request had already finished building — and then threw away.

### The second failure, which is the serious one

Then traffic spikes. Ten requests arrive at once, each doing what it was told: open a connection.

```
=== a traffic spike: 10 concurrent requests (server max is 5) ===
    request-6  ✗ FATAL: too many connections (server max is 5)
    request-7  ✗ FATAL: too many connections (server max is 5)
    request-8  ✗ FATAL: too many connections (server max is 5)
    request-9  ✗ FATAL: too many connections (server max is 5)
    request-10 ✗ FATAL: too many connections (server max is 5)

  requests rejected : 5 of 10   ⚠
```

Half the traffic is dropped on the floor. Note *how* it failed: not slowly, not degraded — **rejected**.

### One root cause, two symptoms

> **Nobody owns the lifecycle.**

Every caller creates and destroys its own connection. So:

- the expensive setup is repaid on every request (nothing survives to be reused), **and**
- there is no single place that could ever say *"that's enough — wait your turn"*.

Both symptoms follow from the same missing thing. That's why you can't fix this by making the
constructor faster or by adding a check at the call site — you fix it by **moving ownership**.

---

## The Fix — Borrow, Don't Build

```java
DatabaseConnection connection = pool.acquire();
try {
    connection.query("SELECT * FROM orders WHERE id = " + i);
} finally {
    pool.release(connection);       // NOT close(). It goes back on the shelf, still open.
}
```

Callers never write `new DatabaseConnection()` and never call `close()`. One object — the pool — does
both. That single change buys three things at once:

### 1. Reuse

A released connection goes back on the shelf **still open**. The next borrower pays 0ms instead of
120ms.

```
  connections created : 1 for 8 requests   ✅ (was 8)
  served off the shelf: 7 × 0ms instead of 120ms
  wall time           : 238ms   ✅ (was 1080ms)
```

Worth saying precisely, because people get this backwards: **the pool did not make creation cheaper.
It made creation rarer.** The 120ms constructor is untouched — it just runs once instead of eight times.

### 2. A cap

The pool creates at most `maxSize` objects, **ever**. Exceeding the database's limit is no longer a
thing that can happen — not because anyone remembered to check, but because there is nowhere left in
the code that could create connection number six.

### 3. Backpressure — and this is the one people forget

When all three are lent out, the fourth caller **waits**:

```java
connection = available.poll(timeoutMillis, TimeUnit.MILLISECONDS);
```

Same 10-request spike, through a pool of 3:

```
  completed : 10 of 10   ✅ (5 were REJECTED without the pool)
  live connections to the database: 3   ✅ never above the cap
```

**Under load the pooled version queues. The unpooled version failed outright.** A pool is not only a
performance optimisation — it is a *concurrency limiter*, and that's often the more valuable half.

---

## The Two Things a Pooled Object Must Have

This is the part most write-ups skip, and it's where pooling actually bites people.

### `reset()` — because the object is handed to a *stranger* next

A returned object doesn't die; it goes back on the shelf and gets given to someone else. Anything the
last borrower left on it goes with it.

```java
public void release(DatabaseConnection connection) {
    connection.reset();               // ← state from the last borrower dies HERE
    available.offer(connection);
}
```

```
=== why release() must reset ===
    borrower A sets sessionUser = alice
    borrower B gets conn-3, sessionUser = null   ✅ wiped by release()
    ⚠ Without that reset(), borrower B would be running as alice.
```

**"The pooled object wasn't reset" is one of the nastiest bug classes there is**, because it only
appears under load and it looks like data corruption rather than a lifecycle bug. One user's session,
one user's open transaction, silently becoming another user's.

### `isValid()` — because an idle object can go stale

The database will happily kill a connection that has been quiet for ten minutes, and a naive pool will
happily hand you the corpse. **Never lend out an object you haven't checked.** Real pools also evict
idle objects and keep a minimum number warm.

---

## What This Buys You

| | Without the pool | With the pool |
|---|---|---|
| Objects created for 8 requests | 8 | **1** |
| Wall time | 1080ms | **238ms** |
| Under a 10-request spike | **5 rejected** | 10 completed |
| Connections to the DB | unbounded → past the server's cap | **capped at 3, always** |
| When capacity runs out | fails | **queues** |
| Who owns the lifecycle | every caller | **the pool** |

---

## The Three Roles

| Role | This project |
|---|---|
| **Pool (Reusable Pool)** | `ConnectionPool` — owns creation, hand-out, return, destruction |
| **Reusable** | `DatabaseConnection` — expensive; must expose `reset()` and `isValid()` |
| **Client** | `Main` — `acquire()` … `finally { release(); }`. Never constructs, never closes. |

---

## Verify (this project)

```
WITHOUT:  8 connections, 1080ms,  5 of 10 spike requests REJECTED
WITH:     1 connection,   238ms, 10 of 10 spike requests completed
```

---

## Trade-offs & Cautions

- **You just re-introduced manual memory management.** This is the real price, and it deserves to be
  said plainly: a borrowed object **must** come back. Every `acquire()` needs a `release()` in a
  `finally`. Forget one and it's a leak; forget enough and the pool starves and every request hangs
  forever. Garbage collection is precisely the thing that saved you from this, and pooling hands the
  problem straight back.
- **Use-after-release is the other half of that.** A caller that keeps a reference after releasing is
  now sharing an object with whoever borrowed it next — a data race that is very hard to see in code
  review. (Real pools defend with a proxy that is invalidated on return.)
- **Don't pool cheap objects.** This is the most common misuse. Java's allocator is a pointer bump and
  the young-generation GC is extremely good at short-lived garbage. Pooling `String`s, DTOs, or small
  value objects makes your program **slower** and far more complex, and it can defeat generational GC
  by turning young objects into old ones. Pool something only when it is *expensive to create*, or
  *genuinely scarce*, or both. Connections, threads, large buffers, sockets — yes. Almost nothing else.
- **Sizing is a real decision, not a default.** Too small and you add latency to every request that
  queues; too large and you exhaust the resource you were protecting. A pool of 100 connections in
  front of a database that maxes at 50 is worse than no pool at all. (The counter-intuitive rule from
  the HikariCP docs: for connection pools, **smaller is usually faster.**)
- **A pool is a global bottleneck by design.** Everything funnels through it, so its own locking has to
  be good — and a pool that hands out objects while holding a lock during a 120ms constructor will
  serialise your entire application.
- **Don't write your own for production.** The pattern is worth understanding; the implementation is
  worth *not* writing. HikariCP and the JDK's `ThreadPoolExecutor` have absorbed years of edge cases
  around timeouts, validation, eviction, and leak detection. The version in this project is
  deliberately simple so the shape stays visible.

---

## Where You've Already Used It

- **JDBC connection pools** — HikariCP, Tomcat JDBC, c3p0. The canonical example, and the one this
  project models. `DataSource.getConnection()` is `acquire()`; `connection.close()` is `release()` in
  disguise — it returns the connection to the pool rather than closing the socket. (Which, by the way,
  is a lovely bit of design: the pool hands you a **proxy** so that the familiar `close()` call does
  the right thing.)
- **Thread pools** — `ExecutorService`. Threads are expensive to create (a stack allocation and a
  syscall) and scarce, so you reuse them. Same pattern, same reasoning.
- **`java.lang.Integer.valueOf()`** — the JDK pools boxed integers in `[-128, 127]`. This is why
  `Integer.valueOf(127) == Integer.valueOf(127)` is `true` and `128` isn't. A pool hiding in plain
  sight.
- **String interning** — `String.intern()` is a pool of `String` objects.
- **Netty's `ByteBuf` pooling / NIO direct buffers** — direct buffers are expensive to allocate and
  live outside the heap, so they're pooled aggressively.
- **Game development** — bullets, particles, enemies. The reason is different from a connection pool:
  it isn't that construction is slow, it's that *allocation causes GC pauses*, and a dropped frame is
  a visible defect.

---

## Object Pool vs. Its Neighbours

| Pattern | The actual difference |
|---|---|
| **Flyweight** | **The one to really get straight, because both are "share objects to save resources".** Flyweight shares **immutable** objects **simultaneously** — a thousand trees point at one `TreeType` *at the same time*, and nobody gives it back. Object Pool lends **mutable** objects **exclusively** — you have the connection, nobody else does, and you must return it. Immutable + shared + forever vs. mutable + exclusive + borrowed. See `FlyweightDesignPattern/`. |
| **Singleton** | A Singleton is a pool of exactly one, with no borrowing protocol — everyone shares it, forever. If you find yourself synchronising access to a Singleton so callers take turns, you probably wanted a pool. See `SingletonDesignPattern/`. |
| **Factory** | A factory answers *which* object to make and *how*; a pool answers *whether to make one at all*. A pool usually contains a factory (something has to construct the object when the shelf is empty). See `FactoryAndAbstractFactoryDesignPattern/`. |
| **Prototype** | Both avoid an expensive construction. Prototype does it by **copying** an existing object — you get a new one, and you keep it. Pool does it by **lending** an existing object — you get an old one, and you give it back. See `PrototypeDesignPattern/`. |
| **Proxy** | Not a rival — a **collaborator**. Real pools hand you a proxy rather than the object itself, so that `close()` means "return me" and so a reference kept after release can be invalidated. See `ProxyDesignPattern/`. |
