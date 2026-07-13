# Bridge Design Pattern

> **Decouple an abstraction from its implementation so that the two can vary independently.**

That sentence is famously the least helpful in the whole GoF book. Here is what it actually means:

**When a thing varies along two independent dimensions, don't try to express both with inheritance. Express one with inheritance and the other with composition.**

**Type:** Structural pattern.

---

## The Problem — Class Explosion

You are building a universal remote. Two dimensions vary:

- **The remote** — basic, advanced, voice-controlled…
- **The device** — TV, radio, speaker…

The instinct is to make one inheritance tree. Watch what happens:

```
                  Remote
        ┌───────────┴───────────┐
   BasicTVRemote          BasicRadioRemote
   AdvancedTVRemote       AdvancedRadioRemote
```

That's 2 × 2 = **4 classes**. And they're not 4 *different* classes — they're 4 *combinations*. Look at `WithoutBridgeDesignPattern`:

```java
public class AdvancedTVRemote extends Remote {
    // ⚠ every line here is copy-pasted from BasicTVRemote
    public void turnOn()  { System.out.println("TV : powering on the screen..."); }
    public void turnOff() { System.out.println("TV : powering off the screen..."); }
    public void volumeUp(){ ... }

    public void mute()    { ... }   // ...the only genuinely new behaviour
}
```

Now the bill comes due:

| You add… | Classes you must write | Total |
|---|---|---|
| Speaker (3rd device) | `BasicSpeakerRemote`, `AdvancedSpeakerRemote` | 6 |
| VoiceRemote (3rd remote) | `VoiceTVRemote`, `VoiceRadioRemote`, `VoiceSpeakerRemote` | 9 |

**The hierarchy grows as `remotes × devices`.** Worse, a bug in the TV's power logic has to be fixed in *every* `*TVRemote` class, and adding one remote means touching every device. This is a **Cartesian product of classes**, and it is the smell Bridge exists to kill.

The root cause: **inheritance can only model one dimension of variation.** We had two, and forced them into one tree.

---

## The Fix — Two Hierarchies, One Reference

Stop trying to inherit both. Split them apart and connect them with a **field**.

### 1. The Implementor — what a device can *do*

```java
public interface Device {                 // IMPLEMENTOR
    void enable();
    void disable();
    boolean isEnabled();
    int getVolume();
    void setVolume(int volume);
    int getChannel();
    void setChannel(int channel);
}
```

Deliberately **primitive** operations. `Device` knows nothing about remotes, buttons, or users. `TV`, `Radio` and `Speaker` implement it.

### 2. The Abstraction — what a user can *do*

```java
public class RemoteControl {              // ABSTRACTION
    protected Device device;              // ◀── THE BRIDGE

    public RemoteControl(Device device) {
        this.device = device;
    }

    public void togglePower() {
        if (device.isEnabled()) device.disable();
        else                    device.enable();
    }

    public void volumeUp() {
        device.setVolume(device.getVolume() + 10);   // high-level op, built from primitives
    }
}
```

**That `Device device` field is the bridge.** It is the *only* place the two hierarchies touch. One line of code is the entire pattern — everything else is consequence.

Notice `togglePower()`: the *device* has no idea what "toggle" means. It only knows `enable`/`disable`/`isEnabled`. The abstraction composes primitives into user-level operations. That division of labour is the point.

### 3. The Refined Abstraction — vary the user side

```java
public class AdvancedRemoteControl extends RemoteControl {   // REFINED ABSTRACTION
    public AdvancedRemoteControl(Device device) { super(device); }

    public void mute() { device.setVolume(0); }
}
```

Written **once**. It works with `TV`, `Radio`, `Speaker`, and every device anyone adds in the future — because it only ever talks to the `Device` interface.

---

## What This Buys You

```java
new RemoteControl(new TV());              // basic remote, TV
new RemoteControl(new Radio());           // SAME class, different device
new AdvancedRemoteControl(new Radio());   // different remote, same device
new AdvancedRemoteControl(new Speaker()); // Speaker was added LAST — neither remote changed
```

| | Without Bridge | With Bridge |
|---|---|---|
| 2 remotes × 2 devices | **4 classes** | **4 classes** (2 + 2) |
| Add a Speaker | **+2 classes** | **+1 class** |
| Add a VoiceRemote | **+3 classes** | **+1 class** |
| Growth | `remotes × devices` | `remotes + devices` |

**Multiplication becomes addition.** At 2×2 the two designs look like a tie — which is exactly why the pattern is easy to dismiss. The divergence starts at the *third* item in either dimension, and it never stops.

And the combination is now chosen **at runtime** (`new AdvancedRemoteControl(device)`), not frozen at compile time by a class name. In the "Without" version, "advanced + radio" *is a class*. In the "With" version, it's an argument.

---

## The Four Roles

| GoF role | This project | Job |
|---|---|---|
| **Abstraction** | `RemoteControl` | The high-level interface a client uses; holds a reference to an Implementor |
| **Refined Abstraction** | `AdvancedRemoteControl` | Extends the abstraction — varies the *user-facing* dimension |
| **Implementor** | `Device` | The primitive operations the abstraction is built from |
| **Concrete Implementor** | `TV`, `Radio`, `Speaker` | Varies the *platform* dimension |

Read the names carefully, because they mislead:

- "Abstraction" does **not** mean `abstract class`. `RemoteControl` here is a concrete, instantiable class. It means "the higher-level, user-facing concept."
- "Implementor" does **not** mean "the implementation of the abstraction." It's a **separate, parallel hierarchy** — not a subclass. `TV` does not extend `RemoteControl`; it has nothing to do with it.

---

## Verify (this project)

Both projects print the *same* thing for the advanced-remote-on-radio case:

```
Radio : powering on the tuner
Radio : volume is now 40 (amplifier)
Radio : volume is now 0 (amplifier)
Radio : powering off the tuner
```

Identical behaviour. The difference is entirely structural — and it shows up the moment you add the fourth thing.

---

## How Do I Know I Need It?

Ask: **does my class name contain an "and"?**

`BasicTVRemote` is "basic **and** TV". `XmlHttpLogger` is "XML **and** HTTP". `MySqlUserRepository` is "MySQL **and** User". Every time two independent concepts get welded into one class name, you are one requirement away from a Cartesian product.

The other tell: you find yourself writing a class **purely as a combination** of two others, with no new behaviour of its own. `BasicRadioRemote` isn't a *kind* of thing; it's a *pairing* of things.

Real examples:

- **JDBC.** `Connection`/`Statement` (abstraction) ← bridge → the vendor `Driver` (implementor). Your query code doesn't change when you swap Postgres for MySQL. This is Bridge at industrial scale.
- **SLF4J.** The logging API you call (abstraction) is bridged to Logback / log4j / java.util.logging (implementor).
- **AWT / Swing.** `java.awt.Button` (abstraction) delegates to a native peer (implementor) per OS. GoF's own motivating example was exactly this — a `Window` bridged to X11 vs. PM.
- **Any "device driver" / "backend" / "provider" split** you have ever seen. That's Bridge.

---

## Trade-offs & Cautions

- **It costs a layer of indirection.** With only one dimension of variation, Bridge is pure overhead — plain inheritance is correct. Don't reach for it on `Shape → Circle`.
- **You must design the Implementor interface up front**, and getting it right is the hard part. Too primitive and every abstraction has to reimplement the same helpers; too high-level and it stops being reusable across platforms.
- **It is best introduced early.** Retrofitting Bridge onto an exploded hierarchy is a real refactor. But the *symptom* (the ×-shaped class list) is loud, so you usually get fair warning.
- **YAGNI still applies.** Two dimensions must genuinely vary *independently*. If every remote only ever works with one device, there is no second dimension and Bridge buys nothing.

---

## Bridge vs. Its Neighbours

This is where people get lost. The distinctions are worth memorising.

| Pattern | Same structure? | The actual difference |
|---|---|---|
| **Adapter** | Yes — nearly identical UML | **Intent and timing.** Adapter is applied *after the fact* to make two incompatible existing interfaces work together. Bridge is designed *up front* so two hierarchies can grow independently. Adapter reacts; Bridge plans. |
| **Strategy** | Yes — object holds an interface, delegates to it | **Scale and purpose.** Strategy swaps *one algorithm* inside a class. Bridge connects *two whole hierarchies*, each free to grow. A Bridge's implementor tends to be a rich interface (a whole platform); a Strategy is usually one method. Structurally, Strategy is a Bridge that gave up its ambitions. |
| **State** | Yes | State's delegate *changes at runtime as the object's state changes*. A Bridge's implementor is normally set once, at construction. |
| **Abstract Factory** | No | Often used *with* Bridge — the factory creates the right Concrete Implementor and hands it to the Abstraction, so the client never sees which one it got. |
| **Decorator** | No | Decorator wraps an object with the **same** interface to add behaviour. Bridge connects **two different** interfaces. |

The honest summary: **Adapter, Bridge, Strategy and State all look the same in UML.** Nearly every structural/behavioural pattern is "hold a reference to an interface and delegate." What separates them is *why you did it* and *when you decided to*. That's not a cop-out — intent is what a pattern actually is. The UML is just the residue.
