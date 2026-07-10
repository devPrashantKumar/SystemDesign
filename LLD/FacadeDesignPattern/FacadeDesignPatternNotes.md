# Facade Design Pattern

> **Provide a single, simplified interface to a complex subsystem of many classes.**

The client wants to do *one thing* ("place an order"), but that one thing actually requires coordinating several independent services in the right order, with the right rules. The Facade wraps all of them behind one clean method, so the client never sees the moving parts.

Real-world analogy: a **restaurant waiter**. You say "I'll have the pasta." You don't talk to the chef, the stockroom, the billing system, and the delivery boy yourself — the waiter (facade) coordinates them all. You have one simple point of contact.

**Type:** Structural pattern (it's about how classes are organized/composed).

---

## When to Use

- A subsystem has many classes and the client only needs a small, common slice of its functionality.
- You want to **decouple** the client from the subsystem's internal classes, so they can change without breaking callers.
- There's a **workflow** (a fixed sequence of steps + rules) that shouldn't be re-implemented by every caller.
- You want a simple default entry point, while still allowing advanced callers to reach the subsystem directly if needed.

---

## The 3 Roles (mapped to this example)

| Role | Class | Responsibility |
|---|---|---|
| **Facade** | `OrderFacade` | One simple method; owns the workflow and hides/orchestrates the subsystem |
| **Subsystem classes** | `InventoryService`, `PaymentService`, `ShippingService` | Do the real work; independent; unaware of the facade and of each other |
| **Client** | `Client` | Uses only the facade; knows nothing about the subsystem |

---

## The Problem — without a Facade

The client would have to own the subsystem *and* the workflow itself:

```java
InventoryService inventory = new InventoryService();
PaymentService payment = new PaymentService();
ShippingService shipping = new ShippingService();

if (inventory.checkStock("item")) {
    payment.charge("card", 10);
    shipping.scheduleDelivery("item");
}
```

Problems:
1. The client is **coupled to three classes** instead of one.
2. The client must **know the correct order and rules** (check stock first; don't ship if payment failed). Every caller re-implements this and can get it wrong.
3. If the workflow changes (add notifications, add fraud checks), **every caller** must change.

---

## The Solution — the Facade

```java
public class OrderFacade {
    private final InventoryService inventory = new InventoryService();
    private final PaymentService payment = new PaymentService();
    private final ShippingService shipping = new ShippingService();

    // The facade owns the WORKFLOW: correct order + failure rules.
    // Each step is guarded, so it short-circuits on any failure.
    public boolean placeOrder(String item, String card, double amount) {
        if (!inventory.checkStock(item)) {
            System.out.println("Order failed: " + item + " is out of stock");
            return false;
        }
        if (!payment.charge(card, amount)) {
            System.out.println("Order failed: payment declined");
            return false;
        }
        shipping.scheduleDelivery(item);
        System.out.println("Order placed successfully for " + item);
        return true;
    }
}
```

The facade does two jobs:
1. **Hides** the three subsystem classes behind one method.
2. **Orchestrates** them — enforces the sequence *and* the failure handling (never charge for out-of-stock, never ship if payment fails). This guarding is the whole reason the facade exists.

---

## The Client + Wiring

```java
// Client — depends ONLY on the facade
public class Client {
    OrderFacade orderFacade;
    Client(OrderFacade orderFacade) { this.orderFacade = orderFacade; }
    void placeOrder() {
        boolean success = orderFacade.placeOrder("item", "card", 10);
        System.out.println("Client sees only the result: " + success);
    }
}

// Main — composition root
Client client = new Client(new OrderFacade());
client.placeOrder();
```

**Output:**
```
Checking stock for item
Charging 10.0 to card
Scheduling delivery for item
Order placed successfully for item
Client sees only the result: true
```

---

## Call Flow

```
                 ┌────────────────────────────────────────────┐
                 │                OrderFacade                  │
Client ─placeOrder()▶  1. inventory.checkStock(item)           │
                 │     2. payment.charge(card, amount)         │
                 │     3. shipping.scheduleDelivery(item)      │
                 │        (each step guarded; stops on failure)│
                 └────────────────────────────────────────────┘
                        │            │              │
                        ▼            ▼              ▼
                 InventoryService  PaymentService  ShippingService
```

The client makes **one** call; the facade fans out to three services in the correct order.

---

## Key Design Points

1. **The facade owns the workflow, not just the references.** Holding the three services is not enough — the value is that the *order of steps and the failure rules* live in one place.

2. **Subsystem classes stay dumb and independent.** `PaymentService` doesn't know inventory or shipping exist. This keeps them reusable and testable on their own.

3. **The facade adds no new behavior — it simplifies access.** That's what separates it from a Decorator (which adds behavior). The subsystem could still be used directly; the facade is a convenience layer, not a mandatory gate.

4. **`new`-ing the services inside the facade** is the classic textbook form and is fine. A more testable variant injects them via the constructor (so you can pass mocks) — a refinement, not a requirement.

---

## Facade vs. the Other Wrappers

All are structural, but the intent differs:

| Pattern | Wraps... | Interface | Intent | Repo example |
|---|---|---|---|---|
| **Facade** | *many* objects | **New, simpler** one | *Simplify* — hide a whole subsystem | This project |
| **Adapter** | *one* object | **Changed** | *Translate* to an expected interface | `AdapterDesignPattern` |
| **Decorator** | *one* object | **Same** | *Add behavior* | `DecoratorDesignPattern` |
| **Proxy** | *one* object | **Same** | *Control access* | `ProxyDesignPattern` |

Distinguishing traits of Facade: it wraps **multiple** classes, and it invents a **brand-new simplified interface** (it doesn't have to match anything). Adapter wraps one object and *must* match a required interface; Facade wraps many and defines its own.

---

## Real-World Examples in Java

- `javax.faces.context.FacesContext` — hides the complex JSF request-processing subsystem.
- SLF4J's `LoggerFactory` — a simple facade over many underlying logging frameworks.
- Spring's `JdbcTemplate` — a facade over the verbose raw JDBC subsystem (connections, statements, result sets, exception handling).
- A typical service-layer class (e.g. `OrderService`) — often acts as a facade over repositories, validators, and gateways.
