package com.thecodeexperience.SingletonDesignPattern;

// VARIANT 5 — BILL PUGH (static inner-class holder)
//
// Lazy AND thread-safe with NO synchronization and NO volatile — the cleanest
// classic approach.
//
// How it works: the inner Holder class isn't loaded until getInstance() first
// references it. At that moment the JVM loads Holder and creates INSTANCE, and
// class loading is guaranteed thread-safe by the JVM. So you get lazy loading
// and thread safety "for free" from the class loader, with zero locking cost.
public class BillPughSingleton {

    private BillPughSingleton() {
    }

    // not loaded until BillPughSingleton.getInstance() is first called
    private static class Holder {
        private static final BillPughSingleton INSTANCE = new BillPughSingleton();
    }

    public static BillPughSingleton getInstance() {
        return Holder.INSTANCE;
    }
}
