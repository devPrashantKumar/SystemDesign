package com.thecodeexperience.WithInterpreterDesignPattern;

import java.util.HashMap;
import java.util.Map;

/**
 * THE CONTEXT — everything the interpreter needs that isn't in the sentence itself.
 *
 * The expression `cpu > 80` is meaningless on its own: it needs to know what "cpu" is right now.
 * The Context carries that. It gets passed down the whole tree, so every node in an expression
 * is evaluated against the same snapshot.
 *
 * Note the consequence: the expression tree holds NO state about any particular server. One
 * parsed rule can be interpreted against a thousand different Contexts.
 */
public class Context {

    private final Map<String, Double> values = new HashMap<>();

    public Context set(String metric, double value) {
        values.put(metric, value);
        return this;
    }

    public double get(String metric) {
        Double value = values.get(metric);
        if (value == null) {
            throw new IllegalArgumentException("unknown metric: " + metric);
        }
        return value;
    }

    @Override
    public String toString() {
        return values.toString();
    }

}
