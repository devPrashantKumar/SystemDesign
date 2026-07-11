package com.thecodeexperience.SingletonDesignPattern;

// VARIANT 6 — ENUM (Joshua Bloch's recommended approach, Effective Java Item 3)
//
// A single-element enum IS a singleton: the JVM guarantees exactly one instance,
// and it's the only variant that is automatically safe against BOTH:
//   - Reflection attacks (you cannot reflectively call an enum constructor)
//   - Serialization (enums serialize/deserialize to the same instance; the other
//     variants need a readResolve() method to avoid creating a second object)
//
// Use it like: EnumSingleton.INSTANCE.doSomething();
// Trade-off: not lazy (created at enum load), and can't extend another class.
public enum EnumSingleton {
    INSTANCE;

    // enums can hold state and behavior like any class
    public void doSomething() {
        System.out.println("EnumSingleton doing work on the one and only instance");
    }
}
