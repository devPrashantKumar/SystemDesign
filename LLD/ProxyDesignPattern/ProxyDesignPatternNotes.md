# Proxy Design Pattern

> **Provide a stand-in (surrogate) for another object to control access to it.**

The proxy implements the *same interface* as the real object, so the client can't tell them apart. But before (or instead of) forwarding the call to the real object, the proxy does something extra — checks permissions, lazily creates the object, caches, logs, etc.

Real-world analogy: a **security guard** at an office door. The guard looks exactly like part of the "enter the building" process, but only lets you through if your badge has the right access level.

**Type:** Structural pattern (like Adapter, Decorator, Facade — it wraps an object).

---

## The Kinds of Proxy

| Kind | What the proxy adds before delegating | In this repo |
|---|---|---|
| **Protection proxy** | Access control / permission checks | `ProtectionProxy/` |
| **Virtual proxy** | Lazy initialization — create the expensive real object only on first use | `VirtualProxy/` |
| **Caching proxy** | Returns cached results instead of re-running the real call | `CachingProxy/` |
| **Remote proxy** | Represents an object in another address space (RPC, RMI) | — |
| **Logging/monitoring proxy** | Records calls, timings, audit trail | — |

The structure is identical across all of them — same interface, wrap the real object,
do something *before* delegating. Only the "something" differs. The three implemented
here are covered below; the primary walkthrough uses the **protection proxy**.

---

## The Roles (mapped to this example)

| Role | Class | Responsibility |
|---|---|---|
| **Subject** (interface) | `EmployeeDao` | Common contract for both real object and proxy |
| **Real Subject** | `EmployeeDaoImpl` | Does the actual work (the real DAO operations) |
| **Proxy** | `EmployeeDaoProxy` | Same interface; checks the role, then delegates or denies |
| **Client** | `Main` | Talks to `EmployeeDao` — unaware whether it's real or proxy |

---

## The Subject Interface

```java
public interface EmployeeDao {
    void create(String client) throws Exception;
    void get(String client) throws Exception;
    void delete(String client) throws Exception;
}
```

Both the real object and the proxy implement this — that's what makes them **interchangeable** to the client.

---

## The Real Subject

```java
public class EmployeeDaoImpl implements EmployeeDao {
    public void create(String client) { System.out.println("Employee created Successfully"); }
    public void get(String client)    { System.out.println("Employee fetched Successfully"); }
    public void delete(String client) { System.out.println("Employee deleted Successfully"); }
}
```

Pure business logic. **No permission code** — it doesn't know a proxy exists. Keeping access-control out of here is the whole point.

---

## The Proxy

```java
public class EmployeeDaoProxy implements EmployeeDao {
    EmployeeDao employeeDao;                              // holds the REAL subject (composition)

    public EmployeeDaoProxy(EmployeeDao employeeDao) {
        this.employeeDao = employeeDao;
    }

    @Override
    public void create(String client) throws Exception {
        if (client.equals("USER")) {                     // ① check permission
            employeeDao.create(client);                  // ② delegate to real object
            return;
        }
        throw new IllegalAccessException("USER ROLE IS REQUIRED");  // ③ or deny
    }

    @Override
    public void get(String client) throws Exception {
        if (client.equals("USER") || client.equals("ADMIN")) {
            employeeDao.get(client);
            return;
        }
        throw new IllegalAccessException("USER or ADMIN ROLE IS REQUIRED");
    }

    @Override
    public void delete(String client) throws Exception {
        if (client.equals("ADMIN")) {
            employeeDao.delete(client);
            return;
        }
        throw new IllegalAccessException("ADMIN ROLE IS REQUIRED");
    }
}
```

The access rules encoded here:

| Operation | Allowed role(s) |
|---|---|
| `create` | `USER` |
| `get` | `USER` or `ADMIN` |
| `delete` | `ADMIN` |

---

## The Client + Wiring

```java
// Client sees only EmployeeDao — the proxy is injected in place of the real object.
EmployeeDao employeeDao = new EmployeeDaoProxy(new EmployeeDaoImpl());

employeeDao.create("USER");    // allowed
employeeDao.create("ADMIN");   // denied
employeeDao.get("ADMIN");      // allowed
employeeDao.delete("ADMIN");   // allowed
```

**Output:**
```
Employee created Successfully
USER ROLE IS REQUIRED
-----------------------------
Employee fetched Successfully
Employee fetched Successfully
-----------------------------
ADMIN ROLE IS REQUIRED
Employee deleted Successfully
```

Notice the client code never mentions roles or permission checks — it just calls the interface. The proxy silently enforces the rules.

---

## Call Flow

```
Client ──create("ADMIN")──▶ EmployeeDaoProxy ──[role check fails]──▶ ✗ throws IllegalAccessException
Client ──create("USER")───▶ EmployeeDaoProxy ──[role check ok]────▶ EmployeeDaoImpl.create()  ✓
```

The proxy is a gate: every call passes through the permission check before it can reach the real object.

---

## Why not just put the checks inside `EmployeeDaoImpl`?

- **Single Responsibility** — the DAO does data operations; the proxy does access control. Two concerns, two classes.
- **Open/Closed** — you can add, remove, or swap the proxy without touching the real class. Run with the proxy in production, without it in trusted internal calls.
- **The real class stays reusable** — anywhere that *doesn't* need the checks can use `EmployeeDaoImpl` directly.

---

## Other Type 1 — Virtual Proxy (`VirtualProxy/`)

**Intent: defer an expensive object's creation until it's actually needed (lazy loading).**

The real object does its heavy work in its constructor, so creating it is costly. The
proxy stays cheap — it stores just enough info (a file name) and builds the real object
on the *first* method call, reusing it afterward.

```java
public class ImageProxy implements Image {
    private final String fileName;
    private RealImage realImage;                 // null until first use

    public ImageProxy(String fileName) { this.fileName = fileName; }  // cheap

    @Override
    public void display() {
        if (realImage == null) {                 // create real object only once,
            realImage = new RealImage(fileName); // on the first actual request
        }
        realImage.display();                     // then delegate
    }
}
```

**Output:**
```
Proxy created — notice NO disk load happened above.

First display() -> triggers the lazy load:
Loading image from disk: photo.png (expensive!)
Displaying: photo.png

Second display() -> reuses the already-loaded image:
Displaying: photo.png
```

The disk load happens on the *first* `display()`, not at construction — and never at all
if `display()` is never called. This is exactly how Hibernate lazy-loads entity relations.

---

## Other Type 2 — Caching Proxy (`CachingProxy/`)

**Intent: remember results so repeated calls skip the expensive work.**

```java
public class CachingDataServiceProxy implements DataService {
    private final DataService realDataService;
    private final Map<String, String> cache = new HashMap<>();

    public CachingDataServiceProxy(DataService realDataService) {
        this.realDataService = realDataService;
    }

    @Override
    public String fetchData(String id) {
        if (cache.containsKey(id)) return cache.get(id);   // HIT — skip the real call
        String data = realDataService.fetchData(id);       // MISS — delegate once
        cache.put(id, data);                                // remember for next time
        return data;
    }
}
```

**Output:**
```
  >> RealDataService: expensive fetch for id=A
fetch A (1st) -> Data(A)
  (cache hit for id=A)
fetch A (2nd) -> Data(A)
  >> RealDataService: expensive fetch for id=B
fetch B (1st) -> Data(B)
  (cache hit for id=A)
fetch A (3rd) -> Data(A)
```

Only the *first* fetch of each id hits `RealDataService`; repeats are served from the
map. This is the idea behind Spring's `@Cacheable`.

---

## What All Three Share

| | Protection | Virtual | Caching |
|---|---|---|---|
| Same interface as real object | ✓ | ✓ | ✓ |
| Holds the real object (composition) | ✓ | ✓ (created lazily) | ✓ |
| Extra step before delegating | role check | lazy create | cache lookup |
| May skip the real call | ✓ (deny) | — | ✓ (cache hit) |

The **structure never changes** — only the logic the proxy runs before (or instead of)
delegating. That's what makes "Proxy" one pattern with many flavors.

---

## Proxy vs. Decorator vs. Adapter vs. Facade

All are structural patterns that wrap an object — **intent** is the difference:

| Pattern | Wraps an object to... | Interface | Repo example |
|---|---|---|---|
| **Proxy** | *control access* (permissions, laziness, caching) | **Same** | This project |
| **Decorator** | *add behavior* | **Same** | `DecoratorDesignPattern` |
| **Adapter** | *translate* to a different interface | **Changes** | `AdapterDesignPattern` |
| **Facade** | *simplify* a whole subsystem | **New, simpler** | `FacadeDesignPattern` |

Proxy and Decorator look almost identical structurally (same interface, wrap one object). The difference is **why**: Proxy decides *whether/when* to forward the call; Decorator always forwards but *adds* to it.

---

## Real-World Examples in Java

- `java.lang.reflect.Proxy` / dynamic proxies — the JDK's built-in proxy mechanism.
- **Spring AOP** — `@Transactional`, `@Secured`, `@Cacheable` are all implemented with proxies that wrap your bean.
- **Hibernate lazy loading** — entity relations are virtual proxies; the real object is fetched on first access.
- **RMI stubs** — remote proxies standing in for objects on another JVM.
