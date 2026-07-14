package com.thecodeexperience.WithoutTemplateMethodDesignPattern;

import java.util.ArrayList;
import java.util.List;

/**
 * Imports employees from a CSV file.
 *
 * Read the sequence inside importData() and remember it — open, read, parse, validate, save,
 * close, audit. Seven steps in that order. That order IS the business rule: you must not save a
 * record you have not validated, and you must not leave the file handle open.
 *
 * Now open JsonImporter and read its version of the same seven steps.
 */
public class CsvImporter {

    private boolean fileOpen = false;

    public void importData(String path) {
        // 1. open
        System.out.println("  [csv]  opening " + path);
        fileOpen = true;

        // 2. read raw lines (format-specific)
        List<String> rawLines = new ArrayList<>();
        rawLines.add("Alice,90000");
        rawLines.add("Bob,-5000");            // ⚠ a bad row: negative salary
        rawLines.add("Carol,120000");
        System.out.println("  [csv]  read " + rawLines.size() + " raw lines");

        // 3. parse (format-specific)
        List<Employee> employees = new ArrayList<>();
        for (String line : rawLines) {
            String[] parts = line.split(",");
            employees.add(new Employee(parts[0], Integer.parseInt(parts[1])));
        }

        // 4. validate  ← the shared business rule
        List<Employee> valid = new ArrayList<>();
        for (Employee employee : employees) {
            if (employee.salary() > 0) {
                valid.add(employee);
            } else {
                System.out.println("  [csv]  ✗ rejected invalid record: " + employee);
            }
        }

        // 5. save  ← the shared business rule
        for (Employee employee : valid) {
            Database.insert(employee);
        }

        // 6. close  ← the shared business rule
        fileOpen = false;
        System.out.println("  [csv]  closed " + path);

        // 7. audit  ← the shared business rule
        System.out.println("  [csv]  AUDIT: imported " + valid.size() + " of " + employees.size());
    }

    public boolean isFileOpen() {
        return fileOpen;
    }

}
