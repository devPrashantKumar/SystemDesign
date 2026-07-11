package com.thecodeexperience.SingletonDesignPattern;

// VARIANT 2 — LAZY INITIALIZATION (NOT thread-safe)
//
// The instance is created only on the first getInstance() call (lazy loading).
// Simple, but BROKEN under multiple threads: two threads can both see
// `instance == null` at the same time and each create a separate object,
// violating the single-instance guarantee.
//
// Use only in single-threaded code. Shown here to motivate the safer variants.
public class LazySingleton {

    private static LazySingleton instance;   // not created until first use

    private LazySingleton() {
    }

    public static LazySingleton getInstance() {
        if (instance == null) {              // ⚠ race condition here across threads
            instance = new LazySingleton();
        }
        return instance;
    }
}
