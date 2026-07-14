# Interpreter Design Pattern — UML Diagrams

Interpreter has one structural idea, and every diagram below is a restatement of it:

> **The grammar becomes the class hierarchy. A sentence becomes a tree of objects.**

The thing to watch for is the arrow that a nonterminal points *at itself* — `AndExpression` holds two
`Expression`s, and one of those might be another `AndExpression`. That self-reference is the pattern.
It's also the one thing a flat loop can never have.

---

## 1. The Canonical Structure

```mermaid
classDiagram
    class Expression {
        <<interface>>
        +interpret(Context) boolean
    }

    class TerminalExpression {
        +interpret(Context) boolean
    }

    class NonterminalExpression {
        -Expression left
        -Expression right
        +interpret(Context) boolean
    }

    class Context {
        -Map~String,Double~ values
        +get(metric) double
    }

    class Client

    Expression <|.. TerminalExpression
    Expression <|.. NonterminalExpression
    NonterminalExpression o--> Expression : children ↺
    Client ..> Expression : builds the tree
    Expression ..> Context : reads

    note for NonterminalExpression "THE RECURSION.<br/>Its children are EXPRESSIONS —<br/>a leaf, or another whole subtree.<br/>This one arrow is the pattern."
    note for TerminalExpression "A LEAF. No children.<br/>The recursion bottoms out here."
```

---

## 2. The Problem — `WithoutInterpreterDesignPattern`

```mermaid
classDiagram
    class RuleEngine {
        +evaluate(ruleName, Metrics) boolean
        ..a switch, one case per rule..
        HIGH_CPU
        HIGH_MEMORY
        HIGH_CPU_AND_MEMORY
        DISK_ALMOST_FULL
        default: throw ⚠
    }

    class NaiveRuleParser {
        +evaluate(ruleString, Metrics) boolean
        ..one flat left-to-right loop..
        ⚠ no precedence
        ⚠ no parentheses
        ⚠ no nesting, ever
    }

    class Metrics

    RuleEngine ..> Metrics
    NaiveRuleParser ..> Metrics

    note for RuleEngine "Rules are JAVA, not data.<br/>Every new rule = review + build + DEPLOY.<br/>Ops knows what it wants to be paged on —<br/>it just isn't allowed to SAY it."
    note for NaiveRuleParser "The instinct was right (rules as strings)<br/>but the shape is wrong:<br/>A LANGUAGE IS A TREE.<br/>THIS IS A LOOP.<br/>⚠ missed page + false page, both real"
```

There is no self-referencing arrow anywhere on this diagram. **That absence is the bug.** Neither
class has any way to say "and here, another whole expression goes."

---

## 3. The Fix — `WithInterpreterDesignPattern`

```mermaid
classDiagram
    class Expression {
        <<interface>>
        +interpret(Context) boolean
    }

    class Comparison {
        <<terminal>>
        -String metric
        -Operator operator
        -double threshold
        +interpret(Context) boolean
    }

    class AndExpression {
        <<nonterminal>>
        -Expression left
        -Expression right
        +interpret(Context) boolean
    }

    class OrExpression {
        <<nonterminal>>
        -Expression left
        -Expression right
        +interpret(Context) boolean
    }

    class NotExpression {
        <<nonterminal>>
        -Expression expression
        +interpret(Context) boolean
    }

    class Context {
        -Map~String,Double~ values
        +get(metric) double
    }

    class RuleParser {
        +parse(String) Expression
        ..not part of the pattern..
    }

    Expression <|.. Comparison
    Expression <|.. AndExpression
    Expression <|.. OrExpression
    Expression <|.. NotExpression

    AndExpression o--> Expression : left, right ↺
    OrExpression o--> Expression : left, right ↺
    NotExpression o--> Expression : child ↺

    RuleParser ..> Expression : builds
    Expression ..> Context : reads

    note for Comparison "The only class that touches the Context.<br/>Everything else just combines answers."
    note for RuleParser "GoF are explicit: the parser is NOT<br/>part of Interpreter. Main builds the first<br/>tree BY HAND to prove it."
```

| Grammar rule | Class | Role |
|---|---|---|
| `expression := term ("OR" term)*` | `OrExpression` | NonterminalExpression |
| `term := factor ("AND" factor)*` | `AndExpression` | NonterminalExpression |
| `factor := "NOT" factor \| "(" expression ")"` | `NotExpression` | NonterminalExpression |
| `comparison := metric (">" \| "<") number` | `Comparison` | **TerminalExpression** |
| — | `Context` | Context |
| — | `Main` / `RuleParser` | Client |

**One class per grammar rule.** That table *is* the pattern.

---

## 4. ASCII — Why the Tree Wins

The rule:  `cpu > 50 OR memory > 95 AND disk > 99`
Metrics:   `cpu=60, memory=10, disk=10`

```
   WITHOUT — a flat LOOP                    WITH — a TREE
   ─────────────────────                    ────────────

   fold left to right:                                    ┌──────────┐
                                                          │    OR    │  ← shallow: binds loosely
   [cpu>50] ──OR──► true                                  └────┬─────┘
        │                                              ┌───────┴───────┐
        ▼                                              ▼               ▼
   [memory>95] ──► false                        ┌────────────┐   ┌──────────┐
        │                                       │  cpu > 50  │   │   AND    │ ← DEEPER:
        │  true OR false = true                 │ (terminal) │   └────┬─────┘   binds tighter
        ▼                                       └────────────┘   ┌────┴────┐
   [disk>99] ──► false                                 true      ▼         ▼
        │                                                  ┌──────────┐ ┌────────┐
        │  true AND false = FALSE  ⚠                       │memory>95 │ │disk>99 │
        ▼                                                  └──────────┘ └────────┘
     FALSE  ← the CPU is at 60%.                              false        false
             Nobody gets paged.                                   └────┬────┘
                                                                   false
                                                                       │
   The loop can only ever go                                    true OR false
   ────────────────────────────                                        │
   left → right → left → right                                       TRUE  ✅
   It has no way to say
   "a whole expression goes here."          interpret() recurses. Deeper = evaluated first.
                                            PRECEDENCE IS NOT CODE — IT'S THE SHAPE.
```

**There is no precedence table in the "With" project.** Not one. "AND binds tighter than OR" is not a
rule the evaluator applies — it is the observation that **AND nodes sit deeper**, and recursion reaches
the bottom first.

Parentheses are the same trick from the other direction: `factor()` sees `(` and restarts the grammar
from the top, nesting a subtree in place. **A bracket was never anything more than "a subtree goes
here."**

The "Without" evaluator had to get precedence right on *every single evaluation*, and got it wrong.
The "With" version settles the shape **once**, at parse time, and after that it is a structural fact
about the object graph.

---

## 5. Sequence — Interpreting One Rule

`cpu > 80 AND ( memory > 90 OR disk > 95 )` against `cpu=10, memory=10, disk=99`

```mermaid
sequenceDiagram
    participant C as Client
    participant A as AndExpression
    participant L as Comparison<br/>(cpu > 80)
    participant O as OrExpression
    participant CTX as Context

    C->>A: interpret(context)
    activate A
    Note over A: I don't know what my children ARE.<br/>I just ask them.

    A->>L: interpret(context)
    activate L
    L->>CTX: get("cpu")
    CTX-->>L: 10.0
    L-->>A: false
    deactivate L

    Note over A: && short-circuits — the right<br/>subtree is never even visited.<br/>(free, because it's a tree)
    A-->>C: false  ✅
    deactivate A

    Note over O: the OR subtree would have said TRUE<br/>(disk=99 > 95) — and in the "Without"<br/>project that leaked out past the AND<br/>and paged someone at 3am.
```

The short-circuit is a nice bonus that falls out of the structure: because the right operand is a
subtree rather than a value that was already computed, **it is never evaluated at all**. A flat
left-to-right fold computes every comparison whether it needs to or not.

---

## Key Structural Points

1. **One class per grammar rule.** Write the grammar, count the productions, that's your class list.
   If you can't write the grammar down, you're not ready to write the interpreter.

2. **Nonterminals hold `Expression`, not `boolean`.** This single field type is the whole pattern.
   It's what allows a child to be a leaf *or* a fifty-node subtree, and it's what the "Without"
   project structurally could not express.

3. **Terminals are where recursion stops.** `Comparison` is the only class that reads the `Context`.
   Every other class just combines answers from its children.

4. **Precedence and parentheses are not implemented — they're the shape of the tree.** Decided once at
   construction, not re-derived on every evaluation. Print the tree (`toString()`) and most precedence
   bugs become visible instantly.

5. **The Context keeps the tree stateless.** The expression knows nothing about any particular server,
   so one parsed rule can be interpreted against the whole fleet.

6. **The parser is not part of the pattern.** GoF are explicit. `Main` builds its first tree by hand
   with `new OrExpression(...)` to make that concrete.

7. **The tree is a Composite; the next step is a Visitor.** Interpreter puts `interpret()` on the node,
   which is right for **one** operation. When you want many operations over the same tree — evaluate,
   type-check, optimise, pretty-print — move them out into visitors. That is exactly the road every
   real compiler travels. See `CompositeDesignPattern/` and `VisitorDesignPattern/`.
