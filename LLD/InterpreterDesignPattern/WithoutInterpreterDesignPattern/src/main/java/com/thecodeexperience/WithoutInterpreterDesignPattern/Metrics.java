package com.thecodeexperience.WithoutInterpreterDesignPattern;

import java.util.HashMap;
import java.util.Map;

/** A snapshot of one server's metrics. This is the data the alert rules are asked about. */
public class Metrics {

    private final Map<String, Double> values = new HashMap<>();

    public Metrics set(String metric, double value) {
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
