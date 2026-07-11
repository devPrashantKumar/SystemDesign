package com.thecodeexperience.SingletonDesignPattern;

// VARIANT 1 — EAGER INITIALIZATION
//
// The instance is created when the class is loaded by the JVM, before anyone
// asks for it. The JVM guarantees class loading is thread-safe, so this is
// inherently thread-safe with no locking.
//
// Trade-off: the instance is built even if it's never used (no lazy loading),
// and you can't pass constructor arguments / handle creation errors gracefully.
public class EagerSingleton {

    // created at class-load time; `final` = assigned exactly once
    private static final EagerSingleton INSTANCE = new EagerSingleton();

    // private constructor stops anyone else from doing `new EagerSingleton()`
    private EagerSingleton() {
    }

    public static EagerSingleton getInstance() {
        return INSTANCE;
    }
}
