package com.thecodeexperience.WithTemplateMethodDesignPattern;

import java.util.ArrayList;
import java.util.List;

/** A stand-in for the real database. It still trusts whatever it is handed — it just never
 *  gets handed anything bad now, because nothing can reach it without passing validate(). */
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
