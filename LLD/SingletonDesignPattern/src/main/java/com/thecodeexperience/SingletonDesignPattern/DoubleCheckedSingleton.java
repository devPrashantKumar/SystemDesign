package com.thecodeexperience.SingletonDesignPattern;

// VARIANT 4 — DOUBLE-CHECKED LOCKING (DCL)
//
// Lazy AND thread-safe, but locks only on the FIRST creation instead of on
// every call. After the instance exists, getInstance() takes the fast path
// (no lock at all).
//
// Two must-haves for correctness:
//   1. `volatile` — without it another thread could see a partially-constructed
//      object due to instruction reordering during `new`.
//   2. The SECOND null check inside the synchronized block — two threads may both
//      pass the first check; only one should actually create the instance.
public class DoubleCheckedSingleton {

    private static volatile DoubleCheckedSingleton instance;

    private DoubleCheckedSingleton() {
    }

    public static DoubleCheckedSingleton getInstance() {
        if (instance == null) {                       // 1st check — no lock (fast path)
            synchronized (DoubleCheckedSingleton.class) {
                if (instance == null) {               // 2nd check — inside the lock
                    instance = new DoubleCheckedSingleton();
                }
            }
        }
        return instance;
    }
}
