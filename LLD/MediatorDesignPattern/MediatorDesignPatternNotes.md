# Mediator Design Pattern

> **Define an object that encapsulates how a set of objects interact. Mediator promotes loose
> coupling by keeping objects from referring to each other explicitly, and it lets you vary their
> interaction independently.**

In one line: **when every object talks to every other object, stop — and give them one thing to talk
to instead.**

**Type:** Behavioural pattern.

---

## The Problem — the Mesh

A chat room with four users. Without a mediator, each `User` holds a reference to every other `User`:

```java
public class User {
    private final List<User> peers = new ArrayList<>();   // ⚠ n-1 references, in EVERY user
    private final List<String> muted = new ArrayList<>(); // ⚠ policy, in EVERY user

    public void send(String message) {
        for (User peer : peers) {
            if (!peer.muted.contains(name)) {             // ⚠ the SENDER enforces the
                peer.receive(name, message);              //   RECEIVER's mute list
            }
        }
    }
}
```

And the wiring in `Main`:

```java
alice.addPeer(bob);   alice.addPeer(carol);  alice.addPeer(dave);
bob.addPeer(alice);   bob.addPeer(carol);    bob.addPeer(dave);
carol.addPeer(alice); carol.addPeer(bob);    carol.addPeer(dave);
dave.addPeer(alice);  dave.addPeer(bob);     dave.addPeer(carol);
```

**Twelve lines to connect four objects.** The connection count is `n × (n-1)` — it grows
quadratically. Five users needs 20. Ten needs 90.

But the line count is the *shallow* problem. Three deeper ones:

1. **Every colleague is coupled to every other.** You cannot reuse `User` anywhere else, test one in
   isolation, or understand one without understanding all of them.
2. **The interaction rules are smeared across the participants.** Look where the mute check lives:
   inside `send()`, on the **sender**, reaching into the **receiver's** private list. The rule "Dave
   doesn't want to hear Bob" is Dave's business, but it is enforced by Bob. That is exactly the kind
   of logic that ends up in the wrong object when nobody owns the interaction.
3. **The real structure of the system is invisible.** The application's topology exists only as those
   twelve `addPeer` calls in `Main`. It is not a *thing* you can name, read, or change.

Adding a fifth user means eight more calls and touching all four existing users. Removing one means
hunting it out of everybody's peer list — miss one and you have a leak and a ghost recipient.

---

## The Fix — the Star

### 1. The Mediator interface — what colleagues are allowed to say

```java
public interface ChatMediator {                                  // MEDIATOR
    void addUser(User user);
    void removeUser(User user);
    void sendMessage(String message, User sender);
    void sendPrivate(String message, User sender, String recipientName);
}
```

### 2. The Colleague — knows the mediator, and nothing else

```java
public abstract class User {                                     // COLLEAGUE
    protected final ChatMediator mediator;
    protected final String name;
    // note what is ABSENT: no peer list. No mute list. No idea anyone else exists.
}

public class ChatUser extends User {                             // CONCRETE COLLEAGUE
    @Override
    public void send(String message) {
        mediator.sendMessage(message, this);   // "here is an event" — not "you, and you, and you"
    }
}
```

**This is the shift.** A colleague no longer *routes*; it *announces*. It reports what happened to
the one object it knows, and that object decides what should follow.

### 3. The Concrete Mediator — the one place that knows how they interact

```java
public class ChatRoom implements ChatMediator {                  // CONCRETE MEDIATOR
    private final List<User> users = new ArrayList<>();
    private final Map<String, Set<String>> mutedBy = new HashMap<>();   // policy, stated ONCE

    @Override
    public void sendMessage(String message, User sender) {
        for (User user : users) {
            if (user == sender)          continue;   // don't echo
            if (isMuted(user, sender))   continue;   // THE one place the mute rule is applied
            user.receive(sender.getName(), message);
        }
    }
}
```

Every rule of the conversation now lives in `ChatRoom`: who gets a broadcast, who is muted, how a
whisper routes, what happens on join and leave. **Want to change how users interact? There is exactly
one file to open.**

---

## What This Buys You

| | Without Mediator | With Mediator |
|---|---|---|
| Wiring 4 users | **12 `addPeer` calls** | **4 `addUser` calls** |
| Connections | `n × (n-1)` — quadratic | `n` — linear |
| Adding a 5th user | 8 more calls, touch all 4 existing | **1 line, nobody else changes** |
| Removing a user | find it in everyone's peer list | 1 line |
| Where the mute rule lives | in every `User.send()` | in `ChatRoom`, once |
| Adding private messages | a new method on every colleague | a new method on the mediator |
| Can you reuse `User` elsewhere? | No — it's welded to its peers | Yes — it only needs a `ChatMediator` |

The `Main` in `WithMediatorDesignPattern` demonstrates the payoff directly: **Erin joins in one
line**, and no existing user is touched. Then `whisper()` shows a genuinely new interaction rule
being added — and it lands in the mediator, exactly where such a rule belongs.

---

## The Four Roles

| GoF role | This project | Job |
|---|---|---|
| **Mediator** | `ChatMediator` | The interface colleagues report events to |
| **Concrete Mediator** | `ChatRoom` | Knows all colleagues; owns **all** the interaction logic |
| **Colleague** | `User` | Knows its mediator; knows nothing about other colleagues |
| **Concrete Colleague** | `ChatUser` | Announces events; reacts to what it's told |

---

## Verify (this project)

Both projects broadcast identically, and both honour the mute:

```
--- Alice says hello ---
Alice sends: Hello everyone
    Bob received from Alice: Hello everyone
    Carol received from Alice: Hello everyone
    Dave received from Alice: Hello everyone

--- Dave mutes Bob, then Bob speaks ---
Bob sends: Anyone there?
    Alice received from Bob: Anyone there?
    Carol received from Bob: Anyone there?     ← Dave correctly does not receive it
```

Same behaviour. The difference is that in one version the rule is enforced by the sender reaching
into the receiver's private state, and in the other it's a single `if` in the object whose actual job
it is.

---

## The Honest Warning — the God Object

**Mediator's failure mode is severe, and you should know it before you reach for the pattern.**

You have not deleted the complexity. You have **moved** it — out of the colleagues and into the
mediator. That is usually a great trade: complexity in one named, testable place beats the same
complexity smeared across ten classes. But it means the mediator **grows**, and if you let it grow
unchecked it becomes a **God Object**: a 2,000-line `ChatRoom` that knows every rule about every
participant, that everything depends on, and that nobody dares touch.

This is not a hypothetical. It is the single most common way Mediator goes wrong in real codebases.

Keep it honest:

- **The mediator should coordinate, not compute.** It routes and applies interaction rules. It should
  not also be doing persistence, validation, and formatting.
- **If the mediator starts branching on colleague *type*** (`if (user instanceof AdminUser)`), that
  logic probably belongs back in the colleague.
- **One mediator per cluster of interaction**, not one per application. If your `ChatRoom` also
  handles billing, you have built a monolith with a design-pattern name on it.
- **Colleagues should stay useful on their own.** If `ChatUser` is a hollow shell that only forwards
  to the mediator, you have drained the domain objects and inverted the problem.

---

## Where You've Already Used It

- **GoF's original example** was a GUI dialog: when the "country" dropdown changes, the "state" list
  must repopulate and the "submit" button must disable. Without a mediator, every widget knows every
  other widget. The `FontDialog` *is* the mediator.
- **Air traffic control.** The canonical real-world analogy: planes do **not** negotiate landing order
  with each other. They all talk to the tower. That is Mediator, and the reason it exists is exactly
  the reason the pattern exists — `n × (n-1)` conversations between planes would be a catastrophe.
- **`java.util.Timer`** — coordinates scheduled tasks that know nothing of each other.
- **Message brokers / event buses** (Kafka, RabbitMQ, Spring's `ApplicationEventPublisher`) — the
  same idea at infrastructure scale: producers and consumers never reference each other.
- **MVC controllers**, **Redux stores**, and most "coordinator" or "orchestrator" classes.

---

## Mediator vs. Its Neighbours

| Pattern | The actual difference |
|---|---|
| **Observer** | The closest relative, and the easy confusion. **Observer is one-to-many broadcast: the subject doesn't care who's listening, and listeners don't talk back.** Mediator is many-to-many coordination: the mediator knows all colleagues by identity and applies *rules* about who should hear what. Observer has no policy; Mediator is nothing but policy. They're often combined — the mediator *uses* Observer to notify colleagues. |
| **Facade** | Both put an object in front of a subsystem. But a **Facade is one-directional and additive**: the subsystem doesn't know the facade exists, and you can still bypass it. A **Mediator is bidirectional and mandatory**: colleagues know the mediator, talk *to* it, and are *called back* by it. Facade simplifies access; Mediator owns interaction. |
| **Command** | A Command encapsulates a *request*. A Mediator encapsulates the *relationships* between senders and receivers. Mediators often dispatch Commands. |
| **Observer vs Mediator, the one-line test** | If the sender genuinely doesn't care who receives it → **Observer**. If something must decide *who should receive it, and whether* → **Mediator**. |
