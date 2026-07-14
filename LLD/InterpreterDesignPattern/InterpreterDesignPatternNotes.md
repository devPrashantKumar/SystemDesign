# Interpreter Design Pattern

> **Given a language, define a representation for its grammar along with an interpreter that uses
> the representation to interpret sentences in the language.**

In one line: **turn the grammar into a class hierarchy, and a sentence becomes a tree of objects.**

**Type:** Behavioural pattern.

This is the least-used pattern in the book and the most misunderstood. People skip it because they
think "I'm never going to write a programming language." But you don't need to be writing a *language*
— you need a **rule that users want to express and you don't want to hard-code**. Alert rules, search
filters, permission checks, pricing rules, feature flags, validation. All of them are little languages,
and most of them get written as a `switch` and then, later, as a broken string parser.

---

## The Problem — Every Rule Is a Deploy

An alerting system. Ops wants to be paged when `cpu > 80`. Easy:

```java
public boolean evaluate(String ruleName, Metrics metrics) {
    switch (ruleName) {
        case "HIGH_CPU":            return metrics.get("cpu") > 80;
        case "HIGH_MEMORY":         return metrics.get("memory") > 90;
        case "HIGH_CPU_AND_MEMORY": return metrics.get("cpu") > 80 && metrics.get("memory") > 90;
        default: throw new IllegalArgumentException("unknown rule — file a ticket");
    }
}
```

For three rules this is fine. The problem is the fourth. **Every new alert rule is a code change, a
review, a build and a deploy** — for what is, in the end, a slightly different arrangement of `>` and
`&&`. Ops knows exactly what they want to be alerted on; they just aren't allowed to *say* it.

**The rules are not data. They are Java.** So they can't live in a config file, can't be edited in a
UI, can't be tested by the people who own them, and can't ship without you.

### Attempt 2, and this is the interesting failure

So somebody does the sensible thing — let ops write rules as strings — and writes the evaluator the
obvious way: walk the tokens, fold the result left to right.

```java
boolean result = compare(tokens[0], tokens[1], tokens[2], metrics);
int i = 3;
while (i < tokens.length) {
    String operator = tokens[i];
    boolean next = compare(tokens[i+1], tokens[i+2], tokens[i+3], metrics);
    if (operator.equals("AND")) result = result && next;   // ⚠
    else if (operator.equals("OR")) result = result || next;
    i += 4;
}
```

It passes every rule anyone tries at first. Then:

```
=== BUG 1: no operator precedence ===
  rule    : cpu > 50 OR memory > 95 AND disk > 99
  correct : cpu>50 OR (memory>95 AND disk>99)  →  true    (AND binds tighter)
  actual  : (cpu>50 OR memory>95) AND disk>99  →  false
  ⚠ The CPU is at 60%. This rule should be paging someone. It isn't.

=== BUG 2: no parentheses ===
  rule    : cpu > 80 AND ( memory > 90 OR disk > 95 )
  correct : false   (the CPU is at 10%)
  actual  : true    ⚠ A false page at 3am, because the parens were thrown away.
```

A missed page and a false page, from the same twelve lines.

### The root cause, stated precisely

> **A language is a TREE. That evaluator is a LOOP.**

`AND` and `OR` are not *steps in a sequence*. They are **nodes with two children**, and each child can
itself be an entire expression. Precedence means "AND nodes sit deeper than OR nodes." Parentheses
mean "a whole subexpression goes right here."

Fold a tree flat into a loop and the meaning is gone. **No amount of patching that loop will fix it** —
which is why "quick expression evaluator" is a genre of bug rather than a task.

---

## The Fix — the Grammar Becomes the Class Hierarchy

Write the grammar down:

```
expression := term ( "OR" term )*
term       := factor ( "AND" factor )*
factor     := "NOT" factor | "(" expression ")" | comparison
comparison := metric ( ">" | "<" ) number
```

Now make **one class per rule**:

| Grammar rule | Class | Kind |
|---|---|---|
| `"OR"` | `OrExpression` | nonterminal (2 children) |
| `"AND"` | `AndExpression` | nonterminal (2 children) |
| `"NOT"` | `NotExpression` | nonterminal (1 child) |
| `metric > number` | `Comparison` | **terminal** (no children — a leaf) |

All of them implement one interface, and it has one method:

```java
public interface Expression {
    boolean interpret(Context context);
}
```

### The line the "Without" project could never write

```java
public class AndExpression implements Expression {
    private final Expression left;      // ← not a boolean. Not a comparison.
    private final Expression right;     //   an EXPRESSION.

    public boolean interpret(Context context) {
        return left.interpret(context) && right.interpret(context);
    }
}
```

A child might be a leaf, or it might be a nested rule fifty nodes deep. `interpret()` neither knows
nor cares — it asks its children and combines the answers. **That recursion is what makes the thing a
language instead of a list.**

### Where did precedence go?

Search the "With" project for a precedence table. There isn't one. No priority numbers, no ranks, no
comparison of operator weights anywhere.

**Precedence isn't evaluated — it's structural.** "AND binds tighter than OR" just means AND nodes end
up *deeper in the tree*, and recursion evaluates deeper nodes first. That's it. Look at the parsed
output:

```
  rule   : cpu > 50 OR memory > 95 AND disk > 99
  tree   : (cpu > 50 OR (memory > 95 AND disk > 99))   ← AND is DEEPER
  result : true    ✅ (the "Without" project said false)
```

Same for parentheses — `factor()` sees `(` and simply **restarts the grammar from the top**, which
nests a subtree in place. There is no parenthesis-handling code, because a bracket was never anything
more than "a subtree goes here."

The "Without" version had to get precedence right on *every evaluation* and didn't. The "With" version
decides the shape **once**, at parse time, and after that it is a fact about the object graph.

### The Context

```java
Expression alert = parser.parse("cpu > 80 AND NOT disk > 95");
```

The tree holds **no state about any particular server**. Everything situational lives in the `Context`,
which is passed down the recursion. So you parse a rule once and evaluate it against the entire fleet:

```
  rule: (cpu > 80 AND NOT disk > 95)
    {disk=40.0, cpu=85.0}   → 🔴 FIRE
    {disk=99.0, cpu=85.0}   →   ok
    {disk=40.0, cpu=12.0}   →   ok
```

---

## The Parser Is Not Part of the Pattern

Worth being clear about, because it trips people up: **GoF's Interpreter says nothing about parsing.**
It defines how to *represent* and *evaluate* a sentence once you have it as a tree. How the tree got
built is your problem — and it's often the bigger one.

`Main` builds the first tree **by hand**, with `new OrExpression(...)`, precisely to make this visible.
The pattern is fully present there, with no parser involved. You could equally build the tree from
JSON, from a UI, or with ANTLR.

`RuleParser` is included because a rule language nobody can type isn't much of a rule language. It's a
textbook recursive-descent parser, one method per grammar rule — and `expression()` calling `term()`
calling `factor()` is *literally* where precedence comes from.

---

## What This Buys You

| | Without Interpreter | With Interpreter |
|---|---|---|
| A new rule | new `case`, review, build, **deploy** | a line in a config file |
| Who can write rules | developers | **whoever owns the alerts** |
| Rules are | Java | **data** |
| Operator precedence | re-derived on every eval, wrongly | **the shape of the tree** |
| Parentheses | unsupported / stripped | free — a subtree |
| Adding `NOT` to the language | rewrite the evaluator | **one new class** |
| Rule + fleet | re-evaluate the string each time | parse once, interpret against N contexts |

---

## The Four Roles

| GoF role | This project |
|---|---|
| **AbstractExpression** | `Expression` — the single `interpret(Context)` method |
| **TerminalExpression** | `Comparison` — a leaf; the recursion bottoms out here |
| **NonterminalExpression** | `AndExpression`, `OrExpression`, `NotExpression` — they hold `Expression` children |
| **Context** | `Context` — the metric values; everything the sentence itself doesn't carry |

(The **Client** builds the tree — by hand, or via a parser.)

---

## Verify (this project)

```
WITHOUT:  cpu > 50 OR memory > 95 AND disk > 99      → false   ⚠ missed page
WITH:     (cpu > 50 OR (memory > 95 AND disk > 99))  → true    ✅

WITHOUT:  cpu > 80 AND ( memory > 90 OR disk > 95 )  → true    ⚠ false page at 3am
WITH:     (cpu > 80 AND (memory > 90 OR disk > 95))  → false   ✅
```

The "With" output prints the tree via `toString()`, which is worth doing in real interpreters too —
the parenthesised form shows you exactly what the parser decided, and most precedence bugs are visible
the moment you print the tree.

---

## Trade-offs & Cautions

- **It does not scale to a real grammar. This is the big one.** One class per rule is charming for
  four rules and unmanageable for forty. GoF say this themselves: for a complex grammar, use a parser
  generator (ANTLR, JavaCC) or a proper compiler architecture. Interpreter is for **small, stable
  languages** — a filter DSL, a rule engine, a query subset. If your grammar has thirty productions,
  you have outgrown the pattern.
- **It's slow.** Walking a tree of objects and making a virtual call per node is far slower than
  compiled code. Fine for evaluating an alert rule every 30 seconds; not fine in a tight loop. The
  standard escape is to *compile* the tree to something faster (bytecode, a closure, a lambda) rather
  than interpret it directly.
- **Interpreting is usually the easy half.** The parsing, the error messages, the tooling, the
  "line 3, column 12: expected `)`" — that's where the work actually is, and the pattern helps with
  none of it.
- **A DSL is a product, not a feature.** The moment ops can write rules, they will write a rule you
  didn't anticipate, and you now own a language: its docs, its error messages, its backwards
  compatibility. That's often worth it. Just know you're signing up for it.
- **Don't reach for it when a lambda will do.** If the "rules" are all written by developers anyway,
  `Predicate<Metrics>` composed with `.and()` / `.or()` gives you the same tree with none of the
  classes. Interpreter earns its keep precisely when **the rules must come from outside the code** —
  as text, config, or user input.

---

## Where You've Already Used It

- **`java.util.regex.Pattern`** — the canonical JDK example. `Pattern.compile()` parses a regex into a
  tree of node objects, and `matcher.matches()` interprets it. A regex is a little language.
- **SQL `WHERE` clauses** — the database parses your predicate into an expression tree and evaluates
  it per row. The query planner then *rewrites* that tree, which is Interpreter plus Visitor.
- **Spring Expression Language (SpEL)**, JSP EL, Thymeleaf conditions — `#{user.age > 18}` is parsed
  to a tree and interpreted against a context object. Exactly this project, at scale.
- **Search filters** — GitHub's `is:open label:bug author:me`, Gmail's `from: has:attachment`, Jira's
  JQL. Each is a small grammar, parsed to a tree.
- **Feature-flag and rule engines** — LaunchDarkly-style targeting rules, Drools.
- **`java.util.function.Predicate.and()/or()/negate()`** — genuinely an interpreter tree, built with
  lambdas instead of named classes. If you've composed predicates, you've built an AST.

---

## Interpreter vs. Its Neighbours

| Pattern | The actual difference |
|---|---|
| **Composite** | **Interpreter's tree IS a Composite.** `AndExpression` holding two `Expression` children is precisely Composite's "a container that is also a component". The difference is intent, not structure: Composite is about **part–whole hierarchies** (a folder holds files); Interpreter is about **a grammar** (an AND holds two expressions). See `CompositeDesignPattern/` — the expression-tree variant there is the same shape as this. |
| **Visitor** | **The natural next step, and they pair constantly.** Interpreter puts `interpret()` *inside* each node. But real languages want many operations over one tree — evaluate, type-check, optimise, pretty-print, compile. Adding each as a method on every node is the exact problem `VisitorDesignPattern/` solves. Rule of thumb: **one operation → put `interpret()` on the node. Many operations → node classes hold data, and Visitors hold the operations.** Every real compiler ends up here. |
| **Iterator** | Iterator traverses a structure without knowing what it means. Interpreter knows exactly what it means but doesn't care how you got there. A tree-walking interpreter often *uses* an iterator over its tokens. |
| **Strategy** | Both let behaviour vary. A Strategy is **one object you swap**; an Interpreter is **a tree you compose**. When the rules stop being expressible as "pick one of N", you've crossed from Strategy into Interpreter. |
| **Command** | A Command is a request as an object. An Interpreter's AST node is a *fragment of a sentence* as an object. Their trees look similar — but Commands are typically executed for their side effects, expressions evaluated for their value. |
