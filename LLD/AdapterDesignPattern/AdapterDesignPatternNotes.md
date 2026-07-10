# Adapter Design Pattern

> **Convert the interface of an existing class into the interface the client expects — so two incompatible interfaces can work together.**

Think of a real power adapter: your laptop plug (client) doesn't fit a foreign wall socket (existing class), so you put an adapter in between. Neither the plug nor the socket changes — the adapter translates.

**Type:** Structural pattern (like Decorator and Proxy — it wraps an object).

---

## When to Use

- You want to use an existing class (legacy code, third-party library) but its interface doesn't match what your code expects.
- You cannot modify the existing class (you don't own it, or changing it would break other users).
- You want the client to stay decoupled from the concrete data source / format.

---

## The 4 Roles (mapped to this example)

| Role | Class | Responsibility |
|---|---|---|
| **Target interface** | `IReport` | What the client wants: `getJSONReport()` returning a `Map` |
| **Adaptee** | `XMLReportProvider` | Existing/legacy class: only speaks XML via `getXMLReport()` |
| **Adapter** | `ReportProviderAdapter` | Implements target, wraps adaptee, translates XML → JSON |
| **Client** | `Client` | Works only with `IReport`, never sees XML |
| **Composition root** | `Main` | The only place that knows the concrete wiring |

---

## The Problem

The client wants report data as JSON:

```java
public interface IReport {
    Map<String, String> getJSONReport();
}
```

But the only data source available is a legacy provider that speaks XML:

```java
public class XMLReportProvider {
    public String getXMLReport() {
        return "<username>Hello World</username>";
    }
}
```

The interfaces are **incompatible** — wrong method name, wrong data format. We can't change `XMLReportProvider` (imagine it's a third-party library), and we don't want the client to know about XML.

---

## The Solution — The Adapter

```java
public class ReportProviderAdapter implements IReport {   // ① speaks the client's language

    private final XMLReportProvider xmlReportProvider;    // ② wraps the adaptee (composition)

    public ReportProviderAdapter(XMLReportProvider xmlReportProvider) {
        this.xmlReportProvider = xmlReportProvider;
    }

    @Override
    public Map<String, String> getJSONReport() {
        String data = xmlReportProvider.getXMLReport();   // ③ call the incompatible API
        String username = data.replace("<username>", "").replace("</username>", "");
        return Map.of("username", username);              // ④ translate to client's format
    }
}
```

The adapter does exactly two things:
1. **Implements the target interface** (`IReport`) so the client can use it.
2. **Holds the adaptee** (`XMLReportProvider`) and translates every call — receives the request in the client's format, forwards it to the adaptee, converts the response back.

---

## The Client + Wiring

```java
// Client — depends ONLY on IReport, injected via constructor (DIP)
public class Client {
    IReport report;

    Client(IReport report) { this.report = report; }

    void generateReport() { System.out.println(report.getJSONReport()); }
}

// Main — composition root, the only place that knows the concrete classes
IReport report = new ReportProviderAdapter(new XMLReportProvider());
Client client = new Client(report);
client.generateReport();   // {username=Hello World}
```

If a `CSVReportProvider` arrives tomorrow, we only add a new `CSVReportProviderAdapter` and change the wiring line in `Main` — `Client` and `IReport` never change (Open/Closed Principle).

---

## Call Flow

```
Client ──getJSONReport()──▶ ReportProviderAdapter ──getXMLReport()──▶ XMLReportProvider
                                     │                                       │
       ◀──── Map{username=...} ──────┴────── "<username>...</username>" ◀────┘
                (translated)                        (raw XML)
```

---

## Object Adapter vs Class Adapter

| | Object Adapter (used here) | Class Adapter |
|---|---|---|
| How | **Composition** — holds a reference to the adaptee | **Inheritance** — extends the adaptee |
| Flexibility | Can wrap any instance/subclass of the adaptee | Locked to one adaptee class |
| Java support | Always works | Limited — Java has no multiple inheritance |
| Recommendation | ✅ Preferred | Rarely used |

---

## Adapter vs Decorator vs Proxy

All three are structural patterns that wrap an object — **the difference is intent**:

| Pattern | Wraps an object to... | Interface | Repo example |
|---|---|---|---|
| **Adapter** | *translate* to a different interface | **Changes** (XML API → JSON API) | This project |
| **Decorator** | *add behavior* | Stays the same (`Coffee` → `Coffee`) | `DecoratorDesignPattern` |
| **Proxy** | *control access* | Stays the same (`EmployeeDao` → `EmployeeDao`) | `ProxyDesignPattern` |

Adapter is the only one where the wrapped interface and the exposed interface are **different**.

---

## Real-World Examples in Java

- `InputStreamReader` — adapts a byte `InputStream` to a character `Reader`
- `Arrays.asList()` — adapts an array to the `List` interface
- `Collections.enumeration()` / `Collections.list()` — adapt between `Enumeration` and `Collection`
- JDBC drivers — adapt each vendor's native database API to the standard `java.sql` interfaces
- Spring's `HandlerAdapter` — adapts different controller types to one dispatch interface
