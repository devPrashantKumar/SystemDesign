# Builder Design Pattern — UML Diagrams

UML for this project's example: building an immutable `Student` with required fields
(`name`, `rollNo`) and optional fields (`address`, `fatherName`, `motherName`, `age`),
shown in the three ways implemented in `WithBuilderDesignPattern/`.

---

## Class Diagram — Way 1 & Way 2 (Product + nested Builder)

`Student` (Way 1) and `StudentWay2` (Way 2) have the same structure; Way 2 only adds
the static factory `builder()` and makes the builder's constructor private.

```mermaid
classDiagram
    direction LR

    class Client {
        +main(args) void
    }

    class StudentWay2 {
        -String name
        -String rollNo
        -String address
        -String fatherName
        -String motherName
        -int age
        -StudentWay2(StudentBuilder builder)
        +builder(name, rollNo)$ StudentBuilder
        +toString() String
    }

    class StudentBuilder {
        -String name
        -String rollNo
        -String address
        -String fatherName
        -String motherName
        -int age
        -StudentBuilder(name, rollNo)
        +address(address) StudentBuilder
        +fatherName(fatherName) StudentBuilder
        +motherName(motherName) StudentBuilder
        +age(age) StudentBuilder
        +build() StudentWay2
    }

    Client ..> StudentWay2 : uses builder(), receives product
    StudentWay2 *-- StudentBuilder : static nested class
    StudentBuilder ..> StudentWay2 : build() creates
```

**Reading the diagram:**

| Element | Meaning | In this example |
|---|---|---|
| `-StudentWay2(builder)` | private constructor | only the builder can create the product |
| `+builder(name, rollNo)$` | static factory method (`$` = static) | the only public entry point (Way 2) |
| `StudentWay2 *-- StudentBuilder` | composition / nesting | builder is a `static` nested class of the product |
| setters return `StudentBuilder` | fluent interface | enables method chaining |
| all product fields `final` | immutability | object can never change after `build()` |

---

## Class Diagram — Way 3 (with Director)

```mermaid
classDiagram
    direction TB

    class Main3 {
        +main(args) void
    }

    class StudentDirector {
        -StudentDirector()
        +buildScienceStudent(builder)$ StudentWay3
        +buildHostelStudent(builder)$ StudentWay3
        +buildMinimalStudent(builder)$ StudentWay3
    }

    class StudentWay3 {
        -String name
        -String rollNo
        -String address
        -String fatherName
        -String motherName
        -int age
        -StudentWay3(StudentBuilder builder)
        +builder(name, rollNo)$ StudentBuilder
    }

    class StudentBuilder {
        -String name
        -String rollNo
        -String address
        -String fatherName
        -String motherName
        -int age
        +address(address) StudentBuilder
        +age(age) StudentBuilder
        +fatherName(fatherName) StudentBuilder
        +motherName(motherName) StudentBuilder
        +build() StudentWay3
    }

    Main3 ..> StudentWay3 : creates builder with required fields
    Main3 ..> StudentDirector : passes pre-configured builder
    StudentDirector ..> StudentBuilder : stamps preset fields, calls build()
    StudentWay3 *-- StudentBuilder : static nested class
    StudentBuilder ..> StudentWay3 : build() creates
```

**Division of responsibility (Way 3):**

| Who | Sets |
|---|---|
| Client (`Main3`) | required fields + individual-specific fields (`age`, personal `address`) |
| Director | fixed preset fields (hostel address, science guardian) |
| Builder | assembles everything, `build()` produces the immutable product |

---

## Class Diagram (ASCII — generic GoF roles)

```
┌─────────────────┐   builder("name","roll")    ┌──────────────────────────────┐
│     Client      │────────────────────────────▶│  «static nested»             │
│   (Main/Main2/  │                             │      StudentBuilder          │
│     Main3)      │   .address(..).age(..)      │──────────────────────────────│
└───────┬─────────┘   (fluent, returns this)    │ - name, rollNo    (final)    │
        │                                       │ - address, age... (optional) │
        │ receives product                      │ + address(a): StudentBuilder │
        ▼                                       │ + age(n):     StudentBuilder │
┌─────────────────────────────┐    build()      │ + build():    Student        │
│          Student            │◀────────────────└──────────────────────────────┘
│─────────────────────────────│  calls private
│ - name:    final (required) │  constructor
│ - rollNo:  final (required) │
│ - address: final (optional) │        ┌───────────────────────────────┐
│ - age:     final (optional) │        │       StudentDirector         │
│ - Student(builder)  PRIVATE │        │  (optional — Way 3 only)      │
└─────────────────────────────┘        │───────────────────────────────│
                                       │ + buildScienceStudent(builder)│
     Director receives a pre-          │ + buildHostelStudent(builder) │
     configured builder, stamps ──────▶│ + buildMinimalStudent(builder)│
     the preset fields, builds         └───────────────────────────────┘
```

---

## Sequence Diagram — Way 2 (Mermaid)

```mermaid
sequenceDiagram
    participant C as Client (Main2)
    participant B as StudentBuilder
    participant S as StudentWay2

    C->>B: StudentWay2.builder("Prashant", "101")
    Note over B: required fields locked in
    C->>B: .address("Delhi")
    B-->>C: returns this (chaining)
    C->>B: .age(21)
    B-->>C: returns this (chaining)
    C->>B: .build()
    B->>S: new StudentWay2(this)  [private ctor]
    Note over S: copies all fields into final fields
    S-->>B: immutable Student
    B-->>C: immutable Student
```

---

## Sequence Diagram — Way 3 with Director (Mermaid)

```mermaid
sequenceDiagram
    participant M as Main3
    participant B as StudentBuilder
    participant D as StudentDirector
    participant S as StudentWay3

    M->>B: StudentWay3.builder("Riya", "102")
    M->>B: .age(19)
    Note over M,B: client sets individual-specific fields
    M->>D: buildHostelStudent(builder)
    D->>B: .address("University Hostel, Block-A")
    Note over D,B: director stamps the preset fields
    D->>B: .build()
    B->>S: new StudentWay3(this)
    S-->>D: immutable Student
    D-->>M: immutable Student
```

---

## Key Structural Points

1. **The product's constructor is private and takes the builder** — the builder is
   the only door into the product, and it hands over all fields at once so the
   product's fields can be `final` (immutable).

2. **Every optional-field setter returns the builder itself** — that self-reference
   is what makes the chain `builder(...).address(...).age(...).build()` possible.

3. **Required vs optional is enforced by structure, not documentation** — required
   fields live in the builder's constructor (can't compile without them), optional
   fields are fluent setters (skip freely, default to null/0).

4. **The Director is an optional layer** — Way 1 and Way 2 have no Director at all.
   It appears only when the same preset recipe repeats across the codebase.
