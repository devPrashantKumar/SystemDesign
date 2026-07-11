package com.thecodeexperience.PrototypeDesignPattern;

import java.util.HashMap;
import java.util.Map;

// PROTOTYPE REGISTRY (optional companion to the pattern).
//
// A central store of ready-made master prototypes, keyed by a name.
// The client asks the registry for a copy by name and never touches
// a concrete class or a constructor — it just gets back a fresh clone.
//
// Why this is useful:
//   - The expensive/complex setup of each master happens ONCE (at registration).
//   - Callers get pre-configured objects by a simple string key.
//   - Adding a new "type" = registering a new prototype, not writing a new class
//     or extending a factory switch (Open/Closed).
public class PrototypeRegistry {

    // Stores the master prototypes. We never hand these out directly —
    // we always return a clone so the masters can't be mutated by callers.
    private final Map<String, Prototype> registry = new HashMap<>();

    // Register a fully-configured master under a name.
    public void register(String key, Prototype prototype) {
        registry.put(key, prototype);
    }

    // Hand the caller a fresh CLONE of the requested master.
    // Returning a clone (not the stored instance) is what keeps the
    // registry's masters pristine no matter what the caller does next.
    public Prototype get(String key) {
        Prototype prototype = registry.get(key);
        if (prototype == null) {
            throw new IllegalArgumentException("No prototype registered for key: " + key);
        }
        return prototype.cloneObject();
    }
}
