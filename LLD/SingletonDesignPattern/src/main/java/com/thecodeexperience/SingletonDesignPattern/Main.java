package com.thecodeexperience.SingletonDesignPattern;

// Demonstrates every variant. For each one we fetch the instance twice and
// confirm both references point to the SAME object (that's the whole guarantee).
public class Main {
    public static void main(String[] args) {
        System.out.println("Each line should print 'true' — both calls return one instance:\n");

        System.out.println("1. Eager          : " +
                (EagerSingleton.getInstance() == EagerSingleton.getInstance()));

        System.out.println("2. Lazy           : " +
                (LazySingleton.getInstance() == LazySingleton.getInstance()));

        System.out.println("3. Thread-safe    : " +
                (ThreadSafeSingleton.getInstance() == ThreadSafeSingleton.getInstance()));

        System.out.println("4. Double-checked : " +
                (DoubleCheckedSingleton.getInstance() == DoubleCheckedSingleton.getInstance()));

        System.out.println("5. Bill Pugh      : " +
                (BillPughSingleton.getInstance() == BillPughSingleton.getInstance()));

        System.out.println("6. Enum           : " +
                (EnumSingleton.INSTANCE == EnumSingleton.INSTANCE));

        System.out.print("\nEnum in action -> ");
        EnumSingleton.INSTANCE.doSomething();
    }
}
