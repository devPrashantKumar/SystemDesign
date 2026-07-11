package com.thecodeexperience.SingletonDesignPattern;

// VARIANT 3 — THREAD-SAFE (synchronized method)
//
// Fixes the lazy race by making getInstance() synchronized, so only one thread
// can be inside it at a time.
//
// Trade-off: EVERY call acquires the lock — even after the instance exists and
// no creation is needed. That synchronization overhead on every read makes this
// the slowest safe option under heavy use (Double-Checked Locking fixes that).
public class ThreadSafeSingleton {

    private static ThreadSafeSingleton instance;

    private ThreadSafeSingleton() {
    }

    public static synchronized ThreadSafeSingleton getInstance() {
        if (instance == null) {
            instance = new ThreadSafeSingleton();
        }
        return instance;
    }
}
