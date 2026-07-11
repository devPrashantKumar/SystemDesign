package com.thecodeexperience.PrototypeDesignPattern;

import java.util.ArrayList;
import java.util.Arrays;

// Client for the registry variant.
// Notice: after setup, this code never calls `new Employee(...)` — every
// object comes from the registry by name, already pre-configured.
public class RegistryMain {
    public static void main(String[] args) {
        PrototypeRegistry registry = new PrototypeRegistry();

        // --- One-time setup: register fully-configured master prototypes ---
        // Imagine each of these was expensive to build (DB lookup, defaults, etc.).
        registry.register("java-dev",
                new Employee("TEMPLATE", "JVM",
                        new ArrayList<>(Arrays.asList("JAVA", "SPRING"))));
        registry.register("data-engineer",
                new Employee("TEMPLATE", "DATA",
                        new ArrayList<>(Arrays.asList("PYTHON", "SPARK", "SQL"))));

        // --- Later: clients grab pre-configured copies by name ---
        Employee dev1 = (Employee) registry.get("java-dev");
        Employee dev2 = (Employee) registry.get("java-dev");

        // Each clone is independent — customise without affecting the master or siblings.
        dev1.addSkills("AWS");

        System.out.println("dev1  : " + dev1);   // has AWS
        System.out.println("dev2  : " + dev2);   // NO AWS — proves clones are independent

        Employee de = (Employee) registry.get("data-engineer");
        System.out.println("de    : " + de);

        // The master is still pristine — fetch again and it's unchanged by dev1's edit.
        System.out.println("master: " + registry.get("java-dev"));
    }
}
