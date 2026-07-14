package com.thecodeexperience.WithoutTemplateMethodDesignPattern;

import java.util.ArrayList;
import java.util.List;

/**
 * Imports employees from a JSON file.
 *
 * The same seven steps as CsvImporter — except they aren't. This class was written six months
 * later by someone who copied the CSV importer as a starting point and edited the parts they
 * cared about. Only steps 2 and 3 are genuinely different between the two formats. Everything
 * else was supposed to be identical.
 *
 * ⚠ Compare it with CsvImporter line by line and count what drifted:
 *
 *     - step 4 (validate) is MISSING     → the bad row lands in the database
 *     - step 6 (close) is MISSING        → the file handle leaks
 *     - step 7 (audit) is MISSING        → the import is invisible to ops
 *
 * Nothing here is wrong syntactically. It compiles, it runs, it prints cheerful output. Nothing
 * in the language, the compiler or the type system says an importer must validate before it
 * saves — because THE ALGORITHM IS NOT WRITTEN DOWN ANYWHERE. It only exists as a habit, copied
 * by hand from one class into the next, and habits drift.
 */
public class JsonImporter {

    private boolean fileOpen = false;

    public void importData(String path) {
        // 1. open
        System.out.println("  [json] opening " + path);
        fileOpen = true;

        // 2. read raw JSON objects (format-specific — legitimately different)
        List<String> rawObjects = new ArrayList<>();
        rawObjects.add("{\"name\":\"Dave\",\"salary\":85000}");
        rawObjects.add("{\"name\":\"Eve\",\"salary\":-1}");     // ⚠ the same kind of bad row
        System.out.println("  [json] read " + rawObjects.size() + " raw objects");

        // 3. parse (format-specific — legitimately different)
        List<Employee> employees = new ArrayList<>();
        for (String object : rawObjects) {
            String name = object.split("\"name\":\"")[1].split("\"")[0];
            String salary = object.split("\"salary\":")[1].replace("}", "");
            employees.add(new Employee(name, Integer.parseInt(salary)));
        }

        // 4. validate  ← ⚠ NEVER WRITTEN. The rule lives only in CsvImporter.
        //                  Eve's salary of -1 is about to be saved.

        // 5. save
        for (Employee employee : employees) {
            Database.insert(employee);
        }

        // 6. close  ← ⚠ NEVER WRITTEN. fileOpen stays true forever.

        // 7. audit ← ⚠ NEVER WRITTEN.
    }

    public boolean isFileOpen() {
        return fileOpen;
    }

}
