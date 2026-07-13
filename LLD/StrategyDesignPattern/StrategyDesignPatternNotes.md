# Strategy Design Pattern

> **Define a family of algorithms, encapsulate each one, and make them interchangeable.
> Strategy lets the algorithm vary independently from the clients that use it.**

In one line: **when a class has several possible ways of doing one job, don't put them *in* the class — put each behind an interface and hand the class the one it should use.**

**Type:** Behavioural pattern.

---

## The Problem — Inheritance Can't Share Sideways

You have a `Vehicle` with a `drive()` method. Most vehicles drive normally, but sports cars drive
differently — so you override:

```java
public class Vehicle {
    public void drive() { System.out.println("Normal Drive Capability"); }
}

public class SportsVehicle extends Vehicle {
    @Override
    public void drive() { System.out.println("Sports Drive Capability"); }
}
```

Fine so far. Now the requirement lands: **off-road vehicles also need sports drive.**

```java
public class OffroadVehicle extends Vehicle {
    @Override
    public void drive() { System.out.println("Sports Drive Capability"); }   // ⚠ copy-pasted
}
```

And there it is. `OffroadVehicle` and `SportsVehicle` now contain **byte-for-byte identical** code,
and there is no way to share it:

- Move `drive()` **up** into `Vehicle`? Then `PassengerVehicle` and `GoodsVehicle` inherit sports
  driving, which is wrong.
- Make `OffroadVehicle extends SportsVehicle`? It isn't one. You'd be lying to the type system to
  reuse a method — and dragging along every other sports-car behaviour with it.
- Copy-paste? That's where we are. Fix a bug in sports driving and you must remember to fix it twice.

**This is the pattern's whole motivation.** Inheritance shares behaviour *downward*, from parent to
child. It has no mechanism for sharing behaviour *sideways*, between siblings that happen to need
the same thing. Two classes needing the same behaviour, without one being a kind of the other, is a
problem inheritance simply cannot express.

---

## The Fix — Pull the Behaviour Out Into an Object

### 1. The Strategy interface — the thing that varies

```java
public interface DriveStrategy {          // STRATEGY
    void drive();
}
```

Tiny on purpose. A Strategy interface is usually **one method**: here is a job, here are the
interchangeable ways of doing it.

### 2. The concrete strategies — one class per way of doing it

```java
public class NormalDriveStrategy implements DriveStrategy {   // CONCRETE STRATEGY
    @Override
    public void drive() { System.out.println("Normal Drive Capability"); }
}

public class SportsDriveStrategy implements DriveStrategy {   // CONCRETE STRATEGY
    @Override
    public void drive() { System.out.println("Sports Drive Capability"); }
}
```

Sports driving is now written **once**, in one place. Both `SportsVehicle` and `OffroadVehicle` point
at that one object. The duplication is gone — not hidden, *gone*.

### 3. The Context — owns a strategy, delegates to it

```java
public class Vehicle {                    // CONTEXT
    private DriveStrategy driveStrategy;

    public Vehicle(DriveStrategy driveStrategy) {
        this.driveStrategy = Objects.requireNonNull(driveStrategy);
    }

    public void setDriveStrategy(DriveStrategy driveStrategy) {   // ◀── swap at RUNTIME
        this.driveStrategy = Objects.requireNonNull(driveStrategy);
    }

    public void drive() {
        driveStrategy.drive();            // "I don't know how. Ask the strategy."
    }
}
```

`Vehicle` no longer knows *how* driving works. It knows only that **something** implementing
`DriveStrategy` will do it. That ignorance is the decoupling.

### 4. The subclasses just pre-select a strategy

```java
public class OffroadVehicle extends Vehicle {
    public OffroadVehicle() { super(new SportsDriveStrategy()); }   // shares — no copy-paste
}
```

---

## The Part Most Implementations Get Wrong

**`setDriveStrategy()` is not optional.** If a strategy can only be supplied in the constructor and
never changed, you have *dependency injection* — which is useful, but it is not what Strategy is
*for*. The pattern's headline feature is that **behaviour changes at runtime, on a live object**:

```java
sportsVehicle.drive();                                    // Sports Drive Capability
sportsVehicle.setDriveStrategy(new EcoDriveStrategy());   // driver presses "Eco"
sportsVehicle.drive();                                    // Eco Drive Capability
```

Same object, same reference — different behaviour. That's the car switching into Eco mode, the
checkout switching payment provider, the list re-sorting with a different `Comparator`. Without the
setter, that whole category of use disappears.

---

## The Three Roles

| GoF role | This project | Job |
|---|---|---|
| **Strategy** | `DriveStrategy` | The interface the context talks to |
| **Concrete Strategy** | `NormalDriveStrategy`, `SportsDriveStrategy`, `EcoDriveStrategy` | One interchangeable implementation each |
| **Context** | `Vehicle` | Holds a strategy; delegates to it; never knows which one it has |

Note the client (`Main`) chooses the strategy. The context deliberately doesn't.

---

## Verify (this project)

`WithoutStrategyPattern` — four vehicles, sports-drive code written **twice**:

```
Sports Drive Capability
Normal Drive Capability
Sports Drive Capability
Normal Drive Capability
```

`WithStrategyPattern` — same four vehicles, **identical output**, sports-drive code written **once**:

```
--- the four vehicles ---
Sports Drive Capability
Normal Drive Capability
Sports Drive Capability
Normal Drive Capability

--- the sports car switches mode, mid-drive ---
Sports Drive Capability
Eco Drive Capability
Sports Drive Capability

--- no subclass needed ---
Normal Drive Capability
```

The first block proves behaviour was preserved. The second proves what the refactor *bought* — and
`EcoDriveStrategy` was added without touching `Vehicle` or any existing strategy. That's the
Open/Closed Principle actually paying out.

---

## An Honest Note on the Vehicle Subclasses

Once `drive()` moved into the strategies, `SportsVehicle`, `OffroadVehicle`, `PassengerVehicle` and
`GoodsVehicle` hold **no state and no behaviour of their own** — each is a constructor that picks a
strategy. You could delete all four and write:

```java
Vehicle sports = new Vehicle(new SportsDriveStrategy());
```

…which `Main` demonstrates. They are kept here because they make the before/after comparison legible,
and because they read like a real domain. But recognise them for what they are: **a convenience, not
the pattern.** In textbook Strategy the context is a *single* class, and only the strategies form a
hierarchy. If those subclasses ever grow real behaviour of their own — and *both* hierarchies keep
growing — you no longer have a Strategy. You have a **Bridge** (see `BridgeDesignPattern/`).

---

## Where You've Already Used It

- **`Collections.sort(list, comparator)`** — `Comparator` *is* a strategy. The sort algorithm is
  fixed; the comparison rule is pluggable. The single clearest example of Strategy in the JDK.
- **`Executor` / `ThreadPoolExecutor`'s `RejectedExecutionHandler`** — what to do when the queue is
  full is a strategy (`AbortPolicy`, `CallerRunsPolicy`, `DiscardPolicy`…).
- **Spring's `PasswordEncoder`**, **`AuthenticationProvider`** — swap BCrypt for Argon2 without
  touching a line of the code that calls them.
- **Payment / shipping / compression / pricing rules** — anywhere you were tempted to write
  `if (type == CARD) … else if (type == UPI) …`, that chain is a Strategy interface trying to be born.

---

## Trade-offs & Cautions

- **It multiplies classes.** Every algorithm becomes a class. For two one-line behaviours that will
  never change, an `if` is honestly fine. Strategy earns its keep when algorithms are numerous,
  substantial, or added over time.
- **The client must know the strategies exist** in order to pick one. Strategy moves the decision
  out of the context and into the caller — usually what you want, but it *is* a shifted burden. A
  factory or enum can hide it again.
- **In modern Java, a single-method strategy is often just a lambda.**
  `vehicle.setDriveStrategy(() -> System.out.println("Sports Drive Capability"))` — `DriveStrategy`
  is a functional interface. The pattern didn't disappear; the language absorbed the boilerplate.
- **Strategies should be stateless** where possible — then one instance can be shared by every
  context, and thread safety is free.

---

## Strategy vs. Its Neighbours

| Pattern | The actual difference |
|---|---|
| **Bridge** | **Same UML — different scale.** Strategy varies *one algorithm* inside *one* context class. Bridge connects *two whole hierarchies*, both free to grow, with a rich platform-like interface (not one method). Structurally, Strategy is a Bridge whose abstraction side never grew. |
| **State** | Also delegates to an interface — but the State object **swaps itself out** as the object's lifecycle advances, and states know about each other. A strategy is chosen *by the client* and doesn't decide what comes next. |
| **Template Method** | Solves the same problem (varying part of an algorithm) with **inheritance and an abstract method** instead of composition. Template Method is compile-time and rigid; Strategy is runtime and swappable. Prefer Strategy. |
| **Decorator** | Decorator *adds* behaviour around the existing one. Strategy *replaces* it outright. |
| **Command** | A Command encapsulates a request *to be executed later or undone*. A Strategy encapsulates *how* to do something you're doing right now. |
