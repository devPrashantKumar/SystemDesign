package com.thecodeexperience.WithoutTemplateMethodDesignPattern;

import java.util.ArrayList;
import java.util.List;

/** A stand-in for the real database. It trusts whatever it is handed. */
public final class Database {

    private static final List<Employee> ROWS = new ArrayList<>();

    private Database() {
    }

    public static void insert(Employee employee) {
        ROWS.add(employee);
        System.out.println("  [db]   inserted " + employee);
    }

    public static List<Employee> rows() {
        return List.copyOf(ROWS);
    }

}
