# Prototype Design Pattern

> **Create new objects by *cloning* an existing configured object (the prototype), instead of building them from scratch with `new` + a constructor.**

You already have an object set up the way you want. Rather than re-specifying every field to make a similar one, you ask the object itself to produce a copy — then tweak only what differs.

Real-world analogy: a **rubber stamp** / photocopier. Set up the master once; every copy comes out pre-filled, and you only change the few details that differ.

**Type:** Creational pattern (it's about *how objects are created*).

---

## When to Use

- **Object creation is expensive** — the master required a DB query, network call, or heavy computation. Cloning a ready instance skips all that.
- **You need many near-identical objects** — configure one master, stamp out copies, tweak the deltas.
- **The client shouldn't know the concrete class** — it calls `cloneObject()` on a `Prototype` reference and gets a correctly-typed copy, with no `switch`/factory over concrete types.

If none of these apply, a plain constructor or a Builder is simpler — Prototype is specifically for the "copy an existing configured instance" scenario.

---

## The Roles (mapped to this example)

| Role | Class | Responsibility |
|---|---|---|
| **Prototype** (interface) | `Prototype` | Declares `cloneObject()` |
| **Concrete Prototype** | `Employee` | Implements the clone; **deep-copies** its mutable state |
| **Client** | `Main` | Gets new objects by cloning, not by `new` |
| **Registry** (optional) | `PrototypeRegistry` | Stores named masters; hands out clones by key |

---

## The Prototype Interface

```java
public interface Prototype {
    Prototype cloneObject();
}
```

One contract: "I can clone myself." This is the modern, **preferred** approach over Java's built-in `Cloneable` / `Object.clone()` (which is protected, skips the constructor, throws a checked exception, and is shallow by default).

---

## The Concrete Prototype — the deep-copy is everything

```java
public class Employee implements Prototype {
    private String name, department;
    private List<String> skills;

    public Employee(String name, String department, List<String> skills) {
        this.name = name;
        this.department = department;
        this.skills = skills;
    }

    public void addSkills(String skill) { this.skills.add(skill); }

    @Override
    public Employee cloneObject() {                       // covariant return: Employee, not Prototype
        // deep copy the mutable list — build a NEW list, don't share the reference
        return new Employee(this.name, this.department, new ArrayList<>(this.skills));
    }
}
```

The critical line is `new ArrayList<>(this.skills)`:

- `name`, `department` are `String` → **immutable** → safe to share by reference.
- `skills` is a `List` → **mutable** → **must** be copied, or the clone and the original would share one list and corrupt each other.

---

## Shallow vs. Deep Copy (the #1 Prototype bug)

```java
// ❌ SHALLOW — clone shares the SAME list as the original
return new Employee(this.name, this.department, this.skills);

// ✅ DEEP — clone gets its own independent list
return new Employee(this.name, this.department, new ArrayList<>(this.skills));
```

**Proof from `Main`:**

```java
Employee employee  = new Employee("Prashant", "JVM", [JAVA, AWS, PYTHON]);
Employee employee1 = employee.cloneObject();   // clone taken
employee.addSkills("DESIGN PATTERNS");         // mutate ORIGINAL after cloning
System.out.println(employee1);                 // clone unaffected
```

Output with the **deep** copy (correct):
```
Employee{name='Prashant', department='JVM', skills=[JAVA, AWS, PYTHON]}
Employee{name='Prashant', department='JVM', skills=[JAVA, AWS, PYTHON]}   ← no DESIGN PATTERNS
```

With a shallow copy the clone would wrongly show `[JAVA, AWS, PYTHON, DESIGN PATTERNS]`. **Rule: a prototype must deep-copy every mutable field.**

---

## Optional Variant — Prototype Registry

A central store of ready-made masters, keyed by name. Clients request a copy by key and never touch a concrete class or constructor.

```java
public class PrototypeRegistry {
    private final Map<String, Prototype> registry = new HashMap<>();

    public void register(String key, Prototype prototype) {
        registry.put(key, prototype);
    }

    public Prototype get(String key) {
        Prototype prototype = registry.get(key);
        if (prototype == null)
            throw new IllegalArgumentException("No prototype registered for key: " + key);
        return prototype.cloneObject();   // return a CLONE, never the stored master
    }
}
```

**Client (`RegistryMain`) — after setup, no `new Employee(...)` anywhere:**

```java
PrototypeRegistry registry = new PrototypeRegistry();
registry.register("java-dev",      new Employee("TEMPLATE", "JVM",  [JAVA, SPRING]));
registry.register("data-engineer", new Employee("TEMPLATE", "DATA", [PYTHON, SPARK, SQL]));

Employee dev1 = (Employee) registry.get("java-dev");
Employee dev2 = (Employee) registry.get("java-dev");
dev1.addSkills("AWS");
```

**Output:**
```
dev1  : Employee{name='TEMPLATE', department='JVM',  skills=[JAVA, SPRING, AWS]}
dev2  : Employee{name='TEMPLATE', department='JVM',  skills=[JAVA, SPRING]}       ← independent of dev1
de    : Employee{name='TEMPLATE', department='DATA', skills=[PYTHON, SPARK, SQL]}
master: Employee{name='TEMPLATE', department='JVM',  skills=[JAVA, SPRING]}        ← still pristine
```

What the registry adds over the basic example:
1. **Named lookup replaces `new`** — adding a new "type" = one `register(...)` line, no new class or factory `switch` (Open/Closed).
2. **Masters stay protected** — because `get()` returns a clone, `dev1`'s edit touches neither `dev2` nor the stored master. This only works because `Employee.cloneObject()` deep-copies — the registry inherits that correctness.

---

## Call Flow

```
Basic:     Client ──cloneObject()──▶ Employee (master) ──▶ new Employee(deep copy) ──▶ back to Client

Registry:  Client ──get("java-dev")──▶ Registry ──cloneObject()──▶ master ──▶ fresh clone ──▶ Client
```

---

## Analysis of This Implementation

1. **Interface-based clone instead of `Cloneable`** — the recommended modern approach. ✅
2. **Covariant return type** — `cloneObject()` returns `Employee`, so callers need no cast in the basic example. ✅
3. **Deep copy done right** — copies the mutable `List`, shares immutable `String`s. ✅
4. **Registry returns clones, not masters** — keeps stored prototypes pristine. ✅

**Verdict:** correct, standard, and it cleanly demonstrates the pattern's key lesson (deep-copy mutable state).

---

## Prototype vs. the Other Creational Patterns

| Pattern | How you get an object | When |
|---|---|---|
| **Prototype** | *Clone* an existing instance | Copying a configured/expensive object is cheaper than rebuilding |
| **Builder** | Assemble step-by-step | Many params, immutability, required vs optional |
| **Factory** | Ask a factory to `new` by type | Decouple client from concrete classes |

---

## Real-World Examples in Java

- `Object.clone()` / `Cloneable` — Java's native (flawed) prototype mechanism.
- `ArrayList`, `HashMap`, etc. — their copy constructors (`new ArrayList<>(other)`) are prototype-style copies.
- `java.util.Calendar.clone()` — cloning a configured calendar instance.
- Spring bean scope `prototype` — a new instance per request (conceptually related: "don't share one master instance").
